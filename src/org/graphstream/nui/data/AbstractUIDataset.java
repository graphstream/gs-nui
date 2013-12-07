/*
 * Copyright 2006 - 2014
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
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
package org.graphstream.nui.data;

import java.util.LinkedList;

import org.graphstream.nui.UIDataset;
import org.graphstream.nui.UIDatasetListener;
import org.graphstream.nui.Viewer;
import org.graphstream.stream.ElementSink;

public abstract class AbstractUIDataset implements UIDataset, ElementSink {

	protected Viewer viewer;
	protected GraphData graphData;

	protected LinkedList<UIDatasetListener> listeners;

	protected DataFactory dataFactory;

	protected AbstractUIDataset(DataFactory factory) {
		this.dataFactory = factory;
		this.listeners = new LinkedList<UIDatasetListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#init(org.graphstream.stream.Source)
	 */
	public void init(Viewer viewer) {
		assert this.viewer == null;

		this.viewer = viewer;
		this.graphData = dataFactory.createGraphData(this, "graph");

		viewer.getSourceFunnel().addElementSink(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#release()
	 */
	public void release() {
		viewer.getSourceFunnel().removeElementSink(this);
		viewer = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getViewer()
	 */
	public Viewer getViewer() {
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#setDataFactory(org.graphstream.nui.data
	 * .DataFactory)
	 */
	public void setDataFactory(DataFactory factory) {
		this.dataFactory = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#addUIDatasetListener(org.graphstream.nui
	 * .UIDatasetListener)
	 */
	public void addUIDatasetListener(UIDatasetListener l) {
		listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIDataset#removeUIDatasetListener(org.graphstream
	 * .nui.UIDatasetListener)
	 */
	public void removeUIDatasetListener(UIDatasetListener l) {
		listeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIDataset#getGraphData()
	 */
	public GraphData getGraphData() {
		return graphData;
	}
}
