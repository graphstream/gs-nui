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
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.spacePartition.BaseSpaceCell;
import org.graphstream.nui.spacePartition.SpaceCell;
import org.graphstream.nui.spacePartition.TreeSpaceCell;

public abstract class NTreeSpaceCell extends BaseSpaceCell implements
		TreeSpaceCell {
	public static final double MIN_EPSILON = 1E-10;

	public static final boolean ENABLE_STRICT_SUBDIVIDE_CHECKING = false;

	protected final NTreeSpaceCell parent;

	protected NTreeSpaceCell[] neighbourhood;

	protected int elementsCount;

	protected int depth;

	protected NTreeSpaceCell(UISpacePartition spacePartition, Bounds boundary,
			NTreeSpaceCell parent) {
		super(spacePartition, boundary);

		this.parent = parent;
		this.changed = true;
		this.depth = parent == null ? 0 : parent.depth + 1;
	}

	public abstract void subdivide();

	public abstract boolean contains(ElementIndex e);

	public int getElementCount() {
		return elementsCount;
	}

	protected boolean canSubdivide() {
		if (ENABLE_STRICT_SUBDIVIDE_CHECKING) {
			Vector3 hi = boundary.getHighestPoint();
			Vector3 lo = boundary.getLowestPoint();

			return depth < 100
					&& Math.abs(hi.x() - lo.x()) > MIN_EPSILON
					&& Math.abs(hi.y() - lo.y()) > MIN_EPSILON
					&& (!spacePartition.getSpace().is3D() || Math.abs(hi.z()
							- lo.z()) > MIN_EPSILON);
		}

		return depth < 100;
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

		if ((elements.size() < spacePartition.getMaxElementsPerCell() && neighbourhood == null)
				|| !canSubdivide()) {
			elements.add(e);
			elementAdded();

			changed = true;
			return this;
		}

		if (neighbourhood == null)
			subdivide();

		for (int i = 0; i < neighbourhood.length; i++) {
			SpaceCell sc = neighbourhood[i].insert(e, x, y, z);

			if (sc != null) {
				changed = true;
				return sc;
			}
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
		if (neighbourhood != null) {
			for (int i = 0; i < neighbourhood.length; i++) {
				if (neighbourhood[i].remove(e)) {
					changed = true;
					return true;
				}
			}
		} else if (elements.contains(e)) {
			if (elements.remove(e)) {
				elementRemoved();
				changed = true;
			}

			if (parent != null)
				parent.checkMergeNeeded();

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.spacePartition.TreeSpaceCell#getParent()
	 */
	@Override
	public TreeSpaceCell getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.spacePartition.TreeSpaceCell#getChildrenCount()
	 */
	@Override
	public int getChildrenCount() {
		return neighbourhood == null ? 0 : neighbourhood.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.spacePartition.TreeSpaceCell#getChild(int)
	 */
	@Override
	public TreeSpaceCell getChild(int i) {
		if (neighbourhood == null)
			return null;

		return neighbourhood[i];
	}

	protected void checkMergeNeeded() {
		if (neighbourhood != null
				&& getElementCount() < spacePartition.getMaxElementsPerCell() / 2) {
			elements.clear();

			for (int i = 0; i < neighbourhood.length; i++) {
				elements.addAll(neighbourhood[i].elements);
				neighbourhood[i].unregister();
			}

			System.err.printf("merge\n");

			neighbourhood = null;
			register();

			if (parent != null)
				parent.checkMergeNeeded();
		}
	}

	protected void elementAdded() {
		elementsCount++;

		if (parent != null)
			parent.elementAdded();
	}

	protected void elementRemoved() {
		elementsCount--;

		if (parent != null)
			parent.elementRemoved();
	}
}
