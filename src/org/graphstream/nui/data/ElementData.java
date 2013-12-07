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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.graphstream.nui.UIDataset;
import org.graphstream.nui.style.ElementStyle;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.VisibilityMode;

public abstract class ElementData {
	private final static Set<String> EMPTY_UI_CLASS = Collections
			.unmodifiableSet(new HashSet<String>());

	public final String id;
	final UIDataset dataset;

	VisibilityMode visibility;

	public HashSet<String> uiClasses;
	public String state;

	public double uiColor;

	public String label;
	public boolean showLabel;

	protected int currentIndex;

	protected ElementStyle style;

	public ElementData(UIDataset dataset, String id) {
		this.id = id;
		this.dataset = dataset;

		this.visibility = VisibilityMode.NORMAL;
		this.showLabel = true;

		checkStyleChanged();
	}

	public int index() {
		return currentIndex;
	}

	public void updateIndex(int index) {
		currentIndex = index;
	}

	public boolean isVisible() {
		return visibility != VisibilityMode.HIDDEN;
	}

	public void hide() {
		if (visibility != VisibilityMode.HIDDEN) {
			visibility = VisibilityMode.HIDDEN;
			dataset.elementDataUpdated(this);
		}
	}

	public void show() {
		if (visibility != VisibilityMode.NORMAL) {
			visibility = VisibilityMode.NORMAL;
			dataset.elementDataUpdated(this);
		}
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;

		ElementStyle style = this.style.getForState(this);
		setStyle(style);
	}

	public int getUIClassCount() {
		return uiClasses == null ? 0 : uiClasses.size();
	}

	public Iterable<String> getEachUIClass() {
		return uiClasses == null ? EMPTY_UI_CLASS : uiClasses;
	}

	public boolean hasUIClass(String c) {
		return uiClasses == null ? false : uiClasses.contains(c);
	}

	public void addUIClass(String uiClass) {
		if (uiClasses == null)
			uiClasses = new HashSet<String>();

		uiClasses.add(uiClass);
		checkStyleChanged();
	}

	public void setUIClass(String uiClass) {
		if (uiClass == null)
			removeUIClass();
		else {
			String[] classes = uiClass.trim().split("\\s+");

			if (classes != null)
				setUIClass(classes);
		}
	}

	public void setUIClass(String[] classes) {
		if (classes == null) {
			if (uiClasses != null) {
				uiClasses.clear();
				uiClasses = null;

				checkStyleChanged();
			}
		} else {
			if (uiClasses == null)
				uiClasses = new HashSet<String>();

			uiClasses.clear();

			for (int i = 0; i < classes.length; i++)
				uiClasses.add(classes[i]);

			checkStyleChanged();
		}
	}

	public void removeUIClass(String uiClass) {
		if (uiClasses == null)
			return;

		if (uiClasses.remove(uiClass))
			checkStyleChanged();

		if (uiClasses.size() == 0)
			uiClasses = null;
	}

	public void removeUIClass() {
		if (uiClasses == null)
			return;

		uiClasses.clear();
		uiClasses = null;

		checkStyleChanged();
	}

	public void setStyle(ElementStyle style) {
		if (style != this.style) {
			System.out.printf("set style of %s#%s to %s\n", getClass()
					.getSimpleName(), id, style.selector);

			this.style = style;
			dataset.elementDataUpdated(this);
		}
	}

	public void checkStyleChanged() {
		if (dataset == null)
			System.err.printf("dataset is null\n");
		if (dataset.getViewer() == null)
			System.err.printf("viewer is null\n");
		if (dataset.getViewer().getStyleSheet() == null)
			System.err.printf("stylesheet is null\n");

		ElementStyle style = dataset.getViewer().getStyleSheet()
				.getElementStyle(this);

		setStyle(style);
	}
}
