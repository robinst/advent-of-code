package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day14 {

    record Robot(Pos p, Pos v) {
    }

    static long solve1(String input, int width, int height) {
        var quadrantWidth = width / 2;
        var quadrantHeight = height / 2;
        var seconds = 100;
        var robots = Arrays.stream(input.split("\n")).map(Day14::parseRobot).toList();
        var perQuadrant = new HashMap<Integer, Long>();
        for (var robot : robots) {
            var p = robot.p();
            var v = robot.v();
            var x = Math.floorMod((p.x() + v.x() * seconds), width);
            var y = Math.floorMod((p.y() + v.y() * seconds), height);
            // Robots on the quadrant divider lines don't count
            if (x != quadrantWidth && y != quadrantHeight) {
                int qx = x < quadrantWidth ? 0 : 1;
                int qy = y < quadrantHeight ? 0 : 1;
                perQuadrant.merge(qx + qy * 2, 1L, Long::sum);
            }
        }
        return perQuadrant.values().stream().mapToLong(Long::valueOf).reduce(1L, (a, b) -> a * b);
    }

    static Robot parseRobot(String line) {
        var nums = Parsing.numbers(line);
        return new Robot(new Pos(nums.get(0), nums.get(1)), new Pos(nums.get(2), nums.get(3)));
    }

    static void solve2(String input, int width, int height) {
        var seconds = 100000;
        var robots = Arrays.stream(input.split("\n")).map(Day14::parseRobot).toList();
        var previousMaps = new HashSet<Map<Pos, String>>();
        for (int i = 0; i < seconds; i++) {
            var map = new HashMap<Pos, String>();
            for (var robot : robots) {
                var p = robot.p();
                var v = robot.v();
                int x = Math.floorMod((p.x() + (long) v.x() * i), width);
                int y = Math.floorMod((p.y() + (long) v.y() * i), height);
                map.put(new Pos(x, y), "#");
            }
            System.out.println("=".repeat(width));
            System.out.println("Seconds: " + (i));
            print(map, width, height);
            if (!previousMaps.add(map)) {
                System.out.println("Loop!");
                break;
            }
        }
    }

    static void print(Map<Pos, String> map, int maxX, int maxY) {
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                System.out.print(map.getOrDefault(new Pos(x, y), " "));
            }
            System.out.println();
        }
    }

    // mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass="aoc2024.Day14" > day14.txt
    // Find the Christmas tree by searching for "########"
//    public static void main(String[] args) {
//        var input = Resources.readString(Resources.class.getResource("/day14.txt"));
//        solve2(input, 101, 103);
//    }

    @Test
    void example() {
        var s = """
                p=0,4 v=3,-3
                p=6,3 v=-1,-3
                p=10,3 v=-1,2
                p=2,0 v=2,-1
                p=0,0 v=1,3
                p=3,0 v=-2,-2
                p=7,6 v=-1,-3
                p=3,0 v=-1,-2
                p=9,3 v=2,3
                p=7,3 v=-1,2
                p=2,4 v=2,-3
                p=9,5 v=-3,-3
                """;
        assertEquals(12, solve1(s, 11, 7));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day14.txt"));
        assertEquals(225810288L, solve1(input, 101, 103));
    }
}
