import java.util.LinkedList;

public class Node {
    String label;
    LinkedList<Node> children;
    boolean isLeaf;
    double prediction;

    public Node(String label) {
        this.label = label;
    }

    public Node(String label, LinkedList<Node> children) {
        this.label = label;
        this.children = children;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
