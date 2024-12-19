package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day19 {

    static long solve1(String input) {
        var parts = input.split("\n\n");
        var patterns = List.of(parts[0].split(", "));
        var designs = List.of(parts[1].split("\n"));
        return designs.stream().filter(d -> possible(d, patterns)).count();
    }

    static boolean possible(String design, List<String> patterns) {
        if (design.isEmpty()) {
            return true;
        }
        for (var pattern : patterns) {
            if (design.startsWith(pattern)) {
                if (possible(design.substring(pattern.length()), patterns)) {
                    return true;
                }
            }
        }
        return false;
    }

    static class Trie {
        Map<Character, Trie> map = new HashMap<Character, Trie>();
        boolean isWord = false;
    }

    static long solve2(String input) {
        var parts = input.split("\n\n");
        var patterns = List.of(parts[0].split(", "));
        var designs = List.of(parts[1].split("\n"));

        // This wasn't necessary (only memoization), but might come in handy some other time
        var root = new Trie();
        for (var pattern : patterns) {
            var trie = root;
            for (int i = 0; i < pattern.length(); i++) {
                var c = pattern.charAt(i);
                trie = trie.map.computeIfAbsent(c, _ -> new Trie());
            }
            trie.isWord = true;
        }

        var result = 0L;
        var i = 0;
        var mem = new HashMap<String, Long>();
        for (var design : designs) {
            long possibilities = possibilities(design, root, mem);
            result += possibilities;
            System.out.println("Did design " + i + ": " + possibilities);
            i++;
        }
        return result;
    }

    static long possibilities(String design, Trie root, Map<String, Long> mem) {
        if (design.isEmpty()) {
            return 1;
        }
        var result = mem.get(design);
        if (result != null) {
            return result;
        }
        var possible = 0L;
        var trie = root;
        for (int i = 0; i < design.length(); i++) {
            var c = design.charAt(i);
            trie = trie.map.get(c);
            if (trie == null) {
                break;
            }
            if (trie.isWord) {
                possible += possibilities(design.substring(i + 1), root, mem);
            }
        }
        mem.put(design, possible);
        return possible;
    }

    @Test
    void example() {
        var s = """
                r, wr, b, g, bwu, rb, gb, br

                brwrr
                bggr
                gbbr
                rrbgbr
                ubwu
                bwurrg
                brgr
                bbrgwb
                """;
        assertEquals(6, solve1(s));
        assertEquals(16, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day19.txt"));
        assertEquals(319, solve1(input));
        assertEquals(692575723305545L, solve2(input));
    }
}
