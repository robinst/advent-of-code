package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day04 {

    static long solve1(String input) {
        var grid = Grid.parse(input, s -> s);
        return grid.bounds().allPositions().mapToLong(p -> countXmas(grid, p)).sum();
    }

    static long countXmas(Grid<String> grid, Pos start) {
        return Arrays.stream(Direction8.values()).filter(d -> matches(grid, start, d, "XMAS")).count();
    }

    static boolean matches(Grid<String> grid, Pos start, Direction8 dir, String needle) {
        var line = start.line(dir.toPos(), needle.length());
        for (int i = 0; i < needle.length(); i++) {
            var s = needle.substring(i, i + 1);
            var pos = line.get(i);
            if (!grid.getOrDefault(pos, "").equals(s)) {
                return false;
            }
        }
        return true;
    }

    static String getText(Grid<String> grid, Pos start, Direction8 dir, int length) {
        return start.line(dir.toPos(), length).stream().map(p -> grid.getOrDefault(p, "")).collect(Collectors.joining());
    }

    static long solve2(String input) {
        var grid = Grid.parse(input, s -> s);
        return grid.bounds().allPositions().filter(p -> hasCross(grid, p)).count();
    }

    static boolean hasCross(Grid<String> grid, Pos start) {
        var dr = getText(grid, start, Direction8.DR, 3);
        var ur = getText(grid, start.plus(new Pos(0, 2)), Direction8.UR, 3);
        return (dr.equals("MAS") || dr.equals("SAM")) && (ur.equals("MAS") || ur.equals("SAM"));
    }

    @Test
    void example() {
        var s = """
                MMMSXXMASM
                MSAMXMSMSA
                AMXSXMAAMM
                MSAMASMSMX
                XMASAMXAMM
                XXAMMXXAMA
                SMSMSASXSS
                SAXAMASAAA
                MAMMMXMMMM
                MXMXAXMASX
                """;
        assertEquals(18, solve1(s));
        assertEquals(9, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day04.txt"));
        assertEquals(2567, solve1(input));
        assertEquals(2029, solve2(input));
    }
}
