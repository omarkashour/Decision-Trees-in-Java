import java.util.HashMap;
import java.util.Map;

public class Node {
    String attribute;
    Map<String, Node> children;
    String classification;

    public Node(String attribute) {
        this.attribute = attribute;
        this.children = new HashMap<>();
    }

    public boolean isLeaf() {
        return classification != null;
    }
}
