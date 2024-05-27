package de.stuttgart.hft.muka.pvl.scanner.reading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CharacterReader {
	
	public static final char EOI = 0;
	
	private BufferedReader buffer;
	private boolean done = false;
	
	private StringBuilder characterBuffer = new StringBuilder();
	private int startPos = 0, pos = 0;
	
	private List<Integer> lines = new ArrayList<>();
	
	public CharacterReader(InputStream in) {
		super();
		Reader reader = new InputStreamReader(in);
		buffer = new BufferedReader(reader);
	}
	
	private void readBuffer(){
		try {
			String line = buffer.readLine();
			if(line == null)
				done = true;
			else{
				characterBuffer.append(line);
				characterBuffer.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
			done = true;
		}
	}
	
	public char nextChar(){
		if(done)
			return EOI;
		if(pos == characterBuffer.length()){
			readBuffer();
			if(done){
				return EOI;
			}
		}
		char c = characterBuffer.charAt(pos++);
		if(c == '\n' && !lines.contains(pos))
			lines.add(pos);
		return c;
	}

	public String getPosition(){
		int line = 0;
		for(; line < lines.size(); line++){
			int nlPos = lines.get(line);
			if(pos > nlPos){
				line--;
				break;
			}
		}
		return (line + 1) + ":" + (pos + 1);
	}
	
	public String getSegment(){
		int from, to;
		
		if(startPos - 10 > 0)
			from = startPos - 10;
		else
			from = 0;
		
		if(startPos + 10 < characterBuffer.length())
			to = startPos + 10;
		else
			to = startPos + 1;		
		
		return "... " + characterBuffer.subSequence(from, to) + " ...";
	}

	public String acceptLexem(int length) {
		String lexem;
		if(startPos + length < characterBuffer.length())
			lexem = characterBuffer.substring(startPos, startPos + length);
		else
			lexem = String.valueOf(EOI);
		startPos = startPos + length;
		pos = startPos;
		return lexem;
	}
}
