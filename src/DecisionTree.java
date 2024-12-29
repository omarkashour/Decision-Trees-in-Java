import java.util.*;

public class DecisionTree {
    Node root;

    public DecisionTree() {
        this.root = null;
    }

    private double entropy(long pos, long neg) {
        if (pos == 0 || neg == 0) return 0;
        if (pos == neg) return 1;

        double total = pos + neg;
        double probOfPos = (double) pos / total;
        double probOfNeg = (double) neg / total;

        return -1 * (probOfPos * log2(probOfPos) + probOfNeg * log2(probOfNeg));
    }

    private double log2(double x) {
        return x == 0 ? 0 : Math.log(x) / Math.log(2);
    }

    private double informationGain(String attribute, String targetAttribute, List<Map<String, String>> data) {
        // total entropy of the dataset
        Map<String, Integer> targetCounts = new HashMap<>();
        for (Map<String, String> example : data) {
            String targetValue = example.get(targetAttribute);
            targetCounts.put(targetValue, targetCounts.getOrDefault(targetValue, 0) + 1);
        }

        double totalEntropy = 0;
        long totalExamples = data.size();
        for (int count : targetCounts.values()) {
            totalEntropy += ((double) count / totalExamples) * entropy(count, totalExamples - count);
        }

        // entropy after splitting by the attribute
        Map<String, List<Map<String, String>>> partitions = partitionData(data, attribute);
        double weightedEntropy = 0;

        for (List<Map<String, String>> subset : partitions.values()) {
            long subsetSize = subset.size();

            // count positive and negative instances in the subset
            Map<String, Integer> subsetTargetCounts = new HashMap<>();
            for (Map<String, String> example : subset) {
                String targetValue = example.get(targetAttribute);
                subsetTargetCounts.put(targetValue, subsetTargetCounts.getOrDefault(targetValue, 0) + 1);
            }

            // entropy for this subset
            double subsetEntropy = 0;
            for (int count : subsetTargetCounts.values()) {
                subsetEntropy += ((double) count / subsetSize) * entropy(count, subsetSize - count);
            }

            // weight by the size of the subset
            weightedEntropy += (double) (subsetSize / totalExamples) * subsetEntropy;
        }


        return totalEntropy - weightedEntropy;
    }

    private double gainRatio(String attribute, String targetAttribute, List<Map<String, String>> data) {
        double infoGain = informationGain(attribute, targetAttribute, data);

        Map<String, Integer> totalCount = new HashMap<>();
        int totalExamples = data.size();

        for (Map<String, String> example : data) {
            String attrValue = example.get(attribute);
            totalCount.put(attrValue, totalCount.getOrDefault(attrValue, 0) + 1);
        }

        double splitInfo = 0;
        for (int count : totalCount.values()) {
            double prob = (double) count / totalExamples;
            splitInfo -= prob * log2(prob);
        }

        return splitInfo == 0 ? 0 : infoGain / splitInfo;
    }

    public Node buildTree(List<Map<String, String>> data, String targetAttribute, ArrayList<String> attributes) {
        // check if all examples belong to one class
        String firstClass = data.get(0).get(targetAttribute);
        boolean allSameClass = data.stream().allMatch(example -> example.get(targetAttribute).equals(firstClass));
        if (allSameClass) {
            Node leaf = new Node(null);
            leaf.classification = firstClass;
            return leaf;
        }

        if (attributes.isEmpty()) {
            Node leaf = new Node(null);
            leaf.classification = majorityClass(data, targetAttribute);
            return leaf;
        }

        String bestAttribute = null;
        double bestGainRatio = 0;

        for (String attribute : attributes) {
            double gainRatio = gainRatio(attribute, targetAttribute, data);
            if (gainRatio > bestGainRatio) {
                bestGainRatio = gainRatio;
                bestAttribute = attribute;
            }

        }

        Node node = new Node(bestAttribute);
        Map<String, List<Map<String, String>>> partitions = partitionData(data, bestAttribute);
        System.out.println("Best attribute: " + bestAttribute);
        ArrayList<String> remainingAttributes = new ArrayList<>(attributes);
        remainingAttributes.remove(bestAttribute);
        attributes.remove(bestAttribute);

        for (Map.Entry<String, List<Map<String, String>>> entry : partitions.entrySet()) {
            String attributeValue = entry.getKey();
            List<Map<String, String>> subset = entry.getValue();

            node.children.put(attributeValue, buildTree(subset, targetAttribute, remainingAttributes));
        }

        return node;
    }

    private String majorityClass(List<Map<String, String>> data, String targetAttribute) {
        Map<String, Integer> count = new HashMap<>();
        for (Map<String, String> example : data) {
            String targetValue = example.get(targetAttribute);
            count.put(targetValue, count.getOrDefault(targetValue, 0) + 1);
        }

        return count.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    private Map<String, List<Map<String, String>>> partitionData(List<Map<String, String>> data, String attribute) {
        Map<String, List<Map<String, String>>> partitions = new HashMap<>();
        for (Map<String, String> example : data) {
            String attrValue = example.get(attribute);
            partitions.putIfAbsent(attrValue, new ArrayList<>());
            partitions.get(attrValue).add(example);
        }
        return partitions;
    }

    public String predict(Node node, Map<String, String> instance) {
        if (node == null) {
            return "Unknown";
        }
        if (node.isLeaf()) {
            return node.classification;
        }

        String attrValue = instance.get(node.attribute);
        Node child = node.children.get(attrValue);

        return predict(child, instance);
    }

    public Map<String, Double> evaluate(List<Map<String, String>> testData, String targetAttribute) {
        double truePositive = 0;
        double trueNegative = 0;
        double falsePositive = 0;
        double falseNegative = 0;

        for (Map<String, String> instance : testData) {
            String actual = instance.get(targetAttribute);
            String predicted = predict(root, instance);
            if (actual.equals("EDIBLE") && predicted.equals("EDIBLE")) {
                truePositive++;
            } else if (actual.equals("POISONOUS") && predicted.equals("POISONOUS")) {
                trueNegative++;
            } else if (actual.equals("POISONOUS") && predicted.equals("EDIBLE")) {
                falsePositive++;
            } else if (actual.equals("EDIBLE") && predicted.equals("POISONOUS")) {
                falseNegative++;
            }
        }

        int total = testData.size();

        // metrics
        double accuracy = (truePositive + trueNegative) / total;
        double precision = truePositive / (truePositive + falsePositive);
        double recall = truePositive / (truePositive + falseNegative);
        double specificity = trueNegative / (trueNegative + falsePositive);
        double fscore = 2 * (precision * recall) / (precision + recall);
        double falsePositiveRate = falsePositive / (trueNegative + falsePositive);

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("accuracy", accuracy);
        metrics.put("precision", precision);
        metrics.put("recall", recall);
        metrics.put("fscore", fscore);
        metrics.put("fpr", falsePositiveRate);
        metrics.put("specificity", specificity);

        return metrics;
    }

    public void printTree(Node node, String indent) {
        if (node == null) return;

        if (node.isLeaf()) {
            System.out.println(indent + "Leaf Node: Class = " + node.classification);
        } else {
            System.out.println(indent + "Decision Node: Attribute = " + node.attribute);
            for (Map.Entry<String, Node> entry : node.children.entrySet()) {
                System.out.println(indent + "  If " + node.attribute + " is [" + entry.getKey() + "]:");
                printTree(entry.getValue(), indent + "    ");
            }
        }
    }
}

