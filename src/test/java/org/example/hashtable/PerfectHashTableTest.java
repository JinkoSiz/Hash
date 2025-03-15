package org.example.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PerfectHashTableTest {
    private static final int DATA_SIZE = 10000;

    private PerfectHashTable hashTable;
    private Set<String> testData;
    private List<String> testList;

    @BeforeEach
    void setUp() {
        hashTable = new PerfectHashTable(DATA_SIZE);
        testData = generateTestData(DATA_SIZE);
        testList = new ArrayList<>(testData);
    }

    private void populateTable(Collection<String> data) {
        data.forEach(hashTable::insert);
    }

    @Test
    void testInsertAndContains() {
        populateTable(testData);

        testData.forEach(value ->
                assertTrue(hashTable.contains(value), "HashTable should contain " + value)
        );
        assertFalse(hashTable.contains("1000000"), "HashTable should not contain a non-inserted value");
    }

    @Test
    void testRemove() {
        populateTable(testData);

        for (int i = 0; i < testList.size(); i++) {
            if (i % 2 == 0) {
                hashTable.remove(testList.get(i));
            }
        }

        for (int i = 0; i < testList.size(); i++) {
            if (i % 2 == 0) {
                assertFalse(hashTable.contains(testList.get(i)), "HashTable should not contain removed value " + testList.get(i));
            } else {
                assertTrue(hashTable.contains(testList.get(i)), "HashTable should still contain " + testList.get(i));
            }
        }
    }

    @Test
    void testRemoveNonExistingElement() {
        populateTable(testData);

        assertFalse(hashTable.contains("9999999"), "Non-existing value should not be found");
        hashTable.remove("9999999");

        testData.forEach(value ->
                assertTrue(hashTable.contains(value), "Existing values should still be in the hash table")
        );
    }

    @Test
    void testEmptyTable() {
        assertFalse(hashTable.contains("10"));
        assertFalse(hashTable.contains("100"));
        assertFalse(hashTable.contains("999"));
    }

    @Test
    void testInsertDuplicateElements() {
        String testValue = "5000";

        hashTable.insert(testValue);
        hashTable.insert(testValue);

        assertTrue(hashTable.contains(testValue), "HashTable should contain inserted value");

        hashTable.remove(testValue);
        assertFalse(hashTable.contains(testValue), "HashTable should not contain removed value");
    }

    @Test
    void testRebuildWithCollisions() {
        populateTable(testData);
        int removalThreshold = DATA_SIZE / 10;

        for (int i = 0; i < testList.size() && i < removalThreshold; i++) {
            hashTable.remove(testList.get(i));
        }

        for (int i = 0; i < testList.size(); i++) {
            if (i < removalThreshold) {
                assertFalse(hashTable.contains(testList.get(i)), "Removed value should not be in the hash table");
            } else {
                assertTrue(hashTable.contains(testList.get(i)), "Remaining values should still be in the hash table");
            }
        }
    }

    @Test
    void testNullKeyInsertion() {
        assertThrows(IllegalArgumentException.class, () -> hashTable.insert(null),
                "Inserting a null key should throw an exception");
    }

    private Set<String> generateTestData(int size) {
        Set<String> data = new HashSet<>();
        Random random = new Random();
        while (data.size() < size) {
            data.add(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
        }
        return data;
    }
}
