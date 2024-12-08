package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day08 {

    static long solve1(String input) {
        var grid = Grid.parse(input, s -> !s.equals(".") ? s : null);
        var groups = grid.cells().entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue));
        var locations = new HashSet<Pos>();
        for (var group : groups.values()) {
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    var a = group.get(i).getKey();
                    var b = group.get(j).getKey();
                    var diff = b.minus(a);
                    locations.add(b.plus(diff));
                    locations.add(a.minus(diff));
                }
            }
        }
        return locations.stream().filter(p -> grid.gridBounds().contains(p)).count();
    }

    static long solve2(String input) {
        var grid = Grid.parse(input, s -> !s.equals(".") ? s : null);
        var groups = grid.cells().entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue));
        var locations = new HashSet<Pos>();
        for (var group : groups.values()) {
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    var a = group.get(i).getKey();
                    var b = group.get(j).getKey();
                    var diff = b.minus(a);
                    addWithinBounds(b, diff, grid.gridBounds(), locations);
                    addWithinBounds(a, diff.negate(), grid.gridBounds(), locations);
                }
            }
        }
        return locations.size();
    }

    static void addWithinBounds(Pos start, Pos step, PosBounds bounds, Set<Pos> set) {
        start.stream(step).takeWhile(bounds::contains).forEach(set::add);
    }

    @Test
    void example() {
        var s = """
                ............
                ........0...
                .....0......
                .......0....
                ....0.......
                ......A.....
                ............
                ............
                ........A...
                .........A..
                ............
                ............
                """;
        assertEquals(14, solve1(s));
        assertEquals(34, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day08.txt"));
        assertEquals(308, solve1(input));
        assertEquals(1147, solve2(input));
    }
}
