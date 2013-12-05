package org.graphstream.nui.data.buffer;

import org.graphstream.nui.data.EdgeData;

public class BufferEdgeData extends EdgeData {
	protected BufferEdgeData(String id, BufferNodeData src, BufferNodeData trg,
			boolean directed) {
		super(id, src, trg, directed);
	}
}
