package org.example.hashtable;

import java.io.*;
import java.util.*;

class Bucket implements Serializable {
    public int localDepth;
    public int capacity;
    public List<String> keys;

    public Bucket(int localDepth, int capacity) {
        this.localDepth = localDepth;
        this.capacity = capacity;
        this.keys = new ArrayList<>();
    }

    public boolean isFull() {
        return keys.size() >= capacity;
    }

    public void insert(String key) {
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    public void remove(String key) {
        keys.remove(key);
    }

    public boolean contains(String key) {
        return keys.contains(key);
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    public static Bucket loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Bucket) ois.readObject();
        }
    }
}

public class ExtendibleHashTable {
    private int globalDepth;
    private int bucketCapacity;
    private List<String> directory;
    private static int bucketCounter = 0;

    public ExtendibleHashTable(int bucketCapacity) throws IOException {
        this.globalDepth = 1;
        this.bucketCapacity = bucketCapacity;
        this.directory = new ArrayList<>();
        String bucketFile1 = createBucketFile(1);
        String bucketFile2 = createBucketFile(1);
        directory.add(bucketFile1);
        directory.add(bucketFile2);
    }

    private String createBucketFile(int localDepth) throws IOException {
        String filename = "tmp_" + (bucketCounter++) + ".txt";
        Bucket bucket = new Bucket(localDepth, bucketCapacity);
        bucket.saveToFile(filename);
        return filename;
    }

    private int hash(String key) {
        return key.hashCode() & ((1 << globalDepth) - 1);
    }

    public void insert(String key) throws IOException, ClassNotFoundException {
        int index = hash(key);
        String bucketFile = directory.get(index);
        Bucket bucket = Bucket.loadFromFile(bucketFile);

        if (!bucket.isFull()) {
            bucket.insert(key);
            bucket.saveToFile(bucketFile);
            return;
        }

        if (bucket.localDepth == globalDepth) {
            expandDirectory();
        }

        splitBucket(index, bucket);
        insert(key);
    }

    private void expandDirectory() {
        globalDepth++;
        List<String> newDir = new ArrayList<>(directory);
        newDir.addAll(directory);
        directory = newDir;
    }

    private void splitBucket(int index, Bucket oldBucket) throws IOException {
        int localDepth = oldBucket.localDepth;
        int newLocalDepth = localDepth + 1;

        String newBucketFile0 = createBucketFile(newLocalDepth);
        String newBucketFile1 = createBucketFile(newLocalDepth);

        Bucket bucket0 = new Bucket(newLocalDepth, bucketCapacity);
        Bucket bucket1 = new Bucket(newLocalDepth, bucketCapacity);

        for (String key : oldBucket.keys) {
            int newIndex = hash(key);
            if ((newIndex & (1 << localDepth)) == 0) {
                bucket0.insert(key);
            } else {
                bucket1.insert(key);
            }
        }

        bucket0.saveToFile(newBucketFile0);
        bucket1.saveToFile(newBucketFile1);

        for (int i = 0; i < directory.size(); i++) {
            if (directory.get(i).equals(directory.get(index))) {
                if ((i & (1 << localDepth)) == 0) {
                    directory.set(i, newBucketFile0);
                } else {
                    directory.set(i, newBucketFile1);
                }
            }
        }
    }

    public boolean search(String key) throws IOException, ClassNotFoundException {
        int index = hash(key);
        Bucket bucket = Bucket.loadFromFile(directory.get(index));
        return bucket.contains(key);
    }

    public void delete(String key) throws IOException, ClassNotFoundException {
        int index = hash(key);
        String bucketFile = directory.get(index);
        Bucket bucket = Bucket.loadFromFile(bucketFile);
        bucket.remove(key);
        bucket.saveToFile(bucketFile);
    }

    public boolean contains(String key) throws IOException, ClassNotFoundException {
        return search(key);
    }
}
