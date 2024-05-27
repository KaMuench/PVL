package de.stuttgart.hft.muka.pvl.tables.semantics.expr;

import java.util.ArrayList;
import java.util.List;

import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.SemanticException;

public class Range extends Expr {
	
	private boolean inclusive = true;
	private List<Segment> segments = new ArrayList<>();
	
	public Range(String image) throws SemanticException {
		parse(image);
	}
		
	private void parse(String image) throws SemanticException {
		int from = 0, to =  image.length() - 1;
		if(image.startsWith("~")) {
			inclusive = false;
			from = 1;
		}
		
		from++;
		
		String prev = null;
		boolean intervall = false;
		
		for(int i = from; i < to; i++) {
			char c = image.charAt(i);

			String chr = null;
			if(c != '\\')
				chr = image.substring(i, i + 1);
			else
				chr = image.substring(i, (++i + 1));
			
			if(intervall) {
				segments.add(new Intervall(prev, chr));
				prev = null;
				intervall = false;
				continue;
			}
			
			if(image.charAt(i + 1) == '-') { // peek for intervall
				prev = chr;
				intervall = true;
				i++;
				continue;
			}
			
			segments.add(new Char(chr));
		}
		
	}



	private static abstract class Segment {
		
		abstract boolean includes(char c);
		
		char toChar(String chr) {
			if(chr.length() == 1)
				return chr.charAt(0);
			
			// otherwise: \x			
			char c = chr.charAt(1);
			switch(c) {
			case 'n': return '\n';
			case 'r': return '\r';
			case 'f': return '\f';
			case 't': return '\t';
			case 'b': return '\b';
			case '\\': return '\\';
			default: return c;
			}
		}
	}
	
	private static class Intervall extends Segment {		
		char from, to;
		String fromStr, toStr;
		Intervall(String from, String to) {
			this.fromStr = from;
			this.toStr = to;
			this.from = toChar(from);
			this.to = toChar(to);
		}

		@Override
		boolean includes(char c) {
			return from <= c && c <= to;
		}

		@Override
		public String toString() {
			return fromStr + "-" + toStr;
		}	
	}
	
	private static class Char extends Segment {		
		char c;
		String cStr;
		Char(String c) {
			this.cStr = c;
			this.c = toChar(c);
		}

		@Override
		boolean includes(char c) {
			return this.c == c;
		}	
		
		@Override
		public String toString() {
			return cStr;
		}	
	}
	
	@Override
	public boolean includes(char c){
		boolean included = false;
		for(Segment s : segments)
			if(s.includes(c)) {
				included = true;
				break;
			}
			
		if(inclusive)
			return included;
		else
			return !included;
	}	

	@Override
	public boolean isEpsilon() {
		return inclusive && segments.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(inclusive ? "[" : "~[");
		for(Segment s : segments)
			sb.append(s);
		sb.append("]");
		return sb.toString();
	}

}
