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

import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.dataset.DatasetListener;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.views.opengl.OpenGLRenderer;

public class VBORenderer extends BaseOpenGLRenderer implements OpenGLRenderer,
		DatasetListener {
	public static final String VIEW_ID = "opengl-vbo";
	private static int VIEW_COUNT = 0;

	protected IntBuffer buffers;
	protected UIDataset dataset;
	protected boolean changed;

	private static String newViewId() {
		return String.format("%s-%d", VIEW_ID, VIEW_COUNT++);
	}

	public VBORenderer() {
		// TODO: camera & controller
		super(newViewId(), null, null);
	}

	public boolean checkCompatibility(GL gl) {
		//
		// Check version.
		//
		String versionStr = gl.glGetString(GL.GL_VERSION);
		versionStr = versionStr.substring(0, 4);
		float version = Float.parseFloat(versionStr);
		boolean versionOK = (version >= 1.59f);

		//
		// Check if extension is available.
		//
		boolean extensionOK = gl
				.isExtensionAvailable("GL_ARB_vertex_buffer_object");

		//
		// Check for VBO functions.
		//
		boolean functionsOK = gl.isFunctionAvailable("glGenBuffersARB")
				&& gl.isFunctionAvailable("glBindBufferARB")
				&& gl.isFunctionAvailable("glBufferDataARB")
				&& gl.isFunctionAvailable("glDeleteBuffersARB");

		return versionOK && extensionOK && functionsOK;
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
		buffers = IntBuffer.allocate(1);
		dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);
		dataset.addDatasetListener(this);

		super.init(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIView#close()
	 */
	@Override
	public void close() {
		super.close();
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
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();

		gl.glGenBuffers(1, buffers);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable
	 * )
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));

		if (changed) {
			gl.glBufferData(GL2.GL_ARRAY_BUFFER, dataset.getNodeCount(),
					dataset.getNodesXYZ(), GL2.GL_DYNAMIC_DRAW);

			changed = false;
		}

		camera.pushView(gl);

		gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, 0);
		gl.glDrawArrays(GL2.GL_POINT, 0, dataset.getNodeCount());

		camera.popView(gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable
	 * , int, int, int, int)
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.dataset.DatasetListener#nodeMoved(org.graphstream
	 * .nui.indexer.ElementIndex, double, double, double)
	 */
	@Override
	public void nodeMoved(ElementIndex nodeIndex, double x, double y, double z) {
		changed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.dataset.DatasetListener#allNodesMoved()
	 */
	@Override
	public void allNodesMoved() {
		changed = true;
	}
}
