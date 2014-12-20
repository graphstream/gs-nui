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

/**
 * A column-major matrix with 4x4 dimensions.
 */
public class Matrix4x4 {
	protected double[] data;

	public Matrix4x4() {
		this(0.0);
	}

	public Matrix4x4(double diagonal) {
		data = new double[16];
		diagonal(0.0);
	}

	public Matrix4x4(Matrix4x4 clone) {
		data = Arrays.copyOf(clone.data, 16);
	}

	public Matrix4x4(double[] data) {
		assert (data.length == 16);
		this.data = data;
	}

	public Matrix4x4(Vector4 column0, Vector4 column1, Vector4 column2,
			Vector4 column3) {
		this();

		System.arraycopy(column0.data, 0, data, 0, 4);
		System.arraycopy(column1.data, 0, data, 4, 4);
		System.arraycopy(column2.data, 0, data, 8, 4);
		System.arraycopy(column3.data, 0, data, 12, 4);
	}

	public Matrix4x4 set(int column, int row, double value) {
		data[column * 4 + row] = value;
		return this;
	}

	public double get(int column, int row) {
		return data[column * 4 + row];
	}

	public Vector4 getRow(int r) {
		return new Vector4(get(0, r), get(1, r), get(2, r), get(3, r));
	}

	public Vector4 getColumn(int c) {
		return new Vector4(get(c, 0), get(c, 1), get(c, 2), get(c, 3));
	}

	public double[] getRawData() {
		return data;
	}

	public Matrix4x4 loadIdentity() {
		diagonal(1.0);
		return this;
	}

	public void diagonal(double value) {
		Arrays.fill(data, 0);
		data[0] = data[5] = data[10] = data[15] = value;
	}

	public Matrix4x4 mult(Matrix4x4 right) {
		double[] r = new double[16];
		double[] b = right.data;
		double[] a = data;

		r[0] = b[0] * a[0] + b[1] * a[4] + b[2] * a[8] + b[3] * a[12];
		r[1] = b[0] * a[1] + b[1] * a[5] + b[2] * a[9] + b[3] * a[13];
		r[2] = b[0] * a[2] + b[1] * a[6] + b[2] * a[10] + b[3] * a[14];
		r[3] = b[0] * a[3] + b[1] * a[7] + b[2] * a[11] + b[3] * a[15];

		r[4] = b[4] * a[0] + b[5] * a[4] + b[7] * a[8] + b[8] * a[12];
		r[5] = b[4] * a[1] + b[5] * a[5] + b[7] * a[9] + b[8] * a[13];
		r[6] = b[4] * a[2] + b[5] * a[6] + b[7] * a[10] + b[8] * a[14];
		r[7] = b[4] * a[3] + b[5] * a[7] + b[7] * a[11] + b[8] * a[15];

		r[8] = b[8] * a[0] + b[9] * a[4] + b[10] * a[8] + b[11] * a[12];
		r[9] = b[8] * a[1] + b[9] * a[5] + b[10] * a[9] + b[11] * a[13];
		r[10] = b[8] * a[2] + b[9] * a[6] + b[10] * a[10] + b[11] * a[14];
		r[11] = b[8] * a[3] + b[9] * a[7] + b[10] * a[11] + b[11] * a[15];

		r[12] = b[12] * a[0] + b[13] * a[4] + b[14] * a[8] + b[15] * a[12];
		r[13] = b[12] * a[1] + b[13] * a[5] + b[14] * a[9] + b[15] * a[13];
		r[14] = b[12] * a[2] + b[13] * a[6] + b[14] * a[10] + b[15] * a[14];
		r[15] = b[12] * a[3] + b[13] * a[7] + b[14] * a[11] + b[15] * a[15];

		return new Matrix4x4(r);
	}

	public Vector4 mult(Vector4 right) {
		Vector4 r = new Vector4();
		// TODO
		return r;
	}

	public void scalarAdd(double scalar) {
		for (int i = 0; i < 16; i++)
			data[i] += scalar;
	}

	public void scalarSub(double scalar) {
		for (int i = 0; i < 16; i++)
			data[i] -= scalar;
	}

	public void scalarMult(double scalar) {
		for (int i = 0; i < 16; i++)
			data[i] *= scalar;
	}

	public void scalarDiv(double scalar) {
		for (int i = 0; i < 16; i++)
			data[i] /= scalar;
	}

