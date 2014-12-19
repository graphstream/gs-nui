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

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector3;

public class Matrix4Tools {
	public static Matrix4x4 lookAt(Point3 eye, Point3 center, Vector3 up) {
		Vector3 f = new Vector3(center.x - eye.x, center.y - eye.y, center.z
				- eye.z);
		f.normalize();

		Vector3 s = new Vector3(f);
		s.crossProduct(up);
		s.normalize();

		Vector3 u = new Vector3(s);
		u.crossProduct(f);

		Matrix4x4 r = new Matrix4x4(1.0);

		r.set(0, 0, s.x());
		r.set(1, 0, s.y());
		r.set(2, 0, s.z());
		r.set(0, 1, u.x());
		r.set(1, 1, u.y());
		r.set(2, 1, u.z());
		r.set(0, 2, -f.x());
		r.set(1, 2, -f.y());
		r.set(2, 2, -f.z());
		r.set(3, 0, -s.dotProduct(eye.x, eye.y, eye.z));
		r.set(3, 1, -u.dotProduct(eye.x, eye.y, eye.z));
		r.set(3, 2, f.dotProduct(eye.x, eye.y, eye.z));

		return r;
	}

	public static Matrix4x4 ortho(double left, double right, double bottom,
			double top, double zNear, double zFar) {
		Matrix4x4 r = new Matrix4x4(1.0);

		r.set(0, 0, 2.0 / (right - left));
		r.set(1, 1, 2.0 / (top - bottom));
		r.set(2, 2, -2.0 / (zFar - zNear));
		r.set(3, 0, -(right + left) / (right - left));
		r.set(3, 1, -(top + bottom) / (top - bottom));
		r.set(3, 2, -(zFar + zNear) / (zFar - zNear));

		return r;
	}

	public static Matrix4x4 ortho(double left, double right, double bottom,
			double top) {
		Matrix4x4 r = new Matrix4x4(1.0);

		r.set(0, 0, 2.0 / (right - left));
		r.set(1, 1, 2.0 / (top - bottom));
		r.set(2, 2, -1.0);
		r.set(3, 0, -(right + left) / (right - left));
		r.set(3, 1, -(top + bottom) / (top - bottom));

		return r;
	}

	public static Matrix4x4 frustrum(double left, double right, double bottom,
			double top, double nearVal, double farVal) {
		Matrix4x4 r = new Matrix4x4();

		r.set(0, 0, (2.0 * nearVal) / (right - left));
		r.set(1, 1, (2.0 * nearVal) / (top - bottom));
		r.set(2, 0, (right + left) / (right - left));
		r.set(2, 1, (top + bottom) / (top - bottom));
		r.set(2, 2, -(farVal + nearVal) / (farVal - nearVal));
		r.set(2, 3, -1.0);
		r.set(3, 2, -(2.0 * farVal * nearVal) / (farVal - nearVal));

		return r;
	}

	public static Matrix4x4 perspective(double fovy, double aspect,
			double zNear, double zFar) {
		assert (zFar > zNear);

		double tanHalfFovy = Math.tan(fovy / 2.0);
		Matrix4x4 r = new Matrix4x4();

		r.set(0, 0, 1.0 / (aspect * tanHalfFovy));
		r.set(1, 1, 1.0 / (tanHalfFovy));
		r.set(2, 2, -(zFar + zNear) / (zFar - zNear));
		r.set(2, 3, -1.0);
		r.set(3, 2, -(2.0 * zFar * zNear) / (zFar - zNear));

		return r;
	}
}
