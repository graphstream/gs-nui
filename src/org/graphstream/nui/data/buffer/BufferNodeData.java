package org.graphstream.nui.data.buffer;

import java.awt.Font;

import org.graphstream.nui.data.NodeData;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextAlignment;

public class BufferNodeData extends NodeData {
	public static enum Shape {
		CIRCLE, SQUARE
	}

	public Shape shape;

	public float width;
	public float height;

	public int stroke;
	public int strokeARGB;

	public TextAlignment labelAlignX;
	public TextAlignment labelAlignY;
	public float labelDX;
	public float labelDY;
	public int labelARGB;
	public float labelSize;
	public Font labelFont;

	protected BufferNodeData(BufferUIDataset dataset, String id) {
		super(dataset, id);

		shape = Shape.CIRCLE;

		width = 20;
		height = 20;

		stroke = 1;
		strokeARGB = 0xFF333333;

		labelAlignX = TextAlignment.CENTER;
		labelAlignY = TextAlignment.CENTER;
		labelARGB = 0xFF222222;
		labelSize = 16;
		labelFont = null;
		labelDX = 0;
		labelDY = 0;
	}
}
