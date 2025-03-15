package org.example.hashtable;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ExtendibleHashTable Tests")
public class ExtendibleHashTableTest {

    private static final int SMALL_DATA_SIZE = 10;
    private static final int LARGE_DATA_SIZE = 1000;
    private Random random = new Random();

    private Set<String> generateUniqueKeys(int count) {
        Set<String> keys = new HashSet<>();
        while (keys.size() < count) {
            keys.add(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
        }
        return keys;
    }

    @Test
    @DisplayName("Insert and Search: All keys should be found after insertion")
    void testInsertAndSearch() throws IOException, ClassNotFoundException {
        ExtendibleHashTable table = new ExtendibleHashTable(1000);
        Set<String> keys = generateUniqueKeys(SMALL_DATA_SIZE);
        for (String key : keys) {
            table.insert(key);
        }
        for (String key : keys) {
            assertTrue(table.search(key), "Expected table to contain key: " + key);
        }
        assertFalse(table.search("non_existent_key"), "Table should not contain non-existent key.");
    }

    @Test
    @DisplayName("Delete: Deleted keys should not be found, others remain")
    void testDelete() throws IOException, ClassNotFoundException {
        ExtendibleHashTable table = new ExtendibleHashTable(2);
        Set<String> keys = generateUniqueKeys(SMALL_DATA_SIZE);
        for (String key : keys) {
            table.insert(key);
        }
        int i = 0;
        for (String key : keys) {
            if (i % 2 == 0) {
                table.delete(key);
            }
            i++;
        }
        i = 0;
        for (String key : keys) {
            if (i % 2 == 0) {
                assertFalse(table.search(key), "Key should have been deleted: " + key);
            } else {
                assertTrue(table.search(key), "Key should still exist: " + key);
            }
            i++;
        }
    }

    @Test
    @DisplayName("Expansion: Table should expand and maintain inserted keys")
    void testExpansion() throws IOException, ClassNotFoundException {
        ExtendibleHashTable table = new ExtendibleHashTable(2);
        Set<String> keys = generateUniqueKeys(SMALL_DATA_SIZE);
        for (String key : keys) {
            table.insert(key);
        }
        for (String key : keys) {
            assertTrue(table.search(key), "Key should be found after expansion: " + key);
        }
        assertFalse(table.search("invalid_key"), "Table should not contain an invalid key.");
    }

    @Test
    @DisplayName("Duplicate Insert: Duplicate keys should not be inserted twice")
    void testDuplicateInsert() throws IOException, ClassNotFoundException {
        ExtendibleHashTable table = new ExtendibleHashTable(1000);
        String duplicateKey = "duplicateKey";
        table.insert(duplicateKey);
        table.insert(duplicateKey);
        assertTrue(table.search(duplicateKey), "Duplicate key should exist in table.");
        assertTrue(table.contains(duplicateKey), "Duplicate key should only be stored once.");
    }
}
