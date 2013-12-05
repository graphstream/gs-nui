/*
 * Copyright 2006 - 2012
 *      Stefan Balev    <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	    <yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.nui.data.buffer;

import org.graphstream.nui.data.NodeData;
import org.graphstream.stream.SinkAdapter;

public class XYZUpdater extends SinkAdapter {

	final BufferUIDataset set;

	public XYZUpdater(BufferUIDataset set) {
		this.set = set;
	}

	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		//
		// First condition is used to reduce call to matches()
		//
		if ((attribute.charAt(0) == 'x' || attribute.charAt(0) == 'X')
				&& attribute.matches("x|y|z|xy|xyz|X|Y|Z|XY|XYZ"))
			update(nodeId, attribute, value);
	}

	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		//
		// First condition is used to reduce call to matches()
		//
		if ((attribute.charAt(0) == 'x' || attribute.charAt(0) == 'X')
				&& attribute.matches("x|y|z|xy|xyz|X|Y|Z|XY|XYZ"))
			update(nodeId, attribute, newValue);
	}

	private void update(String nodeId, String xyzKey, Object value) {
		NodeData data = set.getNodeData(nodeId);
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
}
