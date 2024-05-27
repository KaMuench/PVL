package de.stuttgart.hft.muka.pvl.tables.semantics.expr;

public class Constant extends Expr {
	
	private String id;
	private Expr expr;
	
	public Constant(String id, Expr expr) {
		super();
		this.id = id;
		this.expr = expr;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean includes(char c) {
		return expr.includes(c);
	}

	@Override
	public String toShortString() {
		return id;
	}

	@Override
	public String toString() {
		return id + ": " + expr;
	}
}
