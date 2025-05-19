package de.seg.ghostbuster;

import java.util.ArrayList;

class GrammarBuilder
{
	private Method.Store methodStore;
	private ArrayList<Grammar.Rule> rules;

	public GrammarBuilder(Method.Store backingMethodStore) {
		this.methodStore = backingMethodStore;
		this.rules = new ArrayList<>();
	}

	/**
	 * Constructor that creates its own method store. 
	 *
	 * The method store's lifetime is then tied to this and the resulting grammar.
	 * Use the above constructor to manage the method store yourself.
	 */
	public GrammarBuilder() {
		this(new Method.Store());
	}

	public GrammarBuilder addRule(int from, String methodName, int to)
	{
		Method method = this.methodStore.put(methodName);
		this.rules.add(new Grammar.Rule(from, method, to));
		return this;
	}

	public GrammarBuilder addRule(int from, String methodName)
	{
		return this.addRule(from, methodName, Grammar.Rule.NONE);
	}

	public GrammarBuilder addEmpty(Iterable<Integer> froms)
	{
		for (int from : froms)
			this.addRule(from, "ε");
		return this;
	}

	public GrammarBuilder addEmpty(int... froms) {
		for (int from : froms)
			this.addRule(from, "ε");
		return this;
	}

	public Grammar startWith(int startingRule) {
		return new Grammar(startingRule, this.rules);
	}
}
