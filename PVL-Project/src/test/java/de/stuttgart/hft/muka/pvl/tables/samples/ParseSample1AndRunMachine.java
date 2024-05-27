package de.stuttgart.hft.muka.pvl.tables.samples;

import de.stuttgart.hft.muka.pvl.statemachine.StateMachine;
import de.stuttgart.hft.muka.pvl.tables.parser.Parser;
import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.SemanticException;
import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.Symbols;

public class ParseSample1AndRunMachine {
	
	public static void main(String[] args) throws SemanticException {		
		Parser p = Parser.fromFile("./src/main/resources/samples/sample1.txt");
		
		p.entries();
		
		Symbols s = p.getSymbols();
		System.out.println(s);
		
		StateMachine sm = new StateMachine(s.getTable("integer"));
		System.out.println(sm.toDetailedString());
		
		{
			sm.init();
			int pos = 0;
			String input = "1234a\n";
			while(sm.isRunning()) {
				char c = (pos < input.length() ? input.charAt(pos++) : 0);
				sm.consume(c);
			}
			System.out.println("Consumption of '" + input + "' succeeded" + " : " + sm.succeeded());
		}

		{
			sm.init();
			int pos = 0;
			String input = "abc";
			while(sm.isRunning()) {
				char c = (pos < input.length() ? input.charAt(pos++) : 0);
				sm.consume(c);
			}
			System.out.println("Consumption of '" + input + "' succeeded" + " : " + sm.succeeded());
		}
	}

}
