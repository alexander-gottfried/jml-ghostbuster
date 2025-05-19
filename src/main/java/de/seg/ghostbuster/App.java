package de.seg.ghostbuster;

import java.util.Arrays;

import de.seg.ghostbuster.mu.*;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		Method.Store ms = new Method.Store();
		Method p = ms.put("placeBet");
		Method d = ms.put("decideBet");

		Grammar regular = new Grammar(
				0,
				Arrays.asList(
					new Grammar.Rule(0, p, 1),
					new Grammar.Rule(1, d, 0),
					new Grammar.Rule(1, d, 2),
					new Grammar.Rule(2, p, 1)));

		System.out.println(regular.rules);

		Mu.Expr e = Mu.fixpoint(
				0,
				Mu.concatenation(
					Mu.terminal(p),
					Mu.fixpoint(
						1,
						Mu.alternation(
							Mu.concatenation(
								Mu.terminal(d),
								Mu.recvar(0)),
							Mu.concatenation(
								Mu.terminal(d),
								Mu.recvar(2))))));

		System.out.println(GrammarToMu.unboundIds(e));

		System.out.println(e);


		Mu.Expr ofGrammar = GrammarToMu.translate(regular);
		System.out.println(ofGrammar);
	}
}
