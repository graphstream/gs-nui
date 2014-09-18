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
package org.graphstream.nui;

import java.nio.ByteOrder;

import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.swapper.UIArrayReference;
import org.graphstream.nui.swapper.UIBufferReference;

/**
 * One of the main parts of the ui is to maintain data in buffers that fit the
 * element indexes. Since there is dynamics, index of an element can change or
 * new elements can be added. So, one has to adapt the buffer to these changes.
 * 
 * The buffers module deals with these changes and adapt registered buffers, so
 * element datas always fit the index of elements.
 * 
 * When created, the new buffer can be accessed through a
 * {@link org.graphstream.nui.swapper.UIBufferReference}. Using the reference
 * rather than the buffer is an important point since the buffer itself can
 * change when we have to make it bigger.
 */
public interface UISwapper extends UIModule {
	public static final String MODULE_ID = "swapper";

	/**
	 * Create and registered a new buffer.
	 * 
	 * @param type
	 *            the type of element owning the data
	 * @param initialSize
	 *            initial size (ie. number of data elements) of the new buffer
	 * @param growingSize
	 *            when buffer has to grow, this defines how the capacity of the
	 *            buffer will be increased
	 * @param components
	 *            how many components per data element
	 * @param componentSize
	 *            defines the size of a components (in bytes)
	 * @param direct
	 *            set if the buffer should be direct or not
	 * @param order
	 *            byte order of the buffer
	 * @return a reference to the new registered buffer
	 */
	UIBufferReference createBuffer(ElementIndex.Type type, int components,
			int componentSize, boolean direct, ByteOrder order);

	<T> UIArrayReference<T> createArray(ElementIndex.Type type, int components,
			Class<T> valueType, ValueFactory<T> valueFactory);

	public static interface ValueFactory<T> {
		T createValue(ElementIndex index, int component);
	}
}
