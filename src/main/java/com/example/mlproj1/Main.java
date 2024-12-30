package com.example.mlproj1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    static final String FILE_PATH = "./mushroom.csv";
    static final String TARGET_ATTRIBUTE = "EDIBLE";
    
	Font customFontRegular = Font.loadFont(Main.class.getResourceAsStream("/Product Sans Regular.ttf"), 23);
	static Font customFontBold = Font.loadFont(Main.class.getResourceAsStream("/Product Sans Bold.ttf"), 23);
	
    public static void main(String[] args) {
       launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        try {

            List<Map<String, String>> data = parseCSV(FILE_PATH);

            double trainingRatio = 0.9;
            Map<String, List<Map<String, String>>> splitData = splitData(data, trainingRatio);
            List<Map<String, String>> trainingData = splitData.get("train");
            List<Map<String, String>> testData = splitData.get("test");

            ArrayList<String> attributes = new ArrayList<>(data.get(0).keySet());
            attributes.remove(TARGET_ATTRIBUTE);

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
     
            Pane pane = new Pane();
            pane.setMinWidth(3000);
            pane.setMinHeight(3000);
           
            double treeWidth = calculateSubtreeWidth(tree.root, 30);  // Calculate tree width
            double paneWidth = Math.max(treeWidth + 200, 1000);  // Ensure a minimum width and add some margin for visibility
            pane.setPrefWidth(paneWidth);  // Set preferred width of the pane


            double rootX = paneWidth / 2.0;
            drawTree(pane, tree.root, rootX, 50, 30);

            ScrollPane scrollPane = new ScrollPane(pane);
            scrollPane.setPannable(true); 
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Ensure horizontal scrollbar is visible when needed
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Ensure vertical scrollbar is visible
            scrollPane.setFitToWidth(false); // Do not fit to width automatically (avoid scaling the content)

            Label mainL = new Label("Decision Tree in Java");

            
            GridPane topGP = new GridPane();
            Label trainingRatioL = new Label("Training Ratio: " + String.format("%.2f", trainingRatio));
            Slider trainingRatioSlider = new Slider(0.1, 0.9, trainingRatio); // Min: 10%, Max: 90%, Initial: 50%
            trainingRatioSlider.setShowTickMarks(true);
            trainingRatioSlider.setShowTickLabels(true);
            trainingRatioSlider.setMajorTickUnit(0.2);
            trainingRatioSlider.setMinorTickCount(1);
            trainingRatioSlider.setSnapToTicks(true);

            topGP.add(trainingRatioL, 0, 0);
            topGP.add(trainingRatioSlider, 1, 0);

            
            GridPane gp = new GridPane();
            Label evaluationL = new Label("Evaluation Metrics:");
            Label accuracyL = new Label("Accuracy:");
            Label precisionL = new Label("Precision:");
            Label recallL = new Label("Recall (TBR):");
            Label fScoreL = new Label("F-Score:");
            Label specificityL = new Label("Specificity:");
            Label falsePositiveRateL = new Label("False Positive Rate (FPR):");

            
  
            TextField accuracyTF = new TextField(String.format("%.2f%%", metrics.get("accuracy") * 100));
            TextField precisionTF = new TextField(String.format("%.2f%%",metrics.get("precision") * 100));
            TextField recallTF = new TextField( String.format("%.2f%%",metrics.get("recall") * 100));
            TextField fScoreTF = new TextField( String.format("%.2f%%",metrics.get("fscore") * 100));
            TextField specificityTF = new TextField(String.format("%.2f%%",metrics.get("specificity") * 100));
            TextField falsePositiveRateTF = new TextField( String.format("%.2f%%",metrics.get("fpr") * 100));
            accuracyTF.setEditable(false);
            precisionTF.setEditable(false);
            recallTF.setEditable(false);
            fScoreTF.setEditable(false);
            specificityTF.setEditable(false);
            falsePositiveRateTF.setEditable(false);
            
            
            trainingRatioSlider.setOnMouseReleased(e-> {
                double newTrainRatio = trainingRatioSlider.getValue();
                trainingRatioL.setText("Training Ratio: " + String.format("%.2f", newTrainRatio));

                Map<String, List<Map<String, String>>> newSplitData = splitData(data, newTrainRatio);
                List<Map<String, String>> newTrainingData = newSplitData.get("train");
                List<Map<String, String>> newTestData = newSplitData.get("test");
                
   
                tree.root = tree.buildTree(newTrainingData, TARGET_ATTRIBUTE, attributes);
                Map<String, Double> newMetrics = tree.evaluate(newTestData, TARGET_ATTRIBUTE);

                accuracyTF.setText(String.format("%.2f%%", newMetrics.get("accuracy") * 100));
                precisionTF.setText(String.format("%.2f%%", newMetrics.get("precision") * 100));
                recallTF.setText(String.format("%.2f%%", newMetrics.get("recall") * 100));
                fScoreTF.setText(String.format("%.2f%%", newMetrics.get("fscore") * 100));
                specificityTF.setText(String.format("%.2f%%", newMetrics.get("specificity") * 100));
                falsePositiveRateTF.setText(String.format("%.2f%%", newMetrics.get("fpr") * 100));

                pane.getChildren().clear();
                drawTree(pane, tree.root, rootX, 50, 30);
            });
            
            Button buildTreeBtn = new Button("Build Tree");
            
            topGP.setAlignment(Pos.CENTER);
            topGP.setHgap(15);
            
            gp.add(evaluationL, 0, 0);
            gp.add(accuracyL, 0, 1);
            gp.add(precisionL, 0, 2);
            gp.add(recallL, 0, 3);
            gp.add(fScoreL, 0, 4);
            gp.add(specificityL, 0, 5);
            gp.add(falsePositiveRateL, 0, 6);
            
            gp.add(accuracyTF, 1, 1);
            gp.add(precisionTF, 1, 2);
            gp.add(recallTF, 1, 3);
            gp.add(fScoreTF, 1, 4);
            gp.add(specificityTF, 1, 5);
            gp.add(falsePositiveRateTF, 1, 6);
//            gp.add(buildTreeBtn, 1, 7);
            
            gp.setVgap(15);
            gp.setHgap(15);
            gp.setAlignment(Pos.CENTER);
            
            Image img = new Image(getClass().getResource("/images/main_label.png").toString());
            ImageView imgV = new ImageView(img);
            imgV.setPreserveRatio(true);
            imgV.setFitWidth(500);
            
            BorderPane bp = new BorderPane();
            bp.setAlignment(mainL, Pos.CENTER);
            BorderPane leftBP = new BorderPane();
            VBox vb = new VBox(imgV,topGP,gp);
            vb.setAlignment(Pos.CENTER);
            vb.setSpacing(15);
            leftBP.setCenter(vb);
            leftBP.setMargin(vb, new Insets(15));
    		leftBP.setStyle("-fx-background-color: #C2D9FF; -fx-border-color: #8E8FFA; -fx-border-width: 1px;");
            bp.setLeft(leftBP);
            bp.setRight(scrollPane);
            
            bp.setMargin(scrollPane, new Insets(15));
            bp.setStyle(
            	    "-fx-border-color: transparent; " + // Outer border (if needed)
            	    "-fx-border-insets: 0; " +
            	    "-fx-border-width: 0; " +
            	    "-fx-background-color: white;" +
            	    "-fx-region-border-color: black; " + // Separating lines
            	    "-fx-region-border-width: 2 2 2 2;" // Top, Right, Bottom, Left separators
            	);
    		Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

            Scene scene = new Scene(bp, 1000, 600); 
    		scene.getStylesheets().add(getClass().getResource("/css/style.css").toString());

            stage.setTitle("1210082 - Omar Kashour - Machine Learning Project 1");
            stage.setScene(scene);
            stage.show();
        } catch (FileNotFoundException e) {
            System.err.println("Error reading the dataset: " + e.getMessage());
        }
    }
    
    private double calculateSubtreeWidth(Node node, double horizontalSpacing) {
        if (node == null || node.isLeaf() || node.children == null) {
            return 0;
        }
        double width = 0;
        for (Node child : node.children.values()) {
            width += calculateSubtreeWidth(child, horizontalSpacing) + horizontalSpacing;
        }
        return Math.max(width, horizontalSpacing);
    }

    private void drawTree(Pane pane, Node node, double x, double y, double horizontalSpacing) {
        if (node == null) return;

        
        Circle circle = new Circle(x, y, 20);
        if(node.isLeaf()) {
        	if(node.classification.equals("EDIBLE"))
          circle.setFill(Color.LIGHTGREEN);
        	else
                circle.setFill(Color.RED);

        }
        else {
        circle.setFill(Color.LIGHTBLUE);
        }
        circle.setStroke(Color.BLACK);
        Text text = new Text(x - 10, y + 5, node.isLeaf() ? node.classification : node.attribute);
        pane.getChildren().addAll(circle, text);

        if (!node.isLeaf() && node.children != null) {
            double childY = y + 100; // Vertical spacing between levels

            double totalSubtreeWidth = calculateSubtreeWidth(node, horizontalSpacing);
            double currentX = x - totalSubtreeWidth / 2; // Start position for child nodes

            for (Map.Entry<String, Node> entry : node.children.entrySet()) {
                String edgeLabel = entry.getKey();
                Node childNode = entry.getValue();

                double childSubtreeWidth = calculateSubtreeWidth(childNode, horizontalSpacing);
                double childX = currentX + childSubtreeWidth / 2;

                // Draw the edge to the child node
                Line line = new Line(x, y + 20, childX, childY - 20);
                Text edgeText = new Text((x + childX) / 2, (y + childY) / 2 - 10, edgeLabel);
                pane.getChildren().addAll(line, edgeText);

                // Recursively draw the child node
                drawTree(pane, childNode, childX, childY, horizontalSpacing);

                currentX += childSubtreeWidth + horizontalSpacing; // Update position for the next child
            }
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
