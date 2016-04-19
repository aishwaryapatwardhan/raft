package src.raft.context;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import src.raft.comm.nodes.Node;
import src.raft.persistence.Persistence;
import src.raft.states.CandidateState;
import src.raft.states.FollowerState;
import src.raft.states.LeaderState;
import src.raft.states.State;
import src.raft.util.ClusterNodes;
import src.raft.util.Constants;


public class RaftContext 
{

	public static RaftContext context;
	
	private int currentTerm;
	HashSet<Integer> voteBank = new HashSet<Integer>();
	private Node votedFor;
	private boolean castedVote = false;
	private ArrayList<Entry> log = new ArrayList<Entry>();
	private State currentState;
	private Node leaderNode = null;
	private int commitIndex;
	private int lastApplied;;
	private HashMap<Integer, Channel> clusterMap = new HashMap<Integer, Channel>();
	
	public int getCommitIndex() {
		return commitIndex;
	}
	public void setCommitIndex(int commitIndex) {
		this.commitIndex = commitIndex;
	}
	public int getLastApplied() {
		return lastApplied;
	}
	public void setLastApplied(int lastApplied) {
		this.lastApplied = lastApplied;
	}
	private FollowerState followerState = new FollowerState();
	private CandidateState candidateState = new CandidateState();
	private LeaderState leaderState = new LeaderState();
	
	public Node getVotedFor() {
		return votedFor;
	}
	public void setVotedFor(Node votedFor) {
		this.votedFor = votedFor;
	}
	public ArrayList<Entry> getLog() {
		return log;
	}
	public void setLog(ArrayList<Entry> log) {
		this.log = log;
	}
	
	public void setLastEntry(Entry entry)
	{
		this.log.add(entry);
	}
	
	public Entry getLastEntry()
	{
		return log.get(log.size()-1);
	}
	
	private RaftContext()
	{
//		Persistence pObj = readFromFile();
//		if(pObj != null){
//			this.curentTerm = pObj.getPersistentCurrentTerm();
//			this.votedFor = pObj.getPersistentVotedFor();
//			this.log = pObj.getPersistentLog();
//		}
//		else{
			this.currentTerm = 0;
			this.votedFor = null;
			this.log = new ArrayList<Entry>();
//		}
		
		if(log == null){
			commitIndex = -1;
		}
		else
		{
			commitIndex = log.size()-1;
		}
		
		
		
	}
	public static RaftContext getContext()
	{
		if(context==null)
		{
			context = new RaftContext();
		}
		return context;
	}
	public State getCurrentState() {
		return currentState;
	}
	public void setCurrentState(String currentState) {
		switch(currentState)
		{
		case Constants.FOLLOWER: 
			this.currentState = followerState;	
			break;
		case Constants.CANDIDATE: 
			this.currentState = candidateState;
			break;
		case Constants.LEADER: 
			this.currentState = leaderState;
			break;
		}
		
	}
	public boolean isCastedVote() {
		return castedVote;
	}
	public Node getLeaderNode() {
		return leaderNode;
	}
	public void setLeaderNode(Node leaderNode) {
		this.leaderNode = leaderNode;
	}
	public void setCastedVote(boolean castedVote) {
		this.castedVote = castedVote;
	}
	public int getCurrentTerm() {
		return currentTerm;
	}
	public void setCurrentTerm(int currentTerm) {
		this.currentTerm = currentTerm;
	}
	public void init() {
		setCurrentState(Constants.FOLLOWER);
		getCurrentState().startWorking();
	}
	public HashSet<Integer> getVoteBank() {
		return voteBank;
	}
	public void setVoteBank(HashSet<Integer> voteBank) {
		this.voteBank = voteBank;
	}
	public HashMap<Integer, Channel> getClusterMap() {
		return clusterMap;
	}
	public void setClusterMap(HashMap<Integer, Channel> clusterMap) {
		this.clusterMap = clusterMap;
	}

	
//	
//	public void writeToFile(){
//		
//		ArrayList<Entry> tempLog = new ArrayList<Entry>();
//		
//		int i = 0;
//		while(i <= commitIndex){
//			tempLog.add(i, log.get(i));
//		}
//		Persistence pObj = new Persistence(currentTerm, votedFor, tempLog);
//		
//		pObj.writeToFile();
//	}
//	
//	public Persistence readFromFile(){
//		
//		Persistence pObj = new Persistence();
//		pObj.readFromFile();
//		System.out.println("Persistent term: "+pObj.getPersistentCurrentTerm());
//		System.out.println("Persistent voted for: "+pObj.getPersistentVotedFor());
//		System.out.println("Persistant Log size: "+pObj.getPersistentLog().size());
//		return pObj;
//	}
}
