import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static String filePath = "./mushroom.csv";

    public static void main(String[] args) throws FileNotFoundException {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        File file = new File(filePath);
        Scanner sc = new Scanner(file);
// EDIBLE,CAP-SHAPE,CAP-SURFACE,CAP-COLOR,BRUISES,ODOR,GILL-ATTACHMENT,GILL-SPACING,GILL-SIZE,GILL-COLOR,STALK-SHAPE,STALK-ROOT,STALK-SURFACE-ABOVE-RING,STALK-SRFACE-UNDER-RING,STALK-COLOR-ABOVE-RING,STALK-COLOR-BELOW-RING,VEIL-TYPE,VEIL-COLOR,RING-NUMBER,RING-TYPE,SPORE-PRINT-COLOR,POPULATION,HABITAT
        sc.nextLine();
        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] tokens = line.split(",");

            // calculate frequency for each attribute
        }
    }
}