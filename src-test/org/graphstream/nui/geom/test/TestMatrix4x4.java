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
package org.graphstream.nui.geom.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

import org.graphstream.nui.geom.Matrix4x4;
import org.graphstream.nui.util.Tools;
import org.junit.Assert;
import org.junit.Test;

public class TestMatrix4x4 {
	private static final Logger LOGGER = Logger.getLogger(TestMatrix4x4.class
			.getName());

	public static final double EPSILON = 1E-4;
	public static final double EPSILON_2 = 1E-3;
	public static final double EPSILON_3 = 5E-1;

	@Test
	public void testInverse() {
		InputStream data = getClass().getResourceAsStream("glm-inverse.data");

		if (data == null)
			Assert.fail("'glm-inverse.data' not found");

		Scanner sc = new Scanner(data);
		sc.useLocale(Locale.ROOT);
		int sample = 0;
		int needEpsilon2 = 0;
		int needEpsilon3 = 0;
		int needEpsilon2E = 0;
		int needEpsilon3E = 0;

		while (sc.hasNextDouble()) {
			sample++;

			double[] matData = new double[] { sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble() };

			double[] invData = new double[] { sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble(), sc.nextDouble(),
					sc.nextDouble(), sc.nextDouble() };

			Matrix4x4 mat = new Matrix4x4(matData);
			Matrix4x4 inv = mat.inverse();
			Matrix4x4 expectedInv = new Matrix4x4(invData);

			double[] invData2 = inv.getRawData();

			if (!inv.equals(expectedInv, EPSILON)) {
				if (!inv.equals(expectedInv, EPSILON_2)) {
					Assert.assertTrue(inv.equals(expectedInv, EPSILON_3));
					needEpsilon3++;
					needEpsilon3E += fuzzyElements(invData, invData2, EPSILON_2);
				} else {
					needEpsilon2++;
					needEpsilon2E += fuzzyElements(invData, invData2, EPSILON);
				}
			}
		}

		LOGGER.info(String
				.format(Locale.ROOT,
						"%d samples analyzed with EPSILON=%.2e, %.2f%% of them needs EPSILON=%.2e (%.2f%% of the components), %.2f%% of them needs EPSILON=%.2e (%.2f%% of the components)",
						sample, EPSILON, 100 * (double) needEpsilon2
								/ (double) sample, EPSILON_2, 100
								* (double) needEpsilon2E / (16.0 * sample), 100
								* (double) needEpsilon3 / (double) sample,
						EPSILON_3, 100 * (double) needEpsilon3E
								/ (16.0 * sample)));

		/*
		 * There will be some difference in the results, but we want them as
		 * close of possible and we try to limit the number of floating compare
		 * needing a larger epsilon.
		 */
		Assert.assertTrue(needEpsilon2 < 0.06 * sample);
		Assert.assertTrue(needEpsilon3 < 0.01 * sample);
		Assert.assertTrue((double) needEpsilon2E / (16.0 * sample) < 0.01);
		Assert.assertTrue((double) needEpsilon3E / (16.0 * sample) < 0.005);

		sc.close();

		try {
			data.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected int fuzzyElements(double[] a, double[] b, double epsilon) {
		int c = 0;

		for (int i = 0; i < a.length; i++)
			if (!Tools.fuzzyEquals(a[i], b[i], epsilon))
				c++;

		return c;
	}
}
