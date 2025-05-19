package de.seg.ghostbuster;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import de.seg.ghostbuster.mu.*;


public class GrammarToMu
{

	/**
	 * https://www.cs.ru.nl/bachelors-theses/2018/Bart_Gruppen___4465784___From_mu-regular_expressions_to_CFGs_and_back.pdf
	 *
	 * Use algorithm from above to translate a right-recursive regular grammar
	 * into a μ-expression as defined in the same paper.
	 * The paper translates context-free grammars, which include regular grammars.
	 *
	 * @param grammar the grammar
	 * @return an equivalent μ-expression
	 */
	public static Mu.Expr translate(final Grammar grammar)
	{
		// "Start with the expression as the starting symbol S." (above link)
		Mu.Expr result = Mu.recvar(grammar.startingRule);

		// grammar rule bodies translated like in the referenced paper
		HashMap<Integer, Mu.Expr> translatedRules = new HashMap<>();
		for (var entry : grammar.rules.entrySet())
			translatedRules.put(
					entry.getKey(),
					toFixPoint(entry.getKey(), entry.getValue()));

		ArrayDeque<Integer> freeVariables =
			new ArrayDeque<>(Arrays.asList(grammar.startingRule));

		// replaces RecVar(S) with μS.body, where body is taken from
		// `translatedRules`
		RecVarReplacer recVarReplacer = new RecVarReplacer(translatedRules);

		// finds all RecVar(S) without a corresponding μS and puts them in
		// `freeVariables`
		UnboundIdsVisitor freeVarCollector = new UnboundIdsVisitor(freeVariables);

		// "Continue until there are no free variables left."
		while (!freeVariables.isEmpty())
		{
			int replaceThisId = freeVariables.pop();

			// "Substitute all free variables with the corresponding μ-regular
			// expressions."
			recVarReplacer.replaceThis = replaceThisId;
			result = result.apply(recVarReplacer);

			// collector free variables
			result.accept(freeVarCollector);
			freeVarCollector.reset();
		}

		// The above algorithm sometimes leaves terms μS.body where body doesn't
		// contain references to S. This transformer removes these fixpoint ops.
		NonOccuringIdRemover nonOccuringIdRemover = new NonOccuringIdRemover();
		result = result.apply(nonOccuringIdRemover);

		return result;
	}

	/**
	 * Tranlates one production rule.
	 *
	 * @param boundId left-hand side of the production rule
	 * @param terms right-hand body of the production rule
	 * @return equivalent μ-expression alternation ('+')
	 */
	private static Mu.Expr toFixPoint(int boundId, final List<Grammar.Rule> terms)
	{
		if (terms.isEmpty()) throw new IllegalArgumentException();

		Mu.Expr result = oneTerm(terms.get(0));
		for (int i = 1; i < terms.size(); i++)
		{
			var term = oneTerm(terms.get(i));
			result = Mu.alternation(result, term);
		}

		return Mu.fixpoint(boundId, result);
	}

	/**
	 * Translates one grammar term of form 'terminal nonterminal' or 'terminal'.
	 *
	 * @param term the term
	 * @return equivalent μ-expression
	 */
	static Mu.Expr oneTerm(final Grammar.Rule term)
	{
		Mu.Expr result;
		if (term.nextRule() > -1)
			result = Mu.concatenation(
					Mu.terminal(term.method()),
					Mu.recvar(term.nextRule()));
		else
			result = Mu.terminal(term.method());
		return result;
	}

	private static class UnboundIdsVisitor
		extends MuVisitor
	{
		public Collection<Integer> result;
		private HashSet<Integer> alreadyBound;

		public UnboundIdsVisitor(Collection<Integer> result) {
			this.result = result;
			this.alreadyBound = new HashSet<>();
		}

		public void reset() {
			this.alreadyBound.clear();
		}

		public void visit(Mu.RecVar rv) {
			if (!alreadyBound.contains(rv.id))
				result.add(rv.id);
		}

		public void visit(Mu.FixPoint fp) {
			alreadyBound.add(fp.boundId);
		}
	}

	static Set<Integer> unboundIds(Mu.Expr expr)
	{
		HashSet<Integer> result = new HashSet<>();
		UnboundIdsVisitor v = new UnboundIdsVisitor(result);
		expr.accept(v);
		return result;
	}

	private static class ContainsIdVisitor
		extends MuVisitor
	{
		private int id;
		public boolean result;

		public void reset(int id) {
			this.id = id;
			this.result = false;
		}

		public void visit(Mu.RecVar rv) {
			this.result = this.result || rv.id == this.id;
		}
	}

	private static class NonOccuringIdRemover
		extends MuTransformer
	{
		private ContainsIdVisitor containsIdVisitor = new ContainsIdVisitor();

		public Mu.Expr transformFixPoint(Mu.FixPoint fp, int boundId, Mu.Expr body)
		{
			containsIdVisitor.reset(boundId);
			body.accept(containsIdVisitor);

			Mu.Expr result;
			if (containsIdVisitor.result) {
				result = Mu.fixpoint(boundId, body);
			} else {
				result = body;
			}

			return result;
		}
	}

	private static class RecVarReplacer
		extends MuTransformer
	{
		public int replaceThis;
		private final Map<Integer, Mu.Expr> rules;

		public RecVarReplacer(Map<Integer, Mu.Expr> rules) {
			this.rules = rules;
		}

		public Mu.Expr transformRecVar(Mu.RecVar rv, int id)
		{
			if (id == replaceThis)
				return rules.get(replaceThis);
			return Mu.recvar(id);
		}
	}
}
