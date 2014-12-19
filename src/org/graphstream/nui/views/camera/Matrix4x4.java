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
package org.graphstream.nui.views.camera;

import java.util.Arrays;

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

	public Matrix4x4 set(int r, int c, double value) {
		assert (r < 4 && r >= 0 && c < 4 && c >= 0);
		data[r * 4 + c] = value;
		return this;
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
		double[] a = data;
		double[] b = right.data;

		r[0] = a[0] * b[0] + a[1] * b[4] + a[2] * b[8] + a[3] * b[12];
		r[1] = a[0] * b[1] + a[1] * b[5] + a[2] * b[9] + a[3] * b[13];
		r[2] = a[0] * b[2] + a[1] * b[6] + a[2] * b[10] + a[3] * b[14];
		r[3] = a[0] * b[3] + a[1] * b[7] + a[2] * b[11] + a[3] * b[15];

		r[4] = a[4] * b[0] + a[5] * b[4] + a[7] * b[8] + a[8] * b[12];
		r[5] = a[4] * b[1] + a[5] * b[5] + a[7] * b[9] + a[8] * b[13];
		r[6] = a[4] * b[2] + a[5] * b[6] + a[7] * b[10] + a[8] * b[14];
		r[7] = a[4] * b[3] + a[5] * b[7] + a[7] * b[11] + a[8] * b[15];

		r[8] = a[8] * b[0] + a[9] * b[4] + a[10] * b[8] + a[11] * b[12];
		r[9] = a[8] * b[1] + a[9] * b[5] + a[10] * b[9] + a[11] * b[13];
		r[10] = a[8] * b[2] + a[9] * b[6] + a[10] * b[10] + a[11] * b[14];
		r[11] = a[8] * b[3] + a[9] * b[7] + a[10] * b[11] + a[11] * b[15];

		r[12] = a[12] * b[0] + a[13] * b[4] + a[14] * b[8] + a[15] * b[12];
		r[13] = a[12] * b[1] + a[13] * b[5] + a[14] * b[9] + a[15] * b[13];
		r[14] = a[12] * b[2] + a[13] * b[6] + a[14] * b[10] + a[15] * b[14];
		r[15] = a[12] * b[3] + a[13] * b[7] + a[14] * b[11] + a[15] * b[15];

		return new Matrix4x4(r);
	}
}
