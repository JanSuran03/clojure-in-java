package processor;

public class Main {

    static public void runProcessor(Process[] processes) {
        for (int i = 0; i < 5000; i++) {
            for (Process p : processes) {
                p.dispatchProcess();
            }
        }
        int mem_at_42 = Memory.get(42);
        int sum_of_pcs = 0;
        for (Process p : processes) {
            sum_of_pcs += p.private_pc;
        }
        System.out.print(mem_at_42);
        System.out.println(" " + String.valueOf(sum_of_pcs));
    }

    static public void initMemory(int[][] input) {
        Memory.reset();
        for (int[] process : input) {
            int process_offset = process[0];
            for (int i = 1; i < process.length; i++) {
                Memory.writeOnIndex(process_offset + i - 1, process[i]);
            }
        }
    }

    static public void runSample(String filename) throws Exception {
        int[][][] all_inputs = Parser.parseInputFromFile(filename);
        for (int[][] input : all_inputs) {
            initMemory(input);
            Process[] processes = new Process[input.length];
            for (int i = 0; i < input.length; i++) {
                processes[i] = new Process();
            }
            runProcessor(processes);
        }
    }

    static public void runSamples() throws Exception {
        String[] samples = new String[]{"sample-1.txt", "sample-2.txt", "sample-3.txt"};
        for (String sample : samples) {
            runSample(sample);
        }
    }

    static public void main(String[] args) throws Exception {

        runSamples();

        //for (int num : Memory.memory) {
        //    System.out.print(String.valueOf(num) + ' ');
        //}


        //for (int obj : Process.intToInstructionAndArgument(156456456))
        //    System.out.println(obj);
    }
}