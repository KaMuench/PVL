package de.stuttgart.hft.muka.pvl.tables.semantics.table;

import java.util.List;

import de.stuttgart.hft.muka.pvl.tables.semantics.states.SingleState;
import de.stuttgart.hft.muka.pvl.tables.semantics.states.State;

public record Transition (SingleState from, List<State> to) {
}

