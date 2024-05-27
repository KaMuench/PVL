package de.stuttgart.hft.muka.pvl.tables.semantics.states;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StateSet extends State {

	private int id;
	private final Set<Integer> ids;

	public StateSet(String image, int id) {
		List<Integer> ids = Arrays.stream(image.split("\\|")).map(s -> Integer.valueOf(s)).toList();
		this.ids = new TreeSet<>(ids);
		this.id = id;
	}
	public StateSet(String image) {
		List<Integer> ids = Arrays.stream(image.split("\\|")).map(s -> Integer.valueOf(s)).toList();
		this.ids = new TreeSet<>(ids);
	}


	@Override
	public int getId() {
		return this.id;
	}


	public Set<Integer> getIds() {
		return ids;
	}

	public String toString() {
		return ids.toString();
	}

}
