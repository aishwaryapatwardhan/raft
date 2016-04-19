package src.raft.util;

import test.proto.Protomessage;
import test.proto.Protomessage.Entry;
import test.proto.Protomessage.Message;
import test.proto.Protomessage.Node;
import test.proto.Protomessage.Result;

public class MessageProtoAdapter {
	
	public static Message adapt(src.raft.util.Message msg){
		Protomessage.Message.Builder m = Protomessage.Message.newBuilder();
		
		
		m.setMessageType(msg.getMessageType());
		m.setCommand(msg.getCommand());
		m.setLastLogIndex(msg.getLastLogIndex());
		m.setLastLogTerm(msg.getLastLogTerm());
		m.setLeaderCommit(msg.getLeaderCommit());
		m.setTerm(msg.getTerm());
		m.setSender(msg.getSender());
		m.setReceiver(msg.getReceiver());

		if(msg.getImage()!=null)
		{
			m.setImage(msg.getImage());
		}

		if (msg.getId()!=null)
		{
			Node.Builder nb = Node.newBuilder();
			nb.setHost(msg.getId().getHost());
			nb.setPort(msg.getId().getPort());
			
			m.setId(nb);
		}
		
		
		if(msg.getEntry() != null){
			Entry.Builder eb = Entry.newBuilder();
			eb.setCommand(msg.getCommand());
			eb.setTerm(msg.getEntry().getTerm());
			
			eb.setSender(msg.getEntry().getSender());
			eb.setReceiver(msg.getEntry().getReceiver());
			
			m.setEntry(eb);
		}
		
		if(msg.returned != null){
			Result.Builder rb = Result.newBuilder();
			rb.setSuccess(msg.returned.success);
			rb.setTerm(msg.returned.term);
			
			m.setReturned(rb);
		}
		
		return m.build();
	}

	public static src.raft.util.Message reverseAdapt(Message msg)
	{
		src.raft.util.Message m = new src.raft.util.Message();
		
		
		m.setMessageType(msg.getMessageType());
		m.setCommand(msg.getCommand());
		m.setLastLogIndex(msg.getLastLogIndex());
		m.setLastLogTerm(msg.getLastLogTerm());
		m.setLeaderCommit(msg.getLeaderCommit());
		m.setTerm(msg.getTerm());
		m.setSender(msg.getSender());
		m.setReceiver(msg.getReceiver());
		if(msg.hasImage())
		{
			m.setImage(msg.getImage());
		}
		
		
		if(msg.hasReturned())
		{
			src.raft.util.Message.Result r = new src.raft.util.Message().new Result();
			r.setSuccess(msg.getReturned().getSuccess());
			r.setTerm(msg.getReturned().getTerm());
			
			m.setReturned(r);
		}
		if(msg.hasEntry())
		{
			src.raft.context.Entry newEntry = new src.raft.context.Entry();
			
			newEntry.setCommand(msg.getEntry().getCommand());
			newEntry.setSender(msg.getEntry().getSender());
			newEntry.setReceiver(msg.getEntry().getReceiver());
			
			newEntry.setTerm(msg.getEntry().getTerm());
			m.setEntry(newEntry);
		}
		if(msg.hasId())
		{
			src.raft.comm.nodes.Node n = new src.raft.comm.nodes.Node(msg.getId().getHost(), msg.getId().getPort());
			m.setId(n);
		}
		
		return m;
	}
}
