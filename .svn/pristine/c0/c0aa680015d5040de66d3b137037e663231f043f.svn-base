package src.raft.comm;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import poke.comm.App.ClientMessage;
import poke.comm.App.Request;
import src.raft.comm.nodes.NodeConfig;
import src.raft.comm.nodes.NodeConnectionManager;
import src.raft.comm.nodes.NodeConnector;
import src.raft.context.RaftContext;
import src.raft.util.Message;
import src.raft.util.MessageProtoAdapter;
import src.raft.util.MessageType;

public class ClusterServerHandler extends SimpleChannelInboundHandler<Request>{
		
	private NodeConnector myCluster = new NodeConnector("localhost", 8080);
	private NodeConnectionManager ncm;
	
	public ClusterServerHandler() {
		ncm = NodeConnectionManager.getInstance();
		ncm.init();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Request req)
			throws Exception {
		System.out.println("Cluster Message received from: "+arg0.channel().remoteAddress());
		
		StringBuilder msgCommand = new StringBuilder();
		
		//if the request has an image
		if(req.hasBody())
		{
			final ClientMessage clientMessage = req.getBody().getClusterMessage().getClientMessage();
			
			//send it in a new thread
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Message msg = new Message();
					msg.setMessageType(MessageType.ClientImage);
					msg.setImage(clientMessage.getMsgImageBits());
					msg.setCommand(clientMessage.getMsgImageName());
					
					ncm.sendMessageToAll(msg);					
				}
			}).start();
			
			//send it in a new thread
			//append in leader's log
			
			msgCommand.append("sender: "+clientMessage.getSenderUserName()+": ");
			msgCommand.append("image name: "+clientMessage.getMsgImageName()+ ": ");
			msgCommand.append("receiver: "+clientMessage.getReceiverUserName());
			
			Message msg = new Message(MessageType.ClientCommand,
					clientMessage.getSenderUserName(),
					clientMessage.getReceiverUserName(),
					msgCommand.toString(),
					NodeConfig.self);
			myCluster.sendMessage(MessageProtoAdapter.adapt(msg));
			
			
		}
		//if the req has join message
		else if(req.hasJoinMessage())
		{
			//add it to my cluster node list
			RaftContext.getContext().getClusterMap().put(req.getJoinMessage().getFromClusterId(), arg0.channel());
			System.out.println("received join message");
		}
		
		
		
		
		if(!myCluster.isConnected())
			myCluster.run();
		
		System.out.println("Sent to myCluster");
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    		throws Exception {
    	cause.printStackTrace();
    	ctx.close();
    }
	
}
