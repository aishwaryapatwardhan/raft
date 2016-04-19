package src.raft.comm.nodes;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

import src.raft.util.Message;

public class NodeConnectorHandler extends SimpleChannelInboundHandler<Message> {

	private static final Logger logger = Logger.getLogger(NodeConnectorHandler.class.getName());

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Message arg1)
			throws Exception {
		
	}

}