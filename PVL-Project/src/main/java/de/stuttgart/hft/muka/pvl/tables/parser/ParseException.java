package de.stuttgart.hft.muka.pvl.tables.parser;

import de.stuttgart.hft.muka.pvl.tables.scanner.Token;
import de.stuttgart.hft.muka.pvl.tables.scanner.TokenType;

@SuppressWarnings("serial")
public class ParseException extends Exception {
		
	private static String message(Token token, TokenType expected, TokenType ... expectedMore) {
		String msg = "Line " + token.lineNo + ": expected " + expected;		
		for(int i = 0; i < expectedMore.length - 1; i++)
			msg = msg + ", " + expectedMore[i];
		if(expectedMore.length >= 1)
			msg = msg + " or " + expectedMore[expectedMore.length - 1];
		msg = msg + " but got " + token;
		return msg;
	}

	public ParseException(Token token, TokenType expected, TokenType ... expectedMore) {
		super(message(token, expected, expectedMore));
	}
}
