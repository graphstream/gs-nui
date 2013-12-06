/*
 * Copyright 2006 - 2013
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

/**
 * The various constants and static constant conversion methods used for
 * styling.
 */
public interface StyleConstants {
	/**
	 * The available units for numerical values.
	 */
	public static enum Units {
		PX, GU, PERCENTS
	};

	/**
	 * How to fill the contents of the element.
	 */
	public static enum FillMode {
		NONE, PLAIN, DYN_PLAIN, GRADIENT_RADIAL, GRADIENT_HORIZONTAL, GRADIENT_VERTICAL, GRADIENT_DIAGONAL1, GRADIENT_DIAGONAL2, IMAGE_TILED, IMAGE_SCALED, IMAGE_SCALED_RATIO_MAX, IMAGE_SCALED_RATIO_MIN
	};

	/**
	 * How to draw the contour of the element.
	 */
	public static enum StrokeMode {
		NONE, PLAIN, DASHES, DOTS, DOUBLE
	}

	/**
	 * How to draw the shadow of the element.
	 */
	public static enum ShadowMode {
		NONE, PLAIN, GRADIENT_RADIAL, GRADIENT_HORIZONTAL, GRADIENT_VERTICAL, GRADIENT_DIAGONAL1, GRADIENT_DIAGONAL2
	}

	/**
	 * How to show an element.
	 */
	public static enum VisibilityMode {
		NORMAL, HIDDEN, AT_ZOOM, UNDER_ZOOM, OVER_ZOOM, ZOOM_RANGE, ZOOMS
	}

	/**
	 * How to draw the text of an element.
	 */
	public static enum TextMode {
		NORMAL, TRUNCATED, HIDDEN
	}

	/**
	 * How to show the text of an element.
	 */
	public static enum TextVisibilityMode {
		NORMAL, HIDDEN, AT_ZOOM, UNDER_ZOOM, OVER_ZOOM, ZOOM_RANGE, ZOOMS
	}

	/**
	 * Variant of the font.
	 */
	public static enum TextStyle {
		NORMAL, ITALIC, BOLD, BOLD_ITALIC
	}

	/**
	 * Where to place the icon around the text (or instead of the text).
	 */
	public static enum IconMode {
		NONE, AT_LEFT, AT_RIGHT, UNDER, ABOVE
	}

	/**
	 * How to set the size of the element.
	 */
	public static enum SizeMode {
		NORMAL, FIT, DYN_SIZE
	}

	/**
	 * How to align words around their attach point.
	 */
	public static enum TextAlignment {
		CENTER, LEFT, RIGHT, AT_LEFT, AT_RIGHT, UNDER, ABOVE, JUSTIFY,

		ALONG
	}

	public static enum TextBackgroundMode {
		NONE, PLAIN, ROUNDEDBOX
	}

	public static enum ShapeKind {
		ELLIPSOID, RECTANGULAR, LINEAR, CURVE
	}

	/**
	 * Possible shapes for elements.
	 */
	public static enum Shape {
		CIRCLE(ShapeKind.ELLIPSOID), BOX(ShapeKind.RECTANGULAR), ROUNDED_BOX(
				ShapeKind.RECTANGULAR), DIAMOND(ShapeKind.RECTANGULAR), POLYGON(
				ShapeKind.RECTANGULAR), TRIANGLE(ShapeKind.RECTANGULAR), CROSS(
				ShapeKind.RECTANGULAR), FREEPLANE(ShapeKind.RECTANGULAR), TEXT_BOX(
				ShapeKind.RECTANGULAR), TEXT_ROUNDED_BOX(ShapeKind.RECTANGULAR), TEXT_PARAGRAPH(
				ShapeKind.RECTANGULAR), TEXT_CIRCLE(ShapeKind.ELLIPSOID), TEXT_DIAMOND(
				ShapeKind.RECTANGULAR), JCOMPONENT(ShapeKind.RECTANGULAR),

		PIE_CHART(ShapeKind.ELLIPSOID), FLOW(ShapeKind.LINEAR), ARROW(
				ShapeKind.RECTANGULAR), IMAGES(ShapeKind.RECTANGULAR),

		LINE(ShapeKind.LINEAR), ANGLE(ShapeKind.LINEAR), CUBIC_CURVE(
				ShapeKind.CURVE), POLYLINE(ShapeKind.LINEAR), POLYLINE_SCALED(
				ShapeKind.LINEAR), SQUARELINE(ShapeKind.LINEAR), LSQUARELINE(
				ShapeKind.LINEAR), HSQUARELINE(ShapeKind.LINEAR), VSQUARELINE(
				ShapeKind.LINEAR), BLOB(ShapeKind.CURVE);

