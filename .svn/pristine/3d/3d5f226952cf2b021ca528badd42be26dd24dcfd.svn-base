package src.raft.comm;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.protobuf.ByteString;

import src.raft.comm.nodes.Node;
import src.raft.comm.nodes.NodeConfig;
import src.raft.util.MessageProtoAdapter;
import src.raft.util.MessageType;
import test.proto.Protomessage;
import test.proto.Protomessage.Message;

public class Client {

	public void run(Node sender, Node receiver, String fileName){
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		.channel(NioSocketChannel.class)
		.handler(new ClientInitializer());

		Channel ch;
		try {
			ch = b.connect(receiver.getHost(), receiver.getPort()).sync().channel();
			ChannelFuture lastWriteFuture = null;

			//Build image message
			src.raft.util.Message m = new src.raft.util.Message();
			m.setMessageType(MessageType.ClientImage);
			m.setImage(ByteString.copyFrom(extractBytes(fileName)));
			m.setCommand(fileName);
			m.setId(sender);

			//Build log message
			Protomessage.Message.Builder msg = Protomessage.Message.newBuilder();
			msg.setMessageType(MessageType.ClientCommand);
			//msg.setSender(1);
			//msg.setReceiver(1);
			msg.setCommand(sender.getHost()+":"+receiver.getHost()+":"+fileName);
			

			lastWriteFuture = ch.writeAndFlush(msg.build()).sync();
			ch.writeAndFlush(MessageProtoAdapter.adapt(m));
			lastWriteFuture.channel().closeFuture();
			group.shutdownGracefully();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	public static byte[] extractBytes (String ImageName) throws IOException {
		Path path = Paths.get(ImageName);
		return Files.readAllBytes(path);
	}
	public class ClientInitializer extends ChannelInitializer<SocketChannel>{

		@Override
		public void initChannel(SocketChannel ch) throws Exception {

			ChannelPipeline pipeline = ch.pipeline();


			pipeline.addLast ("frameDecoder", new ProtobufVarint32FrameDecoder ());
			pipeline.addLast ("protobufDecoder", new ProtobufDecoder (Message.getDefaultInstance()));

			pipeline.addLast ("frameEncoder", new ProtobufVarint32LengthFieldPrepender ());
			pipeline.addLast ("protobufEncoder", new ProtobufEncoder ());



			pipeline.addLast("handler", new ClientHandler());
		}
	}

	public class ClientHandler extends SimpleChannelInboundHandler<Message> {

		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			ctx.close();
		}

		@Override
		protected void channelRead0(ChannelHandlerContext arg0, Message arg1)
				throws Exception {

		}

	}

	public static void main(String ar[]){
		String fromIP = ar[0];
		int fromPort = Integer.parseInt(ar[1]);
		String toIP = ar[2];
		int toPort = Integer.parseInt(ar[3]);
		String ImgPath = ar[4];
		new Client().run(new Node(fromIP, fromPort), new Node(toIP, toPort), ImgPath);
		
	}

}
