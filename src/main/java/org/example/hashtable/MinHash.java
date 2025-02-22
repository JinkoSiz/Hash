package org.example.hashtable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MinHash {

    private static final int NUM_HASHES = 200;

    private Random random = new Random();
    private int[] a = new int[NUM_HASHES];
    private int[] b = new int[NUM_HASHES];
    private int prime = 2147483647;

    public MinHash() {
        for (int i = 0; i < NUM_HASHES; i++) {
            a[i] = random.nextInt(prime);
            b[i] = random.nextInt(prime);
        }
    }

    public int[] computeMinHash(Set<String> set) {
        int[] minHashes = new int[NUM_HASHES];

        for (int i = 0; i < NUM_HASHES; i++) {
            int minHash = Integer.MAX_VALUE;
            for (String element : set) {
                int hashValue = hash(element, a[i], b[i]);
                minHash = Math.min(minHash, hashValue);
            }
            minHashes[i] = minHash;
        }
        return minHashes;
    }

    private int hash(String element, int a, int b) {
        int hash = element.hashCode();
        return Math.abs((a * hash + b) % prime);
    }

    public double jaccardSimilarity(int[] minHash1, int[] minHash2) {
        int count = 0;
        for (int i = 0; i < NUM_HASHES; i++) {
            if (minHash1[i] == minHash2[i]) {
                count++;
            }
        }
        return (double) count / NUM_HASHES;
    }

    // Операции для добавления и удаления элементов
    public void addElement(Set<String> set, String element) {
        set.add(element);
    }

    public void removeElement(Set<String> set, String element) {
        set.remove(element);
    }

    // Замер производительности
    public void runPerformanceTest(Set<String> set1, Set<String> set2) {
        long start = System.nanoTime();
        int[] minHash1 = computeMinHash(set1);
        int[] minHash2 = computeMinHash(set2);
        long end = System.nanoTime();
        System.out.println("MinHash: Computation of MinHash for sets took " + (end - start) / 1e6 + " ms");

        double similarity = jaccardSimilarity(minHash1, minHash2);
        System.out.println("MinHash: Jaccard Similarity: " + similarity);
    }

    public static void main(String[] args) {

        Set<String> set1 = new HashSet<>();
        set1.add("apple");
        set1.add("banana");
        set1.add("cherry");

        Set<String> set2 = new HashSet<>();
        set2.add("apple");
        set2.add("banana");
        set2.add("grape");

        MinHash minHash = new MinHash();

        // Запуск теста на производительность и сходство
        minHash.runPerformanceTest(set1, set2);

        // Добавляем элемент и пересчитываем MinHash
        System.out.println("Adding 'orange' to set1...");
        minHash.addElement(set1, "orange");
        minHash.runPerformanceTest(set1, set2);

        // Удаляем элемент и пересчитываем MinHash
        System.out.println("Removing 'banana' from set2...");
        minHash.removeElement(set2, "banana");
        minHash.runPerformanceTest(set1, set2);
    }
}
