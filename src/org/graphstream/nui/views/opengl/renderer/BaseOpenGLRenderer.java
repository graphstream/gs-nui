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
package org.graphstream.nui.views.opengl.renderer;

import java.awt.Component;

import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.views.BaseGraphRenderer;
import org.graphstream.nui.views.UIController;
import org.graphstream.nui.views.opengl.OpenGLCamera;
import org.graphstream.nui.views.opengl.OpenGLRenderer;
import org.graphstream.nui.views.swing.SwingView;
import org.graphstream.ui.geom.Point3;

public abstract class BaseOpenGLRenderer extends
		BaseGraphRenderer<OpenGLCamera, UIController> implements
		OpenGLRenderer, GLEventListener, SwingView {
	static {
		GLProfile.initSingleton();
	}

	protected GLProfile profile;
	protected GLCapabilities capabilities;
	protected GLCanvas canvas;

	protected BaseOpenGLRenderer(String viewId, OpenGLCamera camera,
			UIController controller) {
		super(viewId, camera, controller);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.UIGraphRenderer#setViewport(org.graphstream
	 * .ui.geom.Point3, double[])
	 */
	@Override
	public void setViewport(Point3 center, double... dims) {
		// TODO Auto-generated method stub

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
		super.init(ctx);

		profile = GLProfile.getDefault();
		capabilities = new GLCapabilities(profile);
		canvas = new GLCanvas(capabilities);

		canvas.addGLEventListener(this);
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

	protected abstract boolean checkCompatibility(GL gl);
}