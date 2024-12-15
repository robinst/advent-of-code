package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day15 {

    enum Cell {
        Box,
        Wall
    }

    enum Cell2 {
        BoxL,
        BoxR,
        Wall;

        @Override
        public String toString() {
            return switch (this) {
                case BoxL -> "[";
                case BoxR -> "]";
                case Wall -> "#";
            };
        }
    }

    static long solve1(String input) {
        var parts = input.split("\n\n");
        var gridWithStart = Grid.parseWithStart(parts[0], s -> s.equals("@"), s -> s.equals("O") ? Cell.Box : s.equals("#") ? Cell.Wall : null);
        var grid = gridWithStart.grid();
        var robot = gridWithStart.start();

        var moves = parseDirections(parts[1].replace("\n", ""));
        for (var move : moves) {
            var robotNext = robot.plus(move.pos());
            var pos = robot;
            var hadBox = false;
            while (true) {
                var next = pos.plus(move.pos());
                var cell = grid.getOrDefault(next, null);
                if (cell == null) {
                    // Can move
                    if (hadBox) {
                        grid.cells().remove(robotNext);
                        grid.cells().put(next, Cell.Box);
                    }
                    robot = robotNext;
                    break;
                } else if (cell == Cell.Wall) {
                    // Can't move
                    break;
                } else if (cell == Cell.Box) {
                    hadBox = true;
                }
                pos = next;
            }
        }

        var result = 0L;
        for (var entry : grid.cells().entrySet()) {
            var pos = entry.getKey();
            var cell = entry.getValue();
            if (cell == Cell.Box) {
                result += pos.y() * 100L + pos.x();
            }
        }
        return result;
    }

    static List<Direction> parseDirections(String input) {
        var directions = new ArrayList<Direction>();
        for (int i = 0; i < input.length(); i++) {
            var c = input.charAt(i);
            var direction = switch (c) {
                case '<' -> Direction.LEFT;
                case '>' -> Direction.RIGHT;
                case '^' -> Direction.UP;
                case 'v' -> Direction.DOWN;
                default -> throw new IllegalStateException("Unexpected direction: " + c);
            };
            directions.add(direction);
        }
        return directions;
    }

    static long solve2(String input) {
        var parts = input.split("\n\n");
        var map = parts[0];
        map = map.replace("#", "##");
        map = map.replace("O", "[]");
        map = map.replace(".", "..");
        map = map.replace("@", "@.");
        var gridWithStart = Grid.parseWithStart(map, s -> s.equals("@"), s -> s.equals("[") ? Cell2.BoxL : s.equals("]") ? Cell2.BoxR : s.equals("#") ? Cell2.Wall : null);
        var grid = gridWithStart.grid();
        var robot = gridWithStart.start();

        draw(grid, robot);

        var moves = parseDirections(parts[1].replace("\n", ""));
        for (var move : moves) {
            System.out.println("Move " + move + " with robot at " + robot);
            var updates = new HashMap<Pos, Cell2>();
            var canMove = moveBoxes(robot, move, grid, updates);
            if (!canMove) {
                System.out.println("-> Can't move");
                continue;
            }
            grid.cells().putAll(updates);
            robot = robot.plus(move.pos());
            System.out.println("-> " + updates.size() + " updates");
//                draw(grid, robot);
        }

        var result = 0L;
        for (var entry : grid.cells().entrySet()) {
            var pos = entry.getKey();
            var cell = entry.getValue();
            if (cell == Cell2.BoxL) {
                result += pos.y() * 100L + pos.x();
            }
        }
        return result;
    }

    static boolean moveBoxes(Pos start, Direction direction, Grid<Cell2> grid, Map<Pos, Cell2> updates) {
        var pos = start.plus(direction.pos());
        var cell = grid.getOrDefault(pos, null);
        if (cell == null) {
            return true;
        }
        if (cell == Cell2.Wall) {
            return false;
        }
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            var oldL = cell == Cell2.BoxL ? pos : pos.plus(Direction.LEFT.pos());
            var oldR = cell == Cell2.BoxR ? pos : pos.plus(Direction.RIGHT.pos());
            var newL = oldL.plus(direction.pos());
            var newR = oldR.plus(direction.pos());
            updates.put(newL, Cell2.BoxL);
            updates.put(newR, Cell2.BoxR);
            updates.putIfAbsent(oldL, null);
            updates.putIfAbsent(oldR, null);
            return moveBoxes(pos.plus(direction.pos()), direction, grid, updates);
        } else {
            assert direction == Direction.UP || direction == Direction.DOWN;
            var oldL = cell == Cell2.BoxL ? pos : pos.plus(Direction.LEFT.pos());
            var oldR = oldL.plus(Direction.RIGHT.pos());
            var newL = oldL.plus(direction.pos());
            var newR = oldR.plus(direction.pos());
            updates.put(newL, Cell2.BoxL);
            updates.put(newR, Cell2.BoxR);
            updates.putIfAbsent(oldL, null);
            updates.putIfAbsent(oldR, null);
            // If there's another box without any horizontal offset, then these two calls add the same updates.
            // If there's horizontal offset, then we need both. So always doing both is just some unnecessary
            // work that could be avoided, but the result is still correct.
            return moveBoxes(oldL, direction, grid, updates) && moveBoxes(oldR, direction, grid, updates);
        }
    }

    private static void draw(Grid<Cell2> grid, Pos robot) {
        var bounds = grid.gridBounds();
        for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
            for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
                var pos = new Pos(x, y);
                String s;
                if (pos.equals(robot)) {
                    s = "@";
                } else {
                    var cell = grid.getOrDefault(pos, null);
                    s = cell == null ? "." : cell.toString();
                }
                System.out.print(s);
            }
            System.out.println();
        }
    }

    @Test
    void small() {
        var s = """
                ########
                #..O.O.#
                ##@.O..#
                #...O..#
                #.#.O..#
                #...O..#
                #......#
                ########

                <^^>>>vv<v>>v<<
                """;
        assertEquals(2028, solve1(s));
    }

    @Test
    void example() {
        var s = """
                ##########
                #..O..O.O#
                #......O.#
                #.OO..O.O#
                #..O@..O.#
                #O#..O...#
                #O..O..O.#
                #.OO.O.OO#
                #....O...#
                ##########

                <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
                vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
                ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
                <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
                ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
                ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
                >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
                <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
                ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
                v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
                """;
        assertEquals(10092, solve1(s));
        assertEquals(9021, solve2(s));
    }

    @Test
    public void example2() {
        var s = """
                #######
                #...#.#
                #.....#
                #..OO@#
                #..O..#
                #.....#
                #######

                <vv<<^^<<^^
                """;
        assertEquals(618, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day15.txt"));
        assertEquals(1515788L, solve1(input));
        assertEquals(1516544L, solve2(input));
    }
}
