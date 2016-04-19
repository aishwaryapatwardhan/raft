package src.raft.comm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import poke.server.conf.ClusterConfList;
import poke.server.conf.JsonUtil;
import poke.server.conf.ClusterConfList.ClusterConf;

public class ClusterConfInitializer {

	public static void readConfig()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ClusterConfList  clusterConfList = ClusterConfList.getClusterConfList();
				BufferedInputStream br =null;
				File clusterCfg = new File("cluster.conf");
				try {
					byte[] raw = new byte[(int) clusterCfg.length()];
					// The java.io.BufferedInputStream.read() method reads the next byte
					// of data from the input stream.
					System.out.println("Reading the cluster communication configuration");

					br=new BufferedInputStream(new FileInputStream(clusterCfg));
					br.read(raw);
					clusterConfList = JsonUtil.decode(new String(raw),
					ClusterConfList.class);
					
					ClusterConf nodes = clusterConfList.getClusters().get(1);
					// logger.info("ClusterConf: "
					// + clusterConfList.getClusterNodes().get(1).getNodeName());
					//ResourceFactory.initializeCluster(clusterConfList);
					// logger.info("Cluster " + clusterConfList.getClusterId()
					// + " config initiated");
					System.out.println("Cluster Information saved");
					} 
				catch (Exception ex) {
					ex.printStackTrace();
				}
				finally
				{
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
				
			
		}).start();
	}	
}
