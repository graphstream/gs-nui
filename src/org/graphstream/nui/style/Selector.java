package org.graphstream.nui.style;

import org.graphstream.nui.data.EdgeData;
import org.graphstream.nui.data.ElementData;
import org.graphstream.nui.data.NodeData;

public class Selector {
	protected static enum Target {
		GRAPH, NODE, EDGE, SPRITE, UNDEFINED
	}

	protected Target target;
	protected String id;
	protected String[] uiClass;
	protected String state;

	public Selector() {
		this(Target.UNDEFINED, null, null, null);
	}

	public Selector(Target target, String id, String[] uiClass, String state) {
		this.id = id;
		this.uiClass = uiClass;
		this.target = target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUIClass(String... uiClass) {
		this.uiClass = uiClass;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean match(ElementData data) {
		if (target != Target.UNDEFINED) {
			if (data instanceof NodeData) {
				if (target != Target.NODE)
					return false;
			} else if (data instanceof EdgeData) {
				if (target != Target.EDGE)
					return false;
			} else {
				if (target != Target.SPRITE)
					return false;
			}
		}

		if (id != null && !id.equals(data.id))
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
}
