package poke.server.conf;

import java.util.ArrayList;
import java.util.TreeMap;

import src.raft.util.ClusterNodes;

public class ClusterConfList {

	private static ClusterConfList list= null;
	private ClusterConfList()
	{
		
	}
	public static ClusterConfList getClusterConfList() {
		if(list==null)
		{
			list= new ClusterConfList();
		}
		return list;
	}


	TreeMap<Integer, ClusterConf> clusters;

	public TreeMap<Integer, ClusterConf> getClusters() {
		return clusters;
	}

	public void setClusters(TreeMap<Integer, ClusterConf> clusters) {
		this.clusters = clusters;
	}

	public static class ClusterConf {
		private String clusterName;
		private ArrayList<ClusterNodes> clusterNodes;

		public String getClusterName() {
			return clusterName;
		}

		public void setClusterName(String clusterName) {
			this.clusterName = clusterName;
		}

		public ArrayList<ClusterNodes> getClusterNodes() {
			return clusterNodes;
		}

		public void setClusterNodes(ArrayList<ClusterNodes> clusterNodes) {
			this.clusterNodes = clusterNodes;
		}

	}
}