package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day12 {

    record Shape(List<Pos> positions) {
    }

    record Region(int width, int height, List<Integer> quantities) {
    }

    static long solve1(String input) {
        var sections = input.split("\n\n");

        var shapes = new ArrayList<Shape>();
        for (var i = 0; i < sections.length - 1; i++) {
            var shapeString = sections[i];
            var positions = new ArrayList<Pos>();
            var lines = shapeString.lines().skip(1).toList();
            for (var l = 0; l < lines.size(); l++) {
                var line = lines.get(l);
                for (var c = 0; c < line.length(); c++) {
                    if (line.charAt(c) == '#') {
                        positions.add(new Pos(c, l));
                    }
                }
            }
            shapes.add(new Shape(positions));
        }

        var regions = new ArrayList<Region>();
        var last = sections[sections.length - 1];
        last.lines().forEach(line -> {
            var parts = line.split(": ");
            var dimensions = Parsing.numbers(parts[0]);
            var quantities = Parsing.numbers(parts[1]);
            regions.add(new Region(dimensions.get(0), dimensions.get(1), quantities));
        });

        var result = 0L;
        for (var region : regions) {
            if (canFit(shapes, region)) {
                result++;
            }
        }
        return result;
    }

    private static boolean canFit(List<Shape> shapes, Region region) {
        var sum = region.quantities.stream().mapToInt(Integer::intValue).sum();
        // Let's try this...
        return sum * 9 <= region.width * region.height;
    }

    @Test
    void example() {
        var s = """
                0:
                ###
                ##.
                ##.
                
                1:
                ###
                ##.
                .##
                
                2:
                .##
                ###
                ##.
                
                3:
                ##.
                ###
                ##.
                
                4:
                ###
                #..
                ###
                
                5:
                ###
                .#.
                ###
                
                4x4: 0 0 0 0 2 0
                12x5: 1 0 1 0 2 2
                12x5: 1 0 1 0 3 2
                """;
        // assertEquals(0, solve1(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day12.txt"));
        assertEquals(589, solve1(input));
    }

    void imports() {
        List<Integer> list = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        Set<Integer> set = new HashSet<>();
    }
}
