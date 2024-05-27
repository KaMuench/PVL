package de.stuttgart.hft.muka.pvl.tables.semantics.table;

import java.util.*;
import java.util.stream.IntStream;

import de.stuttgart.hft.muka.pvl.tables.semantics.expr.Char;
import de.stuttgart.hft.muka.pvl.tables.semantics.expr.Expr;
import de.stuttgart.hft.muka.pvl.tables.semantics.states.*;
import de.stuttgart.hft.muka.pvl.tables.semantics.symbols.SemanticException;


public class Table {
	
	private final String id;
	private final List<Expr> header;
	private final List<Transition> transitions;
	
	private final int start;
	private final Set<Integer> ends;

	StringBuilder bd = new StringBuilder();

	private int colEps;
	
	
	public Table(String id, List<Expr> header, List<Transition> transitions) throws SemanticException {
		this.id = id;
		checkRanges(header);
		this.header = header;
		this.colEps = header.indexOf(Char.of("\\e"));
		
		checkStates(transitions);
		this.transitions = transitions;
		
		checkMarkups(transitions);
		this.start = getStart(transitions);
		this.ends = getEnds(transitions);
	}
	
	private void checkRanges(List<Expr> header) throws SemanticException {
		List<Set<Character>> contained = new ArrayList<>();
		for(Expr e : header) {
			Set<Character> chars = new TreeSet<>();
			for(int i = 0; i < 256; i++) {
				char c = (char)i;
				if(e.includes(c))
					chars.add(c);
			}
			contained.add(chars);
		}
		boolean error = false;
		for(int i = 0; i < contained.size(); i++) {
			Set<Character> cs1 = contained.get(i);
			for(int k = 0; k < contained.size(); k++) {
				Set<Character> cs2 = contained.get(k);
	
				if(i < k) 
					for(Character c : cs1)
						if(cs2.contains(c)) {
							System.err.println(header.get(i) + " and " + header.get(k) + " have commonon elements");
							error = true;
							break;
						}
			}
		}
		if(error)
			throw new SemanticException("FSM " + id + " must not have common transition elements");
	}

	private void checkMarkups(List<Transition> transitions) throws SemanticException {
		Set<Integer> starts = new TreeSet<>();
		Set<Integer> ends = new TreeSet<>();

		for(Transition t : transitions) {
			if(t.from().isStart())
				starts.add(t.from().getId());
			if(t.from().isEnd())
				ends.add(t.from().getId());
		}
		boolean error = false;
		if(starts.size() == 0) {
			System.err.println("No start defined for " + id);
			error = true;
		}
		if(starts.size() > 1) {
			System.err.println("Multiple starts defined for " + id);
			error = true;
		}
		
		
		if(ends.size() == 0) {
			System.err.println("No ends defined for " + id);
			error = true;
		}
		
		if(error)
			throw new SemanticException("FSM " + id + " wrongly set starts and ends");
	}

	private void checkStates(List<Transition> transitions) throws SemanticException {
		Set<Integer> defined = new TreeSet<>();
		boolean error = false;
		for(Transition t : transitions) {
			int id = t.from().getId();
			if(defined.contains(id)) {
				System.err.println("FSM " + this.id + " defines " + id + " more than once");
				error = true;
			}else
				defined.add(id);
		}
		
		for(Transition t : transitions) {
			for(State s : t.to()) {
				Set<Integer> ids = s.getIds();
				for(Integer id : ids)
					if(!s.isNoState() && !defined.contains(id)) {
						System.err.println("FSM " + this.id + " undefined " + id);
						error = true;
					}
			}
		}
		
		if(error)
			throw new SemanticException("FSM " + id + " wrongly defined ids");
	}
	
	private int getStart(List<Transition> transitions) {
		return transitions.stream()
				.filter(t -> t.from().isStart())
				.map(t -> t.from().getId())
				.findFirst().get();
	}

	private Set<Integer> getEnds(List<Transition> transitions) {
		List<Integer> ends = transitions.stream()
				.filter(t -> t.from().isEnd())
				.map(t -> t.from().getId())
				.toList();
		
		return new TreeSet<>(ends);
	}

