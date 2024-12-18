package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day16 {

    enum Cell {
        Wall,
        End
    }

    record PosDir(Pos pos, Direction direction) {
    }

    record State(Pos pos, Direction direction, long score, State previous) {
    }

    record Solution(long part1, long part2) {
    }

    static Solution solve(String input) {
        var g = Grid.parseWithStart(input, s -> s.equals("S"), s -> switch (s) {
            case "#" -> Cell.Wall;
            case "E" -> Cell.End;
            default -> null;
        });
        var states = shortestPaths(g.grid(), g.start());

        var positions = new HashSet<Pos>();
        for (var state : states) {
            while (state != null) {
                positions.add(state.pos());
                state = state.previous();
            }
        }
        return new Solution(states.getFirst().score(), positions.size());
    }

    /// Dijkstra with multiple paths
    static List<State> shortestPaths(Grid<Cell> grid, Pos start) {
        var queue = new PriorityQueue<>(Comparator.comparing(State::score));
        queue.add(new State(start, Direction.RIGHT, 0L, null));
        var dist = new HashMap<PosDir, Long>();
        dist.put(new PosDir(start, Direction.RIGHT), 0L);

        var shortest = new ArrayList<State>();
        var lowestScore = Long.MAX_VALUE;

        while (!queue.isEmpty()) {
            var state = queue.remove();
            if (state.score() > lowestScore) {
                break;
            }
            var pos = state.pos();
            var dir = state.direction();

            if (grid.getOrDefault(pos, null) == Cell.End) {
                shortest.add(state);
                lowestScore = state.score();
                continue;
            }

            var neighbors = List.of(new State(pos.plus(dir.pos()), dir, state.score() + 1, state),
                    new State(pos.plus(dir.turnLeft().pos()), dir.turnLeft(), state.score() + 1001, state),
                    new State(pos.plus(dir.turnRight().pos()), dir.turnRight(), state.score() + 1001, state));
            for (var neighbor : neighbors) {
                if (grid.getOrDefault(neighbor.pos(), null) == Cell.Wall) {
                    continue;
                }
                var key = new PosDir(neighbor.pos(), neighbor.direction());
                if (neighbor.score() <= dist.getOrDefault(key, Long.MAX_VALUE)) {
                    dist.put(key, neighbor.score());
                    queue.add(neighbor);
                }
            }
        }

        return shortest;
    }

    @Test
    void example1() {
        var s = """
                ###############
                #.......#....E#
                #.#.###.#.###.#
                #.....#.#...#.#
                #.###.#####.#.#
                #.#.#.......#.#
                #.#.#####.###.#
                #...........#.#
                ###.#.#####.#.#
                #...#.....#.#.#
                #.#.#.###.#.#.#
                #.....#...#.#.#
                #.###.#.#.#.#.#
                #S..#.....#...#
                ###############
                """;
        var solution = solve(s);
        assertEquals(7036, solution.part1());
        assertEquals(45, solution.part2());
    }

    @Test
    void example2() {
        var s = """
                #################
                #...#...#...#..E#
                #.#.#.#.#.#.#.#.#
                #.#.#.#...#...#.#
                #.#.#.#.###.#.#.#
                #...#.#.#.....#.#
                #.#.#.#.#.#####.#
                #.#...#.#.#.....#
                #.#.#####.#.###.#
                #.#.#.......#...#
                #.#.###.#####.###
                #.#.#...#.....#.#
                #.#.#.#####.###.#
                #.#.#.........#.#
                #.#.#.#########.#
                #S#.............#
                #################
                """;
        var solution = solve(s);
        assertEquals(11048, solution.part1());
        assertEquals(64, solution.part2());
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day16.txt"));
        var solution = solve(input);
        assertEquals(73432L, solution.part1());
        assertEquals(496L, solution.part2());
    }
}
