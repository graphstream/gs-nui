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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Source;

import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIDatasetListener;
import org.graphstream.nui.data.AbstractUIDataset;
import org.graphstream.nui.data.DataFactory;
import org.graphstream.nui.data.EdgeData;
import org.graphstream.nui.data.ElementData;
import org.graphstream.nui.data.SpriteData;
import org.graphstream.nui.style.ElementStyle;
import org.graphstream.nui.style.StyleUpdater;

public class BufferUIDataset extends AbstractUIDataset implements UIDataset,
		ElementSink {

	public static final int INITIAL_SIZE = 100;
	public static final int GROW_STEP = 100;

	public static final int A_MASK = 0xFF000000;
	public static final int A_SHIFT = 24;
	public static final int R_MASK = 0x00FF0000;
	public static final int R_SHIFT = 16;
	public static final int G_MASK = 0x0000FF00;
	public static final int G_SHIFT = 8;
	public static final int B_MASK = 0x000000FF;
	public static final int B_SHIFT = 0;

	HashMap<String, Integer> nMapping;
	BufferNodeData[] nodes;
	double[] coordinates;
	int[] nodeColors;
	int nIndex;

	HashMap<String, Integer> eMapping;
	BufferEdgeData[] edges;
	int[] edgeColors;
	int eIndex;

	LinkedList<UIDatasetListener> listeners;

	XYZUpdater xyzUpdater;
	StyleUpdater styleUpdater;

	DataFactory dataFactory;

	public BufferUIDataset() {
		nMapping = new HashMap<String, Integer>();
		nodes = new BufferNodeData[INITIAL_SIZE];
		nIndex = 0;

		eMapping = new HashMap<String, Integer>();
		edges = new BufferEdgeData[INITIAL_SIZE];
		eIndex = 0;

		listeners = new LinkedList<UIDatasetListener>();

		xyzUpdater = new XYZUpdater(this);
		styleUpdater = new StyleUpdater(this);

		dataFactory = new BufferDataFactory();
	}

	public void register(Source source) {
		source.addElementSink(this);
		source.addAttributeSink(xyzUpdater);
		source.addAttributeSink(styleUpdater);
	}

	public void unregister(Source source) {
		source.removeElementSink(this);
		source.removeAttributeSink(xyzUpdater);
		source.removeAttributeSink(styleUpdater);
	}

	public AttributeSink getXYZUpdater() {
		return xyzUpdater;
	}

	public BufferNodeData getNodeData(String nodeId) {
		int idx = getNodeIndex(nodeId);

		if (idx < 0)
			return null;

		return nodes[idx];
	}

	public EdgeData getEdgeData(String edgeId) {
		int idx = getEdgeIndex(edgeId);

		if (idx < 0)
			return null;

		return edges[idx];
	}

	public void dataUpdated(ElementData data) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		if (nIndex >= nodes.length) {
			nodes = Arrays.copyOf(nodes, nodes.length + GROW_STEP);

			coordinates = Arrays.copyOf(coordinates, coordinates.length + 3
					* GROW_STEP);
			nodeColors = Arrays.copyOf(nodeColors, nodeColors.length
					+ GROW_STEP);
		}

		int idx = nIndex++;

		nMapping.put(nodeId, idx);

		nodes[idx] = (BufferNodeData) dataFactory.createNodeData(nodeId);
		nodes[idx].updateIndex(idx);

		coordinates[3 * idx + 0] = Double.NaN;
		coordinates[3 * idx + 1] = Double.NaN;
		coordinates[3 * idx + 2] = Double.NaN;

		nodeColors[idx] = 0xFF000000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		int nodeIndex = getNodeIndex(nodeId);

		if (nodeIndex >= 0) {
			nIndex--;

			if (nodeIndex < nIndex - 1) {
				nodes[nodeIndex] = nodes[nIndex];
				nodes[nodeIndex].updateIndex(nodeIndex);

				nodeColors[nodeIndex] = nodeColors[nIndex];

				System.arraycopy(coordinates, 3 * nodeIndex, coordinates,
						3 * nIndex, 3);

				nMapping.put(nodes[nodeIndex].id, nodeIndex);
			}

			nodes[nIndex] = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		if (eIndex >= edges.length) {
			edges = Arrays.copyOf(edges, edges.length + GROW_STEP);
			edgeColors = Arrays.copyOf(edgeColors, edgeColors.length
					+ GROW_STEP);
		}

		BufferNodeData src = getNodeData(fromNodeId);
		BufferNodeData trg = getNodeData(toNodeId);

		int idx = eIndex++;

		eMapping.put(edgeId, idx);
		edges[idx] = (BufferEdgeData) dataFactory.createEdgeData(edgeId, src,
				trg, directed);
		edges[idx].updateIndex(idx);
		edgeColors[idx] = 0xFF000000;
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		int edgeIndex = getEdgeIndex(edgeId);

		if (edgeIndex >= 0) {
			eIndex--;

			if (edgeIndex < eIndex - 1) {
				edges[edgeIndex] = edges[eIndex];
				eMapping.put(edges[edgeIndex].id, edgeIndex);
			}

			edges[eIndex] = null;
		}
	}

	@Override
	public void graphCleared(String sourceId, long timeId) {
		Arrays.fill(nodes, null);
		nIndex = 0;
		nMapping.clear();

		Arrays.fill(edges, null);
		eIndex = 0;
		eMapping.clear();
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#init(org.graphstream.stream.Source)
	 */
	public void init(Source source) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#release()
	 */
	public void release() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeCount()
	 */
	public int getNodeCount() {
		return nIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeCount()
	 */
	public int getEdgeCount() {
		return eIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeIndex(java.lang.String)
	 */
	public int getNodeIndex(String id) {
		Integer idx = nMapping.get(id);
		return idx == null ? -1 : idx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeIndex(java.lang.String)
	 */
	public int getEdgeIndex(String id) {
		Integer idx = eMapping.get(id);
		return idx == null ? -1 : idx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#setDataFactory(org.graphstream.nui.data
	 * .DataFactory)
	 */
	public void setDataFactory(DataFactory factory) {
		this.dataFactory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeData(int)
	 */
	public BufferNodeData getNodeData(int idx) {
		return nodes[idx];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeData(int)
	 */
	public BufferEdgeData getEdgeData(int idx) {
		return edges[idx];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeSource(int)
	 */
	public int getEdgeSource(int idx) {
		return edges[idx].src.index();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeTarget(int)
	 */
	public int getEdgeTarget(int idx) {
		return edges[idx].trg.index();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeX(int)
	 */
	public double getNodeX(int idx) {
		return this.coordinates[3 * idx];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeY(int)
	 */
	public double getNodeY(int idx) {
		return this.coordinates[3 * idx + 1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeZ(int)
	 */
	public double getNodeZ(int idx) {
		return this.coordinates[3 * idx + 2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeXYZ(int, double[])
	 */
	public double[] getNodeXYZ(int idx, double[] xyz) {
		if (xyz == null || xyz.length < 3)
			xyz = new double[3];

		System.arraycopy(coordinates, 3 * idx, xyz, 0, 3);

		return xyz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodesXYZ(double[], boolean)
	 */
	public double[] getNodesXYZ(double[] buffer, boolean direct) {
		if (direct)
			return coordinates;

		if (buffer == null)
			buffer = new double[3 * nIndex];

		System.arraycopy(coordinates, 0, buffer, 0, 3 * nIndex);

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeR(int)
	 */
	public int getNodeR(int idx) {
		return (nodeColors[idx] & R_MASK) >> R_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeG(int)
	 */
	public int getNodeG(int idx) {
		return (nodeColors[idx] & G_MASK) >> G_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeB(int)
	 */
	public int getNodeB(int idx) {
		return (nodeColors[idx] & B_MASK) >> B_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeA(int)
	 */
	public int getNodeA(int idx) {
		return (nodeColors[idx] & A_MASK) >> A_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeARGB(int)
	 */
	public int getNodeARGB(int idx) {
		return nodeColors[idx];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodesARGB(int[], boolean)
	 */
	public int[] getNodesARGB(int[] buffer, boolean direct) {
		if (direct)
			return nodeColors;

		if (buffer == null)
			buffer = new int[nIndex];

		System.arraycopy(nodeColors, 0, buffer, 0, nIndex);

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeR(int)
	 */
	public int getEdgeR(int idx) {
		return (edgeColors[idx] & R_MASK) >> R_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeG(int)
	 */
	public int getEdgeG(int idx) {
		return (edgeColors[idx] & G_MASK) >> G_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeB(int)
	 */
	public int getEdgeB(int idx) {
		return (edgeColors[idx] & B_MASK) >> B_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeA(int)
	 */
	public int getEdgeA(int idx) {
		return (edgeColors[idx] & A_MASK) >> A_SHIFT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeARGB(int)
	 */
	public int getEdgeARGB(int idx) {
		return edgeColors[idx];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgesARGB(int[], boolean)
	 */
	public int[] getEdgesARGB(int[] buffer, boolean direct) {
		if (direct)
			return edgeColors;

		if (buffer == null)
			buffer = new int[eIndex];

		System.arraycopy(edgeColors, 0, buffer, 0, eIndex);

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#applyNodeStyle(int,
	 * org.graphstream.nui.style.ElementStyle)
	 */
	public void applyNodeStyle(int idx, ElementStyle style) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#applyEdgeStyle(int,
	 * org.graphstream.nui.style.ElementStyle)
	 */
	public void applyEdgeStyle(int idx, ElementStyle style) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#applySpriteStyle(int,
	 * org.graphstream.nui.style.ElementStyle)
	 */
	public void applySpriteStyle(int idx, ElementStyle style) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#addUIDatasetListener(org.graphstream.nui
	 * .UIDatasetListener)
	 */
	public void addUIDatasetListener(UIDatasetListener l) {
		listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#removeUIDatasetListener(org.graphstream
	 * .nui.UIDatasetListener)
	 */
	public void removeUIDatasetListener(UIDatasetListener l) {
		listeners.remove(l);
	}

	public int getSpriteCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSpriteIndex(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public SpriteData getSpriteData(int idx) {
		// TODO Auto-generated method stub
		return null;
	}
}
