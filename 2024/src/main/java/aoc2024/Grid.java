package aoc2024;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Grid<T> {

    private final Map<Pos, T> cells;
    private final PosBounds bounds;

    private Grid(Map<Pos, T> cells, PosBounds bounds) {
        this.cells = cells;
        this.bounds = bounds;
    }

    public static <T> Grid<T> parse(String input, Function<String, T> parseCell) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        var map = new LinkedHashMap<Pos, T>();
        var lines = input.split("\n");
        for (int y = 0; y < lines.length; y++) {
            var line = lines[y];
            var cells = line.split("");
            for (int x = 0; x < cells.length; x++) {
                var cell = parseCell.apply(cells[x]);
                if (cell != null) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                    map.put(new Pos(x, y), cell);
                }
            }
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Empty grid");
        }
        return new Grid<>(map, new PosBounds(minX, maxX, minY, maxY));
    }

    public T getOrDefault(Pos pos, T defaultValue) {
        return cells.getOrDefault(pos, defaultValue);
    }

    public PosBounds bounds() {
        return bounds;
    }
}
