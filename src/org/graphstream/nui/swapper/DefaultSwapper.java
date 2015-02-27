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
package org.graphstream.nui.swapper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UISwapper;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.Type;
import org.graphstream.nui.indexer.IndexerListener;
import org.graphstream.nui.swapper.DefaultBufferReference.DefaultDoubleBufferReference;
import org.graphstream.nui.swapper.DefaultBufferReference.DefaultIntBufferReference;
import org.graphstream.nui.swapper.DefaultBufferReference.DefaultFloatBufferReference;
import org.graphstream.nui.swapper.DefaultBufferReference.DefaultLongBufferReference;

public class DefaultSwapper extends AbstractModule implements UISwapper {
	protected final Map<Type, List<Swappable>> buffers;
	protected UIIndexer indexer;
	protected ByteBuffer swapBuffer;
	protected ChangeListener listener;
	protected SwappableHandler handler;
	protected int nodeInitialSize;
	protected int nodeGrowingSize;
	protected int edgeInitialSize;
	protected int edgeGrowingSize;

	public DefaultSwapper() {
		super(MODULE_ID, UIIndexer.MODULE_ID);

		buffers = new EnumMap<Type, List<Swappable>>(Type.class);
		buffers.put(Type.NODE, new LinkedList<Swappable>());
		buffers.put(Type.EDGE, new LinkedList<Swappable>());

		swapBuffer = ByteBuffer.allocateDirect(1024);
		listener = new ChangeListener();
		handler = new InternalSwappableHandler();

		nodeInitialSize = 1000;
		edgeInitialSize = 2000;
		nodeGrowingSize = 1000;
		edgeGrowingSize = 2000;
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

		indexer.addIndexerListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		//
		// Release all the buffers
		//
		for (List<Swappable> l : buffers.values()) {
			while (l.size() > 0) {
				Swappable sw = l.get(0);
				l.remove(0);
				sw.release();
			}

			l.clear();
		}

		buffers.clear();

		indexer.removeIndexerListener(listener);
		indexer = null;

		super.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UISwapper#createBuffer(org.graphstream.nui.indexer
	 * .ElementIndex.Type, int, org.graphstream.nui.UISwapper.BufferType,
	 *  java.nio.ByteOrder,
	 * org.graphstream.nui.UISwapper.CreationTrigger)
	 */
	@Override
	public UIBufferReference createBuffer(ElementIndex.Type type,
			int components, BufferType bufferType, ByteOrder order,
			CreationTrigger onNewElement) {
		int initialSize, growingSize;

		switch (type) {
		case EDGE:
			initialSize = edgeInitialSize;
			growingSize = edgeGrowingSize;
			break;
		case NODE:
		default:
			initialSize = nodeInitialSize;
			growingSize = nodeGrowingSize;
		}

		DefaultBufferReference ref = null;

		switch (bufferType) {
		case DOUBLE:
			ref = new DefaultDoubleBufferReference(handler, type, components,
					initialSize, growingSize, onNewElement);
			break;
		case FLOAT:
			ref = new DefaultFloatBufferReference(handler, type, components,
					initialSize, growingSize, onNewElement);
			break;
		case INT:
			ref = new DefaultIntBufferReference(handler, type, components,
					initialSize, growingSize, onNewElement);
			break;
		case LONG:
			ref = new DefaultLongBufferReference(handler, type, components,
					initialSize, growingSize, onNewElement);
			break;
		}

		assert ref != null;

		if (!buffers.containsKey(type))
			buffers.put(type, new LinkedList<Swappable>());

		buffers.get(type).add(ref);

		return ref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UISwapper#createArray(org.graphstream.nui.indexer
	 * .ElementIndex.Type, int, int, int, java.lang.Class)
	 */
	@Override
	public <T> UIArrayReference<T> createArray(ElementIndex.Type type,
			int components, Class<T> valueType, ValueFactory<T> valueFactory) {
		int initialSize, growingSize;

		switch (type) {
		case EDGE:
			initialSize = edgeInitialSize;
			growingSize = edgeGrowingSize;
			break;
		case NODE:
		default:
			initialSize = nodeInitialSize;
			growingSize = nodeGrowingSize;
		}

		DefaultArrayReference<T> ref = new DefaultArrayReference<T>(type,
				initialSize, growingSize, components, valueType, valueFactory,
				handler);

		if (!buffers.containsKey(type))
			buffers.put(type, new LinkedList<Swappable>());

		buffers.get(type).add(ref);

		return ref;
	}

	protected void checkSize(Type type) {
		List<Swappable> l = buffers.get(type);

		if (l != null) {
			//
			// First, we have to check the size of all the reference.
			//
			for (Swappable ref : l)
				ref.checkSize();

			//
			// And now we can init the default values.
			//
			for (Swappable ref : l)
				ref.initDefaultValues();
		}
	}

	protected void swap(Type type, int idx1, int idx2) {
		List<Swappable> l = buffers.get(type);

		if (l != null) {
			for (Swappable ref : l)
				ref.swap(idx1, idx2);
		}
	}

	class InternalSwappableHandler implements SwappableHandler {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.SwappableHandler#indexer()
		 */
		@Override
		public UIIndexer indexer() {
			return indexer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.SwappableHandler#release(org.graphstream
		 * .nui.swapper.Swappable)
		 */
		@Override
		public void release(Swappable swappable) {
			buffers.get(swappable.getType()).remove(this);
		}
	}

	class ChangeListener implements IndexerListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#nodeAdded(org.graphstream
		 * .nui.indexer.ElementIndex)
		 */
		@Override
		public void nodeAdded(ElementIndex nodeIndex) {
			checkSize(Type.NODE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#nodeRemoved(org.graphstream
		 * .nui.indexer.ElementIndex)
		 */
		@Override
		public void nodeRemoved(ElementIndex nodeIndex) {
			checkSize(Type.NODE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#nodesSwapped(org.graphstream
		 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
		 */
		@Override
		public void nodesSwapped(ElementIndex nodeIndex1,
				ElementIndex nodeIndex2) {
			swap(Type.NODE, nodeIndex1.index(), nodeIndex2.index());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#edgeAdded(org.graphstream
		 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex,
		 * org.graphstream.nui.indexer.ElementIndex, boolean)
		 */
		@Override
		public void edgeAdded(ElementIndex edgeIndex, ElementIndex sourceIndex,
				ElementIndex targetIndex, boolean directed) {
			checkSize(Type.EDGE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#edgeRemoved(org.graphstream
		 * .nui.indexer.ElementIndex)
		 */
		@Override
		public void edgeRemoved(ElementIndex edgeIndex) {
			checkSize(Type.EDGE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.indexer.IndexerListener#edgesSwapped(org.graphstream
		 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
		 */
		@Override
		public void edgesSwapped(ElementIndex edgeIndex1,
				ElementIndex edgeIndex2) {
			swap(Type.EDGE, edgeIndex1.index(), edgeIndex2.index());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.IndexerListener#elementsClear()
		 */
		@Override
		public void elementsClear() {
			checkSize(Type.NODE);
			checkSize(Type.EDGE);
		}
	}
}
