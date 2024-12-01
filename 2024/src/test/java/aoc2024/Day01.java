package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day01 {

    static long solve1(String input) {
        var nums1 = new ArrayList<Integer>();
        var nums2 = new ArrayList<Integer>();
        var lines = input.split("\n");
        for (var line : lines) {
            var nums = Parsing.numbers(line);
            var a = nums.get(0);
            var b = nums.get(1);
            nums1.add(a);
            nums2.add(b);
        }

        nums1.sort(Comparator.reverseOrder());
        nums2.sort(Comparator.reverseOrder());

        var result = 0;
        while (!nums1.isEmpty()) {
            var a = nums1.removeLast();
            var b = nums2.removeLast();
            result += Math.abs(a - b);
        }
        return result;
    }

    static long solve2(String input) {
        var nums1 = new ArrayList<Integer>();
        var nums2 = new HashMap<Integer, Integer>();
        var lines = input.split("\n");
        for (var line : lines) {
            var nums = Parsing.numbers(line);
            var num1 = nums.get(0);
            var num2 = nums.get(1);
            nums1.add(num1);
            nums2.merge(num2, 1, Integer::sum);
        }

        var result = 0;
        for (var i : nums1) {
            result += i * nums2.getOrDefault(i, 0);
        }
        return result;
    }

    @Test
    void example() {
        var s = """
                3   4
                4   3
                2   5
                1   3
                3   9
                3   3
                """;
        assertEquals(11, solve1(s));
        assertEquals(31, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day01.txt"));
        assertEquals(2196996, solve1(input));
        assertEquals(23655822, solve2(input));
    }
}
