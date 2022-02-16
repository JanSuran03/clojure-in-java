package processor;

import java.util.Stack;

public class Memory {
    static public int memory_size = 256;
    static public int[] memory = new int[memory_size];
    static public Stack<Process> teleports;

    static public int get(int index) {
        return memory[index];
    }

    static public void writeOnIndex(int index, int value) {
        if (index != 0) {
            memory[index] = value;
        }
    }

    static public void addTeleport(Process process) {
        teleports.push(process);
    }

    static public void reset() {
        for (int i = 0; i < memory_size; i++) {
            memory[i] = 0;
        }
        teleports = new Stack<>();
    }
}
