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
package org.graphstream.nui.data;

import org.graphstream.nui.UIDataset;

public class EdgeData extends ElementData {
	public static enum PointsType {
		RELATIVE, ABSOLUTE, ABSOLUTE_SCREEN
	}

	public final NodeData src;
	public final NodeData trg;

	boolean directed;
	int width;

	float[] points;

	public EdgeData(UIDataset dataset, String edgeId, NodeData src,
			NodeData trg, boolean directed) {
		super(dataset, edgeId);

		this.src = src;
		this.trg = trg;
		this.directed = directed;
		this.points = new float[4];
	}

	@Override
	public String toString() {
		return String.format("EdgeData<%s;%s;%s>", id, src.id, trg.id);
	}

	// protected void computeConnectorCircle(NodeData from, NodeData to,
	// float[] points, XYConverter camera) {
	// float px1 = camera.xToScreenX(from.x);
	// float py1 = camera.yToScreenY(from.y);
	// float px2 = camera.xToScreenX(to.x);
	// float py2 = camera.yToScreenY(to.y);
	//
	// points[0] = px1;
	// points[1] = py1;
	// points[2] = px2;
	// points[3] = py2;
	//
	// float w = from.width / 2.0f + from.stroke / 2.0f;
	// float h = from.height / 2.0f + from.stroke / 2.0f;
	// float delta = (float) Math.atan2(py1 - py2, px1 - px2);
	//
	// points[0] -= w * Math.cos(delta);
	// points[1] -= h * Math.sin(delta);
	//
	// w = to.width / 2.0f + to.stroke / 2.0f;
	// h = from.height / 2.0f + from.stroke / 2.0f;
	// delta = (float) Math.atan2(py2 - py1, px2 - px1);
	//
	// points[2] -= w * Math.cos(delta);
	// points[3] -= h * Math.sin(delta);
	// }
}
