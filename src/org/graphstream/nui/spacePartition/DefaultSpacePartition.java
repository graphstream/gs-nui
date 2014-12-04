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
package org.graphstream.nui.spacePartition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UISpace;
import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.spacePartition.ntree.QuadTreeSpaceCell;
import org.graphstream.nui.util.Tools;

public class DefaultSpacePartition extends AbstractModule implements
		SpaceCellHandler {
	private static final Logger LOGGER = Logger
			.getLogger(DefaultSpacePartition.class.getName());

	protected static final String ATTRIBUTE_ELEMENTS_PER_CELL = "maxElementsPerCell";
	protected static final String ATTRIBUTE_SPACE_CELL_FACTORY = "spaceCellFactory";

	protected UIIndexer indexer;

	protected UIDataset dataset;

	protected UISpace space;

	protected int maxElementsPerCell = DEFAULT_MAX_ELEMENTS_PER_CELL;

	protected final LinkedList<SpaceCell> cells;

	protected SpaceCellFactory cellFactory;

	protected SpaceCell root;

	protected DefaultSpacePartition() {
		super(MODULE_ID, UIIndexer.MODULE_ID, UIDataset.MODULE_ID,
				UISpace.MODULE_ID);

		cells = new LinkedList<SpaceCell>();
		cellFactory = new SpaceCellFactory() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.graphstream.nui.spacePartition.SpaceCellFactory#createRootCell
			 * (org.graphstream.nui.UISpacePartition)
			 */
			@Override
			public SpaceCell createRootCell(UISpacePartition spacePartition) {
				return new QuadTreeSpaceCell(spacePartition);
			}
		};
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

		dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);
		assert dataset != null;

		space = (UISpace) ctx.getModule(UISpace.MODULE_ID);
		assert space != null;

		root = cellFactory.createRootCell(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		super.release();
		dataset = null;
		space = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		if (value == null)
			return;

		switch (key) {
		case ATTRIBUTE_ELEMENTS_PER_CELL:
			try {
				maxElementsPerCell = Tools.checkAndGetInt(value);
			} catch (IllegalArgumentException e) {
				LOGGER.warning(String.format("Illegal value for %s.%s : %s",
						MODULE_ID, ATTRIBUTE_ELEMENTS_PER_CELL, value));
			}

			break;
		case ATTRIBUTE_SPACE_CELL_FACTORY:
			try {
				String cls = Tools.checkAndGetString(value, false);
				setSpaceCellFactory(cls);
			} catch (IllegalArgumentException e) {
				LOGGER.warning(String.format("Illegal value for %s.%s : %s",
						MODULE_ID, ATTRIBUTE_SPACE_CELL_FACTORY, value));
			}

			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<SpaceCell> iterator() {
		return cells.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpacePartition#getMaxElementsPerCell()
	 */
	@Override
	public int getMaxElementsPerCell() {
		return maxElementsPerCell;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpacePartition#getDataset()
	 */
	@Override
	public UIDataset getDataset() {
		return dataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UISpacePartition#getSpace()
	 */
	@Override
	public UISpace getSpace() {
		return space;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.spacePartition.SpaceCellHandler#register(org.graphstream
	 * .nui.spacePartition.SpaceCell)
	 */
	@Override
	public void register(SpaceCell cell) {
		cells.add(cell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.spacePartition.SpaceCellHandler#unregister(org.
	 * graphstream.nui.spacePartition.SpaceCell)
	 */
	@Override
	public void unregister(SpaceCell cell) {
		cells.remove(cell);
	}

	public void setSpaceCellFactory(String cls) {
		try {
			Class<?> factoryClass = Class.forName(cls);
			Constructor<?> c = factoryClass
					.getConstructor(UISpacePartition.class);

			Object obj = c.newInstance(this);

			if (SpaceCellFactory.class.isAssignableFrom(obj.getClass())) {
				LOGGER.warning("This is not a SpaceCellFactory");
				return;
			}

			cellFactory = (SpaceCellFactory) obj;
			root = cellFactory.createRootCell(this);

			for (int idx = 0; idx < indexer.getNodeCount(); idx++)
				root.insert(indexer.getNodeIndex(idx));
		} catch (ClassNotFoundException e) {
			LOGGER.severe("SpaceCellFactory class not found : " + cls);
		} catch (NoSuchMethodException e) {
			LOGGER.severe("this SpaceCellFactory does not contain the propre constructor : "
					+ cls);
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, "Something goes wrong", e);
		}
	}
}
