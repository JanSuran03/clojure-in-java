package processor;

import clojure.lang.*;

import java.util.Stack;

public class Process {
    private int pc;
    private Stack<Integer> stack;
    private boolean isTerminated;
    private boolean waitingForTeleport;

    public Process(){
        pc = 0;
        stack = new Stack<Integer>();
        isTerminated = false;
        waitingForTeleport = false;
    }
}
