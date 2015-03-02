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
package org.graphstream.nui.spacePartition.ntree.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.nui.ModuleNotFoundException;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIFactory;
import org.graphstream.nui.UISpacePartition;
import org.graphstream.nui.UIContext.ThreadingModel;
import org.graphstream.nui.spacePartition.SpaceCell;

public class DefaultSpacePartitionDemo {
	public static void main(String[] args) {
		final Logger log = Logger.getAnonymousLogger();

		DefaultGraph g = new DefaultGraph("g");
		final UIContext ctx = UIFactory.getDefaultFactory().createContext();

		ctx.init(ThreadingModel.SOURCE_IN_UI_THREAD);

		try {
			ctx.invokeOnUIThread(new Runnable() {
				public void run() {
					try {
						ctx.loadModule(UISpacePartition.MODULE_ID);
					} catch (InstantiationException | ModuleNotFoundException e) {
						log.log(Level.SEVERE, "Can not load modules", e);
					}

				}
			});
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ctx.connect(g);

		g.addAttribute("ui.spacePartition.maxElementsPerCell", 1);

		g.addNode("Z").addAttribute("xyz", new double[] { 0.5, 0.5, 0 });
		g.addNode("Y").addAttribute("xyz", new double[] { -0.5, 0.5, 0 });
		g.addNode("W").addAttribute("xyz", new double[] { 0.5, -0.5, 0 });
		g.addEdge("ZW", "Z", "W");

		UISpacePartition spacePartition = (UISpacePartition) ctx
				.getModule(UISpacePartition.MODULE_ID);

		for (SpaceCell sc : spacePartition) {
			System.out.println(sc);
		}
	}
}
