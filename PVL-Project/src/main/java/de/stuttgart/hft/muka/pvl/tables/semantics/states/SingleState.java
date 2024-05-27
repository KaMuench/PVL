package de.stuttgart.hft.muka.pvl.tables.semantics.states;

import java.util.Set;

public class SingleState extends State {
	

	private int id;

	public SingleState(String image) {
		this.id = Integer.parseInt(image);
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public Set<Integer> getIds() {
		return Set.of(id);
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}
	
}