		public ShapeKind kind;

		Shape(ShapeKind kind) {
			this.kind = kind;
		}
	}

	/**
	 * Orientation of a sprite toward its attachment point.
	 */
	public static enum SpriteOrientation {
		NONE, FROM, NODE0, TO, NODE1, PROJECTION
	}

	/**
	 * Possible shapes for arrows on edges.
	 */
	public static enum ArrowShape {
		NONE, ARROW, CIRCLE, DIAMOND, IMAGE
	}

	/**
	 * Possible JComponents.
	 */
	public static enum JComponents {
		BUTTON, TEXT_FIELD, PANEL
	}

	public static enum StyleKey {
		/**
		 * The fill mode indicates how to color the interior of elements. If
		 * applied to the graph, it tells how to fill the whole background.
		 */
		FILL_MODE(FillMode.class, false),
		/**
		 * The fill color indicate one or more color used to fill to surface of
		 * the element. The way colors are used depends on the fill-mode. You
		 * specify several colors in order to use gradients of dynamic fill. By
		 * default the fill color is black for nodes, edges and sprites, and
		 * white for the graph background.
		 */
		FILL_COLOR(Colors.class, false),
		/**
		 * The fill image indicates the image to use when the fill mode requires
		 * a texture. The image must be given using the url() notation.
		 */
		FILL_IMAGE(String.class, true),
		/**
		 * Control the if the outline of the element shape is drawn or not.
		 */
		STROKE_MODE(StrokeMode.class, false),
		/**
		 * The color of the stroke of an element, the stroke-mode must not be
		 * none. Only one color is used. The default color is black.
		 */
		STROKE_COLOR(Colors.class, false),
		/**
		 * The width of the stroke of an element, the stroke-mode must not be
		 * none. The default value is 1.
		 */
		STROKE_WIDTH(Value.class, false),
		/**
		 * The padding is the distance between the element stroke and its
		 * contents. This must contain one, two or three values, indicating the
		 * padding along the X, Y and Z axis respectively. Note: actually the
		 * text-box shape for nodes and sprite is not yet implemented.
		 * 
		 * Padding can also be very useful for graphs. Applied to a graph, it
		 * indicates a distance between the whole graph rendering and the canvas
		 * it is drawn into. Indeed, the overall size of a graph is measured
		 * according to the position of nodes or sprites, not their size (this
		 * would be difficult, since it is possible to fix the node size
		 * according to the graph size!). Furthermore, some edges can be curved,
		 * and this is not accounted in the graph size computation. To avoid
		 * elements to be drawn inadvertently out of the canvas, you can define
		 * a padding.
		 */
		PADDING(Values.class, false),
		/**
		 * Define the shape of the node or sprite. The shape property is usable
		 * for nodes, edges, and sprites, but the available shapes are not the
		 * same.
		 */
		SHAPE(Shape.class, false),
		/**
		 * The size of the element, this contains one to three values for the X,
		 * Y and Z axis. If there are no value for Y, the X one is used, the
		 * same for Z. This size is used by the size-mode property.
		 */
		SIZE(Values.class, false),
		/**
		 * The size mode indicate how to set the size of the element.
		 */
		SIZE_MODE(SizeMode.class, false),
		/**
		 * An integer number indicating the "layer" inside which elements are
		 * rendered. This is a rendering order, elements with a lower z-index
		 * will be drawn first, whereas elements with a higher z-index will be
		 * drawn later. This allows to specify which element appears above
		 * another in two dimensions when elements are drawn one on another. You
		 * can for example use this to make the edges appear above nodes, or to
		 * make sprites appear under nodes. By default edges are drawn at index
		 * 1, then nodes at index 2, then sprites at index 3.
		 */
		Z_INDEX(Integer.class, false),
		/**
		 * Shadows draw the same shape as the element they pertain to, but with
		 * a size that may be larger (see shadow-width), and with an offset in
		 * the position (see shadow-offset).
		 */
		SHADOW_MODE(ShadowMode.class, false),
		/**
		 * Color or colors of the shadow. If the shadow mode is a gradient, two
		 * colors at least are needed. The default is black.
		 */
		SHADOW_COLOR(Colors.class, false),
		/**
		 * On or two numbers specifying the offset of the shadow with respect to
		 * the element position. If there is one number only, the offset along
		 * the X and Y axis are the same. The default is 3 pixels.
		 * 
		 * With a shadow offset at zero, and a shadow width greater than zero
		 * you can use shadows as as second stroke around the elements.
		 */
		SHADOW_OFFSET(Values.class, false),
		/**
		 * Width of the shadow. This is given in pixels, graph units or percents
		 * added to the size of the element. This means that a shadow width of 0
		 * will created a shadow of exactly the same size as the element. The
		 * default is 3 pixels.
		 */
		SHADOW_WIDTH(Value.class, false),
		/**
		 * The text mode indicates how the optional label of elements should be
		 * printed.
		 */
		TEXT_MODE(TextMode.class, false),
		/**
		 * The text background is a color painted under the text to make it more
		 * visible.
		 */
		TEXT_BACKGROUND_MODE(TextBackgroundMode.class, false),
		/**
		 * The background color of the optional label. The text background must
		 * be specified by text-background-mode. The default is white.
		 */
		TEXT_BACKGROUND_COLOR(Colors.class, false),
		/**
		 * The text visibility mode describe when the optional label of elements
		 * should be printed.
		 */
		TEXT_VISIBILITY_MODE(TextVisibilityMode.class, false),
		/**
		 * The zoom values at which the labels should be shown, according to
		 * text-visibility-mode.
		 */
		TEXT_VISIBILITY(Values.class, true),
		/**
		 * The foreground color of the optional label. The default is black.
		 */
		TEXT_COLOR(Colors.class, false),
		/**
		 * The style of text to use.
		 */
		TEXT_STYLE(TextStyle.class, false),
		/**
		 * The alignment of the text with respect to the element center (node,
		 * sprite or edge).
		 */
		TEXT_ALIGNMENT(TextAlignment.class, false),
		/**
		 * This adds some space between the text and the background borders, it
		 * is therefore used only when the text-background-mode defines a
		 * background around the text. This can be one or two values defining
		 * the padding along the X and Y axis. If there is only one value, the
		 * value is the same on X and Y. The value for X are added at left and
		 * right (thus twice), and identically, the value for Y are added at the
		 * top and bottom. The default is 0.
		 */
		TEXT_PADDING(Values.class, false),
		/**
		 * This offset the text along the X and Y axis. If there is an icon, the
		 * icon is also offset. This can contain one or two values. If there is
		 * only one value, it is used both for the X and Y axis. The default is
		 * 0 for both axis.
		 */
		TEXT_OFFSET(Values.class, false),
		/**
		 * The font to use for the text. The default is the default sans-serif
		 * font of your system.
		 */
		TEXT_FONT(String.class, true),
		/**
		 * Size of the font in points only (number without units). The default
		 * is 10 points.
		 */
		TEXT_SIZE(Value.class, false),
		/**
		 * This contains either the URL of an icon or the string dyn-icon. In
		 * the later case, you can use the ui.icon attribute on elements to
		 * specify the icon to use. For static icons using CSS is encouraged.
		 * However if you must vary often the icon of an element, ui.icon can be
		 * useful.
		 */
		ICON(String.class, true),
		/**
		 * Alignment of the icon according to the element.
		 */
		ICON_MODE(IconMode.class, false),
		/**
		 * The values for the "zoom" modes of the "visibility-mode" property.
		 */
		VISIBILITY(Values.class, true),
		/**
		 * Visibility of the element. The zooms values allows to show the
		 * element only at specified zoom levels.
		 */
		VISIBILITY_MODE(VisibilityMode.class, false),
		/**
		 * The shape of arrows on directed edges. It is not possible to add an
		 * arrow to a non directed edge.
		 */
		ARROW_SHAPE(ArrowShape.class, false),
		/**
		 * The URL of the file on the local file system or of an image on the
		 * web to use for the arrow, if arrow-shape equals to ìmage. Or
		 * "dynamic" if the URL is the keyword dynamic, the image URL of the
		 * arrow is taken from the attribute ui.arrow-image.
		 */
		ARROW_IMAGE(String.class, true),
		/**
		 * The size of the arrow, the first number gives the length of the
		 * arrow, the second expresses the base length of the arrow.
		 */
		ARROW_SIZE(Values.class, false),
		/**
		 * {@see StyleConstants.SpriteOrientation}
		 */
		SPRITE_ORIENTATION(SpriteOrientation.class, false),
		/**
		 * 
		 */
		CANVAS_COLOR(Colors.class, false);

		public final Class<?> valueType;
		public final boolean nullAllowed;

		StyleKey(Class<?> valueType, boolean nullAllowed) {
			this.valueType = valueType;
			this.nullAllowed = nullAllowed;
		}
	}
}