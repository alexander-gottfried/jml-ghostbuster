package de.seg.ghostbuster;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Mu
{
	static Mu.EmptySet emptySet() {
		return new Mu.EmptySet();
	}

	static Mu.Epsilon epsilon() {
		return new Mu.Epsilon();
	}

	static Mu.Terminal terminal(char t) {
		return new Mu.Terminal(t);
	}

	static Mu.RecVar recvar(int id) {
		return new Mu.RecVar(id);
	}

	static Mu.Concatenation concatenation(Mu.Expr left, Mu.Expr right) {
		return new Mu.Concatenation(left, right);
	}

	static Mu.Alternation alternation(Mu.Expr left, Mu.Expr right) {
		return new Mu.Alternation(left, right);
	}

	static Mu.FixPoint fixpoint(int boundId, Mu.Expr body) {
		return new Mu.FixPoint(boundId, body);
	}

	public static abstract class Expr {
		Set<Integer> unboundIds() {
			HashSet<Integer> result = new HashSet<>();
			HashSet<Integer> alreadySet = new HashSet<>();
			this.findUnboudIds(result, alreadySet);
			return result;
		}

		protected String toString(boolean paren) {
			return this.toString();
		}

		private void findUnboudIds(Set<Integer> result, Set<Integer> alreadySet) {
			if (this instanceof EmptySet
					|| this instanceof Epsilon
					|| this instanceof Terminal)
			{
				return;
			}

			if (this instanceof Concatenation c) {
				HashSet<Integer> copy = new HashSet<>(alreadySet);
				c.left.findUnboudIds(result, alreadySet);
				c.right.findUnboudIds(result, copy);
			}
			else if (this instanceof Alternation a) {
				HashSet<Integer> copy = new HashSet<>(alreadySet);
				a.left.findUnboudIds(result, alreadySet);
				a.right.findUnboudIds(result, copy);
			}
			else if (this instanceof RecVar r) {
				if (!alreadySet.contains(r.id))
					result.add(r.id);
			}
			else if (this instanceof FixPoint fp) {
				alreadySet.add(fp.boundId);
				fp.body.findUnboudIds(result, alreadySet);
			}
		}
	}

	public static class EmptySet
		extends Mu.Expr
	{
		private EmptySet() {}

		public String toString() {
			return "∅";
		}
	}

	public static class Epsilon
		extends Mu.Expr
	{
		private Epsilon() {}

		public String toString() {
			return "ε";
		}
	}

	public static class Terminal
		extends Mu.Expr
	{
		public char terminal;

		private Terminal(char terminal) {
			this.terminal = terminal;
		}

		public String toString() {
			return "%c".formatted(this.terminal);
		}
	}

	public static class Concatenation
		extends Mu.Expr
	{
		public Mu.Expr left;
		public Mu.Expr right;

		private Concatenation(Mu.Expr left, Mu.Expr right) {
			this.left = left;
			this.right = right;
		}

		public String toString() {
			return "%s%s".formatted(
					this.left.toString(true),
					this.right.toString(true));
		}
	}

	public static class Alternation
		extends Mu.Expr
	{
		public Mu.Expr left;
		public Mu.Expr right;

		private Alternation(Mu.Expr left, Mu.Expr right) {
			this.left = left;
			this.right = right;
		}

		public String toString() {
			return "%s+%s".formatted(this.left, this.right);
		}

		protected String toString(boolean paren) {
			if (paren)
				return "(%s)".formatted(this.toString());
			return this.toString();
		}
	}

	public static class RecVar
		extends Mu.Expr
	{
		public int id;

		private RecVar(int id) {
			this.id = id;
		}

		public String toString() {
			return "%d".formatted(this.id);
		}
	}

	public static class FixPoint
		extends Mu.Expr
	{
		public int boundId;
		public Mu.Expr body;

		private FixPoint(int boundId, Mu.Expr body) {
			this.boundId = boundId;
			this.body = body;
		}

		public String toString() {
			return "μ%d.%s".formatted(this.boundId, this.body);
		}

		protected String toString(boolean paren) {
			if (paren)
				return "(%s)".formatted(this.toString());
			return this.toString();
		}
	}

	public static Mu.Expr fromRegularGrammar(final Grammar grammar)
	{
		Mu.Expr result = new Mu.RecVar(grammar.startingRule);

		HashMap<Integer, Mu.Expr> translatedRules = new HashMap<>();
		for (var entry : grammar.rules.entrySet())
			translatedRules.put(
					entry.getKey(),
					toFixPoint(entry.getKey(), entry.getValue()));

		ArrayDeque<Integer> unboundIds =
			new ArrayDeque<>(Arrays.asList(grammar.startingRule));

		while (!unboundIds.isEmpty())
		{
			int replaceThis = unboundIds.pop();

			result = replaceRecVars(result, replaceThis, translatedRules);

			unboundIds.addAll(result.unboundIds());
		}
		return result;
	}

	static Mu.Expr replaceRecVars(final Mu.Expr result,
			int replaceThis,
			final Map<Integer, Mu.Expr> rules)
	{
		if (result instanceof FixPoint fp)
		{
			return fixpoint(fp.boundId,
					replaceRecVars(fp.body, replaceThis, rules));
		}
		else if (result instanceof Concatenation c)
		{
			return concatenation(
					replaceRecVars(c.left, replaceThis, rules),
					replaceRecVars(c.right, replaceThis, rules));
		}
		else if (result instanceof Alternation c)
		{
			return alternation(
					replaceRecVars(c.left, replaceThis, rules),
					replaceRecVars(c.right, replaceThis, rules));
		}
		else if (result instanceof RecVar r && r.id == replaceThis)
		{
			return rules.get(replaceThis);
		}
		return result;
	}

	static Mu.Expr toFixPoint(int boundId, final List<Grammar.Rule> terms)
	{
		if (terms.isEmpty()) throw new IllegalArgumentException();

		Mu.Expr result = oneTerm(terms.get(0));
		for (int i = 1; i < terms.size(); i++)
		{
			var term = oneTerm(terms.get(i));
			result = new Mu.Alternation(result, term);
		}

		return Mu.fixpoint(boundId, result);
	}

	static Mu.Expr oneTerm(final Grammar.Rule term)
	{
		Mu.Expr result;
		if (term.nextRule() > -1)
			result = new Mu.Concatenation(
					new Mu.Terminal(term.terminal()),
					new Mu.RecVar(term.nextRule()));
		else
			result = new Mu.Terminal(term.terminal());
		return result;
	}
}
