package de.seg.ghostbuster.mu;

import de.seg.ghostbuster.*;

public abstract class MuTransformer
{
	public Mu.Expr transformEmptySet(Mu.EmptySet es) {
		return Mu.emptySet();
	}
	public Mu.Expr transformEpsilon(Mu.Epsilon eps) {
		return Mu.epsilon();
	}
	public Mu.Expr transformTerminal(Mu.Terminal t, Method m) {
		return Mu.terminal(m);
	}
	public Mu.Expr transformConcatenation(Mu.Concatenation c,
			Mu.Expr left,
			Mu.Expr right) {
		return Mu.concatenation(left, right);
	}
	public Mu.Expr transformAlternation(Mu.Alternation a,
			Mu.Expr left,
			Mu.Expr right) {
		return Mu.alternation(left, right);
	}
	public Mu.Expr transformFixPoint(Mu.FixPoint fp, int boundId, Mu.Expr body) {
		return Mu.fixpoint(boundId, body);
	}
	public Mu.Expr transformRecVar(Mu.RecVar rv, int id) {
		return Mu.recvar(id);
	}
}

