package src.raft.comm.nodes;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import src.raft.util.Message;
import src.raft.util.MessageProtoAdapter;

public class NodeConnectionManager {

	private static NodeConnectionManager INSTANCE;

	private ExecutorService remoteNodeThreadPool;
	private HashMap<Node, NodeConnector> nodeConnections;

	private NodeConnectionManager(){
		nodeConnections = new HashMap<>();
		for(Node node: NodeConfig.getNodeList()){
			NodeConnector nc = new NodeConnector(node.getHost(), node.getPort());
			nodeConnections.put(node, nc);
		}
		remoteNodeThreadPool = Executors.newFixedThreadPool(nodeConnections.keySet().size());
	}

	public static NodeConnectionManager getInstance(){
		if(INSTANCE == null)
			INSTANCE = new NodeConnectionManager();
		return INSTANCE;
	}

	public void init(){
		for(final Node node : nodeConnections.keySet()){

			remoteNodeThreadPool.execute(new Runnable() {

				@Override
				public void run() {

					while(true){
						try {
							nodeConnections.get(node).run();
						} catch (Exception e) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {

							}
						};
					}

				}
			});

		}
	}
	
	public Set<Node> getNodes(){
		return nodeConnections.keySet();
	}
	
	public void sendMessageToNode(Node node, Message msg){
		//NodeConnector nc = nodeConnections.get(node);
		for(Node n : nodeConnections.keySet()){
			if(n.getHost().equals(node.getHost()) && n.getPort() == node.getPort()){
				NodeConnector nc = nodeConnections.get(n);
				nc.sendMessage(MessageProtoAdapter.adapt(msg));
			}
				
		}
//		if(nc != null){
//			nc.sendMessage(MessageProtoAdapter.adapt(msg));
//		}
	}
	
	public void sendMessageToAll(Message msg){
		for(NodeConnector nc : nodeConnections.values()){
			nc.sendMessage(MessageProtoAdapter.adapt(msg));
		}
	}
	
	public Node getNodeFromAddress(String host){
		for(Node node : nodeConnections.keySet()){
			if(node.getHost().equals(host))
				return node;
		}
		return null;
	}
	
	public Node getNode(String host, int port){
		for(Node node : nodeConnections.keySet()){
			if(node.getHost().equals(host) && node.getPort() == port)
				return node;
		}
		return null;
	}
	
	public boolean isNodeConnected(Node node){
		NodeConnector nc = nodeConnections.get(node);
		if(nc != null)
			return nodeConnections.get(node).isConnected();
		return false;
	}
	
	public int getNumberOfActiveNodes(){
		int count = 0;
		for(NodeConnector nc : nodeConnections.values()){
			if(nc.isConnected())
				count++;
		}
		return count;
	}

}
