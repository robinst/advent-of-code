package aoc2025;

import java.util.List;

enum Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    Pos pos() {
        return switch (this) {
            case UP -> new Pos(0, -1);
            case RIGHT -> new Pos(1, 0);
            case DOWN -> new Pos(0, 1);
            case LEFT -> new Pos(-1, 0);
        };
    }

    Direction turnRight() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    Direction turnLeft() {
        return switch (this) {
            case UP -> LEFT;
            case RIGHT -> UP;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
        };
    }

    /**
     * @return possible new directions with a 90-degree turn
     */
    List<Direction> turns() {
        return switch (this) {
            case UP -> List.of(RIGHT, LEFT);
            case RIGHT -> List.of(DOWN, UP);
            case DOWN -> List.of(LEFT, RIGHT);
            case LEFT -> List.of(UP, DOWN);
        };
    }
}