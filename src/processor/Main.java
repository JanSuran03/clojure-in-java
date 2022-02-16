package processor;

import clojure.lang.*;

public class Main {

    static public void init_memory(int[][] input){
        Memory.reset();
        for (int[] process : input) {
            int process_offset = process[0];
            for (int i = 1; i < process.length; i++) {
                Memory.writeOnIndex(process_offset + i - 1, process[i]);
            }
        }
    }

    static public void main(String[] args) throws Exception {

        int[][][] all_inputs = Parser.parseInputFromFile("sample-3.txt");
        for (int[][] input : all_inputs) {
            init_memory(input);
            Process[] processes = new Process[input.length];
        }

        //for (int num : Memory.memory) {
        //    System.out.print(String.valueOf(num) + ' ');
        //}


        for(int obj: Process.intToInstructionAndArgument(156456456))
            System.out.println(obj);

        //for(int obj: Process.intToInstructionAndArgument(45641579))
        //    System.out.println(obj);
    }
}