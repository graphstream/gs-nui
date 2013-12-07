package org.graphstream.nui.data.buffer;

import org.graphstream.nui.UIDataset;
import org.graphstream.nui.data.DataFactory;
import org.graphstream.nui.data.EdgeData;
import org.graphstream.nui.data.GraphData;
import org.graphstream.nui.data.NodeData;
import org.graphstream.nui.data.SpriteData;

public class BufferDataFactory implements DataFactory {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.data.DataFactory#createNodeData(java.lang.String)
	 */
	public NodeData createNodeData(UIDataset dataset, String nodeId) {
		return new BufferNodeData((BufferUIDataset) dataset, nodeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.data.DataFactory#createEdgeData(java.lang.String,
	 * org.graphstream.nui.data.NodeData, org.graphstream.nui.data.NodeData,
	 * boolean)
	 */
	public EdgeData createEdgeData(UIDataset dataset, String edgeId,
			NodeData source, NodeData target, boolean directed) {
		return new BufferEdgeData((BufferUIDataset) dataset, edgeId,
				(BufferNodeData) source, (BufferNodeData) target, directed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.data.DataFactory#createSpriteData(java.lang.String)
	 */
	public SpriteData createSpriteData(UIDataset dataset, String spriteId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.nui.data.DataFactory#createGraphData(org.graphstream.
	 * nui.UIDataset, java.lang.String)
	 */
	public GraphData createGraphData(UIDataset dataset, String graphId) {
		return new GraphData(dataset, graphId);
	}
}
