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
package org.graphstream.nui.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.ModuleNotFoundException;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIModule;
import org.graphstream.nui.UIModules;
import org.graphstream.nui.UIView;
import org.graphstream.stream.Pipe;
import org.graphstream.stream.PipeBase;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Replayable;
import org.graphstream.stream.Replayable.Controller;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * A base class for contexts that manage the main features that a context should
 * implements.
 */
public abstract class AbstractContext implements UIContext {
	private static final Logger LOGGER = Logger.getLogger(AbstractContext.class
			.getName());

	public static final long DEFAULT_TICK_LENGTH = 1000 / 30;

	public static final TimeUnit DEFAULT_TICK_LENGTH_UNIT = TimeUnit.MILLISECONDS;

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

	protected Map<String, TickTaskWrapper> tasks;

	protected DelayQueue<TickTaskWrapper> tasksQueue;

	protected long tickLength = DEFAULT_TICK_LENGTH;

	protected TimeUnit tickLengthUnits = DEFAULT_TICK_LENGTH_UNIT;

	protected Map<String, UIWorker> workers;

	//
	// Lock used to manage the waitForInitialization method.
	//
	protected ReentrantLock invocationLock;
	//
	// Flag to tell if this context is initialized.
	//
	private AtomicBoolean isInitialized;

	private Runnable syncAction = new Runnable() {
		public void run() {
			sync();
		}
	};

