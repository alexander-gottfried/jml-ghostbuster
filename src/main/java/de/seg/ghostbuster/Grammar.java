package de.seg.ghostbuster;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

public class Grammar {
	public record Rule(
			int name,
			Method method,
			int nextRule)
	{}

	int startingRule;
	HashMap<Integer, ArrayList<Rule>> rules;

	Grammar(int startingRule, Collection<Rule> rules)
	{
		this.startingRule = startingRule;
		this.rules = new HashMap<>();

		for (var rule : rules) {
			if (!this.rules.containsKey(rule.name))
				this.rules.put(rule.name, new ArrayList<>());

			this.rules.get(rule.name).add(rule);
		}
	}
}
