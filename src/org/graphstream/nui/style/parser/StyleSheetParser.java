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
package org.graphstream.nui.style.parser;

import java.awt.Color;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import org.graphstream.nui.style.*;
import org.graphstream.nui.style.base.BaseGroupStyle;
import org.graphstream.nui.style.util.ColorConverter;
import org.graphstream.nui.style.util.Colors;
import org.graphstream.nui.style.util.Value;
import org.graphstream.nui.style.util.Values;
import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.SimpleCharStream;
import org.graphstream.util.parser.Token;
import org.graphstream.util.parser.TokenMgrError;

@SuppressWarnings("unused")
public class StyleSheetParser implements StyleConstants,
		StyleSheetParserConstants {
	/**
	 * The style sheet.
	 */
	protected StyleSheetParserListener listener;

	public StyleSheetParser(StyleSheetParserListener listener,
			InputStream stream) {
		this(stream);
		this.listener = listener;
	}

	public StyleSheetParser(StyleSheetParserListener listener, Reader stream) {
		this(stream);
		this.listener = listener;
	}

	public static class Number {
		public float value;
		public Units units = Units.PX;

		public Number(float value, Units units) {
			this.value = value;
			this.units = units;
		}
	}

	/**
	 * Closes the parser, closing the opened stream.
	 */
	public void close() throws IOException {
		jj_input_stream.close();
	}

	/*
	 * The parser.
	 */
	final public void start() throws ParseException {
		label_1: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case GRAPH:
			case EDGE:
			case NODE:
			case SPRITE:
			case COMMENT:
				;
				break;
			default:
				jj_la1[0] = jj_gen;
				break label_1;
			}
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case GRAPH:
			case EDGE:
			case NODE:
			case SPRITE:
				rule();
				break;
			case COMMENT:
				jj_consume_token(COMMENT);
				break;
			default:
				jj_la1[1] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		jj_consume_token(0);
	}

	final public void rule() throws ParseException {
		Selector select;
		BaseGroupStyle rule;
		select = select();
		rule = new BaseGroupStyle(select);
		jj_consume_token(LBRACE);
		styles(rule);
		jj_consume_token(RBRACE);
		listener.elementStyleAdded(rule);
	}

	final public void styles(GroupStyle rule) throws ParseException {
		label_2: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case FILLMODE:
			case FILLCOLOR:
			case FILLIMAGE:
			case STROKEMODE:
			case STROKECOLOR:
			case STROKEWIDTH:
			case SHADOWMODE:
			case SHADOWCOLOR:
			case SHADOWWIDTH:
			case SHADOWOFFSET:
			case TEXTMODE:
			case TEXTCOLOR:
			case TEXTSTYLE:
			case TEXTFONT:
			case TEXTSIZE:
			case TEXTVISIBILITYMODE:
			case TEXTVISIBILITY:
			case TEXTBACKGROUNDMODE:
			case TEXTBACKGROUNDCOLOR:
			case TEXTOFFSET:
			case TEXTPADDING:
			case ICONMODE:
			case ICON:
			case PADDING:
			case ZINDEX:
			case VISIBILITYMODE:
			case VISIBILITY:
			case SHAPE:
			case SIZE:
			case SIZEMODE:
			case SHAPEPOINTS:
			case TEXTALIGNMENT:
			case JCOMPONENT:
			case ARROWIMGURL:
			case ARROWSIZE:
			case ARROWSHAPE:
			case SPRITEORIENT:
			case CANVASCOLOR:
			case COMMENT:
				;
				break;
			default:
				jj_la1[2] = jj_gen;
				break label_2;
			}
			style(rule);
		}
	}

	final public Selector select() throws ParseException {
		Token t;

		Selector.Target target = Selector.Target.UNDEFINED;
		String id = null;
		LinkedList<String> uiClass = new LinkedList<String>();
		String state = null;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case GRAPH:
			jj_consume_token(GRAPH);
			target = Selector.Target.GRAPH;
			break;
		case NODE:
			jj_consume_token(NODE);
			target = Selector.Target.NODE;
			break;
		case EDGE:
			jj_consume_token(EDGE);
			target = Selector.Target.EDGE;
			break;
		case SPRITE:
			jj_consume_token(SPRITE);
			target = Selector.Target.SPRITE;
			break;
		default:
			jj_la1[3] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case SHARP:
			jj_consume_token(SHARP);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case IDENTIFIER:
				t = jj_consume_token(IDENTIFIER);
				id = t.image;
				break;
			case STRING:
				t = jj_consume_token(STRING);
				id = t.image.substring(1, t.image.length() - 1);
				break;
			default:
				jj_la1[4] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
			break;
		default:
			jj_la1[5] = jj_gen;
			;
		}
		label_3: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case DOT:
				;
				break;
			default:
				jj_la1[6] = jj_gen;
				break label_3;
			}
			jj_consume_token(DOT);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case IDENTIFIER:
				t = jj_consume_token(IDENTIFIER);
				uiClass.add(t.image);
				break;
			case CLICKED:
				t = jj_consume_token(CLICKED);
				uiClass.add("clicked");
				break;
			case SELECTED:
				t = jj_consume_token(SELECTED);
				uiClass.add("selected");
				break;
			case STRING:
				t = jj_consume_token(STRING);
				uiClass.add(t.image.substring(1, t.image.length() - 1));
				break;
			default:
				jj_la1[7] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
		}
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case COLON:
			jj_consume_token(COLON);
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case CLICKED:
				jj_consume_token(CLICKED);
				state = "clicked";
				break;
			case SELECTED:
				jj_consume_token(SELECTED);
				state = "selected";
				break;
			case STRING:
				t = jj_consume_token(STRING);
				state = t.image.substring(1, t.image.length() - 1);
				break;
			case IDENTIFIER:
				t = jj_consume_token(IDENTIFIER);
				state = t.image;
				break;
			default:
				jj_la1[8] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
			break;
		default:
			jj_la1[9] = jj_gen;
			;
		}
		{
			if (true)
				return new Selector(target, id,
						uiClass.toArray(new String[uiClass.size()]), state);
		}
		throw new Error("Missing return statement in function");
	}

	final public void style(GroupStyle style) throws ParseException {
		Color color;
		Colors colors;
		String url;
		Value value;
		Values values;
		FillMode fillMode;
		StrokeMode strokeMode;
		ShadowMode shadowMode;
		TextMode textMode;
		TextVisibilityMode textVisMode;
		TextBackgroundMode textBgMode;
		TextStyle textStyle;
		TextAlignment textAlignment;
		IconMode iconMode;
		VisibilityMode visMode;
		SizeMode sizeMode;
		Shape shape;
		SpriteOrientation spriteOrient;
		ArrowShape arrowShape;
		JComponents component;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case COMMENT:
			jj_consume_token(COMMENT);
			break;
		case ZINDEX:
			jj_consume_token(ZINDEX);
			jj_consume_token(COLON);
			value = value();
			jj_consume_token(SEMICOLON);
			style.set("z-index", new Integer((int) value.value));
			break;
		case FILLMODE:
			jj_consume_token(FILLMODE);
			jj_consume_token(COLON);
			fillMode = fillMode();
			jj_consume_token(SEMICOLON);
			style.set("fill-mode", fillMode);
			break;
		case FILLCOLOR:
			jj_consume_token(FILLCOLOR);
			jj_consume_token(COLON);
			colors = colors();
			jj_consume_token(SEMICOLON);
			style.set("fill-color", colors);
			break;
		case FILLIMAGE:
			jj_consume_token(FILLIMAGE);
			jj_consume_token(COLON);
			url = url();
			jj_consume_token(SEMICOLON);
			style.set("fill-image", url);
			break;
		case STROKEMODE:
			jj_consume_token(STROKEMODE);
			jj_consume_token(COLON);
			strokeMode = strokeMode();
			jj_consume_token(SEMICOLON);
			style.set("stroke-mode", strokeMode);
			break;
		case STROKECOLOR:
			jj_consume_token(STROKECOLOR);
			jj_consume_token(COLON);
			colors = colors();
			jj_consume_token(SEMICOLON);
			style.set("stroke-color", colors);
			break;
		case STROKEWIDTH:
			jj_consume_token(STROKEWIDTH);
			jj_consume_token(COLON);
			value = value();
			jj_consume_token(SEMICOLON);
			style.set("stroke-width", value);
			break;
		case SHADOWMODE:
			jj_consume_token(SHADOWMODE);
			jj_consume_token(COLON);
			shadowMode = shadowMode();
			jj_consume_token(SEMICOLON);
			style.set("shadow-mode", shadowMode);
			break;
		case SHADOWCOLOR:
			jj_consume_token(SHADOWCOLOR);
			jj_consume_token(COLON);
			colors = colors();
			jj_consume_token(SEMICOLON);
			style.set("shadow-color", colors);
			break;
		case SHADOWWIDTH:
			jj_consume_token(SHADOWWIDTH);
			jj_consume_token(COLON);
			value = value();
			jj_consume_token(SEMICOLON);
			style.set("shadow-width", value);
			break;
		case SHADOWOFFSET:
			jj_consume_token(SHADOWOFFSET);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("shadow-offset", values);
			break;
		case PADDING:
			jj_consume_token(PADDING);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("padding", values);
			break;
		case TEXTMODE:
			jj_consume_token(TEXTMODE);
			jj_consume_token(COLON);
			textMode = textMode();
			jj_consume_token(SEMICOLON);
			style.set("text-mode", textMode);
			break;
		case TEXTVISIBILITYMODE:
			jj_consume_token(TEXTVISIBILITYMODE);
			jj_consume_token(COLON);
			textVisMode = textVisMode();
			jj_consume_token(SEMICOLON);
			style.set("text-visibility-mode", textVisMode);
			break;
		case TEXTVISIBILITY:
			jj_consume_token(TEXTVISIBILITY);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("text-visibility", values);
			break;
		case TEXTBACKGROUNDMODE:
			jj_consume_token(TEXTBACKGROUNDMODE);
			jj_consume_token(COLON);
			textBgMode = textBgMode();
			jj_consume_token(SEMICOLON);
			style.set("text-background-mode", textBgMode);
			break;
		case TEXTCOLOR:
			jj_consume_token(TEXTCOLOR);
			jj_consume_token(COLON);
			colors = colors();
			jj_consume_token(SEMICOLON);
			style.set("text-color", colors);
			break;
		case TEXTBACKGROUNDCOLOR:
			jj_consume_token(TEXTBACKGROUNDCOLOR);
			jj_consume_token(COLON);
			colors = colors();
			jj_consume_token(SEMICOLON);
			style.set("text-background-color", colors);
			break;
		case TEXTSTYLE:
			jj_consume_token(TEXTSTYLE);
			jj_consume_token(COLON);
			textStyle = textStyle();
			jj_consume_token(SEMICOLON);
			style.set("text-style", textStyle);
			break;
		case TEXTFONT:
			jj_consume_token(TEXTFONT);
			jj_consume_token(COLON);
			url = font();
			jj_consume_token(SEMICOLON);
			style.set("text-font", url);
			break;
		case TEXTSIZE:
			jj_consume_token(TEXTSIZE);
			jj_consume_token(COLON);
			value = value();
			jj_consume_token(SEMICOLON);
			style.set("text-size", value);
			break;
		case TEXTALIGNMENT:
			jj_consume_token(TEXTALIGNMENT);
			jj_consume_token(COLON);
			textAlignment = textAlign();
			jj_consume_token(SEMICOLON);
			style.set("text-alignment", textAlignment);
			break;
		case TEXTOFFSET:
			jj_consume_token(TEXTOFFSET);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("text-offset", values);
			break;
		case TEXTPADDING:
			jj_consume_token(TEXTPADDING);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("text-padding", values);
			break;
		case ICONMODE:
			jj_consume_token(ICONMODE);
			jj_consume_token(COLON);
			iconMode = iconMode();
			jj_consume_token(SEMICOLON);
			style.set("icon-mode", iconMode);
			break;
		case ICON:
			jj_consume_token(ICON);
			jj_consume_token(COLON);
			url = icon();
			jj_consume_token(SEMICOLON);
			style.set("icon", url);
			break;
		case JCOMPONENT:
			jj_consume_token(JCOMPONENT);
			jj_consume_token(COLON);
			component = jcomponent();
			jj_consume_token(SEMICOLON);
			style.set("jcomponent", component);
			break;
		case VISIBILITYMODE:
			jj_consume_token(VISIBILITYMODE);
			jj_consume_token(COLON);
			visMode = visMode();
			jj_consume_token(SEMICOLON);
			style.set("visibility-mode", visMode);
			break;
		case VISIBILITY:
			jj_consume_token(VISIBILITY);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("visibility", values);
			break;
		case SIZEMODE:
			jj_consume_token(SIZEMODE);
			jj_consume_token(COLON);
			sizeMode = sizeMode();
			jj_consume_token(SEMICOLON);
			style.set("size-mode", sizeMode);
			break;
		case SIZE:
			jj_consume_token(SIZE);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("size", values);
			break;
		case SHAPEPOINTS:
			jj_consume_token(SHAPEPOINTS);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("shape-points", values);
			break;
		case SHAPE:
			jj_consume_token(SHAPE);
			jj_consume_token(COLON);
			shape = shape();
			jj_consume_token(SEMICOLON);
			style.set("shape", shape);
			break;
		case SPRITEORIENT:
			jj_consume_token(SPRITEORIENT);
			jj_consume_token(COLON);
			spriteOrient = spriteOrient();
			jj_consume_token(SEMICOLON);
			style.set("sprite-orientation", spriteOrient);
			break;
		case ARROWSHAPE:
			jj_consume_token(ARROWSHAPE);
			jj_consume_token(COLON);
			arrowShape = arrowShape();
			jj_consume_token(SEMICOLON);
			style.set("arrow-shape", arrowShape);
			break;
		case ARROWIMGURL:
			jj_consume_token(ARROWIMGURL);
			jj_consume_token(COLON);
			url = url();
			jj_consume_token(SEMICOLON);
			style.set("arrow-image", url);
			break;
		case ARROWSIZE:
			jj_consume_token(ARROWSIZE);
			jj_consume_token(COLON);
			values = values();
			jj_consume_token(SEMICOLON);
			style.set("arrow-size", values);
			break;
		case CANVASCOLOR:
			jj_consume_token(CANVASCOLOR);
			jj_consume_token(COLON);
			colors = colors();
			jj_consume_token(SEMICOLON);
			style.set("canvas-color", colors);
			break;
		default:
			jj_la1[10] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
	}

	final public Value value() throws ParseException {
		Token t;
		Units units = Units.PX;
		Value value = null;
		t = jj_consume_token(REAL);
		String nb = t.image.toLowerCase();

		if (nb.endsWith("px")) {
			units = Units.PX;
			nb = nb.substring(0, nb.length() - 2);
		} else if (nb.endsWith("gu")) {
			units = Units.GU;
			nb = nb.substring(0, nb.length() - 2);
		} else if (nb.endsWith("%")) {
			units = Units.PERCENTS;
			nb = nb.substring(0, nb.length() - 1);
		}

		try {
			value = new Value(units, Double.parseDouble(nb));
		} catch (NumberFormatException e) {
			generateParseException();
		}

		{
			if (true)
				return value;
		}
		throw new Error("Missing return statement in function");
	}

	final public Values values() throws ParseException {
		Values values = new Values();
		Value value;
		value = value();
		values.add(value);
		label_4: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case COMA:
				;
				break;
			default:
				jj_la1[11] = jj_gen;
				break label_4;
			}
			jj_consume_token(COMA);
			value = value();
			values.add(value);
		}
		{
			if (true)
				return values;
		}
		throw new Error("Missing return statement in function");
	}

	final public String url() throws ParseException {
		Token t;
		jj_consume_token(URL);
		jj_consume_token(LPAREN);
		t = jj_consume_token(STRING);
		jj_consume_token(RPAREN);
		{
			if (true)
				return t.image.substring(1, t.image.length() - 1);
		}
		throw new Error("Missing return statement in function");
	}

	final public String icon() throws ParseException {
		String s;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case DYNICON:
			jj_consume_token(DYNICON);
			{
				if (true)
					return "dynamic";
			}
			break;
		case URL:
			s = url();
			{
				if (true)
					return s;
			}
			break;
		default:
			jj_la1[12] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		throw new Error("Missing return statement in function");
	}

	final public String font() throws ParseException {
		Token t;
		String s;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case IDENTIFIER:
			t = jj_consume_token(IDENTIFIER);
			s = t.image;
			break;
		case STRING:
			t = jj_consume_token(STRING);
			s = t.image.substring(1, t.image.length() - 1);
			break;
		default:
			jj_la1[13] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return s;
		}
		throw new Error("Missing return statement in function");
	}

	final public Color color() throws ParseException {
		Token t;
		String s;
		Token r, g, b, a;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case RGB:
			jj_consume_token(RGB);
			jj_consume_token(LPAREN);
			r = jj_consume_token(REAL);
			jj_consume_token(COMA);
			g = jj_consume_token(REAL);
			jj_consume_token(COMA);
			b = jj_consume_token(REAL);
			jj_consume_token(RPAREN);
			s = String.format("rgb(%s,%s,%s)", r.image, g.image, b.image);
			break;
		case RGBA:
			jj_consume_token(RGBA);
			jj_consume_token(LPAREN);
			r = jj_consume_token(REAL);
			jj_consume_token(COMA);
			g = jj_consume_token(REAL);
			jj_consume_token(COMA);
			b = jj_consume_token(REAL);
			jj_consume_token(COMA);
			a = jj_consume_token(REAL);
			jj_consume_token(RPAREN);
			s = String.format("rgba(%s,%s,%s,%s)", r.image, g.image, b.image,
					a.image);
			break;
		case HTMLCOLOR:
			t = jj_consume_token(HTMLCOLOR);
			s = t.image;
			break;
		case IDENTIFIER:
			t = jj_consume_token(IDENTIFIER);
			s = t.image;
			break;
		case STRING:
			t = jj_consume_token(STRING);
			s = t.image.substring(1, t.image.length() - 1);
			break;
		default:
			jj_la1[14] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		Color color = ColorConverter.convertColor(s);
		if (color == null)
			color = Color.BLACK;
		{
			if (true)
				return color;
		}
		throw new Error("Missing return statement in function");
	}

	final public Colors colors() throws ParseException {
		Colors colors = new Colors();
		Color color;
		color = color();
		colors.add(color);
		label_5: while (true) {
			switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
			case COMA:
				;
				break;
			default:
				jj_la1[15] = jj_gen;
				break label_5;
			}
			jj_consume_token(COMA);
			color = color();
			colors.add(color);
		}
		{
			if (true)
				return colors;
		}
		throw new Error("Missing return statement in function");
	}

	final public FillMode fillMode() throws ParseException {
		FillMode m;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NONE:
			jj_consume_token(NONE);
			m = FillMode.NONE;
			break;
		case PLAIN:
			jj_consume_token(PLAIN);
			m = FillMode.PLAIN;
			break;
		case DYNPLAIN:
			jj_consume_token(DYNPLAIN);
			m = FillMode.DYN_PLAIN;
			break;
		case GRADIENTRADIAL:
			jj_consume_token(GRADIENTRADIAL);
			m = FillMode.GRADIENT_RADIAL;
			break;
		case GRADIENTVERTICAL:
			jj_consume_token(GRADIENTVERTICAL);
			m = FillMode.GRADIENT_VERTICAL;
			break;
		case GRADIENTHORIZONTAL:
			jj_consume_token(GRADIENTHORIZONTAL);
			m = FillMode.GRADIENT_HORIZONTAL;
			break;
		case GRADIENTDIAGONAL1:
			jj_consume_token(GRADIENTDIAGONAL1);
			m = FillMode.GRADIENT_DIAGONAL1;
			break;
		case GRADIENTDIAGONAL2:
			jj_consume_token(GRADIENTDIAGONAL2);
			m = FillMode.GRADIENT_DIAGONAL2;
			break;
		case IMAGETILED:
			jj_consume_token(IMAGETILED);
			m = FillMode.IMAGE_TILED;
			break;
		case IMAGESCALED:
			jj_consume_token(IMAGESCALED);
			m = FillMode.IMAGE_SCALED;
			break;
		case IMAGESCALEDRATIOMAX:
			jj_consume_token(IMAGESCALEDRATIOMAX);
			m = FillMode.IMAGE_SCALED_RATIO_MAX;
			break;
		case IMAGESCALEDRATIOMIN:
			jj_consume_token(IMAGESCALEDRATIOMIN);
			m = FillMode.IMAGE_SCALED_RATIO_MIN;
			break;
		default:
			jj_la1[16] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return m;
		}
		throw new Error("Missing return statement in function");
	}

	final public StrokeMode strokeMode() throws ParseException {
		StrokeMode m;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NONE:
			jj_consume_token(NONE);
			m = StrokeMode.NONE;
			break;
		case PLAIN:
			jj_consume_token(PLAIN);
			m = StrokeMode.PLAIN;
			break;
		case DASHES:
			jj_consume_token(DASHES);
			m = StrokeMode.DASHES;
			break;
		case DOTS:
			jj_consume_token(DOTS);
			m = StrokeMode.DOTS;
			break;
		case DOUBLE:
			jj_consume_token(DOUBLE);
			m = StrokeMode.DOUBLE;
			break;
		default:
			jj_la1[17] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return m;
		}
		throw new Error("Missing return statement in function");
	}

	final public ShadowMode shadowMode() throws ParseException {
		ShadowMode s;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NONE:
			jj_consume_token(NONE);
			s = ShadowMode.NONE;
			break;
		case PLAIN:
			jj_consume_token(PLAIN);
			s = ShadowMode.PLAIN;
			break;
		case GRADIENTRADIAL:
			jj_consume_token(GRADIENTRADIAL);
			s = ShadowMode.GRADIENT_RADIAL;
			break;
		case GRADIENTHORIZONTAL:
			jj_consume_token(GRADIENTHORIZONTAL);
			s = ShadowMode.GRADIENT_HORIZONTAL;
			break;
		case GRADIENTVERTICAL:
			jj_consume_token(GRADIENTVERTICAL);
			s = ShadowMode.GRADIENT_VERTICAL;
			break;
		case GRADIENTDIAGONAL1:
			jj_consume_token(GRADIENTDIAGONAL1);
			s = ShadowMode.GRADIENT_DIAGONAL1;
			break;
		case GRADIENTDIAGONAL2:
			jj_consume_token(GRADIENTDIAGONAL2);
			s = ShadowMode.GRADIENT_DIAGONAL2;
			break;
		default:
			jj_la1[18] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return s;
		}
		throw new Error("Missing return statement in function");
	}

	final public TextMode textMode() throws ParseException {
		TextMode m;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NORMAL:
			jj_consume_token(NORMAL);
			m = TextMode.NORMAL;
			break;
		case HIDDEN:
			jj_consume_token(HIDDEN);
			m = TextMode.HIDDEN;
			break;
		case TRUNCATED:
			jj_consume_token(TRUNCATED);
			m = TextMode.TRUNCATED;
			break;
		default:
			jj_la1[19] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return m;
		}
		throw new Error("Missing return statement in function");
	}

	final public TextVisibilityMode textVisMode() throws ParseException {
		TextVisibilityMode m;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NORMAL:
			jj_consume_token(NORMAL);
			m = TextVisibilityMode.NORMAL;
			break;
		case HIDDEN:
			jj_consume_token(HIDDEN);
			m = TextVisibilityMode.HIDDEN;
			break;
		case ATZOOM:
			jj_consume_token(ATZOOM);
			m = TextVisibilityMode.AT_ZOOM;
			break;
		case UNDERZOOM:
			jj_consume_token(UNDERZOOM);
			m = TextVisibilityMode.UNDER_ZOOM;
			break;
		case OVERZOOM:
			jj_consume_token(OVERZOOM);
			m = TextVisibilityMode.OVER_ZOOM;
			break;
		case ZOOMRANGE:
			jj_consume_token(ZOOMRANGE);
			m = TextVisibilityMode.ZOOM_RANGE;
			break;
		case ZOOMS:
			jj_consume_token(ZOOMS);
			m = TextVisibilityMode.ZOOMS;
			break;
		default:
			jj_la1[20] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return m;
		}
		throw new Error("Missing return statement in function");
	}

	final public TextBackgroundMode textBgMode() throws ParseException {
		TextBackgroundMode m;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NONE:
			jj_consume_token(NONE);
			m = TextBackgroundMode.NONE;
			break;
		case PLAIN:
			jj_consume_token(PLAIN);
			m = TextBackgroundMode.PLAIN;
			break;
		case ROUNDEDBOX:
			jj_consume_token(ROUNDEDBOX);
			m = TextBackgroundMode.ROUNDEDBOX;
			break;
		default:
			jj_la1[21] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return m;
		}
		throw new Error("Missing return statement in function");
	}

	final public TextStyle textStyle() throws ParseException {
		TextStyle t;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NORMAL:
			jj_consume_token(NORMAL);
			t = TextStyle.NORMAL;
			break;
		case BOLD:
			jj_consume_token(BOLD);
			t = TextStyle.BOLD;
			break;
		case ITALIC:
			jj_consume_token(ITALIC);
			t = TextStyle.ITALIC;
			break;
		case BOLD_ITALIC:
			jj_consume_token(BOLD_ITALIC);
			t = TextStyle.BOLD_ITALIC;
			break;
		default:
			jj_la1[22] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return t;
		}
		throw new Error("Missing return statement in function");
	}

	final public SizeMode sizeMode() throws ParseException {
		SizeMode m;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NORMAL:
			jj_consume_token(NORMAL);
			m = SizeMode.NORMAL;
			break;
		case FIT:
			jj_consume_token(FIT);
			m = SizeMode.FIT;
			break;
		case DYNSIZE:
			jj_consume_token(DYNSIZE);
			m = SizeMode.DYN_SIZE;
			break;
		default:
			jj_la1[23] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return m;
		}
		throw new Error("Missing return statement in function");
	}

	final public TextAlignment textAlign() throws ParseException {
		TextAlignment t;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case CENTER:
			jj_consume_token(CENTER);
			t = TextAlignment.CENTER;
			break;
		case LEFT:
			jj_consume_token(LEFT);
			t = TextAlignment.LEFT;
			break;
		case RIGHT:
			jj_consume_token(RIGHT);
			t = TextAlignment.RIGHT;
			break;
		case ATLEFT:
			jj_consume_token(ATLEFT);
			t = TextAlignment.AT_LEFT;
			break;
		case ATRIGHT:
			jj_consume_token(ATRIGHT);
			t = TextAlignment.AT_RIGHT;
			break;
		case UNDER:
			jj_consume_token(UNDER);
			t = TextAlignment.UNDER;
			break;
		case ABOVE:
			jj_consume_token(ABOVE);
			t = TextAlignment.ABOVE;
			break;
		case JUSTIFY:
			jj_consume_token(JUSTIFY);
			t = TextAlignment.JUSTIFY;
			break;
		case ALONG:
			jj_consume_token(ALONG);
			t = TextAlignment.ALONG;
			break;
		default:
			jj_la1[24] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return t;
		}
		throw new Error("Missing return statement in function");
	}

	final public IconMode iconMode() throws ParseException {
		IconMode i;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NONE:
			jj_consume_token(NONE);
			i = IconMode.NONE;
			break;
		case ATLEFT:
			jj_consume_token(ATLEFT);
			i = IconMode.AT_LEFT;
			break;
		case ATRIGHT:
			jj_consume_token(ATRIGHT);
			i = IconMode.AT_RIGHT;
			break;
		case ABOVE:
			jj_consume_token(ABOVE);
			i = IconMode.ABOVE;
			break;
		case UNDER:
			jj_consume_token(UNDER);
			i = IconMode.UNDER;
			break;
		default:
			jj_la1[25] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return i;
		}
		throw new Error("Missing return statement in function");
	}

	final public VisibilityMode visMode() throws ParseException {
		VisibilityMode m;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NORMAL:
			jj_consume_token(NORMAL);
			m = VisibilityMode.NORMAL;
			break;
		case HIDDEN:
			jj_consume_token(HIDDEN);
			m = VisibilityMode.HIDDEN;
			break;
		case ATZOOM:
			jj_consume_token(ATZOOM);
			m = VisibilityMode.AT_ZOOM;
			break;
		case UNDERZOOM:
			jj_consume_token(UNDERZOOM);
			m = VisibilityMode.UNDER_ZOOM;
			break;
		case OVERZOOM:
			jj_consume_token(OVERZOOM);
			m = VisibilityMode.OVER_ZOOM;
			break;
		case ZOOMRANGE:
			jj_consume_token(ZOOMRANGE);
			m = VisibilityMode.ZOOM_RANGE;
			break;
		case ZOOMS:
			jj_consume_token(ZOOMS);
			m = VisibilityMode.ZOOMS;
			break;
		default:
			jj_la1[26] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return m;
		}
		throw new Error("Missing return statement in function");
	}

	final public Shape shape() throws ParseException {
		Shape s;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case CIRCLE:
			jj_consume_token(CIRCLE);
			s = Shape.CIRCLE;
			break;
		case BOX:
			jj_consume_token(BOX);
			s = Shape.BOX;
			break;
		case ROUNDEDBOX:
			jj_consume_token(ROUNDEDBOX);
			s = Shape.ROUNDED_BOX;
			break;
		case TRIANGLE:
			jj_consume_token(TRIANGLE);
			s = Shape.TRIANGLE;
			break;
		case CROSS:
			jj_consume_token(CROSS);
			s = Shape.CROSS;
			break;
		case DIAMOND:
			jj_consume_token(DIAMOND);
			s = Shape.DIAMOND;
			break;
		case POLYGON:
			jj_consume_token(POLYGON);
			s = Shape.POLYGON;
			break;
		case FREEPLANE:
			jj_consume_token(FREEPLANE);
			s = Shape.FREEPLANE;
			break;
		case TEXTBOX:
			jj_consume_token(TEXTBOX);
			s = Shape.TEXT_BOX;
			break;
		case TEXTROUNDEDBOX:
			jj_consume_token(TEXTROUNDEDBOX);
			s = Shape.TEXT_ROUNDED_BOX;
			break;
		case TEXTCIRCLE:
			jj_consume_token(TEXTCIRCLE);
			s = Shape.TEXT_CIRCLE;
			break;
		case TEXTDIAMOND:
			jj_consume_token(TEXTDIAMOND);
			s = Shape.TEXT_DIAMOND;
			break;
		case TEXTPARAGRAPH:
			jj_consume_token(TEXTPARAGRAPH);
			s = Shape.TEXT_PARAGRAPH;
			break;
		case JCOMPONENT:
			jj_consume_token(JCOMPONENT);
			s = Shape.JCOMPONENT;
			break;
		case PIECHART:
			jj_consume_token(PIECHART);
			s = Shape.PIE_CHART;
			break;
		case FLOW:
			jj_consume_token(FLOW);
			s = Shape.FLOW;
			break;
		case ARROW:
			jj_consume_token(ARROW);
			s = Shape.ARROW;
			break;
		case LINE:
			jj_consume_token(LINE);
			s = Shape.LINE;
			break;
		case ANGLE:
			jj_consume_token(ANGLE);
			s = Shape.ANGLE;
			break;
		case CUBICCURVE:
			jj_consume_token(CUBICCURVE);
			s = Shape.CUBIC_CURVE;
			break;
		case POLYLINE:
			jj_consume_token(POLYLINE);
			s = Shape.POLYLINE;
			break;
		case POLYLINESCALED:
			jj_consume_token(POLYLINESCALED);
			s = Shape.POLYLINE_SCALED;
			break;
		case BLOB:
			jj_consume_token(BLOB);
			s = Shape.BLOB;
			break;
		case SQUARELINE:
			jj_consume_token(SQUARELINE);
			s = Shape.SQUARELINE;
			break;
		case LSQUARELINE:
			jj_consume_token(LSQUARELINE);
			s = Shape.LSQUARELINE;
			break;
		case HSQUARELINE:
			jj_consume_token(HSQUARELINE);
			s = Shape.HSQUARELINE;
			break;
		case VSQUARELINE:
			jj_consume_token(VSQUARELINE);
			s = Shape.VSQUARELINE;
			break;
		default:
			jj_la1[27] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return s;
		}
		throw new Error("Missing return statement in function");
	}

	final public ArrowShape arrowShape() throws ParseException {
		ArrowShape s;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NONE:
			jj_consume_token(NONE);
			s = ArrowShape.NONE;
			break;
		case ARROW:
			jj_consume_token(ARROW);
			s = ArrowShape.ARROW;
			break;
		case CIRCLE:
			jj_consume_token(CIRCLE);
			s = ArrowShape.CIRCLE;
			break;
		case DIAMOND:
			jj_consume_token(DIAMOND);
			s = ArrowShape.DIAMOND;
			break;
		case IMAGE:
			jj_consume_token(IMAGE);
			s = ArrowShape.IMAGE;
			break;
		default:
			jj_la1[28] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return s;
		}
		throw new Error("Missing return statement in function");
	}

	final public JComponents jcomponent() throws ParseException {
		JComponents c;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case BUTTON:
			jj_consume_token(BUTTON);
			c = JComponents.BUTTON;
			break;
		case TEXTFIELD:
			jj_consume_token(TEXTFIELD);
			c = JComponents.TEXT_FIELD;
			break;
		case PANEL:
			jj_consume_token(PANEL);
			c = JComponents.PANEL;
			break;
		default:
			jj_la1[29] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return c;
		}
		throw new Error("Missing return statement in function");
	}

	final public SpriteOrientation spriteOrient() throws ParseException {
		SpriteOrientation s;
		switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
		case NONE:
			jj_consume_token(NONE);
			s = SpriteOrientation.NONE;
			break;
		case TO:
			jj_consume_token(TO);
			s = SpriteOrientation.TO;
			break;
		case FROM:
			jj_consume_token(FROM);
			s = SpriteOrientation.FROM;
			break;
		case NODE0:
			jj_consume_token(NODE0);
			s = SpriteOrientation.NODE0;
			break;
		case NODE1:
			jj_consume_token(NODE1);
			s = SpriteOrientation.NODE1;
			break;
		case PROJECTION:
			jj_consume_token(PROJECTION);
			s = SpriteOrientation.PROJECTION;
			break;
		default:
			jj_la1[30] = jj_gen;
			jj_consume_token(-1);
			throw new ParseException();
		}
		{
			if (true)
				return s;
		}
		throw new Error("Missing return statement in function");
	}

	/** Generated Token Manager. */
	public StyleSheetParserTokenManager token_source;
	SimpleCharStream jj_input_stream;
	/** Current token. */
	public Token token;
	/** Next token. */
	public Token jj_nt;
	private int jj_ntk;
	private int jj_gen;
	final private int[] jj_la1 = new int[31];
	static private int[] jj_la1_0;
	static private int[] jj_la1_1;
	static private int[] jj_la1_2;
	static private int[] jj_la1_3;
	static private int[] jj_la1_4;
	static {
		jj_la1_init_0();
		jj_la1_init_1();
		jj_la1_init_2();
		jj_la1_init_3();
		jj_la1_init_4();
	}

	private static void jj_la1_init_0() {
		jj_la1_0 = new int[] { 0xf000000, 0xf000000, 0xf0000000, 0xf000000,
				0x400000, 0x4000, 0x200, 0x400000, 0x400000, 0x8000,
				0xf0000000, 0x20000, 0x800000, 0x400000, 0x5c0000, 0x20000,
				0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
				0x0, 0x0, 0x0, };
	}

	private static void jj_la1_init_1() {
		jj_la1_1 = new int[] { 0x0, 0x0, 0xffffffff, 0x0, 0x0, 0x0, 0x0, 0x0,
				0x0, 0x0, 0xffffffff, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
				0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x10000000, 0x0, 0x0,
				0x0, };
	}

	private static void jj_la1_init_2() {
		jj_la1_2 = new int[] { 0x0, 0x0, 0x3, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
				0x0, 0x3, 0x0, 0x20, 0x0, 0x0, 0x0, 0xfbe0c, 0x801c4, 0x83e04,
				0x304000, 0x7d04000, 0x80004, 0x70100000, 0x8100010,
				0x80000000, 0x80000, 0x7d04000, 0x0, 0x80000, 0x0, 0x80000, };
	}

	private static void jj_la1_init_3() {
		jj_la1_3 = new int[] { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
				0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
				0x20000, 0x0, 0x0, 0xff, 0x63, 0x0, 0xff1fff00, 0x80100,
				0xe00000, 0x0, };
	}

	private static void jj_la1_init_4() {
		jj_la1_4 = new int[] { 0x8000, 0x8000, 0x8000, 0x0, 0x4000, 0x0, 0x0,
				0x7000, 0x7000, 0x0, 0x8000, 0x0, 0x0, 0x4000, 0x4000, 0x0,
				0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1f,
				0x24, 0x0, 0xf80, };
	}

	/** Constructor with InputStream. */
	public StyleSheetParser(java.io.InputStream stream) {
		this(stream, null);
	}

	/** Constructor with InputStream and supplied encoding */
	public StyleSheetParser(java.io.InputStream stream, String encoding) {
		try {
			jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source = new StyleSheetParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 31; i++)
			jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(java.io.InputStream stream) {
		ReInit(stream, null);
	}

	/** Reinitialise. */
	public void ReInit(java.io.InputStream stream, String encoding) {
		try {
			jj_input_stream.ReInit(stream, encoding, 1, 1);
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 31; i++)
			jj_la1[i] = -1;
	}

	/** Constructor. */
	public StyleSheetParser(java.io.Reader stream) {
		jj_input_stream = new SimpleCharStream(stream, 1, 1);
		token_source = new StyleSheetParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 31; i++)
			jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(java.io.Reader stream) {
		jj_input_stream.ReInit(stream, 1, 1);
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 31; i++)
			jj_la1[i] = -1;
	}

	/** Constructor with generated Token Manager. */
	public StyleSheetParser(StyleSheetParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 31; i++)
			jj_la1[i] = -1;
	}

	/** Reinitialise. */
	public void ReInit(StyleSheetParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 31; i++)
			jj_la1[i] = -1;
	}

	private Token jj_consume_token(int kind) throws ParseException {
		Token oldToken;
		if ((oldToken = token).next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		if (token.kind == kind) {
			jj_gen++;
			return token;
		}
		token = oldToken;
		jj_kind = kind;
		throw generateParseException();
	}

	/** Get the next Token. */
	final public Token getNextToken() {
		if (token.next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	/** Get the specific Token. */
	final public Token getToken(int index) {
		Token t = token;
		for (int i = 0; i < index; i++) {
			if (t.next != null)
				t = t.next;
			else
				t = t.next = token_source.getNextToken();
		}
		return t;
	}

	private int jj_ntk() {
		if ((jj_nt = token.next) == null)
			return (jj_ntk = (token.next = token_source.getNextToken()).kind);
		else
			return (jj_ntk = jj_nt.kind);
	}

	private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
	private int[] jj_expentry;
	private int jj_kind = -1;

	/** Generate ParseException. */
	public ParseException generateParseException() {
		jj_expentries.clear();
		boolean[] la1tokens = new boolean[144];
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 31; i++) {
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & (1 << j)) != 0) {
						la1tokens[j] = true;
					}
					if ((jj_la1_1[i] & (1 << j)) != 0) {
						la1tokens[32 + j] = true;
					}
					if ((jj_la1_2[i] & (1 << j)) != 0) {
						la1tokens[64 + j] = true;
					}
					if ((jj_la1_3[i] & (1 << j)) != 0) {
						la1tokens[96 + j] = true;
					}
					if ((jj_la1_4[i] & (1 << j)) != 0) {
						la1tokens[128 + j] = true;
					}
				}
			}
		}
		for (int i = 0; i < 144; i++) {
			if (la1tokens[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.add(jj_expentry);
			}
		}
		int[][] exptokseq = new int[jj_expentries.size()][];
		for (int i = 0; i < jj_expentries.size(); i++) {
			exptokseq[i] = jj_expentries.get(i);
		}
		return new ParseException(token, exptokseq, tokenImage);
	}

	/** Enable tracing. */
	final public void enable_tracing() {
	}

	/** Disable tracing. */
	final public void disable_tracing() {
	}

}
