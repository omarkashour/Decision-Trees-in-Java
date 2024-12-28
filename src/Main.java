import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    static final String FILE_PATH = "./mushroom.csv";
    static final String TARGET_ATTRIBUTE = "EDIBLE";

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(FILE_PATH);
        Scanner sc = new Scanner(file);
        String header = sc.nextLine();
        String[] attributes = header.split(",");
        DecisionTree tree = new DecisionTree();
    }
}

