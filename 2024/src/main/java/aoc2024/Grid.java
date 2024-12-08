package aoc2024;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Grid<T> {

    public record GridWithStart<T>(Grid<T> grid, Pos start) {
    }

    private final Map<Pos, T> cells;
    private final PosBounds cellBounds;
    private final PosBounds gridBounds;

    private Grid(Map<Pos, T> cells, PosBounds cellBounds, PosBounds gridBounds) {
        this.cells = cells;
        this.cellBounds = cellBounds;
        this.gridBounds = gridBounds;
    }

    public static <T> GridWithStart<T> parseWithStart(String input, Function<String, Boolean> parseStart, Function<String, T> parseCell) {
        return parseInternal(input, parseStart, parseCell);
    }

    public static <T> Grid<T> parse(String input, Function<String, T> parseCell) {
        return parseInternal(input, null, parseCell).grid();
    }

    private static <T> GridWithStart<T> parseInternal(String input, Function<String, Boolean> parseStart, Function<String, T> parseCell) {
        var gridBounds = new PosBounds.Builder();
        var cellBounds = new PosBounds.Builder();

        Pos start = null;
        var map = new LinkedHashMap<Pos, T>();
        var lines = input.split("\n");
        for (int y = 0; y < lines.length; y++) {
            var line = lines[y];
            var cells = line.split("");
            for (int x = 0; x < cells.length; x++) {
                var s = cells[x];
                if (parseStart != null && parseStart.apply(s)) {
                    if (start != null) {
                        throw new IllegalArgumentException("More than one start cell");
                    }
                    start = new Pos(x, y);
                } else {
                    var cell = parseCell.apply(s);
                    if (cell != null) {
                        map.put(new Pos(x, y), cell);
                        cellBounds.add(x, y);
                    }
                }
                gridBounds.add(x, y);
            }
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Empty grid");
        }
        return new GridWithStart<>(new Grid<>(map, cellBounds.build(), gridBounds.build()), start);
    }

    public T getOrDefault(Pos pos, T defaultValue) {
        return cells.getOrDefault(pos, defaultValue);
    }

    public Map<Pos, T> cells() {
        return cells;
    }

    /**
     * The bounds of the present cell positions (not including absent cells) at the time the grid was created.
     */
    public PosBounds cellBounds() {
        return cellBounds;
    }

    /**
     * The bounds of the grid (including absent cells) at the time the grid was created.
     */
    public PosBounds gridBounds() {
        return gridBounds;
    }
}
