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
package org.graphstream.nui.geom;

import java.util.Arrays;

import org.graphstream.nui.util.Tools;

public abstract class AbstractVector<T extends AbstractVector<T>> {
	protected final double[] data;

	protected AbstractVector(int size) {
		this(size, 0.0);
	}

	protected AbstractVector(int size, double fill) {
		data = new double[size];
		Arrays.fill(data, fill);
	}

	protected AbstractVector(T copy) {
		data = new double[copy.data.length];
		System.arraycopy(copy.data, 0, data, 0, data.length);
	}

	protected AbstractVector(double... data) {
		assert data != null;
		this.data = data;
	}

	public double get(int i) {
		assert i >= 0 && i < data.length;
		return data[i];
	}

	public double[] getRawData() {
		return data;
	}

	public void set(int i, double value) {
		assert i >= 0 && i < data.length;
		data[i] = value;

		dataChanged();
	}

	public void set(double... values) {
		assert values != null && values.length == data.length;

		for (int i = 0; i < data.length; i++)
			data[i] = values[i];

		dataChanged();
	}

	public boolean isZero() {
		for (int i = 0; i < data.length; i++)
			if (data[i] != 0)
				return false;

		return true;
	}

	public boolean validComponent(int i) {
		return (i >= 0 && i < data.length);
	}

	@SuppressWarnings("unchecked")
	public double length() {
		return Math.sqrt(dotProduct((T) this));
	}

	@SuppressWarnings("unchecked")
	public T fill(double s) {
		Arrays.fill(data, s);
		dataChanged();

		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T copy(T other) {
		System.arraycopy(other.data, 0, data, 0, data.length);
		dataChanged();

		return (T) this;
	}

	public double distance(T other) {
		double r = 0;

		for (int i = 0, l = data.length; i < l; i++)
			r += (other.data[i] - data[i]) * (other.data[i] - data[i]);

		return Math.abs(Math.sqrt(r));
	}

	public double dotProduct(T other) {
		double r = 0.0;

		for (int i = 0; i < data.length; i++)
			r += data[i] * other.data[i];

		return r;
	}

	public double dotProduct(double... other) {
		assert other != null && data.length == other.length;
		double r = 0.0;

		for (int i = 0; i < data.length; i++)
			r += data[i] * other[i];

		return r;
	}

	@SuppressWarnings("unchecked")
	public T selfAdd(T other) {
		for (int i = 0; i < data.length; i++)
			data[i] += other.data[i];

		dataChanged();

		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T selfSub(T other) {
		for (int i = 0; i < data.length; i++)
			data[i] -= other.data[i];

		dataChanged();

		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T selfMult(T other) {
		for (int i = 0; i < data.length; i++)
			data[i] *= other.data[i];

		dataChanged();

		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T selfDiv(T other) {
		for (int i = 0; i < data.length; i++)
			data[i] /= other.data[i];

		dataChanged();

		return (T) this;
	}

	public T add(T other) {
		T clone = _clone();
		return clone.selfAdd(other);
	}

	public T sub(T other) {
		T clone = _clone();
		return clone.selfSub(other);
	}

	public T mult(T other) {
		T clone = _clone();
		return clone.selfMult(other);
	}

	public T div(T other) {
		T clone = _clone();
		return clone.selfDiv(other);
	}

	@SuppressWarnings("unchecked")
	public T selfScalarAdd(double s) {
		for (int i = 0; i < data.length; i++)
			data[i] += s;

		dataChanged();

		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T selfScalarSub(double s) {
		for (int i = 0; i < data.length; i++)
			data[i] -= s;

		dataChanged();

		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T selfScalarMult(double s) {
		for (int i = 0; i < data.length; i++)
			data[i] *= s;

		dataChanged();

		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T selfScalarDiv(double s) {
		for (int i = 0; i < data.length; i++)
			data[i] /= s;

		dataChanged();

		return (T) this;
	}

	public T scalarAdd(double s) {
		T clone = _clone();
		return clone.selfScalarAdd(s);
	}

	public T scalarSub(double s) {
		T clone = _clone();
		return clone.selfScalarSub(s);
	}

	public T scalarMult(double s) {
		T clone = _clone();
		return clone.selfScalarMult(s);
	}

	public T scalarDiv(double s) {
		assert s != 0;
		T clone = _clone();
		return clone.selfScalarDiv(s);
	}

	public double normalize() {
		double l = length();

		if (l != 0)
			selfScalarDiv(l);

		return l;
	}

	public boolean fuzzyEquals(T other, double epsilon) {
		for (int i = 0; i < data.length; i++)
			if (!Tools.fuzzyEquals(data[i], other.data[i], epsilon))
				return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (getClass().isAssignableFrom(obj.getClass())) {
			AbstractVector<?> other = (AbstractVector<?>) obj;
			return Arrays.equals(data, other.data);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		return _clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return data != null ? Arrays.hashCode(data) : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("[");

		if (data.length > 0) {
			buffer.append(data[0]);

			for (int i = 1; i < data.length; i++)
				buffer.append('|').append(data[i]);
		}

		return buffer.append("]").toString();
	}

	private T _clone() {
		T clone = createNewInstance();
		System.arraycopy(data, 0, clone.data, 0, data.length);

		return clone;
	}

	protected void dataChanged() {

	}

	protected abstract T createNewInstance();
}
