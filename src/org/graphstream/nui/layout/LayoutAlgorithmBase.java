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
package org.graphstream.nui.layout;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.ModuleNotFoundException;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UILayout;
import org.graphstream.nui.UISpace;
import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.dataset.DataProvider;
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.spacePartition.SpaceCell;

public abstract class LayoutAlgorithmBase implements LayoutAlgorithm {
	private static final Logger LOGGER = Logger
			.getLogger(LayoutAlgorithmBase.class.getName());

	protected UIContext ctx;

	protected UIIndexer indexer;

	protected UISpace space;

	protected UIDataset dataset;

	protected boolean enableSpacePartition;

	protected double viewZone = 0.25;

	protected Vector3 viewZoneRadius;

	protected UISpacePartition spacePartition;

	protected DataProvider dataProvider;

	protected boolean publishNeeded = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.layout.LayoutAlgorithm#init(org.graphstream.nui.UIContext
	 * , org.graphstream.nui.UILayout)
	 */
	@Override
	public void init(UIContext ctx, UILayout layout) {
		this.ctx = ctx;

		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		assert indexer != null;

		space = (UISpace) ctx.getModule(UISpace.MODULE_ID);
		assert space != null;

		dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);
		assert dataset != null;

		viewZoneRadius = new Vector3();

		enableSpacePartition(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.layout.LayoutAlgorithm#enableSpacePartition(boolean)
	 */
	@Override
	public void enableSpacePartition(boolean on) {
		if (on && !enableSpacePartition) {
			try {
				ctx.loadModule(UISpacePartition.MODULE_ID);
				spacePartition = (UISpacePartition) ctx
						.getModule(UISpacePartition.MODULE_ID);

				if (spacePartition != null)
					enableSpacePartition = true;
			} catch (InstantiationException | ModuleNotFoundException e) {
				LOGGER.log(Level.SEVERE, "Cannot load space partition module",
						e);
			}
		} else if (!on && enableSpacePartition) {
			ctx.unloadModule(UISpacePartition.MODULE_ID);
			spacePartition = null;
			enableSpacePartition = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.layout.LayoutAlgorithm#release()
	 */
	@Override
	public void release() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.layout.LayoutAlgorithm#isSpacePartitionEnable()
	 */
	@Override
	public boolean isSpacePartitionEnable() {
		return enableSpacePartition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.layout.LayoutAlgorithm#getDataProvider()
	 */
	@Override
	public DataProvider getDataProvider() {
		return dataProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.layout.LayoutAlgorithm#publishNeeded()
	 */
	@Override
	public boolean publishNeeded() {
		return publishNeeded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.layout.LayoutAlgorithm#setViewZone(double)
	 */
	@Override
	public void setViewZone(double viewZone) {
		this.viewZone = viewZone;
	}

	protected void computeViewZoneRadius() {
		Bounds bounds = spacePartition.getSpace().getBounds();
		Vector3 lo = bounds.getLowestPoint();
		Vector3 hi = bounds.getHighestPoint();

		viewZoneRadius.set((hi.x() - lo.x()) * viewZone, (hi.y() - lo.y())
				* viewZone, (hi.z() - lo.z()) * viewZone);
	}

	protected boolean intersection(Vector3 p, SpaceCell cell) {
		Vector3 lo = cell.getBoundary().getLowestPoint();
		Vector3 hi = cell.getBoundary().getLowestPoint();

		double x1 = lo.x();
		double x2 = hi.x();
		double X1 = p.x() - viewZoneRadius.x();
		double X2 = p.x() + viewZoneRadius.x();

		if (X2 < x1 || X1 > x2)
			return false;

		double y1 = lo.y();
		double y2 = hi.y();
		double Y1 = p.y() - viewZoneRadius.y();
		double Y2 = p.y() + viewZoneRadius.y();

		if (Y2 < y1 || Y1 > y2)
			return false;

		if (spacePartition.getSpace().is3D()) {
			double z1 = lo.z();
			double z2 = hi.z();
			double Z1 = p.z() - viewZoneRadius.z();
			double Z2 = p.z() + viewZoneRadius.z();

			if (Z2 < z1 || Z1 > z2)
				return false;
		}

		return true;
	}
}
