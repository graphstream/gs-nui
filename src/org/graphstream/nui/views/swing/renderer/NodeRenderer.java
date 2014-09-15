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
package org.graphstream.nui.views.swing.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.graphstream.nui.UIDataset;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.style.GroupStyle;
import org.graphstream.nui.views.UICamera;
import org.graphstream.nui.views.swing.SwingElementRenderer;

public class NodeRenderer implements SwingElementRenderer {
	protected GroupStyle setupStyle;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.views.swing.SwingElementRenderer#render(java.awt.
	 * Graphics2D, org.graphstream.nui.views.UICamera,
	 * org.graphstream.nui.UIDataset, org.graphstream.nui.style.ElementStyle,
	 * org.graphstream.nui.indexer.UIElementIndex)
	 */
	@Override
	public void render(Graphics2D g, UICamera camera, UIDataset dataset,
			GroupStyle elementStyle, ElementIndex index) {
		if (setupStyle != elementStyle)
			setup(g, elementStyle);

	}

	protected void setup(Graphics2D g, GroupStyle elementStyle) {
		setupStyle = elementStyle;
		
	}
}
