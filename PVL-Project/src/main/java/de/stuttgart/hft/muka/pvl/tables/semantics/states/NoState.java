package de.stuttgart.hft.muka.pvl.tables.semantics.states;

import java.util.Set;

public class NoState extends State {
	
	private static final NoState NO_STATE = new NoState();

	private static final int ID = -1;

	private NoState() { }
	
	public static NoState instance() {
		return NO_STATE;
	}
	
	@Override
	public int getId() {
		return ID;
	}

	@Override
	public Set<Integer> getIds() {
		return Set.of(ID);
	}
	
	@Override
	public boolean isNoState() {
		return true;
	}

	@Override
	public String toString() {
		return "-";
	}
	
}
