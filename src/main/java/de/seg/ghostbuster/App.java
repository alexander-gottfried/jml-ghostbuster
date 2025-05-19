package de.seg.ghostbuster;

import de.seg.ghostbuster.mu.*;

public class App {
	public static void main(String[] args)
	{
		Grammar pre_pb_eps = new GrammarBuilder()
			.addRule(0, "placeBet", 1)
			.addRule(1, "decideBet", 0)
			.addRule(1, "decideBet", 2)
			.addRule(2, "placeBet", 1)
			.addEmpty(0, 2)
			.startWith(0);

		translateAndPrint("Casino, pretrace of placeBet, w/ ε", pre_pb_eps);

		Grammar pre_db_eps = new GrammarBuilder()
			.addRule(0, "placeBet", 1)
			.addRule(1, "decideBet", 0)
			.addRule(1, "decideBet", 2)
			.addRule(2, "placeBet", 1)
			.addEmpty(1)
			.startWith(0);

		translateAndPrint("Casino, pretrace of decideBet, w/ ε", pre_db_eps);

		Grammar pre_pb = new GrammarBuilder()
			.addRule(0, "placeBet", 1)
			.addRule(1, "decideBet", 0)
			.addRule(1, "decideBet", 2)
			.addRule(2, "placeBet", 1)
			.addRule(1, "decideBet")
			.startWith(0);

		translateAndPrint("Casino, pretrace of placeBet", pre_pb);

		Grammar pre_db = new GrammarBuilder()
			.addRule(0, "placeBet", 1)
			.addRule(1, "decideBet", 0)
			.addRule(1, "decideBet", 2)
			.addRule(2, "placeBet", 1)
			.addRule(0, "placeBet")
			.addRule(2, "placeBet")
			.startWith(0);

		translateAndPrint("Casino, pretrace of decideBet", pre_db);
	}

	private static void translateAndPrint(String name, Grammar grammar)
	{
		System.out.println(name);
		System.out.println();
		System.out.println(grammar);
		System.out.println();

		Mu.Expr translated = GrammarToMu._translate(grammar);
		System.out.println("First translation:");
		System.out.println("  " + translated);

		System.out.println();

		Mu.Expr nonOccuringRemoved = GrammarToMu._nonOccurringRemoved(translated);
		System.out.println("Non-occuring fixpoints removed:");
		System.out.println("  " + nonOccuringRemoved);
		System.out.println("\n");
	}
}
