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
package org.graphstream.nui;

/**
 * A factory to create objects related to the viewer. It will defined the
 * implementation of these objects, like {@link org.graphstream.nui.UIDataset}
 * for example.
 * 
 */
public interface UIFactory {
	/**
	 * Create a new dataset.
	 * 
	 * @param viewer
	 *            the viewer which will use this dataset
	 * @return a new dataset object
	 */
	UIDataset createDataset(Viewer viewer);

	/**
	 * Create a new camera.
	 * 
	 * @param viewer
	 *            the viewer which will use this dataset
	 * @return a new camera object
	 */
	UICamera createCamera(Viewer viewer);

	/**
	 * Create a new attributes handler. This allows developper to extends the
	 * default class and add their own code.
	 * 
	 * @param viewer
	 *            the viewer which will use this dataset
	 * @return a new UIAttributes object
	 */
	UIAttributes createAttributesHandler(Viewer viewer);

	/**
	 * Create a new camera. This allows developper to extends the
	 * default class and add their own code.
	 * 
	 * @param viewer
	 *            the viewer which will use this dataset
	 * @return a new UIStylesheet object
	 */
	UIStyleSheet createStylesheetHandler(Viewer viewer);
}
