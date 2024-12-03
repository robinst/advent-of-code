package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day02 {

    static long solve1(String input) {
        var lines = input.split("\n");
        var result = 0L;
        for (var line : lines) {
            var nums = Parsing.numbers(line);
            if (isSafe(nums)) {
                result++;
            }
        }
        return result;
    }

    private static boolean isSafe(List<Integer> nums) {
        var previous = 0;
        for (int i = 0; i < nums.size() - 1; i++) {
            var diff = nums.get(i) - nums.get(i + 1);
            if (Math.abs(diff) < 1 || Math.abs(diff) > 3) {
                return false;
            }
            if (previous != 0 && previous != Integer.signum(diff)) {
                return false;
            }
            previous = Integer.signum(diff);
        }
        return true;
    }

    static long solve2(String input) {
        var lines = input.split("\n");
        var result = 0L;
        for (var line : lines) {
            var nums = Parsing.numbers(line);
            if (isSafe(nums)) {
                result++;
            } else {
                for (int i = 0; i < nums.size(); i++) {
                    // This is not great but the input is small enough that it works
                    var newNums = new ArrayList<>(nums);
                    newNums.remove(i);
                    if (isSafe(newNums)) {
                        result++;
                        break;
                    }
                }
            }
        }
        return result;
    }

    static long solve2NoCopying(String input) {
        var lines = input.split("\n");
        var result = 0L;
        for (var line : lines) {
            var nums = Parsing.numbers(line);
            if (isSafe2(0, 1, nums, true) || isSafe2(0, 1, nums.reversed(), true)) {
                result++;
            }
        }
        return result;
    }

    private static boolean isSafe2(int first, int second, List<Integer> nums, boolean tolerant) {
        while (second <= nums.size() - 1) {
            var a = nums.get(first);
            var b = nums.get(second);
            var diff = a - b;
            if (diff < 1 || diff > 3) {
                if (!tolerant) {
                    return false;
                }
                // Try skipping b
                if (isSafe2(first, second + 1, nums, false)) {
                    return true;
                }
                // Try skipping a if it's the first one (if it's not, it's been covered by "try skipping b" previously)
                return first == 0 && isSafe2(second, second + 1, nums, false);
            }
            first = second;
            second = second + 1;
        }
        return true;
    }

    @Test
    void example() {
        var s = """
                7 6 4 2 1
                1 2 7 8 9
                9 7 6 2 1
                1 3 2 4 5
                8 6 4 4 1
                1 3 6 7 9
                """;
        assertEquals(2, solve1(s));
        assertEquals(4, solve2(s));
        assertEquals(4, solve2NoCopying(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day02.txt"));
        assertEquals(407, solve1(input));
        assertEquals(459, solve2(input));
        assertEquals(459, solve2NoCopying(input));
    }
}
