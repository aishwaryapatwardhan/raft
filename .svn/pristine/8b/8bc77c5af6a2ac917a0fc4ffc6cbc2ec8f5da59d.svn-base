package src.raft.comm.nodes;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import test.proto.Protomessage.Message;

public class CommandReplicator {

	private final String host;
	private final int port;
	private Message msg;

	public CommandReplicator(String host, int port, Message msg) {
		this.host = host;
		this.port = port;
		this.msg = msg;
	}

	public CommandReplicator(Node node, Message msg){
		host = node.getHost();
		port = node.getPort();
		this.msg = msg;
	}

	public void run() throws Exception{    	

		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.handler(new NodeConnectorInitializer());

		Channel ch;
		ch = b.connect(host, port).sync().channel();
		System.out.println("Sending command to "+host+":"+port);
		ChannelFuture lastWriteFuture = null;
		lastWriteFuture = ch.writeAndFlush(msg);
		lastWriteFuture.channel().closeFuture().sync(); 
		System.out.println("Command to "+host+" sent");
		group.shutdownGracefully();
	}

}
