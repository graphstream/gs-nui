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
package org.graphstream.nui.swing;

import java.util.List;

import javax.swing.JFrame;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIView;
import org.graphstream.nui.UIViewer;
import org.graphstream.nui.views.swing.SwingView;

public class SwingViewer implements UIViewer {
	protected JFrame frame;
	protected UIContext ctx;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIViewer#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		this.ctx = ctx;
		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		for (UIView view : ctx.getViews()) {
			if (view instanceof SwingView) {

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIViewer#release()
	 */
	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIViewer#getContext()
	 */
	@Override
	public UIContext getContext() {
		return ctx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIViewer#open()
	 */
	@Override
	public void open() {
		frame.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIViewer#close()
	 */
	@Override
	public void close() {
		frame.setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIViewer#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		frame.setSize(width, height);
		frame.pack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIViewer#isCompatibleWith()
	 */
	@Override
	public List<Class<? extends UIView>> isCompatibleWith() {
		return null;
	}
}
