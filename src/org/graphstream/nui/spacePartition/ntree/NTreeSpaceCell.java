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
package org.graphstream.nui.spacePartition.ntree;

import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.spacePartition.BaseSpaceCell;
import org.graphstream.nui.spacePartition.SpaceCell;

public abstract class NTreeSpaceCell extends BaseSpaceCell {
	protected final NTreeSpaceCell parent;

	protected NTreeSpaceCell[] neighbourhood;

	protected NTreeSpaceCell(UISpacePartition spacePartition, Bounds boundary,
			NTreeSpaceCell parent) {
		super(spacePartition, boundary);
		this.parent = parent;
	}

	public abstract void subdivide();

	public abstract boolean contains(ElementIndex e);

	public int getElementCount() {
		if (neighbourhood == null)
			return elements.size();

		int s = 0;

		for (int i = 0; i < neighbourhood.length; i++)
			s += neighbourhood[i].getElementCount();

		return s;
	}

	protected void insert(ElementIndex e) {
		double x = spacePartition.getDataset().getNodeX(e);
		double y = spacePartition.getDataset().getNodeY(e);
		double z = spacePartition.getDataset().getNodeZ(e);

		insert(e, x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.spacePartition.SpaceCell#insert(org.graphstream.nui
	 * .indexer.ElementIndex, double, double, double)
	 */
	@Override
	public SpaceCell insert(ElementIndex e, double x, double y, double z) {
		if (!contains(e))
			return null;

		if (elements.size() < spacePartition.getMaxElementsPerCell()
				&& neighbourhood == null) {
			elements.add(e);
			return this;
		}

		if (neighbourhood == null) {
			subdivide();
			unregister();

			for (int i = 0; i < neighbourhood.length; i++)
				neighbourhood[i].register();
		}

		for (int i = 0; i < neighbourhood.length; i++) {
			SpaceCell sc = neighbourhood[i].insert(e, x, y, z);

			if (sc != null)
				return sc;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.spacePartition.SpaceCell#remove(org.graphstream.nui
	 * .indexer.ElementIndex)
	 */
	@Override
	public boolean remove(ElementIndex e) {
		if (!contains(e))
			return false;

		if (elements.contains(e)) {
			elements.remove(e);

			if (parent != null)
				parent.checkMergeNeeded();

			return true;
		} else if (neighbourhood != null) {
			for (int i = 0; i < neighbourhood.length; i++) {
				if (neighbourhood[i].remove(e))
					return true;
			}
		}

		return false;
	}

	protected void checkMergeNeeded() {
		if (neighbourhood != null
				&& getElementCount() < spacePartition.getMaxElementsPerCell() / 2) {
			elements.clear();

			for (int i = 0; i < neighbourhood.length; i++)
				elements.addAll(neighbourhood[i].elements);

			neighbourhood = null;
			register();
		}
	}
}
