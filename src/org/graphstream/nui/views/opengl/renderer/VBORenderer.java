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

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JFrame;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.dataset.DatasetListener;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.ElementIndex.EdgeIndex;
import org.graphstream.nui.views.opengl.DefaultOpenGLCamera;
import org.graphstream.nui.views.opengl.OpenGLRenderer;

public class VBORenderer extends BaseOpenGLRenderer implements OpenGLRenderer,
		DatasetListener {
	public static final String VIEW_TYPE_ID_PREFIX = "vbo";

	private static int VIEW_COUNT = 0;

	protected IntBuffer buffers;
	protected UIDataset dataset;
	protected boolean changed;

	protected IntBuffer nodeIndices;
	protected IntBuffer edgeIndices;

	protected UIIndexer indexer;

	private static String newViewId() {
		return String.format("%s%s-%d", VIEW_TYPE_ID_PREFIX,
				VIEW_TYPE_ID_SUFFIX, VIEW_COUNT++);
	}

	public VBORenderer() {
		this(newViewId());
	}

	public VBORenderer(String viewId) {
		// TODO: camera & controller
		super(VIEW_TYPE_ID_PREFIX, viewId, new DefaultOpenGLCamera(), null);
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
		buffers = IntBuffer.allocate(3);
		dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);
		dataset.addDatasetListener(this);

		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);

		nodeIndices = IntBuffer.allocate(0);
		edgeIndices = IntBuffer.allocate(0);

		changed = true;

		super.init(ctx);

		JFrame frame = new JFrame();
		frame.add(canvas);
		canvas.setPreferredSize(new Dimension(640, 480));
		frame.pack();
		frame.setVisible(true);
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
		super.init(drawable);

		GL2 gl = drawable.getGL().getGL2();

		// gl.glClearColor(0.1843f, 0.28627451f, 0.517647f, 1.0f);
		gl.glClearColor(0.89f, 0.89f, 0.89f, 1.0f);

		gl.glGenBuffers(3, buffers);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

		System.out.printf("Buffers : %d %d %d%n", buffers.get(0),
				buffers.get(1), buffers.get(2));
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
		assert gl != null;

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		checkBuffers(gl);

		camera.pushView(gl);

		// gl.glColor4f(0.99f, 0.99f, 0.99f, 0.75f);
		gl.glColor4f(0.7f, 0.1f, 0.1f, 0.25f);

		drawSpaceBox(gl);
		drawSpacePartition(gl);

		gl.glColor4f(0.1f, 0.1f, 0.1f, 0.75f);

		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, arrayBuffer());
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, nodeBuffer());

		gl.glVertexPointer(dataset.getPointDimension(), GL2.GL_DOUBLE, 0, 0);
		gl.glDrawArrays(GL2.GL_POINTS, 0, dataset.getNodeCount());

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, edgeBuffer());
		gl.glDrawArrays(GL2.GL_LINES, 0, dataset.getEdgeCount());

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

		// GLUT glut = new GLUT();
		// glut.glutWireCube(100.0f);
		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3f(-100, -100, -100);
		// gl.glVertex3f(100, 100, 100);
		// gl.glEnd();

		camera.popView(gl);
		/*
		 * Point3 t1 = new Point3(100, 100, 0); Point3 t2 = new Point3();
		 * camera.convert(t1, t2, ConvertType.GU_TO_PX);
		 * System.out.printf("%s -> %s%n", t1, t2); t1 = new Point3(-100, -100,
		 * 0); camera.convert(t1, t2, ConvertType.GU_TO_PX);
		 * System.out.printf("%s -> %s%n", t1, t2);
		 */
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

	protected int arrayBuffer() {
		return buffers.get(0);
	}

	protected int nodeBuffer() {
		return buffers.get(1);
	}

	protected int edgeBuffer() {
		return buffers.get(2);
	}

	protected void checkBuffers(GL2 gl) {
		if (changed) {
			int nodeCount = dataset.getNodeCount();
			int edgeCount = dataset.getEdgeCount();

			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, arrayBuffer());
			gl.glBufferData(GL2.GL_ARRAY_BUFFER,
					nodeCount * dataset.getPointDimension() * Double.SIZE / 8,
					dataset.getNodesXYZ(), GL2.GL_DYNAMIC_DRAW);

			if (nodeCount != nodeIndices.capacity()) {
				nodeIndices = ByteBuffer
						.allocateDirect(nodeCount * Integer.SIZE / 8)
						.order(ByteOrder.nativeOrder()).asIntBuffer();

				for (int idx = 0; idx < nodeCount; idx++)
					nodeIndices.put(idx, idx);

				gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, nodeBuffer());
				gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, nodeCount,
						nodeIndices.rewind(), GL2.GL_DYNAMIC_DRAW);
			}

			if (edgeCount != edgeIndices.capacity())
				edgeIndices = ByteBuffer
						.allocateDirect(edgeCount * 2 * Integer.SIZE / 8)
						.order(ByteOrder.nativeOrder()).asIntBuffer();

			edgeIndices.rewind();

			for (int idx = 0; idx < edgeCount; idx++) {
				EdgeIndex e = indexer.getEdgeIndex(idx);

				edgeIndices.put(e.getSource().index());
				edgeIndices.put(e.getTarget().index());
			}

			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, edgeBuffer());
			gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, edgeCount,
					edgeIndices.rewind(), GL2.GL_DYNAMIC_DRAW);

			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

			changed = false;
		}
	}
}
