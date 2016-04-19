package src.raft.comm.nodes;

import java.io.Serializable;

public class Node implements Serializable{
	
	private String host;
	private int port;
	private int nodeId;
	
	private static final long serialVersionUID = 3L;
	
	public Node(String host, int port){
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getHostAndPort(){
		return host+":"+port;
	}

}
