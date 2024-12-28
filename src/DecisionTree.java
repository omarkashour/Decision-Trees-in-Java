import java.util.HashMap;
import java.util.Map;

public class DecisionTree {

    Node root;


    public DecisionTree() {

    }


    private double entropy(long pos, long neg) {
        if(pos == 0 || neg == 0)
            return 0;
        if (pos == neg)
            return 1;

        double total = pos + neg;
        double probOfPos = pos/total;
        double probOfNeg = neg/total;
        return -1 * (probOfPos * log2(probOfPos) + (probOfNeg) * log2(probOfNeg));
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }



    private double informationGain(String targetClass, String attribute) {

        return 0;
    }


}
