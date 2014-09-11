/*
 * Copyright 2006 - 2014
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
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
package org.graphstream.nui.dataset;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.indexer.UIElementIndex;
import org.graphstream.nui.indexer.IndexerListener;
import org.graphstream.nui.util.Tools;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.ui.geom.Point3;

public class DefaultDataset extends AbstractModule implements UIDataset {
	public static final int DEFAULT_NODE_GROW_STEP = 1000;
	public static final int DEFAULT_EDGE_GROW_STEP = 1000;

	protected double[] nodesPoints;
	protected int nodeCount;
	protected int dim;

	protected EdgeData[] edgesData;
	protected int edgeCount;

	protected UIIndexer indexer;

	protected List<DatasetListener> listeners;
	protected int nodeGrowStep;
	protected int edgeGrowStep;

	protected ElementListener elementListener;
	protected CoordinatesListener coordinatesListener;

	public DefaultDataset() {
		super(MODULE_ID, UIIndexer.MODULE_ID);

		listeners = new LinkedList<DatasetListener>();
		elementListener = new ElementListener();
		coordinatesListener = new CoordinatesListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.AbstractModule#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		super.init(ctx);

		indexer = (UIIndexer) ctx.getModule("indexer");
		indexer.addIndexerListener(elementListener);

		ctx.getContextProxy().addAttributeSink(coordinatesListener);

		dim = 3;

		nodeGrowStep = DEFAULT_NODE_GROW_STEP;
		edgeGrowStep = DEFAULT_EDGE_GROW_STEP;
		nodeCount = 0;
		edgeCount = 0;

		nodesPoints = new double[dim * nodeGrowStep];
		edgesData = new EdgeData[edgeGrowStep];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		indexer.removeIndexerListener(elementListener);
		indexer = null;

		ctx.getContextProxy().removeAttributeSink(coordinatesListener);

		super.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		super.setAttribute(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeCount()
	 */
	@Override
	public int getNodeCount() {
		return nodeCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return edgeCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeX(int)
	 */
	@Override
	public double getNodeX(int nodeIndex) {
		return nodesPoints[nodeIndex * dim];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeY(int)
	 */
	@Override
	public double getNodeY(int nodeIndex) {
		return nodesPoints[nodeIndex * dim + 1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeZ(int)
	 */
	@Override
	public double getNodeZ(int nodeIndex) {
		return dim == 3 ? nodesPoints[nodeIndex * dim + 2] : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeXYZ(int, double[])
	 */
	@Override
	public double[] getNodeXYZ(int nodeIndex, double[] xyz) {
		if (xyz == null || xyz.length < dim)
			xyz = new double[dim];

		for (int i = 0; i < dim; i++)
			xyz[i] = nodesPoints[nodeIndex * dim + i];

		return xyz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodesXYZ(double[], boolean)
	 */
	@Override
	public double[] getNodesXYZ(double[] buffer, boolean direct) {
		if (direct)
			return nodesPoints;

		if (buffer == null || buffer.length < nodeCount * dim)
			buffer = new double[nodeCount * dim];

		for (int i = 0; i < nodeCount * dim; i++)
			buffer[i] = nodesPoints[i];

		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeX(int, double)
	 */
	@Override
	public void setNodeX(int nodeIndex, double x) {
		nodesPoints[nodeIndex * dim] = x;
		fireNodeMoved(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeY(int, double)
	 */
	@Override
	public void setNodeY(int nodeIndex, double y) {
		nodesPoints[nodeIndex * dim + 1] = y;
		fireNodeMoved(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeZ(int, double)
	 */
	@Override
	public void setNodeZ(int nodeIndex, double z) {
		if (dim == 3) {
			nodesPoints[nodeIndex * dim + 2] = z;
			fireNodeMoved(nodeIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeXYZ(int, double[])
	 */
	@Override
	public void setNodeXYZ(int nodeIndex, double[] xyz) {
		for (int i = 0; i < Math.min(xyz.length, dim); i++)
			nodesPoints[nodeIndex * dim + i] = xyz[i];

		fireNodeMoved(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeSource(int)
	 */
	@Override
	public UIElementIndex getEdgeSource(int edgeIndex) {
		return edgesData[edgeIndex].source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeTarget(int)
	 */
	@Override
	public UIElementIndex getEdgeTarget(int edgeIndex) {
		return edgesData[edgeIndex].target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#isEdgeDirected(int)
	 */
	@Override
	public boolean isEdgeDirected(int edgeIndex) {
		return edgesData[edgeIndex].directed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgePoints(int)
	 */
	@Override
	public Point3[] getEdgePoints(int edgeIndex) {
		return edgesData[edgeIndex].points;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgePointsType(int)
	 */
	@Override
	public EdgePointsType getEdgePointsType(int edgeIndex) {
		return edgesData[edgeIndex].pointsType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setEdgePoints(int, double[])
	 */
	@Override
	public void setEdgePoints(int edgeIndex, EdgePointsType type,
			Point3[] points) {
		edgesData[edgeIndex].points = points;
		edgesData[edgeIndex].pointsType = type;
		
		fireEdgePointsChanged(edgeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#addDatasetListener(org.graphstream.nui.
	 * dataset.DatasetListener)
	 */
	@Override
	public void addDatasetListener(DatasetListener l) {
		listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#removeDatasetListener(org.graphstream.nui
	 * .dataset.DatasetListener)
	 */
	@Override
	public void removeDatasetListener(DatasetListener l) {
		listeners.remove(l);
	}

	protected void setDimension(int dim) {
		if (this.dim == dim)
			return;

		if (dim != 2 && dim != 3) {
			Logger.getLogger(getClass().getName()).warning(
					"invalid dimension " + dim);

			return;
		}

		double[] tmp = new double[(nodeCount + nodeGrowStep) * dim];

		for (int i = 0; i < nodeCount; i++) {
			tmp[i * dim] = nodesPoints[i * this.dim];
			tmp[i * dim + 1] = nodesPoints[i * this.dim + 1];

			if (dim == 3)
				tmp[i * dim + 2] = 0;
		}

		nodesPoints = tmp;
	}

	protected void fireNodeMoved(int nodeIndex) {
		UIElementIndex index = indexer.getNodeIndex(nodeIndex);
		double x = getNodeX(nodeIndex);
		double y = getNodeY(nodeIndex);
		double z = getNodeZ(nodeIndex);

		Logger.getGlobal().info(
				"node \"" + nodeIndex + "\" moved to (" + x + "," + y + "," + z
						+ ")");

		for (DatasetListener l : listeners)
			l.nodeMoved(index, x, y, z);
	}

	protected void fireEdgePointsChanged(int edgeIndex) {

	}

	class CoordinatesListener extends SinkAdapter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#nodeAttributeAdded(java.lang.String
		 * , long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		@Override
		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attributeId, Object value) {
			checkCoordinates(nodeId, attributeId, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#nodeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attributeId, Object oldValue,
				Object newValue) {
			checkCoordinates(nodeId, attributeId, newValue);
		}

		private void checkCoordinates(String nodeId, String attributeId,
				Object value) {
			char c1, c2, c3;

			switch (attributeId.length()) {
			case 1:
				c1 = attributeId.charAt(0);

				if (c1 == 'x' || c1 == 'X') {
					double x = Tools.checkAndGetDouble(value);
					UIElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeX(index.index(), x);
				} else if (c1 == 'y' || c1 == 'Z') {
					double y = Tools.checkAndGetDouble(value);
					UIElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeY(index.index(), y);
				} else if (c1 == 'z' || c1 == 'Z') {
					double z = Tools.checkAndGetDouble(value);
					UIElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeZ(index.index(), z);
				}

				break;
			case 2:
				c1 = attributeId.charAt(0);
				c2 = attributeId.charAt(1);

				if ((c1 == 'x' || c1 == 'X') && (c2 == 'y' || c2 == 'Y')) {
					double[] xy = Tools.checkAndGetDoubleArray(value);
					UIElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeXYZ(index.index(), xy);
				}

				break;
			case 3:
				c1 = attributeId.charAt(0);
				c2 = attributeId.charAt(1);
				c3 = attributeId.charAt(2);

				if ((c1 == 'x' || c1 == 'X') && (c2 == 'y' || c2 == 'Y')
						&& (c3 == 'z' || c3 == 'Z')) {
					double[] xyz = Tools.checkAndGetDoubleArray(value);
					UIElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeXYZ(index.index(), xyz);
				}

				break;
			default:
				break;
			}
		}
	}

	class ElementListener implements IndexerListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#nodeAdded(org.graphstream
		 * .nui.indexer.ElementIndex)
		 */
		@Override
		public void nodeAdded(UIElementIndex nodeIndex) {
			nodeCount++;

			if (nodeCount * dim >= nodesPoints.length)
				nodesPoints = Arrays.copyOf(nodesPoints, nodesPoints.length
						+ dim * nodeGrowStep);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#nodeRemoved(org.graphstream
		 * .nui.indexer.ElementIndex)
		 */
		@Override
		public void nodeRemoved(UIElementIndex nodeIndex) {
			nodeCount--;

			//
			// Reduce the buffer when it is too big.
			//
			if (nodeCount * dim < nodesPoints.length / 2
					&& nodeGrowStep * dim < nodesPoints.length / 4)
				nodesPoints = Arrays.copyOf(nodesPoints,
						(nodeCount + nodeGrowStep) * dim);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#nodesSwapped(org.graphstream
		 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
		 */
		@Override
		public void nodesSwapped(UIElementIndex nodeIndex1,
				UIElementIndex nodeIndex2) {
			double x, y;
			int i1 = nodeIndex1.index() * dim;
			int i2 = nodeIndex2.index() * dim;

			x = nodesPoints[i1];
			y = nodesPoints[i1 + 1];

			nodesPoints[i1] = nodesPoints[i2];
			nodesPoints[i2] = x;

			nodesPoints[i1 + 1] = nodesPoints[i2 + 1];
			nodesPoints[i2 + 1] = y;

			if (dim == 3) {
				double z = nodesPoints[i1 + 2];

				nodesPoints[i1 + 2] = nodesPoints[i2 + 2];
				nodesPoints[i2 + 2] = z;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#edgeAdded(org.graphstream
		 * .nui.indexer.ElementIndex)
		 */
		@Override
		public void edgeAdded(UIElementIndex edgeIndex, UIElementIndex sourceIndex,
				UIElementIndex targetIndex, boolean directed) {
			edgeCount++;

			if (edgeCount >= edgesData.length)
				edgesData = Arrays.copyOf(edgesData, edgesData.length
						+ edgeGrowStep);

			edgesData[edgeIndex.index()] = new EdgeData(sourceIndex,
					targetIndex, directed);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#edgeRemoved(org.graphstream
		 * .nui.indexer.ElementIndex)
		 */
		@Override
		public void edgeRemoved(UIElementIndex edgeIndex) {
			edgeCount--;

			edgesData[edgeIndex.index()] = null;

			//
			// Reduce the buffer when it is too big.
			//
			if (edgeCount < edgesData.length / 2
					&& edgeGrowStep < edgesData.length / 4)
				edgesData = Arrays.copyOf(edgesData, edgeCount + edgeGrowStep);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#edgesSwapped(org.graphstream
		 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
		 */
		@Override
		public void edgesSwapped(UIElementIndex edgeIndex1,
				UIElementIndex edgeIndex2) {
			EdgeData ed = edgesData[edgeIndex1.index()];

			edgesData[edgeIndex1.index()] = edgesData[edgeIndex2.index()];
			edgesData[edgeIndex2.index()] = ed;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.IndexerListener#elementsClear()
		 */
		@Override
		public void elementsClear() {
			nodeCount = 0;
			edgeCount = 0;

			nodesPoints = new double[nodeGrowStep * dim];
			edgesData = new EdgeData[edgeGrowStep];
		}
	}

	class EdgeData {
		final UIElementIndex source;
		final UIElementIndex target;
		final boolean directed;
		Point3[] points;
		EdgePointsType pointsType;

		EdgeData(UIElementIndex s, UIElementIndex t, boolean d) {
			source = s;
			target = t;
			directed = d;
			points = null;
		}
	}
}
