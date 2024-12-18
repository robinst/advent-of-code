package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day17 {

    enum Instruction {
        ADV,
        BXL,
        BST,
        JNZ,
        BXC,
        OUT,
        BDV,
        CDV;

        public static Instruction parse(int opcode) {
            return switch (opcode) {
                case 0 -> ADV;
                case 1 -> BXL;
                case 2 -> BST;
                case 3 -> JNZ;
                case 4 -> BXC;
                case 5 -> OUT;
                case 6 -> BDV;
                case 7 -> CDV;
                default -> throw new IllegalArgumentException("Unknown opcode: " + opcode);
            };
        }

        public boolean takesComboOperand() {
            return switch (this) {
                case ADV -> true;
                case BXL -> false;
                case BST -> true;
                case JNZ -> false;
                case BXC -> false;
                case OUT -> true;
                case BDV -> true;
                case CDV -> true;
            };
        }
    }

    record Result(int newIp, /* @Nullable */ Long output) {
    }

    static class Vm {
        private long registerA;
        private long registerB;
        private long registerC;

        public Vm(long registerA, long registerB, long registerC) {
            this.registerA = registerA;
            this.registerB = registerB;
            this.registerC = registerC;
        }

        public void setRegisterA(long registerA) {
            this.registerA = registerA;
        }

        public List<Long> run(List<Integer> program) {
            var output = new ArrayList<Long>();
            var ip = 0;
            while (ip >= 0 && ip < program.size()) {
                var result = step(program, ip);
                ip = result.newIp();
                if (result.output() != null) {
                    output.add(result.output());
                }
            }
            return output;
        }

        public Long runUntilOutput(List<Integer> program) {
            var ip = 0;
            while (ip >= 0 && ip < program.size()) {
                var result = step(program, ip);
                ip = result.newIp();
                if (result.output() != null) {
                    return result.output();
                }
            }
            return null;
        }

        public Result step(List<Integer> program, int ip) {
            var jumped = false;
            Long output = null;
            var ins = Instruction.parse(program.get(ip));
            switch (ins) {
                case ADV -> registerA = dv(registerA, comboOperand(program.get(ip + 1)));
                case BXL -> registerB = registerB ^ program.get(ip + 1);
                case BST -> registerB = comboOperand(program.get(ip + 1)) % 8;
                case JNZ -> {
                    if (registerA != 0) {
                        ip = program.get(ip + 1);
                        jumped = true;
                    }
                }
                case BXC -> registerB = registerB ^ registerC;
                case OUT -> output = comboOperand(program.get(ip + 1)) % 8;
                case BDV -> registerB = dv(registerA, comboOperand(program.get(ip + 1)));
                case CDV -> registerC = dv(registerA, comboOperand(program.get(ip + 1)));
            }
            if (!jumped) {
                ip += 2;
            }
            return new Result(ip, output);
        }

        private long comboOperand(int num) {
            // Combo operands 0 through 3 represent literal values 0 through 3.
            // Combo operand 4 represents the value of register A.
            // Combo operand 5 represents the value of register B.
            // Combo operand 6 represents the value of register C.
            // Combo operand 7 is reserved and will not appear in valid programs.
            return switch (num) {
                case 0, 1, 2, 3 -> num;
                case 4 -> registerA;
                case 5 -> registerB;
                case 6 -> registerC;
                case 7 -> throw new IllegalArgumentException("Reserved operand 7");
                default -> throw new IllegalArgumentException("Unknown operand: " + num);
            };
        }

        private long dv(long numerator, long operand) {
            return numerator >> operand;
        }
    }

    static String solve1(String input) {
        var numbers = Parsing.numbers(input);
        var registerA = numbers.get(0);
        var registerB = numbers.get(1);
        var registerC = numbers.get(2);
        var program = numbers.subList(3, numbers.size());
        var vm = new Vm(registerA, registerB, registerC);
        var outputs = vm.run(program);
        return outputs.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    static long solve2(String input) {
        var numbers = Parsing.numbers(input);
        var registerB = numbers.get(1);
        var registerC = numbers.get(2);
        var program = numbers.subList(3, numbers.size());
        var vm = new Vm(0, registerB, registerC);

        var solutions = new ArrayList<Long>();
        findSolutions(vm, program, program.size() - 1, 0, solutions);
        return solutions.getFirst();
    }

    static void findSolutions(Vm vm, List<Integer> program, int i, long result, List<Long> solutions) {
        var target = program.get(i);
        // All possible 3 bit numbers
        for (long n = 0; n < 8; n++) {
            var candidate = (result << 3) + n;
            vm.setRegisterA(candidate);
            var output = vm.runUntilOutput(program);
            if (output.intValue() == target && (n != 0 || i != program.size() - 1)) {
                if (i == 0) {
                    solutions.add(candidate);
                } else {
                    findSolutions(vm, program, i - 1, candidate, solutions);
                }
            }
        }
    }

    @Test
    void example() {
        var s = """
                Register A: 729
                Register B: 0
                Register C: 0

                Program: 0,1,5,4,3,0
                """;
        assertEquals("4,6,3,5,6,3,5,2,1,0", solve1(s));
    }

    @Test
    void examplePart2() {
        var s = """
                Register A: 2024
                Register B: 0
                Register C: 0

                Program: 0,3,5,4,3,0
                """;
        assertEquals(117440L, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day17.txt"));
        assertEquals("1,5,7,4,1,6,0,3,0", solve1(input));
        assertEquals(108107574778365L, solve2(input));
    }

    @Test
    void printProgram() {
        var input = Resources.readString(Resources.class.getResource("/day17.txt"));
        var nums = Parsing.numbers(input);
        var program = nums.subList(3, nums.size());
        var graph = Mermaid.builder().direction("LR");
        for (int i = 0; i < program.size(); i++) {
            var instruction = Instruction.parse(program.get(i));
            var operand = program.get(i + 1);
            var operandDescription = String.valueOf(operand);
            if (instruction.takesComboOperand()) {
                if (operand == 4) {
                    operandDescription = "A";
                } else if (operand == 5) {
                    operandDescription = "B";
                } else if (operand == 6) {
                    operandDescription = "C";
                }
            }
            graph.node(String.valueOf(i), i + ": " + instruction.name() + " " + operandDescription);
            if (instruction == Instruction.JNZ) {
                graph.edge(String.valueOf(i), String.valueOf(operand));
            } else {
                graph.edge(String.valueOf(i), String.valueOf(i + 2));
            }
            i++;
        }
        System.out.println(graph.build());
    }
}
