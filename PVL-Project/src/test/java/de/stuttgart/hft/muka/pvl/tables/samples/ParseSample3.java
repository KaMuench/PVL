package de.stuttgart.hft.muka.pvl.tables.samples;

import de.stuttgart.hft.muka.pvl.tables.parser.Parser;
import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.SemanticException;
import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.Symbols;
import de.stuttgart.hft.muka.pvl.tables.semantics.table.Table;
import de.stuttgart.hft.muka.pvl.tables.semantics.table.Transition;

import java.util.HashSet;
import java.util.TreeSet;

public class ParseSample3 {

    public static void main(String[] args) throws SemanticException {

        Parser p = Parser.fromFile("./src/main/resources/samples/sample2.txt");

        p.entries();

        Symbols s = p.getSymbols();
        Table t = s.getTable("nondet");
        System.out.println(t);

        Table newTable = t.toDEA();
        System.out.println(newTable);
    }

}
