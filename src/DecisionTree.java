import java.util.HashMap;
import java.util.Map;

public class DecisionTree {

    Node root;


    public DecisionTree() {
        System.out.println(entropy(2,4));
    }


    private double entropy(long pos, long neg) {
        long total = pos + neg;
        long probOfPos = pos/total;
        long probOfNeg = neg/total;
        return -1 * (probOfPos * log2(probOfPos) + (probOfNeg) * log2(probOfNeg));
    }

    private double log2(long x) {
        return Math.log(x) / Math.log(2);
    }

    private double informationGain(String targetClass,String attribute) {

        return 0;

    }


}
