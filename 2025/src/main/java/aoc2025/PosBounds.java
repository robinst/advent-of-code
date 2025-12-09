package aoc2025;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PosBounds(int minX, int maxX, int minY, int maxY) {

    public PosBounds {
        if (minX > maxX) {
            throw new IllegalArgumentException("minX must be <= maxX: " + minX + ", " + maxX);
        }
        if (minY > maxY) {
            throw new IllegalArgumentException("minY must be <= maxY: " + minY + ", " + maxY);
        }
    }

    public static class Builder {
        private int minX = Integer.MAX_VALUE;
        private int maxX = Integer.MIN_VALUE;
        private int minY = Integer.MAX_VALUE;
        private int maxY = Integer.MIN_VALUE;

        public void add(int x, int y) {
            minX = Math.min(x, minX);
            maxX = Math.max(x, maxX);
            minY = Math.min(y, minY);
            maxY = Math.max(y, maxY);
        }

        public PosBounds build() {
            return new PosBounds(minX, maxX, minY, maxY);
        }
    }

    public static PosBounds of(Pos a, Pos b) {
        return new PosBounds(Math.min(a.x(), b.x()), Math.max(a.x(), b.x()), Math.min(a.y(), b.y()), Math.max(a.y(), b.y()));
    }

    public static PosBounds calculate(Collection<Pos> positions) {
        var builder = new Builder();
        positions.forEach(p -> builder.add(p.x(), p.y()));
        return builder.build();
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

    public List<Pos> borderInside() {
        var border = new ArrayList<Pos>();
        border.addAll(new Pos(minX, minY).straightLineToIncluding(new Pos(maxX, minY)));
        border.addAll(new Pos(maxX, minY).straightLineToIncluding(new Pos(maxX, maxY)));
        border.addAll(new Pos(maxX, maxY).straightLineToIncluding(new Pos(minX, maxY)));
        border.addAll(new Pos(minX, maxY).straightLineToIncluding(new Pos(minX, minY)));
        return border;
    }

    public Stream<Pos> allPositions() {
        return IntStream.rangeClosed(minY, maxY).boxed().flatMap(y -> IntStream.rangeClosed(minX, maxX).mapToObj(x -> new Pos(x, y)));
    }

    /**
     * Shrink by N from all sides if possible (otherwise throws)
     */
    public PosBounds shrink(int n) {
        return new PosBounds(minX + n, maxX - n, minY + n, maxY - n);
    }
}
