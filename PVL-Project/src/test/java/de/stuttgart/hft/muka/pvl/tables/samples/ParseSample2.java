package de.stuttgart.hft.muka.pvl.tables.samples;

import de.stuttgart.hft.muka.pvl.tables.parser.Parser;
import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.SemanticException;

public class ParseSample2 {

	public static void main(String[] args) throws SemanticException {
		
		Parser p = Parser.fromFile("./src/main/resources/samples/sample2.txt");
		
		p.entries();
		
		System.out.println(p.getSymbols());
		
	}

}
