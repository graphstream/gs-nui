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
package org.graphstream.nui.style.test;

import java.util.HashMap;

import org.junit.Assert;
import org.graphstream.nui.style.Selector;
import org.graphstream.nui.style.Selector.Target;
import org.junit.Test;

public class SelectorTest {
	@Test
	public void selector() {
		Selector s = new Selector(Target.NODE, "abc", new String[] { "classA",
				"classB" }, "hover");

		String exp = "node#abc.classA.classB:hover";

		Assert.assertEquals(exp, s.toString());
		Assert.assertTrue(exp.hashCode() == s.hashCode());
	}

	@Test
	public void hash() {
		HashMap<Selector, String> selectors = new HashMap<Selector, String>();

		Selector s1 = new Selector(Target.NODE, "abc", new String[] { "classA",
				"classB" }, "hover");
		Selector s2 = new Selector(Target.NODE, "abc", new String[] { "classB",
				"classA" }, "hover");
		Selector s3 = new Selector(Target.NODE, "abc",
				new String[] { "classA" }, "hover");

		selectors.put(s1, "S1");
		selectors.put(s2, "S2");
		selectors.put(s3, "S3");

		Assert.assertTrue(selectors.size() == 2);

		Assert.assertEquals(selectors.get(s1), "S2");
		Assert.assertEquals(selectors.get(s2), "S2");
		Assert.assertEquals(selectors.get(s3), "S3");
	}

	@Test
	public void score() {
		Selector[] selectors = {
				new Selector(Target.NODE, "abc", new String[] { "classA",
						"classB", "classC" }, "hover"),
				new Selector(Target.NODE, "abc", new String[] { "classB",
						"classA" }, null),
				new Selector(Target.NODE, "abc", new String[] { "classA",
						"classB" }, "hover"),
				new Selector(Target.UNDEFINED, null, new String[] { "classA" },
						null),
				new Selector(Target.NODE, null, new String[] { "classB",
						"classA" }, null) };

		for (int i = 0; i < selectors.length; i++)
			System.out.printf("s%d: \"%s\" [%d]\n", i, selectors[i],
					selectors[i].score);

		for (int i = 0; i < selectors.length; i++) {
			for (int j = 0; j < selectors.length; j++)
				if (i != j)
					System.out.printf("s%d isParentOf s%d ? %s\n", i, j,
							selectors[i].isParent(selectors[j]));
		}
	}
}
