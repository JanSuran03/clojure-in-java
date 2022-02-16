package processor;

import java.util.Stack;

public class Process {
    public int private_pc;
    private Stack<Integer> stack;
    public boolean isTerminated;
    private boolean waitingForTeleport;
    static int MAX_STACK_SIZE = 16;
    static int TWO_TO_32 = (int) Math.pow(2, 32);

    public Process(int offset) {
        private_pc = offset;
        stack = new Stack<>();
        isTerminated = false;
        waitingForTeleport = false;
    }

    public int stayIn32Bits(int num) {
        return num % TWO_TO_32;
    }

    boolean canPush() {
        return this.stack.size() < MAX_STACK_SIZE;
    }

    void pushStack(int num) {
        this.stack.push(num);
    }

    boolean canPop() {
        return this.stack.size() >= 1;
    }

    boolean canPopTwo() {
        return this.stack.size() >= 2;
    }

    int popStack() {
        return this.stack.pop();
    }

    void incPc() {
        this.private_pc = mod256(this.private_pc + 1);
    }

    void terminate() {
        this.isTerminated = true;
    }

    static int mod256(int num) {
        return num % 256;
    }

    static int modularExp(int basis, int exp) {
        basis = basis % TWO_TO_32;
        if (basis == 0)
            return 0;
        long ret = 1L;
        long x = basis;
        long y = exp;
        while (y > 0) {
            if (y % 2 == 1) {
                ret = (ret * x) % TWO_TO_32;
            }
            x = (x * x) % TWO_TO_32;
            y = y / 2;
        }
        return (int) ret;
    }

    /**
     * INSTRUCTION: 0x00
     */
    public void nop() {
        this.incPc();
    }

    /**
     * INSTRUCTION: 0x01
     */
    public void pc() {
        if (this.canPush()) {
            this.pushStack(this.private_pc);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x02
     */
    public void push(int immediate) {
        if (this.canPush()) {
            this.pushStack(immediate);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x03
     */
    public void pop() {
        if (this.canPop()) {
            this.popStack();
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x04
     */
    public void swap() {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            this.pushStack(val1);
            this.pushStack(val2);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x05
     */
    public void dup() {
        if (this.canPush()) {
            this.pushStack(this.stack.peek());
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x06
     */
    public void pushssz() {
        if (this.canPush()) {
            this.pushStack(this.stack.size());
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x07
     */
    public void load() {
        if (this.canPop()) {
            int mem_idx = this.popStack();
            this.pushStack(Memory.get(mem_idx));
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x08
     */
    public void store() {
        if (this.canPopTwo()) {
            int mem_idx = this.popStack();
            int val = this.popStack();
            Memory.writeOnIndex(mem_idx, val);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x09
     */
    public void add() {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            int res = stayIn32Bits(val1 + val2);
            this.pushStack(res);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x0a
     */
    public void sub() {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            int res = stayIn32Bits(val1 - val2);
            this.pushStack(res);
            this.incPc();
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x0b
     */
    public void div() {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            if (val2 == 0) {
                this.terminate();
            } else {
                int res = val1 / val2;
                this.pushStack(stayIn32Bits(res));
                this.incPc();
            }
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x0c
     */
    public void pow() {
        if (this.canPopTwo()) {
            int basis = this.popStack();
            int exp = this.popStack();
            int res = modularExp(basis, exp);
            this.pushStack(stayIn32Bits(res));
            this.incPc();
        } else {
            this.terminate();
        }
    }

    private Process br(int limit, int immediate) {
        if (this.canPop()) {
            if (this.popStack() == limit) {
                this.private_pc = mod256(this.private_pc + immediate + 1);
            } else {
                this.incPc();
            }
        } else {
            this.terminate();
        }
        return this;
    }

    /**
     * INSTRUCTION: 0x0d
     */
    public void brz(int immediate) {
        this.br(0, immediate);
    }

    /**
     * INSTRUCTION: 0x0e
     */
    public void br3(int immediate) {
        this.br(3, immediate);
    }

    /**
     * INSTRUCTION: 0x0f
     */
    public void br7(int immediate) {
        this.br(7, immediate);
    }

    /**
     * INSTRUCTION: 0x10
     */
    public void brge(int immediate) {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            if (val1 >= val2) {
                this.private_pc = mod256(this.private_pc + immediate + 1);
            }
        } else {
            this.terminate();
        }
    }

    /**
     * INSTRUCTION: 0x11
     */
    public void jmp(int immediate) {
        this.private_pc = mod256(this.private_pc + immediate + 1);
    }

    /**
     * INSTRUCTION: 0x12
     */
    public void armedBomb() {
        this.terminate();
    }

    /**
     * INSTRUCTION: 0x13
     */
    public void bomb() {
        Memory.writeOnIndex(this.private_pc, 0x12);
        this.incPc();
    }

    /**
     * INSTRUCTION: 0x14
     */
    public Process tlport() {
        this.waitingForTeleport = true;
        Memory.addTeleport(this);
        return this;
    }

    /**
     * INSTRUCTION: 0x15
     */
    public void jntar() {
        int[] offsets = new int[]{-8, -4, -2, 2, 4, 8};
        for (int offset : offsets)
            Memory.writeOnIndex(mod256(this.private_pc + offset), 0x13);
    }

    static public int binStrToDec(String num) {
        String rev = new StringBuilder(num).reverse().toString();
        int ret = 0, pow = 1;
        for (int i = 0; i < rev.length(); i++) {
            if (rev.charAt(i) == '1') {
                ret += pow;
            }
            pow *= 2;
        }
        return ret;
    }

    static public int[] intToInstructionAndArgument(int num) {
        String as_bin = Integer.toBinaryString(num);
        String fill = "0".repeat(32 - as_bin.length());
        as_bin = fill + as_bin;
        int[] ret = new int[2];
        ret[0] = binStrToDec(as_bin.substring(24));
        ret[1] = binStrToDec(as_bin.substring(8, 24));
        return ret;
    }

    public void dispatchProcess() {
        if (!this.isTerminated) {
            int[] inst_and_arg = intToInstructionAndArgument(Memory.get(this.private_pc));
            int inst = inst_and_arg[0];
            int arg = inst_and_arg[1];
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
