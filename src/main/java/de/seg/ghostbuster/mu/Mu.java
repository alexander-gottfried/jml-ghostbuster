package de.seg.ghostbuster.mu;

import de.seg.ghostbuster.Method;

public class Mu
{
	public static Mu.EmptySet emptySet() {
		return new Mu.EmptySet();
	}

	public static Mu.Epsilon epsilon() {
		return new Mu.Epsilon();
	}

	public static Mu.Terminal terminal(Method m) {
		return new Mu.Terminal(m);
	}

	public static Mu.RecVar recvar(int id) {
		return new Mu.RecVar(id);
	}

	public static Mu.Concatenation concatenation(Mu.Expr left, Mu.Expr right) {
		return new Mu.Concatenation(left, right);
	}

	public static Mu.Alternation alternation(Mu.Expr left, Mu.Expr right) {
		return new Mu.Alternation(left, right);
	}

	public static Mu.FixPoint fixpoint(int boundId, Mu.Expr body) {
		return new Mu.FixPoint(boundId, body);
	}

	public static abstract class Expr {
		protected String toString(boolean paren) {
			return this.toString();
		}

		public abstract void accept(MuVisitor v);
		public abstract Mu.Expr apply(MuTransformer t);
	}

	public static class EmptySet
		extends Mu.Expr
	{
		private EmptySet() {}

		public String toString() {
			return "∅";
		}

		public void accept(MuVisitor v) {
			v.visit(this);
		}

		public Mu.Expr apply(MuTransformer t) {
			return t.transformEmptySet(this);
		}
	}

	public static class Epsilon
		extends Mu.Expr
	{
		private Epsilon() {}

		public String toString() {
			return "ε";
		}

		public void accept(MuVisitor v) {
			v.visit(this);
		}

		public Mu.Expr apply(MuTransformer t) {
			return t.transformEpsilon(this);
		}
	}

	public static class Terminal
		extends Mu.Expr
	{
		public Method method;

		private Terminal(Method method) {
			this.method = method;
		}

		public String toString() {
			return "'%s'".formatted(this.method.toString());
		}

		public void accept(MuVisitor v) {
			v.visit(this);
		}

		public Mu.Expr apply(MuTransformer t) {
			return t.transformTerminal(this, this.method);
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

		public void accept(MuVisitor v) {
			v.visit(this);
			this.left.accept(v);
			this.right.accept(v);
		}

		public Mu.Expr apply(MuTransformer t) {
			Mu.Expr left = this.left.apply(t);
			Mu.Expr right = this.right.apply(t);
			var cont = t.transformConcatenation(this, left, right);
			return cont;
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

		public void accept(MuVisitor v) {
			v.visit(this);
			this.left.accept(v);
			this.right.accept(v);
		}

		public Mu.Expr apply(MuTransformer t) {
			Mu.Expr left = this.left.apply(t);
			Mu.Expr right = this.right.apply(t);
			return t.transformAlternation(this, left, right);
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

		public void accept(MuVisitor v) {
			v.visit(this);
		}

		public Mu.Expr apply(MuTransformer t) {
			return t.transformRecVar(this, this.id);
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

		public void accept(MuVisitor v) {
			v.visit(this);
			this.body.accept(v);
		}

		public Mu.Expr apply(MuTransformer t) {
			Mu.Expr body = this.body.apply(t);
			return t.transformFixPoint(this, this.boundId, body);
		}

		protected String toString(boolean paren) {
			if (paren)
				return "(%s)".formatted(this.toString());
			return this.toString();
		}
	}
}
