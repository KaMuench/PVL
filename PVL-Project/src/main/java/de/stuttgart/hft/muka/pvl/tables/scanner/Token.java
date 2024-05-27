package de.stuttgart.hft.muka.pvl.tables.scanner;

public class Token {
	
	public final TokenType type;
	public final String image;
	public final int lineNo;
	
	public Token(TokenType type) {
		this(type, "", 0);
	}

	public Token(TokenType type, int lineNo) {
		this(type, "", lineNo);
	}

	public Token(TokenType type, String image, int lineNo) {
		this.type = type;
		this.image = image;
		this.lineNo = lineNo;
	}

	@Override
	public String toString() {
		return type + " (" + image + ")";
	}

}
