package src.raft.util;

public class MessageType {
	public final static String AppendEntries = "AppendEntries";
	public final static String AppendResponse = "AppendResponse";
	public final static String RequestVote = "RequestVote";
	public final static String VoteResponse = "VoteResponse";
	public final static String ClientCommand = "ClientCommand";
	public final static String ClientImage = "ClientImage";
}
