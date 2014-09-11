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
package org.graphstream.nui.indexer.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.nui.RegisterException;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIFactory;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.UIContext.ThreadingModel;
import org.graphstream.nui.UIModules;
import org.graphstream.nui.indexer.DefaultIndexer;
import org.graphstream.nui.indexer.UIElementIndex;
import org.junit.Test;

public class DefaultIndexerTest {
	Logger log;
	
	@Before
	public void checkModule() {
		log = Logger.getGlobal();

		try {
			//
			// Be sure we are using the default indexer.
			//
			UIModules.registerModule(DefaultIndexer.class);
		} catch (RegisterException e) {
			log.log(Level.SEVERE, "Cannot register the default indexer", e);
			Assert.fail();
		}
	}
	
	@Test
	public void testNodeIndexes() {
		DefaultGraph g = new DefaultGraph("g");
		UIContext ctx = UIFactory.getDefaultFactory().createContext();

		ctx.init(ThreadingModel.SOURCE_IN_UI_THREAD);
		ctx.loadModule("indexer");

		UIIndexer indexer = (UIIndexer) ctx.getModule("indexer");
		Assert.assertEquals(indexer.getClass(), DefaultIndexer.class);

		ctx.connect(g);

		g.addNode("Z");
		g.addNode("W");
		g.addNode("Y");

		UIElementIndex zIndex = indexer.getNodeIndex("Z");
		UIElementIndex wIndex = indexer.getNodeIndex("W");
		UIElementIndex yIndex = indexer.getNodeIndex("Y");

		Assert.assertEquals(zIndex.index(), 0);
		Assert.assertEquals(wIndex.index(), 1);
		Assert.assertEquals(yIndex.index(), 2);

		Assert.assertEquals(indexer.getNodeIndex(0).id(), "Z");
		Assert.assertEquals(indexer.getNodeIndex(1).id(), "W");
		Assert.assertEquals(indexer.getNodeIndex(2).id(), "Y");

		g.removeNode("W");

		Assert.assertEquals(zIndex.index(), 0);
		Assert.assertEquals(yIndex.index(), 1);
		Assert.assertEquals(wIndex.index(), -1);

		Assert.assertEquals(indexer.getNodeIndex(0).id(), "Z");
		Assert.assertEquals(indexer.getNodeIndex(1).id(), "Y");
		Assert.assertEquals(indexer.getNodeIndex(2), null);

		g.addNode("W");
		wIndex = indexer.getNodeIndex("W");

		Assert.assertEquals(zIndex.index(), 0);
		Assert.assertEquals(yIndex.index(), 1);
		Assert.assertEquals(wIndex.index(), 2);

		Assert.assertEquals(indexer.getNodeIndex(0).id(), "Z");
		Assert.assertEquals(indexer.getNodeIndex(1).id(), "Y");
		Assert.assertEquals(indexer.getNodeIndex(2).id(), "W");

		ctx.close();
	}
	
	@Test
	public void testEdgeIndexes() {
		DefaultGraph g = new DefaultGraph("g");
		UIContext ctx = UIFactory.getDefaultFactory().createContext();

		ctx.init(ThreadingModel.SOURCE_IN_UI_THREAD);
		ctx.loadModule("indexer");

		UIIndexer indexer = (UIIndexer) ctx.getModule("indexer");
		Assert.assertEquals(indexer.getClass(), DefaultIndexer.class);

		ctx.connect(g);

		g.addNode("Z");
		g.addNode("W");
		g.addNode("Y");
		g.addEdge("ZW", "Z", "W");
		g.addEdge("ZY", "Z", "Y");
		g.addEdge("YW", "Y", "W");
		
		UIElementIndex zwIndex = indexer.getEdgeIndex("ZW");
		UIElementIndex zyIndex = indexer.getEdgeIndex("ZY");
		UIElementIndex ywIndex = indexer.getEdgeIndex("YW");
		
		Assert.assertEquals(zwIndex.index(), 0);
		Assert.assertEquals(zyIndex.index(), 1);
		Assert.assertEquals(ywIndex.index(), 2);

		Assert.assertEquals(indexer.getEdgeIndex(0).id(), "ZW");
		Assert.assertEquals(indexer.getEdgeIndex(1).id(), "ZY");
		Assert.assertEquals(indexer.getEdgeIndex(2).id(), "YW");

		g.removeEdge("ZW");

		Assert.assertEquals(zyIndex.index(), 1);
		Assert.assertEquals(ywIndex.index(), 0);
		Assert.assertEquals(zwIndex.index(), -1);

		Assert.assertEquals(indexer.getEdgeIndex(0).id(), "YW");
		Assert.assertEquals(indexer.getEdgeIndex(1).id(), "ZY");
		Assert.assertEquals(indexer.getEdgeIndex(2), null);

		g.addEdge("ZW", "Z", "W");
		zwIndex = indexer.getEdgeIndex("ZW");

		Assert.assertEquals(zwIndex.index(), 2);
		Assert.assertEquals(zyIndex.index(), 1);
		Assert.assertEquals(ywIndex.index(), 0);

		Assert.assertEquals(indexer.getEdgeIndex(0).id(), "YW");
		Assert.assertEquals(indexer.getEdgeIndex(1).id(), "ZY");
		Assert.assertEquals(indexer.getEdgeIndex(2).id(), "ZW");

		g.removeNode("Z");

		Assert.assertEquals(zwIndex.index(), -1);
		Assert.assertEquals(zyIndex.index(), -1);
		Assert.assertEquals(ywIndex.index(), 0);

		Assert.assertEquals(indexer.getEdgeIndex(0).id(), "YW");
		Assert.assertEquals(indexer.getEdgeIndex(1), null);
		Assert.assertEquals(indexer.getEdgeIndex(2), null);
		
		ctx.close();
	}
}
