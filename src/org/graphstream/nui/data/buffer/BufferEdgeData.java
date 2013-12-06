package org.graphstream.nui.data.buffer;

import org.graphstream.nui.data.EdgeData;

public class BufferEdgeData extends EdgeData {
	protected BufferEdgeData(BufferUIDataset dataset, String id,
			BufferNodeData src, BufferNodeData trg, boolean directed) {
		super(dataset, id, src, trg, directed);
	}
}
