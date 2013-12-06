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

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.graphstream.nui.data.ElementData;
import org.graphstream.nui.style.Colors;
import org.graphstream.nui.style.ElementStyle;
import org.graphstream.nui.style.Selector;
import org.graphstream.nui.style.StyleConstants;
import org.graphstream.nui.style.Value;
import org.graphstream.nui.style.Values;
import org.graphstream.nui.style.Selector.Target;
import org.graphstream.nui.style.parser.StyleSheetParser;
import org.graphstream.util.parser.ParseException;

public class UIStyleSheet implements StyleConstants, Iterable<ElementStyle> {
	HashMap<Selector, ElementStyle> styles;
	TreeSet<ElementStyle> sortedStyles;

	public UIStyleSheet() {
		styles = new HashMap<Selector, ElementStyle>();
		sortedStyles = new TreeSet<ElementStyle>(new ScoreComparator());

		setDefaultStyle();
	}

	public void init(Viewer viewer) {

	}

	public ElementStyle getElementStyle(ElementData data) {
		Iterator<ElementStyle> it = sortedStyles.descendingIterator();

		while (it.hasNext()) {
			ElementStyle es = it.next();

			if (es.selector.match(data))
				return es;
		}

		throw new RuntimeException("this should not happen");
	}

	public void addStyle(ElementStyle style) {
		Selector s = style.selector;
		ElementStyle exists = styles.get(s);

		if (exists != null) {
			exists.merge(style);
			style = exists;
		} else {
			styles.put(s, style);
			sortedStyles.add(style);

			if (s.hasState()) {
				Selector noState = s.getNoStateSelector();
				ElementStyle papa = styles.get(noState);

				if (papa == null) {
					papa = new ElementStyle(noState);
					addStyle(papa);
				}

				style.setParent(papa);
			} else {
				ElementStyle parent = findParent(style);

				if (parent != null)
					style.setParent(parent);

				checkChildren(style);
			}
		}

		fireStyleUpdated(style);
	}

	public void clear() {
		styles.clear();
		sortedStyles.clear();

		setDefaultStyle();

		fireStyleCleared();
	}

	protected ElementStyle findParent(ElementStyle style) {
		ElementStyle parent = null;
		Selector select = style.selector;

		for (ElementStyle s : styles.values()) {
			if (s.selector.isParent(select)) {
				if (parent == null)
					parent = s;
				else {
					if (s.selector.score > parent.selector.score)
						parent = s;
				}
			}
		}

		return parent;
	}

	protected void checkChildren(ElementStyle style) {
		Selector select = style.selector;

		for (ElementStyle s : styles.values()) {
			if (select.isParent(s.selector)
					&& (s.parent() == null || s.parent().selector.score < select.score)) {
				s.setParent(style);
				fireStyleUpdated(s);
			}
		}
	}

	protected void fireStyleUpdated(ElementStyle style) {
		// TODO
		switch (style.selector.target) {
		case UNDEFINED:
			break;
		case NODE:
			if (style.selector.id != null) {

			}
			break;
		case EDGE:
			break;
		case SPRITE:
			break;
		case GRAPH:
			break;
		default:
			break;
		}
	}

	protected void fireStyleCleared() {

	}

	public void setStyleSheet(Object content) {
		
	}
	
	public void parseText(String content) throws ParseException {
		StringReader sr = new StringReader(content);
		StyleSheetParser parser = new StyleSheetParser(this, sr);

		parser.start();
	}

	public void parse(File file) throws IOException, ParseException {
		FileReader reader = new FileReader(file);

		try {
			parse(reader);
		} finally {
			reader.close();
		}
	}

	public void parse(URL url) throws IOException, ParseException {
		InputStream stream = url.openStream();

		try {
			parse(stream);
		} finally {
			stream.close();
		}
	}

	public void parse(InputStream stream) throws IOException, ParseException {
		StyleSheetParser parser = new StyleSheetParser(this, stream);
		parser.start();
	}

