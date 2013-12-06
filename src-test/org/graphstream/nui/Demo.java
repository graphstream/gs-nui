/*
 * Copyright 2006 - 2014
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
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

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.nui.Viewer.ThreadingModel;
import org.graphstream.nui.data.NodeData;

public class Demo {
	Graph g = new AdjacencyListGraph("g");
	Viewer v = new Viewer();

	public Demo() {

		v.register(g, ThreadingModel.SOURCE_IN_ANOTHER_THREAD);
	}

	public void run() {
		g.addNode("A");
		g.addNode("B");
		g.addNode("C");

		g.addEdge("AB", "A", "B");
		g.addEdge("AC", "A", "C");
		g.addEdge("BC", "B", "C");

		g.getNode(0).addAttribute("xyz", new double[] { 1, 0, 0 });
		g.getNode(1).addAttribute("xyz", new double[] { -0.5, 0.5, 0 });
		g.getNode(2).addAttribute("xyz", new double[] { -0.5, -0.5, 0 });

		output();

		g.removeNode("A");
		g.getNode(1).addAttribute("ui.class", "classA classB");
		g.getNode(1).addAttribute("ui.class", "+classC");
		g.getNode(1).addAttribute("ui.class", "-classA");

		output();
	}

	public void output() {
		UIDataset dataset = v.dataset;
		for (int idx = 0; idx < dataset.getNodeCount(); idx++) {
			NodeData data = dataset.getNodeData(idx);

			System.out.printf("Node#%d \"%s\"\n", idx,
					dataset.getNodeData(idx).id);

			System.out.printf("   xyz: %.2f;%.2f;%.2f\n",
					dataset.getNodeX(idx), dataset.getNodeY(idx),
					dataset.getNodeZ(idx));
			System.out.printf("  argb: 0x%X\n", dataset.getNodeARGB(idx));
			
			System.out.printf(" class:");
			for (int c = 0; c < data.getUIClassCount(); c++)
				System.out.printf(" \"%s\"", data.getUIClass(c));
			System.out.printf("\n");
		}
	}

	public static void main(String[] args) {
		Demo d = new Demo();
		d.run();
	}

}
