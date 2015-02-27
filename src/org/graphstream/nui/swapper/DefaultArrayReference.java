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
import java.util.Arrays;

import org.graphstream.nui.UISwapper.ValueFactory;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.Type;

public class DefaultArrayReference<T> implements UIArrayReference<T>, Swappable {
	protected final Type type;
	protected int growingSize;
	protected int initialSize;
	protected final int componentCount;
	protected T[] data;
	protected ValueFactory<T> valueFactory;
	protected int size;
	protected final SwappableHandler handler;

	@SuppressWarnings("unchecked")
	public DefaultArrayReference(Type type, int initialSize, int growingSize,
			int componentCount, Class<T> componentType,
			ValueFactory<T> valueFactory, SwappableHandler handler) {
		this.type = type;
		this.initialSize = initialSize;
		this.growingSize = growingSize;
		this.componentCount = componentCount;
		this.handler = handler;

		initialSize = Math.max(initialSize, getElementCount() + growingSize);

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
	 * @see org.graphstream.nui.swapper.UIArrayReference#release()
	 */
	@Override
	public void release() {
		handler.release(this);
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

				for (int j = 0; j < componentCount; j++)
					set(index, j, valueFactory.createValue(index, j));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.swapper.Swappable#swap(int, int)
	 */
	@Override
	public void swap(int index1, int index2) {
		T tmp;

		index1 *= componentCount;
		index2 *= componentCount;

		for (int i = 0; i < componentCount; i++) {
			tmp = data[index1 + i];

			data[index1 + i] = data[index2 + i];
			data[index2 + i] = tmp;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.swapper.UIArrayReference#get(org.graphstream.
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
	 * @see org.graphstream.nui.swapper.UIArrayReference#set(org.graphstream.
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
