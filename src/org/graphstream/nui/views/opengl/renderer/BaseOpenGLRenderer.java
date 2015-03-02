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
package org.graphstream.nui.views.opengl.renderer;

import java.awt.Component;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.SwingUtilities;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UISpace;
import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.geom.Vector3;
import org.graphstream.nui.space.Bounds;
import org.graphstream.nui.spacePartition.SpaceCell;
import org.graphstream.nui.views.BaseGraphRenderer;
import org.graphstream.nui.views.UIController;
import org.graphstream.nui.views.opengl.DefaultOpenGLCamera;
import org.graphstream.nui.views.opengl.OpenGLCamera;
import org.graphstream.nui.views.opengl.OpenGLRenderer;
import org.graphstream.nui.views.swing.SwingView;

public abstract class BaseOpenGLRenderer extends
		BaseGraphRenderer<OpenGLCamera, UIController> implements
		OpenGLRenderer, GLEventListener, SwingView {
	static {
		GLProfile.initSingleton();
	}

	private static final Logger LOGGER = Logger
			.getLogger(BaseOpenGLRenderer.class.getName());

	public static final String VIEW_TYPE_ID_SUFFIX = "-opengl-renderer";

	protected GLProfile profile;
	protected GLCapabilities capabilities;
	protected GLCanvas canvas;

	protected BaseOpenGLRenderer(String viewTypePrefix, String viewId) {
		this(viewTypePrefix, viewId, new DefaultOpenGLCamera(), null);
	}

	protected BaseOpenGLRenderer(String viewTypePrefix, String viewId,
			OpenGLCamera camera, UIController controller) {
		super(viewTypePrefix + VIEW_TYPE_ID_SUFFIX, viewId, camera, controller);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.BaseGraphRenderer#init(org.graphstream.nui.
	 * UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		assert SwingUtilities.isEventDispatchThread();

		super.init(ctx);

		profile = GLProfile.getDefault();
		capabilities = new GLCapabilities(profile);
		capabilities.setHardwareAccelerated(true);

		canvas = new GLCanvas(capabilities);

		canvas.addGLEventListener(this);

		if (camera instanceof DefaultOpenGLCamera)
			((DefaultOpenGLCamera) camera).setRenderingSurface(canvas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIView#update()
	 */
	@Override
	public void update() {
		canvas.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIView#close()
	 */
	@Override
	public void close() {
		canvas.removeGLEventListener(this);
		canvas.destroy();
		canvas = null;
		profile = null;
		capabilities = null;

		super.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.swing.SwingView#getSwingComponent()
	 */
	@Override
	public Component getSwingComponent() {
		return canvas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		boolean isCompatible = checkCompatibility(gl);

		LOGGER.info("is compatible ? " + isCompatible);

		//gl.glEnable(GL2.GL_LINE_SMOOTH);
		//gl.glEnable(GL2.GL_POINT_SMOOTH);

		//gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		//gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_DONT_CARE);
		//gl.glDisable(GL2.GL_DEPTH_TEST);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		//gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_DONT_CARE);
		//gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_DONT_CARE);

		// gl.glShadeModel(GL2.GL_FLAT);
		gl.glPointSize(10.0f);
	}

	protected abstract boolean checkCompatibility(GL gl);

	protected void drawWireBounds(GL2 gl, Bounds boundary) {
		Vector3 lo = boundary.getLowestPoint();
		Vector3 hi = boundary.getHighestPoint();

		double lx = lo.x(), ly = lo.y(), lz = lo.z();
		double hx = hi.x(), hy = hi.y(), hz = hi.z();

		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3d(lx, ly, lz);
		gl.glVertex3d(hx, ly, lz);
		gl.glVertex3d(hx, hy, lz);
		gl.glVertex3d(lx, hy, lz);
		gl.glVertex3d(lx, ly, lz);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3d(lx, ly, hz);
		gl.glVertex3d(hx, ly, hz);
		gl.glVertex3d(hx, hy, hz);
		gl.glVertex3d(lx, hy, hz);
		gl.glVertex3d(lx, ly, hz);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(lx, ly, lz);
		gl.glVertex3d(lx, ly, hz);

		gl.glVertex3d(hx, ly, lz);
		gl.glVertex3d(hx, ly, hz);

		gl.glVertex3d(lx, hy, lz);
		gl.glVertex3d(lx, hy, hz);

		gl.glVertex3d(hx, hy, lz);
		gl.glVertex3d(hx, hy, hz);
		gl.glEnd();
	}

	protected void drawSpaceBox(GL2 gl) {
		UISpace space = (UISpace) ctx.getModule(UISpace.MODULE_ID);
		drawWireBounds(gl, space.getBounds());
	}

	protected void drawSpacePartition(GL2 gl) {
		UISpacePartition spacePartition = (UISpacePartition) ctx
				.getModule(UISpacePartition.MODULE_ID);

		if (spacePartition == null) {
			System.out.printf("no space partition\n");
			return;
		}

		for (SpaceCell cell : spacePartition)
			drawWireBounds(gl, cell.getBoundary());
	}

	protected OpenGLCamera createCamera() {
		return new DefaultOpenGLCamera();
	}

	protected UIController createController() {
		// TODO
		return null;
	}
}
