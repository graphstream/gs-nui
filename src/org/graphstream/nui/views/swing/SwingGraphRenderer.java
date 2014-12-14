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
package org.graphstream.nui.views.swing;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UIStyle;
import org.graphstream.nui.views.BaseGraphRenderer;
import org.graphstream.nui.views.UIGraphRenderer;

public class SwingGraphRenderer extends BaseGraphRenderer implements
		UIGraphRenderer, SwingView {
	public static final String VIEW_ID = "swing-graph-renderer";
	private static int VIEW_COUNT = 0;

	private static String newViewId() {
		return String.format("%s-%d", VIEW_ID, VIEW_COUNT++);
	}

	protected UIDataset dataset;
	protected UIStyle style;
	protected UIIndexer indexer;

	protected SwingGraphCanvas canvas;

	public SwingGraphRenderer() {
		// TODO: set camera and controller
		super(newViewId(), null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIView#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		super.init(ctx);

		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);
		style = (UIStyle) ctx.getModule(UIStyle.MODULE_ID);
		canvas = new SwingGraphCanvas(camera, indexer, dataset, style);

		JFrame frame = new JFrame();
		frame.add(canvas);
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
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.views.swing.SwingView#getSwingPanel()
	 */
	@Override
	public JPanel getSwingPanel() {
		return canvas;
	}
}
