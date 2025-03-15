package org.example.hashtable;

import java.util.*;

public class MinHash {
    private int numHashFunctions;
    private int[] a;
    private int[] b;
    private int prime;
    private Map<String, Integer> universe;
    private Set<String> elements;
    private int[] minHashSignature;

    public MinHash(int numHashFunctions) {
        this.numHashFunctions = numHashFunctions;
        this.a = new int[numHashFunctions];
        this.b = new int[numHashFunctions];
        this.prime = 2147483647;
        this.universe = new HashMap<>();
        this.elements = new HashSet<>();
        this.minHashSignature = new int[numHashFunctions];

        Random rand = new Random();
        for (int i = 0; i < numHashFunctions; i++) {
            a[i] = rand.nextInt(prime - 1) + 1;
            b[i] = rand.nextInt(prime - 1) + 1;
        }

        Arrays.fill(minHashSignature, Integer.MAX_VALUE);
    }

    private int hash(int x, int i) {
        return (a[i] * x + b[i]) % prime;
    }

    public void addElement(String element) {
        if (!universe.containsKey(element)) {
            universe.put(element, universe.size());
        }
        int x = universe.get(element);
        elements.add(element);

        for (int i = 0; i < numHashFunctions; i++) {
            minHashSignature[i] = Math.min(minHashSignature[i], hash(x, i));
        }
    }

    public void removeElement(String element) {
        if (!elements.contains(element)) return;
        elements.remove(element);
        Arrays.fill(minHashSignature, Integer.MAX_VALUE);
        for (String el : elements) {
            int x = universe.get(el);
            for (int i = 0; i < numHashFunctions; i++) {
                minHashSignature[i] = Math.min(minHashSignature[i], hash(x, i));
            }
        }
    }

    public int[] getSignature() {
        return minHashSignature;
    }

    public static double computeSimilarity(int[] sig1, int[] sig2) {
        int matches = 0;
        for (int i = 0; i < sig1.length; i++) {
            if (sig1[i] == sig2[i]) matches++;
        }
        return (double) matches / sig1.length;
    }
}