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
package org.graphstream.nui.style.util;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;

public class ColorConverter {
	/**
	 * A set of colour names mapped to real AWT Colour objects.
	 */
	protected static HashMap<String, Color> colorMap;

	/**
	 * Pattern to ensure a "#FFFFFF" colour is recognised.
	 */
	protected static Pattern sharpColor1, sharpColor2;

	/**
	 * Pattern to ensure a CSS style "rgb(1,2,3)" colour is recognised.
	 */
	protected static Pattern cssColor;

	/**
	 * Pattern to ensure a CSS style "rgba(1,2,3,4)" colour is recognised.
	 */
	protected static Pattern cssColorA;

	/**
	 * Pattern to ensure that java.awt.Color.toString() strings are recognised
	 * as colour.
	 */
	protected static Pattern awtColor;

	/**
	 * Pattern to ensure an hexadecimal number is a recognised colour.
	 */
	protected static Pattern hexaColor;

	static {
		//
		// Prepare some pattern matchers.
		//
		sharpColor1 = Pattern
				.compile("#(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})((\\p{XDigit}\\p{XDigit})?)");
		sharpColor2 = Pattern
				.compile("#(\\p{XDigit})(\\p{XDigit})(\\p{XDigit})((\\p{XDigit})?)");
		hexaColor = Pattern
				.compile("0[xX](\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})((\\p{XDigit}\\p{XDigit})?)");
		cssColor = Pattern
				.compile("rgb\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)");
		cssColorA = Pattern
				.compile("rgba\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)");
		awtColor = Pattern
				.compile("java.awt.Color\\[r=([0-9]+),g=([0-9]+),b=([0-9]+)\\]");
		colorMap = new HashMap<String, Color>();

		//
		// Load all the X11 predefined colour names and their RGB definition
		// from a file stored in the graphstream.jar. This allows the DOT
		// import to correctly map colour names to real AWT Color objects.
		// There are more than 800 such colours...
		//
		
		URL url = StyleConstants.class.getResource("rgb.properties");

		if (url == null)
			throw new RuntimeException(
					"corrupted graphstream.jar ? the org/miv/graphstream/ui/graphicGraph/rgb.properties file is not found");

		Properties p = new Properties();

		try {
			p.load(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Object o : p.keySet()) {
			String key = (String) o;
			String val = p.getProperty(key);
			Color col = Color.decode(val);

			colorMap.put(key.toLowerCase(), col);
		}
	}

	/**
	 * Try to convert the given string value to a colour. It understands the 600
	 * colour names of the X11 RGB data base. It also understands colours given
	 * in the "#FFFFFF" format and the hexadecimal "0xFFFFFF" format. Finally,
	 * it understands colours given as a "rgb(1,10,100)", CSS-like format. If
	 * the input value is null, the result is null.
	 * 
	 * @param anyValue
	 *            The value to convert.
	 * @return the converted colour or null if the conversion failed.
	 */
	public static Color convertColor(Object anyValue) {
		if (anyValue == null)
			return null;

		if (anyValue instanceof Color)
			return (Color) anyValue;

		if (anyValue instanceof String) {
			Color c = null;
			String value = (String) anyValue;

			if (value.startsWith("#")) {
				Matcher m = sharpColor1.matcher(value);

				if (m.matches()) {
					if (value.length() == 7) {
						try {
							c = Color.decode(value);

							return c;
						} catch (NumberFormatException e) {
							c = null;
						}
					} else if (value.length() == 9) {
						int r = Integer.parseInt(m.group(1), 16);
						int g = Integer.parseInt(m.group(2), 16);
						int b = Integer.parseInt(m.group(3), 16);
						int a = Integer.parseInt(m.group(4), 16);

						return new Color(r, g, b, a);
					}
				}

				m = sharpColor2.matcher(value);

				if (m.matches()) {
					if (value.length() >= 4) {
						int r = Integer.parseInt(m.group(1), 16) * 16;
						int g = Integer.parseInt(m.group(2), 16) * 16;
						int b = Integer.parseInt(m.group(3), 16) * 16;
						int a = 255;

						if (value.length() == 5)
							a = Integer.parseInt(m.group(4), 16) * 16;

						return new Color(r, g, b, a);
					}
				}
			} else if (value.startsWith("rgb")) {
				Matcher m = cssColorA.matcher(value);

				if (m.matches()) {
					int r = Integer.parseInt(m.group(1));
					int g = Integer.parseInt(m.group(2));
					int b = Integer.parseInt(m.group(3));
					int a = Integer.parseInt(m.group(4));

					return new Color(r, g, b, a);
				}

				m = cssColor.matcher(value);

				if (m.matches()) {
					int r = Integer.parseInt(m.group(1));
					int g = Integer.parseInt(m.group(2));
					int b = Integer.parseInt(m.group(3));

					return new Color(r, g, b);
				}
			} else if (value.startsWith("0x") || value.startsWith("0X")) {
				Matcher m = hexaColor.matcher(value);

				if (m.matches()) {
					if (value.length() == 8) {
						try {
							return Color.decode(value);
						} catch (NumberFormatException e) {
							c = null;
						}
					} else if (value.length() == 10) {
						String r = m.group(1);
						String g = m.group(2);
						String b = m.group(3);
						String a = m.group(4);

						return new Color(Integer.parseInt(r, 16),
								Integer.parseInt(g, 16),
								Integer.parseInt(b, 16),
								Integer.parseInt(a, 16));
					}
				}
			} else if (value.startsWith("java.awt.Color[")) {
				Matcher m = awtColor.matcher(value);

				if (m.matches()) {
					int r = Integer.parseInt(m.group(1));
					int g = Integer.parseInt(m.group(2));
					int b = Integer.parseInt(m.group(3));

					return new Color(r, g, b);
				}
			}

			return colorMap.get(value.toLowerCase());
		}

		// TODO throw an exception instead ??
		return null;
	}
}
