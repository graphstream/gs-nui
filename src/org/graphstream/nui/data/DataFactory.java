package org.graphstream.nui.data;

import org.graphstream.nui.UIDataset;

/**
 * This factory is used to create data for each elements (node, edge, sprite).
 * Developers can implement their own factory to customize the behavior of the
 * viewer.
 * 
 * @author Guilhelm Savin
 * 
 */
public interface DataFactory {
	/**
	 * Create a new data associated to a node.
	 * 
	 * @param nodeId
	 *            id of the node
	 * @return a new data for the node
	 */
	NodeData createNodeData(UIDataset dataset, String nodeId);

	/**
	 * Create a new data associated to an edge.
	 * 
	 * @param edgeId
	 *            id of the edge
	 * @param sourceId
	 *            id of the node which is the source od the edge
	 * @param targetId
	 *            id of the node which is the target of the edge
	 * @param directed
	 *            is the edge directed
	 * @return a new data for the edge
	 */
	EdgeData createEdgeData(UIDataset dataset, String edgeId, NodeData source,
			NodeData target, boolean directed);

	/**
	 * Create a new data associated to a sprite.
	 * 
	 * @param spriteId
	 *            id of the sprite
	 * @return a new data for the sprite
	 */
	SpriteData createSpriteData(UIDataset dataset, String spriteId);
}
