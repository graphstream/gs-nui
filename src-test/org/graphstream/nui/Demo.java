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

import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.nui.UIContext.ThreadingModel;
import org.graphstream.nui.context.DefaultContext;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.IndexerListener;
import org.graphstream.nui.indexer.ElementIndex.Type;
import org.graphstream.nui.layout.force.springbox.LinLogLayout;
import org.graphstream.nui.swapper.UIBufferReference;
import org.graphstream.nui.swing.SwingContext;

@SuppressWarnings("unused")
public class Demo {

	public static void main(String[] args) {
		final Logger log = Logger.getAnonymousLogger();

		final DefaultGraph g = new DefaultGraph("g");
		final UIContext ctx = new DefaultContext();// UIFactory.getDefaultFactory().createContext();

		ctx.init(ThreadingModel.SOURCE_IN_ANOTHER_THREAD);

		try {
			ctx.invokeOnUIThread(new Runnable() {
				public void run() {
					try {
						ctx.loadModule(UISwapper.MODULE_ID);
						ctx.loadModule(UIDataset.MODULE_ID);
						ctx.loadModule(UISpace.MODULE_ID);
						ctx.loadModule(UIStyle.MODULE_ID);
						ctx.loadModule(UILayout.MODULE_ID);
					} catch (InstantiationException | ModuleNotFoundException e) {
						log.log(Level.SEVERE, "Can not load modules", e);
					}

				}
			});
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ctx.connect(g);

		// g.addAttribute(
		// "ui.stylesheet",
		// "graph { fill-color: red; } node { fill-mode: dyn-plain; fill-color: green, blue; z-index:1; } node#W { fill-color: blue; z-index:10; }");

		BarabasiAlbertGenerator gen = new BarabasiAlbertGenerator();
		gen.addSink(g);

		gen.begin();
		for (int i = 0; i < 100; i++)
			gen.nextEvents();
		gen.end();
		gen.removeSink(g);

		/*
		 * g.addNode("A"); g.addNode("B"); g.addNode("C"); g.addNode("D");
		 * g.addNode("E"); g.addEdge("AB", "A", "B"); g.addEdge("AC", "A", "C");
		 * g.addEdge("BC", "B", "C"); g.addEdge("AD", "A", "D"); g.addEdge("DE",
		 * "D", "E");
		 */

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ctx.disconnect(g);
		g.display(false);

		while (true) {
			try {
				ctx.invokeOnUIThread(new Runnable() {
					public void run() {
						getCoordinates(ctx, g);
					}
				});
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// ctx.release();

	}

	static void getCoordinates(UIContext ctx, Graph g) {
		UIIndexer indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		UIDataset dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);
		double[] xyz = new double[3];

		for (int idx = 0; idx < indexer.getNodeCount(); idx++) {
			ElementIndex nodeIndex = indexer.getNodeIndex(idx);
			dataset.getNodeXYZ(nodeIndex, xyz);
			g.getNode(nodeIndex.id()).addAttribute("xyz", xyz[0], xyz[1],
					xyz[2]);
			// System.err.printf("%s @ %s%n", nodeIndex, Arrays.toString(xyz));
		}
	}

	static void mainUIThread(UIContext ctx) {
		Logger log = Logger.getAnonymousLogger();

		UIIndexer indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);

		UISwapper buffers = (UISwapper) ctx.getModule(UISwapper.MODULE_ID);
		UIBufferReference ref = buffers.createBuffer(Type.NODE, 3,
				Double.SIZE / 8, true, null, null);

		UIDataset dataset = (UIDataset) ctx.getModule(UIDataset.MODULE_ID);

		print(ref.buffer().asDoubleBuffer());

		// y.setAttribute("xyz", 10, 15, 20);

		UIStyle style = (UIStyle) ctx.getModule(UIStyle.MODULE_ID);
		// log.info("graph color is " + style.getGraphStyle().getFillColor());
		// log.info("node color is "
		// + style.getElementStyle(indexer.getNodeIndex(0)).getFillColor());
		// log.info("node#W color is "
		// + style.getElementStyle(indexer.getNodeIndex("W"))
		// .getFillColor());

		ElementIndex w = indexer.getNodeIndex("W");
		ref.setDouble(w, 0, 23.0);
		ref.setDouble(w, 1, 32.0);

		print(ref.buffer().asDoubleBuffer());

		Iterator<ElementIndex> it = style.getRenderingOrder();

		while (it.hasNext()) {
			ElementIndex idx = it.next();
			System.err.printf("%s : 0x%X%n", idx, style.getElementStyle(idx)
					.getColor());
		}

		System.err.printf("weight of %s is %f%n", w,
				dataset.getElementWeight(w));
	}

	static void print(DoubleBuffer buffer) {
		buffer.rewind();
		System.err.printf("%d elements {", buffer.remaining());

		while (buffer.hasRemaining())
			System.err.printf("%f%s", buffer.get(),
					buffer.hasRemaining() ? "; " : "");

		System.err.printf("}%n");
	}
}
