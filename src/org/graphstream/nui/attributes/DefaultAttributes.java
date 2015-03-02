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
package org.graphstream.nui.attributes;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graphstream.nui.AbstractModule;
import org.graphstream.nui.UIAttributes;
import org.graphstream.nui.UIContext;
import org.graphstream.nui.UIIndexer;
import org.graphstream.nui.indexer.ElementIndex;
import org.graphstream.stream.AttributeSink;

public class DefaultAttributes extends AbstractModule implements UIAttributes {

	protected UIContext ctx;
	protected UIIndexer indexer;

	protected AttributeDispatcher dispatcher;
	protected AttributeHandlers attributeHandlers;

	public DefaultAttributes() {
		super(MODULE_ID, UIIndexer.MODULE_ID);

		attributeHandlers = new AttributeHandlers();
		dispatcher = new AttributeDispatcher();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.AbstractModule#init(org.graphstream.nui.UIContext)
	 */
	@Override
	public void init(UIContext ctx) {
		this.ctx = ctx;
		indexer = (UIIndexer) ctx.getModule(UIIndexer.MODULE_ID);

		ctx.getContextProxy().addAttributeSink(dispatcher);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.nui.AbstractModule#release()
	 */
	@Override
	public void release() {
		ctx.getContextProxy().removeAttributeSink(dispatcher);

		indexer = null;
		ctx = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIAttributes#registerUIAttributeHandler(org.graphstream
	 * .nui.UIAttributes.AttributeType, java.lang.String,
	 * org.graphstream.nui.context.AttributeHandler)
	 */
	@Override
	public void registerUIAttributeHandler(AttributeType type, String key,
			AttributeHandler handler) {
		attributeHandlers.addHandler(type, key, handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.UIAttributes#unregisterUIAttributeHandler(org.graphstream
	 * .nui.UIAttributes.AttributeType, java.lang.String,
	 * org.graphstream.nui.context.AttributeHandler)
	 */
	@Override
	public void unregisterUIAttributeHandler(AttributeType type, String key,
			AttributeHandler handler) {
		attributeHandlers.removeHandler(type, key, handler);
	}

	class AttributeDispatcher implements AttributeSink {
		private boolean isUIAttribute(String attributeId) {
			return attributeId.length() > 3 && attributeId.charAt(0) == 'u'
					&& attributeId.charAt(1) == 'i'
					&& attributeId.charAt(2) == '.';
		}

		private void handleGraphAttribute(String attributeId, Object value) {
			if (isUIAttribute(attributeId)) {
				boolean handled = false;
				attributeId = attributeId.substring(3);

				int i = attributeId.indexOf('.');

				if (i > 0) {
					String moduleId = attributeId.substring(0, i);
					attributeId = attributeId.substring(i + 1);

					if (ctx.hasModule(moduleId)) {
						ctx.getModule(moduleId)
								.setAttribute(attributeId, value);
						handled = true;
					}

				} else {
					String moduleId = attributeId;

					if (!ctx.hasModule(moduleId))
						handled = ctx.tryLoadModule(moduleId);
				}

				if (!handled) {
					attributeHandlers.fire(AttributeType.GRAPH,
							indexer.getGraphIndex(), attributeId, value);
					attributeHandlers.fire(AttributeType.ALL,
							indexer.getGraphIndex(), attributeId, value);
				}
			}
		}

		private void handleElementAttribute(AttributeType type,
				ElementIndex index, String attributeId, Object value) {
			if (isUIAttribute(attributeId)) {
				attributeId = attributeId.substring(3);

				attributeHandlers.fire(type, index, attributeId, value);
				attributeHandlers.fire(AttributeType.ELEMENT, index,
						attributeId, value);
				attributeHandlers.fire(AttributeType.ALL, index, attributeId,
						value);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#graphAttributeAdded(java.lang.
		 * String, long, java.lang.String, java.lang.Object)
		 */
		@Override
		public void graphAttributeAdded(String sourceId, long timeId,
				String attributeId, Object value) {
			handleGraphAttribute(attributeId, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#graphAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void graphAttributeChanged(String sourceId, long timeId,
				String attributeId, Object oldValue, Object newValue) {
			handleGraphAttribute(attributeId, newValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.SinkAdapter#graphAttributeRemoved(java.lang
		 * .String, long, java.lang.String)
		 */
		@Override
		public void graphAttributeRemoved(String sourceId, long timeId,
				String attributeId) {
			handleGraphAttribute(attributeId, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		@Override
		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			ElementIndex index = indexer.getNodeIndex(nodeId);
			handleElementAttribute(AttributeType.NODE, index, attribute, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue, Object value) {
			ElementIndex index = indexer.getNodeIndex(nodeId);
			handleElementAttribute(AttributeType.NODE, index, attribute, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#nodeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			ElementIndex index = indexer.getNodeIndex(nodeId);
			handleElementAttribute(AttributeType.NODE, index, attribute, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeAdded(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object)
		 */
		@Override
		public void edgeAttributeAdded(String sourceId, long timeId,
				String edgeId, String attribute, Object value) {
			ElementIndex index = indexer.getEdgeIndex(edgeId);
			handleElementAttribute(AttributeType.EDGE, index, attribute, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeChanged(java.lang
		 * .String, long, java.lang.String, java.lang.String, java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue, Object value) {
			ElementIndex index = indexer.getEdgeIndex(edgeId);
			handleElementAttribute(AttributeType.EDGE, index, attribute, value);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.stream.AttributeSink#edgeAttributeRemoved(java.lang
		 * .String, long, java.lang.String, java.lang.String)
		 */
		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			ElementIndex index = indexer.getEdgeIndex(edgeId);
			handleElementAttribute(AttributeType.EDGE, index, attribute, null);
		}
	}

	@SuppressWarnings("serial")
	class AttributeHandlers
			extends
			EnumMap<UIAttributes.AttributeType, Map<String, List<AttributeHandler>>> {

		public AttributeHandlers() {
			super(UIAttributes.AttributeType.class);
		}

		public void fire(UIAttributes.AttributeType type, ElementIndex index,
				String attributeId, Object value) {
			Map<String, List<AttributeHandler>> m = get(type);

			if (m != null) {
				List<AttributeHandler> l = m.get(attributeId);

				if (l != null) {
					for (AttributeHandler ah : l)
						ah.handleAttribute(index, attributeId, value);
				}
			}
		}

		public void addHandler(UIAttributes.AttributeType type,
				String attributeId, AttributeHandler ah) {
			if (!containsKey(type))
				put(type, new HashMap<String, List<AttributeHandler>>());

			Map<String, List<AttributeHandler>> m = get(type);

			if (!m.containsKey(attributeId))
				m.put(attributeId, new LinkedList<AttributeHandler>());

			List<AttributeHandler> l = m.get(attributeId);
			l.add(ah);
		}

		public void removeHandler(AttributeType type, String attributeId,
				AttributeHandler ah) {
			Map<String, List<AttributeHandler>> m = get(type);

			if (m == null)
				return;

			if (!m.containsKey(attributeId))
				return;

			List<AttributeHandler> l = m.get(attributeId);
			l.remove(ah);
		}
	}
}
