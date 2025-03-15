package org.example.hashtable;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ExtendibleHashTableBenchmark {

    private static final int SIZE = 10000;
    private static final int OPERATIONS = 100;
    private static final Random rand = new Random();
    private String[] testKeys;
    private ExtendibleHashTable hashTable;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        clearBuckets();
        hashTable = new ExtendibleHashTable(1000);
        testKeys = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            testKeys[i] = "Key" + i;
        }
    }

    private void clearBuckets() {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.startsWith("tmp_") && name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    @Benchmark
    public void benchmarkBulkInsert() throws IOException, ClassNotFoundException {
        for (String key : testKeys) {
            hashTable.insert(key);
        }
    }

    @Benchmark
    public void benchmarkBulkSearch() throws IOException, ClassNotFoundException {
        for (int i = 0; i < OPERATIONS; i++) {
            hashTable.search(testKeys[rand.nextInt(SIZE)]);
        }
    }

    @Benchmark
    public void benchmarkBulkDelete() throws IOException, ClassNotFoundException {
        for (int i = 0; i < OPERATIONS; i++) {
            hashTable.delete(testKeys[rand.nextInt(SIZE)]);
        }
    }

    @Benchmark
    public void benchmarkMixedOperations() throws IOException, ClassNotFoundException {
        for (int i = 0; i < OPERATIONS; i++) {
            int op = rand.nextInt(3);
            switch (op) {
                case 0:
                    hashTable.insert("MixedKey" + System.nanoTime());
                    break;
                case 1:
                    hashTable.search(testKeys[rand.nextInt(SIZE)]);
                    break;
                case 2:
                    hashTable.delete(testKeys[rand.nextInt(SIZE)]);
                    break;
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ExtendibleHashTableBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(0)
                .measurementIterations(3)
                .build();
        new Runner(opt).run();
    }
}
