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
package org.graphstream.nui.swapper;

import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.graphstream.nui.UISwapper.CreationTrigger;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.Type;

public abstract class DefaultBufferReference implements UIBufferReference,
		Swappable {
	protected final SwappableHandler handler;
	protected Buffer backArrayBuffer;
	protected final int componentCount;
	protected final Type type;
	protected int growingSize;
	protected int initialSize;
	protected int capacity;
	protected int size;
	protected final CreationTrigger onNewElement;
	protected ByteOrder order;

	protected DefaultBufferReference(SwappableHandler handler, Type type,
			int componentCount, int initialSize, int growingSize,
			CreationTrigger onNewElement) {
		this.type = type;
		this.componentCount = componentCount;
		this.handler = handler;
		this.onNewElement = onNewElement;
		this.initialSize = initialSize;
		this.growingSize = growingSize;

		this.capacity = Math.max(getElementCount(), initialSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.swapper.UIBufferReference#buffer()
	 */
	@Override
	public Buffer buffer() {
		return backArrayBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.swapper.UIBufferReference#release()
	 */
	@Override
	public void release() {
		handler.release(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.swapper.UIBufferReference#getComponentsCount()
	 */
	@Override
	public int getComponentsCount() {
		return componentCount;
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
			capacity = Math.max(capacity + growingSize, elementCount);
			adaptDataToCapacity();
		} else if (elementCount < capacity / 2
				&& elementCount + growingSize > initialSize
				&& elementCount + growingSize < capacity) {
			capacity = elementCount + growingSize;
			adaptDataToCapacity();
		}

		backArrayBuffer.limit(elementCount * componentCount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.swapper.Swappable#initDefaultValues()
	 */
	@Override
	public void initDefaultValues() {
		int elementCount = getElementCount();

		if (onNewElement != null)
			for (int i = size; i < elementCount; i++) {
				ElementIndex index;

				switch (type) {
				case NODE:
					index = handler.indexer().getNodeIndex(i);
					break;
				case EDGE:
					index = handler.indexer().getEdgeIndex(i);
					break;
				case SPRITE:
					index = handler.indexer().getSpriteIndex(i);
					break;
				default:
					index = handler.indexer().getGraphIndex();
					break;
				}

				onNewElement.newBufferElement(this, index);
			}

		size = elementCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.swapper.Swappable#getType()
	 */
	@Override
	public Type getType() {
		return type;
	}

	protected int bindex(ElementIndex index, int component) {
		assert component < componentCount;
		return index.index() * componentCount + component;
	}

	protected int getElementCount() {
		int elementCount;

		switch (type) {
		case NODE:
			elementCount = handler.indexer().getNodeCount();
			break;
		case EDGE:
			elementCount = handler.indexer().getEdgeCount();
			break;
		case SPRITE:
			elementCount = handler.indexer().getSpriteCount();
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

	protected abstract void adaptDataToCapacity();

	public static class DefaultDoubleBufferReference extends
			DefaultBufferReference implements
			UIBufferReference.DoubleBufferReference {
		protected double[] data;

		public DefaultDoubleBufferReference(SwappableHandler handler,
				Type type, int componentCount, int initialSize,
				int growingSize, CreationTrigger onNewElement) {
			super(handler, type, componentCount, initialSize, growingSize,
					onNewElement);

			adaptDataToCapacity();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#swap(int, int)
		 */
		@Override
		public void swap(int index1, int index2) {
			double tmp;

			index1 *= componentCount;
			index2 *= componentCount;

			for (int c = 0; c < componentCount; c++) {
				tmp = data[index1 + c];
				data[index1 + c] = data[index2 + c];
				data[index2 + c] = tmp;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.DoubleBufferReference
		 * #getDouble(org.graphstream.nui.indexer.ElementIndex, int)
		 */
		@Override
		public double getDouble(ElementIndex index, int component) {
			return data[bindex(index, component)];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.DoubleBufferReference
		 * #setDouble(org.graphstream.nui.indexer.ElementIndex, int, double)
		 */
		@Override
		public void setDouble(ElementIndex index, int component, double d) {
			data[bindex(index, component)] = d;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.DoubleBufferReference
		 * #getTuple(org.graphstream.nui.indexer.ElementIndex, double[])
		 */
		@Override
		public void getTuple(ElementIndex index, double[] tuple) {
			assert tuple.length == componentCount;
			System.arraycopy(data, bindex(index, 0), tuple, 0, componentCount);
		}

		protected void adaptDataToCapacity() {
			int size = capacity * componentCount;

			if (data != null && data.length == size)
				return;

			double[] newData = new double[size];

			if (data != null)
				System.arraycopy(data, 0, newData, 0,
						Math.min(data.length, newData.length));

			data = newData;
			backArrayBuffer = DoubleBuffer.wrap(data);
		}
	}

	public static class DefaultIntBufferReference extends
			DefaultBufferReference implements
			UIBufferReference.IntBufferReference {
		protected int[] data;

		public DefaultIntBufferReference(SwappableHandler handler, Type type,
				int componentCount, int initialSize, int growingSize,
				CreationTrigger onNewElement) {
			super(handler, type, componentCount, initialSize, growingSize,
					onNewElement);

			adaptDataToCapacity();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#swap(int, int)
		 */
		@Override
		public void swap(int index1, int index2) {
			int tmp;

			index1 *= componentCount;
			index2 *= componentCount;

			for (int c = 0; c < componentCount; c++) {
				tmp = data[index1 + c];
				data[index1 + c] = data[index2 + c];
				data[index2 + c] = tmp;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.IntBufferReference#
		 * getInt(org.graphstream.nui.indexer.ElementIndex, int)
		 */
		@Override
		public int getInt(ElementIndex index, int component) {
			return data[bindex(index, component)];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.IntBufferReference#
		 * setInt(org.graphstream.nui.indexer.ElementIndex, int, int)
		 */
		@Override
		public void setInt(ElementIndex index, int component, int i) {
			data[bindex(index, component)] = i;
		}

		protected void adaptDataToCapacity() {
			int size = capacity * componentCount;

			if (data != null && data.length == size)
				return;

			int[] newData = new int[size];

			if (data != null)
				System.arraycopy(data, 0, newData, 0,
						Math.min(data.length, newData.length));

			data = newData;
			backArrayBuffer = IntBuffer.wrap(data);
		}
	}

	public static class DefaultFloatBufferReference extends
			DefaultBufferReference implements
			UIBufferReference.FloatBufferReference {
		protected float[] data;

		public DefaultFloatBufferReference(SwappableHandler handler, Type type,
				int componentCount, int initialSize, int growingSize,
				CreationTrigger onNewElement) {
			super(handler, type, componentCount, initialSize, growingSize,
					onNewElement);

			adaptDataToCapacity();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#swap(int, int)
		 */
		@Override
		public void swap(int index1, int index2) {
			float tmp;

			index1 *= componentCount;
			index2 *= componentCount;

			for (int c = 0; c < componentCount; c++) {
				tmp = data[index1 + c];
				data[index1 + c] = data[index2 + c];
				data[index2 + c] = tmp;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.FloatBufferReference
		 * #getFloat(org.graphstream.nui.indexer.ElementIndex, int)
		 */
		@Override
		public float getFloat(ElementIndex index, int component) {
			return data[bindex(index, component)];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.FloatBufferReference
		 * #setFloat(org.graphstream.nui.indexer.ElementIndex, int, float)
		 */
		@Override
		public void setFloat(ElementIndex index, int component, float f) {
			data[bindex(index, component)] = f;
		}

		protected void adaptDataToCapacity() {
			int size = capacity * componentCount;

			if (data != null && data.length == size)
				return;

			float[] newData = new float[size];

			if (data != null)
				System.arraycopy(data, 0, newData, 0,
						Math.min(data.length, newData.length));

			data = newData;
			backArrayBuffer = FloatBuffer.wrap(data);
		}
	}

	public static class DefaultLongBufferReference extends
			DefaultBufferReference implements
			UIBufferReference.LongBufferReference {
		protected long[] data;

		public DefaultLongBufferReference(SwappableHandler handler, Type type,
				int componentCount, int initialSize, int growingSize,
				CreationTrigger onNewElement) {
			super(handler, type, componentCount, initialSize, growingSize,
					onNewElement);

			adaptDataToCapacity();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.swapper.Swappable#swap(int, int)
		 */
		@Override
		public void swap(int index1, int index2) {
			long tmp;

			index1 *= componentCount;
			index2 *= componentCount;

			for (int c = 0; c < componentCount; c++) {
				tmp = data[index1 + c];
				data[index1 + c] = data[index2 + c];
				data[index2 + c] = tmp;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.IntBufferReference#
		 * getInt(org.graphstream.nui.indexer.ElementIndex, int)
		 */
		@Override
		public long getLong(ElementIndex index, int component) {
			return data[bindex(index, component)];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.nui.swapper.UIBufferReference.IntBufferReference#
		 * setInt(org.graphstream.nui.indexer.ElementIndex, int, int)
		 */
		@Override
		public void setLong(ElementIndex index, int component, long l) {
			data[bindex(index, component)] = l;
		}

		protected void adaptDataToCapacity() {
			int size = capacity * componentCount;

			if (data != null && data.length == size)
				return;

			long[] newData = new long[size];

			if (data != null)
				System.arraycopy(data, 0, newData, 0,
						Math.min(data.length, newData.length));

			data = newData;
			backArrayBuffer = LongBuffer.wrap(data);
		}
	}
}