	public List<Expr> getHeader() {
		return header;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public String getId() {
		return id;
	}
	
	public boolean isDeterministic() {
		for(Expr e : header)
			if(e.isEpsilon())
				return false;
		
		for(Transition e : transitions)
			for(State s : e.to())
				if(s.getIds().size() != 1)
					return false;
		
		return true;
	}
	
	public boolean isNonDeterministic() {
		return !isDeterministic();
	}
	
	public int getStart() {
		return start;
	}

	public boolean isEnd(int state) {
		return ends.contains(state);
	}
	
	public boolean canConsume(char c) {
		for(Expr e : header)
			if(e.includes(c))
				return true;
		return  false;
	}
	
	public int next(int state, char c) {
		int pos = -1;
		for(int i = 0; i < header.size(); i++) {
			Expr e = header.get(i);
			if(e.includes(c)) {
				pos = i;
				break;
			}
		}
		
		if(pos == -1)
			return State.invalidState();
		
		for(Transition e : transitions) 
			if(e.from().getId() == state) 
				return e.to().get(pos).getId();
		
		return State.invalidState();	
	}


	/*
	 * Diese Funktion wandelt den NEA in einen DEA um. Dabei erstellt sie eine Tabelle mit integer sets, die die States beinhalten.
	 * Diese Tabelle ist die Zustandstabelle des DEA der die selbe Sprache akzeptiert wie der NEA.
	 * Für jede Menge in der ersten Spalte werden die Zustandsmengen für die jeweiligen Zeichen des header erstellt.
	 * Dabei geht der Algorithmus für jede Zeile die Menge der ersten Spalte durch und "berechnet" für jeden Zustand in dem Set die möglichen Folgezustände
	 * für die nächste Spalte. Ergibt sich eine neue Folgezustandsmenge, die noch nicht in der ersten Spalte enthalten ist, wird diese in die
	 * erste Spalte eingefügt. So wächst die erste Spalte, und damit die gesamte Tabelle, während des Durchlaufs.
	 *
	 * Die Folgezustandsberechnung für eine Zustandsmenge erfolgt rekursiv und wird auf zwei Methoden aufgeteilt.
	 *
	 *  Set<Ineger> getState(S,C)
	 *  Wenn es für Zustand S und Zeichen C einen Eintrag in der Tabelle gibt, wird dieser Eintrag in das Set, welches getState zurückgibt hinzugefügt
	 * 	und es werden außerdem für jeden Zustand dieses Eintrags, die Methode getPostEps(...) aufgerufen.
	 *  Wenn es keinen Eintrag für S/C gibt, also der Eintrag ein NonState gibt die Methode ein leeres Set zurück.
	 *
	 *  Set<Ineger> getPostEps(S)
	 * 	Diese Methode schaut für den Zustande S nach, ob von diesem Zustand aus weiter Zustände via Epsilonübergängen erreichbar sind.
	 *  Das passiert in dem getPostEps für jeden Zustand im Eintrag S/eps aufgerufen wird. Hier findet eine Rekursion statt. Diese rekursiven
	 *  Aufrufe passieren so lange, bis ein Eintrag S/eps NonState ist. Die Methode getPostEps, liefert ein Set mit allen Zuständen die von Zustand S
	 *  via Epsilonübergängen erreichbar sind.
	 *
	 *  Der Algorithmus startet, damit getPostEps(start) aufzurufen und die Rückgabe in die erste Spalte der neuen Tabelle hinzuzufügen.
	 *  Damit werden alle Zustände die von dem Startzustand des NEA via Epsilonübergänge erreichbar sind als neuen Startzustand festgelegt.
	 *  Außerdem wird diesen neuen Eintrag der Zustand start des NEA mit übergeben.
	 *  Danach erfolgt der eigentliche Algorithmus, der in einer doppelten for Schleife für jeden Eintrag der ersten Spalte
	 *  der neuen Tabelle die Folgezustände berechnet und in die neue Tabel einfügt.
	 *
	 *  Es wurde weiterhin eine neue Klasse StartEndState erstellt, da der NEA im samples2.txt einen DEA erzeugt dessen
	 *  Startzustand gleichzeitig Endzustand ist.
	 *
	 *  Leider scheint in der Statemachine ein Bug drinnen zu sein, weswegen das Ausführen der StateMachine mit der DEA
	 *  Tabelle nicht ganz funktioniert. Bzw. werden auch Wörter akzeptiert, die in der Sprachen nicht vorkommen.
	 *  Deswegen kann man z.B. auch bei dem "integer" Automaten in samples1.txt die Eingabe 1234aaa ausgeführt werden, und das Wort
	 *  wird akzeptiert.
	 */
	public Table toDEA() {

		Expr headerArray[] = header.stream().filter(h->!h.isEpsilon()).toArray(Expr[]::new);

		List<List<Set<Integer>>> newTable = new ArrayList<>();									// Neue Tabelle mit Anzahl Spalten wie header ohne eps Spalte
		newTable.add(new ArrayList<>());														// Erste Spalte
		for(Expr entry : headerArray) newTable.add(new ArrayList<>());							// Die anderen Spalten
		Set<Integer> startSet = new HashSet<>();
		startSet.add(start);																	// Startzustand des NEA in neuen Startzustand hinzufügen
		startSet.addAll(getPostEps(start));														// Alle Zustände die von start via epsilon erreichbar sind hinzufügen
		newTable.get(0).add(startSet);															// Neuen Startzustand in er ste Spalte der neuen Tabelle einfügen


		// Für jede Zeile der ersten Spalte
		// Die erste Spalte und damit die Tabelle, wächst mit jeden Durchlauf der äußeren Schleife,
		// falls ein neues Set in die Tabelle hinzugefügt wird.
		for(int row=0;row< newTable.get(0).size();row++) {
			IntStream.range(1, newTable.size()).forEach(x->newTable.get(x).add(new HashSet<>()));					// Für jede Spalte der aktuellen Reihe ein HashSet hinzufügen
			for(Integer j : newTable.get(0).get(row).stream().toList()){											// Für jeden Zustand S aktuellen Reihe in der ersten Spalte der neuen Tabelle
				for(int col=1;col< newTable.size();col++) {															// Jede Spalte C der alten Tabelle durchgehen und
					newTable.get(col).get(row).addAll(getStates(j, headerArray[col-1]));							// die Folgezustände für S und C in die neue Tabelle einfügen
					}
			}
			// Neues Set in erste Spalte der DEA Tabelle hinzufügen, wenn es dieses dort noch nicht gibt und es nicht leer ist
			for(int col=1;col< newTable.size();col++) {
				Set<Integer> set = newTable.get(col).get(row);
				if(!newTable.get(0).contains(set) && set.size() != 0) newTable.get(0).add(set);
			}

			// Nur die Ausgabe der aktuellen Reihe der DEA Tabelle
			System.out.println("\n");
			for(int col=0;col< newTable.size();col++) System.out.printf("%-12s", newTable.get(col).get(row).toString());
			System.out.println("\n");
		}

		// Komplette Tabelle ausgeben
		System.out.printf("%n%-12s", " ");														
		header.stream().filter(h->!h.isEpsilon()).forEach(s-> System.out.printf("%-12s",s));
		System.out.println();
		for(int row=0;row< newTable.get(0).size();row++) {
			for(int col=0;col<newTable.size();col++) {
				System.out.printf("%-12s",newTable.get(col).get(row));
			}
			System.out.println();
		}

		/*
		 * Umwandlung der erstellten Set-Tabelle in die Klasse Table.
		 */
		List<Transition> newTransitions = new ArrayList<>();
		for(int row=0;row< newTable.get(0).size();row++) {
			List<State> newStates = new ArrayList<>();
			SingleState from = null;



			// Übeprüfen ob der neue From Zustand eine Startzustand
			if(newTable.get(0).get(row).contains(start)) from = new StartState(String.valueOf(row)+"s");
			for(Integer end : ends) {
				if(newTable.get(0).get(row).contains(end)) {
					// Start und Endzustand
					if(from != null) from = new StartEndState((StartState) from);
					// Endzustand
					else from = new EndState(String.valueOf(row)+"e");
					break;
				}
			}
			//Normaler Zustand sein soll
			if(from == null) from = new SingleState(String.valueOf(row));

			// Jede Spalte durchgehen und Eintrag für row/col in einen SingleState transformieren
			for (int col = 1; col < newTable.size(); col++) {

				if(newTable.get(col).get(row).size() == 0) newStates.add(NoState.instance());
				else newStates.add(
						new SingleState(
								//Index für den State, ist die Zeilennummer der ersten Spalte in dem das Set auch auftaucht
								String.valueOf(newTable.get(0).indexOf(newTable.get(col).get(row)))
						)
				);
			}
			// Einfügen der SingleStates Liste und From State als neue Transition in die Transition Liste
			newTransitions.add(new Transition(from, newStates));
		}

		// Erstellen einer neuen Tabelle mit den neuen Transitions, dem alten header, ohne epsilon Spalte und der selben ID.
		try {
			Table t =  new Table(id, header.stream().filter(h->!h.isEpsilon()).toList(), newTransitions);
			return t;
		} catch (SemanticException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * Function to get all states for epsilon transitions, after transition for c is already found
	 */

	private Set<Integer> getPostEps(int state) {
		TreeSet<Integer> list = new TreeSet<>();

		System.out.printf("%sgetPostEps(%d)%n", bd.toString(), state);
		bd.append("\t");

		for (Transition t : transitions) {
			if (t.from().getId() == state) {
				if(t.to().get(colEps) != NoState.instance()) {
					// Für jeden Eintrag states/eps in der NEA Tabelle, die Methode getPostEps aufrufen und den Eintrag in
					// rückgabe Set hinzufügen
					for(int s : t.to().get(colEps).getIds()) {
						list.add(s);
						if(s != state) list.addAll(getPostEps(s));
					}
				}
			}
		}

		bd.deleteCharAt(bd.length()-1);
		System.out.printf("%sreturn %s%n", bd.toString(),list);
		return list;
	}

	/*
	 * Get the states for the transition from state with expression c
	 */
	private Set<Integer> getStates(int state, Expr c) {
		TreeSet<Integer> list = new TreeSet<>();

		int col = header.indexOf(c);

		System.out.printf("%sgetStates(%d, %s)%n", bd.toString(), state, c);
		bd.append("\t");

		for (Transition t : transitions) {
			if (t.from().getId() == state) {
				// Falls der Eintrag s/c in der NEA Tabelle nicht NonState ist
				if (t.to().get(col) != NoState.instance()) {
					list.addAll(t.to().get(col).getIds());								// Alle Zustände in s/c als Folgezustände in Set hinzufügen
					for (int s : t.to().get(col).getIds()) list.addAll(getPostEps(s));	// Und für alle Folgezustände getPostEps aufrufen
				}
			}
		}


		bd.deleteCharAt(bd.length()-1);
		System.out.printf("%sreturn %s%n", bd.toString(),list);
		return list;
	}

	private String toString = null;
	
	@Override
	public String toString() {
		if(toString != null)
			return toString;
		
		Object[][] table = new String[transitions.size() + 1][header.size() + 1];
		
		{
			table[0][0] = id;
			int k = 1;
			for(Expr e : header)
				table[0][k++] = e.toShortString();
		}
		{
			int i = 1;
			for(Transition t : transitions) {
				table[i][0] = t.from().toString();
				int k = 1;
				for(State s : t.to())
					table[i][k++] = s.toString();
				i++;
			}
		}
		
		int max0 = 0;
		for(int i = 0; i < table.length; i++)
			if(max0 < table[i][0].toString().length())
				max0 = table[i][0].toString().length();
		
		int max = 0;
		for(int i = 0; i < table.length; i++)
			for(int k = 1; k < table[i].length; k++)
				if(max < table[i][k].toString().length())
					max = table[i][k].toString().length();
		max++;
		
		String fmt = "%" + max0 + "s |";
		for(int i = 1; i < table[0].length; i++)
			fmt = fmt + "%" + max + "s |";
		fmt = fmt + "%n";
		
		
		StringBuilder sb = new StringBuilder(String.format(fmt, table[0]));
		for(int i = 0; i < max0; i++)
			sb.append('-');
		sb.append("-+");
		
		for(int i = 1; i < table[0].length; i++) {
			for(int k = 0; k < max; k++)
				sb.append('-');
			sb.append("-+");
		}
		
		sb.append('\n');
		
		for(int i = 1; i < table.length; i++)
			sb.append(String.format(fmt, table[i]));
		
		toString = sb.toString();
		return toString;		
	}

}
