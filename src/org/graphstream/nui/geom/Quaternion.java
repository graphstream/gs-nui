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

import org.graphstream.nui.geom.Vector3;

public class Quaternion {
	protected double x, y, z, w;

	public Quaternion(double w, Vector3 v) {
		this(w, v.x(), v.y(), v.z());
	}

	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double z() {
		return z;
	}

	public double w() {
		return w;
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public double normalize() {
		double l = length();

		x /= l;
		y /= l;
		z /= l;
		w /= l;

		return l;
	}

	public Quaternion getConjugate() {
		return new Quaternion(w, -x, -y, -z);
	}

	public Quaternion multiplyBy(Quaternion q2) {
		double mx = w * q2.x + x * q2.w + y * q2.z - z * q2.y;
		double my = w * q2.y - x * q2.z + y * q2.w + z * q2.x;
		double mz = w * q2.z + x * q2.y - y * q2.x + z * q2.w;
		double mw = w * q2.w - x * q2.x - y * q2.y - z * q2.z;

		return new Quaternion(mw, mx, my, mz);
	}

	public static void rotate(Vector3 view, double angle, Vector3 up) {
		double sin = Math.sin(angle / 2);
		double cos = Math.cos(angle / 2);

		Quaternion v = new Quaternion(0, view);
		Quaternion q = new Quaternion(cos, up.x() * sin, up.y() * sin, up.z()
				* sin);

		Quaternion r = q.multiplyBy(v).multiplyBy(q.getConjugate());

		view.set(r.x, r.y, r.z);
	}
}
