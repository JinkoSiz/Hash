package org.example.hashtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class KeyValue {
    String key;
    String value;

    KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

class Bucket {
    List<KeyValue> records;

    Bucket() {
        this.records = new ArrayList<>();
    }
}

public class ExtendibleHashTable {
    private List<Bucket> directory;
    private int globalDepth;
    private int bucketSize;
    private int[] localDepth;

    public ExtendibleHashTable(int bucketSize) {
        this.globalDepth = 1;
        this.bucketSize = bucketSize;
        int directorySize = 1 << globalDepth;
        this.directory = new ArrayList<>(directorySize);
        Bucket initialBucket = new Bucket();
        for (int i = 0; i < directorySize; i++) {
            this.directory.add(initialBucket);
        }
        this.localDepth = new int[directorySize];
        for (int i = 0; i < directorySize; i++) {
            this.localDepth[i] = 1;
        }
    }

    private int hash(String key) {
        int h = key.hashCode();
        return h & 0x7fffffff;
    }

    public void insert(String key, String value) {
        int h = hash(key);
        int bucketIndex = h & ((1 << globalDepth) - 1);

        if (directory.get(bucketIndex).records.size() >= bucketSize) {
            splitBucket(bucketIndex);
            bucketIndex = h & ((1 << globalDepth) - 1);
        }

        directory.get(bucketIndex).records.add(new KeyValue(key, value));
    }

    private void splitBucket(int index) {
        Bucket oldBucket = directory.get(index);
        int oldLocalDepth = localDepth[index];

        localDepth[index]++;
        if (localDepth[index] > globalDepth) {
            globalDepth++;
            int newSize = 1 << globalDepth;
            List<Bucket> newDirectory = new ArrayList<>(newSize);
            int[] newLocalDepth = new int[newSize];
            for (int i = 0; i < newSize; i++) {
                int oldIndex = i & ((1 << (globalDepth - 1)) - 1);
                newDirectory.add(directory.get(oldIndex));
                newLocalDepth[i] = localDepth[oldIndex];
            }
            directory = newDirectory;
            localDepth = newLocalDepth;
        }

        Bucket newBucket = new Bucket();
        int mask = 1 << (localDepth[index] - 1);

        for (int i = 0; i < directory.size(); i++) {
            if (directory.get(i) == oldBucket && ((i & mask) != 0)) {
                directory.set(i, newBucket);
                localDepth[i] = localDepth[index];
            }
        }

        List<KeyValue> temp = new ArrayList<>(oldBucket.records);
        oldBucket.records.clear();
        for (KeyValue kv : temp) {
            int hVal = hash(kv.key);
            int idx = hVal & ((1 << globalDepth) - 1);
            directory.get(idx).records.add(kv);
        }
    }

    public String search(String key) {
        int h = hash(key);
        int bucketIndex = h & ((1 << globalDepth) - 1);
        for (KeyValue kv : directory.get(bucketIndex).records) {
            if (kv.key.equals(key)) {
                return kv.value;
            }
        }
        return null;
    }

    public void delete(String key) {
        int h = hash(key);
        int bucketIndex = h & ((1 << globalDepth) - 1);
        Bucket bucket = directory.get(bucketIndex);
        bucket.records.removeIf(kv -> kv.key.equals(key));
    }

    public void printUnique() {
        Map<Bucket, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < directory.size(); i++) {
            Bucket b = directory.get(i);
            map.computeIfAbsent(b, k -> new ArrayList<>()).add(i);
        }
        for (Map.Entry<Bucket, List<Integer>> entry : map.entrySet()) {
            System.out.print("Directory indices " + entry.getValue() + " -> ");
            for (KeyValue kv : entry.getKey().records) {
                System.out.print("[" + kv.key + "=" + kv.value + "] ");
            }
            System.out.println();
        }
    }
}
