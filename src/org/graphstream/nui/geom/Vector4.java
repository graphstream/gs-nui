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
package org.graphstream.nui.geom;

import java.util.Arrays;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector3;

public class Vector4 extends Vector3 {
	public Vector4() {
		data = new double[4];
		Arrays.fill(data, 0);
	}

	public Vector4(Vector3 v, double w) {
		data = Arrays.copyOf(v.data, 4);
		data[3] = w;
	}

	public Vector4(Point3 p, double w) {
		data = new double[] { p.x, p.y, p.z, w };
	}

	public Vector4(double x, double y, double z, double w) {
		data = new double[] { x, y, z, w };
	}

	public double w() {
		return data[3];
	}

	public Vector3 asVector3() {
		return new Vector3(data[0], data[1], data[2]);
	}

	public Vector4 mult(Vector4 v) {
		return new Vector4(data[0] * v.data[0], data[1] * v.data[1], data[2]
				* v.data[2], data[3] * v.data[3]);
	}

	public Vector4 sub(Vector4 v) {
		return new Vector4(data[0] - v.data[0], data[1] - v.data[1], data[2]
				- v.data[2], data[3] - v.data[3]);
	}

	public Vector4 add(Vector4 v) {
		return new Vector4(data[0] + v.data[0], data[1] + v.data[1], data[2]
				+ v.data[2], data[3] + v.data[3]);
	}
}
