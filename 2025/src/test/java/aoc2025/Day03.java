package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day03 {

    static long solve1(String input) {
        var lines = input.lines().toList();
        var sum = 0L;
        for (var line : lines) {
            var digits = line.chars().mapToObj(c -> Integer.parseInt(Character.toString(c))).toList();
            var firstDigit = 0;
            var firstIndex = 0;
            for (int i = 0; i < digits.size() - 1; i++) {
                var digit = digits.get(i);
                if (digit > firstDigit) {
                    firstDigit = digit;
                    firstIndex = i;
                }
            }
            var secondDigit = 0;
            for (int i = firstIndex + 1; i < digits.size(); i++) {
                var digit = digits.get(i);
                if (digit > secondDigit) {
                    secondDigit = digit;
                }
            }
            sum += firstDigit * 10L + secondDigit;
        }
        return sum;
    }

    static long solve2(String input) {
        var lines = input.lines().toList();
        var sum = 0L;
        for (var line : lines) {
            var digits = line.chars().mapToObj(c -> Integer.parseInt(Character.toString(c))).toList();
            var joltage = joltage(digits);
            sum += joltage;
        }
        return sum;
    }

    private static long joltage(List<Integer> digits) {
        var cache = new HashMap<Key, Long>();
        return max(cache, digits, 12);
    }

    record Key(List<Integer> digits, int length) {
    }

    private static long max(Map<Key, Long> cache, List<Integer> digits, int length) {
        var key = new Key(digits, length);
        var result = cache.get(key);
        if (result != null) {
            return result;
        }

        if (length == 1) {
            result = digits.stream().mapToLong(Long::valueOf).max().getAsLong();
        } else {
            var max = 0L;
            for (int i = 0; i < digits.size() - length + 1; i++) {
                var digit = digits.get(i);
                var pow = Math.powExact(10L, length - 1);
                var attempt = digit * pow + max(cache, digits.subList(i + 1, digits.size()), length - 1);
                if (attempt > max) {
                    max = attempt;
                }
            }
            result = max;
        }
        cache.put(key, result);
        return result;
    }

    @Test
    void example() {
        var s = """
                987654321111111
                811111111111119
                234234234234278
                818181911112111
                """;
        assertEquals(357, solve1(s));
        assertEquals(3121910778619L, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day03.txt"));
        assertEquals(17330, solve1(input));
        assertEquals(171518260283767L, solve2(input));
    }
}
