package src.raft.states;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import src.raft.comm.Server;
import src.raft.comm.nodes.Node;
import src.raft.comm.nodes.NodeConfig;
import src.raft.comm.nodes.NodeConnectionManager;
import src.raft.context.RaftContext;
import src.raft.util.Common;
import src.raft.util.Constants;
import src.raft.util.Message;
import src.raft.util.MessageType;

public class CandidateState implements State {

	private Node self;
	private int voteCount =0; 

	private ScheduledFuture<?> timeOutFuture;
	ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
	Runnable task = new StartElection();
	public CandidateState() 
	{
		self =  NodeConfig.self;
	}

	@Override
	public void sendHeartBeat() {
		// TODO Auto-generated method stub



	}

	@Override
	public void receiveHeartBeat(Message heartBeat) {
		
		//the leader has been chosen and candidate becomes a follower
		System.out.println("Candidate received HB");
		RaftContext self = RaftContext.getContext();
//		self.writeToFile();
		heartBeat.setMessageType(MessageType.AppendResponse);
		Node node = NodeConnectionManager.getInstance().getNode(heartBeat.getId().getHost(), heartBeat.getId().getPort());
		
		if(heartBeat.getLastLogIndex()>=self.getLog().size()-1)
		{
			if(!(heartBeat.getTerm()<self.getCurrentTerm()))
			{
				
				timeOutFuture.cancel(false);
				self.setCurrentState(Constants.FOLLOWER);
				//self.getCurrentState().receiveHeartBeat(candidateState);			
				self.getCurrentState().startWorking();
			}
		}
			
		heartBeat.setId(this.self);
		
		NodeConnectionManager.getInstance().sendMessageToNode(node, heartBeat);
		System.out.println("Received HeartBeat Response sent for term:  " + heartBeat.getTerm() + "in candidate to "
				+ heartBeat.getId().getHost()+":  " + heartBeat.returned.success);
		self.setCurrentTerm(Math.max(self.getCurrentTerm(), heartBeat.getTerm()));

	}

