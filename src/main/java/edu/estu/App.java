package edu.estu;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        Path path = Paths.get("src/main/java/edu/estu/test.in");

        final int n = Integer.parseInt(readFirstLine(path));
        Set<Item> items;

        try (Stream<String> stream = Files.lines(path)) {
            items = stream.skip(1)
                    .limit(n)
                    .map(App::fromLine)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final long capacity = Long.parseLong(readLastLine(path));
        KP01 problem = new KP01(capacity, items);

        // Yeni eklenen kısım - Knapsack çözücüyü kullan
        System.out.println("Problem details: " + problem);
        KnapsackSolver solver = new KnapsackSolver(problem);
        solver.compareStrategiesParallel(1_000_000);
    }

    // Yardımcı metodlar aynen kalabilir
    static String readFirstLine(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String readLastLine(Path path) {
        String last = null;
        try (BufferedReader input = Files.newBufferedReader(path)) {
            String line;
            while ((line = input.readLine()) != null) {
                last = line;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return last;
    }

    static Item fromLine(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length != 3) throw new RuntimeException(line);
        return new Item(
                Long.parseLong(parts[0]),
                Long.parseLong(parts[1]),
                Long.parseLong(parts[2])
        );
    }
}