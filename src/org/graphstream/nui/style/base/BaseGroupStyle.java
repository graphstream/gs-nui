/*
 * Copyright 2006 - 2014
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
package org.graphstream.nui.style.base;

import java.awt.Color;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.nui.style.ElementStyle;
import org.graphstream.nui.style.GroupStyle;
import org.graphstream.nui.style.Selector;
import org.graphstream.nui.style.StyleConstants;
import org.graphstream.nui.style.util.Colors;
import org.graphstream.nui.style.util.Value;
import org.graphstream.nui.style.util.Values;

public class BaseGroupStyle implements StyleConstants, GroupStyle {
	protected static final String DEFAULT_STATE = "<default>";

	protected GroupStyle parent;
	public final Selector selector;
	protected final Map<StyleKey, Object> styles;
	protected Map<String, GroupStyle> states;

	public BaseGroupStyle(final Selector selector) {
		this.selector = selector;
		this.styles = new EnumMap<StyleKey, Object>(StyleKey.class);
		this.states = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#parent()
	 */
	@Override
	public GroupStyle parent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#selector()
	 */
	@Override
	public Selector selector() {
		return selector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.style.GroupStyle#setParent(org.graphstream.nui.style
	 * .BaseGroupStyle)
	 */
	@Override
	public void setParent(GroupStyle parent) {
		this.parent = parent;

		if (parent.selector().equals(selector.getNoStateSelector())
				&& selector.hasState())
			parent.setStateStyle(selector.state, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getStateStyle(java.lang.String)
	 */
	@Override
	public GroupStyle getStateStyle(String state) {
		return states.get(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.style.GroupStyle#getForState(org.graphstream.nui.
	 * style.ElementStyle)
	 */
	@Override
	public GroupStyle getForState(ElementStyle data) {
		if (selector.hasState()) {
			if (parent != null)
				return parent.getForState(data);
		} else {
			GroupStyle r = states == null ? null : states.get(data.getState());

			if (r != null)
				return r;
		}

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#setStateStyle(java.lang.String,
	 * org.graphstream.nui.style.BaseGroupStyle)
	 */
	@Override
	public void setStateStyle(String state, GroupStyle style) {
		if (states == null)
			states = new HashMap<String, GroupStyle>();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#set(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		StyleKey st = getStyleKey(key);

		if (st != null)
			set(st, value);
		else
			System.err.printf("[warning] invalid style key \"%s\"\n", key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#set(org.graphstream.nui.style.
	 * StyleConstants.StyleKey, java.lang.Object)
	 */
	@Override
	public void set(StyleKey style, Object value) {
		if (checkValue(style, value))
			styles.put(style, value);
		else
			System.err.printf(
					"[warning] bad value type for %s, expecting %s, got %s\n",
					style, style.valueType,
					value == null ? "null" : value.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#get(org.graphstream.nui.style.
	 * StyleConstants.StyleKey)
	 */
	@Override
	public Object get(StyleKey key) {
		Object r = styles.get(key);

		if (r == null && parent != null)
			return parent.get(key);

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getEachStyleKey()
	 */
	@Override
	public Iterable<StyleKey> getEachStyleKey() {
		return Collections.unmodifiableSet(styles.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getEachState()
	 */
	@Override
	public Iterable<String> getEachState() {
		if (states == null)
			return Collections.emptySet();

		return states.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.style.GroupStyle#merge(org.graphstream.nui.style.
	 * BaseGroupStyle)
	 */
	@Override
	public void merge(GroupStyle style) {
		for (StyleKey key : style.getEachStyleKey())
			styles.put(key, style.get(key));

		for (String state : style.getEachState()) {
			if (states == null)
				states = new HashMap<String, GroupStyle>();

			if (states.containsKey(state)) {
				states.get(state).merge(style.getStateStyle(state));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getFillMode()
	 */
	@Override
	public FillMode getFillMode() {
		return (FillMode) get(StyleKey.FILL_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getFillColor()
	 */
	@Override
	public Colors getFillColor() {
		return (Colors) get(StyleKey.FILL_COLOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getFillColorCount()
	 */
	@Override
	public int getFillColorCount() {
		Colors colors = getFillColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getFillColor(int)
	 */
	@Override
	public Color getFillColor(int i) {
		Colors colors = getFillColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getFillImage()
	 */
	@Override
	public String getFillImage() {
		return (String) get(StyleKey.FILL_IMAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getStrokeMode()
	 */
	@Override
	public StrokeMode getStrokeMode() {
		return (StrokeMode) get(StyleKey.STROKE_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getStrokeColor()
	 */
	@Override
	public Colors getStrokeColor() {
		return (Colors) get(StyleKey.STROKE_COLOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getStrokeColorCount()
	 */
	@Override
	public int getStrokeColorCount() {
		Colors colors = getStrokeColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getStrokeColor(int)
	 */
	@Override
	public Color getStrokeColor(int i) {
		Colors colors = getStrokeColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getStrokeWidth()
	 */
	@Override
	public Value getStrokeWidth() {
		return (Value) get(StyleKey.STROKE_WIDTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getShadowMode()
	 */
	@Override
	public ShadowMode getShadowMode() {
		return (ShadowMode) get(StyleKey.SHADOW_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getShadowColor()
	 */
	@Override
	public Colors getShadowColor() {
		return (Colors) get(StyleKey.SHADOW_COLOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getShadowColorCount()
	 */
	@Override
	public int getShadowColorCount() {
		Colors colors = getShadowColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getShadowColor(int)
	 */
	@Override
	public Color getShadowColor(int i) {
		Colors colors = getShadowColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getShadowWidth()
	 */
	@Override
	public Value getShadowWidth() {
		return (Value) get(StyleKey.SHADOW_WIDTH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getShadowOffset()
	 */
	@Override
	public Values getShadowOffset() {
		return (Values) get(StyleKey.SHADOW_OFFSET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getPadding()
	 */
	@Override
	public Values getPadding() {
		return (Values) get(StyleKey.PADDING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextMode()
	 */
	@Override
	public TextMode getTextMode() {
		return (TextMode) get(StyleKey.TEXT_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextVisibilityMode()
	 */
	@Override
	public TextVisibilityMode getTextVisibilityMode() {
		return (TextVisibilityMode) get(StyleKey.TEXT_VISIBILITY_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextVisibility()
	 */
	@Override
	public Values getTextVisibility() {
		return (Values) get(StyleKey.TEXT_VISIBILITY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextColor()
	 */
	@Override
	public Colors getTextColor() {
		return (Colors) get(StyleKey.TEXT_COLOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextColorCount()
	 */
	@Override
	public int getTextColorCount() {
		Colors colors = getTextColor();

		if (colors != null)
			return colors.size();

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextColor(int)
	 */
	@Override
	public Color getTextColor(int i) {
		Colors colors = getTextColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextStyle()
	 */
	@Override
	public TextStyle getTextStyle() {
		return (TextStyle) get(StyleKey.TEXT_STYLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextFont()
	 */
	@Override
	public String getTextFont() {
		return (String) get(StyleKey.TEXT_FONT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextSize()
	 */
	@Override
	public double getTextSize() {
		return (Double) get(StyleKey.TEXT_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getIconMode()
	 */
	@Override
	public IconMode getIconMode() {
		return (IconMode) get(StyleKey.ICON_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getIcon()
	 */
	@Override
	public String getIcon() {
		return (String) get(StyleKey.ICON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getVisibilityMode()
	 */
	@Override
	public VisibilityMode getVisibilityMode() {
		return (VisibilityMode) get(StyleKey.VISIBILITY_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getVisibility()
	 */
	@Override
	public Values getVisibility() {
		return (Values) get(StyleKey.VISIBILITY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getSizeMode()
	 */
	@Override
	public SizeMode getSizeMode() {
		return (SizeMode) get(StyleKey.SIZE_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getSize()
	 */
	@Override
	public Values getSize() {
		return (Values) get(StyleKey.SIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextAlignment()
	 */
	@Override
	public TextAlignment getTextAlignment() {
		return (TextAlignment) get(StyleKey.TEXT_ALIGNMENT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextBackgroundMode()
	 */
	@Override
	public TextBackgroundMode getTextBackgroundMode() {
		return (TextBackgroundMode) get(StyleKey.TEXT_BACKGROUND_MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextBackgroundColor()
	 */
	@Override
	public Colors getTextBackgroundColor() {
		return (Colors) get(StyleKey.TEXT_BACKGROUND_COLOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextBackgroundColor(int)
	 */
	@Override
	public Color getTextBackgroundColor(int i) {
		Colors colors = getTextBackgroundColor();

		if (colors != null)
			return colors.get(i);

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextOffset()
	 */
	@Override
	public Values getTextOffset() {
		return (Values) get(StyleKey.TEXT_OFFSET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getTextPadding()
	 */
	@Override
	public Values getTextPadding() {
		return (Values) get(StyleKey.TEXT_PADDING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getShape()
	 */
	@Override
	public Shape getShape() {
		return (Shape) get(StyleKey.SHAPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getSpriteOrientation()
	 */
	@Override
	public SpriteOrientation getSpriteOrientation() {
		return (SpriteOrientation) get(StyleKey.SPRITE_ORIENTATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getArrowShape()
	 */
	@Override
	public ArrowShape getArrowShape() {
		return (ArrowShape) get(StyleKey.ARROW_SHAPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getArrowImage()
	 */
	@Override
	public String getArrowImage() {
		return (String) get(StyleKey.ARROW_IMAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getArrowSize()
	 */
	@Override
	public Values getArrowSize() {
		return (Values) get(StyleKey.ARROW_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.style.GroupStyle#getZIndex()
	 */
	@Override
	public Integer getZIndex() {
		return (Integer) get(StyleKey.Z_INDEX);
	}
}
