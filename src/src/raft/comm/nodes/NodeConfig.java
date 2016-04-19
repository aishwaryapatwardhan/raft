package src.raft.comm.nodes;

import java.util.ArrayList;
import java.util.List;

public class NodeConfig {

	private static List<Node> nodes;

	public static Node self = NodeData.getSelfNode();

	public static List<Node> getNodeList() {
		nodes = NodeData.getNeighbours();
		return nodes;
	}
}