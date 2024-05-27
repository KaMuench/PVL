package de.stuttgart.hft.muka.pvl.tables.scanner;

import de.stuttgart.hft.muka.pvl.tables.scanner.TokenType.Result;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


class TokenTypeTest {
	
	private void assertAccepted(TokenType type, String input, String expected) {
		Result result = type.match(input);
		
		assertTrue(result.matched());		
		assertEquals(expected, result.image());
		
		String remainder = input.substring(result.end());		
		assertTrue(remainder.isEmpty() || !remainder.substring(0, 1).isBlank());
	}
	
	private void assertRejected(TokenType type, String input) {
		Result result = type.match(input);
		assertFalse(result.matched());
	}

	
	@ParameterizedTest
	@ValueSource(strings = {"def", "def ", " def", "  def", "def  ", "def some other text", "\tdef\t", " def\t some other text"})
	void testDefAccepts(String input) {
		assertAccepted(TokenType.DEF, input, "def");
	}

	@ParameterizedTest
	@ValueSource(strings = {"defx"})
	void testDefAcceptsPrefix(String input) {
		Result result = TokenType.DEF.match(input);
		assertTrue(result.matched());		
		assertTrue(result.length() < input.length());
	}

	@ParameterizedTest
	@ValueSource(strings = {"abc", "de", "fsm"})
	void testDefRejects(String input) {
		assertRejected(TokenType.DEF, input);
	}
	
	@Test
	void testFsmAccepts() {
		assertAccepted(TokenType.FSM, "fsm\t", "fsm");
	}

	@ParameterizedTest
	@CsvSource({"abc, abc", "' abc', abc", "' abc ', abc", "'\tabc\t', abc", "ab, ab",
		"def, def", "defx, defx"})
	void testIdAccepts(String input, String expected) {
		assertAccepted(TokenType.ID, input, expected);
	}


	@ParameterizedTest
	@CsvSource({"a, a", "\\a, \\a", "[, [", "\\0, \\0", "'\\ ', '\\ '", "\\\\, \\\\", "' \\\\ ', \\\\"})
	void testCharAccepts(String input, String expected) {
		assertAccepted(TokenType.CHAR, input, expected);
	}

	@ParameterizedTest
	@ValueSource(strings = {"#", "0", " ", "\\"})
	void testCharRejects(String input) {
		assertRejected(TokenType.CHAR, input);
	}

	@ParameterizedTest
	@CsvSource({"[a], [a]", "[ab], [ab]", "[abc], [abc]",
		"[a-b], [a-b]", "[a-bx], [a-bx]", "[xa-bx], [xa-bx]", 
		"[xa-bc-d], [xa-bc-d]", "[xa-bc-dx], [xa-bc-dx]", "[xa-bxc-dx], [xa-bxc-dx]",
		"[\\a], [\\a]", "[\\--+], [\\--+]", "[+\\-*/], [+\\-*/]",
		"~[a], ~[a]", "~[a-b], ~[a-b]"})
	void testRangeAccepts(String input, String expected) {
		assertAccepted(TokenType.RANGE, input, expected);
	}

	@ParameterizedTest
	@ValueSource(strings = {"[a", "[a-]"})
	void testRangeRejects(String input) {
		assertRejected(TokenType.RANGE, input);
	}

	@ParameterizedTest
	@ValueSource(strings = {"0s", "0S", "0123s"})
	void testStartStateAccepts(String input) {
		assertAccepted(TokenType.START_STATE, input, input);
	}

	@ParameterizedTest
	@ValueSource(strings = {"0e", "0E", "0123e"})
	void testEndStateAccepts(String input) {
		assertAccepted(TokenType.END_STATE, input, input);
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "12", "123", "-"})
	void testSingleStateAccepts(String input) {
		assertAccepted(TokenType.SINGLE_STATE, input, input);
	}

	@ParameterizedTest
	@ValueSource(strings = {"0|1", "12|345", "12|345|6789"})
	void testStateSetAccepts(String input) {
		assertAccepted(TokenType.STATE_SET, input, input);
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "12", "123", "-", "1|", "1|-"})
	void testStateSetRejects(String input) {
		assertRejected(TokenType.STATE_SET, input);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "# some text", "   # some text"})
	void testEolAccepts(String input) {
		assertAccepted(TokenType.EOL, input, input.trim());
	}

	@ParameterizedTest
	@ValueSource(strings = {"\u0003"})
	void testEoiAccepts(String input) {
		assertAccepted(TokenType.EOI, input, input);
	}

	@Test
	void testUnmatched() {
		assertAccepted(TokenType.UNMATCHED, "some remaining text   ", "some");
	}
}
