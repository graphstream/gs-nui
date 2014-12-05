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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.nui.attributes.DefaultAttributes;
import org.graphstream.nui.dataset.DefaultDataset;
import org.graphstream.nui.indexer.DefaultIndexer;
import org.graphstream.nui.space.DefaultSpace;
import org.graphstream.nui.spacePartition.DefaultSpacePartition;
import org.graphstream.nui.style.base.BaseStyle;
import org.graphstream.nui.swapper.DefaultSwapper;

public class UIModules {
	private static final Logger LOGGER = Logger.getLogger(UIModules.class
			.getName());

	private static final Map<String, Class<? extends UIModule>> MODULES_CLASSES;

	/**
	 * 
	 * @param moduleClass
	 * @throws RegisterException
	 */
	public static void registerModule(Class<? extends UIModule> moduleClass)
			throws RegisterException {
		try {
			String moduleId = null;

			try {
				Field f = moduleClass.getField("MODULE_ID");

				if (f.getType() != String.class)
					throw new NoSuchFieldException();

				moduleId = (String) f.get(null);
			} catch (NoSuchFieldException | SecurityException e) {
				LOGGER.warning("trying to register a module with no MODULE_ID field");

				UIModule module = moduleClass.newInstance();
				moduleId = module.getModuleID();
			}

			if (MODULES_CLASSES.containsKey(moduleId)) {
				LOGGER.warning(String.format(
						"module \"%s\" is already registered with class %s",
						moduleId, MODULES_CLASSES.get(moduleId).getName()));
				LOGGER.warning(String.format(
						"overriding module \"%s\" with class %s", moduleId,
						moduleClass.getName()));
			}

			MODULES_CLASSES.put(moduleId, moduleClass);

			LOGGER.info(String.format("module \"%s\" succesfully registered",
					moduleId));
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RegisterException(moduleClass.getName(), e);
		}
	}

	static {
		MODULES_CLASSES = new ConcurrentHashMap<String, Class<? extends UIModule>>();

		/*
		 * Here, we register known modules.
		 */
		try {
			registerModule(DefaultIndexer.class);
			registerModule(DefaultSwapper.class);
			registerModule(DefaultAttributes.class);
			registerModule(DefaultDataset.class);
			registerModule(DefaultSpace.class);
			registerModule(BaseStyle.class);
			registerModule(DefaultSpacePartition.class);
		} catch (RegisterException e) {
			/*
			 * This should not happens because lovely devs check and test their
			 * modules implementations...
			 */
			LOGGER.log(Level.SEVERE, "while registering modules", e);
		}
	}

	public static Iterable<String> createDeps(String... deps) {
		return Collections.unmodifiableList(Arrays
				.asList(deps == null ? new String[] {} : deps));
	}

	public static UIModule getModuleInstance(String moduleId)
			throws ModuleNotFoundException, InstantiationException {
		Class<? extends UIModule> clazz = MODULES_CLASSES.get(moduleId);

		if (clazz == null)
			throw new ModuleNotFoundException(moduleId);

		UIModule module = null;

		try {
			module = clazz.newInstance();
		} catch (IllegalAccessException e) {
			throw new InstantiationException(e.getMessage());
		}

		return module;
	}

	public static void loadModule(UIContext ctx, String moduleId)
			throws InstantiationException, ModuleNotFoundException {
		UIModule module = getModuleInstance(moduleId);

		Graph gDeps = makeDepsGraph(ctx, module);

		UIModule[] modules = new UIModule[gDeps.getNodeCount()];
		int i = 0;

		while (gDeps.getNodeCount() > 0) {
			Node next = gDeps.getNode(0);

			for (int j = 1; j < gDeps.getNodeCount(); j++) {
				if (gDeps.getNode(j).getInDegree() < next.getInDegree())
					next = gDeps.getNode(j);
			}

			modules[i++] = next.getAttribute("module");
			gDeps.removeNode(next);
		}

		ctx.insertModules(modules);
	}

