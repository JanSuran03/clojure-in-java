package processor;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class ProcessorIO {
    static public long[][][] parseInputFromFile(String filename) throws Exception {
        Scanner scanner = new Scanner(new File(filename));
        int num_inputs = scanner.nextInt();
        long[][][] ret = new long[num_inputs][][];
        for (int i = 0; i < num_inputs; i++) {
            int num_processors = scanner.nextInt();
            long[][] processors = new long[num_processors][];
            for (int j = 0; j < num_processors; j++) {
                int mem_offset = scanner.nextInt();
                int num_instructions = scanner.nextInt();
                long[] instructions = new long[num_instructions + 1];
                instructions[0] = mem_offset;
                for (int k = 1; k <= num_instructions; k++) {
                    instructions[k] = scanner.nextLong();
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
        writer.close();
    }

    static public long[][] readSolution(String filename) throws Exception{
        Scanner scanner = new Scanner(new File(filename));
        int output_lines = 500;
        long[][] ret = new long[output_lines][];
        for (int i = 0; i < output_lines; i++){
            long at_42 = scanner.nextLong();
            long sum_of_pcs = scanner.nextLong();
            ret[i] = new long[]{at_42, sum_of_pcs};
        }
        return ret;
    }
}
