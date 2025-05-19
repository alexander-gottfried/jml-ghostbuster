package de.seg.ghostbuster;

import java.util.HashMap;
import java.util.ArrayList;

public class Method {
	private final Method.Store origin;
	private final int id;

	private Method(int id, Method.Store origin) {
		this.id = id;
		this.origin = origin;
	}

	public String toString() {
		return this.origin.getName(this);
	}

	// TODO idk ????
	public int hashCode() {
		return id;
	}

	public boolean equals(Method other) {
		return this.id == other.id;
	}

	public static class Store {
		private HashMap<String, Method> indeces = new HashMap<>();
		private ArrayList<String> names = new ArrayList<>();

		public Method put(String name) {
			if (indeces.containsKey(name))
				return indeces.get(name);

			int index = names.size();
			names.add(name);
			var t = new Method(index, this);
			indeces.put(name, t);
			return t;
		}

		public Method getMethod(String name) {
			return indeces.get(name);
		}

		public String getName(Method terminal) {
			return names.get(terminal.id);
		}
	}
}
