package de.seg.ghostbuster.mu;

public abstract class MuVisitor
{
	public void visit(Mu.EmptySet es) {}
	public void visit(Mu.Epsilon eps) {}
	public void visit(Mu.Terminal t) {}
	public void visit(Mu.Concatenation c) {}
	public void visit(Mu.Alternation a) {}
	public void visit(Mu.FixPoint fp) {}
	public void visit(Mu.RecVar rv) {}
}
