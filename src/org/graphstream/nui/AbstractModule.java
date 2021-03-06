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
package org.graphstream.nui;

import java.util.logging.Logger;

/**
 * This is the base class for ui modules. It manages the id and the deps of this
 * module.
 *
 */
public abstract class AbstractModule implements UIModule {

	private final String id;
	private final Iterable<String> deps;

	protected UIContext ctx;

	protected AbstractModule(String id, String... deps) {
		this.id = id;
		this.deps = UIModules.createDeps(deps);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIModule#getModuleID()
	 */
	@Override
	public String getModuleID() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIModule#getModuleDeps()
	 */
	@Override
	public Iterable<String> getModuleDeps() {
		return deps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIModule#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		this.ctx = ctx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIModule#release()
	 */
	@Override
	public void release() {
		this.ctx = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIModule#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		//
		// Classes have to override this method.
		//

		Logger.getLogger(getClass().getName()).info(
				"set attribute \"" + key + "\" to " + value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIModule#getContext()
	 */
	@Override
	public UIContext getContext() {
		return ctx;
	}
}
