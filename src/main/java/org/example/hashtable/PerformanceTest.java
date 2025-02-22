package org.example.hashtable;

import java.util.HashSet;
import java.util.Set;

public class PerformanceTest {
    public static void main(String[] args) {
        int[] testSizes = {1000, 10000, 100000}; // Размеры для замеров

        for (int n : testSizes) {
            System.out.println("\nRunning tests for size: " + n);

            // Замеры для ExtendibleHashTable
            ExtendibleHashTable ht = new ExtendibleHashTable(10);
            runInsertTest(ht, n);
            runSearchTest(ht, n);
            runDeleteTest(ht, n);
            System.gc();

            // Замеры для PerfectHashTable
            String[] keys = new String[n];
            for (int i = 0; i < n; i++) {
                keys[i] = "key" + i;
            }
            long buildStart = System.nanoTime();
            PerfectHashTable ph = new PerfectHashTable(keys);
            long buildEnd = System.nanoTime();
            System.out.println("PerfectHashTable: Construction of table for " + n + " keys took " + (buildEnd - buildStart) / 1e6 + " ms");

            long start = System.nanoTime();
            for (int i = 0; i < n; i++) {
                ph.search("key" + i);
            }
            long end = System.nanoTime();
            System.out.println("PerfectHashTable: Search of " + n + " keys took " + (end - start) / 1e6 + " ms");
            System.gc();

            // Замеры для MinHash
            runMinHashTest(n);
            System.gc();
        }
    }

    // ExtendibleHashTable замеры
    private static void runInsertTest(ExtendibleHashTable ht, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            ht.insert("key" + i, "value" + i);
        }
        long end = System.nanoTime();
        System.out.println("ExtendibleHashTable: Insertion of " + n + " keys took " + (end - start) / 1e6 + " ms");
    }

    private static void runSearchTest(ExtendibleHashTable ht, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            ht.search("key" + i);
        }
        long end = System.nanoTime();
        System.out.println("ExtendibleHashTable: Search of " + n + " keys took " + (end - start) / 1e6 + " ms");
    }

    private static void runDeleteTest(ExtendibleHashTable ht, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            ht.delete("key" + i);
        }
        long end = System.nanoTime();
        System.out.println("ExtendibleHashTable: Deletion of " + n + " keys took " + (end - start) / 1e6 + " ms");
    }

    // MinHash замеры
    private static void runMinHashTest(int n) {
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        for (int i = 0; i < n; i++) {
            set1.add("key" + i);
            set2.add("key" + (i + 1));
        }

        MinHash minHash = new MinHash();
        long start = System.nanoTime();
        int[] minHash1 = minHash.computeMinHash(set1);
        int[] minHash2 = minHash.computeMinHash(set2);
        long end = System.nanoTime();
        System.out.println("MinHash: Computation of MinHash for sets took " + (end - start) / 1e6 + " ms");

        double similarity = minHash.jaccardSimilarity(minHash1, minHash2);
        System.out.println("MinHash: Jaccard Similarity: " + similarity);

        // Добавление элемента и пересчёт MinHash
        System.out.println("Adding 'orange' to set1...");
        minHash.addElement(set1, "orange");
        start = System.nanoTime();
        minHash1 = minHash.computeMinHash(set1);
        minHash2 = minHash.computeMinHash(set2);
        end = System.nanoTime();
        System.out.println("MinHash: Computation of MinHash for sets took " + (end - start) / 1e6 + " ms");
        similarity = minHash.jaccardSimilarity(minHash1, minHash2);
        System.out.println("MinHash: Jaccard Similarity: " + similarity);

        // Удаление элемента и пересчёт MinHash
        System.out.println("Removing 'banana' from set2...");
        minHash.removeElement(set2, "banana");
        start = System.nanoTime();
        minHash1 = minHash.computeMinHash(set1);
        minHash2 = minHash.computeMinHash(set2);
        end = System.nanoTime();
        System.out.println("MinHash: Computation of MinHash for sets took " + (end - start) / 1e6 + " ms");
        similarity = minHash.jaccardSimilarity(minHash1, minHash2);
        System.out.println("MinHash: Jaccard Similarity: " + similarity);
    }
}
