package processor;

import java.util.Stack;

public class Process {
    public long private_pc;
    private final Stack<Long> stack;
    public boolean isTerminated;
    public boolean waitingForTeleport;
    static int MAX_STACK_SIZE = 16;
    static long TWO_TO_32 = (long) Math.pow(2, 32);

    public Process(int offset) {
        private_pc = offset;
        stack = new Stack<>();
        isTerminated = false;
        waitingForTeleport = false;
    }

    static public long stayIn32Bits(long num) {
        return num % TWO_TO_32;
    }

    boolean canPush() {
        return this.stack.size() < MAX_STACK_SIZE;
    }

    boolean pushStack(long num) {
        if (num == -1) {
            return false;
        }
        this.stack.push(num);
        return true;
    }

    boolean canPop() {
        return this.stack.size() >= 1;
    }

    boolean canPopTwo() {
        return this.stack.size() >= 2;
    }

    long popStack() {
        return this.stack.pop();
    }

    void incPc() {
        this.private_pc = mod256(this.private_pc + 1);
    }

    void terminate() {
        this.isTerminated = true;
    }

    static long mod256(long num) {
        return num % 256;
    }

    static long modularExp(long x, long y) {
        x = x % TWO_TO_32;
        if (x == 0)
            return 0;
        long ret = 1L;
        while (y > 0) {
            if (y % 2 == 1) {
                ret = ((ret * x) % TWO_TO_32 + TWO_TO_32) % TWO_TO_32;
            }
            x = ((x * x) % TWO_TO_32 + TWO_TO_32) % TWO_TO_32;
            y = y >> 1;
        }
        return ret;
    }

    public void nop() {
        this.incPc();
    }

    public void pc() {
        if (this.canPush()) {
            this.pushStack(this.private_pc);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void push(long immediate) {
        if (this.canPush()) {
            this.pushStack(immediate);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void pop() {
        if (this.canPop()) {
            this.popStack();
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void swap() {
        if (this.canPopTwo()) {
            long val1 = this.popStack();
            long val2 = this.popStack();
            this.pushStack(val1);
            this.pushStack(val2);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void dup() {
        if (this.canPush() && this.canPop()) {
            this.pushStack(this.stack.peek());
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void pushssz() {
        if (this.canPush()) {
            this.pushStack(this.stack.size());
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void load() {
        if (this.canPop()) {
            long mem_idx = this.popStack();
            //System.out.println(mem_idx);
            if (this.pushStack(Memory.get(mem_idx))) {
                this.incPc();
            } else {
                this.terminate();
            }
        } else {
            this.terminate();
        }
    }

    public void store() {
        if (this.canPopTwo()) {
            long mem_idx = this.popStack();
            long val = this.popStack();
            if (!Memory.writeOnIndex((int) mem_idx, val)) {
                this.terminate();
            } else {
                this.incPc();
            }
        } else {
            this.terminate();
        }
    }

    public void add() {
        if (this.canPopTwo()) {
            long val1 = this.popStack();
            long val2 = this.popStack();
            long res = stayIn32Bits(val1 + val2);
            this.pushStack(res);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void sub() {
        if (this.canPopTwo()) {
            long val1 = this.popStack();
            long val2 = this.popStack();
            long res = stayIn32Bits(val1 - val2 + TWO_TO_32);
            this.pushStack(res);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void div() {
        if (this.canPopTwo()) {
            long val1 = this.popStack();
            long val2 = this.popStack();
            if (val2 == 0) {
                this.terminate();
            } else {
                long res = val1 / val2;
                this.pushStack(stayIn32Bits(res));
                this.incPc();
            }
        } else {
            this.terminate();
        }
    }

    public void pow() {
        if (this.canPopTwo()) {
            long basis = this.popStack();
            long exp = this.popStack();
            long res = modularExp(basis, exp);
            this.pushStack(stayIn32Bits(res));
            this.incPc();
        } else {
            this.terminate();
        }
    }

    private void br(int limit, long immediate) {
        if (this.canPop()) {
            if (this.popStack() == limit) {
                this.private_pc = mod256(this.private_pc + immediate);
            }
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void brz(long immediate) {
        this.br(0, immediate);
    }

    public void br3(long immediate) {
        this.br(3, immediate);
    }

    public void br7(long immediate) {
        this.br(7, immediate);
    }

    public void brge(long immediate) {
        if (this.canPopTwo()) {
            long val1 = this.popStack();
            long val2 = this.popStack();
            if (val1 >= val2) {
                this.private_pc = mod256(this.private_pc + immediate);
            }
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void jmp(long immediate) {
        this.private_pc = mod256(immediate + 1);
    }

    public void armedBomb() {
        this.terminate();
    }

    public void bomb() {
        if (Memory.writeOnIndex((int) this.private_pc, 0x12)) {
            this.incPc();
        } else {
            this.terminate();
        }
    }

    public void tlport() {
        this.waitingForTeleport = true;
        Memory.addTeleport(this);
    }

    public void jntar() {
        int[] offsets = new int[]{-8, -4, -2, 2, 4, 8};
        for (int offset : offsets)
            if (!Memory.writeOnIndex
                    ((int) mod256((this.private_pc + offset + Memory.MEMORY_SIZE)),
                            0x13)) {
                this.terminate();
                break;
            }
        if (!this.isTerminated) {
            this.incPc();
        }
    }

    static public long binStrToDec(String num) {
        String rev = new StringBuilder(num).reverse().toString();
        long ret = 0, pow = 1;
        for (int i = 0; i < rev.length(); i++) {
            if (rev.charAt(i) == '1') {
                ret += pow;
            }
            pow *= 2;
        }
        return ret;
    }

    static public long[] intToInstructionAndArgument(long num) {
        String as_bin = Long.toBinaryString(num);
        String fill = "0".repeat(Math.max(32 - as_bin.length(), 0));
        as_bin = fill + as_bin;
        long[] ret = new long[2];
        ret[0] = binStrToDec(as_bin.substring(24));
        ret[1] = binStrToDec(as_bin.substring(8, 24));
        return ret;
    }

    public void dispatchProcess() {
        if (!this.isTerminated && !this.waitingForTeleport) {
            long[] inst_and_arg = intToInstructionAndArgument(Memory.get(this.private_pc));
            int inst = (int) inst_and_arg[0];
            long arg = inst_and_arg[1];
            switch (inst) {
                case 0x00 -> this.nop();
                case 0x01 -> this.pc();
                case 0x02 -> this.push(arg);
                case 0x03 -> this.pop();
                case 0x04 -> this.swap();
                case 0x05 -> this.dup();
                case 0x06 -> this.pushssz();
                case 0x07 -> this.load();
                case 0x08 -> this.store();
                case 0x09 -> this.add();
                case 0x0a -> this.sub();
                case 0x0b -> this.div();
                case 0x0c -> this.pow();
                case 0x0d -> this.brz(arg);
                case 0x0e -> this.br3(arg);
                case 0x0f -> this.br7(arg);
                case 0x10 -> this.brge(arg);
                case 0x11 -> this.jmp(arg);
                case 0x12 -> this.armedBomb();
                case 0x13 -> this.bomb();
                case 0x14 -> this.tlport();
                case 0x15 -> this.jntar();
                default -> this.terminate();
            }
        }
    }
}
