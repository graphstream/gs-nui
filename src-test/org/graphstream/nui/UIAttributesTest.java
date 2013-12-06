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

import org.junit.Assert;
import org.junit.Test;

public class UIAttributesTest {
	@Test
	public void match() {
		for (int i = 0; i < 100; i++) {
			int chainSize = 64 + (int) (256 * Math.random());
			String chain = generateRandomString(chainSize);

			for (int j = 0; j < 10; j++) {
				int b = (int) ((chain.length() - 10) * Math.random());
				int e = b + (int) ((chain.length() - b) * Math.random());
				String valid = chain.substring(b, e);

				Assert.assertTrue(UIAttributes.match(chain, b, valid, false));
			}

			for (int j = 0; j < 10; j++) {
				int b = (int) ((chain.length() - 10) * Math.random());
				int e = chain.length();
				String valid = chain.substring(b, e);

				Assert.assertTrue(UIAttributes.match(chain, b, valid, true));
			}

			for (int j = 0; j < 10; j++) {
				String invalid;
				int o = (int) (chain.length() * Math.random());
				int invalidSize = chainSize / 3
						+ (int) (2 * chainSize / 3 * Math.random());

				do {
					invalid = generateRandomString(invalidSize);
				} while (chain.indexOf(invalid) >= 0);

				Assert.assertFalse(UIAttributes.match(chain, o, invalid, false));
			}
		}
	}

	@Test
	public void coordinates() {
		String[] xyz = { "x", "y", "z", "xyz", "X", "Y", "Z", "XYZ" };
		String[] pre = { "", "ui." };

		for (int p = 0; p < pre.length; p++)
			for (int x = 0; x < xyz.length; x++) {
				String a = pre[p] + xyz[x];
				Assert.assertTrue(UIAttributes.isCoordinatesAttribute(a));
			}
	}

	public String generateRandomString(int size) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_./+*%!:;,?${}()[]";
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < size; i++)
			buffer.append(chars.charAt((int) (chars.length() * Math.random())));

		return buffer.toString();
	}
}
