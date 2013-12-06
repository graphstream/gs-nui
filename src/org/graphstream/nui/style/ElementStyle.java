/*
 * Copyright 2006 - 2012
 *      Stefan Balev    <stefan.balev@graphstream-project.org>
 *      Julien Baudry	<julien.baudry@graphstream-project.org>
 *      Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *      Yoann Pigné	    <yoann.pigne@graphstream-project.org>
 *      Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
import java.util.EnumMap;
import java.util.HashMap;

import org.graphstream.nui.data.ElementData;
import org.graphstream.ui.graphicGraph.stylesheet.Colors;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;

public class ElementStyle implements StyleConstants {
	protected static final String DEFAULT_STATE = "<default>";

	protected ElementStyle parent;
	public final Selector selector;
	protected final EnumMap<StyleKey, Object> styles;
	protected HashMap<String, ElementStyle> states;

	public ElementStyle(Selector selector) {
		this.selector = selector;
		this.styles = new EnumMap<StyleKey, Object>(StyleKey.class);
		this.states = null;
	}

	public ElementStyle parent() {
		return parent;
	}

	public void setParent(ElementStyle parent) {
		this.parent = parent;

		if (parent.selector.equals(selector.getNoStateSelector())
				&& selector.hasState())
			parent.setStateStyle(selector.state, this);
	}

	public ElementStyle getForState(ElementData data) {
		if (selector.hasState()) {
			if (parent != null)
				return parent.getForState(data);
		} else {
			ElementStyle r = states == null ? null : states.get(data.state);

			if (r != null)
				return r;
		}

		return this;
	}

	public void setStateStyle(String state, ElementStyle style) {
		if (states == null)
			states = new HashMap<String, ElementStyle>();

		states.put(state, style);
	}

	protected StyleKey getStyleKey(String key) {
		try {
			key = key.replaceAll("\\W", "_");
			key = key.toUpperCase();

			StyleConstants.StyleKey st = StyleKey.valueOf(key);
			return st;
		} catch (IllegalArgumentException e) {
			// Bad key
		}

		return null;
	}

	protected boolean checkValue(StyleKey style, Object value) {
		if (value == null)
			return style.nullAllowed;

		return style.valueType.isAssignableFrom(value.getClass());
	}

	public void set(String key, Object value) {
		StyleKey st = getStyleKey(key);

		if (st != null)
			set(st, value);
		else
			System.err.printf("[warning] invalid style key \"%s\"\n", key);
	}

	public void set(StyleKey style, Object value) {
		if (checkValue(style, value))
			styles.put(style, value);
		else
			System.err.printf(
					"[warning] bad value type for %s, expecting %s, got %s\n",
					style, style.valueType,
					value == null ? "null" : value.getClass());
	}

	public Object get(StyleKey key) {
		Object r = styles.get(key);

		if (r == null && parent != null)
			return parent.get(key);

		return r;
	}

	public void merge(ElementStyle style) {
		styles.putAll(style.styles);

		for (String state : style.states.keySet()) {
			if (states.containsKey(state)) {
				states.get(state).merge(style.states.get(state));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = selector.toString() + " " + styles.toString();

		if (states != null && states.size() > 0)
			str += " states:" + states.keySet();

		return str;
	}

	/**
	 * How to fill the content of an element.
	 */
	public FillMode getFillMode() {
		return (FillMode) get(StyleKey.FILL_MODE);
	}

	/**
	 * Which color(s) to use for fill modes that use it.
	 */
	public Colors getFillColor() {
		return (Colors) get(StyleKey.FILL_COLOR);
	}

	public int getFillColorCount() {
		Colors colors = getFillColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getFillColor(int i) {
		Colors colors = getFillColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Which image to use when filling the element contents with it.
	 */
	public String getFillImage() {
		return (String) get(StyleKey.FILL_IMAGE);
	}

	/**
	 * How to draw the element contour.
	 */
	public StrokeMode getStrokeMode() {
		return (StrokeMode) get(StyleKey.STROKE_MODE);
	}

	/**
	 * How to color the element contour.
	 */
	public Colors getStrokeColor() {
		return (Colors) get(StyleKey.STROKE_COLOR);
	}

	public int getStrokeColorCount() {
		Colors colors = getStrokeColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getStrokeColor(int i) {
		Colors colors = getStrokeColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Width of the element contour.
	 */
	public Value getStrokeWidth() {
		return (Value) get(StyleKey.STROKE_WIDTH);
	}

	/**
	 * How to draw the shadow of the element.
	 */
	public ShadowMode getShadowMode() {
		return (ShadowMode) get(StyleKey.SHADOW_MODE);
	}

	/**
	 * Color(s) of the element shadow.
	 */
	public Colors getShadowColor() {
		return (Colors) get(StyleKey.SHADOW_COLOR);
	}

	public int getShadowColorCount() {
		Colors colors = getShadowColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getShadowColor(int i) {
		Colors colors = getShadowColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Width of the element shadow.
	 */
	public Value getShadowWidth() {
		return (Value) get(StyleKey.SHADOW_WIDTH);
	}

	/**
	 * Offset of the element shadow centre according to the element centre.
	 */
	public Values getShadowOffset() {
		return (Values) get(StyleKey.SHADOW_OFFSET);
	}

	/**
	 * Additional space to add inside the element between its contour and its
	 * contents.
	 */
	public Values getPadding() {
		return (Values) get(StyleKey.PADDING);
	}

	/**
	 * How to draw the text of the element.
	 */
	public TextMode getTextMode() {
		return (TextMode) get(StyleKey.TEXT_MODE);
	}

	/**
	 * How and when to show the text of the element.
	 */
	public TextVisibilityMode getTextVisibilityMode() {
		return (TextVisibilityMode) get(StyleKey.TEXT_VISIBILITY_MODE);
	}

	/**
	 * Visibility values if the text visibility changes.
	 */
	public Values getTextVisibility() {
		return (Values) get(StyleKey.TEXT_VISIBILITY);
	}

	/**
	 * The text color(s).
	 */
	public Colors getTextColor() {
		return (Colors) get(StyleKey.TEXT_COLOR);
	}

	public int getTextColorCount() {
		Colors colors = getTextColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	public Color getTextColor(int i) {
		Colors colors = getTextColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * The text font style variation.
	 */
	public TextStyle getTextStyle() {
		return (TextStyle) get(StyleKey.TEXT_STYLE);
	}

	/**
	 * The text font.
	 */
	public String getTextFont() {
		return (String) get(StyleKey.TEXT_FONT);
	}

	/**
	 * The text size in points.
	 */
	public double getTextSize() {
		return (Double) get(StyleKey.TEXT_SIZE);
	}

	/**
	 * How to draw the icon around the text (or instead of the text).
	 */
	public IconMode getIconMode() {
		return (IconMode) get(StyleKey.ICON_MODE);
	}

	/**
	 * The icon image to use.
	 */
	public String getIcon() {
		return (String) get(StyleKey.ICON);
	}

	/**
	 * How and when to show the element.
	 */
	public VisibilityMode getVisibilityMode() {
		return (VisibilityMode) get(StyleKey.VISIBILITY_MODE);
	}

	/**
	 * The element visibility if it is variable.
	 */
	public Values getVisibility() {
		return (Values) get(StyleKey.VISIBILITY);
	}

	/**
	 * How to size the element.
	 */
	public SizeMode getSizeMode() {
		return (SizeMode) get(StyleKey.SIZE_MODE);
	}

	/**
	 * The element dimensions.
	 */
	public Values getSize() {
		return (Values) get(StyleKey.SIZE);
	}

	/**
	 * How to align the text according to the element centre.
	 */
	public TextAlignment getTextAlignment() {
		return (TextAlignment) get(StyleKey.TEXT_ALIGNMENT);
	}

	public TextBackgroundMode getTextBackgroundMode() {
		return (TextBackgroundMode) get(StyleKey.TEXT_BACKGROUND_MODE);
	}

	public Colors getTextBackgroundColor() {
		return (Colors) get(StyleKey.TEXT_BACKGROUND_COLOR);
	}

	public Color getTextBackgroundColor(int i) {
		Colors colors = getTextBackgroundColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/**
	 * Offset of the text from its computed position.
	 */
	public Values getTextOffset() {
		return (Values) get(StyleKey.TEXT_OFFSET);
	}

	/**
	 * Padding of the text inside its background, if any.
	 */
	public Values getTextPadding() {
		return (Values) get(StyleKey.TEXT_PADDING);
	}

	/**
	 * The element shape.
	 */
	public Shape getShape() {
		return (Shape) get(StyleKey.SHAPE);
	}

	/**
	 * How to orient a sprite according to its attachement.
	 */
	public SpriteOrientation getSpriteOrientation() {
		return (SpriteOrientation) get(StyleKey.SPRITE_ORIENTATION);
	}

	/**
	 * The shape of edges arrows.
	 */
	public ArrowShape getArrowShape() {
		return (ArrowShape) get(StyleKey.ARROW_SHAPE);
	}

	/**
	 * Image to use for the arrow.
	 */
	public String getArrowImage() {
		return (String) get(StyleKey.ARROW_IMAGE);
	}

	/**
	 * Edge arrow dimensions.
	 */
	public Values getArrowSize() {
		return (Values) get(StyleKey.ARROW_SIZE);
	}

	public Integer getZIndex() {
		return (Integer) get(StyleKey.Z_INDEX);
	}
}
