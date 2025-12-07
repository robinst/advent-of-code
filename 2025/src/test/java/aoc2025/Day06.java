package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day06 {

    enum Op {
        PLUS,
        TIMES
    }

    static long solve1(String input) {
        var lines = new ArrayList<>(input.trim().lines().toList());
        var operations = lines.removeLast();
        var ops = Arrays.stream(operations.split("\\s+")).map(s -> switch (s) {
            case "*" -> Op.TIMES;
            case "+" -> Op.PLUS;
            default -> throw new IllegalArgumentException();
        }).toList();

        var results = new ArrayList<>(parseNumbers(lines.removeFirst()).toList());

        for (var line : lines) {
            var nums = parseNumbers(line).toList();
            for (var i = 0; i < ops.size(); i++) {
                var op = ops.get(i);
                var result = results.get(i);
                var num = nums.get(i);
                result = switch (op) {
                    case PLUS -> result + num;
                    case TIMES -> result * num;
                };
                results.set(i, result);
            }
        }

        return results.stream().mapToLong(Long::valueOf).sum();
    }

    private static Stream<Long> parseNumbers(String line) {
        return Arrays.stream(line.trim().split("\\s+")).map(Long::parseLong);
    }

    static long solve2(String input) {
        var lines = new ArrayList<>(input.lines().toList());

        var sum = 0L;

        var result = 0L;
        Op currentOp = null;
        Op previousOp = null;

        var lineOps = lines.getLast();
        for (int col = 0; col < lineOps.length(); col++) {
            var opChar = lineOps.charAt(col);
            var op = switch (opChar) {
                case '*' -> Op.TIMES;
                case '+' -> Op.PLUS;
                case ' ' -> null;
                default -> throw new IllegalArgumentException();
            };

            if (op != null) {
                currentOp = op;
            }

            if (op != null && previousOp == null) {
                sum += result;
                result = 0;
            }

            var num = parseNumberFromColumn(col, lines);
            if (num != 0) {
                if (result == 0) {
                    result = num;
                } else {
                    result = switch (currentOp) {
                        case PLUS -> result + num;
                        case TIMES -> result * num;
                    };
                }
            }

            previousOp = op;
        }

        sum += result;

        return sum;
    }

    private static long parseNumberFromColumn(int column, List<String> lines) {
        var result = 0L;
        for (var i = 0; i < lines.size() - 1; i++) {
            var digit = lines.get(i).substring(column, column + 1);
            if (digit.equals(" ")) {
                continue;
            }

            var d = Integer.parseInt(digit);
            result = result * 10 + d;
        }
        return result;
    }
 
    @Test
    void example() {
        var s = """
                123 328  51 64 \s
                 45 64  387 23 \s
                  6 98  215 314\s
                *   +   *   +  \s
                """;
        assertEquals(4277556, solve1(s));
        assertEquals(3263827, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day06.txt"));
        assertEquals(5381996914800L, solve1(input));
        assertEquals(9627174150897L, solve2(input));
    }
}
