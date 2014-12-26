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
package org.graphstream.nui.util;

import org.graphstream.nui.geom.Vector3;

public class Tools {
	public static double checkAndGetDouble(Object value)
			throws IllegalArgumentException {
		if (value instanceof Double)
			return (Double) value;

		if (value instanceof Number)
			return ((Number) value).doubleValue();

		throw new IllegalArgumentException(String.format(
				"invalid double value \"%s\"", value.getClass().getName()));
	}

	public static double[] checkAndGetDoubleArray(Object value)
			throws IllegalArgumentException {
		double[] r = null;

		if (value instanceof double[])
			r = (double[]) value;
		else if (value instanceof Double[]) {
			Double[] rO = (Double[]) value;
			r = new double[rO.length];

			for (int i = 0; i < r.length; i++)
				r[i] = rO[i];
		} else if (value instanceof Vector3) {
			Vector3 p3 = (Vector3) value;
			r = new double[] { p3.x(), p3.y(), p3.z() };
		} else if (value instanceof Object[]) {
			Object[] rO = (Object[]) value;
			r = new double[rO.length];

			for (int i = 0; i < r.length; i++)
				r[i] = checkAndGetDouble(rO[i]);
		}

		if (r == null)
			throw new IllegalArgumentException(String.format(
					"invalid double array value \"%s\"", value.getClass()
							.getName()));

		return r;
	}

	public static int checkAndGetInt(Object value)
			throws IllegalArgumentException {
		if (value instanceof Integer
				|| Integer.TYPE.isAssignableFrom(value.getClass()))
			return (Integer) value;
		else if (value instanceof Number)
			return ((Number) value).intValue();

		throw new IllegalArgumentException();
	}

	public static long checkAndGetLong(Object value)
			throws IllegalArgumentException {
		if (value instanceof Long
				|| Long.TYPE.isAssignableFrom(value.getClass()))
			return (Integer) value;
		else if (value instanceof Number)
			return ((Number) value).longValue();

		throw new IllegalArgumentException();
	}

	public static String checkAndGetString(Object value, boolean nullAllowed)
			throws IllegalArgumentException {
		if (value == null) {
			if (nullAllowed)
				return null;
			else
				throw new IllegalArgumentException();
		}

		return value.toString();
	}

	public static boolean checkAndGetBoolean(Object value)
			throws IllegalArgumentException {
		if (value instanceof Boolean)
			return (Boolean) value;

		throw new IllegalArgumentException();
	}

	public static boolean checkAndGetBoolean(Object value, boolean nullIsTrue)
			throws IllegalArgumentException {
		if (value == null)
			return nullIsTrue;

		return checkAndGetBoolean(value);
	}

	//
	// http://floating-point-gui.de/errors/comparison/
	//
	public static boolean fuzzyEquals(double a, double b, double epsilon) {
		if (a == b)
			return true;
		else {
			final double diff = Math.abs(a - b);

			if (a == 0 || b == 0 || diff < Double.MIN_NORMAL)
				return diff < (epsilon * Double.MIN_NORMAL);
			else {
				final double absA = Math.abs(a);
				final double absB = Math.abs(b);

				return diff / (absA + absB) < epsilon;
			}
		}
	}

	public static boolean fuzzyEquals(double[] a, double[] b, double epsilon) {
		if (a.length != b.length)
			return false;

		for (int i = 0; i < a.length; i++)
			if (!fuzzyEquals(a[i], b[i], epsilon))
				return false;

		return true;
	}
}
