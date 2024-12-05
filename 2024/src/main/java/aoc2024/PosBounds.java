package aoc2024;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PosBounds(int minX, int maxX, int minY, int maxY) {

    public static PosBounds calculate(Collection<Pos> positions) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (var pos : positions) {
            minX = Math.min(pos.x(), minX);
            maxX = Math.max(pos.x(), maxX);
            minY = Math.min(pos.y(), minY);
            maxY = Math.max(pos.y(), maxY);
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
