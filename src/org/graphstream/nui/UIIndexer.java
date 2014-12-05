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

import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.IndexerListener;

/**
 * 
 */
public interface UIIndexer extends UIModule {
	public static final String MODULE_ID = "indexer";
	public static final int MODULE_PRIORITY = LOW_PRIORITY;

	/**
	 * Get the index of the graph.
	 * 
	 * @return
	 */
	ElementIndex getGraphIndex();

	/**
	 * Get the count of nodes indexed in this indexer.
	 * 
	 * @return node count
	 */
	int getNodeCount();

	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	ElementIndex getNodeIndex(String nodeId);

	/**
	 * 
	 * @param nodeIndex
	 * @return null if index is out of bounds
	 */
	ElementIndex getNodeIndex(int nodeIndex);

	/**
	 * Get the count of edges indexed in this indexer.
	 * 
	 * @return edge count
	 */
	int getEdgeCount();

	/**
	 * 
	 * @param edgeId
	 * @return
	 */
	ElementIndex getEdgeIndex(String edgeId);

	/**
	 * 
	 * @param edgeIndex
	 * @return
	 */
	ElementIndex getEdgeIndex(int edgeIndex);

	/**
	 * Get the count of sprites indexed in this indexer.
	 * 
	 * @return sprite count
	 */
	int getSpriteCount();

	/**
	 * 
	 * @param spriteId
	 * @return
	 */
	ElementIndex getSpriteIndex(String spriteId);

	/**
	 * 
	 * @param spriteIndex
	 * @return
	 */
	ElementIndex getSpriteIndex(int spriteIndex);

	/**
	 * 
	 * @param l
	 */
	void addIndexerListener(IndexerListener l);

	/**
	 * 
	 * @param l
	 */
	void removeIndexerListener(IndexerListener l);
}
