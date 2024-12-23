package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day23 {

    static long solve1(String input) {
        var map = parse(input);
        var sets = new HashSet<Set<String>>();

        for (var entry : map.entrySet()) {
            var a = entry.getKey();
            for (var b : entry.getValue()) {
                for (var c : map.get(b)) {
                    if (map.get(c).contains(a)) {
                        sets.add(Set.of(a, b, c));
                    }
                }
            }
        }

        return sets.stream().filter(s -> s.stream().anyMatch(c -> c.startsWith("t"))).count();
    }

    static Map<String, Set<String>> parse(String input) {
        var lines = input.split("\n");
        var map = new HashMap<String, Set<String>>();
        for (var line : lines) {
            var parts = line.split("-");
            var a = parts[0];
            var b = parts[1];
            map.computeIfAbsent(a, _ -> new HashSet<>()).add(b);
            map.computeIfAbsent(b, _ -> new HashSet<>()).add(a);
        }
        return map;
    }

    static String solve2(String input) {
        var map = parse(input);

        var largest = Set.<String>of();
        for (var entry : map.entrySet()) {
            var a = entry.getKey();
            var values = entry.getValue();
            if (values.size() <= largest.size()) {
                continue;
            }

            for (var b : values) {
                var set = new HashSet<String>();
                set.add(a);
                set.add(b);
                values.forEach(v -> {
                    set.retainAll(map.get(v));
                    set.add(v);
                });
                if (set.size() > largest.size()) {
                    largest = set;
                }
            }
        }
        return largest.stream().sorted().collect(Collectors.joining(","));
    }

    static void drawGraph(String input) {
        var g = Graphviz.undirected();

        var lines = input.split("\n");
        for (var line : lines) {
            var parts = line.split("-");
            var a = parts[0];
            var b = parts[1];
            g.edge(a, b);
        }
        System.out.println(g.build());
    }

    @Test
    void example() {
        var s = """
                kh-tc
                qp-kh
                de-cg
                ka-co
                yn-aq
                qp-ub
                cg-tb
                vc-aq
                tb-ka
                wh-tc
                yn-cg
                kh-ub
                ta-co
                de-co
                tc-td
                tb-wq
                wh-td
                ta-ka
                td-qp
                aq-cg
                wq-ub
                ub-vc
                de-ta
                wq-aq
                wq-vc
                wh-yn
                ka-de
                kh-ta
                co-tc
                wh-qp
                tb-vc
                td-yn
                """;
        assertEquals(7, solve1(s));
        assertEquals("co,de,ka,ta", solve2(s));
        drawGraph(s);
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day23.txt"));
        assertEquals(1156, solve1(input));
        assertEquals("bx,cx,dr,dx,is,jg,km,kt,li,lt,nh,uf,um", solve2(input));
    }
}
