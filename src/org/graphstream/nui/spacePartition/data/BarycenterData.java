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
package org.graphstream.nui.spacePartition.data;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.NodeIndex;
import org.graphstream.nui.spacePartition.SpaceCell;
import org.graphstream.ui.geom.Point3;

public class BarycenterData implements SpaceCellData {
	public static final SpaceCellDataFactory FACTORY = new SpaceCellDataFactory() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.spacePartition.data.SpaceCellDataFactory#
		 * createNewData(org.graphstream.nui.spacePartition.SpaceCell)
		 */
		@Override
		public SpaceCellData createNewData(SpaceCell cell) {
			return new BarycenterData();
		}
	};

	protected Point3 barycenter;
	protected double weight;
	protected double degree;

	public BarycenterData() {
		barycenter = new Point3();
		weight = 0;
		degree = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.spacePartition.data.SpaceCellData#compute(org.graphstream
	 * .nui.UIContext, org.graphstream.nui.spacePartition.SpaceCell)
	 */
	@Override
	public void compute(UIContext ctx, SpaceCell cell) {
		double x = 0;
		double y = 0;
		double z = 0;
		double[] xyz = new double[3];

		weight = 0;

		UIDataset dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);

		for (ElementIndex index : cell) {
			dataset.getNodeXYZ(index, xyz);

			x += xyz[0];
			y += xyz[1];
			z += xyz[2];

			weight += dataset.getElementWeight(index);
			degree += ((NodeIndex) index).getDegree();
		}

		x /= cell.getElementCount();
		y /= cell.getElementCount();
		z /= cell.getElementCount();

		barycenter.set(x, y, z);
	}

	public Point3 getBarycenter() {
		return barycenter;
	}

	public double getWeight() {
		return weight;
	}

	public double getDegree() {
		return degree;
	}
}
