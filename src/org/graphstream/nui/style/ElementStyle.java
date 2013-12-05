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

import java.util.EnumMap;

public class ElementStyle {
	public static enum Style {
		FILL_MODE, FILL_COLOR, FILL_IMAGE, STROKE_MODE, STROKE_COLOR, STROKE_WIDTH, PADDING, SHAPE, SIZE, SIZE_MODE, Z_INDEX, SHADOW_MODE, SHADOW_COLOR, SHADOW_OFFSET, SHADOW_WIDTH, TEXT_MODE, TEXT_BACKGROUND_MODE, TEXT_BACKGROUND_COLOR, TEXT_VISIBILITY_MODE, TEXT_VISIBILITY, TEXT_COLOR, TEXT_STYLE, TEXT_ALIGNMENT, TEXT_PADDING, TEXT_OFFSET, TEXT_FONT, TEXT_SIZE, ICON, ICON_MODE, VISIBILITY, VISIBILITY_MODE, ARROW_SHAPE, ARROW_IMAGE, ARROW_SIZE, SPRITE_ORIENTATION
	}

	protected Selector selector;
	protected EnumMap<Style, Object> styles;

	public ElementStyle() {
		this(new Selector());
	}

	public ElementStyle(Selector selector) {
		this.styles = new EnumMap<Style, Object>(Style.class);
		this.selector = selector;
	}

	public void addStyle(Style style, Object data) {
		styles.put(style, data);
	}

	public Iterable<Style> getStyles() {
		return styles.keySet();
	}

	public Object getStyleData(Style style) {
		return styles.get(style);
	}
}
