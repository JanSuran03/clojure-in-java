package processor;

import clojure.lang.*;

public class Main {

    public static Object[][] readInput(String filename) {
        return (Object[][])
                Clj.toArray2d.invoke(
                        Clj.map.invoke(
                                Clj.parseIntegersFn,
                                Clj.splitLines.invoke(
                                        Clj.slurp.invoke(filename))));
    }

    public static void main(String[] args) {
        Object[][] parsed_input = readInput("sample-1.txt");
        for (Object[] i : parsed_input) {
            for (Object k : i) {
                System.out.println(k);
            }
        }
    }
}