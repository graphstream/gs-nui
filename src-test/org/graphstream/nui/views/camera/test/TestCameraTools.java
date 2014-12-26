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
package org.graphstream.nui.views.camera.test;

import org.graphstream.nui.geom.Matrix4x4;
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.geom.test.Sample;
import org.graphstream.nui.geom.test.UseSamples;
import org.graphstream.nui.views.camera.CameraTools;
import org.junit.Before;
import org.junit.Test;

public class TestCameraTools extends UseSamples {

	@Before
	public void prepare() {
		samples.put("rotate", new Sample("glm-rotate.data", getClass()));
		samples.put("translate", new Sample("glm-translate.data", getClass()));
		samples.put("scale", new Sample("glm-scale.data", getClass()));
		samples.put("lookAt", new Sample("glm-lookat.data", getClass()));
		samples.put("ortho", new Sample("glm-ortho.data", getClass()));
		samples.put("frustum", new Sample("glm-frustum.data", getClass()));
		samples.put("project", new Sample("glm-project.data", getClass()));
		samples.put("unProject", new Sample("glm-unproject.data", getClass()));
		samples.put("perspective", new Sample("glm-perspective.data",
				getClass()));
	}

	@Test
	public void testRotate() {
		Sample s = samples.get("rotate");

		while (s.hasNext()) {
			s.startSample();

			Matrix4x4 mat = s.nextMatrix();
			double angle = s.nextDouble();
			Vector3 axe = s.nextVector3();
			Matrix4x4 rotateExpected = s.nextMatrix();
			Matrix4x4 rotate = CameraTools.rotate(mat, angle, axe);

			s.check(rotate, rotateExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testTranslate() {
		Sample s = samples.get("translate");

		while (s.hasNext()) {
			s.startSample();

			Matrix4x4 mat = s.nextMatrix();
			Vector3 delta = s.nextVector3();
			Matrix4x4 translateExpected = s.nextMatrix();
			Matrix4x4 translate = CameraTools.translate(mat, delta);

			s.check(translate, translateExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testScale() {
		Sample s = samples.get("scale");

		while (s.hasNext()) {
			s.startSample();

			Matrix4x4 mat = s.nextMatrix();
			Vector3 delta = s.nextVector3();
			Matrix4x4 scaleExpected = s.nextMatrix();
			Matrix4x4 scale = CameraTools.scale(mat, delta);

			s.check(scale, scaleExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testLookAt() {
		Sample s = samples.get("lookAt");

		while (s.hasNext()) {
			s.startSample();

			Vector3 eye = s.nextVector3();
			Vector3 center = s.nextVector3();
			Vector3 up = s.nextVector3();
			Matrix4x4 lookAtExpected = s.nextMatrix();
			Matrix4x4 lookAt = CameraTools.lookAt(eye, center, up);
			
			s.check(lookAt, lookAtExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testOrtho() {
		Sample s = samples.get("ortho");

		while (s.hasNext()) {
			s.startSample();

			double left = s.nextDouble();
			double right = s.nextDouble();
			double bottom = s.nextDouble();
			double top = s.nextDouble();
			double zNear = s.nextDouble();
			double zFar = s.nextDouble();

			Matrix4x4 ortho1Expected = s.nextMatrix();
			Matrix4x4 ortho2Expected = s.nextMatrix();

			Matrix4x4 ortho1 = CameraTools.ortho(left, right, bottom, top,
					zNear, zFar);
			Matrix4x4 ortho2 = CameraTools.ortho(left, right, bottom, top);

			s.check(ortho1, ortho1Expected);
			s.check(ortho2, ortho2Expected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testFrustum() {
		Sample s = samples.get("frustum");

		while (s.hasNext()) {
			s.startSample();

			double left = s.nextDouble();
			double right = s.nextDouble();
			double bottom = s.nextDouble();
			double top = s.nextDouble();
			double zNear = s.nextDouble();
			double zFar = s.nextDouble();

			Matrix4x4 frustumExpected = s.nextMatrix();

			Matrix4x4 frustum = CameraTools.frustum(left, right, bottom, top,
					zNear, zFar);

			s.check(frustum, frustumExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testPerspective() {
		Sample s = samples.get("perspective");

		while (s.hasNext()) {
			s.startSample();

			double fovy = s.nextDouble();
			double aspect = s.nextDouble();
			double zNear = s.nextDouble();
			double zFar = s.nextDouble();

			Matrix4x4 perspectiveExpected = s.nextMatrix();

			Matrix4x4 perspective = CameraTools.perspective(fovy, aspect,
					zNear, zFar);

			s.check(perspective, perspectiveExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testProject() {
		Sample s = samples.get("project");

		while (s.hasNext()) {
			s.startSample();

			Vector3 point = s.nextVector3();
			Matrix4x4 view = s.nextMatrix();
			Matrix4x4 proj = s.nextMatrix();
			double[] viewport = s.nextDoubles(4);
			Vector3 projectedExpected = s.nextVector3();

			Vector3 projected = new Vector3();
			CameraTools.project(point, projected, view, proj, viewport);

			s.check(projected, projectedExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}

	@Test
	public void testUnProject() {
		Sample s = samples.get("unProject");

		while (s.hasNext()) {
			s.startSample();

			Vector3 win = s.nextVector3();
			Matrix4x4 view = s.nextMatrix();
			Matrix4x4 proj = s.nextMatrix();
			double[] viewport = s.nextDoubles(4);
			Vector3 projectedExpected = s.nextVector3();

			Vector3 projected = new Vector3();
			CameraTools.unProject(win, projected, proj.mult(view).inverse(),
					viewport);

			s.check(projected, projectedExpected);
		}

		s.finalCheck(1, 1, 0.1, 0.1);
	}
}
