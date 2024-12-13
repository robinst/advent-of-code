package aoc2024;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day13 {

    static long solve1(String input) {
        var machines = input.split("\n\n");
        var result = 0L;
        for (var machine : machines) {
            var numbers = Parsing.numbers(machine);
            if (numbers.size() != 6) {
                throw new IllegalStateException("Expected 6 numbers, got " + numbers.size());
            }
            var x = numbers.get(4);
            var y = numbers.get(5);
            var tokens = tokens(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), x, y);
            if (tokens != null) {
                result += tokens;
            }
        }
        return result;
    }

    static long solve2(String input) {
        var machines = input.split("\n\n");
        var result = 0L;
        for (var machine : machines) {
            var numbers = Parsing.numbersLong(machine);
            if (numbers.size() != 6) {
                throw new IllegalStateException("Expected 6 numbers, got " + numbers.size());
            }
            var x = numbers.get(4) + 10000000000000L;
            var y = numbers.get(5) + 10000000000000L;
            var tokens = tokens(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), x, y);
            if (tokens != null) {
                result += tokens;
            }
        }
        return result;
    }

    static Long tokens(long ax, long ay, long bx, long by, long x, long y) {
        // a * ax = x, a * ay = y
        // b * bx = x, b * by = y
        //
        // a * ax + b * bx = x
        // a * ay + b * by = y
        //
        // Solving for a:
        // b * bx = x - a * ax
        // b * by = y - a * ay
        //
        // b = (x - a * ax) / bx
        // b = (y - a * ay) / by
        //
        // (x - a * ax) / bx = (y - a * ay) / by
        //
        // (x - a * ax) * by = (y - a * ay) * bx
        //
        // x * by - a * ax * by = y * bx - a * ay * bx
        //
        // x * by + a * ay * bx - a * ax * by = y * bx
        //
        // a * ay * bx - a * ax * by = y * bx - x * by
        // a * (ay * bx - ax * by)   = y * bx - x * by
        //
        // a = (y * bx - x * by) / (ay * bx - ax * by)
        //
        // Solving for b:
        // b = (y * ax - x * ay) / (by * ax - bx * ay)

        long numA = y * bx - x * by;
        long denA = ay * bx - ax * by;
        if (numA % denA != 0) {
            return null;
        }
        long a = numA / denA;

        long numB = y * ax - x * ay;
        long denB = by * ax - bx * ay;
        if (numB % denB != 0) {
            return null;
        }
        long b = numB / denB;
        return a * 3 + b;
    }

    @Test
    void example() {
        var s = """
                Button A: X+94, Y+34
                Button B: X+22, Y+67
                Prize: X=8400, Y=5400

                Button A: X+26, Y+66
                Button B: X+67, Y+21
                Prize: X=12748, Y=12176

                Button A: X+17, Y+86
                Button B: X+84, Y+37
                Prize: X=7870, Y=6450

                Button A: X+69, Y+23
                Button B: X+27, Y+71
                Prize: X=18641, Y=10279
                """;
        assertEquals(480, solve1(s));
        assertEquals(875318608908L, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day13.txt"));
        assertEquals(37128, solve1(input));
        assertEquals(74914228471331L, solve2(input));
    }
}
