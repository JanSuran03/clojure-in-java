package processor;

import java.util.Stack;

public class Memory {
    static public int MEMORY_SIZE = 256;
    static public long[] memory = new long[MEMORY_SIZE];
    static public Stack<Process> teleports;

    static public long get(long index) {
        return memory[(((int) index % MEMORY_SIZE) + MEMORY_SIZE) % MEMORY_SIZE];
    }

    static public long get(int index) {
        return get((long) index);
    }

    static public void writeOnIndex(int index, long value) {
        if (index != 0) {
            if (value < 0) {
                throw new RuntimeException("WRITE ON INDEX: value = " + value);
            }
            memory[index % MEMORY_SIZE] = value;
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
