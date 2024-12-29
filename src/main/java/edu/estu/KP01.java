package edu.estu;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * The 0–1 knapsack problem is a classical NP-hard optimization problem.
 * In this problem, one is given a knapsack with an integer capacity c and a set of n items,
 * which each have an integer profit p_i and an integer weight w_i.
 * The goal is to select a subset of items to put into the knapsack such
 * that the total value is maximized and the total weight does not
 * exceed the knapsack capacity.
 */
public class KP01 {
    private final long capacity;
    private final Set<Item> items;

    public KP01(long capacity, Item... items) {
        this.capacity = capacity;
        this.items = new HashSet<>(Arrays.asList(items));
    }

    public KP01(long capacity, Set<Item> items) {
        this.capacity = capacity;
        this.items = items;
    }

    public static KP01 fromFile(Path path) {

        try (InputStream input = Files.newInputStream(path);
             Scanner scanner = new Scanner(input)) {

            int numberOfItems = scanner.nextInt();

            Set<Item> items = new HashSet<>(numberOfItems);

            for (int i = 0; i < numberOfItems; i++) {
                items.add(new Item(scanner.nextLong(), scanner.nextLong(), scanner.nextLong()));
            }

            long capacity = scanner.nextLong();

            KP01 result = new KP01(capacity, Set.copyOf(items));
            items.clear();
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return "KP01{" +
                "capacity=" + capacity +
                ", # items=" + items.size() +
                '}';
    }
    public Set<Item> items() {
        return Collections.unmodifiableSet(items);
    }
    public long capacity() {
        return capacity;
    }
    boolean trivial() {
        return items.stream().mapToLong(Item::weight).sum() <= capacity;
    }

/*
    public void oneMillionParallel() {
        AtomicReference<Item> ref = new AtomicReference<>();

        long maxProfit = Long.MIN_VALUE;
        Solution best = null;

        IntStream.range(0, 1_000_000_000).boxed().parallel().forEach(i -> {

            Solution randomSolution = generateRandomSolution();
            if (randomSolution.totalProfit() > maxProfit) {
                maxProfit = randomSolution.totalProfit();
                best = randomSolution;
            }
        });

        best.print();
    }
    */

}
