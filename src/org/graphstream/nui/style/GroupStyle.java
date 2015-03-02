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
package org.graphstream.nui.style;

import java.awt.Color;

import org.graphstream.nui.style.StyleConstants.ArrowShape;
import org.graphstream.nui.style.StyleConstants.FillMode;
import org.graphstream.nui.style.StyleConstants.IconMode;
import org.graphstream.nui.style.StyleConstants.ShadowMode;
import org.graphstream.nui.style.StyleConstants.Shape;
import org.graphstream.nui.style.StyleConstants.SizeMode;
import org.graphstream.nui.style.StyleConstants.SpriteOrientation;
import org.graphstream.nui.style.StyleConstants.StrokeMode;
import org.graphstream.nui.style.StyleConstants.StyleKey;
import org.graphstream.nui.style.StyleConstants.TextAlignment;
import org.graphstream.nui.style.StyleConstants.TextBackgroundMode;
import org.graphstream.nui.style.StyleConstants.TextMode;
import org.graphstream.nui.style.StyleConstants.TextStyle;
import org.graphstream.nui.style.StyleConstants.TextVisibilityMode;
import org.graphstream.nui.style.StyleConstants.VisibilityMode;
import org.graphstream.nui.style.util.Colors;
import org.graphstream.nui.style.util.Value;
import org.graphstream.nui.style.util.Values;

public interface GroupStyle {

	Selector selector();

	GroupStyle parent();

	void setParent(GroupStyle parent);

	/**
	 * Get each of the states define in this group. The associated group style
	 * can be retrieved using {@link #getStateStyle(String)}.
	 * 
	 * @return an iterable on the state key
	 */
	Iterable<String> getEachState();

	/**
	 * Get the style directly associated with a state in this group style.
	 * Unlike {@link #getForState(ElementStyle)}m there is no deep search into
	 * parent style to find the correct style.
	 * 
	 * @param state
	 * @return
	 */
	GroupStyle getStateStyle(String state);

	GroupStyle getForState(ElementStyle data);

	void setStateStyle(String state, GroupStyle style);

	void set(String key, Object value);

	void set(StyleKey style, Object value);

	Object get(StyleKey key);
	
	Iterable<StyleKey> getEachStyleKey();

	void merge(GroupStyle style);

	//
	// ------------------------------------------------------------
	// The following is the methods to access all style properties.
	// ------------------------------------------------------------
	//

	/**
	 * How to fill the content of an element.
	 */
	FillMode getFillMode();

	/**
	 * Which color(s) to use for fill modes that use it.
	 */
	Colors getFillColor();

	int getFillColorCount();

	Color getFillColor(int i);

	/**
	 * Which image to use when filling the element contents with it.
	 */
	String getFillImage();

	/**
	 * How to draw the element contour.
	 */
	StrokeMode getStrokeMode();

	/**
	 * How to color the element contour.
	 */
	Colors getStrokeColor();

	int getStrokeColorCount();

	Color getStrokeColor(int i);

	/**
	 * Width of the element contour.
	 */
	Value getStrokeWidth();

	/**
	 * How to draw the shadow of the element.
	 */
	ShadowMode getShadowMode();

	/**
	 * Color(s) of the element shadow.
	 */
	Colors getShadowColor();

	int getShadowColorCount();

	Color getShadowColor(int i);

	/**
	 * Width of the element shadow.
	 */
	Value getShadowWidth();

	/**
	 * Offset of the element shadow centre according to the element centre.
	 */
	Values getShadowOffset();

	/**
	 * Additional space to add inside the element between its contour and its
	 * contents.
	 */
	Values getPadding();

	/**
	 * How to draw the text of the element.
	 */
	TextMode getTextMode();

	/**
	 * How and when to show the text of the element.
	 */
	TextVisibilityMode getTextVisibilityMode();

	/**
	 * Visibility values if the text visibility changes.
	 */
	Values getTextVisibility();

	/**
	 * The text color(s).
	 */
	Colors getTextColor();

	int getTextColorCount();

	Color getTextColor(int i);

	/**
	 * The text font style variation.
	 */
	TextStyle getTextStyle();

	/**
	 * The text font.
	 */
	String getTextFont();

	/**
	 * The text size in points.
	 */
	double getTextSize();

	/**
	 * How to draw the icon around the text (or instead of the text).
	 */
	IconMode getIconMode();

	/**
	 * The icon image to use.
	 */
	String getIcon();

	/**
	 * How and when to show the element.
	 */
	VisibilityMode getVisibilityMode();

	/**
	 * The element visibility if it is variable.
	 */
	Values getVisibility();

	/**
	 * How to size the element.
	 */
	SizeMode getSizeMode();

	/**
	 * The element dimensions.
	 */
	Values getSize();

	/**
	 * How to align the text according to the element centre.
	 */
	TextAlignment getTextAlignment();

	TextBackgroundMode getTextBackgroundMode();

	Colors getTextBackgroundColor();

	Color getTextBackgroundColor(int i);

	/**
	 * Offset of the text from its computed position.
	 */
	Values getTextOffset();

	/**
	 * Padding of the text inside its background, if any.
	 */
	Values getTextPadding();

	/**
	 * The element shape.
	 */
	Shape getShape();

	/**
	 * How to orient a sprite according to its attachement.
	 */
	SpriteOrientation getSpriteOrientation();

	/**
	 * The shape of edges arrows.
	 */
	ArrowShape getArrowShape();

	/**
	 * Image to use for the arrow.
	 */
	String getArrowImage();

	/**
	 * Edge arrow dimensions.
	 */
	Values getArrowSize();

	Integer getZIndex();

}