package src.raft.comm.nodes;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import test.proto.Protomessage.Message;

public class NodeConnector {

	private final String host;
	private final int port;
	private Queue<Message> tasks;
	private boolean connected = false;

	public NodeConnector(String host, int port) {
		this.host = host;
		this.port = port;
		tasks = new ConcurrentLinkedQueue<>();
	}

	public void run() throws Exception{    	
//		tasks.clear();
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.handler(new NodeConnectorInitializer());

		Channel ch;
		ch = b.connect(host, port).sync().channel();
		System.out.println("Connected to "+host+":"+port);
		connected = true;
		ChannelFuture lastWriteFuture = null;

		while (connected) {

			if(tasks.size() > 0){
				Message line = tasks.poll();
				lastWriteFuture = ch.writeAndFlush(line); 
				if (line == null) {
					break;
				}
			}
			Thread.sleep(100);

		}
		lastWriteFuture.channel().closeFuture().sync(); 
		System.out.println(host+" disconnected");
		group.shutdownGracefully();
	}

	public void sendMessage(Message msg){
		if(connected)
			tasks.offer(msg);
	}
	
	public void sendMsgStatic(Message msg){
		tasks.offer(msg);
	}

	public boolean isConnected(){
		return connected;
	}

	public class NodeConnectorInitializer extends ChannelInitializer<SocketChannel> {

		@Override
		public void initChannel(SocketChannel ch) throws Exception {

			ChannelPipeline pipeline = ch.pipeline();
			
	        pipeline.addLast ("frameDecoder", new ProtobufVarint32FrameDecoder ());
	        pipeline.addLast ("protobufDecoder", new ProtobufDecoder (Message.getDefaultInstance()));

	        pipeline.addLast ("frameEncoder", new ProtobufVarint32LengthFieldPrepender ());
	        pipeline.addLast ("protobufEncoder", new ProtobufEncoder ());

			pipeline.addLast("handler", new NodeConnectorHandler());
		}
	}

	public class NodeConnectorHandler extends SimpleChannelInboundHandler<Message> {
		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx)
				throws Exception {
			connected = true;
			tasks.clear();
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			connected = true;
		}


		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			ctx.close();
		}

		@Override
		protected void channelRead0(ChannelHandlerContext arg0, Message arg1)
				throws Exception {

			System.out.println("inside client channel read method");
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			connected = false;
//			System.out.println("channelInactive");
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
				throws Exception {
			connected = false;
//			System.out.println("channelUnregistered");
			tasks.clear();
		}

	}

}
