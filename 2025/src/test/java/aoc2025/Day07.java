package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day07 {

    enum Cell {
        SPLITTER
    }

    static long solve1(String input) {
        var parsed = Grid.parseWithStart(input, s -> s.equals("S"), s -> s.equals("^") ? Cell.SPLITTER : null);
        var grid = parsed.grid();
        var start = parsed.start();

        var queue = new ArrayDeque<Pos>();
        queue.add(start);

        var visited = new HashSet<Pos>();

        var splits = new HashSet<Pos>();

        while (!queue.isEmpty()) {
            var pos = queue.pop();
            var nextPos = pos.plus(new Pos(0, 1));
            if (!grid.gridBounds().contains(nextPos) || !visited.add(nextPos)) {
                continue;
            }

            var nextCell = grid.cells().getOrDefault(nextPos, null);
            if (nextCell == Cell.SPLITTER) {
                splits.add(nextPos);
                queue.add(nextPos.plus(new Pos(-1, 0)));
                queue.add(nextPos.plus(new Pos(1, 0)));
            } else {
                queue.add(nextPos);
            }
        }

        return splits.size();
    }

    static long solve2(String input) {
        var parsed = Grid.parseWithStart(input, s -> s.equals("S"), s -> s.equals("^") ? Cell.SPLITTER : null);
        var grid = parsed.grid();
        var start = parsed.start();

        return timelines(new HashMap<>(), grid, start);
    }

    private static long timelines(Map<Pos, Long> cache, Grid<Cell> grid, Pos pos) {
        var nextPos = pos.plus(new Pos(0, 1));
        if (!grid.gridBounds().contains(nextPos)) {
            return 1;
        }

        var result = cache.get(nextPos);
        if (result != null) {
            return result;
        }

        var nextCell = grid.cells().getOrDefault(nextPos, null);
        if (nextCell == Cell.SPLITTER) {
            var left = nextPos.plus(new Pos(-1, 0));
            var right = nextPos.plus(new Pos(1, 0));
            result = timelines(cache, grid, right) + timelines(cache, grid, left);
        } else {
            result = timelines(cache, grid, nextPos);
        }

        cache.put(pos, result);
        return result;
    }

    @Test
    void example() {
        var s = """
                .......S.......
                ...............
                .......^.......
                ...............
                ......^.^......
                ...............
                .....^.^.^.....
                ...............
                ....^.^...^....
                ...............
                ...^.^...^.^...
                ...............
                ..^...^.....^..
                ...............
                .^.^.^.^.^...^.
                ...............
                """;
        assertEquals(21, solve1(s));
        assertEquals(40, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day07.txt"));
        assertEquals(1613, solve1(input));
        assertEquals(48021610271997L, solve2(input));
    }
}
