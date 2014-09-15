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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.graphstream.nui.attributes.DefaultAttributes;
import org.graphstream.nui.buffers.DefaultBuffers;
import org.graphstream.nui.dataset.DefaultDataset;
import org.graphstream.nui.indexer.DefaultIndexer;
import org.graphstream.nui.space.DefaultSpace;
import org.graphstream.nui.style.base.BaseStyle;

public class UIModules {
	private static final Map<String, Class<? extends UIModule>> MODULES_CLASSES;

	/**
	 * 
	 * @param moduleClass
	 * @throws RegisterException
	 */
	public static void registerModule(Class<? extends UIModule> moduleClass)
			throws RegisterException {
		Logger log = Logger.getLogger(UIModules.class.getName());

		try {
			String moduleId = null;

			try {
				Field f = moduleClass.getField("MODULE_ID");

				if (f.getType() != String.class)
					throw new NoSuchFieldException();

				moduleId = (String) f.get(null);
			} catch (NoSuchFieldException | SecurityException e) {
				log.warning("trying to register a module with no MODULE_ID field");

				UIModule module = moduleClass.newInstance();
				moduleId = module.getModuleID();
			}

			if (MODULES_CLASSES.containsKey(moduleId)) {
				log.warning(String.format(
						"module %s is already registered with class %s",
						moduleId, MODULES_CLASSES.get(moduleId).getName()));
				log.warning(String.format("overriding module %s with class %s",
						moduleId, moduleClass.getName()));
			}

			MODULES_CLASSES.put(moduleId, moduleClass);

			log.info(String
					.format("module %s succesfully registered", moduleId));
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
			registerModule(DefaultBuffers.class);
			registerModule(DefaultAttributes.class);
			registerModule(DefaultDataset.class);
			registerModule(DefaultSpace.class);
			registerModule(BaseStyle.class);
		} catch (RegisterException e) {
			/*
			 * This should not happens because lovely devs check and test their
			 * modules implementations...
			 */
			e.printStackTrace();
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

		HashMap<String, UIModule> deps = new HashMap<String, UIModule>();
		deps.put(moduleId, module);

		LinkedList<UIModule> check = new LinkedList<UIModule>();
		check.add(module);

		while (check.size() > 0) {
			UIModule m = check.poll();

			for (String dep : m.getModuleDeps()) {
				if (!ctx.hasModule(dep) && !deps.containsKey(dep)) {
					UIModule d = getModuleInstance(dep);
					deps.put(dep, d);
					check.add(d);
				}
			}
		}

		ctx.insertModules(deps.values().toArray(new UIModule[deps.size()]));
	}
}
