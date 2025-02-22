package org.example.hashtable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class HashTableTest {

    @Test
    public void testExtendibleInsertAndSearch() {
        ExtendibleHashTable ht = new ExtendibleHashTable(2);

        ht.insert("name", "John");
        ht.insert("age", "30");

        assertEquals("John", ht.search("name"));
        assertNull(ht.search("address"));
    }

//    @Test
//    public void testPerfectInsertAndSearch() {
//        String[] keys = {"name", "age", "city"};
//        PerfectHashTable ht = new PerfectHashTable(3, keys);
//
//        assertEquals("name", ht.search("name"));
//        assertNull(ht.search("address"));
//    }

    @Test
    public void testDelete() {
        ExtendibleHashTable ht = new ExtendibleHashTable(2);

        ht.insert("name", "John");
        ht.insert("age", "30");
        ht.delete("name");

        assertNull(ht.search("name"));
        assertEquals("30", ht.search("age"));
    }

    @Test
    public void testInsertLargeData() {
        ExtendibleHashTable ht = new ExtendibleHashTable(10);

        for (int i = 0; i < 100000; i++) {
            ht.insert("key" + i, "value" + i);
        }

        for (int i = 0; i < 100000; i++) {
            assertEquals("value" + i, ht.search("key" + i));
        }
    }

    @Test
    public void testMinHashSimilarity() {
        Set<String> set1 = new HashSet<>();
        set1.add("apple");
        set1.add("banana");
        set1.add("cherry");

        Set<String> set2 = new HashSet<>();
        set2.add("apple");
        set2.add("banana");
        set2.add("grape");

        MinHash minHash = new MinHash();

        int[] minHash1 = minHash.computeMinHash(set1);
        int[] minHash2 = minHash.computeMinHash(set2);

        double similarity = minHash.jaccardSimilarity(minHash1, minHash2);
        System.out.println("Jaccard Similarity: " + similarity);

        assertTrue(similarity >= 0 && similarity <= 1);
    }

//    @Test
//    public void testPerfectInsertAndDelete() {
//        PerfectHashTable ht = new PerfectHashTable(3, new String[]{"name", "age", "city"});
//
//        ht.insert("address", "123 Main St");
//        assertEquals("123 Main St", ht.search("address"));
//        ht.delete("address");
//        assertNull(ht.search("address"));
//    }

    @Test
    public void testExtendibleHashTablePerformance() {
        // Симуляция нескольких операций на ExtendibleHashTable
        ExtendibleHashTable ht = new ExtendibleHashTable(5);

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            ht.insert("key" + i, "value" + i);
        }
        long end = System.nanoTime();
        System.out.println("ExtendibleHashTable Insert: " + (end - start) / 1e6 + " ms");

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            ht.search("key" + i);
        }
        end = System.nanoTime();
        System.out.println("ExtendibleHashTable Search: " + (end - start) / 1e6 + " ms");

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            ht.delete("key" + i);
        }
        end = System.nanoTime();
        System.out.println("ExtendibleHashTable Delete: " + (end - start) / 1e6 + " ms");
    }
}
