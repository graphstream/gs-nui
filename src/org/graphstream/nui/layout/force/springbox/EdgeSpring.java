/*
 * Copyright 2006 - 2013
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
package org.graphstream.nui.layout.force.springbox;

import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.EdgeIndex;
import org.graphstream.ui.geom.Vector3;

/**
 * Edge representation.
 * 
 * <p>
 * This is mainly used to store data about an edge, all the computation is done
 * in the node particle.
 * </p>
 */
public class EdgeSpring {
	/**
	 * The edge identifier.
	 */
	public final EdgeIndex index;

	/**
	 * Edge weight.
	 */
	public double weight = 1f;

	/**
	 * The attraction force on this edge.
	 */
	public Vector3 spring = new Vector3();

	/**
	 * Make this edge ignored by the layout algorithm ?.
	 */
	public boolean ignored = false;

	/**
	 * The edge attraction energy.
	 */
	public double attE;

	/**
	 * New edge between two given nodes.
	 * 
	 * @param index
	 *            The edge identifier.
	 */
	public EdgeSpring(EdgeIndex index) {
		this.index = index;
	}

	/**
	 * Considering the two nodes of the edge, return the one that was not given
	 * as argument.
	 * 
	 * @param node
	 *            One of the nodes of the edge.
	 * @return The other node.
	 */
	public ElementIndex getOpposite(ElementIndex node) {
		return index.getSource() == node ? index.getTarget() : index
				.getSource();
	}
}