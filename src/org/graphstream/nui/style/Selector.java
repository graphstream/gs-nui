package org.graphstream.nui.style;

import java.util.Arrays;

/**
 * One of the key element for style. Selector defines the target of a style. It
 * is composed of a target type, which can be a node, an edge, a sprite, the
 * whole graph, or any element. Then, it can have an id to be dedicated to a
 * particular element. Then some "class", and a state. The representation of a
 * selector is <code>type#id.class1.class2:state</code>. Classes are ordered to
 * produce a unique representation. HashCode and equals are overridden to allow
 * selectors to be used in hash collection.
 * 
 */
public class Selector {
	public static enum Target {
		GRAPH, NODE, EDGE, SPRITE, UNDEFINED
	}

	public final Target target;
	public final String id;
	protected final String[] uiClass;
	public final String state;
	protected final String repr;
	public final int score;

	public Selector() {
		this(Target.UNDEFINED, null, null, null);
	}

	public Selector(Target target, String id, String[] uiClass, String state) {
		this.id = id;
		this.uiClass = uiClass;
		this.target = target;
		this.state = state;

		if (uiClass != null)
			Arrays.sort(uiClass);

		StringBuilder buffer = new StringBuilder(target.name().toLowerCase());

		if (id != null)
			buffer.append('#').append(id);

		if (uiClass != null)
			for (int i = 0; i < uiClass.length; i++)
				buffer.append('.').append(uiClass[i]);

		if (state != null)
			buffer.append(':').append(state);

		this.repr = buffer.toString();

		int score = 0;

		if (target != Target.UNDEFINED)
			score += 1;

		if (uiClass != null)
			score += 10 * uiClass.length;

		if (id != null)
			score += 100;

		this.score = score;
	}

	public boolean hasState() {
		return state != null;
	}

	public boolean hasUIClass() {
		return uiClass != null && uiClass.length > 0;
	}

	public Selector getNoStateSelector() {
		if (state == null)
			return this;

		Selector noState = new Selector(target, id, uiClass, null);
		return noState;
	}

	public int partsCount() {
		int p = 0;

		if (target != Target.UNDEFINED)
			p++;

		if (uiClass != null)
			p += uiClass.length;

		if (id != null)
			p++;

		if (state != null)
			p++;

		return p;
	}

	public boolean isParent(Selector child) {
		if (equals(child))
			return false;

		if (state != null)
			return false;

		if (child.target != target) {
			if (target != Target.UNDEFINED)
				return false;
		}

		if (child.id != null) {
			if (id != null) {
				if (!id.equals(child.id))
					return false;
			}
		} else {
			if (id != null)
				return false;
		}

		if (uiClass != null) {
			if (child.uiClass == null)
				return false;
			else {
				for (int i = 0; i < uiClass.length; i++) {
					boolean find = false;

					for (int j = 0; j < child.uiClass.length && !find; j++)
						find = find || child.uiClass[j].equals(uiClass[i]);

					if (!find)
						return false;
				}
			}
		}

		return true;
	}

	public boolean match(ElementData data) {
		if (target != Target.UNDEFINED) {
			switch (data.index.getType()) {
			case NODE:
				if (target != Target.NODE)
					return false;
				break;
			case EDGE:
				if (target != Target.EDGE)
					return false;
				break;
			case GRAPH:
				if (target != Target.GRAPH)
					return false;
				break;
			case SPRITE:
				return false;
			}
		}

		if (id != null && !id.equals(data.index.id()))
			return false;

		if (uiClass != null) {
			for (int i = 0; i < uiClass.length; i++)
				if (!data.hasUIClass(uiClass[i]))
					return false;
		}

		if (state != null)
			return state.equals(data.state);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Selector) {
			Selector data = (Selector) obj;
			return repr.equals(data.repr);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return repr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return repr.hashCode();
	}
}
