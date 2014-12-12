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

import org.graphstream.nui.indexer.ElementIndex;

public interface UIBufferReference {
	public static final int SIZE_INT = Integer.SIZE / 8;
	public static final int SIZE_SHORT = Short.SIZE / 8;
	public static final int SIZE_LONG = Long.SIZE / 8;
	public static final int SIZE_FLOAT = Float.SIZE / 8;
	public static final int SIZE_DOUBLE = Double.SIZE / 8;

	ByteBuffer buffer();

	void release();

	byte getByte(ElementIndex index, int component);

	void setByte(ElementIndex index, int component, byte b);

	float getFloat(ElementIndex index, int component);

	void setFloat(ElementIndex index, int component, float f);

	double getDouble(ElementIndex index, int component);

	void setDouble(ElementIndex index, int component, double d);

	short getShort(ElementIndex index, int component);

	void setShort(ElementIndex index, int component, short s);

	int getInt(ElementIndex index, int component);

	void setInt(ElementIndex index, int component, int i);

	long getLong(ElementIndex index, int component);

	void setLong(ElementIndex index, int component, long l);
}
