package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day10 {

    static long solve1(String input) {
        var grid = Grid.parse(input, s -> !s.equals(".") ? Integer.parseInt(s) : null);
        var trailheads = grid.cells().entrySet().stream().filter(e -> e.getValue() == 0).toList();
        var result = 0L;
        for (var trailhead : trailheads) {
            var pos = trailhead.getKey();
            var targets = new HashSet<Pos>();
            distinctDestinations(grid, pos, targets);
            result += targets.size();
        }
        return result;
    }

    static void distinctDestinations(Grid<Integer> grid, Pos pos, Set<Pos> destinations) {
        var height = grid.getOrThrow(pos);
        if (height == 9) {
            destinations.add(pos);
            return;
        }
        for (var neighbor : pos.neighbors()) {
            if (grid.getOrDefault(neighbor, -1) == height + 1) {
                distinctDestinations(grid, neighbor, destinations);
            }
        }
    }

    static long solve2(String input) {
        var grid = Grid.parse(input, s -> !s.equals(".") ? Integer.parseInt(s) : null);
        var trailheads = grid.cells().entrySet().stream().filter(e -> e.getValue() == 0).toList();
        var result = 0L;
        for (var trailhead : trailheads) {
            var pos = trailhead.getKey();
            result += distinctTrails(grid, pos);
        }
        return result;
    }

    static long distinctTrails(Grid<Integer> grid, Pos start) {
        var height = grid.getOrThrow(start);
        if (height == 9) {
            return 1;
        }
        var result = 0L;
        for (var neighbor : start.neighbors()) {
            if (grid.getOrDefault(neighbor, -1) == height + 1) {
                result += distinctTrails(grid, neighbor);
            }
        }
        return result;
    }

    @Test
    void simple() {
        var s = """
                ...0...
                ...1...
                ...2...
                6543456
                7.....7
                8.....8
                9.....9
                """;
        assertEquals(2, solve1(s));
    }

    @Test
    void example() {
        var s = """
                89010123
                78121874
                87430965
                96549874
                45678903
                32019012
                01329801
                10456732
                """;
        assertEquals(36, solve1(s));
        assertEquals(81, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day10.txt"));
        assertEquals(776, solve1(input));
        assertEquals(1657, solve2(input));
    }
}
