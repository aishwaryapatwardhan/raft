package src.raft.comm.nodes;

import java.util.ArrayList;
import java.util.List;

public class NodeConfig {
	
	private static List<Node> nodes;
	//Define you're machines ip and port here
	//public static Node self = new Node("192.168.0.13", 8080);
	public static Node self = NodeData.getSelfNode();
	
	
	public static List<Node> getNodeList(){
		
		nodes = NodeData.getNeighbours();
		
//		if(nodes == null){
//			nodes = new ArrayList<>();
//			
//			nodes.add(new Node("192.168.0.14", 8080));
//			nodes.add(new Node("192.168.0.12", 8080));
//			nodes.add(new Node("192.168.0.15", 8080));
//			nodes.add(new Node("192.168.0.49", 8080));
//		}
		
		return nodes;
	}

}
