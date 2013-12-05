package org.graphstream.nui;

import org.graphstream.nui.data.EdgeData;
import org.graphstream.nui.data.NodeData;
import org.graphstream.nui.data.SpriteData;

public interface UIDatasetListener {
	void dataNodeAdded(NodeData data);

	void dataNodeRemoved(NodeData data);

	void dataEdgeAdded(EdgeData data);

	void dataEdgeRemoved(EdgeData data);

	void dataSpriteAdded(SpriteData data);

	void dataSpriteRemoved(SpriteData data);
}
