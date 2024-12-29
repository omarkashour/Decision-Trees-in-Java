import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    static final String FILE_PATH = "./mushroom.csv";
    static final String TARGET_ATTRIBUTE = "EDIBLE";

    public static void main(String[] args) {
        try {
            // parse the dataset
            List<Map<String, String>> data = parseCSV(FILE_PATH);

            // split data into training and test sets
            Map<String, List<Map<String, String>>> splitData = splitData(data, 0.5);
            List<Map<String, String>> trainingData = splitData.get("train");
            List<Map<String, String>> testData = splitData.get("test");



            ArrayList<String> attributes = new ArrayList<String> (data.get(0).keySet());
            attributes.remove(TARGET_ATTRIBUTE);
//            System.out.println(attributes);

            DecisionTree tree = new DecisionTree();
            tree.root = tree.buildTree(trainingData, TARGET_ATTRIBUTE, attributes);


            System.out.println("Decision Tree:");
            tree.printTree(tree.root, "");

            // evaluate the tree on the test set
            System.out.println();
            System.out.println("Training data size: " + trainingData.size());
            System.out.println("Test data size: " + testData.size());

            Map<String, Double> metrics = tree.evaluate(testData, TARGET_ATTRIBUTE);

            System.out.println("\nEvaluation Metrics:");
            System.out.printf("Accuracy: %.2f%%\n", metrics.get("accuracy") * 100);
            System.out.printf("Precision: %.2f%%\n", metrics.get("precision") * 100);
            System.out.printf("Recall (TPR): %.2f%%\n", metrics.get("recall") * 100);
            System.out.printf("F-Score: %.2f%%\n", metrics.get("fscore") * 100);
            System.out.printf("Specificity: %.2f%%\n", metrics.get("specificity") * 100);
            System.out.printf("False Positive Rate (FPR): %.2f%%\n", metrics.get("fpr") * 100);


        } catch (FileNotFoundException e) {
            System.err.println("Error reading the dataset: " + e.getMessage());
        }
    }

    public static List<Map<String, String>> parseCSV(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        List<Map<String, String>> data = new ArrayList<>();
        String headerLine = sc.nextLine();
        String[] attributes = headerLine.split(",");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] values = line.split(",");

            if (values.length == attributes.length) {
                Map<String, String> instance = new HashMap<>();
                for (int i = 0; i < attributes.length; i++) {
                    instance.put(attributes[i], values[i]);
                }
                data.add(instance);
            }
        }

        sc.close();
        return data;
    }

    public static Map<String, List<Map<String, String>>> splitData(List<Map<String, String>> data, double trainRatio) {
        Collections.shuffle(data);
        int trainSize = (int) (data.size() * trainRatio);

        List<Map<String, String>> trainingData = new ArrayList<>(data.subList(0, trainSize));
        List<Map<String, String>> testData = new ArrayList<>(data.subList(trainSize, data.size()));

        Map<String, List<Map<String, String>>> split = new HashMap<>();
        split.put("train", trainingData);
        split.put("test", testData);

        return split;
    }

}
