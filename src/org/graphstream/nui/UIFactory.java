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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.context.DefaultContext;

/**
 * 
 */
public abstract class UIFactory {
	/**
	 * 
	 */
	public static final String UI_FACTORY_PROPERTY = "org.graphstream.ui.factory";

	/**
	 * 
	 * @return
	 */
	public static UIFactory getDefaultFactory() {
		Logger log = Logger.getLogger(UIFactory.class.getName());

		try {
			String factoryClass = System.getProperty(UI_FACTORY_PROPERTY);

			if (factoryClass != null) {
				log.info("Found UIFactory class defined in system properties : "
						+ factoryClass);

				Class<?> c = Class.forName(factoryClass);
				UIFactory factory = (UIFactory) c.newInstance();

				return factory;
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "failed to load custom UIFactory", e);
		}

		return new DefaultUIFactory();
	}

	/**
	 * 
	 * @return
	 */
	public abstract UIContext createContext();

	static class DefaultUIFactory extends UIFactory {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.UIFactory#createContext()
		 */
		@Override
		public UIContext createContext() {
			return new DefaultContext();
		}
	}
}
