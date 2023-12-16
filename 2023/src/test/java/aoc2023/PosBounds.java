package aoc2023;

import java.util.Collection;

public record PosBounds(int minX, int maxX, int minY, int maxY) {

    public static PosBounds calculate(Collection<Pos> positions) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Pos elf : positions) {
            minX = Math.min(elf.x(), minX);
            maxX = Math.max(elf.x(), maxX);
            minY = Math.min(elf.y(), minY);
            maxY = Math.max(elf.y(), maxY);
        }
        return new PosBounds(minX, maxX, minY, maxY);
    }

    public int size() {
        return width() * height();
    }

    public int width() {
        return maxX - minX + 1;
    }

    public int height() {
        return maxY - minY + 1;
    }

    public boolean contains(Pos pos) {
        return pos.x() >= minX && pos.x() <= maxX && pos.y() >= minY && pos.y() <= maxY;
    }
}
