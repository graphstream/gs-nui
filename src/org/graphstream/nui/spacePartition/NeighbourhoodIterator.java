/*
 * Copyright 2006 - 2015
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
package org.graphstream.nui.spacePartition;

import java.util.Iterator;

import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.space.Bounds;
import org.graphstream.ui.geom.Point3;

public class NeighbourhoodIterator implements Iterator<ElementIndex> {
	protected final UISpacePartition spacePartition;
	protected final ElementIndex index;
	protected final Iterator<SpaceCell> cells;
	protected final double x;
	protected final double y;
	protected final double z;
	protected final Point3 radius;

	protected Iterator<ElementIndex> elements;
	protected ElementIndex nextElement;

	public NeighbourhoodIterator(UISpacePartition spacePartition,
			ElementIndex index, double viewZone) {
		this.spacePartition = spacePartition;
		this.index = index;
		this.cells = spacePartition.iterator();
		this.x = spacePartition.getDataset().getNodeX(index);
		this.y = spacePartition.getDataset().getNodeY(index);
		this.z = spacePartition.getDataset().getNodeZ(index);
		this.radius = radius(viewZone);

		findNextCell();
		findNextElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return nextElement != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ElementIndex next() {
		ElementIndex r = nextElement;
		findNextElement();

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
	}

	protected void findNextCell() {
		SpaceCell next = null;

		while (next == null && cells.hasNext()) {
			next = cells.next();

			if (!intersection(next))
				next = null;
		}

		elements = next == null ? null : next.iterator();
	}

	protected void findNextElement() {
		while ((elements == null || !elements.hasNext()) && cells.hasNext())
			findNextCell();

		if (elements == null) {
			nextElement = null;
			return;
		}

		nextElement = elements.next();

		if (nextElement == index)
			findNextElement();
	}

	protected Point3 radius(double viewZone) {
		Bounds bounds = spacePartition.getSpace().getBounds();
		Point3 lo = bounds.getLowestPoint();
		Point3 hi = bounds.getHighestPoint();

		Point3 r = new Point3((hi.x - lo.x) * viewZone, (hi.y - lo.y)
				* viewZone, (hi.z - lo.z) * viewZone);

		return r;
	}

	protected boolean intersection(SpaceCell cell) {
		Point3 lo = cell.getBoundary().getLowestPoint();
		Point3 hi = cell.getBoundary().getLowestPoint();

		double x1 = lo.x;
		double x2 = hi.x;
		double X1 = x - radius.x;
		double X2 = x + radius.x;

		if (X2 < x1 || X1 > x2)
			return false;

		double y1 = lo.y;
		double y2 = hi.y;
		double Y1 = y - radius.y;
		double Y2 = y + radius.y;

		if (Y2 < y1 || Y1 > y2)
			return false;

		if (spacePartition.getSpace().is3D()) {
			double z1 = lo.z;
			double z2 = hi.z;
			double Z1 = z - radius.z;
			double Z2 = z + radius.z;

			if (Z2 < z1 || Z1 > z2)
				return false;
		}

		return true;
	}
}