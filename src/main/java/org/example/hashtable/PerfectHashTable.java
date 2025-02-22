package org.example.hashtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerfectHashTable {
    private static final int PRIME = 2147483647;
    private int primaryA;
    private int primaryB;
    private int m;
    private SecondaryTable[] secondaryTables;

    private String[] keys;

    private static class SecondaryTable {
        int a;
        int b;
        int size;
        String[] table;

        SecondaryTable(int size) {
            this.size = size;
            this.table = new String[size];
        }
    }

    public PerfectHashTable(String[] keys) {
        this.keys = keys;
        this.m = keys.length;
        this.secondaryTables = new SecondaryTable[m];
        Random rand = new Random();
        primaryA = rand.nextInt(PRIME - 1) + 1;
        primaryB = rand.nextInt(PRIME);

        List<String>[] buckets = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            buckets[i] = new ArrayList<>();
        }
        for (String key : keys) {
            int x = key.hashCode();
            int bucketIndex = primaryHash(x);
            buckets[bucketIndex].add(key);
        }

        for (int i = 0; i < m; i++) {
            List<String> bucketKeys = buckets[i];
            if (bucketKeys.isEmpty()) {
                secondaryTables[i] = null;
            } else {
                int s = bucketKeys.size();
                int tableSize = s * s;
                SecondaryTable st = new SecondaryTable(tableSize);
                boolean found = false;
                while (!found) {
                    st.a = rand.nextInt(PRIME - 1) + 1;
                    st.b = rand.nextInt(PRIME);
                    boolean collision = false;
                    String[] tempTable = new String[tableSize];
                    for (String key : bucketKeys) {
                        int x = key.hashCode();
                        int idx = secondaryHash(x, st.a, st.b, tableSize);
                        if (tempTable[idx] != null) {
                            collision = true;
                            break;
                        } else {
                            tempTable[idx] = key;
                        }
                    }
                    if (!collision) {
                        st.table = tempTable;
                        found = true;
                    }
                }
                secondaryTables[i] = st;
            }
        }
    }

    private int primaryHash(int x) {
        long hash = ((long) primaryA * (x & 0x7fffffff) + primaryB) % PRIME;
        return (int) (hash % m);
    }

    private int secondaryHash(int x, int a, int b, int tableSize) {
        long hash = ((long) a * (x & 0x7fffffff) + b) % PRIME;
        return (int) (hash % tableSize);
    }

    public String search(String key) {
        int x = key.hashCode();
        int bucketIndex = primaryHash(x);
        SecondaryTable st = secondaryTables[bucketIndex];
        if (st == null) return null;
        int idx = secondaryHash(x, st.a, st.b, st.size);
        String found = st.table[idx];
        if (found != null && found.equals(key)) {
            return found;
        }
        return null;
    }

    public void print() {
        for (int i = 0; i < m; i++) {
            System.out.print("Bucket " + i + ": ");
            SecondaryTable st = secondaryTables[i];
            if (st != null) {
                for (int j = 0; j < st.size; j++) {
                    if (st.table[j] != null) {
                        System.out.print("[" + st.table[j] + "] ");
                    }
                }
            }
            System.out.println();
        }
    }
}
