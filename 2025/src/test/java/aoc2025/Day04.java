package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day04 {

    enum Cell {
        Paper
    }

    static long solve1(String input) {
        var grid = Grid.parse(input, s -> s.equals("@") ? Cell.Paper : null);
        return grid.cells().keySet().stream().filter(middle -> {
            var neighbors = middle.neighbors8();
            return neighbors.stream().filter(p -> grid.cells().getOrDefault(p, null) == Cell.Paper).count() < 4;
        }).count();
    }

    static long solve2(String input) {
        var grid = Grid.parse(input, s -> s.equals("@") ? Cell.Paper : null);
        var removed = 0;
        while (true) {
            var additionalRemoved = 0;
            for (var cell : new HashSet<>(grid.cells().keySet())) {
                var neighbors = cell.neighbors8();
                if (neighbors.stream().filter(p -> grid.cells().getOrDefault(p, null) == Cell.Paper).count() < 4) {
                    additionalRemoved++;
                    grid.cells().remove(cell);
                }
            }
            if (additionalRemoved == 0) {
                break;
            }
            removed += additionalRemoved;
        }
        return removed;
    }

    @Test
    void example() {
        var s = """
                ..@@.@@@@.
                @@@.@.@.@@
                @@@@@.@.@@
                @.@@@@..@.
                @@.@@@@.@@
                .@@@@@@@.@
                .@.@.@.@@@
                @.@@@.@@@@
                .@@@@@@@@.
                @.@.@@@.@.
                """;
        assertEquals(13, solve1(s));
        assertEquals(43, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day04.txt"));
        assertEquals(1505, solve1(input));
        assertEquals(9182, solve2(input));
    }
}
