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
package org.graphstream.nui.context.worker;

import java.util.concurrent.atomic.AtomicBoolean;

import org.graphstream.nui.UIContext;
import org.graphstream.nui.context.UIWorker;

public class DefaultWorker implements UIWorker {
	protected final String id;
	protected final UIContext ctx;

	protected Thread workerThread;

	protected AtomicBoolean alive;

	protected WorkerTask task;

	public DefaultWorker(String id, UIContext ctx) {
		this.id = id;
		this.ctx = ctx;
		this.alive = new AtomicBoolean(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.UIWorker#getWorkerID()
	 */
	@Override
	public String getWorkerID() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.UIWorker#getWorkerTask()
	 */
	@Override
	public WorkerTask getWorkerTask() {
		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.UIWorker#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub
		alive.set(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.context.UIWorker#terminate()
	 */
	@Override
	public void terminate() throws InterruptedException {
		alive.set(false);

		workerThread.interrupt();
		workerThread.join();

		task.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (alive.get()) {

		}
	}
}
