package processor;

import java.util.Arrays;
import java.util.Vector;

public class Main {

    static public String runProcessor(Process[] processes, long[][] for_testing, int ctr) {
        for (int i = 0; i < 5000; i++) {
            for (Process p : processes) {
                p.dispatchProcess();
            }
        }
        long mem_at_42 = Memory.get(42);
        long sum_of_pcs = 0;
        for (Process p : processes) {
            sum_of_pcs += p.private_pc;
        }
        for_testing[ctr] = new long[]{mem_at_42, sum_of_pcs};
        return mem_at_42 + " " + sum_of_pcs + "\n";
    }

    static public Process[] initProcesses(long[][] input) {
        Process[] processes = new Process[input.length];
        for (int i = 0; i < input.length; i++) {
            processes[i] = new Process((int) input[i][0]);
        }
        return processes;
    }

    static public void initMemory(long[][] input) {
        Memory.reset();
        for (long[] process : input) {
            long process_offset = process[0];
            for (int i = 1; i < process.length; i++) {
                Memory.writeOnIndex((int) process_offset + i - 1, process[i]);

            }
        }
    }

    static public long[][] runTask(String filename) throws Exception {
        long[][][] all_inputs = ProcessorIO.parseInputFromFile(filename);
        /*int cc = 0;
        for (long[][] x : all_inputs) {
            System.out.println(cc++);
            for (long[] y : x) {
                for (long z : y) {
                    System.out.print(z + " ");
                }
                System.out.print("\n");
            }
            System.out.println("\n");
        }
        int a = 8 / 2;
        int b = 12 / 3;
        int c = (1 / 0);*/
        long[][] for_testing = new long[all_inputs.length][];
        int ctr = 0;
        StringBuilder to_file = new StringBuilder();
        for (long[][] input : all_inputs) {
            initMemory(input);
            Process[] processes = initProcesses(input);
            to_file.append(runProcessor(processes, for_testing, ctr));
            ctr++;
        }
        ProcessorIO.writeToFile("out-" + filename, to_file.toString());
        return for_testing;
    }

    static public void runSamples() throws Exception {
        String[] samples = new String[]{"sample-1.txt", "sample-2.txt", "sample-3.txt"};
        for (String sample : samples) {
            runTask(sample);
        }
    }

    static public void compare(long[][] ret, long[][] correct) {
        System.out.println();
        System.out.println();
        for (int i = 0; i < ret.length; i++)
            if (ret[i][0] != correct[i][0] || ret[i][1] != correct[i][1])
                System.out.print(i + " ");
    }

    static public void test(String inFile, String outFile) throws Exception {
        long[][] ret = runTask(inFile);
        StringBuilder ret_sb = new StringBuilder();
        int ctr1 = 0, ctr2 = 0;
        for (long[] i : ret) {
            ret_sb.append("[").append(ctr1++).append("| ").append(i[0]).append(" ").append(i[1]).append("] ");
        }
        System.out.println(ret_sb.toString());
        long[][] correct = ProcessorIO.readSolution(outFile);
        StringBuilder correct_sb = new StringBuilder();
        for (long[] i : correct) {
            correct_sb.append("[").append(ctr2++).append("| ").append(i[0]).append(" ").append(i[1]).append("] ");
        }
        System.out.println(correct_sb.toString());

        compare(ret, correct);
    }

    static public void main(String[] args) throws Exception {
        //runSamples();
        //runTask("input.txt");
        test("input_1.txt", "output_1.txt");
        //runTask("input.txt");
    }
}