package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day22 {

    static long solve1(String input) {
        var initialNumbers = Parsing.numbersLong(input);

        var result = 0L;
        for (var num : initialNumbers) {
            for (int i = 0; i < 2000; i++) {
                num = next(num);
            }
            result += num;
        }

        return result;
    }

    // 16777216 = 0b1000000000000000000000000 = 1 << 24
    static long next(long num) {
        var multiplied = num << 6; //  * 64
        num = (num ^ multiplied) % (1 << 24);
        var divided = num >> 5; //  / 32
        num = (num ^ divided) % (1 << 24);
        var multiplied2 = num << 11; //  * 2048
        return (num ^ multiplied2) % (1 << 24);
    }

    record Sequence(int n1, int n2, int n3, int n4) {
    }

    static long solve2(String input) {
        var initialNumbers = Parsing.numbersLong(input);

        // Map from sequence of diffs to the sum of prices (from all monkeys)
        var combined = new HashMap<Sequence, Integer>();

        for (var num : initialNumbers) {
            var map = new HashMap<Sequence, Integer>();
            var diffs = new int[4];
            var diffsIndex = 0;
            var previousDigit = (int) (num % 10);
            for (int i = 0; i < 2000; i++) {
                num = next(num);
                var digit = (int) (num % 10);
                var diff = digit - previousDigit;
                previousDigit = digit;

                diffs[diffsIndex] = diff;
                diffsIndex = (diffsIndex + 1) % diffs.length;

                if (i >= 2) {
                    var key = new Sequence(
                            diffs[(diffsIndex + 1) % diffs.length],
                            diffs[(diffsIndex + 2) % diffs.length],
                            diffs[(diffsIndex + 3) % diffs.length],
                            diffs[(diffsIndex) % diffs.length]);
                    map.putIfAbsent(key, digit);
                }
            }

            for (var entry : map.entrySet()) {
                combined.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        System.out.println("All sequences: " + combined.size());
        System.out.println("Best: " + combined.entrySet().stream().max(Map.Entry.comparingByValue()).get());
        return combined.values().stream().mapToInt(i -> i).max().getAsInt();
    }

    @Test
    void example() {
        var s = """
                1
                10
                100
                2024
                """;
        assertEquals(37327623L, solve1(s));
    }

    @Test
    void examplePart2() {
        var s = """
                1
                2
                3
                2024
                """;
        assertEquals(23, solve2(s));
    }

    @Test
    public void debug() {
        solve2("123");
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day22.txt"));
        assertEquals(20332089158L, solve1(input));
        assertEquals(2191, solve2(input));
    }
}
