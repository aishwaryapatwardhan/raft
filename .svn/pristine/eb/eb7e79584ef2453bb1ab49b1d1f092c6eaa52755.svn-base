package src.raft.comm;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import poke.server.conf.ClusterConfList;
import poke.server.conf.ClusterConfList.ClusterConf;
import poke.server.conf.JsonUtil;
import src.raft.comm.nodes.NodeConfig;
import src.raft.comm.nodes.NodeConnectionManager;
import src.raft.comm.nodes.NodeData;
import src.raft.context.RaftContext;
import src.raft.util.ClusterNodes;

public class Server {

	private static int port;

	public Server(int port) {
		Server.port = port;
	}

	public void run() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();  
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();    
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ServerInitializer());

			final ChannelFuture cf = b.bind(port).sync();
			cf.addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> arg0)
						throws Exception {
					if(cf.isSuccess()){
						System.out.println("Server started");
						System.out.println("Listening on port "+port);
						ClusterConfInitializer.readConfig();
						NodeConnectionManager.getInstance().init();
						RaftContext.getContext().init();
						// code to read the configuration file and put it in the cluster map
						
					}

				}
			});
			
			cf.channel().closeFuture().sync(); 
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();  
		}
	}

	public static void main(String[] args) throws Exception {
		final int port;
		final String confFile = args[0];
		if (args.length > 0) {
			NodeData.setConf(confFile);
			port = NodeConfig.self.getPort();
		} else {
			System.out.println("Pass Configuration file as argument..");
			port = 8080;
			System.exit(0);
		}
		

		//HeartBeatManager.getInstance().init();
		new Server(port).run();
		
		
	}

	public static int getPort(){
		return port;
	}
	
			
}