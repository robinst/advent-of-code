package aoc2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day02 {
    
    static long solve1(String input) {
        var ranges = input.trim().split(",");
        var invalid = 0L;
        for (var range : ranges) {
            var parts = range.split("-");
            var start = Long.parseLong(parts[0]);
            var end = Long.parseLong(parts[1]);
            for (var i = start; i <= end; i++) {
                var s = String.valueOf(i);
                if (s.length() % 2 == 0) {
                    var first = s.substring(0, s.length() / 2);
                    var second = s.substring(s.length() / 2);
                    if (first.equals(second)) {
                        invalid += i;
                    }
                }
            }
        }
        return invalid;
    }

    static long solve2(String input) {
        var ranges = input.trim().split(",");
        var invalid = 0L;
        for (var range : ranges) {
            var parts = range.split("-");
            var start = Long.parseLong(parts[0]);
            var end = Long.parseLong(parts[1]);
            for (var i = start; i <= end; i++) {
                var s = String.valueOf(i);
                if (isSilly2(s)) {
                    invalid += i;
                }
            }
        }
        return invalid;
    }

    static boolean isSilly2(String s) {
        for (var len = 1; len <= s.length() / 2; len++) {
            var first = s.substring(0, len);
            if (isSilly2(s, len, first)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSilly2(String s, int len, String first) {
        for (var i = len; i < s.length(); i += len) {
            var second = s.substring(i, Math.min(i + len, s.length()));
            if (!second.equals(first)) {
                return false;
            }
        }
        return true;
    }

    @Test
    void example() {
        var s = "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124";
        assertEquals(1227775554, solve1(s));
        assertEquals(4174379265L, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day02.txt"));
        assertEquals(31210613313L, solve1(input));
        assertEquals(41823587546L, solve2(input));
    }
}
