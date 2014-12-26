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
package org.graphstream.nui.space;

import org.graphstream.nui.geom.Vector3;

public class Bounds {
	protected final Vector3 lowestPoint;
	protected final Vector3 highestPoint;
	protected double diagonal;

	public Bounds() {
		lowestPoint = new Vector3();
		highestPoint = new Vector3();
	}

	public Bounds(Vector3 lo, Vector3 hi) {
		this();

		this.lowestPoint.copy(lo);
		this.highestPoint.copy(hi);
		fireBoundsUpdated();
	}

	public Bounds(Bounds clone) {
		this(clone.lowestPoint, clone.highestPoint);
	}

	public Vector3 getLowestPoint() {
		return lowestPoint;
	}

	public void setLowestPoint(Vector3 xyz) {
		lowestPoint.copy(xyz);
		fireBoundsUpdated();
	}

	public Vector3 getHighestPoint() {
		return highestPoint;
	}

	public void setHighestPoint(Vector3 xyz) {
		highestPoint.copy(xyz);
		fireBoundsUpdated();
	}

	public double getDiagonal() {
		return diagonal;
	}

	public void set(Vector3 lo, Vector3 hi) {
		set(lo.x(), lo.y(), lo.z(), hi.x(), hi.y(), hi.z());
	}

	public void set(double lx, double ly, double lz, double hx, double hy,
			double hz) {
		assert lx <= hx && ly <= hy && lz <= hz;

		lowestPoint.set(lx, ly, lz);
		highestPoint.set(hx, hy, hz);

		fireBoundsUpdated();
	}

	public boolean contains(double x, double y) {
		return x >= lowestPoint.x() && x <= highestPoint.x()
				&& y >= lowestPoint.y() && y <= highestPoint.y();
	}

	public boolean contains(double x, double y, double z) {
		return contains(x, y) && z >= lowestPoint.z() && z <= highestPoint.z();
	}

	public void computeDiagonal() {
		diagonal = highestPoint.distance(lowestPoint);
	}

	public double getWidth() {
		return highestPoint.x() - lowestPoint.x();
	}

	public double getHeight() {
		return highestPoint.y() - lowestPoint.y();
	}

	public double getDepth() {
		return highestPoint.z() - lowestPoint.z();
	}

	public Vector3 getCenter() {
		return new Vector3((highestPoint.x() + lowestPoint.x()) / 2.0,
				(highestPoint.y() + lowestPoint.y()) / 2.0,
				(highestPoint.z() + lowestPoint.z()) / 2.0);
	}

	protected void fireBoundsUpdated() {
		computeDiagonal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Bounds) {
			Bounds b = (Bounds) o;
			return b.highestPoint.equals(highestPoint)
					&& b.lowestPoint.equals(lowestPoint);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s;%s", lowestPoint, highestPoint);
	}
}
