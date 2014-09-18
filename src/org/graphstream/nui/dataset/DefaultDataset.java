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

import java.nio.DoubleBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UISwapper;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.EdgeIndex;
import org.graphstream.nui.indexer.ElementIndex.Type;
import org.graphstream.nui.swapper.UIArrayReference;
import org.graphstream.nui.swapper.UIBufferReference;
import org.graphstream.nui.util.Tools;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.ui.geom.Point3;

/**
 * 
 */
public class DefaultDataset extends AbstractModule implements UIDataset {
	protected int dim;
	protected UIBufferReference nodesPoints;
	protected UIArrayReference<EdgeData> edgesData;
	protected UIIndexer indexer;
	protected List<DatasetListener> listeners;
	protected CoordinatesListener coordinatesListener;

	public DefaultDataset() {
		super(MODULE_ID, UIIndexer.MODULE_ID);

		listeners = new LinkedList<DatasetListener>();
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

		UISwapper swapper = (UISwapper) ctx.getModule(UISwapper.MODULE_ID);

		indexer = (UIIndexer) ctx.getModule("indexer");

		ctx.getContextProxy().addAttributeSink(coordinatesListener);

		dim = 3;

		nodesPoints = swapper.createBuffer(Type.NODE, dim, Double.SIZE / 8,
				true, null);

		edgesData = swapper.createArray(Type.EDGE, 1, EdgeData.class,
				new UISwapper.ValueFactory<EdgeData>() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.graphstream.nui.UISwapper.ValueFactory#createValue
					 * (org.graphstream.nui.indexer.ElementIndex,int)
					 */
					@Override
					public EdgeData createValue(ElementIndex index,
							int component) {
						return new EdgeData();
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
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
		return indexer.getNodeCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return indexer.getEdgeCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeX(int)
	 */
	@Override
	public double getNodeX(ElementIndex nodeIndex) {
		return nodesPoints.getDouble(nodeIndex, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeY(int)
	 */
	@Override
	public double getNodeY(ElementIndex nodeIndex) {
		return nodesPoints.getDouble(nodeIndex, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeZ(int)
	 */
	@Override
	public double getNodeZ(ElementIndex nodeIndex) {
		return dim == 3 ? nodesPoints.getDouble(nodeIndex, 2) : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodeXYZ(int, double[])
	 */
	@Override
	public double[] getNodeXYZ(ElementIndex nodeIndex, double[] xyz) {
		if (xyz == null || xyz.length < dim)
			xyz = new double[dim];

		for (int i = 0; i < dim; i++)
			xyz[i] = nodesPoints.getDouble(nodeIndex, i);

		return xyz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getNodesXYZ()
	 */
	@Override
	public DoubleBuffer getNodesXYZ() {
		return nodesPoints.buffer().asDoubleBuffer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeX(int, double)
	 */
	@Override
	public void setNodeX(ElementIndex nodeIndex, double x) {
		nodesPoints.setDouble(nodeIndex, 0, x);
		fireNodeMoved(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeY(int, double)
	 */
	@Override
	public void setNodeY(ElementIndex nodeIndex, double y) {
		nodesPoints.setDouble(nodeIndex, 1, y);
		fireNodeMoved(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeZ(int, double)
	 */
	@Override
	public void setNodeZ(ElementIndex nodeIndex, double z) {
		if (dim == 3) {
			nodesPoints.setDouble(nodeIndex, 2, z);
			fireNodeMoved(nodeIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setNodeXYZ(int, double[])
	 */
	@Override
	public void setNodeXYZ(ElementIndex nodeIndex, double[] xyz) {
		for (int i = 0; i < Math.min(xyz.length, dim); i++)
			nodesPoints.setDouble(nodeIndex, i, xyz[i]);

		fireNodeMoved(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeSource(int)
	 */
	@Override
	public ElementIndex getEdgeSource(ElementIndex edgeIndex) {
		return ((EdgeIndex) edgeIndex).getSource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgeTarget(int)
	 */
	@Override
	public ElementIndex getEdgeTarget(ElementIndex edgeIndex) {
		return ((EdgeIndex) edgeIndex).getTarget();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#isEdgeDirected(int)
	 */
	@Override
	public boolean isEdgeDirected(ElementIndex edgeIndex) {
		return ((EdgeIndex) edgeIndex).isDirected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgePoints(int)
	 */
	@Override
	public Point3[] getEdgePoints(ElementIndex edgeIndex) {
		return edgesData.get(edgeIndex, 0).points;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getEdgePointsType(int)
	 */
	@Override
	public EdgePointsType getEdgePointsType(ElementIndex edgeIndex) {
		return edgesData.get(edgeIndex, 0).pointsType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#setEdgePoints(int, double[])
	 */
	@Override
	public void setEdgePoints(ElementIndex edgeIndex, EdgePointsType type,
			Point3[] points) {
		edgesData.get(edgeIndex, 0).points = points;
		edgesData.get(edgeIndex, 0).pointsType = type;

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

		UISwapper swapper = (UISwapper) ctx.getModule(UISwapper.MODULE_ID);
		UIBufferReference tmp = swapper.createBuffer(Type.NODE, dim,
				Double.SIZE / 8, true, null);

		for (int i = 0; i < indexer.getNodeCount(); i++) {
			ElementIndex index = indexer.getNodeIndex(i);

			tmp.setDouble(index, 0, nodesPoints.getDouble(index, 0));
			tmp.setDouble(index, 1, nodesPoints.getDouble(index, 1));

			if (dim == 3)
				tmp.setDouble(index, 2, nodesPoints.getDouble(index, 2));
		}

		nodesPoints.release();
		nodesPoints = tmp;
	}

	protected void fireNodeMoved(ElementIndex nodeIndex) {
		double x = getNodeX(nodeIndex);
		double y = getNodeY(nodeIndex);
		double z = getNodeZ(nodeIndex);

		Logger.getGlobal().info(
				"node \"" + nodeIndex + "\" moved to (" + x + "," + y + "," + z
						+ ")");

		for (DatasetListener l : listeners)
			l.nodeMoved(nodeIndex, x, y, z);
	}

	protected void fireEdgePointsChanged(ElementIndex edgeIndex) {

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
					ElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeX(index, x);
				} else if (c1 == 'y' || c1 == 'Z') {
					double y = Tools.checkAndGetDouble(value);
					ElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeY(index, y);
				} else if (c1 == 'z' || c1 == 'Z') {
					double z = Tools.checkAndGetDouble(value);
					ElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeZ(index, z);
				}

				break;
			case 2:
				c1 = attributeId.charAt(0);
				c2 = attributeId.charAt(1);

				if ((c1 == 'x' || c1 == 'X') && (c2 == 'y' || c2 == 'Y')) {
					double[] xy = Tools.checkAndGetDoubleArray(value);
					ElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeXYZ(index, xy);
				}

				break;
			case 3:
				c1 = attributeId.charAt(0);
				c2 = attributeId.charAt(1);
				c3 = attributeId.charAt(2);

				if ((c1 == 'x' || c1 == 'X') && (c2 == 'y' || c2 == 'Y')
						&& (c3 == 'z' || c3 == 'Z')) {
					double[] xyz = Tools.checkAndGetDoubleArray(value);
					ElementIndex index = indexer.getNodeIndex(nodeId);

					setNodeXYZ(index, xyz);
				}

				break;
			default:
				break;
			}
		}
	}

	class EdgeData {
		Point3[] points;
		EdgePointsType pointsType;

		EdgeData() {
			points = null;
		}
	}
}
