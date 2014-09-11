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
package org.graphstream.nui.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIModule;
import org.graphstream.nui.UIModules;
import org.graphstream.nui.UIView;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.PipeBase;
import org.graphstream.stream.Replayable;
import org.graphstream.stream.Replayable.Controller;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * A base class for contexts that manage the main features that a context should
 * implements.
 */
public abstract class AbstractContext implements UIContext {
	/**
	 * The UI thread used to initialize this context.
	 */
	protected Thread thread;
	/**
	 * The threading model defined by a call to
	 * {@link #init(org.graphstream.nui.UIContext.ThreadingModel)}.
	 */
	protected ThreadingModel threadingModel;
	/**
	 * This map contains all the modules loaded inside this context. They are
	 * map using their id.
	 */
	protected Map<String, UIModule> modules;
	/**
	 * This map containss all the views added to this context. They are map
	 * using their id.
	 */
	protected Map<String, UIView> views;
	/**
	 * The proxy used to dispatch events from the sources to the ui components.
	 */
	protected Pipe proxy;

	protected Map<String, String> registeredAttributes;

	//
	// Lock used to manage the waitForInitialization method.
	//
	private ReentrantLock initLock;
	//
	// Condition used to manage the waitForInitialization method.
	//
	private Condition initCondition;
	//
	// Flag to tell if this context is initialized.
	//
	private AtomicBoolean isInitialized;

