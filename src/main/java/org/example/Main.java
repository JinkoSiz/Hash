package org.example;

import org.example.hashtable.ExtendibleHashTable;
import org.example.hashtable.PerfectHashTable;
import org.example.hashtable.MinHash;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // Extendible Hashing
        System.out.println("=== Extendible Hashing Demo ===");
        ExtendibleHashTable htExtendible = new ExtendibleHashTable(2);
        htExtendible.insert("name", "Alice");
        htExtendible.insert("city", "New York");
        htExtendible.insert("job", "Developer");
        htExtendible.printUnique();

        // Perfect Hashing
        System.out.println("\n=== Perfect Hashing Demo ===");
        String[] keys = {"name", "age", "city", "job"};
        PerfectHashTable ph = new PerfectHashTable(keys);
        System.out.println("Search 'city': " + ph.search("city"));
        ph.print();

        // MinHash
        System.out.println("\n=== MinHash Demo ===");
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
    }
}
