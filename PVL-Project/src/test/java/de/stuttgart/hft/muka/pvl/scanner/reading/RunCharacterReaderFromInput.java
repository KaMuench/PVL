package de.stuttgart.hft.muka.pvl.scanner.reading;

import de.stuttgart.hft.muka.pvl.scanner.reading.CharacterReader;

public class RunCharacterReaderFromInput {

	public static void main(String[] args) {
		CharacterReader s = new CharacterReader(System.in);
		char c;
		do{
			c = s.nextChar();
			System.out.println((int)c + " " + c);
		} while(c != 0);

	}

}
