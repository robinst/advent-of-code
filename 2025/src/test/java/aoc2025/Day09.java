package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day09 {

    static long solve1(String input) {
        var positions = input.lines().map(l -> {
            var nums = Parsing.numbers(l);
            return new Pos(nums.get(0), nums.get(1));
        }).toList();

        var largest = 0L;
        for (var i = 0; i < positions.size(); i++) {
            for (var j = i + 1; j < positions.size(); j++) {
                var a = positions.get(i);
                var b = positions.get(j);
                var xDiff = (long) Math.abs(a.x() - b.x()) + 1;
                var yDiff = (long) Math.abs(a.y() - b.y()) + 1;
                largest = Math.max(largest, xDiff * yDiff);
            }
        }
        return largest;
    }

    static long solve2(String input) {
        var reds = input.lines().map(l -> {
            var nums = Parsing.numbers(l);
            return new Pos(nums.get(0), nums.get(1));
        }).toList();

        // TODO: Improve on this by getting all rectangles (part 1), sorting by size and then find the first that's inside
        var largest = 0L;
        for (var i = 0; i < reds.size(); i++) {
            for (var j = i + 1; j < reds.size(); j++) {
                var a = reds.get(i);
                var b = reds.get(j);
                var xDiff = (long) Math.abs(a.x() - b.x()) + 1;
                var yDiff = (long) Math.abs(a.y() - b.y()) + 1;
                var area = xDiff * yDiff;
                if (area > largest) {
                    // Check if inside
                    // TODO: Rect method
                    // TODO: PosBounds shrink method?
                    // Inside of rect (without border); the lines between reds can be on the border but not go through.
                    var rect = new PosBounds(Math.min(a.x(), b.x()) + 1, Math.max(a.x(), b.x()) - 1, Math.min(a.y(), b.y()) + 1, Math.max(a.y(), b.y()) - 1);
                    if (isInside(rect, reds)) {
                        largest = area;
                    }
                }
            }
            System.out.println(i);
        }
        return largest;
    }

    private static boolean isInside(PosBounds rect, List<Pos> reds) {
        for (var i = 0; i < reds.size(); i++) {
            var from = reds.get(i);
            var to = reds.get((i + 1) % reds.size());

            // If any of the outside lines goes through our rect (not just on the border of it), that means part of the
            // rect is outside (not on red or green).
            if (from.straightLineToIncludingStream(to).anyMatch(rect::contains)) {
                return false;
            }
        }
        return true;
    }

    @Test
    void example() {
        var s = """
                7,1
                11,1
                11,7
                9,7
                9,5
                2,5
                2,3
                7,3
                """;
        assertEquals(50, solve1(s));
        assertEquals(24, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day09.txt"));
        assertEquals(4774877510L, solve1(input));
        assertEquals(1560475800L, solve2(input));
    }
}
