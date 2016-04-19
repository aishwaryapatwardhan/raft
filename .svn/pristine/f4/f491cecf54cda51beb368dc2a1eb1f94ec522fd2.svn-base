package src.raft.comm;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ClusterServer {
	
	private static int port;

	public ClusterServer(int port) {
		ClusterServer.port = port;
	}

	public void run() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();  
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();    
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ClusterServerInitializer());

			final ChannelFuture cf = b.bind(port).sync();
			System.out.println("Listening on port "+port);
			cf.channel().closeFuture().sync(); 
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();  
		}
	}

	public static void main(String[] args) throws Exception {
/*		final int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8282;
		}
		new ClusterServer(port).run();
*/	

		
	
	
	}

	public static int getPort(){
		return port;
	}

}
