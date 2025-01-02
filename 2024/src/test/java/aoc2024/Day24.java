package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day24 {

    sealed interface Operation {
        boolean run(boolean a, boolean b);
    }

    static final class And implements Operation {
        @Override
        public boolean run(boolean a, boolean b) {
            return a && b;
        }

        @Override
        public String toString() {
            return "AND";
        }
    }

    static final class Or implements Operation {
        @Override
        public boolean run(boolean a, boolean b) {
            return a || b;
        }

        @Override
        public String toString() {
            return "OR";
        }
    }

    static final class Xor implements Operation {
        @Override
        public boolean run(boolean a, boolean b) {
            return a ^ b;
        }

        @Override
        public String toString() {
            return "XOR";
        }
    }

    static final Operation AND = new And();
    static final Operation OR = new Or();
    static final Operation XOR = new Xor();

    record Gate(String input1, String input2, Operation operation, String output) {
    }

    static long solve1(String input) {
        var parts = input.split("\n\n");
        var wires = parseInitialWires(parts[0]);
        var gateByOutput = new HashMap<String, Gate>();
        var zs = new HashSet<String>();
        for (var line : parts[1].split("\n")) {
            var gate = parseGate(line);
            gateByOutput.put(gate.output(), gate);
            if (gate.output().startsWith("z")) {
                zs.add(gate.output());
            }
        }

        var result = 0L;
        var zOrdered = zs.stream().sorted(Comparator.reverseOrder()).toList();
        for (var wire : zOrdered) {
            var value = calculate(wire, gateByOutput, wires);
            result = result << 1;
            result += value ? 1 : 0;
        }

        return result;
    }

    static Map<String, Boolean> parseInitialWires(String input) {
        var map = new HashMap<String, Boolean>();
        for (var line : input.split("\n")) {
            var parts = line.split(": ");
            map.put(parts[0], Integer.parseInt(parts[1]) == 1);
        }
        return map;
    }

    static Gate parseGate(String line) {
        var arrowParts = line.split(" -> ");
        var p = arrowParts[0].split(" ");
        var output = arrowParts[1];
        var input1 = p[0];
        var operationString = p[1];
        var input2 = p[2];
        var operation = switch (operationString) {
            case "AND" -> AND;
            case "OR" -> OR;
            case "XOR" -> XOR;
            default -> throw new IllegalStateException("Unexpected value: " + operationString);
        };
        return new Gate(input1, input2, operation, output);
    }

    static boolean calculate(String wire, Map<String, Gate> gateByOutput, Map<String, Boolean> wires) {
        var outputValue = wires.get(wire);
        if (outputValue != null) {
            return outputValue;
        }

        var gate = gateByOutput.get(wire);
        var value1 = calculate(gate.input1, gateByOutput, wires);
        var value2 = calculate(gate.input2, gateByOutput, wires);
        outputValue = gate.operation.run(value1, value2);
        wires.put(wire, outputValue);
        return outputValue;
    }

    static String solve2(String input) {
        var parts = input.split("\n\n");
        var gatesByInputs = new HashMap<String, List<Gate>>();
        for (var line : parts[1].split("\n")) {
            var gate = parseGate(line);
            gatesByInputs.computeIfAbsent(gate.input1, _ -> new ArrayList<>()).add(gate);
            gatesByInputs.computeIfAbsent(gate.input2, _ -> new ArrayList<>()).add(gate);
        }

        // Each input/output has 45 wires (0 to 44), one for each binary digit.
        String previousDigitWire = null;
        var errors = new HashSet<String>();
        for (int i = 0; i < 45; i++) {
            previousDigitWire = checkDigit(i, previousDigitWire, gatesByInputs, errors);
        }

        return errors.stream().sorted().collect(Collectors.joining(","));
    }

    static String checkDigit(int digit, String previousDigitWire, Map<String, List<Gate>> gatesByInput, Set<String> errors) {
        // We want this:
        //
        //     x_  y_
        //     |\ /|
        //     | X |
        //     |/ \|
        //    AND XOR
        //     |   |
        //     a   b   previous
        //     |   |\ /|
        //     |   | X |
        //     |   |/ \|
        //     |  AND XOR
        //     |   |   |
        //     |   c   z_
        //     |  /
        //     | /
        //     OR
        //     |
        //     return (previous for next)

        var x = "x%02d".formatted(digit);
        var y = "y%02d".formatted(digit);

        var g1 = checkGates(x, y, gatesByInput, Set.of(AND, XOR));
        var a = g1.get(AND).output();
        var b = g1.get(XOR).output();

        if (previousDigitWire == null) {
            // For the first digit, the first XOR output goes straight to z and a is for the next digit.
            if (!b.startsWith("z")) {
                errors.add(a);
                errors.add(b);
                return b;
            }
            return a;
        }

        if (a.startsWith("z")) {
            errors.add(a);
        }

        if (b.startsWith("z")) {
            throw new IllegalStateException("Not handled");
        }

        if (!gatesByInput.get(b).equals(gatesByInput.get(previousDigitWire))) {
            // b and previous are expected to go to AND, XOR. It's not the case, so assume a and b are swapped.
            errors.add(a);
            errors.add(b);
            var tmp = a;
            a = b;
            b = tmp;
        }

        var g2 = checkGates(b, previousDigitWire, gatesByInput, Set.of(AND, XOR));
        var c = g2.get(AND).output();
        var z = g2.get(XOR).output();

        if (c.startsWith("z")) {
            errors.add(c);
        }

        if (!z.startsWith("z")) {
            errors.add(z);
            if (a.startsWith("z")) {
                a = z;
            } else if (c.startsWith("z")) {
                c = z;
            }
        }

        var g3 = checkGates(a, c, gatesByInput, Set.of(OR));
        var r = g3.get(OR).output();
        if (r.startsWith("z") && digit != 44) {
            errors.add(r);
            return z;
        }
        return r;
    }

    static Map<Operation, Gate> checkGates(String input1, String input2, Map<String, List<Gate>> gatesByInput, Set<Operation> operations) {
        var g1 = gatesByInput.get(input1);
        var g2 = gatesByInput.get(input2);
        if (!Objects.equals(g1, g2)) {
            throw new IllegalStateException("Expected gates for inputs " + input1 + ", " + input2 + " to be equal, but got " + g1 + " and " + g2);
        }

        var gatesByOperation = g1.stream().collect(Collectors.groupingBy(Gate::operation));
        if (!gatesByOperation.keySet().equals(operations)) {
            throw new IllegalStateException("Expected gates for inputs " + input1 + ", " + input2 + " to be " + operations + " but was " + gatesByOperation.keySet());
        }

        return gatesByOperation.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));
    }

    @Test
    void example() {
        var s = """
                x00: 1
                x01: 0
                x02: 1
                x03: 1
                x04: 0
                y00: 1
                y01: 1
                y02: 1
                y03: 1
                y04: 1

                ntg XOR fgs -> mjb
                y02 OR x01 -> tnw
                kwq OR kpj -> z05
                x00 OR x03 -> fst
                tgd XOR rvg -> z01
                vdt OR tnw -> bfw
                bfw AND frj -> z10
                ffh OR nrd -> bqk
                y00 AND y03 -> djm
                y03 OR y00 -> psh
                bqk OR frj -> z08
                tnw OR fst -> frj
                gnj AND tgd -> z11
                bfw XOR mjb -> z00
                x03 OR x00 -> vdt
                gnj AND wpb -> z02
                x04 AND y00 -> kjc
                djm OR pbm -> qhw
                nrd AND vdt -> hwm
                kjc AND fst -> rvg
                y04 OR y02 -> fgs
                y01 AND x02 -> pbm
                ntg OR kjc -> kwq
                psh XOR fgs -> tgd
                qhw XOR tgd -> z09
                pbm OR djm -> kpj
                x03 XOR y03 -> ffh
                x00 XOR y04 -> ntg
                bfw OR bqk -> z06
                nrd XOR fgs -> wpb
                frj XOR qhw -> z04
                bqk OR frj -> z07
                y03 OR x01 -> nrd
                hwm AND bqk -> z03
                tgd XOR rvg -> z12
                tnw OR pbm -> gnj
                """;
        assertEquals(2024, solve1(s));
    }

    @Test
    void drawGraph() {
        var g = Graphviz.directed();
        var input = Resources.readString(Resources.class.getResource("/day24.txt"));
        var parts = input.split("\n\n");
        var gates = parts[1].split("\n");
        var i = 0;
        var zs = new HashSet<String>();
        for (var gateString : gates) {
            var gate = parseGate(gateString);
            var gateName = String.valueOf(i);
            g.node(gateName, gate.operation().toString());
            g.edge(gate.input1, gateName);
            g.edge(gate.input2, gateName);
            g.edge(gateName, gate.output());
            i++;

            if (gate.output().startsWith("z")) {
                zs.add(gate.output());
            }
        }
        for (var z : zs) {
            g.edge(z, "out");
        }
        System.out.println(g.build());
        // g.generate("day24");
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day24.txt"));
        assertEquals(43942008931358L, solve1(input));
        assertEquals("dvb,fhg,fsq,tnc,vcf,z10,z17,z39", solve2(input));
    }
}
