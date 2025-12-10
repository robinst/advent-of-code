package aoc2025;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day10 {

    record Machine(long targetNumber, List<Long> wiringButtons) {

        @Override
        public String toString() {
            return "[" + Long.toString(targetNumber, 2) + "] " + wiringButtons.stream().map(b -> "(" + Long.toString(b, 2) + ")").collect(Collectors.joining(", "));
        }
    }

    static long solve1(String input) {
        var lines = input.lines().toList();

        var result = 0L;
        for (var line : lines) {
            var machine = parseMachine(line);
            System.out.println(machine);

            result += fewestPresses(machine);
        }
        return result;
    }

    private static long fewestPresses(Machine machine) {
        var combinations = (1L << machine.wiringButtons.size());
        var fewest = Long.MAX_VALUE;
        for (var attempt = 0L; attempt < combinations; attempt++) {
            var state = 0L;
            var presses = 0L;
            for (int button = 0; button < machine.wiringButtons.size(); button++) {
                if ((attempt & (1L << button)) != 0L) {
                    state ^= machine.wiringButtons.get(button);
                    presses++;
                }
            }
            if (state == machine.targetNumber) {
                fewest = Math.min(fewest, presses);
            }
        }
        return fewest;
    }

    private static Machine parseMachine(String line) {
        var parts = line.split("] ");
        var target = parts[0].substring(1);
        var targetNumber = 0L;
        for (var i = 0; i < target.length(); i++) {
            var c = target.charAt(i);
            if (c == '#') {
                targetNumber |= 1L << i;
            }
        }

        var parts2 = parts[1].split(" \\{");
        var wiringsPart = parts2[0];

        var wirings = new ArrayList<Long>();

        var wiringsStrings = wiringsPart.split(" ");
        for (var wiringString : wiringsStrings) {
            var nums = Parsing.numbersLong(wiringString);
            var wiring = 0L;
            for (var num : nums) {
                wiring |= 1L << num;
            }
            wirings.add(wiring);
        }

        // var joltageString = parts2[1];

        return new Machine(targetNumber, wirings);
    }

    @Test
    void example() {
        var s = """
                [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
                [...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
                [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
                """;
        assertEquals(7, solve1(s));
        // assertEquals(33, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day10.txt"));
        assertEquals(522, solve1(input));
        // see day10.py
        // assertEquals(0, solve2(input));
    }
}