	@Override
	public void startWorking() {

		
		RaftContext.getContext().setCurrentTerm(RaftContext.getContext().getCurrentTerm()+1);
		System.out.println("Candidate Started working for the term: " + RaftContext.getContext().getCurrentTerm());
		voteCount=0;
		if(timeOutFuture!=null)
		{
			timeOutFuture.cancel(false);
		}
		self =  NodeConfig.self;
		startElectionTimeOut();
		requestVotes();
		
	}
	public void startElectionTimeOut()
	{
		try {
			resetElectionTimeout();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void resetElectionTimeout() throws InterruptedException
	{
		int randomTime = Common.generateRandomTime(3, 5);
		System.out.println("election timeout: " + randomTime + " secs");
		timeOutFuture = 
				scheduler1.scheduleWithFixedDelay(task, randomTime,randomTime , TimeUnit.SECONDS);
	}
	
	@Override
	public void requestVotes() {
		/*System.out.println("Active nodes: "+NodeConnectionManager.getInstance().getNumberOfActiveNodes());
		System.out.println("Node list size: "+NodeConfig.getNodeList().size());
		System.out.println("Vote Count: "+voteCount);*/
		int lastLogTerm = 0;
		int lastLogIndex = -1;
		
		RaftContext selfContext = RaftContext.getContext();
		
		//this part requires some work and understanding
		if(selfContext.getLog().size() > 0)
		{
			lastLogTerm = selfContext.getLastEntry().getTerm();
			lastLogIndex = selfContext.getLog().size()-1;
		}

		Message msg = new Message(MessageType.RequestVote,
				selfContext.getCurrentTerm(),self,lastLogIndex ,lastLogTerm);
		voteCount+=1;
		RaftContext.getContext().getVoteBank().add(voteCount);
		System.out.println("sent request vote for term: "+RaftContext.getContext().getCurrentTerm());
		NodeConnectionManager.getInstance().sendMessageToAll(msg);
		
	}

	@Override
	public void receiveVoteRequest(Message candidateState) {

		//Receive a request vote from other followers and decide 
		// decide whether to vote or not depending on the current term
		
		System.out.println("Received a vote reqest from " + candidateState.getId().getHost()+":"+candidateState.getId().getPort() + "for the term: "
				+ candidateState.getTerm());

		candidateState.returned.success = Constants.FALSE;
		RaftContext self = RaftContext.getContext();
//		self.writeToFile();
		//candidateState.returned.term = self.getCurrentTerm();
		
		if(!self.getVoteBank().contains(candidateState.getTerm()))
		{
			// the condition where logs are empty.
			// Decision point is only the current term
			if(candidateState.getLastLogTerm()== 0&&
					candidateState.getLastLogIndex()==-1)
			{
				if(candidateState.getTerm() >= self.getCurrentTerm()){

					timeOutFuture.cancel(true);
					self.setVotedFor(candidateState.getId());
					self.getVoteBank().add(candidateState.getTerm());
					//self.setCurrentTerm(candidateState.getTerm());
					candidateState.returned.success = Constants.TRUE;
					
					RaftContext.getContext().setCurrentState(Constants.FOLLOWER);
					RaftContext.getContext().getCurrentState().startWorking();
					
					//become a follower
					
				}
				else
					candidateState.returned.success = Constants.FALSE;

			}
			else if(candidateState.getLastLogTerm()> 0 &&  // the condition when logs are not empty and 
					candidateState.getLastLogIndex()>=0)  // the decision is made considering all the three parameters
			{
				if(candidateState.getTerm() >= self.getCurrentTerm()){

					if(
							(candidateState.getLastLogTerm()>=self.getCurrentTerm())&&
							(candidateState.getLastLogIndex()>=self.getLog().size()-1))
					{
						timeOutFuture.cancel(true);
						self.setVotedFor(candidateState.getId());
						self.getVoteBank().add(candidateState.getTerm());
						//self.setCurrentTerm(candidateState.getTerm());
						candidateState.returned.success = Constants.TRUE;

						RaftContext.getContext().setCurrentState(Constants.FOLLOWER);
						RaftContext.getContext().getCurrentState().startWorking();
					}
				}
				else
					candidateState.returned.success = Constants.FALSE;
			}
		}
		
		self.setCurrentTerm(Math.max(candidateState.getTerm(), self.getCurrentTerm()));
		candidateState.returned.term = self.getCurrentTerm();
		candidateState.setMessageType(MessageType.VoteResponse);
		//Node node = NodeConnectionManager.getInstance().getNodeFromAddress(candidateState.getId().getHost());
		NodeConnectionManager.getInstance().sendMessageToNode(candidateState.getId(), candidateState);
		System.out.println("Vote sent for term:  " + candidateState.getTerm() + " to "
				+ candidateState.getId().getHost()+":  " + candidateState.returned.success);
	
		

	
	}

	@Override
	public void receiveVoteResponse(Message m)
	{
		/*System.out.println("Active nodes: "+NodeConnectionManager.getInstance().getNumberOfActiveNodes());
		System.out.println("Node list size: "+NodeConfig.getNodeList().size());
		*/System.out.println("Vote Count: "+voteCount);
		if(!(RaftContext.getContext().getCurrentTerm()<m.getTerm()))
		{
			System.out.println("recevied a vote from  " + m.getId().getHost()+"for the term: "+ m.getTerm() + "  " 
					+ m.returned.success);
			if(m.returned.success==Constants.TRUE &&
					m.returned.term == RaftContext.getContext().getCurrentTerm())
			{
				//increment count
				voteCount++;
				//check if majority number of active nodes are up
				//If majority no. of nodes are not up,
				//there will be no leader and the system will continue in the same way
				if((NodeConnectionManager.getInstance().getNumberOfActiveNodes()+1) >=
						((NodeConfig.getNodeList().size()+1)/2)+1)
				{
					//check if there are majority votes for the candidate 
					if(voteCount>=(((NodeConnectionManager.getInstance().getNumberOfActiveNodes()+1)/2)+1))
					{
						voteCount=0;
						timeOutFuture.cancel(true);
						RaftContext.getContext().setCurrentState(Constants.LEADER);
						RaftContext.getContext().getCurrentState().startWorking();
					}
				}
				
				//check for majority
				//become the leader if majority
			}

		}
		else
		{
			System.out.println("Discarded vote for the term: "+ m.getTerm());
		}
				

	}
	class StartElection implements Runnable
	{
		@Override
		public void run()
		{
			// become a candidate
			// send voting requests to all the nodes
			startWorking();
		}
		
		
	}
	@Override
	public void receiveClientCommand(Message m) {
		NodeConnectionManager.getInstance().sendMessageToNode(RaftContext.getContext().getLeaderNode(), m);
	}

	@Override
	public void receiveHearBeatResponse(Message m) {
		// TODO Auto-generated method stub
		
	}
}
