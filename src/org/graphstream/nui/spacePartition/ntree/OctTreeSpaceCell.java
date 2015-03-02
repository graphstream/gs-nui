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
package org.graphstream.nui.spacePartition.ntree;

import java.util.logging.Logger;

import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.space.Bounds;

public class OctTreeSpaceCell extends NTreeSpaceCell {
	private static final Logger LOGGER = Logger
			.getLogger(OctTreeSpaceCell.class.getName());

	public OctTreeSpaceCell(UISpacePartition spacePartition) {
		this(spacePartition, spacePartition.getSpace().getBounds(), null);
	}

	public OctTreeSpaceCell(UISpacePartition spacePartition, Bounds boundary,
			OctTreeSpaceCell parent) {
		super(spacePartition, boundary, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.spacePartition.ntree.NTreeSpaceCell#subdivide()
	 */
	@Override
	public void subdivide() {
		if (neighbourhood != null) {
			LOGGER.warning("Already subdivide.");
			return;
		}

		Vector3 se = boundary.getLowestPoint();
		Vector3 nw = boundary.getHighestPoint();

		double lx = se.x(), hx = nw.x(), cx = (lx + hx) / 2;
		double ly = se.y(), hy = nw.y(), cy = (ly + hy) / 2;
		double lz = se.z(), hz = nw.z(), cz = (lz + hz) / 2;

		double[][] points = { { lx, cy, lz, cx, hy, cz },
				{ cx, cy, lz, hx, hy, cz }, { lx, ly, lz, cx, cy, cz },
				{ cx, ly, lz, hx, cy, cz }, { lx, cy, cz, cx, hy, hz },
				{ cx, cy, cz, hx, hy, hz }, { lx, ly, cz, cx, cy, hz },
				{ cx, ly, cz, hx, cy, hz } };

		neighbourhood = new OctTreeSpaceCell[8];

		for (int i = 0; i < points.length; i++) {
			Vector3 l = new Vector3(points[i][0], points[i][1], points[i][2]);
			Vector3 h = new Vector3(points[i][3], points[i][4], points[i][5]);
			Bounds b = new Bounds(l, h);

			neighbourhood[i] = new OctTreeSpaceCell(spacePartition, b, this);
		}

		for (int i = 0; i < elements.size(); i++)
			insert(elements.get(i));

		elements.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.spacePartition.ntree.NTreeSpaceCell#contains(org.
	 * graphstream.nui.indexer.ElementIndex)
	 */
	@Override
	public boolean contains(ElementIndex e) {
		UIDataset dataset = spacePartition.getDataset();
		return boundary.contains(dataset.getNodeX(e), dataset.getNodeY(e),
				dataset.getNodeZ(e));
	}

}
