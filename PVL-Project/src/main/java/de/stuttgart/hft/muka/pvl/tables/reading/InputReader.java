package de.stuttgart.hft.muka.pvl.tables.reading;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputReader {
	
    private static final Pattern CSV_DELIMITER = Pattern.compile(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    private static final Pattern QUOTED = Pattern.compile("\"(.*)\"");

    private static final String DOUBLE_QUOTE = "\"\"";
	private static final String SINGLE_QUOTE = "\"";
	
	private InputStream is;
	private BufferedReader br = null;
	private boolean isCsv = false;
	private String line = null;
	
	private InputReader(InputStream is, boolean isCsv) {
		this.is = is;
		this.isCsv = isCsv;
		Reader reader = new InputStreamReader(is);
		this.br = new BufferedReader(reader);
		try {
			this.line = process(br.readLine());
			if(this.line == null)
				close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static InputReader fromFile(String filename) {
		try {
			return new InputReader(new FileInputStream(filename), filename.endsWith(".csv"));
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static InputReader fromString(String text) {
		return new InputReader(new StringInputStream(text), false);
	}

	private String process(String line) {
		if(line == null)
			return null;

		if(!isCsv)
			return line;

	    String[] cells = CSV_DELIMITER.split(line);
	    
	    for(int i = 0; i < cells.length; i++) {
	    	String str = cells[i];
			Matcher m = QUOTED.matcher(str);
			if(m.matches()) {
				str = m.group(1);
				str = str.replace(DOUBLE_QUOTE, SINGLE_QUOTE);
				cells[i] = str;
			}
	    }
	    if(cells.length == 0)
	    	return "";
	    
	    StringBuilder sb = new StringBuilder(cells[0]);
	    for(int i = 1; i < cells.length; i++)
	    	sb.append('\t').append(cells[i]);

		return sb.toString();
	}

	private void close() {
		if(is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			is = null;
			br = null;
		}		
	}
	
	public String readLine() {
		String current = this.line;
		if(this.line != null)
			try {
				this.line = process(br.readLine());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		if(this.line == null)
			this.close();
		return current;
	}
}
