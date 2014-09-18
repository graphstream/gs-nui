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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Iterator;

import javax.swing.JPanel;

import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UIStyle;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.style.ElementStyle;
import org.graphstream.nui.views.UICamera;
import org.graphstream.nui.views.swing.renderer.BackgroundRenderer;

public class SwingGraphCanvas extends JPanel {
	private static final long serialVersionUID = 2570462198303271151L;

	protected UICamera camera;

	protected UIDataset dataset;
	protected UIStyle style;
	protected UIIndexer indexer;

	protected boolean antialiasing;
	protected boolean quality;

	protected SwingElementRenderer backgroundRenderer;
	protected SwingElementRenderer nodeRenderer;
	protected SwingElementRenderer edgeRenderer;
	protected SwingElementRenderer spriteRenderer;

	public SwingGraphCanvas(UICamera camera, UIIndexer indexer,
			UIDataset dataset, UIStyle style) {
		this.camera = camera;

		this.indexer = indexer;
		this.dataset = dataset;
		this.style = style;

		backgroundRenderer = getDefaultBackgroundRenderer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		setupGraphics(g2d);

		renderBackground(g2d, style.getGraphStyle());
		renderElements(g2d);
	}

	protected SwingElementRenderer getDefaultBackgroundRenderer() {
		return new BackgroundRenderer();
	}

	protected void setupGraphics(Graphics2D g) {
		if (antialiasing) {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_DEFAULT);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		if (quality) {
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		} else {
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		}
	}

	protected void renderBackground(Graphics2D g, ElementStyle graphStyle) {
		backgroundRenderer.render(g, camera, dataset, graphStyle);
	}

	protected void renderElements(Graphics2D g) {
		Iterator<ElementIndex> rOrder = style.getRenderingOrder();

		while (rOrder.hasNext()) {
			ElementIndex eIndex = rOrder.next();
			SwingElementRenderer renderer = null;

			switch (eIndex.getType()) {
			case NODE:
				renderer = nodeRenderer;
				break;
			case EDGE:
				renderer = edgeRenderer;
				break;
			case SPRITE:
				renderer = spriteRenderer;
				break;
			default:
				// Nothing to do !
			}

			if (renderer != null) {
				ElementStyle eStyle = style.getElementStyle(eIndex);
				renderer.render(g, camera, dataset, eStyle);
			}
		}
	}
}
