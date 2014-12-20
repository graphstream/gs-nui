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

public abstract class BaseCamera3D<T extends CameraTransform> extends
		BaseCamera<T> implements UICamera3D {

	protected Point3 eye;
	protected double viewportDepth;
	protected ProjectionType projectionType;

	protected BaseCamera3D() {
		eye = new Point3();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getEyePosition()
	 */
	@Override
	public Point3 getCameraPosition() {
		return eye;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.camera.UICamera3D#getCameraUpVector()
	 */
	@Override
	public Vector3 getCameraUpVector() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.camera.UICamera3D#getViewportDepth()
	 */
	@Override
	public double getViewportDepth() {
		return viewportDepth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.camera.UICamera3D#getProjectionType()
	 */
	@Override
	public ProjectionType getProjectionType() {
		return projectionType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.camera.BaseCamera#setViewport(org.graphstream
	 * .ui.geom.Point3, org.graphstream.ui.geom.Point3)
	 */
	@Override
	protected void setViewport(Point3 lo, Point3 hi) {
		viewportOrigin.set((lo.x + hi.x) / 2, (lo.y + hi.y) / 2,
				(lo.z + hi.z) / 2);

		viewportWidth = hi.x - lo.x;
		viewportHeight = hi.y - lo.y;
		viewportDepth = hi.z - lo.z;

		transform.init(this);
	}
}
