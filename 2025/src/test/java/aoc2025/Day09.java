package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day09 {

    static long solve1(String input) {
        var reds = input.lines().map(l -> {
            var nums = Parsing.numbers(l);
            return new Pos(nums.get(0), nums.get(1));
        }).toList();

        var largest = 0L;
        for (var i = 0; i < reds.size(); i++) {
            for (var j = i + 1; j < reds.size(); j++) {
                var a = reds.get(i);
                var b = reds.get(j);
                var rect = PosBounds.of(a, b);
                largest = Math.max(largest, rect.size());
            }
        }
        return largest;
    }

    static long solve2(String input) {
        var reds = input.lines().map(l -> {
            var nums = Parsing.numbers(l);
            return new Pos(nums.get(0), nums.get(1));
        }).toList();

        var largest = 0L;
        for (var i = 0; i < reds.size(); i++) {
            for (var j = i + 1; j < reds.size(); j++) {
                var a = reds.get(i);
                var b = reds.get(j);
                if (a.x() == b.x() || a.y() == b.y()) {
                    // Don't bother with lines
                    continue;
                }
                var rect = PosBounds.of(a, b);
                var area = rect.size();
                if (area > largest) {
                    // Inside of rect (without border); the lines between reds can be on the border but not go through.
                    var inside = rect.shrink(1);
                    if (isInside(inside, reds)) {
                        largest = area;
                    }
                }
            }
        }
        return largest;
    }

    private static boolean isInside(PosBounds rect, List<Pos> reds) {
        for (var i = 0; i < reds.size(); i++) {
            var a = reds.get(i);
            var b = reds.get((i + 1) % reds.size());
            var line = PosBounds.of(a, b);

            // If any of the outside lines goes through our rect (not just on the border of it), that means part of the
            // rect is outside (not on red or green).
            if (line.intersects(rect)) {
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
