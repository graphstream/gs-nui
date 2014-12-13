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
package org.graphstream.nui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.graphstream.nui.context.AbstractContext;

public class SwingContext extends AbstractContext {
	protected Timer timer;

	public SwingContext() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					SwingContext.this.thread = Thread.currentThread();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.AbstractContext#internalInit()
	 */
	@Override
	protected void internalInit() {
		createTimer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.AbstractContext#internalRelease()
	 */
	@Override
	protected void internalRelease() {
		timer.stop();
		timer = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.context.AbstractContext#invokeOnUIThread(java.lang
	 * .Runnable)
	 */
	@Override
	public void invokeOnUIThread(final Runnable r) throws InterruptedException {
		if (SwingUtilities.isEventDispatchThread()) {
			sync();
			r.run();
			return;
		}

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					sync();
					r.run();
				}
			});
		} catch (InvocationTargetException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"invocation on ui-thread failed", e);
		}
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
		createTimer();
	}

	protected void createTimer() {
		if (timer != null)
			timer.stop();

		timer = new Timer((int) TimeUnit.MILLISECONDS.convert(tickLength,
				tickLengthUnits), new Ticker());

		timer.start();
	}

	class Ticker implements ActionListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			SwingContext.this.tick();

			//
			// Should add here actions that have to be triggered at each timer
			// step.
			//
		}
	}
}