	public void parse(Reader reader) throws IOException, ParseException {
		StyleSheetParser parser = new StyleSheetParser(this, reader);
		parser.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ElementStyle> iterator() {
		return sortedStyles.descendingIterator();
	}

	protected void setDefaultStyle() {
		Selector s = new Selector(Target.UNDEFINED, null, null, null);
		ElementStyle defaultStyle = new ElementStyle(s);

		Colors fillColor = new Colors();
		Colors strokeColor = new Colors();
		Colors shadowColor = new Colors();
		Colors textColor = new Colors();
		Colors canvasColor = new Colors();
		Colors textBgColor = new Colors();

		fillColor.add(Color.BLACK);
		strokeColor.add(Color.BLACK);
		shadowColor.add(Color.GRAY);
		textColor.add(Color.BLACK);
		canvasColor.add(Color.WHITE);
		textBgColor.add(Color.WHITE);

		defaultStyle.set(StyleKey.Z_INDEX, 0);

		defaultStyle.set(StyleKey.FILL_MODE, FillMode.PLAIN);
		defaultStyle.set(StyleKey.FILL_COLOR, fillColor);
		defaultStyle.set(StyleKey.FILL_IMAGE, null);

		defaultStyle.set(StyleKey.STROKE_MODE, StrokeMode.NONE);
		defaultStyle.set(StyleKey.STROKE_COLOR, strokeColor);
		defaultStyle.set(StyleKey.STROKE_WIDTH, new Value(Units.PX, 1));

		defaultStyle.set(StyleKey.SHADOW_MODE, ShadowMode.NONE);
		defaultStyle.set(StyleKey.SHADOW_COLOR, shadowColor);
		defaultStyle.set(StyleKey.SHADOW_WIDTH, new Value(Units.PX, 3));
		defaultStyle.set(StyleKey.SHADOW_OFFSET, new Values(new Value(Units.PX,
				3), new Value(Units.PX, 3)));

		defaultStyle.set(StyleKey.PADDING, new Values(new Value(Units.PX, 0),
				new Value(Units.PX, 0), new Value(Units.PX, 0)));

		defaultStyle.set(StyleKey.TEXT_MODE, TextMode.NORMAL);
		defaultStyle.set(StyleKey.TEXT_COLOR, textColor);
		defaultStyle.set(StyleKey.TEXT_STYLE, TextStyle.NORMAL);
		defaultStyle.set(StyleKey.TEXT_FONT, "default");
		defaultStyle.set(StyleKey.TEXT_SIZE, new Value(Units.PX, 10));
		defaultStyle.set(StyleKey.TEXT_ALIGNMENT, TextAlignment.CENTER);
		defaultStyle
				.set(StyleKey.TEXT_BACKGROUND_MODE, TextBackgroundMode.NONE);
		defaultStyle.set(StyleKey.TEXT_BACKGROUND_COLOR, textBgColor);
		defaultStyle.set(StyleKey.TEXT_OFFSET, new Values(
				new Value(Units.PX, 0), new Value(Units.PX, 0)));
		defaultStyle.set(StyleKey.TEXT_PADDING, new Values(new Value(Units.PX,
				0), new Value(Units.PX, 0)));
		defaultStyle.set(StyleKey.TEXT_VISIBILITY, null);
		defaultStyle.set(StyleKey.TEXT_VISIBILITY_MODE,
				TextVisibilityMode.NORMAL);

		defaultStyle.set(StyleKey.ICON_MODE, IconMode.NONE);
		defaultStyle.set(StyleKey.ICON, null);

		defaultStyle.set(StyleKey.VISIBILITY_MODE, VisibilityMode.NORMAL);
		defaultStyle.set(StyleKey.VISIBILITY, null);

		defaultStyle.set(StyleKey.SIZE_MODE, SizeMode.NORMAL);
		defaultStyle.set(StyleKey.SIZE, new Values(new Value(Units.PX, 10),
				new Value(Units.PX, 10), new Value(Units.PX, 10)));

		defaultStyle.set(StyleKey.SHAPE, Shape.CIRCLE);
		// defaultStyle.set(StyleKey.SHAPE_POINTS, null);
		// defaultStyle.set(StyleKey.JCOMPONENT, null);

		defaultStyle.set(StyleKey.SPRITE_ORIENTATION, SpriteOrientation.NONE);

		defaultStyle.set(StyleKey.ARROW_SHAPE, ArrowShape.ARROW);
		defaultStyle.set(StyleKey.ARROW_IMAGE, null);
		defaultStyle.set(StyleKey.ARROW_SIZE, new Values(
				new Value(Units.PX, 8), new Value(Units.PX, 4)));

		defaultStyle.set(StyleKey.CANVAS_COLOR, canvasColor);

		addStyle(defaultStyle);

		setDefaultGraphStyle();
		setDefaultNodeStyle();
		setDefaultEdgeStyle();
		setDefaultSpriteStyle();
	}

	protected void setDefaultNodeStyle() {
		Selector s = new Selector(Target.NODE, null, null, null);
		ElementStyle defaultStyle = new ElementStyle(s);

		addStyle(defaultStyle);
	}

	protected void setDefaultEdgeStyle() {
		Selector s = new Selector(Target.EDGE, null, null, null);
		ElementStyle defaultStyle = new ElementStyle(s);

		addStyle(defaultStyle);
	}

	protected void setDefaultGraphStyle() {
		Selector s = new Selector(Target.GRAPH, null, null, null);
		ElementStyle defaultStyle = new ElementStyle(s);

		addStyle(defaultStyle);
	}

	protected void setDefaultSpriteStyle() {
		Selector s = new Selector(Target.SPRITE, null, null, null);
		ElementStyle defaultStyle = new ElementStyle(s);

		addStyle(defaultStyle);
	}

	protected static class ScoreComparator implements Comparator<ElementStyle> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(ElementStyle arg0, ElementStyle arg1) {
			int r = Integer.compare(arg0.selector.score, arg1.selector.score);

			if (r != 0)
				return r;

			//
			// So bad, score are equal and TreeSet does not like that.
			//

			r = Integer.compare(arg0.selector.partsCount(),
					arg1.selector.partsCount());

			if (r != 0)
				return r;

			//
			// These selectors are really annoying...
			//

			r = Integer.compare(arg0.selector.toString().length(),
					arg1.selector.toString().length());

			if (r != 0)
				return r;

			//
			// Ok, it sucks, drop it.
			//

			return -1;
		}
	}
}
