package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day05 {

    record Rule(int a, int b) {
    }

    static long solve1(String input) {
        var parts = input.split("\n\n");
        var rulesByFirst = parseRules(parts[0]).stream().collect(Collectors.groupingBy(r -> r.a));
        var updates = parseUpdates(parts[1]);
        return updates.stream().filter(u -> isValid(u, rulesByFirst)).mapToInt(l -> l.get(l.size() / 2)).sum();
    }

    static List<Rule> parseRules(String s) {
        return Arrays.stream(s.split("\n")).map(Day05::parseRule).toList();
    }

    static Rule parseRule(String s) {
        var nums = Parsing.numbers(s);
        return new Rule(nums.get(0), nums.get(1));
    }

    static List<List<Integer>> parseUpdates(String s) {
        return Arrays.stream(s.split("\n")).map(Parsing::numbers).toList();
    }

    static boolean isValid(List<Integer> update, Map<Integer, List<Rule>> rules) {
        var indexes = new HashMap<Integer, Integer>();
        for (int i = 0; i < update.size(); i++) {
            indexes.put(update.get(i), i);
        }

        for (int i = 0; i < update.size(); i++) {
            var num = update.get(i);
            for (var rule : rules.getOrDefault(num, List.of())) {
                if (indexes.getOrDefault(rule.b, Integer.MAX_VALUE) < i) {
                    return false;
                }
            }
        }
        return true;
    }

    static long solve2(String input) {
        var parts = input.split("\n\n");
        var rules = parseRules(parts[0]);
        var rulesByFirst = rules.stream().collect(Collectors.groupingBy(r -> r.a));
        var updates = parseUpdates(parts[1]);

        var invalid = updates.stream()
                .filter(u -> !isValid(u, rulesByFirst))
                .toList();
        var result = 0L;
        for (var u : invalid) {
            makeValid(u, rulesByFirst);
            result += u.get(u.size() / 2);
        }
        return result;
    }

    static void makeValid(List<Integer> update, Map<Integer, List<Rule>> rulesByFirst) {
        update.sort((a, b) -> {
            if (a.equals(b)) {
                return 0;
            }
            return rulesByFirst.getOrDefault(a, List.of()).stream().anyMatch(r -> r.b == b) ? -1 : 1;
        });
    }

    static String mermaid(String input) {
        var rules = parseRules(input.split("\n\n")[0]);
        var mermaid = Mermaid.builder();
        for (var rule : rules) {
            mermaid.edge(String.valueOf(rule.a), String.valueOf(rule.b));
        }
        return mermaid.build();
    }

    @Test
    void example() {
        var s = """
                47|53
                97|13
                97|61
                97|47
                75|29
                61|13
                75|53
                29|13
                97|29
                53|29
                61|53
                97|53
                61|29
                47|13
                75|47
                97|75
                47|61
                75|61
                47|29
                75|13
                53|13
                
                75,47,61,53,29
                97,61,53,29,13
                75,29,13
                75,97,47,61,53
                61,13,29
                97,13,75,29,47
                """;
        System.out.println(mermaid(s));
        assertEquals(143, solve1(s));
        assertEquals(123, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day05.txt"));
        assertEquals(4766, solve1(input));
        assertEquals(6257, solve2(input));
    }
}
