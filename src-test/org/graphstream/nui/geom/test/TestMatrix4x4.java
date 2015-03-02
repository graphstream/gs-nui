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
package org.graphstream.nui.geom.test;

import org.graphstream.nui.geom.Matrix4x4;
import org.graphstream.nui.geom.Vector4;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestMatrix4x4 extends UseSamples {
	@Before
	public void prepare() {
		samples.put("inverse", new Sample("glm-inverse.data"));
		samples.put("mult", new Sample("glm-mult.data"));
		samples.put("vmult", new Sample("glm-vmult.data"));
	}

	@Test
	public void testInverse() {
		Sample s = samples.get("inverse");

		while (s.hasNext()) {
			s.startSample();

			Matrix4x4 mat = s.nextMatrix();
			Matrix4x4 expectedInv = s.nextMatrix();
			Matrix4x4 inv = mat.inverse();

			s.check(inv, expectedInv);
		}

		s.finalCheck(6, 1, 1, 0.5);
	}

	@Test
	public void testMult() {
		Sample s = samples.get("mult");

		while (s.hasNext()) {
			s.startSample();

			Matrix4x4 m1 = s.nextMatrix();
			Matrix4x4 m2 = s.nextMatrix();
			Matrix4x4 m1m2Expected = s.nextMatrix();
			Matrix4x4 m2m1Expected = s.nextMatrix();
			Matrix4x4 m1m2 = m1.mult(m2);
			Matrix4x4 m2m1 = m2.mult(m1);

			s.check(m1m2, m1m2Expected);
			s.check(m2m1, m2m1Expected);
		}

		s.finalCheck(6, 1, 1, 0.5);
	}

	@Test
	public void testVMult() {
		Sample s = samples.get("vmult");

		while (s.hasNext()) {
			s.startSample();

			Matrix4x4 m = s.nextMatrix();
			Vector4 v = s.nextVector4();
			Vector4 mvExpected = s.nextVector4();
			Vector4 vmExpected = s.nextVector4();
			Vector4 mv = m.mult(v);
			Vector4 vm = v.mult(m);

			s.check(mv, mvExpected);
			s.check(vm, vmExpected);
		}

		s.finalCheck(2, 0.5, 0.1, 0.01);
	}

	@Test
	public void testTranspose() {
		for (int i = 0; i < 100; i++) {
			Matrix4x4 m = new Matrix4x4(new double[] { nextRandom(),
					nextRandom(), nextRandom(), nextRandom(), nextRandom(),
					nextRandom(), nextRandom(), nextRandom(), nextRandom(),
					nextRandom(), nextRandom(), nextRandom(), nextRandom(),
					nextRandom(), nextRandom(), nextRandom() });
			Matrix4x4 t = m.transpose();

			for (int c = 0; c < 4; c++)
				for (int r = 0; r < 4; r++)
					Assert.assertArrayEquals(new double[] { m.get(c, r) },
							new double[] { t.get(r, c) }, Double.MIN_NORMAL);
		}
	}

	protected double nextRandom() {
		return 128.0 * Math.random() - 64.0;
	}
}
