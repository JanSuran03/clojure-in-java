package processor;

import java.util.Stack;

public class Memory {
    static public int MEMORY_SIZE = 256;
    static public int[] memory = new int[MEMORY_SIZE];
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
        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = 0;
        }
        teleports = new Stack<>();
    }
}
