package aoc2025;

public enum Direction8 {
    U, R, D, L, UR, DR, DL, UL;

    public Pos toPos() {
        return switch (this) {
            case U -> new Pos(0, -1);
            case R -> new Pos(1, 0);
            case D -> new Pos(0, 1);
            case L -> new Pos(-1, 0);
            case UR -> new Pos(1, -1);
            case DR -> new Pos(1, 1);
            case DL -> new Pos(-1, 1);
            case UL -> new Pos(-1, -1);
        };
    }
}
