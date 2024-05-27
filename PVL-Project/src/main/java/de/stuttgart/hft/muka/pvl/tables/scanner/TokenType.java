package de.stuttgart.hft.muka.pvl.tables.scanner;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TokenType {
	
	// The order of the tokens is also the precedence - which means 
	// in a maximum munch when several tokens with equal length are
	// matched, the one with the lowest ordinal number is selected.
	
	START_STATE("[0-9]+(s|S)"),
	END_STATE("[0-9]+(e|E)"),
	SINGLE_STATE("[0-9]+"),
	NO_STATE("-"),
	STATE_SET("([0-9]+)(\\|([0-9]+))+"),
	
	DEF("def"), 
	FSM("fsm"),
	
	// any identifier with at least two characters - to discriminate them from single characters
	ID("[_a-zA-Z][_a-zA-Z0-9]+"), 
	
	// either any character preceeded by a single \ (which is \\\\) , e.g. \0, \a, \#, \\, \ , \x
	// or any single character apart form digits, \, #, space, end of line (x03)	
	CHAR("(\\\\.)|[^0-9\\\\#\\s\\x03]"),
	
	// A range may be a set of chars or a group (x-y). Any character may be escaped,
	// if a character is not escaped, any character is allowed except for -, [, ]
	RANGE("~?\\[(((\\\\.|[^\\-\\[])-(\\\\.|[^\\]]))|(\\\\.|[^\\-\\[\\]]))*\\]"),
	
	EOL("(#[^\\n]*)?"), 
	EOI("\\x03"),
	
	UNMATCHED("[^\\s]+");
	
	public static record Result(boolean matched, TokenType type, String image, int length, int end) {
		
		public static Result failed() {
			return new Result(false, null, null, -1, -1);
		}
		
		public static Result matched(TokenType type, String image, int end) {
			return new Result(true, type, image, image.length(), end);
		}
	}
	
	private static List<TokenType> types = null;
	
	private Pattern pattern;

	private TokenType(String regex) {
		this.pattern = Pattern.compile("^\\s*(" + regex + ")\\s*");
	}
	
	static List<TokenType> types() {
		if(types == null)
			types = List.of(TokenType.values()).stream()
					.filter(t -> t != UNMATCHED)
					.toList();
		return types;
	}

	Result match(String line) {
		Matcher matcher = pattern.matcher(line);
		if(matcher.find())
			return Result.matched(this, matcher.group(1), matcher.end());
		else
			return Result.failed();
	}
}
