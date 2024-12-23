package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day21 {

    enum Command {
        UP,
        LEFT,
        RIGHT,
        DOWN,
        ACTIVATE;

        @Override
        public String toString() {
            return switch (this) {
                case UP -> "^";
                case LEFT -> "<";
                case RIGHT -> ">";
                case DOWN -> "v";
                case ACTIVATE -> "A";
            };
        }
    }

    static class Keypad<T> {
        private final Map<T, Pos> pad;
        private final Pos avoid;
        private final Pos start;

        public Keypad(Map<T, Pos> pad, Pos avoid, Pos start) {
            this.pad = pad;
            this.avoid = avoid;
            this.start = start;
        }
    }

    record MemKey<T>(List<T> inputs, int remainingLevels) {
    }

    // +---+---+---+
    // | 7 | 8 | 9 |
    // +---+---+---+
    // | 4 | 5 | 6 |
    // +---+---+---+
    // | 1 | 2 | 3 |
    // +---+---+---+
    //     | 0 | A |
    //     +---+---+
    static final Keypad<String> numericPad = new Keypad<>(
            Map.ofEntries(
                    entry("7", new Pos(0, 0)),
                    entry("8", new Pos(1, 0)),
                    entry("9", new Pos(2, 0)),
                    entry("4", new Pos(0, 1)),
                    entry("5", new Pos(1, 1)),
                    entry("6", new Pos(2, 1)),
                    entry("1", new Pos(0, 2)),
                    entry("2", new Pos(1, 2)),
                    entry("3", new Pos(2, 2)),
                    entry("0", new Pos(1, 3)),
                    entry("A", new Pos(2, 3))),
            new Pos(0, 3),
            new Pos(2, 3));

    //     +---+---+
    //     | ^ | A |
    // +---+---+---+
    // | < | v | > |
    // +---+---+---+
    static final Keypad<Command> directionalPad = new Keypad<>(
            Map.of(
                    Command.UP, new Pos(1, 0),
                    Command.ACTIVATE, new Pos(2, 0),
                    Command.LEFT, new Pos(0, 1),
                    Command.DOWN, new Pos(1, 1),
                    Command.RIGHT, new Pos(2, 1)
            ),
            new Pos(0, 0),
            new Pos(2, 0)
    );

    static long solve1(String input) {
        var codes = input.split("\n");
        var result = 0L;

        for (var code : codes) {
            System.out.println("Code: " + code);
            var num = Parsing.numbers(code).getFirst();
            var shortest = shortest(numericPad, List.of(code.split("")), 3, new HashMap<>());
            result += num * shortest;
        }
        return result;
    }

    static long solve2(String input) {
        var codes = input.split("\n");
        var result = 0L;
        for (var code : codes) {
            System.out.println("Code: " + code);
            var num = Parsing.numbersLong(code).getFirst();
            var shortest = shortest(numericPad, List.of(code.split("")), 26, new HashMap<>());
            result += num * shortest;
        }
        return result;
    }

    static <T> long shortest(Keypad<T> keypad, List<T> inputs, int remainingLevels, Map<MemKey<?>, Long> mem) {
        if (remainingLevels == 0) {
            return inputs.size();
        }

        var key = new MemKey<T>(inputs, remainingLevels);
        var memResult = mem.get(key);
        if (memResult != null) {
            return memResult;
        }

        var result = 0L;
        var pos = keypad.start;
        for (var input : inputs) {
            var target = keypad.pad.get(input);

            if (pos.equals(target)) {
                result += shortest(directionalPad, List.of(Command.ACTIVATE), remainingLevels - 1, mem);
            } else {
                // Positive is right/down, negative is left/up
                var move = target.minus(pos);
                var ys = Collections.nCopies(Math.abs(move.y()), move.y() > 0 ? Command.DOWN : Command.UP);
                var xs = Collections.nCopies(Math.abs(move.x()), move.x() > 0 ? Command.RIGHT : Command.LEFT);
                if (pos.x() == keypad.avoid.x() && target.y() == keypad.avoid.y()) {
                    // Can not go down first then right, need to go right first
                    var c = sequence(xs, ys);
                    result += shortest(directionalPad, c, remainingLevels - 1, mem);
                } else if (pos.y() == keypad.avoid.y() && target.x() == keypad.avoid.x()) {
                    // Can not go left first then up, need to go up first
                    var c = sequence(ys, xs);
                    result += shortest(directionalPad, c, remainingLevels - 1, mem);
                } else if (move.x() == 0 || move.y() == 0) {
                    var c = sequence(xs, ys);
                    result += shortest(directionalPad, c, remainingLevels - 1, mem);
                } else {
                    // There are multiple ways to go here, e.g. if we need to go one right and two up, we do:
                    // - RIGHT, UP, UP
                    // - UP, UP, RIGHT
                    // - UP, RIGHT, UP
                    // The last one we don't need to try though, as it's always going to result in more commands than
                    // the others.
                    var option1 = sequence(ys, xs);
                    var option2 = sequence(xs, ys);

                    var shortest1 = shortest(directionalPad, option1, remainingLevels - 1, mem);
                    var shortest2 = shortest(directionalPad, option2, remainingLevels - 1, mem);
                    result += Math.min(shortest1, shortest2);
                }
            }

            pos = target;
        }

        mem.put(key, result);
        return result;
    }

    static List<Command> sequence(List<Command> first, List<Command> second) {
        var c = new ArrayList<Command>();
        c.addAll(first);
        c.addAll(second);
        c.add(Command.ACTIVATE);
        return c;
    }

    @Test
    void example() {
        var s = """
                029A
                980A
                179A
                456A
                379A
                """;
        assertEquals(126384L, solve1(s));
        assertEquals(154115708116294L, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day21.txt"));
        assertEquals(231564L, solve1(input));
        assertEquals(281212077733592L, solve2(input));
    }
}
