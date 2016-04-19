package src.raft.util;

import java.io.Serializable;

import com.google.protobuf.ByteString;

import src.raft.comm.nodes.Node;
import src.raft.context.Entry;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	private String messageType; // append-entries or request-vote
	
	private int term; //message sender's (leader or candidate) term
	private Node id; //message sender's (leader or candidate) id
	
	private int lastLogIndex;
	private int lastLogTerm;
	
	//append-entries message only, make sure to set them before sending
	private Entry entry;
	private int leaderCommit = 0;
	private int sender;
	private int receiver;
	private String command="";

	private ByteString image;


	public Message()
	{
		
	}

	//constructor sets attributes common to both type of messages
	public Message(String messageType, int term, Node id, int lastLogIndex, int lastLogTerm){
		this.messageType = messageType;
		
		this.term = term;
		this.id = id;
		this.lastLogIndex = lastLogIndex;
		this.lastLogTerm = lastLogTerm;
		returned = new Result();
	}
	
	public Message(String messageType,int sender, int receiver, String command, Node self){
		this.messageType = messageType;
		
		this.id = self;
		this.sender = sender;
		this.receiver = receiver;
		this.command = command;
	}
	
	
	//getters and setters

	public int getLeaderCommit() {
		return leaderCommit;
	}

	public void setLeaderCommit(int leaderCommit) {
		this.leaderCommit = leaderCommit;
	}

	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String type) {
		messageType = type;
	}
	public int getTerm() {
		return term;
	}

	public Node getId() {
		return id;
	}

	public int getLastLogIndex() {
		return lastLogIndex;
	}

	public int getLastLogTerm() {
		return lastLogTerm;
	}




	//result to be written in object below (same object can be returned back)
	public Result returned;
	
	public class Result implements Serializable {
		
		private static final long serialVersionUID = 1L;
		public int term = -1;
		public int success = Constants.NULL;
		// -1 = null
		// 0 = false
		// 1 = true
		public int getTerm() {
			return term;
		}
		public void setTerm(int term) {
			this.term = term;
		}
		public int getSuccess() {
			return success;
		}
		public void setSuccess(int success) {
			this.success = success;
		}
		
	}

	public Entry getEntry() {
		return entry;
	}
	
	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	

	

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Result getReturned() {
		return returned;
	}

	public void setReturned(Result returned) {
		this.returned = returned;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public void setId(Node id) {
		this.id = id;
	}

	public void setLastLogIndex(int lastLogIndex) {
		this.lastLogIndex = lastLogIndex;
	}

	public void setLastLogTerm(int lastLogTerm) {
		this.lastLogTerm = lastLogTerm;
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public ByteString getImage() {
		return image;
	}

	public void setImage(ByteString image) {
		this.image = image;
	}



}
