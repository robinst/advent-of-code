package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day07 {

    static long solve1(String input) {
        var sum = 0L;
        var lines = input.split("\n");
        for (var line : lines) {
            var nums = Parsing.numbersLong(line);
            var result = nums.getFirst();
            var inputs = nums.subList(1, nums.size());
            if (check(result, inputs)) {
                sum += result;
            }
        }
        return sum;
    }

    static boolean check(long result, List<Long> inputs) {
        var num = inputs.getLast();
        if (inputs.size() == 1) {
            return result == num;
        }
        var head = inputs.subList(0, inputs.size() - 1);
        return check(result - num, head) || (result % num == 0 && check(result / num, head));
    }

    static long solve2(String input) {
        var sum = 0L;
        var lines = input.split("\n");
        for (var line : lines) {
            var nums = Parsing.numbersLong(line);
            var result = nums.getFirst();
            var inputs = nums.subList(1, nums.size());
            if (check2(result, inputs, inputs.getFirst(), 1)) {
                sum += result;
            }
        }
        return sum;
    }

    static boolean check2(long target, List<Long> inputs, long result, int index) {
        if (index >= inputs.size()) {
            return result == target;
        }
        var num = inputs.get(index);
        return check2(target, inputs, result + num, index + 1) ||
                check2(target, inputs, result * num, index + 1) ||
                check2(target, inputs, concat(result, num), index + 1);
    }

    // Example:
    // 17 || 8 = 178
    private static long concat(long a, long b) {
        long high = a;
        long n = b;
        while (n > 0) {
            n = n / 10;
            high = high * 10;
        }
        return high + b;
    }

    @Test
    void example() {
        var s = """
                190: 10 19
                3267: 81 40 27
                83: 17 5
                156: 15 6
                7290: 6 8 6 15
                161011: 16 10 13
                192: 17 8 14
                21037: 9 7 18 13
                292: 11 6 16 20
                """;
        assertEquals(3749, solve1(s));
        assertEquals(11387, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day07.txt"));
        assertEquals(7710205485870L, solve1(input));
        assertEquals(20928985450275L, solve2(input));
    }
}
