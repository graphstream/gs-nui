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
import java.util.List;

import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AbstractNode;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.AdjacencyListNode;
import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIIndexer;

public class DefaultIndexer extends AbstractModule implements UIIndexer {
	protected final List<IndexerListener> listeners;

	protected final Indexes struct;

	public DefaultIndexer() {
		super(MODULE_ID);

		struct = new Indexes();
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
		ctx.getContextProxy().addElementSink(struct);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		ctx.getContextProxy().removeElementSink(struct);
		super.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getGraphIndex()
	 */
	@Override
	public ElementIndex getGraphIndex() {
		return struct;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getNodeCount()
	 */
	@Override
	public int getNodeCount() {
		return struct.getNodeCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return struct.getEdgeCount();
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
	public ElementIndex getNodeIndex(String nodeId) {
		return struct.getNode(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getEdgeIndex(java.lang.String)
	 */
	@Override
	public ElementIndex getEdgeIndex(String edgeId) {
		return struct.getEdge(edgeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getSpriteIndex(java.lang.String)
	 */
	@Override
	public ElementIndex getSpriteIndex(String spriteId) {
		// TODO
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getNodeIndex(int)
	 */
	@Override
	public ElementIndex getNodeIndex(int nodeIndex) {
		return struct.getNode(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getEdgeIndex(int)
	 */
	@Override
	public ElementIndex getEdgeIndex(int edgeIndex) {
		return struct.getEdge(edgeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIIndexer#getSpriteIndex(int)
	 */
	@Override
	public ElementIndex getSpriteIndex(int spriteIndex) {
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

	protected void fireElementAdded(ElementIndex index, Object... args) {
		switch (index.getType()) {
		case NODE:
			for (IndexerListener l : listeners)
				l.nodeAdded(index);
			break;
		case EDGE:
			for (IndexerListener l : listeners)
				l.edgeAdded(index, (ElementIndex) args[0],
						(ElementIndex) args[1], (Boolean) args[2]);
			break;
		default:
			// TODO
			throw new UnsupportedOperationException();
		}
	}

	protected void fireElementsSwap(ElementIndex e1, ElementIndex e2) {
		assert e1.getType() == e2.getType();

		switch (e1.getType()) {
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

	protected void fireElementRemoved(ElementIndex index) {
		switch (index.getType()) {
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

	protected void fireElementCleared() {
		for (IndexerListener l : listeners)
			l.elementsClear();
	}

	private class Indexes extends AdjacencyListGraph implements ElementIndex {

		public Indexes() {
			super("graph");

			setNodeFactory(new NodeFactory<_NodeIndex>() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.graphstream.graph.NodeFactory#newInstance(java.lang.String
				 * , org.graphstream.graph.Graph)
				 */
				@Override
				public _NodeIndex newInstance(String id, Graph graph) {
					return new _NodeIndex((AbstractGraph) graph, id);
				}
			});

			setEdgeFactory(new EdgeFactory<DefaultIndexer._EdgeIndex>() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.graphstream.graph.EdgeFactory#newInstance(java.lang.String
				 * , org.graphstream.graph.Node, org.graphstream.graph.Node,
				 * boolean)
				 */
				@Override
				public DefaultIndexer._EdgeIndex newInstance(String id,
						Node src, Node dst, boolean directed) {
					return new DefaultIndexer._EdgeIndex(id, (_NodeIndex) src,
							(_NodeIndex) dst, directed);
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#id()
		 */
		@Override
		public String id() {
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#index()
		 */
		@Override
		public int index() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#getType()
		 */
		@Override
		public Type getType() {
			return Type.GRAPH;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AdjacencyListGraph#addNodeCallback
		 * (org.graphstream.graph.implementations.AbstractNode)
		 */
		@Override
		protected void addNodeCallback(AbstractNode node) {
			super.addNodeCallback(node);
			fireElementAdded((_NodeIndex) node);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AdjacencyListGraph#addEdgeCallback
		 * (org.graphstream.graph.implementations.AbstractEdge)
		 */
		@Override
		protected void addEdgeCallback(AbstractEdge edge) {
			super.addEdgeCallback(edge);
			
			fireElementAdded((_EdgeIndex) edge, edge.getSourceNode(),
					edge.getTargetNode(), edge.isDirected());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.graph.implementations.AdjacencyListGraph#
		 * removeEdgeCallback
		 * (org.graphstream.graph.implementations.AbstractEdge)
		 */
		@Override
		protected void removeEdgeCallback(AbstractEdge edge) {
			_EdgeIndex index = (_EdgeIndex) edge;
			_EdgeIndex last = (_EdgeIndex) edgeArray[edgeCount - 1];
			boolean swap = index != last;

			if (swap) {
				int i = edge.getIndex();
				edgeArray[i] = last;
				last.setIndex(i);
				edgeArray[edgeCount - 1] = index;
				index.setIndex(edgeCount - 1);

				fireElementsSwap(index, last);
			}

			fireElementRemoved(index);

			edgeMap.remove(edge.getId());
			edgeArray[--edgeCount] = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.graph.implementations.AdjacencyListGraph#
		 * removeNodeCallback
		 * (org.graphstream.graph.implementations.AbstractNode)
		 */
		@Override
		protected void removeNodeCallback(AbstractNode node) {
			_NodeIndex index = (_NodeIndex) node;
			_NodeIndex last = (_NodeIndex) nodeArray[nodeCount - 1];
			boolean swap = index != last;

			if (swap) {
				int i = node.getIndex();
				nodeArray[i] = last;
				last.setIndex(i);
				nodeArray[nodeCount - 1] = index;
				index.setIndex(nodeCount - 1);

				fireElementsSwap(index, last);
			}

			fireElementRemoved(index);

			nodeMap.remove(node.getId());
			nodeArray[--nodeCount] = null;
			index.setIndex(-1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AdjacencyListGraph#clearCallback
		 * ()
		 */
		@Override
		protected void clearCallback() {
			super.clearCallback();
			fireElementCleared();
		}
	}

	private class _NodeIndex extends AdjacencyListNode implements
			ElementIndex.NodeIndex {

		protected _NodeIndex(AbstractGraph graph, String id) {
			super(graph, id);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#id()
		 */
		@Override
		public String id() {
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#index()
		 */
		@Override
		public int index() {
			return getIndex();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#getType()
		 */
		@Override
		public Type getType() {
			return Type.NODE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AbstractElement#setIndex(int)
		 */
		@Override
		protected void setIndex(int index) {
			super.setIndex(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.ElementIndex.NodeIndex#getEdgeIndex(int)
		 */
		@Override
		public EdgeIndex getEdgeIndex(int i) {
			return (EdgeIndex) edges[i];
		}
	}

	private class _EdgeIndex extends AbstractEdge implements
			ElementIndex.EdgeIndex {

		protected _EdgeIndex(String id, _NodeIndex source, _NodeIndex target,
				boolean directed) {
			super(id, source, target, directed);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#id()
		 */
		@Override
		public String id() {
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#index()
		 */
		@Override
		public int index() {
			return getIndex();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#getType()
		 */
		@Override
		public Type getType() {
			return Type.EDGE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex.EdgeIndex#getSource()
		 */
		@Override
		public ElementIndex getSource() {
			return (ElementIndex) source;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex.EdgeIndex#getTarget()
		 */
		@Override
		public ElementIndex getTarget() {
			return (ElementIndex) target;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.graph.implementations.AbstractElement#setIndex(int)
		 */
		@Override
		protected void setIndex(int index) {
			super.setIndex(index);
		}
	}
}
