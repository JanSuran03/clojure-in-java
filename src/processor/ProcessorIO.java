package processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

public class ProcessorIO {
    static public int[][][] parseInputFromFile(String filename) throws Exception {
        Scanner scanner = new Scanner(new File(filename));
        int num_inputs = scanner.nextInt();
        int[][][] ret = new int[num_inputs][][];
        for (int i = 0; i < num_inputs; i++) {
            int num_processors = scanner.nextInt();
            int[][] processors = new int[num_processors][];
            for (int j = 0; j < num_processors; j++) {
                int mem_offset = scanner.nextInt();
                int num_instructions = scanner.nextInt();
                int[] instructions = new int[num_instructions + 1];
                instructions[0] = mem_offset;
                for (int k = 1; k <= num_instructions; k++) {
                    instructions[k] = scanner.nextInt();
                }
                processors[j] = instructions;
            }
            ret[i] = processors;
        }
        return ret;
    }

    static public void writeToFile(String filename, String content) throws Exception {
        FileWriter writer = new FileWriter(filename);
        writer.write(content);
        writer.flush();
    }
}
