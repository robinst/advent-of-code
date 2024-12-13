package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day12 {

    static long solve1(String input) {
        var grid = Grid.parse(input, s -> s);
        var done = new HashSet<>();
        var result = 0L;
        for (Map.Entry<Pos, String> entry : grid.cells().entrySet()) {
            var pos = entry.getKey();
            if (!done.contains(pos)) {
                var region = findRegion(grid, pos);
                done.addAll(region.positions);
                result += (long) region.positions.size() * region.perimeter();
            }
        }
        return result;
    }

    static Region findRegion(Grid<String> grid, Pos start) {
        var type = grid.getOrThrow(start);

        var positions = new HashSet<Pos>();
        int perimeter = 0;

        var directionSides = new HashMap<Direction, Set<Pos>>();

        var candidates = new ArrayDeque<Pos>();
        candidates.add(start);

        while (!candidates.isEmpty()) {
            var pos = candidates.removeFirst();
            positions.add(pos);
            for (Direction direction : Direction.values()) {
                var neighbor = pos.plus(direction.pos());

                if (Objects.equals(grid.getOrDefault(neighbor, null), type)) {
                    if (!positions.contains(neighbor) && !candidates.contains(neighbor)) {
                        candidates.add(neighbor);
                    }
                } else {
                    perimeter++;
                    directionSides.computeIfAbsent(direction, _ -> new HashSet<>()).add(neighbor);
                }
            }
        }

        int sides = 0;
        for (Map.Entry<Direction, Set<Pos>> entry : directionSides.entrySet()) {
            var direction = entry.getKey();
            var all = entry.getValue();
            var toCount = new HashSet<>(all);
            while (!toCount.isEmpty()) {
                sides++;
                var pos = toCount.iterator().next();
                toCount.remove(pos);
                var a = direction.turnLeft().pos();
                var b = direction.turnRight().pos();
                // Unlike the perimeter, we want to count a continuous line of fence as a single side,
                // so remove adjacent sides from counting.
                pos.stream(a).skip(1).takeWhile(toCount::contains).forEach(toCount::remove);
                pos.stream(b).skip(1).takeWhile(toCount::contains).forEach(toCount::remove);
            }
        }
        return new Region(positions, perimeter, sides);
    }

    record Region(Set<Pos> positions, int perimeter, int sides) {
    }

    static long solve2(String input) {
        var grid = Grid.parse(input, s -> s);
        var done = new HashSet<>();
        var result = 0L;
        for (Map.Entry<Pos, String> entry : grid.cells().entrySet()) {
            var pos = entry.getKey();
            if (!done.contains(pos)) {
                var region = findRegion(grid, pos);
                done.addAll(region.positions);
                result += (long) region.positions.size() * region.sides();
            }
        }
        return result;
    }

    @Test
    void small() {
        var s = """
                AAAA
                BBCD
                BBCC
                EEEC
                """;
        assertEquals(140, solve1(s));
        assertEquals(80, solve2(s));
    }

    @Test
    void example() {
        var s = """
                RRRRIICCFF
                RRRRIICCCF
                VVRRRCCFFF
                VVRCCCJFFF
                VVVVCJJCFE
                VVIVCCJJEE
                VVIIICJJEE
                MIIIIIJJEE
                MIIISIJEEE
                MMMISSJEEE
                """;
        assertEquals(1930, solve1(s));
        assertEquals(1206, solve2(s));
    }

    @Test
    void eShape() {
        var s = """
                EEEEE
                EXXXX
                EEEEE
                EXXXX
                EEEEE
                """;
        assertEquals(236, solve2(s));
    }

    @Test
    void tricky() {
        var s = """
                AAAAAA
                AAABBA
                AAABBA
                ABBAAA
                ABBAAA
                AAAAAA
                """;
        assertEquals(368, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day12.txt"));
        assertEquals(1461806, solve1(input));
        // Wrong: 888017, 903382
        assertEquals(887932, solve2(input));
    }

    void imports() {
        List<Integer> list = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        Set<Integer> set = new HashSet<>();
    }
}
