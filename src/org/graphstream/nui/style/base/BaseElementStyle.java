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
package org.graphstream.nui.style.base;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.graphstream.nui.UIStyle;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.style.ElementStyle;
import org.graphstream.nui.style.GroupStyle;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.VisibilityMode;

public abstract class BaseElementStyle implements ElementStyle {
	private final static Set<String> EMPTY_UI_CLASS = Collections
			.unmodifiableSet(new HashSet<String>());

	public final ElementIndex index;

	VisibilityMode visibility;

	HashSet<String> uiClasses;
	String state;

	double uiColor;

	String label;
	boolean showLabel;

	protected GroupStyle style;

	public BaseElementStyle(ElementIndex index) {
		this.index = index;

		this.visibility = VisibilityMode.NORMAL;
		this.showLabel = true;

		checkStyleChanged();
	}

	protected abstract void elementStyleUpdated();

	protected abstract UIStyle getUIStyle();

	protected int computeColor() {
		if (style != null) {
			switch (style.getFillMode()) {
			case DYN_PLAIN:
				return interpolateColor();
			default:
				return style.getFillColor(0).getRGB();
			}
		}

		return 0xFF000000;
	}

	protected int interpolateColor() {
		int color = style.getFillColor(0).getRGB();
		int n = style.getFillColorCount();

		if (n > 1) {
			if (n > 1) {
				if (uiColor < 0)
					uiColor = 0;
				else if (uiColor > 1)
					uiColor = 1;

				if (uiColor == 1) {
					//
					// Simplification, faster.
					//
					color = style.getFillColor(n - 1).getRGB();
				}
				//
				// If value == 0, color is already set above.
				//
				else if (uiColor != 0) {
					double div = 1f / (n - 1);
					int col = (int) (uiColor / div);

					div = (uiColor - (div * col)) / div;

					Color c0 = style.getFillColor(col);
					Color c1 = style.getFillColor(col + 1);

					int r = (int) ((c0.getRed() * (1 - div)) + (c1.getRed() * div));
					int g = (int) ((c0.getGreen() * (1 - div)) + (c1.getGreen() * div));
					int b = (int) ((c0.getBlue() * (1 - div)) + (c1.getBlue() * div));
					int a = (int) ((c0.getAlpha() * (1 - div)) + (c1.getAlpha() * div));

					color = ((a & 0xFF) << 24) | ((r & 0xFF) << 16)
							| ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
				}
			}
		}

		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#index()
	 */
	@Override
	public ElementIndex index() {
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return visibility != VisibilityMode.HIDDEN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#hide()
	 */
	@Override
	public void hide() {
		if (visibility != VisibilityMode.HIDDEN) {
			visibility = VisibilityMode.HIDDEN;
			elementStyleUpdated();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#show()
	 */
	@Override
	public void show() {
		if (visibility != VisibilityMode.NORMAL) {
			visibility = VisibilityMode.NORMAL;
			elementStyleUpdated();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#hasGroupStyle()
	 */
	@Override
	public boolean hasGroupStyle() {
		return style != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#getGroupStyle()
	 */
	@Override
	public GroupStyle getGroupStyle() {
		return style;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#getColor()
	 */
	@Override
	public abstract int getColor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#getState()
	 */
	@Override
	public String getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#setState(java.lang.String)
	 */
	@Override
	public void setState(String state) {
		this.state = state;

		GroupStyle style = this.style.getForState(this);
		setStyle(style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#getUIClassCount()
	 */
	@Override
	public int getUIClassCount() {
		return uiClasses == null ? 0 : uiClasses.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#getEachUIClass()
	 */
	@Override
	public Iterable<String> getEachUIClass() {
		return uiClasses == null ? EMPTY_UI_CLASS : uiClasses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#hasUIClass(java.lang.String)
	 */
	@Override
	public boolean hasUIClass(String c) {
		return uiClasses == null ? false : uiClasses.contains(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#addUIClass(java.lang.String)
	 */
	@Override
	public void addUIClass(String uiClass) {
		if (uiClasses == null)
			uiClasses = new HashSet<String>();

		uiClasses.add(uiClass);
		checkStyleChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#setUIClass(java.lang.String)
	 */
	@Override
	public void setUIClass(String uiClass) {
		if (uiClass == null)
			removeUIClass();
		else {
			String[] classes = uiClass.trim().split("\\s+");

			if (classes != null)
				setUIClass(classes);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.style.ElementStyle#setUIClass(java.lang.String[])
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.style.ElementStyle#removeUIClass(java.lang.String)
	 */
	@Override
	public void removeUIClass(String uiClass) {
		if (uiClasses == null)
			return;

		if (uiClasses.remove(uiClass))
			checkStyleChanged();

		if (uiClasses.size() == 0)
			uiClasses = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#removeUIClass()
	 */
	@Override
	public void removeUIClass() {
		if (uiClasses == null)
			return;

		uiClasses.clear();
		uiClasses = null;

		checkStyleChanged();
	}

	public void setUIColor(double uiColor) {
		this.uiColor = uiColor;
		elementStyleUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.style.ElementStyle#setStyle(org.graphstream.nui.style
	 * .base.BaseGroupStyle)
	 */
	@Override
	public void setStyle(GroupStyle style) {
		if (style != this.style) {
			this.style = style;
			elementStyleUpdated();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.ElementStyle#checkStyleChanged()
	 */
	@Override
	public void checkStyleChanged() {
		setStyle(getUIStyle().searchGroupStyle(this));
	}
}
