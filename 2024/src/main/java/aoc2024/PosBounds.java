package aoc2024;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PosBounds(int minX, int maxX, int minY, int maxY) {

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

    public boolean contains(Pos pos) {
        return pos.x() >= minX && pos.x() <= maxX && pos.y() >= minY && pos.y() <= maxY;
    }
}
