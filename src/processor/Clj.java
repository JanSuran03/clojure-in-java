package processor;

import clojure.java.api.*;
import clojure.lang.*;

public class Clj {

    static public final String
            Clj = "clojure.core",
            Str = "clojure.string";

    static public final IFn
            read = Clojure.var(Clj, "read-string"),
            eval = Clojure.var(Clj, "eval"),
            slurp = Clojure.var(Clj, "slurp"),
            splitLines = Clojure.var(Str, "split-lines"),
            map = Clojure.var(Clj, "map"),
            parseIntegersFn = (IFn) eval.invoke(
                    read.invoke("(fn [x] (as-> x <> (clojure.string/split <> #\"\\ \") (map read-string <>)))")),
            toArray2d = Clojure.var(Clj, "to-array-2d");

    public Clj() {
    }
}
