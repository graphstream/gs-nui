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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultContext extends AbstractContext {
	private static final Logger LOGGER = Logger.getLogger(DefaultContext.class
			.getName());

	protected ScheduledExecutorService executor;
	protected ScheduledFuture<?> tickerFuture;
	protected Runnable ticker = new Runnable() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			tick();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.context.AbstractContext#init(org.graphstream.nui.
	 * UIContext.ThreadingModel)
	 */
	@Override
	public void init(ThreadingModel threadingModel) {
		executor = Executors.newScheduledThreadPool(1);
		super.init(threadingModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.context.AbstractContext#invokeOnUIThread(java.lang
	 * .Runnable)
	 */
	@Override
	public void invokeOnUIThread(Runnable r) throws InterruptedException {
		Future<?> f = executor.submit(r);

		try {
			f.get();
		} catch (ExecutionException e) {
			LOGGER.log(Level.SEVERE, "failed to execute task", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.AbstractContext#internalInit()
	 */
	@Override
	protected void internalInit() {
		resetTicker();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.AbstractContext#internalRelease()
	 */
	@Override
	protected void internalRelease() {
		tickerFuture.cancel(true);
		executor.shutdownNow();

		try {
			executor.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			if (!executor.isShutdown())
				LOGGER.log(Level.SEVERE, "fail to shutdown executor", e);
		}

		executor = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.AbstractContext#setTickLength(long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public void setTickLength(long tickLength, TimeUnit unit) {
		super.setTickLength(tickLength, unit);

		ScheduledExecutorService oldExecutor = executor;
		resetTicker();
		oldExecutor.shutdown();
	}

	protected void resetTicker() {
		if (tickerFuture != null)
			tickerFuture.cancel(false);

		tickerFuture = executor.scheduleAtFixedRate(ticker, 0, tickLength,
				tickLengthUnits);
	}
}
