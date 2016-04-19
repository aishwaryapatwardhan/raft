package src.raft.states;

import src.raft.util.Message;

public interface State {

	public void sendHeartBeat();
	public void receiveHeartBeat(Message m);
	public void startWorking();
	
	public void requestVotes();
	public void receiveVoteRequest(Message msg);
	public void receiveVoteResponse(Message m);
	public void receiveClientCommand(Message m);
	public void receiveHearBeatResponse(Message m);
}
