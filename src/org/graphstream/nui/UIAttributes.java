/*
 * Copyright 2006 - 2014
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.nui;

import org.graphstream.nui.data.ElementData;
import org.graphstream.stream.AttributeSink;

public class UIAttributes implements AttributeSink {
	protected static boolean match(String chain, int offset, String what,
			boolean ends) {
		if (offset + what.length() > chain.length())
			return false;

		for (int i = 0; i < what.length(); i++)
			if (chain.charAt(offset + i) != what.charAt(i))
				return false;

		return ends ? offset + what.length() == chain.length() : true;
	}

	/**
	 * Check is the attribute name is a valid name for coordinates attribute. It
	 * can be "xyz", "x", "y" or "z" and can be prefixed by "ui.".
	 * 
	 * @param attribute
	 *            the name of the attribute
	 * @return true if attribute is a valid attribute name
	 */
	public static boolean isCoordinatesAttribute(String attribute) {
		int offset = 0;

		if (match(attribute, 0, "ui.", false))
			offset = 3;

		/*
		 * Sort by likely frequency.
		 */
		if (match(attribute, offset, "xyz", true)
				| match(attribute, offset, "x", true)
				| match(attribute, offset, "y", true)
				| match(attribute, offset, "z", true)
				| match(attribute, offset, "XYZ", true)
				| match(attribute, offset, "X", true)
				| match(attribute, offset, "Y", true)
				| match(attribute, offset, "Z", true))
			return true;

		return false;
	}

	protected Viewer viewer;
	protected UIDataset dataset;
	private double[] xyzBuffer;

	public UIAttributes() {
		xyzBuffer = new double[3];
	}

	public void init(Viewer viewer) {
		this.viewer = viewer;
		this.dataset = viewer.dataset;

		viewer.sources.addAttributeSink(this);
	}

	/*
	 * This is not thread-safe because of the use of xyzBuffer.
	 */
	private void updateCoordinates(String nodeId, String xyzKey, Object value) {
		int idx = dataset.getNodeIndex(nodeId);
		double tmp;
		boolean changed = false;

		if (idx < 0)
			return;

		dataset.getNodeXYZ(idx, xyzBuffer);

		if (xyzKey.length() == 1) {
			switch (xyzKey.charAt(0)) {
			case 'X':
			case 'x':
				tmp = checkAndGetDouble(value);
				changed = changed || (xyzBuffer[0] != tmp);
				xyzBuffer[0] = tmp;
				break;
			case 'Y':
			case 'y':
				tmp = checkAndGetDouble(value);
				changed = changed || (xyzBuffer[1] != tmp);
				xyzBuffer[1] = tmp;
				break;
			case 'Z':
			case 'z':
				tmp = checkAndGetDouble(value);
				changed = changed || (xyzBuffer[2] != tmp);
				xyzBuffer[2] = tmp;
				break;
			}
		} else {
			double[] xyz = checkAndGetDoubleArray(value);
			changed = changed || (xyzBuffer[0] != xyz[0])
					|| (xyzBuffer[1] != xyz[1]);

			xyzBuffer[0] = xyz[0];
			xyzBuffer[1] = xyz[1];

			if (xyz.length > 2) {
				changed = changed || (xyzBuffer[2] != xyz[2]);
				xyzBuffer[2] = xyz[2];
			}
		}

		if (changed)
			dataset.setNodeXYZ(idx, xyzBuffer);
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

	protected void updateUIClass(ElementData data, Object value) {
		if (value == null) {
			data.removeUIClass();
		} else {
			if (value.getClass().isArray()) {
				String[] classes;

				if (value instanceof String[])
					classes = (String[]) value;
				else {
					Object[] classesObj = (Object[]) value;
					classes = new String[classesObj.length];

					for (int i = 0; i < classesObj.length; i++)
						classes[i] = (String) classesObj[i];
				}

				data.setUIClass(classes);
			} else {
				if (!(value instanceof String))
					return;

				String uiClass = (String) value;

				if (uiClass.charAt(0) == '+')
					data.addUIClass(uiClass.substring(1));
				else if (uiClass.charAt(0) == '-')
					data.removeUIClass(uiClass.substring(1));
				else
					data.setUIClass(uiClass);
			}
		}
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
				viewer.stylesheet.setStyleSheet(value);
			} else if (match(attribute, 3, "space", true)) {
				viewer.camera.set(attribute, value);
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
				viewer.stylesheet.setStyleSheet(newValue);
			} else if (match(attribute, 3, "space", true)) {
				viewer.camera.set(attribute, newValue);
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
		} else if (match(attribute, 3, "space", true)) {
			viewer.camera.set(attribute, null);
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
		if (isCoordinatesAttribute(attribute)) {
			updateCoordinates(nodeId, attribute, value);
		} else if (match(attribute, 0, "ui.", false)) {
			if (match(attribute, 3, "class", true)) {
				int idx = dataset.getNodeIndex(nodeId);

				if (idx >= 0)
					updateUIClass(dataset.getNodeData(idx), value);
			}
		}
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
		if (isCoordinatesAttribute(attribute)) {
			updateCoordinates(nodeId, attribute, newValue);
		} else if (match(attribute, 0, "ui.", false)) {
			if (match(attribute, 3, "class", true)) {
				int idx = dataset.getNodeIndex(nodeId);

				if (idx >= 0)
					updateUIClass(dataset.getNodeData(idx), newValue);
			}
		}
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
		if (match(attribute, 0, "ui.", false)) {
			if (match(attribute, 3, "class", true)) {
				int idx = dataset.getNodeIndex(nodeId);

				if (idx >= 0)
					updateUIClass(dataset.getNodeData(idx), null);
			}
		}
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
