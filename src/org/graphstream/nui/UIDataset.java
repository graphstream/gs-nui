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

import org.graphstream.nui.data.DataFactory;
import org.graphstream.nui.data.EdgeData;
import org.graphstream.nui.data.ElementData;
import org.graphstream.nui.data.GraphData;
import org.graphstream.nui.data.NodeData;
import org.graphstream.nui.data.SpriteData;
import org.graphstream.nui.style.ElementStyle;

/**
 * Define a container of data needed to draw the graph.
 * 
 */
public interface UIDataset {
	/**
	 * Init this dataset.
	 * 
	 * @param viewer
	 *            the viewer which will provide data
	 */
	void init(Viewer viewer);

	/**
	 * Call when this dataset is released by its owner. Can be used to release
	 * any resources associated with the dataset.
	 */
	void release();

	/**
	 * Get the current viewer.
	 * 
	 * @return the viewer
	 */
	Viewer getViewer();

	/**
	 * Get the object modeling the data directly associated to the graph.
	 * 
	 * @return the graph data
	 */
	GraphData getGraphData();

	/**
	 * Get the current node count in this dataset.
	 * 
	 * @return
	 */
	int getNodeCount();

	/**
	 * Get the current edge count in this dataset.
	 * 
	 * @return
	 */
	int getEdgeCount();

	/**
	 * Get the current sprite count in this dataset.
	 * 
	 * @return
	 */
	int getSpriteCount();

	/**
	 * Convert a string id to the current index of the attached node data. This
	 * index may change over time.
	 * 
	 * @param id
	 * @return
	 */
	int getNodeIndex(String id);

	/**
	 * Convert a string id to the current index of the attached edge data. This
	 * index may change over time.
	 * 
	 * @param id
	 * @return
	 */
	int getEdgeIndex(String id);

	/**
	 * Convert a string id to the current index of the attached sprite data.
	 * This index may change over time.
	 * 
	 * @param id
	 * @return
	 */
	int getSpriteIndex(String id);

	/**
	 * Get the index of the source node of an edge.
	 * 
	 * @param idx
	 *            index of the edge
	 * @return index of the source of the edge
	 */
	int getEdgeSource(int idx);

	/**
	 * Get the index of the target node of an edge
	 * 
	 * @param idx
	 *            index of the edge
	 * @return index of the target of the edge
	 */
	int getEdgeTarget(int idx);

	/**
	 * Get the x-coordinate of a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return the abscissa of the node
	 */
	double getNodeX(int idx);

	/**
	 * Get the y-coordinate of a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return the ordinate of the node
	 */
	double getNodeY(int idx);

	/**
	 * Get the z-coordinate of a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return the depth of the node
	 */
	double getNodeZ(int idx);

	/**
	 * Get the three coordinates of the node. To improve performances, one can
	 * give a second argument which is an already created array. This allows to
	 * re-use an array. If this array is null, a new one is created.
	 * 
	 * @param idx
	 *            index of the node
	 * @param xyz
	 *            optional existing array or null to create a new one
	 * @return an array containing the three coordinates of the node.
	 */
	double[] getNodeXYZ(int idx, double[] xyz);

	/**
	 * Get the coordinates of all nodes. If direct is true, dataset will try to
	 * give a direct access to its buffers (if possible) to reduce array copies.
	 * If buffer is not null, it will be used as destination buffer. Note that
	 * giving a non-null buffer while setting direct to true is a non-sense.
	 * 
	 * @param buffer
	 *            optional buffer that can be used to copy coordinates
	 * @param direct
	 *            flag to define if dataset should try to give direct access to
	 *            increase performances
	 * @return
	 */
	double[] getNodesXYZ(double[] buffer, boolean direct);

	/**
	 * Set the abscissa of the node with index idx.
	 * 
	 * @param idx
	 *            index of the node
	 * @param x
	 *            abscissa of the node
	 */
	void setNodeX(int idx, double x);

	/**
	 * Set the ordinate of the node with index idx.
	 * 
	 * @param idx
	 *            index of the node
	 * @param y
	 *            ordinate of the node
	 */
	void setNodeY(int idx, double y);

	/**
	 * Set the depth of the node with index idx.
	 * 
	 * @param idx
	 *            index of the node
	 * @param z
	 *            depth of the node
	 */
	void setNodeZ(int idx, double z);

