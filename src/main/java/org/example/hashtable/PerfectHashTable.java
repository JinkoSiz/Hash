package org.example.hashtable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PerfectHashTable {
    private String[][] table;
    private HashFunction[] hashFunctions;
    private int size;
    private HashFunction primaryHash;

    static class HashFunction {
        private final int a, b, p, m;
        private final Random rand = new Random();

        public HashFunction(int size) {
            this.m = Math.max(size, 1);
            this.p = 2147483647;
            this.a = rand.nextInt(p - 1) + 1;
            this.b = rand.nextInt(p);
        }

        public int hash(String key) {
            long hash = key.hashCode() & 0x7FFFFFFF;
            return (int) (((a * hash + b) % p) % m);
        }
    }

    public PerfectHashTable(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        table = new String[capacity][];
        hashFunctions = new HashFunction[capacity];
        size = 0;
        primaryHash = new HashFunction(capacity);
    }

    private void rebuild(String[] keys) {
        int n = keys.length;
        table = new String[n][];
        hashFunctions = new HashFunction[n];
        primaryHash = new HashFunction(n);
        List<String>[] buckets = new ArrayList[n];

        for (String key : keys) {
            int index = primaryHash.hash(key);
            if (buckets[index] == null) {
                buckets[index] = new ArrayList<>();
            }
            buckets[index].add(key);
        }
        for (int i = 0; i < n; i++) {
            if (buckets[i] == null) continue;
            int bucketSize = buckets[i].size();
            if (bucketSize == 1) {
                table[i] = new String[]{buckets[i].get(0)};
                hashFunctions[i] = null;
                continue;
            }

            boolean success = false;
            while (!success) {
                hashFunctions[i] = new HashFunction(bucketSize * bucketSize);
                table[i] = new String[bucketSize * bucketSize];
                Arrays.fill(table[i], null);

                success = true;
                for (String key : buckets[i]) {
                    int pos = hashFunctions[i].hash(key);
                    if (pos >= table[i].length || table[i][pos] != null) {
                        success = false;
                        break;
                    }
                    table[i][pos] = key;
                }
            }
        }
    }
    private void rebuildBucket(int index, String newKey) {
        List<String> keys = new ArrayList<>();
        for (String key : table[index]) {
            if (key != null) {
                keys.add(key);
            }
        }
        keys.add(newKey);

        int newSize = keys.size() * keys.size();
        while (true) {
            hashFunctions[index] = new HashFunction(newSize);
            table[index] = new String[newSize];
            Arrays.fill(table[index], null);

            boolean success = true;
            for (String key : keys) {
                int pos = hashFunctions[index].hash(key);
                if (table[index][pos] != null) {
                    success = false;
                    break;
                }
                table[index][pos] = key;
            }
            if (success) break;

            newSize *= 2;
        }
    }

    public void insert(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (contains(key)) return;

        if (size >= table.length) {
            rebuild(getAllKeys());
        }

        int index = primaryHash.hash(key);
        if (index >= table.length) {
            rebuild(getAllKeys());
            index = primaryHash.hash(key);
        }

        if (table[index] == null) {
            table[index] = new String[]{key};
            hashFunctions[index] = null;
        } else if (hashFunctions[index] == null) {
            rebuildBucket(index, key);
        } else {
            int pos = hashFunctions[index].hash(key);
            if (pos >= table[index].length || table[index][pos] != null) {
                rebuildBucket(index, key);
            } else {
                table[index][pos] = key;
            }
        }
        size++;
    }

    public void remove(String key) {
        if (!contains(key)) return;

        int index = primaryHash.hash(key);
        if (hashFunctions[index] == null) {
            table[index] = null;
        } else {
            int pos = hashFunctions[index].hash(key);
            if (pos < table[index].length) {
                table[index][pos] = null;
            }
        }
        size--;
        if (size < table.length / 4) {
            rebuild(getAllKeys());
        }
    }


    public boolean contains(String key) {
        if (table.length == 0) return false;

        int index = primaryHash.hash(key);
        if (index >= table.length || table[index] == null) return false;

        if (hashFunctions[index] == null) {
            return table[index][0] != null && table[index][0].equals(key);
        }

        int pos = hashFunctions[index].hash(key);
        return pos < table[index].length && table[index][pos] != null && table[index][pos].equals(key);
    }

    private String[] getAllKeys() {
        List<String> keys = new ArrayList<>();
        for (String[] bucket : table) {
            if (bucket != null) {
                for (String key : bucket) {
                    if (key != null) {
                        keys.add(key);
                    }
                }
            }
        }
        return keys.toArray(new String[0]);
    }
}