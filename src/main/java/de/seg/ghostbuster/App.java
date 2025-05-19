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

		Grammar regular = new GrammarBuilder(ms)
			.addRule(0, "placeBet", 1)
			.addRule(1, "decideBet", 0)
			.addRule(1, "decideBet", 2)
			.addRule(2, "placeBet", 1)
			.addEmpty(0, 2)
			.startWith(0);

		System.out.println(regular);

		Mu.Expr ofGrammar = GrammarToMu.translate(regular);
		System.out.println(ofGrammar);
	}
}
