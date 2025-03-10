package com.example.mlproj1;

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
        Map<String, Integer> targetCounts = new HashMap<>();
        for (Map<String, String> example : data) {
            String targetValue = example.get(targetAttribute);
            targetCounts.put(targetValue, targetCounts.getOrDefault(targetValue, 0) + 1);
        }
        long totalPositive = targetCounts.getOrDefault("EDIBLE", 0);
        long totalNegative = targetCounts.getOrDefault("POISONOUS", 0);
        double totalEntropy = entropy(totalPositive, totalNegative);

        Map<String, List<Map<String, String>>> partitions = partitionData(data, attribute);
        double weightedEntropy = 0;

        for (List<Map<String, String>> subset : partitions.values()) {
            long subsetPositive = 0;
            long subsetNegative = 0;

            for (Map<String, String> example : subset) {
                String targetValue = example.get(targetAttribute);
                if ("EDIBLE".equals(targetValue)) subsetPositive++;
                else subsetNegative++;
            }

            long subsetSize = subset.size();
            weightedEntropy += ((double) subsetSize / data.size()) * entropy(subsetPositive, subsetNegative);
        }

        return totalEntropy - weightedEntropy;
    }

    private double gainRatio(String attribute, String targetAttribute, List<Map<String, String>> data) {
        double infoGain = informationGain(attribute, targetAttribute, data);

        Map<String, List<Map<String, String>>> partitions = partitionData(data, attribute);
        double splitInfo = 0;
        int totalExamples = data.size();

        for (List<Map<String, String>> subset : partitions.values()) {
            double prob = (double) subset.size() / totalExamples;
            splitInfo -= prob * log2(prob);
        }

        return Math.abs(splitInfo) < 1e-10 ? 0 : infoGain / splitInfo;
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
        double bestGainRatio = 0.0;

        double bestInfoGain = 0.0;
        for (String attribute : attributes) {
            double gainRatio = gainRatio(attribute, targetAttribute, data);
//            double infoGain = informationGain(attribute, targetAttribute, data);
            System.out.println("Attribute: " + attribute + ", Gain Ratio: " + gainRatio);
//            System.out.println("Attribute: " + attribute + ", Information Gain: " + infoGain);
            if (gainRatio > bestGainRatio) {
                bestGainRatio = gainRatio;
                bestAttribute = attribute;
            }

//            if (infoGain > bestInfoGain) {
//                bestInfoGain = infoGain;
//                bestAttribute = attribute;
//            }

        }
        System.out.println("===========================================");
        Node node = new Node(bestAttribute);
        Map<String, List<Map<String, String>>> partitions = partitionData(data, bestAttribute);
//        System.out.println("Best attribute: " + bestAttribute +", Gain Ratio: " + bestGainRatio);
        ArrayList<String> remainingAttributes = new ArrayList<>(attributes);
        remainingAttributes.remove(bestAttribute);
//        attributes.remove(bestAttribute);

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
                printTree(entry.getValue(), indent + "----");
            }
        }
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        buildTreeString(root, "", output);
        return output.toString();
    }

    private void buildTreeString(Node node, String indent, StringBuilder output) {
        if (node == null) return;

        if (node.isLeaf()) {
            output.append(indent).append("Leaf Node: Class = ").append(node.classification).append("\n");
        } else {
            output.append(indent).append("Decision Node: Attribute = ").append(node.attribute).append("\n");
            for (Map.Entry<String, Node> entry : node.children.entrySet()) {
                output.append(indent).append("  If ").append(node.attribute).append(" is [").append(entry.getKey()).append("]:\n");
                buildTreeString(entry.getValue(), indent + "----", output);
            }
        }
    }

}

