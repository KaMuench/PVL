package de.stuttgart.hft.muka.pvl.tables.semantics.symbols;

import java.util.ArrayList;
import java.util.List;

import de.stuttgart.hft.muka.pvl.tables.semantics.expr.Constant;
import de.stuttgart.hft.muka.pvl.tables.semantics.table.Table;

public class Symbols {
	
	private boolean verbose = false;
	
	private List<Constant> constants = new ArrayList<>();
	private List<Table> tables = new ArrayList<>();
	
	private Constant lookupConstant(String id) {
		for(Constant c : constants)
			if(c.getId().equals(id))
				return c;
		return null;
	}

	private Table lookupTable(String id) {
		for(Table t : tables)
			if(t.getId().equals(id))
				return t;
		return null;
	}

	public void add(Constant constant) throws SemanticException {
		String id = constant.getId();
		if(lookupConstant(id) != null)
			throw new SemanticException(id + " already defined");
		
		constants.add(constant);
		
		if(verbose)
			System.out.println("added constant " + constant);
	}
	
	public void add(Table table) throws SemanticException {
		String id = table.getId();
		if(lookupTable(id) != null)
			throw new SemanticException(id + " already defined");
		
		tables.add(table);
		
		if(verbose)
			System.out.println("added table " + table);
	}
	
	public Constant getConstant(String id) throws SemanticException {
		Constant c = lookupConstant(id);
		if(c == null)
			throw new SemanticException(id + " not defined");

		return c;
	}
	
	public Table getTable(String id) throws SemanticException {
		Table t = lookupTable(id);
		if(t == null)
			throw new SemanticException(id + " not defined");

		return t;
	}
	
	public List<Table> getTables() {
		return tables;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Defintions\n==========\n");
		for(Constant c : constants)
			sb.append(c).append('\n');
		
		sb.append("\nState Machine Tables\n=====================\n");
		for(Table t : tables)
			sb.append(t.toString() + "\n").append('\n');
		return sb.toString();
	}

}
