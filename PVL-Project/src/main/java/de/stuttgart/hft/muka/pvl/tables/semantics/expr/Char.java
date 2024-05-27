package de.stuttgart.hft.muka.pvl.tables.semantics.expr;

public class Char extends Expr {	
	
	private char c;
	private  String cStr;
	
	public static Char of(String c) {
		if(c.equalsIgnoreCase("\\e"))
			return Epsilon.instance();
		else
			return new Char(c);
	}
	
	protected Char(String c) {
		this.cStr = c;
		this.c = toChar(c);
	}
	
	char toChar(String chr) {
		if(chr.length() == 1)
			return chr.charAt(0);
		
		// otherwise: \x			
		char c = chr.charAt(1);
		switch(c) {
		case 'n': return '\n';
		case 'r': return '\r';
		case 'f': return '\f';
		case 't': return '\t';
		case 'b': return '\b';
		case '\\': return '\\';
		default: return c;
		}
	}


	@Override
	public boolean includes(char c) {
		return this.c == c;
	}	
	
	@Override
	public String toString() {
		return cStr;
	}	
}
