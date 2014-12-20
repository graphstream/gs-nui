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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import java.util.logging.Logger;

import org.graphstream.nui.geom.Matrix4x4;
import org.graphstream.nui.geom.Vector4;
import org.graphstream.nui.util.Tools;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector3;
import org.junit.Assert;

public class Sample {
	static final Logger LOGGER = Logger.getLogger(Sample.class.getName());

	public static final double EPSILON = 1E-12;
	public static final double EPSILON_2 = 1E-10;
	public static final double EPSILON_3 = 1E-8;

	InputStream in;
	ReadableByteChannel dataIn;
	ByteBuffer buffer;
	String res;

	double epsilon, epsilon2, epsilon3;

	int samples = 0;
	int compare = 0;
	int needEpsilon2 = 0;
	int needEpsilon3 = 0;
	int needEpsilon2E = 0;
	int needEpsilon3E = 0;

	double next = Double.NaN;

	public Sample(String res) {
		this(res, Sample.class);
	}

	public Sample(String res, Class<?> cls) {
		this(res, cls, EPSILON, EPSILON_2, EPSILON_3);
	}

	public Sample(String res, Class<?> cls, double epsilon, double epsilon2,
			double epsilon3) {
		this.res = res;
		this.epsilon = epsilon;
		this.epsilon2 = epsilon2;
		this.epsilon3 = epsilon3;

		in = cls.getResourceAsStream("data/" + res);

		if (in == null)
			Assert.fail("'" + res + "' not found");

		dataIn = Channels.newChannel(in);
		buffer = ByteBuffer.allocate(Double.SIZE / 8);
		buffer.order(ByteOrder.nativeOrder());

		checkNext();
	}

	public boolean hasNext() {
		return !Double.isNaN(next);
	}

	public void startSample() {
		samples++;
	}

	protected void checkNext() {
		buffer.rewind();

		try {
			if (dataIn.read(buffer) > 0)
				next = buffer.getDouble(0);
			else
				next = Double.NaN;

		} catch (IOException e) {
			next = Double.NaN;
		}
	}

	public double nextDouble() {
		double r = next;
		checkNext();

		return r;
	}

	public double[] nextDoubles(int size) {
		return nextData(size);
	}

	protected double[] nextData(int size) {
		double[] data = new double[size];

		for (int i = 0; i < size; i++)
			data[i] = nextDouble();

		return data;
	}

	public Matrix4x4 nextMatrix() {
		return new Matrix4x4(nextData(16));
	}

	public Point3 nextPoint3() {
		double[] data = nextData(3);
		return new Point3(data[0], data[1], data[2]);
	}

	public Vector3 nextVector3() {
		double[] data = nextData(3);
		return new Vector3(data[0], data[1], data[2]);
	}

	public Vector4 nextVector4() {
		return new Vector4(nextData(4));
	}

	public void check(double[] a, double[] b) {
		compare++;

		if (!Tools.fuzzyEquals(a, b, epsilon)) {
			if (!Tools.fuzzyEquals(a, b, epsilon2)) {
				Assert.assertTrue(Tools.fuzzyEquals(a, b, epsilon3));

				int c = fuzzyElements(a, b, epsilon2);
				needEpsilon3++;
				needEpsilon3E += c;
			} else {
				int c = fuzzyElements(a, b, epsilon);
				needEpsilon2++;
				needEpsilon2E += c;
			}
		}
	}

	public void check(Point3 a, Point3 b) {
		check(new double[] { a.x, a.y, a.z }, new double[] { b.x, b.y, b.z });
	}

	public void check(Matrix4x4 a, Matrix4x4 b) {
		double[] aData = a.getRawData();
		double[] bData = b.getRawData();

		compare++;

		if (!a.fuzzyEquals(b, epsilon)) {
			if (!a.fuzzyEquals(b, epsilon2)) {
				Assert.assertTrue(a.fuzzyEquals(b, epsilon3));

				int c = fuzzyElements(aData, bData, epsilon2);
				needEpsilon3++;
				needEpsilon3E += c;
			} else {
				int c = fuzzyElements(aData, bData, epsilon);
				needEpsilon2++;
				needEpsilon2E += c;
			}
		}
	}

	public void check(Vector4 a, Vector4 b) {
		double[] aData = a.data;
		double[] bData = b.data;

		compare++;

		if (!a.fuzzyEquals(b, epsilon)) {
			if (!a.fuzzyEquals(b, epsilon2)) {
				Assert.assertTrue(a.fuzzyEquals(b, epsilon3));

				int c = fuzzyElements(aData, bData, epsilon2);
				needEpsilon3++;
				needEpsilon3E += c;
			} else {
				int c = fuzzyElements(aData, bData, epsilon);
				needEpsilon2++;
				needEpsilon2E += c;
			}
		}
	}

	public double percentMatrixNeedingEpsilon2() {
		return 100 * (double) needEpsilon2 / (double) compare;
	}

	public double percentMatrixNeedingEpsilon3() {
		return 100 * (double) needEpsilon3 / (double) compare;
	}

	public double percentElementNeedingEpsilon2() {
		return 100 * (double) needEpsilon2E / (double) (compare * 16);
	}

	public double percentElementNeedingEpsilon3() {
		return 100 * (double) needEpsilon3E / (double) (compare * 16);
	}

	public void log() {
		LOGGER.info(String
				.format(Locale.ROOT,
						"[%s] %d samples analyzed with EPSILON=%.2e, "
								+ "%.2f%% of them needs EPSILON=%.2e (%.2f%% of the components), "
								+ "%.2f%% of them needs EPSILON=%.2e (%.2f%% of the components)",
						res, samples, epsilon, percentMatrixNeedingEpsilon2(),
						epsilon2, percentElementNeedingEpsilon2(),
						percentMatrixNeedingEpsilon3(), epsilon3,
						percentElementNeedingEpsilon3()));
	}

	public void finalCheck(double t1, double t2, double t3, double t4) {
		log();

		/*
		 * There will be some difference in the results, but we want them as
		 * close of possible and we try to limit the number of floating compare
		 * needing a larger epsilon.
		 */
		Assert.assertTrue(percentMatrixNeedingEpsilon2() < t1);
		Assert.assertTrue(percentMatrixNeedingEpsilon3() < t2);
		Assert.assertTrue(percentElementNeedingEpsilon2() < t3);
		Assert.assertTrue(percentElementNeedingEpsilon3() < t4);
	}

	public static int fuzzyElements(double[] a, double[] b, double epsilon) {
		int c = 0;

		for (int i = 0; i < a.length; i++)
			if (!Tools.fuzzyEquals(a[i], b[i], epsilon))
				c++;

		return c;
	}
}