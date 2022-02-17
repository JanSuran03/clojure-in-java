package processor;

public class Memory {
    static public int MEMORY_SIZE = 256;
    static public long[] memory = new long[MEMORY_SIZE];
    static public int num_teleports = 0;
    static public Process[] teleports = new Process[1000];

    static public long get(long index) {
        if (index == 666L) {
            return -1;
        }
        return memory[(((int) index % MEMORY_SIZE) + MEMORY_SIZE) % MEMORY_SIZE];
    }

    static public boolean writeOnIndex(int index, long value) {
        switch (index) {
            case 0:
                return true;
            case 666:
                return false;
            default:
                memory[index % MEMORY_SIZE] = value;
                return true;
        }
    }

    static public void addTeleport(Process process) {
        teleports[num_teleports++] = process;
    }

    static public void resetMemory() {
        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = 0;
        }
    }

    static public void resetTeleports() {
        num_teleports = 0;
        teleports = new Process[1000];
    }

    static public void maybeRunTeleports() {
        if (num_teleports > 1) {
            long temp = teleports[0].private_pc;
            for (int i = 0; i < num_teleports - 1; i++) {
                teleports[i].private_pc = Process.mod256(teleports[i + 1].private_pc + 1);
                teleports[i].waitingForTeleport = false;
            }
            teleports[num_teleports - 1].private_pc = Process.mod256(temp + 1);
            teleports[num_teleports - 1].waitingForTeleport = false;
            resetTeleports();
        }
    }
}
