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
package org.graphstream.nui.layout;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.layout.force.springbox.LinLogLayout;
import org.graphstream.nui.layout.force.springbox.SpringBoxLayout;

public class LayoutAlgorithms {
	private static final Logger LOGGER = Logger
			.getLogger(LayoutAlgorithms.class.getName());

	private static final Map<String, Class<? extends LayoutAlgorithm>> ALGORITHMS = new HashMap<String, Class<? extends LayoutAlgorithm>>();

	public static final String DEFAULT_LAYOUT = SpringBoxLayout.LAYOUT_NAME;

	public static void register(Class<? extends LayoutAlgorithm> algorithmClass) {
		String name = getLayoutName(algorithmClass);

		if (name != null)
			ALGORITHMS.put(name, algorithmClass);
	}

	public static String getLayoutName(Class<? extends LayoutAlgorithm> cls) {
		String name = null;

		try {
			Field f = cls.getField("LAYOUT_NAME");
			name = (String) f.get(null);
		} catch (NoSuchFieldException | SecurityException e) {
			LOGGER.log(Level.SEVERE,
					"layout algorithm does not declare a public LAYOUT_NAME field");
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, "fail to get the LAYOUT_NAME field value",
					e);
		}

		return name;
	}

	public static LayoutAlgorithm getInstance(String name)
			throws NoSuchLayoutAlgorithmException {
		Class<? extends LayoutAlgorithm> cls = ALGORITHMS.get(name);

		if (cls == null)
			throw new NoSuchLayoutAlgorithmException(name);

		try {
			LayoutAlgorithm algo = cls.newInstance();
			return algo;
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE,
					"fail to create new instance of layout algorithm " + name,
					e);
		}

		return null;
	}

	public static LayoutAlgorithm getDefaultLayoutAlgorithm() {
		try {
			return getInstance(DEFAULT_LAYOUT);
		} catch (NoSuchLayoutAlgorithmException e) {
			LOGGER.severe("default layout algorithm is not registered");
		}

		return null;
	}

	static {
		register(LinLogLayout.class);
		register(SpringBoxLayout.class);
	}
}
