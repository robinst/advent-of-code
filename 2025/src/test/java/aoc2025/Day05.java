package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day05 {

    record RangeInclusive(long start, long end) implements Comparable<RangeInclusive> {

        boolean includes(long ingredient) {
            return ingredient >= start && ingredient <= end;
        }

        @Override
        public int compareTo(RangeInclusive o) {
            return Long.compare(start, o.start);
        }
    }

    static long solve1(String input) {
        var parts = input.split("\n\n");

        var freshRanges = parts[0].lines().map(l -> {
            var numbers = l.split("-");
            return new RangeInclusive(Long.parseLong(numbers[0]), Long.parseLong(numbers[1]));
        }).toList();

        var ingredients = Parsing.numbersLong(parts[1]);

        return ingredients.stream().filter(ingredient -> freshRanges.stream().anyMatch(r -> r.includes(ingredient))).count();
    }

    static long solve2(String input) {
        var parts = input.split("\n\n");

        var freshRanges = parts[0].lines().map(l -> {
            var numbers = l.split("-");
            return new RangeInclusive(Long.parseLong(numbers[0]), Long.parseLong(numbers[1]));
        }).sorted().toList();

        var sum = 0L;
        var start = 0L;
        var end = 0L;
        for (var range : freshRanges) {
            if (range.start > end && start != 0) {
                // Count the range now
                sum += end - start + 1;

                start = range.start;
                end = range.end;
                continue;
            }

            if (start == 0) {
                start = range.start;
            }

            if (range.end > end) {
                end = range.end;
            }
        }

        sum += end - start + 1;
        return sum;
    }

    @Test
    void example() {
        var s = """
                3-5
                10-14
                16-20
                12-18
                
                1
                5
                8
                11
                17
                32
                """;
        assertEquals(3, solve1(s));
        assertEquals(14, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day05.txt"));
        assertEquals(640, solve1(input));
        assertEquals(365804144481581L, solve2(input));
    }
}
