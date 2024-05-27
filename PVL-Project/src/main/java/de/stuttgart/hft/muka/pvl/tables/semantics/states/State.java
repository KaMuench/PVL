package de.stuttgart.hft.muka.pvl.tables.semantics.states;

import java.util.Set;

public abstract class State {
	
	private static final int NO_STATE_ID = NoState.instance().getId();
	
	public static int invalidState() {
		return NO_STATE_ID;
	}
	
	public static boolean isInvalidState(int state) {
		return state == NO_STATE_ID;
	}
	
	public static boolean isValidState(int state) {
		return state != NO_STATE_ID;
	}
	
	public int getId() {
		throw new UnsupportedOperationException("Illegal Access");
	}
	
	public Set<Integer> getIds() {
		throw new UnsupportedOperationException("Illegal Access");
	}
	
	public boolean isNoState() {
		return false;
	}
	
	public boolean isStart() {
		return false;
	}

	public boolean isEnd() {
		return false;
	}
}
