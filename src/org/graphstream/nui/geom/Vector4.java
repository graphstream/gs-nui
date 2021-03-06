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
package org.graphstream.nui.geom;

public class Vector4 extends AbstractVector<Vector4> {
	public Vector4() {
		super(4);
	}

	public Vector4(Vector3 v, double w) {
		super(v.x(), v.y(), v.z(), w);
	}

	public Vector4(double x, double y, double z, double w) {
		super(x, y, z, w);
	}

	public Vector4(double[] data) {
		super(data);
		assert data.length == 4;
	}

	// //////////////////////////
	// Access shortcuts

	public double x() {
		return data[0];
	}

	public double y() {
		return data[1];
	}

	public double z() {
		return data[2];
	}

	public double w() {
		return data[3];
	}

	public Vector4 mult(Matrix4x4 right) {
		double[] r = new double[4];
		double[] a = right.data;
		double[] b = data;

		r[0] = b[0] * a[0] + b[1] * a[1] + b[2] * a[2] + b[3] * a[3];
		r[1] = b[0] * a[4] + b[1] * a[5] + b[2] * a[6] + b[3] * a[7];
		r[2] = b[0] * a[8] + b[1] * a[9] + b[2] * a[10] + b[3] * a[11];
		r[3] = b[0] * a[12] + b[1] * a[13] + b[2] * a[14] + b[3] * a[15];

		return new Vector4(r);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.geom.AbstractVector#createNewInstance()
	 */
	@Override
	protected Vector4 createNewInstance() {
		return new Vector4();
	}
}
