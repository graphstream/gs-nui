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

import org.graphstream.nui.views.UICamera;
import org.graphstream.ui.geom.Point3;

public abstract class BaseCamera implements UICamera {
	protected int displayWidth;
	protected int displayHeight;

	protected Point3 viewportOrigin;
	protected double viewportWidth;
	protected double viewportHeight;

	protected CameraTransform transform;

	protected BaseCamera() {
		viewportOrigin = new Point3();
		transform = createTransform();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getViewportOrigin()
	 */
	@Override
	public Point3 getViewportOrigin() {
		return viewportOrigin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getViewportWidth()
	 */
	@Override
	public double getViewportWidth() {
		return viewportWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getViewportHeight()
	 */
	@Override
	public double getViewportHeight() {
		return viewportHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getDisplayWidth()
	 */
	@Override
	public int getDisplayWidth() {
		return displayWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.UICamera#getDisplayHeight()
	 */
	@Override
	public int getDisplayHeight() {
		return displayHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.UICamera#convert(org.graphstream.ui.geom.Point3
	 * , org.graphstream.ui.geom.Point3,
	 * org.graphstream.nui.views.UICamera.ConvertType)
	 */
	@Override
	public void convert(Point3 source, Point3 target, ConvertType type) {
		transform.convert(source, target, type);
	}

	protected abstract CameraTransform createTransform();

	protected void resizeDisplay(int width, int height) {
		displayWidth = width;
		displayHeight = height;
		transform.init(this);
	}

	protected void setViewportOrigin(Point3 origin) {
		viewportOrigin.copy(origin);
		transform.init(this);
	}
}
