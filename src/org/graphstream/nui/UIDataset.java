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
package org.graphstream.nui;

import java.nio.DoubleBuffer;

import org.graphstream.nui.dataset.DataProvider;
import org.graphstream.nui.dataset.DatasetListener;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.geom.Vector3;

/**
 * The dataset is one of the main objects involved in the ui since it allows to
 * store the coordinates of nodes. The idea here is to provide a fast way to get
 * the coordinates, like a direct access. The method {@link #getNodesXYZ()}
 * allows to get all the coordinates in a buffer object which can be used by an
 * opengl view for example. the aim is also to optimize the memory usage.
 * 
 * The dataset used the indexes provided by
 * {@link org.graphstream.nui.UIIndexer}.
 * 
 * @see org.graphstream.nui.UIModule
 * @see org.graphstream.nui.UIIndexer
 * @see org.graphstream.nui.dataset.DatasetListener
 */
public interface UIDataset extends UIModule {
	/**
	 * The module unique identifier.
	 */
	public static final String MODULE_ID = "dataset";
	/**
	 * Priority of this module while creating the dependencies tree.
	 */
	public static final int MODULE_PRIORITY = HIGH_PRIORITY;

	/**
	 * This enumeration defines the type of edge breaking points.
	 */
	public static enum EdgePointsType {
		/**
		 * The coordinates of the breaking points will be relative to the
		 * extremities coordinates.
		 */
		RELATIVE,
		/**
		 * The coordinates of the breaking points will be absolute, and
		 * specified in the same space than the coordinates of nodes.
		 */
		ABSOLUTE,
		/**
		 * The coordinates of breaking points will be absolute and specified in
		 * the view pixel space.
		 */
		ABSOLUTE_SCREEN
	}

	/**
	 * Return the count of nodes stored in this dataset.
	 * 
	 * @return count of nodes
	 */
	int getNodeCount();

	/**
	 * Return the count of edges stored in this dataset.
	 * 
	 * @return count of edges
	 */
	int getEdgeCount();

	int getPointDimension();

	/**
	 * Return the first coordinate of a node.
	 * 
	 * @param nodeIndex
	 *            index of the node
	 * @return the x-coordinate
	 */
	double getNodeX(ElementIndex nodeIndex);

	/**
	 * Return the second coordinate of a node.
	 * 
	 * @param nodeIndex
	 *            index of the node
	 * @return the y-coordinate
	 */
	double getNodeY(ElementIndex nodeIndex);

	/**
	 * Return the depth coordinate of a node. This only makes sense if we are
	 * using a 3d space.
	 * 
	 * @param nodeIndex
	 *            index of the node
	 * @return the z-coordinate or 0 if the space is not a 3d space
	 */
	double getNodeZ(ElementIndex nodeIndex);

	/**
	 * This method allows to optimize the retrieval of coordinates by getting
	 * them in single method call. To improve this optimization, one can used
	 * the same array for multiple calls to this method.
	 * 
	 * @param nodexIndex
	 *            index of the node
	 * @param xyz
	 *            an optional array that will be used to return the coordinate.
	 *            If this array is null or if it is too small, a new one will be
	 *            created
	 * @return the coordinates in the xyz if specified, or in a new array
	 */
	double[] getNodeXYZ(ElementIndex nodexIndex, double[] xyz);

	Vector3 getNodeXYZ(ElementIndex nodeIndex, Vector3 xyz);

	/**
	 * Get all the coordinates of all nodes in the buffer object. The main
	 * application of this is to use the buffer in a view like an opengl one.
	 * 
	 * @return a buffer object containing all the coordinates of all nodes
	 */
	DoubleBuffer getNodesXYZ();

	/**
	 * Set the abscissa of a node.
	 * 
	 * @param nodeIndex
	 *            index of the node
	 * @param x
	 *            the new x-coordinate
	 */
	void setNodeX(ElementIndex nodeIndex, double x);

	/**
	 * Set the ordinate of a node.
	 * 
	 * @param nodeIndex
	 *            index of the node
	 * @param y
	 *            the new y-coordinate
	 */
	void setNodeY(ElementIndex nodeIndex, double y);

