package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day08 {

    record Connection(Pos3 from, Pos3 to, long distance) {
    }

    record Circuit(Set<Pos3> boxes) {
    }

    static long solve1(String input, int times) {
        var boxes = new ArrayList<Pos3>();
        input.lines().forEach(line -> {
            var nums = Parsing.numbers(line);
            boxes.add(new Pos3(nums.get(0), nums.get(1), nums.get(2)));
        });

        var distances = new ArrayList<Connection>();
        for (var i = 0; i < boxes.size(); i++) {
            var from = boxes.get(i);
            for (var j = i + 1; j < boxes.size(); j++) {
                var to = boxes.get(j);
                var distance = from.distanceStraightLine(to);
                distances.add(new Connection(from, to, distance));
            }
        }

        distances.sort(Comparator.comparingLong(Connection::distance));

        var circuits = new HashMap<Pos3, Circuit>();

        for (var connection : distances.subList(0, times)) {
            var circuitA = circuits.get(connection.from);
            var circuitB = circuits.get(connection.to);
            if (circuitA == null && circuitB == null) {
                var circuit = new Circuit(new HashSet<>(List.of(connection.from, connection.to)));
                circuits.put(connection.from, circuit);
                circuits.put(connection.to, circuit);
            } else if (circuitA != null && circuitB != null) {
                // Merge
                circuitA.boxes.addAll(circuitB.boxes);
                circuitB.boxes.forEach(b -> circuits.put(b, circuitA));
            } else {
                // Only one exists
                var circuit = circuitA != null ? circuitA : circuitB;
                circuit.boxes.addAll(List.of(connection.from, connection.to));
                circuits.put(connection.from, circuit);
                circuits.put(connection.to, circuit);
            }
        }

        var sortedCircuits = circuits.values().stream().distinct().sorted(Comparator.comparingInt(c -> -c.boxes.size())).toList();
        return sortedCircuits.stream().mapToLong(c -> c.boxes.size()).limit(3).reduce(1, (a, b) -> a * b);
    }

    static long solve2(String input) {
        var boxes = new ArrayList<Pos3>();
        input.lines().forEach(line -> {
            var nums = Parsing.numbers(line);
            boxes.add(new Pos3(nums.get(0), nums.get(1), nums.get(2)));
        });

        var distances = new ArrayList<Connection>();
        for (var i = 0; i < boxes.size(); i++) {
            var from = boxes.get(i);
            for (var j = i + 1; j < boxes.size(); j++) {
                var to = boxes.get(j);
                var distance = from.distanceStraightLine(to);
                distances.add(new Connection(from, to, distance));
            }
        }

        distances.sort(Comparator.comparingLong(Connection::distance));

        var circuits = new HashMap<Pos3, Circuit>();

        for (var connection : distances) {
            var circuitA = circuits.get(connection.from);
            var circuitB = circuits.get(connection.to);
            if (circuitA == null && circuitB == null) {
                var circuit = new Circuit(new HashSet<>(List.of(connection.from, connection.to)));
                circuits.put(connection.from, circuit);
                circuits.put(connection.to, circuit);
            } else if (circuitA != null && circuitB != null) {
                // Merge
                circuitA.boxes.addAll(circuitB.boxes);
                circuitB.boxes.forEach(b -> circuits.put(b, circuitA));

                if (circuitA.boxes.size() == boxes.size()) {
                    return (long) connection.from.x() * connection.to.x();
                }
            } else {
                // Only one exists
                var circuit = circuitA != null ? circuitA : circuitB;
                circuit.boxes.addAll(List.of(connection.from, connection.to));
                circuits.put(connection.from, circuit);
                circuits.put(connection.to, circuit);

                if (circuit.boxes.size() == boxes.size()) {
                    return (long) connection.from.x() * connection.to.x();
                }
            }
        }
        return 0;
    }

    @Test
    void example() {
        var s = """
                162,817,812
                57,618,57
                906,360,560
                592,479,940
                352,342,300
                466,668,158
                542,29,236
                431,825,988
                739,650,466
                52,470,668
                216,146,977
                819,987,18
                117,168,530
                805,96,715
                346,949,466
                970,615,88
                941,993,340
                862,61,35
                984,92,344
                425,690,689
                """;
        assertEquals(40, solve1(s, 10));
        assertEquals(25272, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day08.txt"));
        assertEquals(123420, solve1(input, 1000));
        assertEquals(673096646, solve2(input));
    }
}
