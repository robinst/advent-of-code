package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day25 {

    enum Type {
        LOCK,
        KEY
    }

    record Schematic(Type type, List<Integer> heights) {
    }

    static long solve1(String input) {
        var blocks = input.trim().split("\n\n");
        var locks = new ArrayList<List<Integer>>();
        var keys = new ArrayList<List<Integer>>();
        for (var block : blocks) {
            var schematic = parseSchematic(block);
            switch (schematic.type()) {
                case LOCK -> locks.add(schematic.heights());
                case KEY -> keys.add(schematic.heights());
            }
        }

        var fits = 0L;
        for (var lock : locks) {
            for (var key : keys) {
                if (fits(key, lock)) {
                    fits++;
                }
            }
        }
        return fits;
    }

    static boolean fits(List<Integer> key, List<Integer> lock) {
        for (int i = 0; i < key.size(); i++) {
            var k = key.get(i);
            var l = lock.get(i);
            if (k + l > 5) {
                return false;
            }
        }
        return true;
    }

    static Schematic parseSchematic(String input) {
        if (input.startsWith("#####")) {
            return new Schematic(Type.LOCK, parseHeights(input, true));
        } else if (input.endsWith("#####")) {
            return new Schematic(Type.KEY, parseHeights(input, false));
        } else {
            throw new IllegalArgumentException("Unknown schematic type: " + input);
        }
    }

    static List<Integer> parseHeights(String input, boolean top) {
        var lines = List.of(input.split("\n"));
        var heights = new int[5];
        for (int y = 0; y < lines.size(); y++) {
            var chars = lines.get(y).split("");
            for (int x = 0; x < chars.length; x++) {
                if (chars[x].equals("#")) {
                    var height = top ? y : 6 - y;
                    heights[x] = Math.max(height, heights[x]);
                }
            }
        }
        return Arrays.stream(heights).boxed().toList();
    }

    @Test
    void example() {
        var s = """
                #####
                .####
                .####
                .####
                .#.#.
                .#...
                .....

                #####
                ##.##
                .#.##
                ...##
                ...#.
                ...#.
                .....

                .....
                #....
                #....
                #...#
                #.#.#
                #.###
                #####

                .....
                .....
                #.#..
                ###..
                ###.#
                ###.#
                #####

                .....
                .....
                .....
                #....
                #.#..
                #.#.#
                #####
                """;
        assertEquals(3, solve1(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day25.txt"));
        assertEquals(3127, solve1(input));
    }
}
