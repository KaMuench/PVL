package de.stuttgart.hft.muka.pvl.statemachine;

import de.stuttgart.hft.muka.pvl.tables.semantics.states.State;
import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.SemanticException;
import de.stuttgart.hft.muka.pvl.tables.semantics.table.Table;

public class StateMachine {
	
	private final Table table;
	
	private boolean running = false;
	private int state;
		
	public StateMachine(Table table) throws SemanticException {
		this.table = table;
	}

	public String getId() {
		return table.getId();
	}
	
	public String toDetailedString(){
		return table.toString();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean hasStoped() {
		return !running;
	}
	
	public boolean succeeded() {
		if(running)
			throw new StateMachineException("Cannot access halt state while running");
		return table.isEnd(state);
	}

	public boolean failed() {
		if(isRunning())
			throw new StateMachineException("Cannot access halt state while running");
		return !succeeded();
	}
	
	public void init() {
		if(table.isNonDeterministic()) {
			System.err.println("Cannot run an non-deterministic machine");
			return;
		}
		this.running = true;
		this.state = table.getStart();
	}
	
	public boolean consume(char c) {
		if(!running)
			return false;
		
		if(!table.canConsume(c))
			return running = false;
		
		state = table.next(state, c);
		if(State.isInvalidState(state))
			running = false;
		return running;
	}

	@Override
	public String toString() {
		return table.getId();
	}
}
