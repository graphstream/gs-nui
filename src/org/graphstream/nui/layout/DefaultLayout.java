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
package org.graphstream.nui.layout;

import java.util.logging.Logger;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UILayout;
import org.graphstream.nui.UISpace;
import org.graphstream.nui.UISwapper;
import org.graphstream.nui.context.TickTask;
import org.graphstream.nui.util.Tools;

public class DefaultLayout extends AbstractModule implements UILayout {
	private static final Logger LOGGER = Logger.getLogger(DefaultLayout.class
			.getName());

	public static final String ATTRIBUTE_SPACE_PARTITION = "spacePartition";

	public static final String ATTRIBUTE_VIEW_ZONE = "viewZone";

	protected UIDataset dataset;

	protected LayoutStatistics stats;

	protected LayoutAlgorithm algorithm;

	protected boolean enableSpacePartition;

	protected double viewZone;

	public DefaultLayout() {
		super(MODULE_ID, UIIndexer.MODULE_ID, UISwapper.MODULE_ID,
				UISpace.MODULE_ID, UIDataset.MODULE_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.AbstractModule#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		super.init(ctx);

		dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);
		assert dataset != null;

		stats = new LayoutStatistics();
		ctx.addTickTask("layout", new LayoutComputation());

		algorithm = LayoutAlgorithms.getDefaultLayoutAlgorithm();
		algorithm.init(ctx, this);

		enableSpacePartition = true;
		algorithm.enableSpacePartition(enableSpacePartition);

		LOGGER.info("using layout "
				+ LayoutAlgorithms.getLayoutName(algorithm.getClass()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		ctx.removeTickTask("layout");
		super.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		super.setAttribute(key, value);

		switch (key) {
		case ATTRIBUTE_SPACE_PARTITION:
			try {
				enableSpacePartition = Tools.checkAndGetBoolean(value);
				algorithm.enableSpacePartition(enableSpacePartition);
			} catch (IllegalArgumentException e) {
				LOGGER.warning(String.format("Illegal value for %s.%s : %s",
						MODULE_ID, ATTRIBUTE_SPACE_PARTITION, value));
			}

			break;
		case ATTRIBUTE_VIEW_ZONE:
			try {
				viewZone = Tools.checkAndGetDouble(value);
			} catch (IllegalArgumentException e) {
				LOGGER.warning(String.format("Illegal value for %s.%s : %s",
						MODULE_ID, ATTRIBUTE_VIEW_ZONE, value));
			}

			break;
		default:
			break;
		}
	}

	class LayoutComputation implements TickTask {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			algorithm.compute();

			if (algorithm.publishNeeded())
				dataset.setNodesXYZ(algorithm.getDataProvider());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.context.TickTask#getCycleLength()
		 */
		@Override
		public int getCycleLength() {
			return LAYOUT_CYCLE_LENGTH;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.context.TickTask#isPeriodic()
		 */
		@Override
		public boolean isPeriodic() {
			return true;
		}
	}
}
