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

import java.util.logging.Logger;

import org.graphstream.nui.views.UICamera;
import org.graphstream.nui.views.UICamera.ConvertType;
import org.graphstream.ui.geom.Point3;

import static org.graphstream.nui.views.camera.Matrix4Tools.*;

public class PerspectiveTransform implements CameraTransform {
	private static final Logger LOGGER = Logger
			.getLogger(PerspectiveTransform.class.getName());

	protected UICamera3D camera;
	protected Point3 theta = new Point3();
	protected Matrix4x4 mvp;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.camera.CameraTransform#init(org.graphstream
	 * .nui.views.UICamera)
	 */
	@Override
	public void init(UICamera camera) {
		if (camera instanceof UICamera3D) {
			this.camera = (UICamera3D) camera;
		} else {
			LOGGER.severe("This transform needs a 3D camera");
			throw new RuntimeException();
		}

		UICamera3D camera3d = (UICamera3D) camera;

		Point3 eye = this.camera.getEyePosition();
		Point3 at = this.camera.getViewportOrigin();

		Matrix4x4 model = new Matrix4x4(1.0);
		Matrix4x4 view = lookAt(eye, at, CameraTools.computeUpVector(camera3d));
		Matrix4x4 projection = perspective(0, 0, 0, 0);

		mvp = projection.mult(view).mult(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.camera.CameraTransform#convert(org.graphstream
	 * .ui.geom.Point3, org.graphstream.ui.geom.Point3,
	 * org.graphstream.nui.views.UICamera.ConvertType)
	 */
	@Override
	public void convert(Point3 source, Point3 target, ConvertType type) {
		// TODO Auto-generated method stub

	}
}
