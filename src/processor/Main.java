package processor;

import clojure.lang.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Process process = new Process();

        Parser.parseInputFromFile("sample-1.txt");
    }
}