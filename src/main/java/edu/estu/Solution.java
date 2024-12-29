package edu.estu;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public record Solution(Set<Item> selectedItems) {
    public long totalWeight() {
        return selectedItems.stream().mapToLong(Item::weight).sum();
    }

    public long totalProfit() {
        return selectedItems.stream().mapToLong(Item::profit).sum();
    }

    public void print() {
        System.out.printf("Total profit: %d, Total weight: %d, Items: %s%n",
                totalProfit(), totalWeight(), selectedItems);
    }
}
 class KnapsackSolver {
    private final KP01 problem;
    private final Random random = new Random();

    public KnapsackSolver(KP01 problem) {
        this.problem = problem;
    }

    public Solution generateRandomSolution() {
        Set<Item> available = new HashSet<>(problem.items());
        Set<Item> selected = new HashSet<>();
        long currentWeight = 0;

        while (!available.isEmpty()) {
            Item[] items = available.toArray(new Item[0]);
            Item randomItem = items[random.nextInt(items.length)];

            if (currentWeight + randomItem.weight() <= problem.capacity()) {
                selected.add(randomItem);
                currentWeight += randomItem.weight();
            }

            available.remove(randomItem);
        }

        return new Solution(selected);
    }

    public Solution greedySolution() {
        List<Item> sortedItems = new ArrayList<>(problem.items());
        sortedItems.sort((a, b) -> {
            double ratioA = (double) a.profit() / a.weight();
            double ratioB = (double) b.profit() / b.weight();
            return Double.compare(ratioB, ratioA);
        });

        Set<Item> selected = new HashSet<>();
        long currentWeight = 0;

        for (Item item : sortedItems) {
            if (currentWeight + item.weight() <= problem.capacity()) {
                selected.add(item);
                currentWeight += item.weight();
            }
        }

        return new Solution(selected);
    }

    public void compareStrategiesParallel(int iterations) {
        Solution greedySolution = greedySolution();
        long greedyProfit = greedySolution.totalProfit();

        System.out.println("Greedy solution profit: " + greedyProfit);

        // Atomic değişkenler thread-safe olması için
        AtomicLong minProfit = new AtomicLong(Long.MAX_VALUE);
        AtomicLong maxProfit = new AtomicLong(Long.MIN_VALUE);
        AtomicLong totalProfit = new AtomicLong(0);
        AtomicInteger betterCount = new AtomicInteger(0);

        // Parallel stream kullanarak 1 milyon denemeyi paralel yap
        IntStream.range(0, iterations)
                .parallel()
                .forEach(i -> {
                    Solution randomSolution = generateRandomSolution();
                    long profit = randomSolution.totalProfit();

                    // Minimum profit güncelle
                    minProfit.updateAndGet(current -> Math.min(current, profit));

                    // Maximum profit güncelle
                    maxProfit.updateAndGet(current -> Math.max(current, profit));

                    // Toplam profit güncelle
                    totalProfit.addAndGet(profit);

                    // Eğer greedy'den daha iyiyse sayacı arttır
                    if (profit > greedyProfit) {
                        betterCount.incrementAndGet();
                    }
                });

        double avgProfit = (double) totalProfit.get() / iterations;

        System.out.printf("Random solutions stats - Min: %d, Avg: %.2f, Max: %d%n",
                minProfit.get(), avgProfit, maxProfit.get());
        System.out.printf("Random solutions better than greedy: %d/%d%n",
                betterCount.get(), iterations);
    }
}