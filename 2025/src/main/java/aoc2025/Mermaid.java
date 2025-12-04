package aoc2025;

import java.util.ArrayList;
import java.util.List;

public class Mermaid {

    record Node(String name, String label) {
    }

    record Edge(String from, String to, String label) {
    }

    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private String direction = "TD";

    public static Mermaid builder() {
        return new Mermaid();
    }

    /**
     * @param direction e.g. "LR" for left to right or "TB" for top to bottom
     */
    public Mermaid direction(String direction) {
        this.direction = direction;
        return this;
    }

    public void node(String name, String label) {
        nodes.add(new Node(name, label));
    }

    public void edge(String from, String to) {
        edges.add(new Edge(from, to, null));
    }

    public String build() {
        var sb = new StringBuilder();
        sb.append("flowchart " + direction);
        sb.append("\n");

        for (Node node : nodes) {
            sb.append("    ");
            sb.append(node.name());
            if (node.label() != null) {
                sb.append("[\"`");
                sb.append(node.label());
                sb.append("`\"]");
            }
            sb.append("\n");
        }

        for (Edge edge : edges) {
            sb.append("    ");
            sb.append(edge.from());
            sb.append("-->");
            if (edge.label() != null) {
                sb.append("|").append(edge.label()).append("|");
            }
            sb.append(edge.to());
            sb.append("\n");
        }
        return sb.toString();
    }
}
