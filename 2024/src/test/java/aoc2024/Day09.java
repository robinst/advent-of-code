package aoc2024;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day09 {

    static long solve1(String input) {
        var disk = parse1(input);

        // Compact
        int lastIndex = disk.size();
        int emptyIndex = -1;
        while (true) {
            emptyIndex = findNextEmpty(disk, emptyIndex + 1);
            lastIndex = findPreviousNonEmpty(disk, lastIndex - 1);
            if (emptyIndex >= lastIndex) {
                break;
            }

            disk.set(emptyIndex, disk.get(lastIndex));
            disk.set(lastIndex, null);
        }

        // Checksum
        var result = 0L;
        for (int i = 0; i < disk.size(); i++) {
            var b = disk.get(i);
            if (b != null) {
                result += b * i;
            }
        }
        return result;
    }

    static List<Integer> parse1(String input) {
        var disk = new ArrayList<Integer>();
        var blockIndex = 0;
        for (int i = 0; i < input.length(); i++) {
            var length = Integer.parseInt(input.substring(i, i + 1));
            var block = (i % 2 == 0) ? blockIndex : null;
            for (int x = 0; x < length; x++) {
                disk.add(block);
            }
            if (i % 2 == 0) {
                blockIndex++;
            }
        }
        return disk;
    }

    static int findNextEmpty(List<Integer> disk, int start) {
        for (int i = start; i < disk.size(); i++) {
            if (disk.get(i) == null) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    static int findPreviousNonEmpty(List<Integer> disk, int start) {
        for (int i = start; i > 0; i--) {
            if (disk.get(i) != null) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }

    record FileBlock(int id, int length) {
    }

    record Block(int length) {
    }

    static long solve2(String input) {
        // Parse
        var fileBlocks = new TreeMap<Integer, FileBlock>();
        var freeBlocks = new TreeMap<Integer, Block>();
        var blockIndex = 0;
        var blockId = 0;
        for (int i = 0; i < input.length(); i++) {
            var length = Integer.parseInt(input.substring(i, i + 1));
            if (i % 2 == 0) {
                fileBlocks.put(blockIndex, new FileBlock(blockId++, length));
            } else {
                freeBlocks.put(blockIndex, new Block(length));
            }
            blockIndex += length;
        }

        // Compact
        var candidates = new ArrayList<>(fileBlocks.reversed().keySet());
        for (var index : candidates) {
            var fileBlock = fileBlocks.get(index);
            var found = freeBlocks.entrySet().stream().filter(b -> b.getValue().length >= fileBlock.length).findFirst();
            if (found.isPresent() && found.get().getKey() < index) {
                var freeIndex = found.get().getKey();
                var freeBlock = found.get().getValue();
                // This frees up a block, but looks like adding the newly-free block to freeBlocks is not necessary
                fileBlocks.remove(index);
                fileBlocks.put(freeIndex, fileBlock);
                var remainingLength = freeBlock.length - fileBlock.length;
                freeBlocks.remove(freeIndex);
                if (remainingLength > 0) {
                    freeBlocks.put(freeIndex + fileBlock.length, new Block(remainingLength));
                }
            }
        }

        // Checksum
        var result = 0L;
        for (var entry : fileBlocks.entrySet()) {
            var index = entry.getKey();
            var block = entry.getValue();
            for (int i = index; i < index + block.length; i++) {
                result += (long) block.id * i;
            }
        }
        return result;
    }

    @Test
    void example() {
        var s = "2333133121414131402";
        assertEquals(1928, solve1(s));
        assertEquals(2858, solve2(s));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day09.txt"));
        assertEquals(6432869891895L, solve1(input));
        assertEquals(6467290479134L, solve2(input));
    }
}
