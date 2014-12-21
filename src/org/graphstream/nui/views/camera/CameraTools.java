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

import org.graphstream.nui.geom.Matrix4x4;
import org.graphstream.nui.geom.Vector4;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector3;

/**
 * We tried to make a happy bunny by using the code of the <a
 * href="http://glm.g-truc.net/">GLM library</a>.
 *
 */
public class CameraTools {
	public static Matrix4x4 translate(Matrix4x4 mat, Vector3 delta) {
		Matrix4x4 r = new Matrix4x4(mat);

		for (int i = 0; i < 4; i++)
			r.set(3, i, mat.get(0, i) * delta.x() + mat.get(1, i) * delta.y()
					+ mat.get(2, i) * delta.z() + mat.get(3, i));

		return r;
	}

	public static Matrix4x4 rotate(Matrix4x4 m, double angle, Vector3 v) {
		final double a = angle;
		final double c = Math.cos(a);
		final double s = Math.sin(a);

		Vector3 axis = new Vector3(v);
		axis.normalize();
		Vector3 temp = new Vector3(axis);
		temp.scalarMult(1.0 - c);

		Matrix4x4 Rotate = new Matrix4x4();
		Rotate.set(0, 0, c + temp.x() * axis.x());
		Rotate.set(0, 1, 0 + temp.x() * axis.y() + s * axis.z());
		Rotate.set(0, 2, 0 + temp.x() * axis.z() - s * axis.y());

		Rotate.set(1, 0, 0 + temp.y() * axis.x() - s * axis.z());
		Rotate.set(1, 1, c + temp.y() * axis.y());
		Rotate.set(1, 2, 0 + temp.y() * axis.z() + s * axis.x());

		Rotate.set(2, 0, 0 + temp.z() * axis.x() + s * axis.y());
		Rotate.set(2, 1, 0 + temp.z() * axis.y() - s * axis.x());
		Rotate.set(2, 2, c + temp.z() * axis.z());

		Vector4 col0 = m.getColumn(0);
		Vector4 col1 = m.getColumn(1);
		Vector4 col2 = m.getColumn(2);
		Vector4 col3 = m.getColumn(3);

		Vector4 rCol0 = col0.mult(Rotate.get(0, 0))
				.add(col1.mult(Rotate.get(0, 1)))
				.add(col2.mult(Rotate.get(0, 2)));
		Vector4 rCol1 = col0.mult(Rotate.get(1, 0))
				.add(col1.mult(Rotate.get(1, 1)))
				.add(col2.mult(Rotate.get(1, 2)));
		Vector4 rCol2 = col0.mult(Rotate.get(2, 0))
				.add(col1.mult(Rotate.get(2, 1)))
				.add(col2.mult(Rotate.get(2, 2)));

		return new Matrix4x4(rCol0, rCol1, rCol2, col3);
	}

	public static Matrix4x4 scale(Matrix4x4 m, Vector3 s) {
		Vector4 col0 = m.getColumn(0).mult(s.x());
		Vector4 col1 = m.getColumn(1).mult(s.y());
		Vector4 col2 = m.getColumn(2).mult(s.z());
		Vector4 col3 = m.getColumn(3);

		return new Matrix4x4(col0, col1, col2, col3);
	}

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

	public static Matrix4x4 frustum(double left, double right, double bottom,
			double top, double near, double far) {
		Matrix4x4 r = new Matrix4x4();

		r.set(0, 0, (2.0 * near) / (right - left));
		r.set(1, 1, (2.0 * near) / (top - bottom));
		r.set(2, 0, (right + left) / (right - left));
		r.set(2, 1, (top + bottom) / (top - bottom));
		r.set(2, 2, -(far + near) / (far - near));
		r.set(2, 3, -1.0);
		r.set(3, 2, -(2.0 * far * near) / (far - near));

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

	public static void project(Point3 source, Point3 target,
			Matrix4x4 modelView, Matrix4x4 projection, double[] viewport) {
		Vector4 tmp = new Vector4(source, 1.0);
		tmp = modelView.mult(tmp);
		tmp = projection.mult(tmp);

		tmp.scalarDiv(tmp.w());
		tmp.scalarMult(0.5);
		tmp.scalarAdd(0.5);

		target.x = tmp.x() * viewport[2] + viewport[0];
		target.y = tmp.y() * viewport[3] + viewport[1];
		target.z = tmp.z();
	}

	public static void unProject(Point3 source, Point3 target,
			Matrix4x4 invMVP, double[] viewport) {
		Vector4 tmp = new Vector4(source, 1.0);

		tmp.set(0, (tmp.x() - viewport[0]) / viewport[2]);
		tmp.set(1, (tmp.y() - viewport[1]) / viewport[3]);
		tmp.scalarMult(2.0);
		tmp.scalarSub(1.0);

		Vector4 obj = invMVP.mult(tmp);
		obj.scalarDiv(obj.w());

		target.x = obj.x();
		target.y = obj.y();
		target.z = obj.z();
	}
}
