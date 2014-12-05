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

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
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

public class DefaultSwapper extends AbstractModule implements UISwapper {
	protected final Map<Type, List<Swappable>> buffers;
	protected UIIndexer indexer;
	protected ByteBuffer swapBuffer;
	protected ChangeListener listener;
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
	 * org.graphstream.nui.UIBuffers#createBuffer(org.graphstream.nui.indexer
	 * .ElementIndex.Type, int, int, int, int, boolean)
	 */
	@Override
	public UIBufferReference createBuffer(Type type, int components,
			int componentSize, boolean direct, ByteOrder order) {
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

		DefaultBufferReference ref = new DefaultBufferReference(type,
				initialSize, growingSize, components, componentSize, direct,
				order);

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
				initialSize, growingSize, components, valueType, valueFactory);

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

	abstract class BaseReference implements Swappable {
		protected final Type type;
		protected int growingSize;
		protected int initialSize;
		protected final int componentCount;

		BaseReference(final Type type, int initialSize, int growingSize,
				int componentCount) {
			this.type = type;
			this.initialSize = initialSize;
			this.growingSize = growingSize;
			this.componentCount = componentCount;
		}

		@Override
		public void release() {
			buffers.get(type).remove(this);
		}

		protected int getElementCount() {
			int elementCount;

			switch (type) {
			case NODE:
				elementCount = indexer.getNodeCount();
				break;
			case EDGE:
				elementCount = indexer.getEdgeCount();
				break;
			case SPRITE:
				elementCount = indexer.getSpriteCount();
				break;
			default:
				//
				// What's the hell ?!?
				//
				elementCount = 1;
				break;
			}

			return elementCount;
		}
	}

	//
	// Internal implementation of buffer reference.
	//
	class DefaultBufferReference extends BaseReference implements
			UIBufferReference, Swappable {
		ByteBuffer theBuffer;
		int capacity;
		final boolean direct;
		final int componentSize;
		final ByteOrder order;
		final int cc;

		DefaultBufferReference(final Type type, final int initialSize,
				final int growingSize, final int componentCount,
				final int componentSize, final boolean direct,
				final ByteOrder order) {
			super(type, initialSize, growingSize, componentCount);

			this.componentSize = componentSize;
			this.direct = direct;
			this.order = order == null ? ByteOrder.nativeOrder() : order;
			this.cc = componentCount * componentSize;

			init();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.buffers.UIBufferReference#buffer()
		 */
		@Override
		public ByteBuffer buffer() {
			theBuffer.rewind();
			theBuffer.limit(getElementCount() * cc);

			return theBuffer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.buffers.UIBufferReference#release()
		 */
		@Override
		public void release() {
			super.release();
			theBuffer = null;
		}

		void init() {
			capacity = Math.max(initialSize, getElementCount() + growingSize);
			int s = capacity * cc;

			//
			// Check size of the swap buffer
			//
			if (cc > swapBuffer.capacity())
				swapBuffer = ByteBuffer.allocateDirect(componentCount
						* componentSize);

			if (direct)
				theBuffer = ByteBuffer.allocateDirect(s);
			else
				theBuffer = ByteBuffer.allocate(s);

			theBuffer.order(order);
			theBuffer.limit(getElementCount() * cc);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#checkSize()
		 */
		@Override
		public void checkSize() {
			int elementCount = getElementCount();

			if (elementCount >= capacity) {
				capacity += growingSize;
				int s = capacity * cc;

				assert s > theBuffer.capacity();

				ByteBuffer tmp;

				if (direct)
					tmp = ByteBuffer.allocateDirect(s);
				else
					tmp = ByteBuffer.allocate(s);

				theBuffer.rewind();
				tmp.order(order);
				tmp.rewind();
				tmp.put(theBuffer);

				theBuffer = tmp;
			} else if (elementCount < capacity / 2
					&& elementCount + growingSize > initialSize) {
				capacity = elementCount + growingSize;

				int s = elementCount * cc;
				ByteBuffer tmp;

				if (direct)
					tmp = ByteBuffer.allocateDirect(s);
				else
					tmp = ByteBuffer.allocate(s);

				theBuffer.rewind();
				theBuffer.limit(s);
				tmp.order(order);
				tmp.rewind();
				tmp.put(theBuffer);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#initDefaultValues()
		 */
		@Override
		public void initDefaultValues() {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#swap(int, int)
		 */
		@Override
		public void swap(int idx1, int idx2) {
			idx1 = idx1 * cc;
			idx2 = idx2 * cc;

			if (swapBuffer.order() != order)
				swapBuffer.order(order);

			swapBuffer.rewind();
			swapBuffer.limit(2 * cc);
			theBuffer.limit(idx1 + cc);
			theBuffer.position(idx1);
			swapBuffer.put(theBuffer);
			theBuffer.limit(idx2 + cc);
			theBuffer.position(idx2);
			swapBuffer.put(theBuffer);

			swapBuffer.rewind();
			swapBuffer.limit(cc);
			theBuffer.position(idx2);
			theBuffer.put(swapBuffer);
			swapBuffer.limit(2 * cc);
			theBuffer.position(idx1);
			theBuffer.put(swapBuffer);
		}

		int bindex(ElementIndex index, int component) {
			assert component < componentSize;
			return index.index() * cc + component * componentSize;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#getByte(org.graphstream
		 * .nui.indexer.ElementIndex, int)
		 */
		@Override
		public byte getByte(ElementIndex index, int component) {
			return theBuffer.get(bindex(index, component));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#getFloat(org.graphstream
		 * .nui.indexer.ElementIndex, int)
		 */
		@Override
		public float getFloat(ElementIndex index, int component) {
			return theBuffer.getFloat(bindex(index, component));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#getDouble(org.graphstream
		 * .nui.indexer.ElementIndex, int)
		 */
		@Override
		public double getDouble(ElementIndex index, int component) {
			return theBuffer.getDouble(bindex(index, component));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#getShort(org.graphstream
		 * .nui.indexer.ElementIndex, int)
		 */
		@Override
		public short getShort(ElementIndex index, int component) {
			return theBuffer.getShort(bindex(index, component));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#getInt(org.graphstream
		 * .nui.indexer.ElementIndex, int)
		 */
		@Override
		public int getInt(ElementIndex index, int component) {
			return theBuffer.getInt(bindex(index, component));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#getLong(org.graphstream
		 * .nui.indexer.ElementIndex, int)
		 */
		@Override
		public long getLong(ElementIndex index, int component) {
			return theBuffer.getLong(bindex(index, component));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#setByte(org.graphstream
		 * .nui.indexer.ElementIndex, int, byte)
		 */
		@Override
		public void setByte(ElementIndex index, int component, byte b) {
			theBuffer.put(bindex(index, component), b);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.buffers.UIBufferReference#setFloat(org.graphstream
		 * .nui.indexer.ElementIndex, int, float)
		 */
		@Override
		public void setFloat(ElementIndex index, int component, float f) {
			theBuffer.putFloat(bindex(index, component), f);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference#setDouble(org.graphstream
		 * .nui.indexer.ElementIndex, int, double)
		 */
		@Override
		public void setDouble(ElementIndex index, int component, double d) {
			theBuffer.putDouble(bindex(index, component), d);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference#setShort(org.graphstream
		 * .nui.indexer.ElementIndex, int, short)
		 */
		@Override
		public void setShort(ElementIndex index, int component, short s) {
			theBuffer.putShort(bindex(index, component), s);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference#setInt(org.graphstream
		 * .nui.indexer.ElementIndex, int, int)
		 */
		@Override
		public void setInt(ElementIndex index, int component, int i) {
			theBuffer.putInt(bindex(index, component), i);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference#setLong(org.graphstream
		 * .nui.indexer.ElementIndex, int, long)
		 */
		@Override
		public void setLong(ElementIndex index, int component, long l) {
			theBuffer.putLong(bindex(index, component), l);
		}
	}

	class DefaultArrayReference<T> extends BaseReference implements
			UIArrayReference<T>, Swappable {
		T[] data;
		ValueFactory<T> valueFactory;
		int size;

		@SuppressWarnings("unchecked")
		DefaultArrayReference(Type type, int initialSize, int growingSize,
				int componentCount, Class<T> componentType,
				ValueFactory<T> valueFactory) {
			super(type, initialSize, growingSize, componentCount);

			initialSize = Math
					.max(initialSize, getElementCount() + growingSize);

			this.valueFactory = valueFactory;
			this.size = 0;
			this.data = (T[]) Array.newInstance(componentType, componentCount
					* initialSize);

			checkSize();
			initDefaultValues();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#checkSize()
		 */
		@Override
		public void checkSize() {
			int elementCount = getElementCount();

			if (elementCount >= data.length)
				data = Arrays.copyOf(data, data.length + growingSize);
			else if (elementCount < data.length / 2
					&& elementCount + growingSize > initialSize)
				data = Arrays.copyOf(data, elementCount + growingSize);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#initDefaultValues()
		 */
		@Override
		public void initDefaultValues() {
			int elementCount = getElementCount();

			if (valueFactory != null)
				for (int i = size; i < elementCount; i++) {
					ElementIndex index;

					switch (type) {
					case NODE:
						index = indexer.getNodeIndex(i);
						break;
					case EDGE:
						index = indexer.getEdgeIndex(i);
						break;
					case SPRITE:
						index = indexer.getSpriteIndex(i);
						break;
					default:
						index = indexer.getGraphIndex();
						break;
					}

					for (int j = 0; j < componentCount; j++)
						set(index, j, valueFactory.createValue(index, j));
				}

			size = elementCount;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#swap(int, int)
		 */
		@Override
		public void swap(int index1, int index2) {
			for (int i = 0; i < componentCount; i++) {
				T tmp = data[index1 * componentCount + i];

				data[index1 * componentCount + i] = data[index2
						* componentCount + i];
				data[index2 * componentCount + i] = tmp;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIArrayReference#get(org.graphstream.
		 * nui.indexer.ElementIndex, int)
		 */
		@Override
		public T get(ElementIndex index, int component) {
			assert component >= 0 && component < componentCount;
			return data[index.index() * componentCount + component];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIArrayReference#set(org.graphstream.
		 * nui.indexer.ElementIndex, int, java.lang.Object)
		 */
		@Override
		public void set(ElementIndex index, int component, T value) {
			assert component >= 0 && component < componentCount;
			data[index.index() * componentCount + component] = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.UIArrayReference#array()
		 */
		@Override
		public T[] array() {
			return data;
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
