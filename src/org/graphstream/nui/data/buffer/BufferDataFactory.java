package org.graphstream.nui.data.buffer;

import org.graphstream.nui.data.DataFactory;
import org.graphstream.nui.data.EdgeData;
import org.graphstream.nui.data.NodeData;
import org.graphstream.nui.data.SpriteData;

public class BufferDataFactory implements DataFactory {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.data.DataFactory#createNodeData(java.lang.String)
	 */
	public NodeData createNodeData(String nodeId) {
		return new BufferNodeData(nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.data.DataFactory#createEdgeData(java.lang.String,
	 * org.graphstream.nui.data.NodeData, org.graphstream.nui.data.NodeData,
	 * boolean)
	 */
	public EdgeData createEdgeData(String edgeId, NodeData source,
			NodeData target, boolean directed) {
		return new BufferEdgeData(edgeId, (BufferNodeData) source,
				(BufferNodeData) target, directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.data.DataFactory#createSpriteData(java.lang.String)
	 */
	public SpriteData createSpriteData(String spriteId) {
		// TODO Auto-generated method stub
		return null;
	}

}
