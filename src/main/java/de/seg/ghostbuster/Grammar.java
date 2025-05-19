package de.seg.ghostbuster;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;

public class Grammar {
	public record Rule(
		int name,
		char terminal,
		int nextRule)
	{}

	private static Comparator<Rule> ruleCmp = new Comparator<Rule>() {
		public int compare(Rule r1, Rule r2) {
			return Integer.compare(r1.name, r2.name);
		}
	};

	int startingRule;
	HashMap<Integer, ArrayList<Rule>> rules;

	// TODO create proper safe way of instantiating a grammar
	Grammar(int startingRule, Collection<Rule> rules) {
		this.rules = new HashMap<>();
		for (var rule : rules) {
			if (!this.rules.containsKey(rule.name))
				this.rules.put(rule.name, new ArrayList<>());

			this.rules.get(rule.name).add(rule);
		}
	}
}
