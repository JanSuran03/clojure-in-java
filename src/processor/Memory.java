package processor;

import java.util.Stack;

public class Memory {
    static public int[] memory;
    static public int memory_size = 256;
    static public Stack<Process> teleports;

    public int get(int index) {
        return memory[index];
    }

    public void writeOnIndex(int index, int value) {
        memory[index] = value;
    }

    public Memory addTeleport(Process process) {
        teleports.push(process);
        return this;
    }

    public Memory reset() {
        for (int i = 0; i < memory_size; i++) {
            memory[i] = 0;
        }
        teleports = new Stack<Process>();
        return this;
    }
}
