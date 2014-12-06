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
package org.graphstream.nui.indexer;

import java.util.Iterator;

import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.indexer.ElementIndex.Type;

public abstract class ElementIterator implements Iterator<ElementIndex> {
	public static Iterator<ElementIndex> iterateOn(UIIndexer indexer,
			Type type) {
		switch (type) {
		case EDGE:
			return new EdgeIterator(indexer);
		case NODE:
			return new NodeIterator(indexer);
		case SPRITE:
			return new SpriteIterator(indexer);
		default:
			break;
		}

		return null;
	}

	protected final ElementIndex.Type type;

	protected final UIIndexer indexer;

	protected int cursor;

	public ElementIterator(UIIndexer indexer, ElementIndex.Type type) {
		this.indexer = indexer;
		this.type = type;
		this.cursor = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return cursor < count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ElementIndex next() {
		return get(cursor++);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
	}

	protected abstract int count();

	protected abstract ElementIndex get(int index);

	public static class NodeIterator extends ElementIterator {
		public NodeIterator(UIIndexer indexer) {
			super(indexer, ElementIndex.Type.NODE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIterator#count()
		 */
		@Override
		protected int count() {
			return indexer.getNodeCount();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIterator#get(int)
		 */
		@Override
		protected ElementIndex get(int index) {
			return indexer.getNodeIndex(index);
		}
	}

	public static class EdgeIterator extends ElementIterator {

		public EdgeIterator(UIIndexer indexer) {
			super(indexer, ElementIndex.Type.EDGE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIterator#count()
		 */
		@Override
		protected int count() {
			return indexer.getEdgeCount();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIterator#get(int)
		 */
		@Override
		protected ElementIndex get(int index) {
			return indexer.getEdgeIndex(index);
		}
	}

	public static class SpriteIterator extends ElementIterator {
		public SpriteIterator(UIIndexer indexer) {
			super(indexer, Type.SPRITE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIterator#count()
		 */
		@Override
		protected int count() {
			return indexer.getSpriteCount();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIterator#get(int)
		 */
		@Override
		protected ElementIndex get(int index) {
			return indexer.getSpriteIndex(index);
		}
	}
}
