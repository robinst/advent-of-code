package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day11 {

    static long solve1(String input) {
        var numbers = new LinkedList<>(Parsing.numbersLong(input));
        for (int i = 0; i < 25; i++) {
            blinkList(numbers);
            System.out.println("Numbers after blink " + i + ": " + numbers.size());
        }
        return numbers.size();
    }

    static void blinkList(LinkedList<Long> numbers) {
        var iterator = numbers.listIterator();
        while (iterator.hasNext()) {
            var element = iterator.next();
            if (element == 0) {
                iterator.set(1L);
            } else if (element.toString().length() % 2 == 0) {
                var s = element.toString();
                var left = Long.parseLong(s.substring(0, s.length() / 2));
                var right = Long.parseLong(s.substring(s.length() / 2));
                iterator.set(left);
                iterator.add(right);
            } else {
                iterator.set(element * 2024L);
            }
        }
    }

    record State(long number, long steps) {
    }

    static long solve2(String input) {
        var numbers = Parsing.numbersLong(input);
        var mem = new HashMap<State, Long>();

        var result = 0L;
        for (var number : numbers) {
            result += numbers(new State(number, 75), mem);
        }
        return result;
    }

    static long numbers(State state, Map<State, Long> mem) {
        if (state.steps() == 0) {
            return 1;
        }

        var memResult = mem.get(state);
        if (memResult != null) {
            return memResult;
        }

        var result = 0L;
        var next = blink(state.number());
        for (var n : next) {
            result += numbers(new State(n, state.steps() - 1), mem);
        }
        mem.put(state, result);
        return result;
    }

    static List<Long> blink(long num) {
        if (num == 0) {
            return List.of(1L);
        } else if (String.valueOf(num).length() % 2 == 0) {
            var s = String.valueOf(num);
            var left = Long.parseLong(s.substring(0, s.length() / 2));
            var right = Long.parseLong(s.substring(s.length() / 2));
            return List.of(left, right);
        } else {
            return List.of(num * 2024L);
        }
    }

    @Test
    void example() {
        var s = "125 17";
        assertEquals(55312, solve1(s));
        assertEquals(65601038650482L, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day11.txt"));
        assertEquals(203228, solve1(input));
        assertEquals(240884656550923L, solve2(input));
    }
}
