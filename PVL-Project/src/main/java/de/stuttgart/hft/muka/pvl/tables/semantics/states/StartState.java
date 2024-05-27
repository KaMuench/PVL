package de.stuttgart.hft.muka.pvl.tables.semantics.states;

public class StartState extends SingleState {

	public StartState(String image) {
		super(image.substring(0, image.length() - 1));
	}

	@Override
	public boolean isStart() {
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "s";
	}

}
