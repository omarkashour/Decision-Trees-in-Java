import java.util.LinkedList;

public class Node {
    String label;
    Node left;
    Node right;
    boolean isLeaf;
    double prediction;

    public Node(String label) {
        this.label = label;
    }

    public Node(String label, Node left, Node right) {
        this.label = label;
        this.left = left;
        this.right = right;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    @Override
    public String toString() {
        return "Node{" +
                "label='" + label + '\'' +
                ", isLeaf=" + isLeaf +
                ", prediction=" + prediction +
                '}';
    }
}
