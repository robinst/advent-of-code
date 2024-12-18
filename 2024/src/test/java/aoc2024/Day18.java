package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day18 {

    record State(Pos pos, int distance) {
    }

    static long solve1(String input, int bytes, int maxX, int maxY) {
        var positions = parseInput(input);
        var corrupted = new HashSet<>(positions.subList(0, bytes));

        var state = getShortestPath(corrupted, maxX, maxY);
        assert state != null;
        return state;
    }

    static List<Pos> parseInput(String input) {
        return input.lines().map(l -> {
            var nums = Parsing.numbers(l);
            return new Pos(nums.get(0), nums.get(1));
        }).toList();
    }

    static Integer getShortestPath(HashSet<Pos> corrupted, int maxX, int maxY) {
        var start = new Pos(0, 0);
        var end = new Pos(maxX, maxY);
        var bounds = new PosBounds(0, maxX, 0, maxY);

        var queue = new PriorityQueue<>(Comparator.comparing(State::distance));
        queue.add(new State(start, 0));
        var dist = new HashMap<Pos, Integer>();
        dist.put(start, 0);

        while (!queue.isEmpty()) {
            var state = queue.remove();
            var pos = state.pos();

            if (pos.equals(end)) {
                return state.distance();
            }

            for (var next : pos.neighbors()) {
                if (corrupted.contains(next) || !bounds.contains(next)) {
                    continue;
                }
                var nextDist = state.distance() + 1;
                if (nextDist < dist.getOrDefault(next, Integer.MAX_VALUE)) {
                    dist.put(next, nextDist);
                    queue.add(new State(next, nextDist));
                }
            }
        }
        return null;
    }

    static String solve2(String input, int maxX, int maxY) {
        var positions = parseInput(input);

        // Binary search for change from reachable -> blocked
        var lower = 0;
        var upper = positions.size();
        while (lower < upper) {
            var middle = (lower + upper) / 2;
            var corrupted = new HashSet<>(positions.subList(0, middle));
            if (getShortestPath(corrupted, maxX, maxY) != null) {
                // Reachable, search upward
                lower = middle + 1;
            } else {
                // Blocked, search downward
                upper = middle;
            }
        }

        var pos = positions.get(upper - 1);
        return pos.x() + "," + pos.y();
    }

    @Test
    void example() {
        var s = """
                5,4
                4,2
                4,5
                3,0
                2,1
                6,3
                2,4
                1,5
                0,6
                3,3
                2,6
                5,1
                1,2
                5,5
                2,5
                6,5
                1,4
                0,4
                6,4
                1,1
                6,1
                1,0
                0,5
                1,6
                2,0
                """;
        assertEquals(22, solve1(s, 12, 6, 6));
        assertEquals("6,1", solve2(s, 6, 6));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day18.txt"));
        assertEquals(330, solve1(input, 1024, 70, 70));
        assertEquals("10,38", solve2(input, 70, 70));
    }
}
