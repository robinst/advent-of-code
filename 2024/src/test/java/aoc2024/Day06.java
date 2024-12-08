package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day06 {

    enum Cell {
        OBSTACLE,
        START
    }

    static long solve1(String input) {
        // TODO: Need a parse variant that can also return a start pos
        var grid = Grid.parse(input, s -> s.equals("^") ? Cell.START : s.equals("#") ? Cell.OBSTACLE : null);
        var startPos = grid.cells().entrySet().stream().filter(e -> e.getValue() == Cell.START).findFirst().get().getKey();
        var direction = Direction.UP;
        grid.cells().remove(startPos);
        return calculateRoute(grid, startPos, direction).size();
    }

    static Set<Pos> calculateRoute(Grid<Cell> grid, Pos startPos, Direction direction) {
        var visited = new HashSet<Pos>();
        var pos = startPos;
        visited.add(pos);
        while (true) {
            var next = pos.plus(direction.pos());
            if (!grid.cellBounds().contains(next)) {
                break;
            }
            if (grid.getOrDefault(next, null) == Cell.OBSTACLE) {
                direction = direction.turnRight();
                continue;
            }

            pos = next;
            visited.add(pos);
        }
        return visited;
    }

    static long solve2(String input) {
        var grid = Grid.parse(input, s -> s.equals("^") ? Cell.START : s.equals("#") ? Cell.OBSTACLE : null);
        var startPos = grid.cells().entrySet().stream().filter(e -> e.getValue() == Cell.START).findFirst().get().getKey();

        var route = calculateRoute(grid, startPos, Direction.UP);
        var result = 0L;
        for (var pos : route) {
            if (wouldLoop(grid, startPos, Direction.UP, pos)) {
                result++;
            }
        }
        return result;
    }

    static boolean wouldLoop(Grid<Cell> grid, Pos startPos, Direction direction, Pos obstruction) {
        // TODO: Can we unify this and calculateRoute?
        var bounds = grid.cellBounds();
        var visited = new HashMap<Pos, Set<Direction>>();
        var pos = startPos;
        visited.computeIfAbsent(pos, _ -> new HashSet<>()).add(direction);
        while (true) {
            var next = pos.plus(direction.pos());
            if (!bounds.contains(next)) {
                break;
            }
            if (next.equals(obstruction) || grid.getOrDefault(next, null) == Cell.OBSTACLE) {
                direction = direction.turnRight();
                continue;
            }

            if (visited.containsKey(next) && visited.get(next).contains(direction)) {
                return true;
            }

            pos = next;
            visited.computeIfAbsent(pos, _ -> new HashSet<>()).add(direction);
        }
        return false;
    }

    @Test
    void example() {
        var s = """
                ....#.....
                .........#
                ..........
                ..#.......
                .......#..
                ..........
                .#..^.....
                ........#.
                #.........
                ......#...
                """;
        assertEquals(41, solve1(s));
        assertEquals(6, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day06.txt"));
        assertEquals(5331, solve1(input));
        assertEquals(1812, solve2(input));
    }
}
