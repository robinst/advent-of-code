package aoc2025;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day01 {

    static long solve1(String input) {
        var lines = input.lines().toList();
        var password = 0;
        var current = 50;
        for (var line : lines) {
            int clicks;
            if (line.startsWith("L")) {
                clicks = -Parsing.numbers(line).getFirst();
            } else if (line.startsWith("R")) {
                clicks = Parsing.numbers(line).getFirst();
            } else {
                throw new IllegalArgumentException();
            }
            current = (100 + current + clicks) % 100;
            if (current == 0) {
                password++;
            }
        }
        return password;
    }

    static long solve2(String input) {
        var lines = input.lines().toList();
        var password = 0;
        var current = 50;
        for (var line : lines) {
            int clicks;
            if (line.startsWith("L")) {
                clicks = -Parsing.numbers(line).getFirst();
            } else if (line.startsWith("R")) {
                clicks = Parsing.numbers(line).getFirst();
            } else {
                throw new IllegalArgumentException();
            }

            var direction = clicks > 0 ? 1 : -1;
            for (int i = 0; i < Math.abs(clicks); i++) {
                current = (100 + current + direction) % 100;
                if (current == 0) {
                    password++;
                }
            }
        }
        return password;
    }

    @Test
    void example() {
        var s = """
                L68
                L30
                R48
                L5
                R60
                L55
                L1
                L99
                R14
                L82
                """;
        assertEquals(3, solve1(s));
        assertEquals(6, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day01.txt"));
        assertEquals(1180, solve1(input));
        assertEquals(6892, solve2(input));
    }
}