	/**
	 * Set the three coordinates of the node idx in one call. If the size of the
	 * array is less than 3, then the method start by filling the x-coordinate,
	 * then the y-coordinate, and the z-coordinate, until no more value is
	 * available.
	 * 
	 * @param idx
	 *            index of the node
	 * @param xyz
	 *            array of coordinate
	 */
	void setNodeXYZ(int idx, double... xyz);

	/**
	 * Get the red component of the color of a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return red color component
	 */
	int getNodeR(int idx);

	/**
	 * Get the green component of the color of a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return green color component
	 */
	int getNodeG(int idx);

	/**
	 * Get the blue component of the color of a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return blue color component
	 */
	int getNodeB(int idx);

	/**
	 * Get the opacity of the fill color of a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return fill opacity
	 */
	int getNodeA(int idx);

	/**
	 * Get the four color components of a node pack in a single integer.
	 * 
	 * @param idx
	 *            index of the node
	 * @return an integer in the ARGB style
	 */
	int getNodeARGB(int idx);

	/**
	 * Get the color components of all nodes. If direct is true, dataset will
	 * try to give a direct access to its buffers (if possible) to reduce array
	 * copies. If buffer is not null, it will be used as destination buffer.
	 * Note that giving a non-null buffer while setting direct to true is a
	 * non-sense.
	 * 
	 * @param buffer
	 *            optional buffer that can be used to copy color components
	 * @param direct
	 *            flag to define if dataset should try to give direct access to
	 *            increase performances
	 * @return
	 */
	int[] getNodesARGB(int[] buffer, boolean direct);

	/**
	 * Get the red component of the color of an edge.
	 * 
	 * @param idx
	 *            index of the edge
	 * @return red color component
	 */
	int getEdgeR(int idx);

	/**
	 * Get the green component of the color of an edge.
	 * 
	 * @param idx
	 *            index of the edge
	 * @return green color component
	 */
	int getEdgeG(int idx);

	/**
	 * Get the blue component of the color of an edge.
	 * 
	 * @param idx
	 *            index of the edge
	 * @return blue color component
	 */
	int getEdgeB(int idx);

	/**
	 * Get the opacity of the fill color of an edge.
	 * 
	 * @param idx
	 *            index of the edge
	 * @return fill opacity
	 */
	int getEdgeA(int idx);

	/**
	 * Get the four color components of an edge pack in a single integer.
	 * 
	 * @param idx
	 *            index of the edge
	 * @return an integer in the ARGB style
	 */
	int getEdgeARGB(int idx);

	/**
	 * Get the color components of all nodes. If direct is true, dataset will
	 * try to give a direct access to its buffers (if possible) to reduce array
	 * copies. If buffer is not null, it will be used as destination buffer.
	 * Note that giving a non-null buffer while setting direct to true is a
	 * non-sense.
	 * 
	 * @param buffer
	 *            optional buffer that can be used to copy color components
	 * @param direct
	 *            flag to define if dataset should try to give direct access to
	 *            increase performances
	 * @return
	 */
	int[] getEdgesARGB(int[] buffer, boolean direct);

	/**
	 * Get data attached to a node.
	 * 
	 * @param idx
	 *            index of the node
	 * @return attached data
	 */
	NodeData getNodeData(int idx);

	/**
	 * Get data attached to an edge.
	 * 
	 * @param idx
	 *            index of the edge
	 * @return attached data
	 */
	EdgeData getEdgeData(int idx);

	/**
	 * Get data attached to a sprite.
	 * 
	 * @param idx
	 *            index of the sprite
	 * @return attached data
	 */
	SpriteData getSpriteData(int idx);

	void elementDataUpdated(ElementData data);

	void applyNodeStyle(int idx, ElementStyle style);

	void applyEdgeStyle(int idx, ElementStyle style);

	void applySpriteStyle(int idx, ElementStyle style);

	/**
	 * Add a listener to this dataset.
	 * 
	 * @param l
	 *            the new listener
	 */
	void addUIDatasetListener(UIDatasetListener l);

	/**
	 * Remove a listener from this dataset.
	 * 
	 * @param l
	 *            the listener to remove
	 */
	void removeUIDatasetListener(UIDatasetListener l);

	/**
	 * Set the factory used to create data for elements.
	 * 
	 * @param factory
	 *            the new factory
	 */
	void setDataFactory(DataFactory factory);
}
