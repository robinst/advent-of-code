package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day20 {

    enum Cell {
        Wall,
        End
    }

    static long solve1(String input, long saveAtLeast) {
        var g = Grid.parseWithStart(input, s -> s.equals("S"), s -> switch (s) {
            case "#" -> Cell.Wall;
            case "E" -> Cell.End;
            default -> null;
        });

        var distances = distances(g.grid(), g.start());
        return cheats(g.grid(), distances, saveAtLeast);
    }

    record State(Pos pos, Long distance) {
    }

    static Map<Pos, Long> distances(Grid<Cell> grid, Pos start) {
        var queue = new PriorityQueue<>(Comparator.comparing(State::distance));
        queue.add(new State(start, 0L));
        var dist = new LinkedHashMap<Pos, Long>();
        dist.put(start, 0L);

        while (!queue.isEmpty()) {
            var state = queue.remove();
            var pos = state.pos();

            if (grid.getOrDefault(pos, null) == Cell.End) {
                break;
            }

            for (var next : pos.neighbors()) {
                var nextDist = state.distance() + 1;
                if (grid.getOrDefault(next, null) != Cell.Wall) {
                    if (nextDist < dist.getOrDefault(next, Long.MAX_VALUE)) {
                        queue.add(new State(next, nextDist));
                        dist.put(next, nextDist);
                    }
                }
            }
        }

        return dist;
    }

    static long cheats(Grid<Cell> grid, Map<Pos, Long> distances, long saveAtLeast) {
        var end = grid.cells().entrySet().stream().filter(s -> s.getValue() == Cell.End).findFirst().get().getKey();
        var fullDistance = distances.get(end);

        var result = 0L;
        for (var entry : distances.entrySet()) {
            var pos = entry.getKey();
            var distance = entry.getValue();

            for (var dir : Direction.values()) {
                var one = pos.plus(dir.pos());
                if (grid.getOrDefault(one, null) == Cell.Wall) {
                    var two = one.plus(dir.pos());
                    if (grid.getOrDefault(two, null) != Cell.Wall && grid.gridBounds().contains(two)) {
                        var cheatedDistance = distance + 2 + (fullDistance - distances.get(two));
                        if (cheatedDistance < fullDistance) {
                            var saved = fullDistance - cheatedDistance;
                            // System.out.println("Saved " + saved + " by cheating at " + pos + " direction " + dir);
                            if (saved >= saveAtLeast) {
                                result++;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    static long solve2(String input, long saveAtLeast) {
        var g = Grid.parseWithStart(input, s -> s.equals("S"), s -> switch (s) {
            case "#" -> Cell.Wall;
            case "E" -> Cell.End;
            default -> null;
        });

        var distances = distances(g.grid(), g.start());
        return cheats2(g.grid(), distances, saveAtLeast);
    }

    static long cheats2(Grid<Cell> grid, Map<Pos, Long> distances, long saveAtLeast) {
        var end = grid.cells().entrySet().stream().filter(s -> s.getValue() == Cell.End).findFirst().get().getKey();
        var fullDistance = distances.get(end);

        var result = 0L;
        for (var entry : distances.entrySet()) {
            var pos = entry.getKey();
            var distance = entry.getValue();

            var destinations = reachable(grid, pos, 20);
            for (var e : destinations.entrySet()) {
                var destination = e.getKey();
                var cheatDistance = e.getValue();
                var cheatedDistance = distance + cheatDistance + (fullDistance - distances.get(destination));
                if (cheatedDistance < fullDistance) {
                    var saved = fullDistance - cheatedDistance;
                    // System.out.println("Saved " + saved + " by cheating at " + pos);
                    if (saved >= saveAtLeast) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    static Map<Pos, Long> reachable(Grid<Cell> grid, Pos start, long maxSteps) {
        var result = new HashMap<Pos, Long>();
        var visited = new HashSet<Pos>();
        var check = new HashSet<>(start.neighbors());
        for (long i = 2; i <= maxSteps; i++) {
            var newCheck = new HashSet<Pos>();
            for (var pos : check) {
                visited.add(pos);
                for (var next : pos.neighbors()) {
                    if (!grid.gridBounds().contains(next)) {
                        continue;
                    }
                    if (grid.getOrDefault(next, null) != Cell.Wall) {
                        result.merge(next, i, Long::min);
                    }
                    if (!visited.contains(next)) {
                        newCheck.add(next);
                    }
                }
            }
            check = newCheck;
        }
        return result;
    }

    @Test
    void example() {
        var s = """
                ###############
                #...#...#.....#
                #.#.#.#.#.###.#
                #S#...#.#.#...#
                #######.#.#.###
                #######.#.#...#
                #######.#.###.#
                ###..E#...#...#
                ###.#######.###
                #...###...#...#
                #.#####.#.###.#
                #.#...#.#.#...#
                #.#.#.#.#.#.###
                #...#...#...###
                ###############
                """;
        assertEquals(44, solve1(s, 0));
        assertEquals(285, solve2(s, 50));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day20.txt"));
        assertEquals(1459L, solve1(input, 100));
        assertEquals(1016066L, solve2(input, 100));
    }
}
