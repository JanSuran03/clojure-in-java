package processor;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

public class Parser {
    static public Vector<Vector<Integer>> parseInputFromFile(String filename) throws Exception {
        Scanner scanner = new Scanner(new File(filename));
        int num_inputs = scanner.nextInt();
        for (int i = 0; i < num_inputs; i++){
            int num_processors = scanner.nextInt();
        }
        while(scanner.hasNext()){
            System.out.println(scanner.nextInt());
        }
        return new Vector<Vector<Integer>>();
    }
}
