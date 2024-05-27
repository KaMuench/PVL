package de.stuttgart.hft.muka.pvl.tables.scanner;

import java.util.List;

import de.stuttgart.hft.muka.pvl.tables.reading.InputReader;

import static de.stuttgart.hft.muka.pvl.tables.scanner.TokenType.*;

public class Scanner {
	
	private InputReader ir;
	private String line;
	private int number;
	
	private Scanner(InputReader ir) {
		this.ir = ir;
		this.line = ir.readLine() + '\n';
		this.number = 1;
	}
	
	public static Scanner fromFile(String filename) {
		return new Scanner(InputReader.fromFile(filename));
	}

	public static Scanner fromString(String text) {
		return new Scanner(InputReader.fromString(text));
	}
	
	public Token next() {
//		System.out.println("Current Line: '" + (line == null ? "null" : line.replace("\n", "\\n")) + "'");
		if(line != null && line.matches("\\s*")) {
			line = ir.readLine();
			if (line != null) {
				line = line + '\n';
				number++;
			}
			return new Token(EOL, number);
		}
		if(line == null)
			return new Token(EOI, number);
		
		// Maximum Munch
		List<Result> matches = TokenType.types().stream()
				.map(t -> t.match(line))
				.filter(r -> r.matched())
				.toList();
		if(matches.isEmpty())
			return new Token(UNMATCHED, line, number);
		
		Result match = null;
		if(matches.size() == 1)
			match = matches.get(0);
		else {
			int maxLength = matches.stream().mapToInt(Result::length).max().getAsInt();
			match = matches.stream().filter(r -> r.length() == maxLength).findFirst().get();
		}
		
		// build result and update input
		Token token = new Token(match.type(), match.image(), number);
		if(token.type == EOL) {
			line = ir.readLine();
			number++;			
		} else {
			int end = match.end();
			line = line.substring(end);			
		}
		return token;
	}
}