	private static Graph makeDepsGraph(UIContext ctx, UIModule module)
			throws InstantiationException, ModuleNotFoundException {
		Graph gDeps = new DefaultGraph("deps");

		{
			Node n = gDeps.addNode(module.getModuleID());
			n.addAttribute("ui.label", module.getModuleID());
			n.addAttribute("module", module);
			n.addAttribute("priority", getModulePriority(module.getModuleID()));
		}

		LinkedList<UIModule> check = new LinkedList<UIModule>();
		check.add(module);

		while (check.size() > 0) {
			UIModule m = check.poll();

			for (String dep : m.getModuleDeps()) {
				if (!ctx.hasModule(dep)) {
					if (gDeps.getNode(dep) == null) {
						UIModule d = getModuleInstance(dep);

						Node n = gDeps.addNode(dep);
						n.addAttribute("ui.label", dep);
						n.addAttribute("module", d);
						n.addAttribute("priority", getModulePriority(dep));

						check.add(d);
					}

					String did = String.format("%s>%s", dep, m.getModuleID());

					if (gDeps.getEdge(did) == null)
						gDeps.addEdge(did, dep, m.getModuleID(), true);
				}
			}
		}

		if (gDeps.getNodeCount() < 2)
			return gDeps;

		//
		// Check if the graph is fully connected and connect it if needed.
		//

		Iterator<Node> it = gDeps.getNode(0).getBreadthFirstIterator(false);
		HashSet<Node> connected = new HashSet<Node>();

		while (it.hasNext())
			connected.add(it.next());

		while (connected.size() < gDeps.getNodeCount()) {
			int start = 0;
			int connectTo = connected.iterator().next().getIndex();

			while (connected.contains(gDeps.getNode(start)))
				start++;

			it = gDeps.getNode(start).getBreadthFirstIterator(false);
			connected.add(gDeps.getNode(start));

			while (it.hasNext())
				connected.add(it.next());

			gDeps.addEdge(String.format("%d>%d", connectTo, start), connectTo,
					start, true);
		}

		//
		// Find loop
		//

		for (int idx = 0; idx < gDeps.getNodeCount(); idx++)
			gDeps.getNode(idx).addAttribute("region", idx);

		LinkedList<Edge> edges = new LinkedList<Edge>();

		for (int idx = 0; idx < gDeps.getEdgeCount(); idx++)
			edges.add(gDeps.getEdge(idx));

		Collections.sort(edges, new Comparator<Edge>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object,
			 * java.lang.Object)
			 */
			@Override
			public int compare(Edge o1, Edge o2) {
				double p1 = o1.getSourceNode().getNumber("priority");
				double p2 = o2.getSourceNode().getNumber("priority");

				if (p1 == p2) {
					p2 = o1.getTargetNode().getInDegree();
					p1 = o2.getTargetNode().getInDegree();
				}

				return Double.compare(p2, p1);
			}
		});

		while (edges.size() > 0) {
			Edge e = edges.poll();

			int s1 = e.getSourceNode().getAttribute("region");
			int s2 = e.getTargetNode().getAttribute("region");

			if (s1 != s2) {
				if (s1 < s2) {
					s1 += s2;
					s2 = s1 - s2;
					s1 = s1 - s2;
				}

				for (int nIdx = 0; nIdx < gDeps.getNodeCount(); nIdx++) {
					int t = gDeps.getNode(nIdx).getAttribute("region");

					if (t == s1)
						gDeps.getNode(nIdx).setAttribute("region", s2);
				}
			} else {
				gDeps.removeEdge(e);
			}
		}

		return gDeps;
	}

	public static int getModulePriority(String moduleId)
			throws ModuleNotFoundException {
		Class<? extends UIModule> clazz = MODULES_CLASSES.get(moduleId);

		if (clazz == null)
			throw new ModuleNotFoundException(moduleId);

		try {
			Field f = clazz.getField("MODULE_PRIORITY");

			if (f.getType() != Integer.TYPE)
				throw new NoSuchFieldException();

			return (int) f.get(null);
		} catch (NoSuchFieldException e) {
		} catch (SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			LOGGER.log(Level.WARNING, "Cannot get module priority", e);
		}

		return UIModule.DEFAULT_PRIORITY;
	}
}
