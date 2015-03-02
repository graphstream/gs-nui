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
package org.graphstream.nui.views.camera;

import java.util.logging.Logger;

import org.graphstream.nui.geom.Matrix4x4;
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.geom.Vector4;
import org.graphstream.nui.views.UICamera;
import org.graphstream.nui.views.UICamera.ConvertType;

import static org.graphstream.nui.views.camera.CameraTools.*;

public class DefaultMatrixTransform implements MatrixTransform {
	private static final Logger LOGGER = Logger
			.getLogger(DefaultMatrixTransform.class.getName());

	protected UICamera3D camera;
	protected Matrix4x4 modelView;
	protected Matrix4x4 model, view, projection;
	protected Matrix4x4 mvp;
	protected Matrix4x4 invMVP;

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

		Vector3 at = this.camera.getObservedSpace().getCenter();
		Vector3 eye = this.camera.getCameraPosition();

		System.out.printf("at = %s%n", at);

		model = new Matrix4x4(1.0);

		Vector3 delta = new Vector3(at);
		delta.scalarMult(-1.0);
		model = translate(model, delta);

		Vector3 rotation = this.camera.getSpaceRotation();
		model = rotate(model, rotation.z(), new Vector3(0, 0, 1));
		model = rotate(model, rotation.x(), new Vector3(1, 0, 0));
		model = rotate(model, rotation.y(), new Vector3(0, 1, 0));

		view = lookAt(eye, new Vector3(0, 0, 0), new Vector3(0.0, 1.0, 0.0));

		Vector4 viewport = camera.getViewport();
		double aspect = viewport.get(2) / viewport.get(3);

		System.out.printf("aspect %f (%.0fx%.0f)%n", aspect, viewport.get(2),
				viewport.get(3));

		switch (camera3d.getProjectionType()) {
		case ORTHOGONAL:
			projection = ortho(-1.5 * aspect, 1.5 * aspect, -1.5, 1.5, -100,
					100);
			break;
		case PERSPECTIVE:
			projection = infinitePerspective(Math.PI / 4.0, aspect, 0.1);
			break;
		}

		modelView = view.mult(model);
		mvp = projection.mult(view).mult(model);
		invMVP = mvp.inverse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.camera.CameraTransform#convert(org.graphstream
	 * .nui.geom.Vector3, org.graphstream.nui.geom.Vector3,
	 * org.graphstream.nui.views.UICamera.ConvertType)
	 */
	@Override
	public void convert(Vector3 source, Vector3 target, ConvertType type) {
		switch (type) {
		case GU_TO_PX:
			project(source, target, modelView, projection, camera.getViewport()
					.getRawData());
			break;
		case PX_TO_GU:
			unProject(source, target, invMVP, camera.getViewport().getRawData());
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.camera.MatrixTransform#getMVPMatrix()
	 */
	@Override
	public Matrix4x4 getMVPMatrix() {
		return mvp;
	}
}
