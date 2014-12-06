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

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.ModuleNotFoundException;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UILayout;
import org.graphstream.nui.UISpace;
import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIterator;
import org.graphstream.nui.indexer.ElementIndex.Type;
import org.graphstream.nui.spacePartition.NeighbourhoodIterator;
import org.graphstream.nui.util.Tools;

public class BaseLayout extends AbstractModule implements UILayout {
	//
	// Join two arrays of String, avoiding repeat.
	//
	private static String[] join(String[] a1, String... a2) {
		if (a1 == null)
			return a2;

		if (a2 == null)
			return a1;

		HashSet<String> a = new HashSet<String>();

		for (int i = 0; i < a1.length; i++)
			a.add(a1[i]);
		for (int i = 0; i < a2.length; i++)
			a.add(a2[i]);

		return a.toArray(new String[a.size()]);
	}

	private static final Logger LOGGER = Logger.getLogger(BaseLayout.class
			.getName());

	public static final String ATTRIBUTE_SPACE_PARTITION = "spacePartition";

	public static final String ATTRIBUTE_VIEW_ZONE = "viewZone";

	protected UIIndexer indexer;

	protected UISpace space;

	protected UISpacePartition spacePartition;

	protected LayoutStatistics stats;

	protected boolean enableSpacePartition;

	protected double viewZone;

	protected BaseLayout(String... extDeps) {
		super(MODULE_ID, join(extDeps, UIIndexer.MODULE_ID, UISpace.MODULE_ID));
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

		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		assert indexer != null;

		space = (UISpace) ctx.getModule(UISpace.MODULE_ID);
		assert space != null;

		stats = new LayoutStatistics();
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
				enableSpacePartition(Tools.checkAndGetBoolean(value));
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

	protected Iterator<ElementIndex> getNeighbourhood(ElementIndex source) {
		if (!enableSpacePartition)
			return ElementIterator.iterateOn(indexer, Type.NODE);

		return new NeighbourhoodIterator(spacePartition, source, viewZone);
	}
}
