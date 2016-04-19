package src.raft.comm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import src.raft.context.RaftContext;
import src.raft.util.ImageViewer;
import src.raft.util.MessageProtoAdapter;
import src.raft.util.MessageType;
import test.proto.Protomessage.Message;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
	
	private ExecutorService workerPool;
	
	public ServerHandler() {
		workerPool = Executors.newFixedThreadPool(10);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		
		
		final src.raft.util.Message m = MessageProtoAdapter.reverseAdapt(msg);
		
		
		String ip = ctx.channel().remoteAddress().toString();
		ip = ip.substring(1, ip.length()-1).split(":")[0];
		if(!msg.getMessageType().equals(MessageType.ClientCommand))
		{
			m.getId().setHost(ip);
		}
		if(m.getMessageType().equals(MessageType.ClientCommand))
		{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					RaftContext.getContext().getCurrentState().receiveClientCommand(m);
				}
			}).start();
			
		}
		workerPool.execute(new Runnable() {
			
			@Override
			public void run() {
				if(m.getMessageType().equals(MessageType.AppendEntries))
				{
					RaftContext.getContext().getCurrentState().receiveHeartBeat(m);
				}
				else if(m.getMessageType().equals(MessageType.RequestVote))
				{
					RaftContext.getContext().getCurrentState().receiveVoteRequest(m);
				}
				else if(m.getMessageType().equals(MessageType.VoteResponse))
				{
					RaftContext.getContext().getCurrentState().receiveVoteResponse(m);
				}
				else if(m.getMessageType().equals(MessageType.AppendResponse))
				{
					RaftContext.getContext().getCurrentState().receiveHearBeatResponse(m);
				}else if(m.getMessageType().equals(MessageType.ClientImage)){
					FileOutputStream fileOuputStream = null;
					try {
						
						byte[] bFile = m.getImage().toByteArray();//message.getMsgImageBits().toByteArray();
						fileOuputStream = new FileOutputStream("incoming/test.jpg");
						fileOuputStream.write(bFile);
					
						ImageViewer.view("incoming/test.jpg");
						System.out.println("**************** Previewing "+m.getCommand()+" ************");
						
						
						//forward this image to nodes in the other cluster
						for(int n: RaftContext.getContext().getClusterMap().keySet())
						{
							Channel ch = RaftContext.getContext().getClusterMap().get(n);
							
							//send msg to this node in the form of request message
							ch.writeAndFlush(m);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					finally
					{
						try {
							fileOuputStream.flush();
							fileOuputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			}
		});

	}

}