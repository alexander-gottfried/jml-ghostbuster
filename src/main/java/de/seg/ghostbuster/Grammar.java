package de.seg.ghostbuster;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class Grammar {
	public record Rule(
			int name,
			Method method,
			int nextRule)
	{
		static int NONE = -1;

		public static Rule terminal(int name, Method method) {
			return new Rule(name, method, NONE);
		}

		private static String str(Rule rule) {
			String result = "'%s'".formatted(rule.method.toString());
			if (!rule.isTerminal())
				result += rule.nextRule;
			return result;
		}

		public boolean isTerminal() {
			return this.nextRule <= NONE;
		}
	}

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

	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("S -> %d\n".formatted(this.startingRule));

		for (var entry : this.rules.entrySet()) {
			int name = entry.getKey();
			var ruleIter = entry.getValue().stream()
				.map(Rule::str)
				.collect(Collectors.toList());

			sb.append(name)
				.append(" -> ")
				.append(String.join(" | ", ruleIter))
				.append('\n');
		}
		return sb.toString();
	}
}
