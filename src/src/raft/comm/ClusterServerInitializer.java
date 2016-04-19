package src.raft.comm;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class ClusterServerInitializer  extends ChannelInitializer<SocketChannel> {

/*    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
//        pipeline.addLast("decoder", (new ObjectDecoder(ClassResolvers.softCachingResolver(ClassLoader.getSystemClassLoader()))));  
//        pipeline.addLast("encoder", new ObjectEncoder());  
        
        // add decoders and encoders.
        pipeline.addLast ("frameDecoder", new ProtobufVarint32FrameDecoder ());
        pipeline.addLast ("protobufDecoder", new ProtobufDecoder (ClusterMessage.getDefaultInstance()));

        pipeline.addLast ("frameEncoder", new ProtobufVarint32LengthFieldPrepender ());
        pipeline.addLast ("protobufEncoder", new ProtobufEncoder ());
        
        pipeline.addLast("handler", new ClusterServerHandler());
    }
    */
	
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		// Enable stream compression (you can remove these two if unnecessary)
		/**
		* length (4 bytes).
		* 
		* Note: max message size is 64 Mb = 67108864 bytes this defines a
		* framer with a max of 64 Mb message, 4 bytes are the length, and strip
		* 4 bytes
		*/
		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(67108864, 0, 4, 0, 4));
		// pipeline.addLast("frameDecoder", new
		// DebugFrameDecoder(67108864, 0, 4, 0, 4));
		// decoder must be first
		pipeline.addLast("protobufDecoder", new ProtobufDecoder(poke.comm.App.Request.getDefaultInstance()));
		pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
		pipeline.addLast("protobufEncoder", new ProtobufEncoder());
		// our server processor (new instance for each connection)
		pipeline.addLast("handler", new ClusterServerHandler());
		}
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    		throws Exception {
    	cause.printStackTrace();
    	ctx.close();
    }
}