	public Matrix4x4 inverse() {
		double Coef00 = get(2, 2) * get(3, 3) - get(3, 2) * get(2, 3);
		double Coef02 = get(1, 2) * get(3, 3) - get(3, 2) * get(1, 3);
		double Coef03 = get(1, 2) * get(2, 3) - get(2, 2) * get(1, 3);

		double Coef04 = get(2, 1) * get(3, 3) - get(3, 1) * get(2, 3);
		double Coef06 = get(1, 1) * get(3, 3) - get(3, 1) * get(1, 3);
		double Coef07 = get(1, 1) * get(2, 3) - get(2, 1) * get(1, 3);

		double Coef08 = get(2, 1) * get(3, 2) - get(3, 1) * get(2, 2);
		double Coef10 = get(1, 1) * get(3, 2) - get(3, 1) * get(1, 2);
		double Coef11 = get(1, 1) * get(2, 2) - get(2, 1) * get(1, 2);

		double Coef12 = get(2, 0) * get(3, 3) - get(3, 0) * get(2, 3);
		double Coef14 = get(1, 0) * get(3, 3) - get(3, 0) * get(1, 3);
		double Coef15 = get(1, 0) * get(2, 3) - get(2, 0) * get(1, 3);

		double Coef16 = get(2, 0) * get(3, 2) - get(3, 0) * get(2, 2);
		double Coef18 = get(1, 0) * get(3, 2) - get(3, 0) * get(1, 2);
		double Coef19 = get(1, 0) * get(2, 2) - get(2, 0) * get(1, 2);

		double Coef20 = get(2, 0) * get(3, 1) - get(3, 0) * get(2, 1);
		double Coef22 = get(1, 0) * get(3, 1) - get(3, 0) * get(1, 1);
		double Coef23 = get(1, 0) * get(2, 1) - get(2, 0) * get(1, 1);

		Vector4 Fac0 = new Vector4(Coef00, Coef00, Coef02, Coef03);
		Vector4 Fac1 = new Vector4(Coef04, Coef04, Coef06, Coef07);
		Vector4 Fac2 = new Vector4(Coef08, Coef08, Coef10, Coef11);
		Vector4 Fac3 = new Vector4(Coef12, Coef12, Coef14, Coef15);
		Vector4 Fac4 = new Vector4(Coef16, Coef16, Coef18, Coef19);
		Vector4 Fac5 = new Vector4(Coef20, Coef20, Coef22, Coef23);

		Vector4 Vec0 = new Vector4(get(1, 0), get(0, 0), get(0, 0), get(0, 0));
		Vector4 Vec1 = new Vector4(get(1, 1), get(0, 1), get(0, 1), get(0, 1));
		Vector4 Vec2 = new Vector4(get(1, 2), get(0, 2), get(0, 2), get(0, 2));
		Vector4 Vec3 = new Vector4(get(1, 3), get(0, 3), get(0, 3), get(0, 3));

		Vector4 Inv0 = Vec1.mult(Fac0).sub(Vec2.mult(Fac1))
				.add(Vec3.mult(Fac2));
		Vector4 Inv1 = Vec0.mult(Fac0).sub(Vec2.mult(Fac3))
				.add(Vec3.mult(Fac4));
		Vector4 Inv2 = Vec0.mult(Fac1).sub(Vec1.mult(Fac3))
				.add(Vec3.mult(Fac5));
		Vector4 Inv3 = Vec0.mult(Fac2).sub(Vec1.mult(Fac4))
				.add(Vec2.mult(Fac5));

		Vector4 SignA = new Vector4(+1, -1, +1, -1);
		Vector4 SignB = new Vector4(-1, +1, -1, +1);

		Matrix4x4 Inverse = new Matrix4x4(Inv0.mult(SignA), Inv1.mult(SignB),
				Inv2.mult(SignA), Inv3.mult(SignB));

		Vector4 Row0 = Inverse.getRow(0);
		Vector4 Dot0 = getColumn(0).mult(Row0);
		double Dot1 = (Dot0.x() + Dot0.y()) + (Dot0.z() + Dot0.w());
		Inverse.scalarDiv(Dot1);

		return Inverse;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Matrix4x4) {
			Matrix4x4 mat = (Matrix4x4) obj;
			return Arrays.equals(data, mat.data);
		}

		return false;
	}

	public boolean equals(Matrix4x4 mat, double epsilon) {
		for (int i = 0; i < 16; i++)
			if (!Tools.fuzzyEquals(data[i], mat.data[i], epsilon))
				return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("[");
		for (int r = 0; r < 4; r++)
			b.append(String.format("%s[%e; %e; %e; %e]%s", r > 0 ? " " : "",
					get(r, 0), get(r, 1), get(r, 2), get(r, 3), r < 3 ? ", \n"
							: ""));

		return b.append("]").toString();
	}
}
