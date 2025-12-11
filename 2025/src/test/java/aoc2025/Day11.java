package aoc2025;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day11 {

    record Device(String name, List<String> outputs) {
    }

    static long solve1(String input) {
        var devices = parseDevices(input);

        var you = devices.get("you");
        var out = devices.get("out");

        return paths(you, out, devices);
    }

    static long solve2(String input) {
        var devices = parseDevices(input);

        var svr = devices.get("svr");
        var fft = devices.get("fft");
        var dac = devices.get("dac");
        var out = devices.get("out");

        var a = paths(svr, fft, devices) * paths(fft, dac, devices) * paths(dac, out, devices);
        var b = paths(svr, dac, devices) * paths(dac, fft, devices) * paths(fft, out, devices);
        return a + b;
    }

    private static long paths(Device from, Device to, Map<String, Device> devices) {
        return paths(new HashMap<>(), from, to, devices);
    }

    private static long paths(Map<Device, Long> cache, Device from, Device to, Map<String, Device> devices) {
        var result = cache.get(from);
        if (result != null) {
            return result;
        }

        result = 0L;
        for (var output : from.outputs) {
            var outputDevice = devices.get(output);
            if (outputDevice == to) {
                result += 1;
            } else if (outputDevice != null) {
                result += paths(cache, outputDevice, to, devices);
            }
        }
        cache.put(from, result);
        return result;
    }

    private static Map<String, Device> parseDevices(String input) {
        return input.lines().map(Day11::parseDevice).collect(toMap(Device::name, d -> d));
    }

    private static Device parseDevice(String line) {
        var parts = line.split(": ");
        var name = parts[0];
        var outputs = Arrays.stream(parts[1].split(" ")).toList();
        return new Device(name, outputs);
    }

    @Test
    void example() {
        var s = """
                aaa: you hhh
                you: bbb ccc
                bbb: ddd eee
                ccc: ddd eee fff
                ddd: ggg
                eee: out
                fff: out
                ggg: out
                hhh: ccc fff iii
                iii: out
                """;
        assertEquals(5, solve1(s));

        var s2 = """
                svr: aaa bbb
                aaa: fft
                fft: ccc
                bbb: tty
                tty: ccc
                ccc: ddd eee
                ddd: hub
                hub: fff
                eee: dac
                dac: fff
                fff: ggg hhh
                ggg: out
                hhh: out
                """;
        assertEquals(2, solve2(s2));
    }

    @Test
    void input() {
        var input = Resources.readString(Resources.class.getResource("/day11.txt"));
        assertEquals(571, solve1(input));
        assertEquals(511378159390560L, solve2(input));
    }
}
