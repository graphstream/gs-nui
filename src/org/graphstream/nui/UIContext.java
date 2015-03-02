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

import org.graphstream.nui.context.TickTask;
import org.graphstream.nui.context.worker.WorkerTask;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.Source;

/**
 * A context is the main block in the creation of a new display interface. It
 * allows to load modules and to create views. This context is the link between
 * these elements and the graph who has to be displayed.
 * 
 * In the creation of a context, we consider two threads, A and B. The first
 * one, A, is the one who manage the source of the event, a graph for example.
 * The second, B, is the one where the UI will be managed and the views will be
 * drawn. If A and B are the same thread, the {@link ThreadingModel} of the
 * context is {@link ThreadingModel.SOURCE_IN_UI_THREAD}. But if the UI has its
 * own thread, and so A and B are two different threads, the threading model
 * will be {@link ThreadingModel.SOURCE_IN_ANOTHER_THREAD}. The context HAS TO
 * be initialized inside the ui thread (ie. inside the B thread). The thread
 * used for initialization can be obtained by the {@link #getContextThread()}
 * method. Once the initialization is done, one can use the
 * {@link #connect(Source)} method inside the thread A to add a new source of
 * events.
 * 
 * The link between the sources you connect and all the elements of the ui is
 * done through a proxy, who can be obtained with {@link #getContextProxy()}.
 * 
 * Most of the methods of a context have to be used inside the context thread to
 * avoid concurrent troubles. If you need to control a module or a view, you
 * have to use the attributes of the source. The context will dispatch graph
 * attributes starting with the <code>ui.</code> prefix. The pattern is
 * <code>ui.moduleID.attribute</code> to send attribute to a module, or
 * <code>ui.views.viewID.attribute</code> to send attribute to a view.
 */
public interface UIContext {
	/**
	 * The threading model defines the thread layout of the program. We
	 * distinguish the thread of the source from the thread of the UI.
	 */
	public static enum ThreadingModel {
		/**
		 * The source runs in the same thread than the UI. The context proxy can
		 * be a simple pipe that just transmits events to the ui components.
		 */
		SOURCE_IN_UI_THREAD,
		/**
		 * The source and the UI run in different threads. The context proxy has
		 * to manage the transmission of events between these two threads.
		 */
		SOURCE_IN_ANOTHER_THREAD
	}

	/**
	 * The first thing one has to do is to initialize this context. This HAS TO
	 * be done INSIDE the ui thread.
	 * 
	 * @param threadingModel
	 *            the threading model of this context
	 * @UIThreadUse
	 */
	void init(ThreadingModel threadingModel);

	/**
	 * Run a task on the ui thread and wait until this task is done.
	 * 
	 * @param r
	 * @throws InterruptedException
	 */
	void invokeOnUIThread(Runnable r) throws InterruptedException;

	void addTickTask(String id, TickTask task);

	void removeTickTask(String id);

	/**
	 * This context has to be connected to at least one source. This has to be
	 * done INSIDE the thread of the source, and after the initialization. One
	 * can use the {@link #waitForInitialization(long)} method to be sure that
	 * the initialization is done.
	 * 
	 * @param source
	 *            the new that will be connected to the context
	 */
	void connect(Source source);

	/**
	 * This can be used to disconnected a connected source to the context. This
	 * has to be done INSIDE the thread of the source, and after the
	 * initialization.
	 * 
	 * @param source
	 *            the source to disconnect from this context
	 */
	void disconnect(Source source);

	void sync();

	/**
	 * This context can be released when it is no more in use. All modules will
	 * be released and all views will be closed.
	 * 
	 */
	void release();

	/**
	 * Get the UI thread. This is the thread used to call the
	 * {@link #init(ThreadingModel)} method of this context.
	 * 
	 * @return the UI thread
	 */
	Thread getContextThread();

	/**
	 * The proxy of this context used to dispatch events from the outside (the
	 * sources) to the inside (modules and views). Modules and views can used
	 * this proxy to connect themselves to the stream of events.
	 * 
	 * @return the context proxy
	 */
	Pipe getContextProxy();

	/**
	 * Get the threading model of this context as defined by the call to
	 * {@link #init(ThreadingModel)}.
	 * 
	 * @return the threading model of this context
	 */
	ThreadingModel getThreadingModel();

	/**
	 * This method can be used to iterate on the modules loaded inside this
	 * context.
	 * 
	 * @return an iterable object on the context modules
	 * @UIThreadUse
	 */
	Iterable<? extends UIModule> getModules();

	/**
	 * This method can be used to iterate on the views opened inside this
	 * context.
	 * 
	 * @return an iterable object on the context views
	 * @UIThreadUse
	 */
	Iterable<? extends UIView> getViews();

	/**
	 * Check if a module is loaded inside this context.
	 * 
	 * @param moduleId
	 *            the id of the module
	 * @return true if the module is loaded
	 */
	boolean hasModule(String moduleId);

	/**
	 * Get a module. If no module with this id is loaded, the method will return
	 * null.
	 * 
	 * @param moduleId
	 *            id of the module
	 * @return the module, or null
	 */
	UIModule getModule(String moduleId);

	/**
	 * Load a module. All dependencies of the module will be loaded at the same
	 * time.
	 * 
	 * @param moduleId
	 *            id of the module
	 * @UIThreadUse
	 */
	void loadModule(String moduleId) throws InstantiationException,
			ModuleNotFoundException;

	boolean tryLoadModule(String moduleId);

	/**
	 * Insert modules in this context. Unlike the {@link #loadModule(String)}
	 * method, this method will not load dependencies of modules.
	 * 
	 * @param modules
	 *            modules to insert into this context
	 * @UIThreadUse
	 */
	void insertModules(UIModule... modules);

	/**
	 * Unload a module from this context.
	 * 
	 * @param moduleId
	 *            id of the module to unload
	 * @UIThreadUse
	 */
	void unloadModule(String moduleId);

	/**
	 * Add a new view.
	 * 
	 * @param view
	 *            the view you want to add into this context
	 * @UIThreadUse
	 */
	void addView(UIView view);

	/**
	 * Remove a view from this context.
	 * 
	 * @param viewId
	 *            id of the view to remove
	 * @UIThreadUse
	 */
	void removeView(String viewId);
}
