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
package org.graphstream.nui;

import java.util.logging.Logger;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.nui.UIContext.ThreadingModel;
import org.graphstream.nui.indexer.UIElementIndex;
import org.graphstream.nui.indexer.IndexerListener;

public class Demo {

	public static void main(String[] args) {
		Logger log = Logger.getAnonymousLogger();

		DefaultGraph g = new DefaultGraph("g");
		UIContext ctx = UIFactory.getDefaultFactory().createContext();

		ctx.init(ThreadingModel.SOURCE_IN_UI_THREAD);
		ctx.loadModule(UIDataset.MODULE_ID);
		ctx.loadModule(UISpace.MODULE_ID);
		ctx.loadModule(UIStyle.MODULE_ID);

		UIIndexer indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		indexer.addIndexerListener(new IListener());

		ctx.connect(g);

		g.addAttribute(
				"ui.stylesheet",
				"graph { fill-color: red; } node { fill-color: green; } node#W { fill-color: blue; }");

		g.addNode("Z");
		g.addNode("W");
		Node y = g.addNode("Y");

		y.setAttribute("xyz", 10, 15, 20);

		UIStyle style = (UIStyle) ctx.getModule(UIStyle.MODULE_ID);
		log.info("graph color is " + style.getGraphStyle().getFillColor());
		log.info("node color is "
				+ style.getElementStyle(indexer.getNodeIndex(0)).getFillColor());
		log.info("node#W color is "
				+ style.getElementStyle(indexer.getNodeIndex("W"))
						.getFillColor());
	}

	static class IListener implements IndexerListener {
		Logger log = Logger.getAnonymousLogger();

		@Override
		public void nodeAdded(UIElementIndex nodeIndex) {
			log.info("node \"" + nodeIndex.id() + "\" added @ "
					+ nodeIndex.index());
		}

		@Override
		public void nodeRemoved(UIElementIndex nodeIndex) {
			log.info("node \"" + nodeIndex.id() + "\" removed @ "
					+ nodeIndex.index());
		}

		@Override
		public void nodesSwapped(UIElementIndex nodeIndex1,
				UIElementIndex nodeIndex2) {
			log.info("node \"" + nodeIndex1.id() + "\" @ " + nodeIndex1.index()
					+ " and \"" + nodeIndex2.id() + "\" @ "
					+ nodeIndex2.index() + " swapped @ ");
		}

		@Override
		public void edgeAdded(UIElementIndex edgeIndex, UIElementIndex sourceIndex,
				UIElementIndex targetIndex, boolean directed) {
			// TODO Auto-generated method stub

		}

		@Override
		public void edgeRemoved(UIElementIndex edgeIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void edgesSwapped(UIElementIndex edgeIndex1,
				UIElementIndex edgeIndex2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void elementsClear() {
			// TODO Auto-generated method stub

		}

	}
}
