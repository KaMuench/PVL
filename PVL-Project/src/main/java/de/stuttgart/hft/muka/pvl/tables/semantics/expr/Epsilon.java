package de.stuttgart.hft.muka.pvl.tables.semantics.expr;

public class Epsilon extends Char {
	
	private static final Epsilon EPSILON = new Epsilon();

	private Epsilon() {
		super("\\e");
	}
	
	public static Epsilon instance() {
		return EPSILON;
	}

	@Override
	public boolean isEpsilon() {
		return true;
	}

	@Override
	public boolean includes(char c) {
		return false;
	}	
}
