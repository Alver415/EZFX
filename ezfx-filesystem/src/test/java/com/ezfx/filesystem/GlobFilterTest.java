package com.ezfx.filesystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GlobFilterTest {

	@Test
	public void basic() {
		GlobFilter filter = GlobFilter.parse("match", "another");

		assertTrue(filter.test("match"));
		assertTrue(filter.test("MATCH")); //Windows case-insensitive

		assertTrue(filter.test("another"));

		assertFalse(filter.test("non-match"));
	}

	@Test
	public void empty() {
		GlobFilter filter = GlobFilter.parse("");

		assertFalse(filter.test("nothing"));
		assertFalse(filter.test("matches"));
	}


	@Test
	public void negation() {
		GlobFilter filter = GlobFilter.parse("!no_match", "yes_match");

		assertFalse(filter.test("no_match"));
		assertTrue(filter.test("yes_match"));
	}


	@Test
	public void multiline() {
		GlobFilter commaSeparated = GlobFilter.parse("a", "!b", "c/d");
		GlobFilter multiLineString = GlobFilter.parse("""
				a
				!b
				c/d
				""");
		assertEquals(commaSeparated, multiLineString);
	}

}
