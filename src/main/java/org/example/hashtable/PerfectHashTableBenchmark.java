package org.example.hashtable;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.Random;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class PerfectHashTableBenchmark {

    private PerfectHashTable hashTable;
    private String[] testKeys;
    private static final int SIZE = 1_000_000;
    private static final Random rand = new Random();

    @Setup(Level.Trial)
    public void setup() {
        hashTable = new PerfectHashTable(SIZE);
        testKeys = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            testKeys[i] = "Key" + i;
        }
    }

    @Benchmark
    public void benchmarkBulkInsert() {
        for (int i = 0; i < SIZE; i++) {
            hashTable.insert(testKeys[i]);
        }
    }

    @Benchmark
    public void benchmarkBulkSearch() {
        for (int i = 0; i < 100_000; i++) {
            hashTable.contains(testKeys[rand.nextInt(SIZE)]);
        }
    }

    @Benchmark
    public void benchmarkBulkRemove() {
        for (int i = 101_000; i < 201_000; i++) {
            hashTable.remove(testKeys[i]);
        }
    }

    @Benchmark
    public void benchmarkMixedOperations() {
        for (int i = 0; i < 100_000; i++) {
            int op = rand.nextInt(3);
            int idx = rand.nextInt(SIZE);
            switch (op) {
                case 0:
                    hashTable.insert("MixedKey" + idx);
                    break;
                case 1:
                    hashTable.contains(testKeys[rand.nextInt(SIZE)]);
                    break;
                case 2:
                    hashTable.remove(testKeys[rand.nextInt(SIZE)]);
                    break;
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PerfectHashTableBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(0)
                .measurementIterations(3)
                .build();
        new Runner(opt).run();
    }
}
