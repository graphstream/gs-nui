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

public class Vector3 extends AbstractVector<Vector3> {
	public Vector3() {
		super(3);
	}

	public Vector3(Vector3 copy) {
		super(copy);
	}

	public Vector3(Vector2 other, double z) {
		super(other.x(), other.y(), z);
	}

	public Vector3(double x, double y, double z) {
		super(x, y, z);
	}

	public Vector3 selfCrossProduct(Vector3 other) {
		double x, y;

		x = (data[1] * other.data[2]) - (data[2] * other.data[1]);
		y = (data[2] * other.data[0]) - (data[0] * other.data[2]);
		data[2] = (data[0] * other.data[1]) - (data[1] * other.data[0]);
		data[0] = x;
		data[1] = y;

		dataChanged();

		return this;
	}

	public Vector3 crossProduct(Vector3 other) {
		Vector3 clone = new Vector3(this);
		return clone.selfCrossProduct(other);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.geom.AbstractVector#createNewInstance()
	 */
	@Override
	protected Vector3 createNewInstance() {
		return new Vector3();
	}
}
