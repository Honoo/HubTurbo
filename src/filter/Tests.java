package filter;

import static org.junit.Assert.*;

import org.junit.Test;

public class Tests {

	@Test
	public void basics() {
		assertEquals(Parser.parse(null), null);
		assertEquals(Parser.parse(""), null);
	}
	
	@Test
	public void predicates() {
		assertEquals(Parser.parse("a(b)"), new Predicate("a", "b"));
		assertEquals(Parser.parse("    a   (   b   )   "), new Predicate("a", "b"));
		assertEquals(Parser.parse("a(dar ius)"), new Predicate("a", "dar ius"));

		try {
			Parser.parse("c a(b)");
			fail("c is a predicate without parentheses -- that should fail");
		} catch (ParseException e) {}
	}
	
	@Test
	public void operators() {
		assertEquals(Parser.parse("a(b) or c(d)"), new Disjunction(new Predicate("a", "b"), new Predicate("c", "d")));
		assertEquals(Parser.parse("a(b) | c(d)"), new Disjunction(new Predicate("a", "b"), new Predicate("c", "d")));
		assertEquals(Parser.parse("a(b) || c(d)"), new Disjunction(new Predicate("a", "b"), new Predicate("c", "d")));

		assertEquals(Parser.parse("a(b) c(d)"), new Conjunction(new Predicate("a", "b"), new Predicate("c", "d")));
		assertEquals(Parser.parse("a(b) and c(d)"), new Conjunction(new Predicate("a", "b"), new Predicate("c", "d")));
		assertEquals(Parser.parse("a(b) & c(d)"), new Conjunction(new Predicate("a", "b"), new Predicate("c", "d")));
		assertEquals(Parser.parse("a(b) && c(d)"), new Conjunction(new Predicate("a", "b"), new Predicate("c", "d")));

		assertEquals(Parser.parse("!a(b)"), new Negation(new Predicate("a", "b")));
		assertEquals(Parser.parse("-a(b)"), new Negation(new Predicate("a", "b")));
		assertEquals(Parser.parse("~a(b)"), new Negation(new Predicate("a", "b")));
	}
	
	@Test
	public void associativity() {
		assertEquals(Parser.parse("a:b or c:d or e(f)"),
				new Disjunction(new Disjunction(new Predicate("a", "b"), new Predicate("c", "d")), new Predicate("e", "f")));

		assertEquals(Parser.parse("a(b) and c:d and e(f)"),
				new Conjunction(new Conjunction(new Predicate("a", "b"), new Predicate("c", "d")), new Predicate("e", "f")));
	}
	
	@Test
	public void precedence() {
		assertEquals(Parser.parse("a(b) or c(d) and e(f)"),
				new Disjunction(new Predicate("a", "b"), new Conjunction(new Predicate("c", "d"), new Predicate("e", "f"))));
		assertEquals(Parser.parse("~a(b) or c(d) and e(f)"),
				new Disjunction(new Negation(new Predicate("a", "b")), new Conjunction(new Predicate("c", "d"), new Predicate("e", "f"))));
		
		assertEquals(Parser.parse("a(b) ~c(d)"), new Conjunction(new Predicate("a", "b"), new Negation(new Predicate("c", "d"))));
	}

	@Test
	public void grouping() {
		assertEquals(Parser.parse("(a(b) or c(d)) and e(f)"),
				new Conjunction(new Disjunction(new Predicate("a", "b"), new Predicate("c", "d")), new Predicate("e", "f")));
		assertEquals(Parser.parse("(a(b) or c(d)) e(f)"),
				new Conjunction(new Disjunction(new Predicate("a", "b"), new Predicate("c", "d")), new Predicate("e", "f")));
		assertEquals(Parser.parse("e(f) ~(a(b) or c(d))"),
				new Conjunction(new Predicate("e", "f"), new Negation(new Disjunction(new Predicate("a", "b"), new Predicate("c", "d")))));
	}
	
	@Test
	public void colon() {
		assertEquals(Parser.parse("assignee:darius"),
				new Predicate("assignee", "darius"));
		assertEquals(Parser.parse("assignee:    darius   "),
				new Predicate("assignee", "darius"));
		assertEquals(Parser.parse("assignee:dar ius(one)"),
				new Conjunction(new Predicate("assignee", "dar"), new Predicate("ius", "one")));
		
	}
}
