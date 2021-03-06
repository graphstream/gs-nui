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
package org.graphstream.nui.views.opengl;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.media.opengl.GL2;

import org.graphstream.nui.views.camera.BaseCamera3D;
import org.graphstream.nui.views.camera.DefaultMatrixTransform;
import org.graphstream.nui.views.camera.MatrixTransform;

public class DefaultOpenGLCamera extends BaseCamera3D<MatrixTransform>
		implements OpenGLCamera {
	protected Component renderingSurface;
	protected Listener listener;

	public void setRenderingSurface(Component component) {
		if (renderingSurface != null)
			renderingSurface.removeComponentListener(listener);

		if (listener == null)
			listener = new Listener();

		renderingSurface = component;
		renderingSurface.addComponentListener(listener);

		viewport.set(0.0, 0.0, renderingSurface.getWidth(),
				renderingSurface.getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.opengl.OpenGLCamera#pushView(javax.media.opengl
	 * .GL2)
	 */
	@Override
	public void pushView(GL2 gl) {
		checkChanged();
		assert transform != null;
		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		// gl.glViewport(0, 0, getViewportWidth(), getViewportHeight());
		gl.glLoadMatrixd(transform.getMVPMatrix().getRawData(), 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.opengl.OpenGLCamera#popView(javax.media.opengl
	 * .GL2)
	 */
	@Override
	public void popView(GL2 gl) {
		gl.glPopMatrix();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.camera.BaseCamera#createTransform()
	 */
	@Override
	protected MatrixTransform createTransform() {
		return new DefaultMatrixTransform();
	}

	class Listener implements ComponentListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ComponentListener#componentResized(java.awt.event.
		 * ComponentEvent)
		 */
		@Override
		public void componentResized(ComponentEvent e) {
			viewport.set(0.0, 0.0, renderingSurface.getWidth(),
					renderingSurface.getHeight());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.
		 * ComponentEvent)
		 */
		@Override
		public void componentMoved(ComponentEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.
		 * ComponentEvent)
		 */
		@Override
		public void componentShown(ComponentEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.
		 * ComponentEvent)
		 */
		@Override
		public void componentHidden(ComponentEvent e) {
		}
	}
}
