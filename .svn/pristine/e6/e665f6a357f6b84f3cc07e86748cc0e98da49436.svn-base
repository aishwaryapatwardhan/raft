package src.raft.states;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import src.raft.comm.Server;
import src.raft.comm.nodes.Node;
import src.raft.comm.nodes.NodeConfig;
import src.raft.comm.nodes.NodeConnectionManager;
import src.raft.context.Entry;
import src.raft.context.RaftContext;
import src.raft.util.Common;
import src.raft.util.Constants;
import src.raft.util.Message;
import src.raft.util.MessageType;


public class FollowerState implements State {

	
	private ScheduledFuture<?> timeOutFuture;
	ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
	ScheduledFuture<?> heartBeatFuture;
	Runnable task = new BecomeCandidate();
	int randomTime;
	
	private Node self;
	
	//RaftContext context = RaftContext.getContext();
	
	public ScheduledFuture<?> getTimeOutFuture() {
		return timeOutFuture;
	}

	public void setTimeOutFuture(ScheduledFuture<?> timeOutFuture) {
		this.timeOutFuture = timeOutFuture;
	}

	@Override
	public void sendHeartBeat() {
		
	}

	@Override
	public void receiveHeartBeat(Message heartBeat) 
	{
		RaftContext self = RaftContext.getContext();
		//self.writeToFile();
		System.out.println("heartbeat received.. resetting timeout.. current term: " + 
				self.getCurrentTerm());
		System.out.println("Log Size: "+self.getLog().size());
		
		//process the heart beat only if its from a genuine leader and not a stale leader
		/*if(!(self.getCurrentTerm()>heartBeat.getTerm()))
		{}
		
		else
			self.setCurrentTerm(Math.max(heartBeat.getTerm(),self.getCurrentTerm()));
		*/

		Node leader = NodeConnectionManager.getInstance().getNode(heartBeat.getId().getHost(), heartBeat.getId().getPort());
	
		//set leader node
		self.setLeaderNode(leader);
		//System.out.println("Leader Set to: "+ heartBeat.getId().getHost());


		

		if (heartBeat.getMessageType().equals(MessageType.AppendEntries))
			appendEntries(heartBeat);

		
		
		
		timeOutFuture.cancel(true);
		try {
			
			resetElectionTimeout();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private void appendEntries(Message heartBeat) {
		int index = heartBeat.getLastLogIndex();
		int term = heartBeat.getLastLogTerm();
		Entry newEntry = heartBeat.getEntry();
		RaftContext selfContext = RaftContext.getContext();
		ArrayList<Entry> log = selfContext.getLog();
		
		System.out.println("term: "+heartBeat.getTerm()+
				" node id: "+heartBeat.getId().getHost()+
				" port: "+heartBeat.getId().getPort()+
				" lastlogindex: "+index+
				" lastlogterm: "+term+
				" leaderCommit: "+heartBeat.getLeaderCommit()+
				" entry: "+newEntry);	
		
		
		if(index != -1){

			if(log.size()==index+1&&log.get(index).getTerm()==term)	
			{
				doAppend(newEntry, index, heartBeat, selfContext, log);
			}
			else
			{
				/*if(log.size()<index+1)
				{
					//return false to reduce matchindex by 1
					heartBeat.returned.success = Constants.FALSE;
				}
				else
				{*/
					if(log.size()>index+1)
					{
						if (log.get(index+1).getCommand()==newEntry.getCommand()&&log.get(index+1).getTerm()==newEntry.getTerm())								
						{
							heartBeat.returned.success = Constants.TRUE;
							System.out.println("Entry already present at index: "+(index+1));
							if (heartBeat.getLeaderCommit()>selfContext.getCommitIndex()&&Math.min(heartBeat.getLeaderCommit(),index+1)!=selfContext.getCommitIndex())				
								selfContext.setCommitIndex(Math.min(heartBeat.getLeaderCommit(),index+1));
						}
						else					
						{
							heartBeat.returned.success = Constants.FALSE;
							while(log.get(index) != null && log.size()-index != 0) {
								log.remove(index);
								System.out.println("Bad entries removed");
							}
						}
					}
				}

//			}
		}
		else
		{
			doAppend(newEntry, index, heartBeat, selfContext, log);
		}
		
		heartBeat.setMessageType(MessageType.AppendResponse);
		selfContext.setCurrentTerm(Math.max(heartBeat.getTerm(),selfContext.getCurrentTerm()));
		heartBeat.returned.term = selfContext.getCurrentTerm();
		
		Node node = NodeConnectionManager.getInstance().getNode(heartBeat.getId().getHost(), heartBeat.getId().getPort());
		heartBeat.setId(self);
		NodeConnectionManager.getInstance().sendMessageToNode(node, heartBeat);
		System.out.println("Append Response sent for term:  " + heartBeat.getTerm() + " to "
				+ heartBeat.getId().getHost()+ " port:  "+ heartBeat.getId().getPort() +":  " + heartBeat.returned.success);
		
	}

	private void doAppend(Entry newEntry, int index, Message heartBeat, RaftContext selfContext, ArrayList<Entry> log) {		
		if (newEntry!=null){
			Entry e = new Entry();
			e.setCommand(newEntry.getCommand());
			e.setTerm(newEntry.getTerm());
			log.add(index+1,e);
			System.out.println("Entry appended at index: "+(index+1)+" Command : "+newEntry.getCommand());
			heartBeat.returned.success = Constants.TRUE;	
		}
		else
			System.out.println("Empty HeartBeat");

		if (heartBeat.getLeaderCommit()>selfContext.getCommitIndex()&&Math.min(heartBeat.getLeaderCommit(),index+1)!=selfContext.getCommitIndex())				
			{
				selfContext.setCommitIndex(Math.min(heartBeat.getLeaderCommit(),index+1));
				System.out.println("Setting Commit Index to : "+Math.min(heartBeat.getLeaderCommit(),index+1));
			}
	}

	public void startWorking()
	{
		System.out.println("In Follwer.. starting to work");
		self =  NodeConfig.self;
		startElectionTimeOut();
		//sendHeartBeat();
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
		randomTime = Common.generateRandomTime(1, 4);
		System.out.println("election timeout: " + randomTime + " secs");
		//Server.getChannels().write("Election timeout in " + randomTime + "secs");
		timeOutFuture = 
				scheduler1.scheduleWithFixedDelay(task, randomTime,randomTime , TimeUnit.SECONDS);
	}
	

	@Override
	public void requestVotes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveVoteRequest(Message candidateState) {
	
		// decide whether to vote or not depending on the current term
		
		// discard vote if ur current term is greater than or equal to the incoming term
		// cast ur vote once
		
		System.out.println("Received a vote reqest from " + candidateState.getId().getHost() + "for the term: "
				+ candidateState.getTerm());


		RaftContext self = RaftContext.getContext();
		
		 //update followers term
		
		
		candidateState.returned.success = Constants.FALSE;
		if(!self.getVoteBank().contains(candidateState.getTerm()))
		{
			// the condition where logs are empty.
			// Decision point is only the current term
			if(candidateState.getLastLogTerm()== 0&&
					candidateState.getLastLogIndex()==-1)
			{
				if(candidateState.getTerm() >= self.getCurrentTerm()){
					try {
						timeOutFuture.cancel(true);
						resetElectionTimeout();
						System.out.println("follower resetting....");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					candidateState.returned.success = Constants.TRUE;
					self.setCurrentTerm(candidateState.getTerm());
					//candidateState.returned.term = self.getCurrentTerm();
					self.setVotedFor(candidateState.getId());
					self.getVoteBank().add(candidateState.getTerm());
					
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
						try {
							timeOutFuture.cancel(true);
							resetElectionTimeout();
							System.out.println("follower resetting....");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						candidateState.returned.success = Constants.TRUE;
						self.setCurrentTerm(candidateState.getTerm());
						candidateState.returned.term = self.getCurrentTerm();
						self.setVotedFor(candidateState.getId());
						self.getVoteBank().add(candidateState.getTerm());
					}
				}
				else{
					candidateState.returned.success = Constants.FALSE;
				}
			}
		}
		
		self.setCurrentTerm(Math.max(candidateState.getTerm(), self.getCurrentTerm()));
		candidateState.returned.term = self.getCurrentTerm();
		candidateState.setMessageType(MessageType.VoteResponse);
		//Node node = NodeConnectionManager.getInstance().getNodeFromAddress(candidateState.getId().getHost());
		NodeConnectionManager.getInstance().sendMessageToNode(candidateState.getId(), candidateState);
		System.out.println("Vote sent for term:  " + candidateState.getTerm() + " to "
				+ candidateState.getId().getHost()+":"+candidateState.getId().getPort()+" "+ candidateState.returned.success);
//		self.writeToFile();
	
	}
	class BecomeCandidate implements Runnable
	{
		@Override
		public void run()
		{
			// become a candidate
			// send voting requests to all the nodes
			
			System.out.println("becoming candidate");
			//heartBeatFuture.cancel(true);
			timeOutFuture.cancel(true);
			RaftContext.getContext().setCurrentState(Constants.CANDIDATE);
			RaftContext.getContext().getCurrentState().startWorking();
		}
		
	}
	@Override
	public void receiveVoteResponse(Message m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveClientCommand(Message m) {
		System.out.println("Received client command.. Forwarding it to the leader: " + RaftContext.getContext().getLeaderNode().getHostAndPort());
		NodeConnectionManager.getInstance().sendMessageToNode(RaftContext.getContext().getLeaderNode(), m);
	
	}

	@Override
	public void receiveHearBeatResponse(Message m) {
		// TODO Auto-generated method stub
		
	}
}