	/**
	 * Set the depth of a node.
	 * 
	 * @param nodeIndex
	 *            index of the node
	 * @param z
	 *            the new z-coordinate
	 */
	void setNodeZ(ElementIndex nodeIndex, double z);

	/**
	 * Set all the coordinates of a node in a one call method.
	 * 
	 * @param nodexIndex
	 *            index of the node
	 * @param xyz
	 *            array containing the coordinates
	 */
	void setNodeXYZ(ElementIndex nodexIndex, double[] xyz);

	/**
	 * Set the coordinates of all nodes in a one call method. This allows to
	 * improve the event production.
	 * 
	 * @param dataProvider
	 */
	void setNodesXYZ(DataProvider dataProvider);

	/**
	 * Set the data provider who provides the default coordinates for new nodes.
	 * 
	 * @param defaultData
	 *            default data provider
	 */
	void setDefaultNodeDataProvider(DataProvider defaultData);

	/**
	 * Allows to retrieve the source node of an edge.
	 * 
	 * @param edgeIndex
	 *            index of the edge
	 * @return the source node of the edge, or just the first extremity if the
	 *         edge is not directed
	 * @see org.graphstream.nui.indexer.ElementIndex.EdgeIndex#getSource()
	 */
	ElementIndex getEdgeSource(ElementIndex edgeIndex);

	/**
	 * Allows to retrieve the target node of an edge.
	 * 
	 * @param edgeIndex
	 *            index of the edge
	 * @return the target node of the edge, or just the second extremity if the
	 *         edge is not directed
	 * @see org.graphstream.nui.indexer.ElementIndex.EdgeIndex#getTarget()
	 */
	ElementIndex getEdgeTarget(ElementIndex edgeIndex);

	/**
	 * Test if an edge is directed or not.
	 * 
	 * @param edgeIndex
	 *            index of the edge
	 * @return true if the edge is directed
	 */
	boolean isEdgeDirected(ElementIndex edgeIndex);

	/**
	 * Get the breaking points of an edge. The type of the coordinates can be
	 * retrieve by the {@link #getEdgePointsType(ElementIndex)} method.
	 * 
	 * @param edgeIndex
	 *            index of the edge
	 * @return an array of points
	 */
	Vector3[] getEdgePoints(ElementIndex edgeIndex);

	/**
	 * Get the type of the breaking points of an edge. These points can be
	 * retrieve using the {@link #getEdgePoints(ElementIndex)} method.
	 * 
	 * @param edgeIndex
	 *            index of the edge
	 * @return the type of the breaking points
	 * @see org.graphstream.nui.UIDataset.EdgePointsType
	 */
	EdgePointsType getEdgePointsType(ElementIndex edgeIndex);

	/**
	 * Set the breaking points of an edge.
	 * 
	 * @param edgeIndex
	 *            index of the edge
	 * @param type
	 *            type of coordinates
	 * @param points
	 *            the breaking points
	 */
	void setEdgePoints(ElementIndex edgeIndex, EdgePointsType type,
			Vector3[] points);

	/**
	 * Get the weight of an element.
	 * 
	 * @param index
	 *            index of the element
	 * @return the weight of the element
	 */
	double getElementWeight(ElementIndex index);

	/**
	 * Set the weight of an element.
	 * 
	 * @param index
	 *            index of the element
	 * @param weight
	 *            new weight of the element
	 */
	void setElementWeight(ElementIndex index, double weight);

	/**
	 * Add a new dataset listener to this dataset.
	 * 
	 * @param l
	 *            the new dataset listener
	 * @see org.graphstream.nui.dataset.DatasetListener
	 */
	void addDatasetListener(DatasetListener l);

	/**
	 * Remove a dataset listener from this dataset.
	 * 
	 * @param l
	 *            the dataset listener
	 * @see org.graphstream.nui.dataset.DatasetListener
	 */
	void removeDatasetListener(DatasetListener l);
}
