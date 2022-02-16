package processor;

import java.math.BigInteger;
import java.util.Stack;

public class Process {
    private int private_pc;
    private Stack<Integer> stack;
    private boolean isTerminated;
    private boolean waitingForTeleport;
    static int maxStackSize = 16;
    static int two_to_32 = (int) Math.pow(2, 32);

    public Process() {
        private_pc = 0;
        stack = new Stack<Integer>();
        isTerminated = false;
        waitingForTeleport = false;
    }

    public int stayIn32Bits(int num) {
        return num % two_to_32;
    }

    boolean canPush() {
        return this.stack.size() < maxStackSize;
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
        this.private_pc++;
    }

    void terminate() {
        this.isTerminated = true;
    }

    static int mod256(int num) {
        return num % 256;
    }

    static int modularExp(int basis, int exp) {
        basis = basis % two_to_32;
        if (basis == 0)
            return 0;
        long ret = 1L;
        long x = basis;
        long y = exp;
        while (y > 0) {
            if (y % 2 == 1) {
                ret = (ret * x) % two_to_32;
            }
            x = (x * x) % two_to_32;
            y = y / 2;
        }
        return (int) ret;
    }

    public Process nop(int immediate) {
        this.incPc();
        return this;
    }

    public Process pc(int immediate) {
        if (this.canPush()) {
            this.pushStack(private_pc);
            this.incPc();
        } else {
            this.terminate();
        }

        return this;
    }

    public Process push(int immediate) {
        if (this.canPush()) {
            this.pushStack(immediate);
            this.incPc();
        } else {
            this.terminate();
        }
        return this;
    }

    public Process pop(int immediate) {
        if (this.canPop()) {
            this.popStack();
            this.incPc();
        } else {
            this.terminate();
        }
        return this;
    }

    public Process swap(int immediate) {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            this.pushStack(val2);
            this.pushStack(val1);
            this.incPc();
        } else {
            this.terminate();
        }
        return this;
    }

    public Process dup(int immediate) {
        if (this.canPush()) {
            this.pushStack(this.stack.peek());
            this.incPc();
        } else {
            this.terminate();
        }
        return this;
    }

    public Process pushssize(int immediate) {
        if (this.canPush()) {
            this.pushStack(this.stack.size());
            this.incPc();
        } else {
            this.terminate();
        }
        return this;
    }

    public Process load(int immediate) {
        if (this.canPop()) {
            int mem_idx = this.popStack();
        } else {
            this.terminate();
        }
        return this;
    }

    public Process store(int immediate) {
        if (this.canPopTwo()) {
            int mem_idx = this.popStack();
            int val = this.popStack();
        } else {
            this.terminate();
        }
        return this;
    }

    public Process add(int immediate) {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            int res = stayIn32Bits(val1 + val2);
            this.pushStack(res);
        } else {
            this.terminate();
        }
        return this;
    }

    public Process sub(int immediate) {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            int res = stayIn32Bits(val1 - val2);
            this.pushStack(res);
        } else {
            this.terminate();
        }
        return this;
    }

    public Process div(int immediate) {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            if (val2 == 0) {
                this.terminate();
            } else {
                int res = val1 / val2;
                this.pushStack(stayIn32Bits(res));
            }
        } else {
            this.terminate();
        }
        return this;
    }

    public Process pow(int immediate) {
        if (this.canPopTwo()) {
            int basis = this.popStack();
            int exp = this.popStack();
            int res = modularExp(basis, exp);
            this.pushStack(stayIn32Bits(res));
            this.incPc();
        } else {
            this.terminate();
        }
        return this;
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

    public Process brz(int immediate) {
        return this.br(0, immediate);
    }

    public Process br3(int immediate) {
        return this.br(3, immediate);
    }

    public Process br7(int immediate) {
        return this.br(7, immediate);
    }

    public Process brge(int immediate) {
        if (this.canPopTwo()) {
            int val1 = this.popStack();
            int val2 = this.popStack();
            if (val1 >= val2) {
                this.private_pc = mod256(this.private_pc + immediate + 1);
            }
        } else {
            this.terminate();
        }
        return this;
    }

    public Process jmp(int immediate) {
        this.private_pc = mod256(this.private_pc + immediate + 1);
        return this;
    }

    /*0x13*/
    public Process bomb(Memory memory, int immediate) {
        memory.writeOnIndex(this.private_pc, 0x12);
        this.incPc();
        return this;
    }

    /*0x12*/
    public Process armedBomb(int immediate) {
        this.terminate();
        return this;
    }

    public Process tlport(Memory memory, int immediate) {
        this.waitingForTeleport = true;
        memory.addTeleport(this);
        return this;
    }

    public Process jntr(Memory memory, int immediate) {
        int[] offsets = new int[]{-8, -4, -2, 2, 4, 8};
        for (int offset : offsets)
            memory.writeOnIndex(mod256(this.private_pc + offset), 0x13);
        return this;
    }

}
