package de.stuttgart.hft.muka.pvl.tables.semantics.states;

public class EndState extends SingleState {

	public EndState(String image) {
		super(image.substring(0, image.length() - 1));
	}

	@Override
	public boolean isEnd() {
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "e";
	}

}
