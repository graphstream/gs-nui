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

import java.util.LinkedList;
import java.util.Vector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIIndexer;
import org.graphstream.stream.ElementSink;

public class DefaultIndexer extends AbstractModule implements UIIndexer,
		ElementSink {
	protected final IndexSet nodes;
	protected final IndexSet edges;

	protected final List<IndexerListener> listeners;

	public DefaultIndexer() {
		super(MODULE_ID);

		nodes = new IndexSet(UIElementIndex.Type.NODE);
		edges = new IndexSet(UIElementIndex.Type.EDGE);

		listeners = new LinkedList<IndexerListener>();
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
		ctx.getContextProxy().addElementSink(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		ctx.getContextProxy().removeElementSink(this);
		super.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getNodeCount()
	 */
	@Override
	public int getNodeCount() {
		return nodes.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return edges.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getSpriteCount()
	 */
	@Override
	public int getSpriteCount() {
		// TODO
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getNodeIndex(java.lang.String)
	 */
	@Override
	public UIElementIndex getNodeIndex(String nodeId) {
		return nodes.get(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getEdgeIndex(java.lang.String)
	 */
	@Override
	public UIElementIndex getEdgeIndex(String edgeId) {
		return edges.get(edgeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getSpriteIndex(java.lang.String)
	 */
	@Override
	public UIElementIndex getSpriteIndex(String spriteId) {
		// TODO
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getNodeIndex(int)
	 */
	@Override
	public UIElementIndex getNodeIndex(int nodeIndex) {
		return nodes.get(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getEdgeIndex(int)
	 */
	@Override
	public UIElementIndex getEdgeIndex(int edgeIndex) {
		return edges.get(edgeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getSpriteIndex(int)
	 */
	@Override
	public UIElementIndex getSpriteIndex(int spriteIndex) {
		// TODO
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIIndexer#addIndexerListener(org.graphstream.nui.
	 * indexer.IndexerListener)
	 */
	@Override
	public void addIndexerListener(IndexerListener l) {
		listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIIndexer#removeIndexerListener(org.graphstream.nui
	 * .indexer.IndexerListener)
	 */
	@Override
	public void removeIndexerListener(IndexerListener l) {
		listeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long,
	 * java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String node1Id, String node2Id, boolean directed) {
		UIElementIndex source = nodes.get(node1Id);
		UIElementIndex target = nodes.get(node2Id);

		if (source == null)
			source = nodes.add(node1Id);

		if (target == null)
			target = nodes.add(node2Id);

		edges.add(edgeId, source, target, directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		edges.remove(edgeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String,
	 * long)
	 */
	@Override
	public void graphCleared(String arg0, long arg1) {
		for (IndexerListener l : listeners)
			l.elementsClear();

		edges.clear();
		nodes.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long,
	 * java.lang.String)
	 */
	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		nodes.add(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String,
	 * long, java.lang.String)
	 */
	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		nodes.remove(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String,
	 * long, double)
	 */
	@Override
	public void stepBegins(String arg0, long arg1, double arg2) {
	}

	protected void fireElementAdded(UIElementIndex.Type type, UIElementIndex index,
			Object... args) {
		switch (type) {
		case NODE:
			for (IndexerListener l : listeners)
				l.nodeAdded(index);
			break;
		case EDGE:
			for (IndexerListener l : listeners)
				l.edgeAdded(index, (UIElementIndex) args[0],
						(UIElementIndex) args[1], (Boolean) args[2]);
			break;
		default:
			// TODO
			throw new UnsupportedOperationException();
		}
	}

	protected void fireElementsSwap(UIElementIndex.Type type, UIElementIndex e1,
			UIElementIndex e2) {
		switch (type) {
		case NODE:
			for (IndexerListener l : listeners)
				l.nodesSwapped(e1, e2);
			break;
		case EDGE:
			for (IndexerListener l : listeners)
				l.edgesSwapped(e1, e2);
			break;
		default:
			// TODO
			throw new UnsupportedOperationException();
		}
	}

	protected void fireElementRemoved(UIElementIndex.Type type, UIElementIndex index) {
		switch (type) {
		case NODE:
			for (IndexerListener l : listeners)
				l.nodeRemoved(index);
			break;
		case EDGE:
			for (IndexerListener l : listeners)
				l.edgeRemoved(index);
			break;
		default:
			// TODO
			throw new UnsupportedOperationException();
		}
	}

	/*
	 * Object who manages a set of element indexes.
	 */
	private class IndexSet {
		final UIElementIndex.Type type;
		final Map<String, IEIndex> id2index;
		final List<IEIndex> indexes;

		IndexSet(UIElementIndex.Type indexSetType) {
			type = indexSetType;
			id2index = new HashMap<String, IEIndex>();
			indexes = new Vector<IEIndex>();
		}

		int size() {
			return indexes.size();
		}

		UIElementIndex get(String id) {
			return id2index.get(id);
		}

		UIElementIndex get(int index) {
			if (index >= indexes.size())
				return null;

			return indexes.get(index);
		}

		UIElementIndex add(String id, Object... args) {
			IEIndex index = new IEIndex(type, id, indexes.size());

			id2index.put(id, index);
			indexes.add(index);

			fireElementAdded(type, index, args);

			return index;
		}

		UIElementIndex remove(String id) {
			IEIndex index = id2index.get(id);

			if (index == null)
				return null;

			if (index.index < indexes.size() - 1) {
				IEIndex last = indexes.get(indexes.size() - 1);

				last.index = index.index;
				index.index = indexes.size() - 1;
				indexes.set(last.index, last);
				indexes.set(index.index, index);

				fireElementsSwap(type, last, index);
			}

			fireElementRemoved(type, index);
			indexes.remove(indexes.size() - 1);

			index.index = -1;

			return id2index.remove(id);
		}

		void clear() {
			id2index.clear();
			indexes.clear();
		}
	}

	/*
	 * Internal implementation of ElementIndex.
	 */
	private class IEIndex implements UIElementIndex {
		private final String id;
		private final Type type;
		private int index;

		IEIndex(Type type, String id, int index) {
			this.type = type;
			this.id = id;
			this.index = index;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.util.ElementIndex#id()
		 */
		@Override
		public String id() {
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.util.ElementIndex#index()
		 */
		@Override
		public int index() {
			return index;
		}

		@Override
		public Type getType() {
			return type;
		}
	}
}