	/**
	 * 
	 */
	protected AbstractContext() {
		modules = new HashMap<String, UIModule>();
		views = new HashMap<String, UIView>();
		initLock = new ReentrantLock();
		initCondition = initLock.newCondition();
		isInitialized = new AtomicBoolean(false);
		registeredAttributes = new HashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#init(org.graphstream.stream.Source,
	 * org.graphstream.nui.UIContext.ThreadingModel, java.lang.Thread)
	 */
	@Override
	public void init(ThreadingModel threadingModel) {
		initLock.lock();

		try {
			if (isInitialized.getAndSet(true)) {
				Logger log = Logger.getLogger(getClass().getName());
				log.warning("ui context already initialized");

				initLock.unlock();
				return;
			}

			this.thread = Thread.currentThread();
			this.threadingModel = threadingModel;

			switch (threadingModel) {
			case SOURCE_IN_UI_THREAD:
				proxy = new PipeBase();
				break;
			default:
				proxy = new ThreadProxyPipe();
				break;
			}

			proxy.addAttributeSink(new AttributeDispatcher());

			initCondition.signalAll();
		} finally {
			initLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#waitForInitialization(long)
	 */
	@Override
	public boolean waitForInitialization(long timeout)
			throws InterruptedException {
		if (!isInitialized.get()) {
			if (timeout <= 0)
				initCondition.await();
			else
				initCondition.await(timeout, TimeUnit.MILLISECONDS);
		}

		return isInitialized.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#connect(org.graphstream.stream.Source)
	 */
	@Override
	public void connect(Source source) {
		source.addSink(proxy);

		if (source instanceof Replayable) {
			Replayable r = (Replayable) source;
			Controller c = r.getReplayController();

			c.addSink(proxy);
			c.replay();
			c.removeSink(proxy);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIContext#disconnect(org.graphstream.stream.Source)
	 */
	@Override
	public void disconnect(Source source) {
		source.removeSink(proxy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#close()
	 */
	@Override
	public void close() {
		for (UIView view : views.values())
			view.close();

		views.clear();

		for (UIModule module : modules.values())
			module.release();

		modules.clear();

		proxy.clearSinks();
		proxy = null;

		thread = null;
		threadingModel = null;
		isInitialized.set(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#getContextThread()
	 */
	@Override
	public Thread getContextThread() {
		return thread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#getContextProxy()
	 */
	@Override
	public Pipe getContextProxy() {
		return proxy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#getThreadingModel()
	 */
	@Override
	public ThreadingModel getThreadingModel() {
		return threadingModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#getModules()
	 */
	@Override
	public Iterable<? extends UIModule> getModules() {
		return Collections.unmodifiableCollection(modules.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#getViews()
	 */
	@Override
	public Iterable<? extends UIView> getViews() {
		return Collections.unmodifiableCollection(views.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#hasModule(java.lang.String)
	 */
	@Override
	public boolean hasModule(String moduleId) {
		return modules.containsKey(moduleId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#getModule(java.lang.String)
	 */
	@Override
	public UIModule getModule(String moduleId) {
		return modules.get(moduleId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#loadModule(java.lang.String)
	 */
	@Override
	public void loadModule(String moduleId) {
		checkThread();
		UIModules.loadModule(this, moduleId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIContext#loadModules(org.graphstream.nui.UIModule[])
	 */
	@Override
	public void insertModules(UIModule... modules) {
		if (modules == null)
			return;

		checkThread();

		for (int i = 0; i < modules.length; i++)
			this.modules.put(modules[i].getModuleID(), modules[i]);

		for (int i = 0; i < modules.length; i++)
			modules[i].init(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#unloadModule(java.lang.String)
	 */
	@Override
	public void unloadModule(String moduleId) {
		checkThread();

		UIModule module = modules.remove(moduleId);
		module.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#addView(org.graphstream.nui.UIView)
	 */
	@Override
	public void addView(UIView view) {
		checkThread();

		if (views.containsKey(view.getViewId())) {
			Logger.getLogger(AbstractContext.class.getName()).warning(
					"view already exists");
		} else {
			views.put(view.getViewId(), view);
			view.init(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#removeView(java.lang.String)
	 */
	@Override
	public void removeView(String viewId) {
		checkThread();

		UIView view = views.remove(viewId);

		if (view != null)
			view.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#registerUIAttribute(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void registerUIAttribute(String key, String moduleId) {
		registeredAttributes.put(key, moduleId);
	}

	protected void checkThread() {
		if (Thread.currentThread() != thread) {
			Logger.getLogger(AbstractContext.class.getName())
					.warning(
							"Manipulating modules or views outside of the context thread may cause concurrent exceptions. Don't blame the devs.");
		}
	}

	class AttributeDispatcher extends SinkAdapter {
		private boolean isUIAttribute(String attributeId) {
			return attributeId.length() > 3 && attributeId.charAt(0) == 'u'
					&& attributeId.charAt(1) == 'i'
					&& attributeId.charAt(2) == '.';
		}

		private void handleAttribute(String attributeId, Object value) {
			if (isUIAttribute(attributeId)) {
				attributeId = attributeId.substring(3);

				int i = attributeId.indexOf('.');

				if (i > 0) {
					String moduleId = attributeId.substring(0, i);
					attributeId = attributeId.substring(i + 1);

					if (moduleId.equals("views")) {

					} else {
						if (hasModule(moduleId))
							getModule(moduleId)
									.setAttribute(attributeId, value);
						else
							Logger.getLogger(AbstractContext.class.getName())
									.warning("no module \"" + moduleId + "\"");
					}
				} else if (registeredAttributes.containsKey(attributeId)) {
					String moduleId = registeredAttributes.get(attributeId);
					UIModule module = getModule(moduleId);

					if (module != null)
						module.setAttribute(attributeId, value);
				} else {
					String moduleId = attributeId;

					if (!hasModule(moduleId))
						loadModule(moduleId);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#graphAttributeAdded(java.lang.
		 * String, long, java.lang.String, java.lang.Object)
		 */
		@Override
		public void graphAttributeAdded(String sourceId, long timeId,
				String attributeId, Object value) {
			handleAttribute(attributeId, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#graphAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void graphAttributeChanged(String sourceId, long timeId,
				String attributeId, Object oldValue, Object newValue) {
			handleAttribute(attributeId, newValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#graphAttributeRemoved(java.lang
		 * .String, long, java.lang.String)
		 */
		@Override
		public void graphAttributeRemoved(String sourceId, long timeId,
				String attributeId) {
			handleAttribute(attributeId, null);
		}
	}
}