	/**
	 * 
	 */
	protected AbstractContext() {
		modules = new HashMap<String, UIModule>();
		views = new HashMap<String, UIView>();
		invocationLock = new ReentrantLock();
		isInitialized = new AtomicBoolean(false);
		workers = new HashMap<String, UIWorker>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#init(org.graphstream.stream.Source,
	 * org.graphstream.nui.UIContext.ThreadingModel, java.lang.Thread)
	 */
	@Override
	public void init(ThreadingModel threadingModel) {
		if (isInitialized.getAndSet(true)) {
			LOGGER.warning("ui context already initialized");
			return;
		}

		this.threadingModel = threadingModel;

		switch (AbstractContext.this.threadingModel) {
		case SOURCE_IN_UI_THREAD:
			proxy = new PipeBase();
			break;
		default:
			proxy = new ThreadProxyPipe();
			break;
		}

		tickLength = DEFAULT_TICK_LENGTH;
		tickLengthUnits = DEFAULT_TICK_LENGTH_UNIT;
		tasks = new HashMap<String, TickTaskWrapper>();
		tasksQueue = new DelayQueue<TickTaskWrapper>();

		try {
			invokeOnUIThread(new Runnable() {
				public void run() {
					AbstractContext.this.thread = Thread.currentThread();
					internalInit();
				}
			});

			LOGGER.info(getContextThread().getName());
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE,
					"ui-context initialization does not end correctly", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#invokeOnUIThread(java.lang.Runnable)
	 */
	@Override
	public abstract void invokeOnUIThread(Runnable r)
			throws InterruptedException;

	/**
	 * An initialization action called inside the ui-thread during the
	 * initialization of the context. Things that have to be done inside the
	 * ui-thread to initialize this context should be defined in this method.
	 */
	protected abstract void internalInit();

	/**
	 * An release action called inside the ui-thread during the release process
	 * of the context. Things that have to be done inside the ui-thread to
	 * release this context should be defined in this method.
	 */
	protected abstract void internalRelease();

	protected void tick() {
		assert Thread.currentThread() == thread;

		//
		// The TICK process
		// ----------------------------
		//
		// 1. Synchronization and dispatching of events
		// 2. Run the "pre-process" phase of workers
		// 3. Let the workers run and the rendering be done
		// 4. Run the "post-process" phase of workers
		//

		sync();

		for (UIWorker w : workers.values())
			w.getWorkerTask().preProcess();

		TickTaskWrapper ttw;

		while ((ttw = tasksQueue.poll()) != null) {
			ttw.resetNextExecutionDate();
			ttw.run();

			if (ttw.task.isPeriodic())
				tasksQueue.add(ttw);
		}

		for (UIView view : views.values())
			view.update();

		//
		// Now wait for all workers to finish their current process.
		// And then publish their data.
		//

		
		
		for (UIWorker w : workers.values())
			w.getWorkerTask().postProcess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#addTickTask(java.lang.String,
	 * org.graphstream.nui.context.TickTask)
	 */
	@Override
	public void addTickTask(String id, TickTask task) {
		TickTaskWrapper ttw = new TickTaskWrapper(task);

		tasks.put(id, ttw);
		tasksQueue.offer(ttw);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#removeTickTask(java.lang.String)
	 */
	@Override
	public void removeTickTask(String id) {
		TickTaskWrapper ttw = tasks.remove(id);

		if (id == null) {
			LOGGER.warning("no such task " + id);
			return;
		}

		tasksQueue.remove(ttw);
	}

	public void setTickLength(long tickLength, TimeUnit unit) {
		this.tickLength = tickLength;
		this.tickLengthUnits = unit;
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
	 * @see org.graphstream.nui.UIContext#sync()
	 */
	@Override
	public void sync() {
		if (proxy instanceof ProxyPipe) {
			if (Thread.currentThread() != thread) {
				try {
					invokeOnUIThread(syncAction);
				} catch (InterruptedException e) {
					LOGGER.log(Level.WARNING, "sync was interrupted", e);
				}
			} else {
				((ProxyPipe) proxy).pump();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#close()
	 */
	@Override
	public void release() {
		try {
			invokeOnUIThread(new Runnable() {
				public void run() {
					internalRelease();

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
			});
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE,
					"ui-context release does not end correctly", e);
		}
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
	public void loadModule(String moduleId) throws InstantiationException,
			ModuleNotFoundException {
		checkThread();
		UIModules.loadModule(this, moduleId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#tryLoadModule(java.lang.String)
	 */
	@Override
	public boolean tryLoadModule(String moduleId) {
		try {
			loadModule(moduleId);
			return true;
		} catch (InstantiationException | ModuleNotFoundException e) {
			LOGGER.log(Level.INFO, "unable to load module " + moduleId);
		}

		return false;
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

		for (int i = 0; i < modules.length; i++) {
			LOGGER.info("insert module " + modules[i].getModuleID());
			this.modules.put(modules[i].getModuleID(), modules[i]);
		}

		for (int i = 0; i < modules.length; i++) {
			LOGGER.info("init module " + modules[i].getModuleID());
			modules[i].init(this);
		}
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
			LOGGER.warning("view already exists");
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
	 * @see org.graphstream.nui.UIContext#createWorker(java.lang.String,
	 * org.graphstream.nui.context.WorkerTaskMap)
	 */
	@Override
	public void createWorker(String id, WorkerTaskMap tasks) {
		if (workers.containsKey(id))
			LOGGER.warning(String.format("Worker \"%s\" already created", id));
		else {
			ContextWorker cw = createContextWorker(id);
			cw.setTaskMap(tasks);
			cw.init();

			workers.put(id, cw);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIContext#terminateWorker(java.lang.String)
	 */
	@Override
	public void terminateWorker(String id) {
		UIWorker w = workers.remove(id);

		if (w != null) {
			try {
				w.terminate();
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE,
						String.format("Failed to terminate worker \"%s\"", id),
						e);
			}
		} else
			LOGGER.warning(String.format("Worker \"%s\" does not exist", id));
	}

	protected ContextWorker createContextWorker(String id) {
		return null;
	}

	protected void checkThread() {
		if (Thread.currentThread() != thread) {
			Logger.getLogger(AbstractContext.class.getName()).warning(
					"Manipulating modules or views outside of "
							+ "the context thread may cause "
							+ "concurrent exceptions. Don't blame the devs.");
		}
	}

	class TickTaskWrapper implements Delayed, Runnable {
		protected final TickTask task;
		protected long nextExecutionDate;

		TickTaskWrapper(TickTask task) {
			this.task = task;
			resetNextExecutionDate();
		}

		protected void resetNextExecutionDate() {
			nextExecutionDate = System.currentTimeMillis()
					+ TimeUnit.MILLISECONDS.convert(task.getCycleLength()
							* tickLength, tickLengthUnits);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Delayed o) {
			return Long.compare(getDelay(TimeUnit.MILLISECONDS),
					o.getDelay(TimeUnit.MILLISECONDS));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
		 */
		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(nextExecutionDate - System.currentTimeMillis(),
					TimeUnit.MILLISECONDS);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			task.run();
		}
	}
}
