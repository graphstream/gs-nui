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
package org.graphstream.nui.style.base;

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
import org.graphstream.nui.UIAttributes;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UIStyle;
import org.graphstream.nui.UIAttributes.AttributeType;
import org.graphstream.nui.attributes.AttributeHandler;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.nui.indexer.IndexerListener;
import org.graphstream.nui.style.ElementStyle;
import org.graphstream.nui.style.GroupStyle;
import org.graphstream.nui.style.Selector;
import org.graphstream.nui.style.StyleListener;
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
import org.graphstream.nui.style.util.Colors;
import org.graphstream.nui.style.util.Value;
import org.graphstream.nui.style.util.Values;
import org.graphstream.nui.util.Tools;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.util.parser.ParseException;

public class BaseStyle extends AbstractModule implements UIStyle,
		IndexerListener, StyleSheetParserListener {
	protected static final int DEFAULT_GROW_STEP = 1000;

	protected Map<Selector, BaseGroupStyle> styles;
	protected StyleTree sortedStyles;
	protected ZIndexTree zIndexTree;

	protected BaseElementStyle graphData;
	protected BaseElementStyle[] nodeDatas;
	protected BaseElementStyle[] edgeDatas;
	protected BaseElementStyle[] spritesData;

	protected int[] nodeColors;
	protected int[] edgeColors;

	protected UIIndexer indexer;

	protected int nodeGrowStep;
	protected int edgeGrowStep;

	protected List<StyleListener> listeners;

	protected AttributeHandler stylesheetHandler;
	protected AttributeHandler uiColorHandler;

	public BaseStyle() {
		super(MODULE_ID, UIIndexer.MODULE_ID, UIAttributes.MODULE_ID);

		styles = new HashMap<Selector, BaseGroupStyle>();
		sortedStyles = new StyleTree();
		zIndexTree = new ZIndexTree();

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

		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);
		indexer.addIndexerListener(this);

		graphData = new DefaultGraphStyle(indexer.getGraphIndex());

		if (indexer.getNodeCount() > 0) {
			nodeDatas = new BaseElementStyle[indexer.getNodeCount()
					+ nodeGrowStep];
			nodeColors = new int[indexer.getNodeCount() + nodeGrowStep];

			for (int i = 0; i < indexer.getNodeCount(); i++)
				nodeDatas[i] = new DefaultNodeStyle(indexer.getNodeIndex(i));
		} else {
			nodeDatas = new BaseElementStyle[nodeGrowStep];
			nodeColors = new int[nodeGrowStep];
		}

		if (indexer.getEdgeCount() > 0) {
			edgeDatas = new BaseElementStyle[indexer.getEdgeCount()
					+ edgeGrowStep];
			edgeColors = new int[indexer.getEdgeCount() + edgeGrowStep];

			for (int i = 0; i < indexer.getEdgeCount(); i++)
				edgeDatas[i] = new DefaultEdgeStyle(indexer.getEdgeIndex(i));
		} else {
			edgeDatas = new BaseElementStyle[edgeGrowStep];
			edgeColors = new int[edgeGrowStep];
		}

		setDefaultStyle();

		UIAttributes attributes = (UIAttributes) ctx
				.getModule(UIAttributes.MODULE_ID);

		stylesheetHandler = new AttributeHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.graphstream.nui.attributes.AttributeHandler#
			 * handleAttribute(org.graphstream.nui.indexer.ElementIndex,
			 * java.lang.String, java.lang.Object)
			 */
			@Override
			public void handleAttribute(ElementIndex index, String attributeId,
					Object value) {
				try {
					setStyleSheet(value);
				} catch (IOException | ParseException e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING,
							"cannot load the stylesheet", e);
				}
			}
		};

		uiColorHandler = new AttributeHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.graphstream.nui.attributes.AttributeHandler#handleAttribute
			 * (org.graphstream.nui.indexer.ElementIndex, java.lang.String,
			 * java.lang.Object)
			 */
			@Override
			public void handleAttribute(ElementIndex index, String attributeId,
					Object value) {
				BaseElementStyle style = null;

				switch (index.getType()) {
				case NODE:
					style = nodeDatas[index.index()];
					break;
				case EDGE:
					style = edgeDatas[index.index()];
					break;
				default:
					break;
				}

				if (style == null)
					return;

				style.setUIColor(Tools.checkAndGetDouble(value));
			}
		};

		attributes.registerUIAttributeHandler(AttributeType.GRAPH,
				"stylesheet", stylesheetHandler);
		attributes.registerUIAttributeHandler(AttributeType.ELEMENT, "color",
				uiColorHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		UIAttributes attributes = (UIAttributes) ctx
				.getModule(UIAttributes.MODULE_ID);
		attributes.unregisterUIAttributeHandler(AttributeType.GRAPH,
				"stylesheet", stylesheetHandler);
		attributes.unregisterUIAttributeHandler(AttributeType.ELEMENT, "color",
				uiColorHandler);

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
		return graphData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIStyle#getElementStyle(org.graphstream.nui.indexer
	 * .ElementIndex)
	 */
	@Override
	public ElementStyle getElementStyle(ElementIndex index) {
		BaseElementStyle data = null;

		switch (index.getType()) {
		case NODE:
			data = nodeDatas[index.index()];
			break;
		case EDGE:
			data = edgeDatas[index.index()];
			break;
		case SPRITE:
			data = spritesData[index.index()];
			break;
		case GRAPH:
			data = graphData;
			break;
		}

		assert data != null;
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIStyle#getRenderingOrder()
	 */
	@Override
	public Iterator<ElementIndex> getRenderingOrder() {
		return zIndexTree.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIStyle#addStyleListener(org.graphstream.nui.style
	 * .StyleListener)
	 */
	@Override
	public void addStyleListener(StyleListener l) {
		listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIStyle#removeStyleListener(org.graphstream.nui.style
	 * .StyleListener)
	 */
	@Override
	public void removeStyleListener(StyleListener l) {
		listeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.UIStyle#getGroupStyle(org.graphstream.nui.style.
	 * ElementStyle)
	 */
	@Override
	public GroupStyle searchGroupStyle(ElementStyle data) {
		Iterator<BaseGroupStyle> it = sortedStyles.descendingIterator();

		while (it.hasNext()) {
			BaseGroupStyle es = it.next();

			if (es.selector.match(data))
				return es;
		}

		throw new RuntimeException("this should not happen");
	}

	public void addStyle(BaseGroupStyle style) {
		Selector s = style.selector;
		BaseGroupStyle exists = styles.get(s);

		if (exists != null) {
			exists.merge(style);
			style = exists;
		} else {
			styles.put(s, style);
			sortedStyles.add(style);

			if (s.hasState()) {
				Selector noState = s.getNoStateSelector();
				BaseGroupStyle papa = styles.get(noState);

				if (papa == null) {
					papa = new BaseGroupStyle(noState);
					addStyle(papa);
				}

				style.setParent(papa);
			} else {
				BaseGroupStyle parent = findParent(style);

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

	protected BaseGroupStyle findParent(BaseGroupStyle style) {
		BaseGroupStyle parent = null;
		Selector select = style.selector;

		for (BaseGroupStyle s : styles.values()) {
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

	protected void checkChildren(BaseGroupStyle style) {
		Selector select = style.selector;

		for (BaseGroupStyle s : styles.values()) {
			if (select.isParent(s.selector)
					&& (s.parent() == null || s.parent().selector().score < select.score)) {
				s.setParent(style);
				fireStyleUpdated(s);
			}
		}
	}

	/**
	 * 
	 * @param style
	 */
	protected void fireStyleUpdated(BaseGroupStyle style) {
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

	protected void fireNodeStyleUpdatedById(BaseGroupStyle style) {
		ElementIndex index = indexer.getNodeIndex(style.selector.id);

		if (index != null) {
			ElementStyle data = nodeDatas[index.index()];
			data.checkStyleChanged();
		}
	}

	protected void fireNodeStyleUpdated(BaseGroupStyle style) {
		for (int idx = 0; idx < indexer.getNodeCount(); idx++) {
			BaseElementStyle data = nodeDatas[idx];

			if (style == null || style.selector.match(data))
				data.checkStyleChanged();
		}
	}

	protected void fireEdgeStyleUpdatedById(BaseGroupStyle style) {
		ElementIndex index = indexer.getEdgeIndex(style.selector.id);

		if (index != null) {
			ElementStyle data = edgeDatas[index.index()];
			data.checkStyleChanged();
		}
	}

	protected void fireEdgeStyleUpdated(BaseGroupStyle style) {
		for (int idx = 0; idx < indexer.getEdgeCount(); idx++) {
			BaseElementStyle data = edgeDatas[idx];

			if (style == null || style.selector.match(data))
				data.checkStyleChanged();
		}
	}

	protected void fireSpriteStyleUpdatedById(GroupStyle style) {
		// TODO
	}

	protected void fireSpriteStyleUpdated(GroupStyle style) {
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
	public Iterator<BaseGroupStyle> iterator() {
		return sortedStyles.descendingIterator();
	}

	protected void setDefaultStyle() {
		Selector s = new Selector(Target.UNDEFINED, null, null, null);
		BaseGroupStyle defaultStyle = new BaseGroupStyle(s);

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
		BaseGroupStyle defaultStyle = new BaseGroupStyle(s);

		addStyle(defaultStyle);
	}

	protected void setDefaultEdgeStyle() {
		Selector s = new Selector(Target.EDGE, null, null, null);
		BaseGroupStyle defaultStyle = new BaseGroupStyle(s);

		addStyle(defaultStyle);
	}

	protected void setDefaultGraphStyle() {
		Selector s = new Selector(Target.GRAPH, null, null, null);
		BaseGroupStyle defaultStyle = new BaseGroupStyle(s);

		addStyle(defaultStyle);
	}

	protected void setDefaultSpriteStyle() {
		Selector s = new Selector(Target.SPRITE, null, null, null);
		BaseGroupStyle defaultStyle = new BaseGroupStyle(s);

		addStyle(defaultStyle);
	}

	protected void elementStyleUpdated(BaseElementStyle data) {
		//
		// Maybe the z-index of this element was updated...
		//
		if (data.index.getType() != ElementIndex.Type.GRAPH) {
			zIndexTree.remove(data.index);
			zIndexTree.add(data.index);
		}

		//
		// Maybe the color changes...
		//
		switch (data.index.getType()) {
		case NODE:
			nodeColors[data.index.index()] = data.computeColor();
			break;
		case EDGE:
			edgeColors[data.index.index()] = data.computeColor();
			break;
		default:
			break;
		}

		//
		// Tell the world that something changed...
		//
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
	public void elementStyleAdded(BaseGroupStyle style) {
		addStyle(style);
	}

	abstract class DefaultElementStyle extends BaseElementStyle {
		public DefaultElementStyle(ElementIndex index) {
			super(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.style.ElementData#elementDataUpdated()
		 */
		@Override
		protected void elementStyleUpdated() {
			BaseStyle.this.elementStyleUpdated(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.style.ElementData#getUIStyle()
		 */
		@Override
		protected UIStyle getUIStyle() {
			return BaseStyle.this;
		}
	}

	class DefaultNodeStyle extends DefaultElementStyle {
		public DefaultNodeStyle(ElementIndex index) {
			super(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.style.ElementStyle#getColor()
		 */
		@Override
		public int getColor() {
			return nodeColors[index.index()];
		}
	}

	class DefaultEdgeStyle extends DefaultElementStyle {
		public DefaultEdgeStyle(ElementIndex index) {
			super(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.style.ElementStyle#getColor()
		 */
		@Override
		public int getColor() {
			return edgeColors[index.index()];
		}
	}

	class DefaultGraphStyle extends DefaultElementStyle {
		public DefaultGraphStyle(ElementIndex index) {
			super(index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.nui.style.ElementStyle#getColor()
		 */
		@Override
		public int getColor() {
			return 0;
		}
	}

	protected static class ScoreComparator implements
			Comparator<BaseGroupStyle> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(BaseGroupStyle arg0, BaseGroupStyle arg1) {
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

	protected static class StyleTree extends TreeSet<BaseGroupStyle> {
		private static final long serialVersionUID = -5875910062336795000L;

		public StyleTree() {
			super(new ScoreComparator());
		}
	}

	protected class ZIndexComparator implements Comparator<ElementIndex> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ElementIndex o1, ElementIndex o2) {
			int z1 = 0, z2 = 0, t1 = o1.getType().ordinal(), t2 = o2.getType()
					.ordinal();

			switch (o1.getType()) {
			case NODE:
				z1 = nodeDatas[o1.index()].style.getZIndex();
				break;
			case EDGE:
				z1 = edgeDatas[o1.index()].style.getZIndex();
				break;
			case SPRITE:
				break;
			default:
				z1 = 0;
			}

			switch (o2.getType()) {
			case NODE:
				z2 = nodeDatas[o2.index()].style.getZIndex();
				break;
			case EDGE:
				z2 = edgeDatas[o2.index()].style.getZIndex();
				break;
			case SPRITE:
				break;
			default:
				z2 = 0;
			}

			//
			// TreeSet needs that elements are all different, so we need some
			// more work.
			//
			if (z1 == z2) {
				//
				// If types are equals, we can use id comparison.
				//
				if (t1 == t2)
					return o1.id().compareTo(o2.id());
				//
				// Else we just use the ordinal of the type as order.
				//
				else
					return Integer.compare(t1, t2);
			} else
				return Integer.compare(z1, z2);
		}
	}

	@SuppressWarnings("serial")
	protected class ZIndexTree extends TreeSet<ElementIndex> {
		public ZIndexTree() {
			super(new ZIndexComparator());
		}
	}

	class ElementAttributeListener extends SinkAdapter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#nodeAttributeAdded(java.lang.String
		 * , long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		@Override
		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attributeId, Object value) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#nodeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attributeId, Object oldValue,
				Object newValue) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#nodeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attributeId) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#edgeAttributeAdded(java.lang.String
		 * , long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		@Override
		public void edgeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attributeId, Object value) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#edgeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attributeId, Object oldValue,
				Object newValue) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#edgeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attributeId) {

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
	public void nodeAdded(ElementIndex nodeIndex) {
		if (indexer.getNodeCount() >= nodeDatas.length) {
			nodeDatas = Arrays.copyOf(nodeDatas, indexer.getNodeCount()
					+ nodeGrowStep);
			nodeColors = Arrays.copyOf(nodeColors, indexer.getNodeCount()
					+ nodeGrowStep);
		}

		nodeDatas[nodeIndex.index()] = new DefaultNodeStyle(nodeIndex);
		nodeDatas[nodeIndex.index()].checkStyleChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#nodeRemoved(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	public void nodeRemoved(ElementIndex nodeIndex) {
		zIndexTree.remove(nodeIndex);
		nodeDatas[nodeIndex.index()] = null;

		if (indexer.getNodeCount() < nodeDatas.length / 3) {
			nodeDatas = Arrays.copyOf(nodeDatas, indexer.getNodeCount()
					+ nodeGrowStep);
			nodeColors = Arrays.copyOf(nodeColors, indexer.getNodeCount()
					+ nodeGrowStep);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#nodesSwapped(org.graphstream
	 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
	 */
	@Override
	public void nodesSwapped(ElementIndex nodeIndex1, ElementIndex nodeIndex2) {
		BaseElementStyle tmp = nodeDatas[nodeIndex1.index()];

		nodeDatas[nodeIndex1.index()] = nodeDatas[nodeIndex2.index()];
		nodeDatas[nodeIndex2.index()] = tmp;

		int itmp = nodeColors[nodeIndex1.index()];

		nodeColors[nodeIndex1.index()] = nodeColors[nodeIndex2.index()];
		nodeColors[nodeIndex2.index()] = itmp;
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
	public void edgeAdded(ElementIndex edgeIndex, ElementIndex sourceIndex,
			ElementIndex targetIndex, boolean directed) {
		if (indexer.getEdgeCount() >= edgeDatas.length) {
			edgeDatas = Arrays.copyOf(edgeDatas, indexer.getEdgeCount()
					+ edgeGrowStep);
			edgeColors = Arrays.copyOf(edgeColors, indexer.getEdgeCount()
					+ edgeGrowStep);
		}

		edgeDatas[edgeIndex.index()] = new DefaultEdgeStyle(edgeIndex);
		edgeDatas[edgeIndex.index()].checkStyleChanged();
		// zIndexTree.add(edgeIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#edgeRemoved(org.graphstream
	 * .nui.indexer.ElementIndex)
	 */
	@Override
	public void edgeRemoved(ElementIndex edgeIndex) {
		zIndexTree.remove(edgeIndex);
		edgeDatas[edgeIndex.index()] = null;

		if (indexer.getEdgeCount() < edgeDatas.length / 3) {
			edgeDatas = Arrays.copyOf(edgeDatas, indexer.getEdgeCount()
					+ edgeGrowStep);
			edgeColors = Arrays.copyOf(edgeColors, indexer.getEdgeCount()
					+ edgeGrowStep);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.indexer.IndexerListener#edgesSwapped(org.graphstream
	 * .nui.indexer.ElementIndex, org.graphstream.nui.indexer.ElementIndex)
	 */
	@Override
	public void edgesSwapped(ElementIndex edgeIndex1, ElementIndex edgeIndex2) {
		BaseElementStyle tmp = edgeDatas[edgeIndex1.index()];

		edgeDatas[edgeIndex1.index()] = edgeDatas[edgeIndex2.index()];
		edgeDatas[edgeIndex2.index()] = tmp;

		int itmp = edgeColors[edgeIndex1.index()];

		edgeColors[edgeIndex1.index()] = edgeColors[edgeIndex2.index()];
		edgeColors[edgeIndex2.index()] = itmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.indexer.IndexerListener#elementsClear()
	 */
	@Override
	public void elementsClear() {
		nodeDatas = new BaseElementStyle[nodeGrowStep];
		edgeDatas = new BaseElementStyle[edgeGrowStep];
		zIndexTree.clear();
	}
}
