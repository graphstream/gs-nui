/*
 * Copyright 2006 - 2014
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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UIStyle;
import org.graphstream.nui.indexer.UIElementIndex;
import org.graphstream.nui.indexer.IndexerListener;
import org.graphstream.nui.style.Selector.Target;
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
import org.graphstream.nui.style.StyleConstants.Units;
import org.graphstream.nui.style.StyleConstants.VisibilityMode;
import org.graphstream.nui.style.parser.StyleSheetParser;
import org.graphstream.nui.style.parser.StyleSheetParserListener;
import org.graphstream.util.parser.ParseException;

public class DefaultStyle extends AbstractModule implements UIStyle,
		IndexerListener, StyleSheetParserListener {
	protected static final int DEFAULT_GROW_STEP = 1000;

	protected Map<Selector, ElementStyle> styles;
	protected StyleTree sortedStyles;

	protected ElementData graphData;
	protected ElementData[] nodesData;
	protected ElementData[] edgesData;
	protected ElementData[] spritesData;

	protected UIIndexer indexer;

	protected int nodeGrowStep;
	protected int edgeGrowStep;

	protected List<StyleListener> listeners;

	public DefaultStyle() {
		super(MODULE_ID, UIIndexer.MODULE_ID);

		styles = new HashMap<Selector, ElementStyle>();
		sortedStyles = new StyleTree();

		nodeGrowStep = DEFAULT_GROW_STEP;
		edgeGrowStep = DEFAULT_GROW_STEP;

		listeners = new LinkedList<StyleListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.AbstractModule#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		super.init(ctx);

		ctx.registerUIAttribute("stylesheet", MODULE_ID);

		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		indexer.addIndexerListener(this);

		graphData = new DefaultElementData(new GraphElementIndex());

		if (indexer.getNodeCount() > 0) {
			nodesData = new ElementData[indexer.getNodeCount() + nodeGrowStep];

			for (int i = 0; i < indexer.getNodeCount(); i++)
				nodesData[i] = new DefaultElementData(indexer.getNodeIndex(i));
		} else
			nodesData = new ElementData[nodeGrowStep];

		if (indexer.getEdgeCount() > 0) {
			edgesData = new ElementData[indexer.getEdgeCount() + edgeGrowStep];

			for (int i = 0; i < indexer.getEdgeCount(); i++)
				edgesData[i] = new DefaultElementData(indexer.getEdgeIndex(i));
		} else
			edgesData = new ElementData[edgeGrowStep];

		setDefaultStyle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		indexer.removeIndexerListener(this);
		indexer = null;

		super.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		super.setAttribute(key, value);

		switch (key) {
		case "stylesheet":
			try {
				setStyleSheet(value);
			} catch (IOException | ParseException e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING,
						"cannot load the stylesheet", e);
			}

			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIStyle#getGraphStyle()
	 */
	@Override
	public ElementStyle getGraphStyle() {
		return graphData.style;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIStyle#getElementStyle(org.graphstream.nui.indexer
	 * .ElementIndex)
	 */
	@Override
	public ElementStyle getElementStyle(UIElementIndex index) {
		ElementData data = null;

		switch (index.getType()) {
		case NODE:
			data = nodesData[index.index()];
			break;
		case EDGE:
			data = edgesData[index.index()];
			break;
		case SPRITE:
			data = spritesData[index.index()];
			break;
		case GRAPH:
			data = graphData;
			break;
		}

		return getElementStyle(data);
	}

	@Override
	public void addStyleListener(StyleListener l) {
		listeners.add(l);
	}

	@Override
	public void removeStyleListener(StyleListener l) {
		listeners.remove(l);
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

	/**
	 * 
	 * @param style
	 */
	protected void fireStyleUpdated(ElementStyle style) {
		//
		// Here we try to detect elements which are concerned by the style
		// update.
		//
		// First we separate by type. Then, if selector contains an id, we try
		// to apply the style only to the target element. If there is no id, we
		// try to update all elements.
		//
		switch (style.selector.target) {
		case UNDEFINED:
			if (style.selector.id != null) {
				fireNodeStyleUpdatedById(style);
				fireEdgeStyleUpdatedById(style);
				fireSpriteStyleUpdatedById(style);
			} else {
				fireNodeStyleUpdated(style);
				fireEdgeStyleUpdated(style);
				fireSpriteStyleUpdated(style);
			}

			break;
		case NODE:
			if (style.selector.id != null)
				fireNodeStyleUpdatedById(style);
			else
				fireNodeStyleUpdated(style);

			break;
		case EDGE:
			if (style.selector.id != null)
				fireEdgeStyleUpdatedById(style);
			else
				fireEdgeStyleUpdated(style);

			break;
		case SPRITE:
			if (style.selector.id != null)
				fireSpriteStyleUpdatedById(style);
			else
				fireSpriteStyleUpdated(style);

			break;
		case GRAPH:
			graphData.checkStyleChanged();
			break;
		default:
			//
			// Some auto-procreation phenomena lead to the creation of a new
			// target type that we did not know.
			//
			break;
		}
	}

	protected void fireNodeStyleUpdatedById(ElementStyle style) {
		UIElementIndex index = indexer.getNodeIndex(style.selector.id);

		if (index != null) {
			ElementData data = nodesData[index.index()];
			data.checkStyleChanged();
		}
	}

	protected void fireNodeStyleUpdated(ElementStyle style) {
		for (int idx = 0; idx < indexer.getNodeCount(); idx++) {
			ElementData data = nodesData[idx];

			if (style == null || style.selector.match(data))
				data.checkStyleChanged();
		}
	}

	protected void fireEdgeStyleUpdatedById(ElementStyle style) {
		UIElementIndex index = indexer.getEdgeIndex(style.selector.id);

		if (index != null) {
			ElementData data = edgesData[index.index()];
			data.checkStyleChanged();
		}
	}

	protected void fireEdgeStyleUpdated(ElementStyle style) {
		for (int idx = 0; idx < indexer.getEdgeCount(); idx++) {
			ElementData data = edgesData[idx];

			if (style == null || style.selector.match(data))
				data.checkStyleChanged();
		}
	}

	protected void fireSpriteStyleUpdatedById(ElementStyle style) {
		// TODO
	}

	protected void fireSpriteStyleUpdated(ElementStyle style) {
		// TODO
	}

	protected void fireStyleCleared() {
		fireNodeStyleUpdated(null);
		fireEdgeStyleUpdated(null);
		fireSpriteStyleUpdated(null);

		graphData.checkStyleChanged();
	}

	public void setStyleSheet(Object content) throws IOException,
			ParseException {
		if (content == null)
			return;

		if (content instanceof String) {
			String styleSheetValue = (String) content;

			if (styleSheetValue.startsWith("url")) {
				//
				// Extract the part between '(' and ')'.
				//
				int beg = styleSheetValue.indexOf('(');
				int end = styleSheetValue.lastIndexOf(')');

				if (beg >= 0 && end > beg)
					styleSheetValue = styleSheetValue.substring(beg + 1, end);

				styleSheetValue = styleSheetValue.trim();

				//
				// Remove the quotes (') or (").
				//
				if (styleSheetValue.startsWith("'")) {
					beg = 0;
					end = styleSheetValue.lastIndexOf('\'');

					if (beg >= 0 && end > beg)
						styleSheetValue = styleSheetValue.substring(beg + 1,
								end);
				}

				styleSheetValue = styleSheetValue.trim();

				if (styleSheetValue.startsWith("\"")) {
					beg = 0;
					end = styleSheetValue.lastIndexOf('"');

					if (beg >= 0 && end > beg)
						styleSheetValue = styleSheetValue.substring(beg + 1,
								end);
				}

				//
				// That's it.
				//
				parse(new URL(styleSheetValue));
			} else {
				//
				// Parse from string, the value is considered to be the
				// stylesheet contents.
				//
				parseText(styleSheetValue);
			}
		} else if (content instanceof File)
			parse((File) content);
		else if (content instanceof URL)
			parse((URL) content);
		else if (content instanceof InputStream)
			parse((InputStream) content);
		else if (content instanceof Reader)
			parse((Reader) content);
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

	protected void elementDataUpdated(ElementData data) {
		for (StyleListener sl : listeners)
			sl.elementStyleUpdated(data.index, data.style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.style.parser.StyleSheetParserListener#elementStyleAdded
	 * (org.graphstream.nui.style.ElementStyle)
	 */
	@Override
	public void elementStyleAdded(ElementStyle style) {
		addStyle(style);
	}

	class DefaultElementData extends ElementData {
		public DefaultElementData(UIElementIndex index) {
			super(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.style.ElementData#elementDataUpdated()
		 */
		@Override
		protected void elementDataUpdated() {
			DefaultStyle.this.elementDataUpdated(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.style.ElementData#getUIStyle()
		 */
		@Override
		protected UIStyle getUIStyle() {
			return DefaultStyle.this;
		}

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

	protected static class StyleTree extends TreeSet<ElementStyle> {
		private static final long serialVersionUID = -5875910062336795000L;

		public StyleTree() {
			super(new ScoreComparator());
		}
	}

	protected static class GraphElementIndex implements UIElementIndex {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#id()
		 */
		@Override
		public String id() {
			return "graph";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#index()
		 */
		@Override
		public int index() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.indexer.ElementIndex#getType()
		 */
		@Override
		public Type getType() {
			return Type.GRAPH;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#nodeAdded(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	public void nodeAdded(UIElementIndex nodeIndex) {
		if (indexer.getNodeCount() >= nodesData.length)
			nodesData = Arrays.copyOf(nodesData, indexer.getNodeCount()
					+ nodeGrowStep);

		nodesData[nodeIndex.index()] = new DefaultElementData(nodeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#nodeRemoved(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	public void nodeRemoved(UIElementIndex nodeIndex) {
		nodesData[nodeIndex.index()] = null;

		if (indexer.getNodeCount() < nodesData.length / 3)
			nodesData = Arrays.copyOf(nodesData, indexer.getNodeCount()
					+ nodeGrowStep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#nodesSwapped(org.graphstream
	 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
	 */
	@Override
	public void nodesSwapped(UIElementIndex nodeIndex1,
			UIElementIndex nodeIndex2) {
		ElementData tmp = nodesData[nodeIndex1.index()];

		nodesData[nodeIndex1.index()] = nodesData[nodeIndex2.index()];
		nodesData[nodeIndex2.index()] = tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#edgeAdded(org.graphstream
	 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex,
	 * org.graphstream.nui.indexer.ElementIndex, boolean)
	 */
	@Override
	public void edgeAdded(UIElementIndex edgeIndex, UIElementIndex sourceIndex,
			UIElementIndex targetIndex, boolean directed) {
		if (indexer.getEdgeCount() >= edgesData.length)
			edgesData = Arrays.copyOf(edgesData, indexer.getEdgeCount()
					+ edgeGrowStep);

		edgesData[edgeIndex.index()] = new DefaultElementData(edgeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#edgeRemoved(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	public void edgeRemoved(UIElementIndex edgeIndex) {
		edgesData[edgeIndex.index()] = null;

		if (indexer.getEdgeCount() < edgesData.length / 3)
			edgesData = Arrays.copyOf(edgesData, indexer.getEdgeCount()
					+ edgeGrowStep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#edgesSwapped(org.graphstream
	 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
	 */
	@Override
	public void edgesSwapped(UIElementIndex edgeIndex1,
			UIElementIndex edgeIndex2) {
		ElementData tmp = edgesData[edgeIndex1.index()];

		edgesData[edgeIndex1.index()] = edgesData[edgeIndex2.index()];
		edgesData[edgeIndex2.index()] = tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.indexer.IndexerListener#elementsClear()
	 */
	@Override
	public void elementsClear() {
		nodesData = new ElementData[nodeGrowStep];
		edgesData = new ElementData[edgeGrowStep];
	}
}
