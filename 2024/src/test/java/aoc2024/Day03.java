package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day03 {

    static long solve1(String input) {
        var pattern = Pattern.compile("mul\\(\\d+,\\d+\\)");
        var matcher = pattern.matcher(input);
        var result = 0;
        while (matcher.find()) {
            var nums = Parsing.numbers(matcher.group());
            result += nums.get(0) * nums.get(1);
        }
        return result;
    }

    static long solve2(String input) {
        var pattern = Pattern.compile("mul\\(\\d+,\\d+\\)|do\\(\\)|don't\\(\\)");
        var matcher = pattern.matcher(input);
        var enabled = true;
        var result = 0;
        while (matcher.find()) {
            var m = matcher.group();
            if (m.equals("do()")) {
                enabled = true;
            } else if (m.equals("don't()")) {
                enabled = false;
            } else if (enabled) {
                var nums = Parsing.numbers(m);
                result += nums.get(0) * nums.get(1);
            }
        }
        return result;
    }

    @Test
    void example() {
        assertEquals(161, solve1("xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"));
        assertEquals(48, solve2("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day03.txt"));
        assertEquals(187833789, solve1(input));
        assertEquals(94455185, solve2(input));
    }
}
