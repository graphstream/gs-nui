package org.graphstream.nui;

import org.graphstream.nui.data.NodeData;
import org.graphstream.stream.AttributeSink;

public class UIAttributes implements AttributeSink {
	protected static boolean match(String chain, int offset, String what,
			boolean ends) {
		if (offset + what.length() > chain.length())
			return false;

		for (int i = 0; i < what.length(); i++)
			if (chain.charAt(offset + i) != what.charAt(i))
				return ends ? offset + what.length() == chain.length() : true;

		return true;
	}

	protected Viewer viewer;
	protected UIDataset set;

	private void update(String nodeId, String xyzKey, Object value) {
		int idx = set.getNodeIndex(nodeId);
		
		NodeData data = set.getNodeData(idx);
		double tmp;
		boolean changed = false;

		if (data == null)
			return;

		xyzKey = xyzKey.toLowerCase();

		switch (xyzKey.length()) {
		case 1:
			switch (xyzKey.charAt(0)) {
			case 'x':
				tmp = checkAndGetDouble(value);
				changed = changed || (data.x != tmp);
				data.x = tmp;
				break;
			case 'y':
				tmp = checkAndGetDouble(value);
				changed = changed || (data.y != tmp);
				data.y = tmp;
				break;
			case 'z':
				tmp = checkAndGetDouble(value);
				changed = changed || (data.z != tmp);
				data.z = tmp;
				break;
			}
			break;
		default:
			double[] xyz = checkAndGetDoubleArray(value);
			changed = changed || (data.x != xyz[0]) || (data.y != xyz[1]);
			data.x = xyz[0];
			data.y = xyz[1];

			if (xyz.length > 2) {
				changed = changed || (data.z != xyz[2]);
				data.z = xyz[2];
			}

			break;
		}

		if (changed)
			set.dataUpdated(data);
	}

	private double checkAndGetDouble(Object value) {
		if (value instanceof Double)
			return (Double) value;

		if (value instanceof Number)
			return ((Number) value).doubleValue();

		throw new RuntimeException(String.format(
				"invalid xyz value with type %s", value.getClass().getName()));
	}

	private double[] checkAndGetDoubleArray(Object value) {
		double[] r = null;

		if (value instanceof double[])
			r = (double[]) value;

		if (value instanceof Double[]) {
			Double[] rO = (Double[]) value;
			r = new double[rO.length];

			for (int i = 0; i < r.length; i++)
				r[i] = rO[i];
		}

		if (value instanceof Object[]) {
			Object[] rO = (Object[]) value;
			r = new double[rO.length];

			for (int i = 0; i < r.length; i++)
				r[i] = checkAndGetDouble(rO[i]);
		}

		if (r == null)
			throw new RuntimeException(String.format(
					"invalid xyz value with type %s", value.getClass()
							.getName()));

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeAdded(java.lang.String
	 * , long, java.lang.String, java.lang.Object)
	 */
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		if (match(attribute, 0, "ui.", false)) {
			if (match(attribute, 3, "stylesheet", true)) {
				if (value instanceof String)
					viewer.stylesheet.addStylesheet((String) value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeChanged(java.lang.
	 * String, long, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		if (match(attribute, 0, "ui.", false)) {
			if (match(attribute, 3, "stylesheet", true)) {
				if (newValue instanceof String)
					viewer.stylesheet.setStylesheet((String) newValue);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#graphAttributeRemoved(java.lang.
	 * String, long, java.lang.String)
	 */
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		if (match(attribute, 0, "ui.", false)) {
			if (match(attribute, 3, "stylesheet", true))
				viewer.stylesheet.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang.String,
	 * long, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang.String
	 * , long, java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.Object)
	 */
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang.String
	 * , long, java.lang.String, java.lang.String)
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		// TODO Auto-generated method stub

	}
}
