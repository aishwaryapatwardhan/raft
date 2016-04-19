package src.raft.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import poke.comm.App;
import poke.server.conf.ClusterConfList;
import poke.server.conf.ClusterConfList.ClusterConf;
import src.raft.comm.nodes.ClusterNodeConnector;
import src.raft.comm.nodes.Node;
import src.raft.comm.nodes.NodeConfig;
import src.raft.comm.nodes.NodeConnectionManager;
import src.raft.context.Entry;
import src.raft.context.RaftContext;
import src.raft.util.ClusterNodes;
import src.raft.util.Common;
import src.raft.util.Constants;
import src.raft.util.Message;
import src.raft.util.MessageType;

public class LeaderState implements State {

	ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
	ScheduledFuture<?> heartBeatFuture;
	ScheduledFuture<?> clusterConnectFuture;
	private Node self;
	HashMap<String, Integer> matchIndex = new HashMap<String, Integer>();
	HashMap<String, Integer> nextIndex = new HashMap<String, Integer>();
	private int appendEntriesCount = 1;

	public LeaderState() 
	{		
		//		try {
		//			self =  new Node(Inet4Address.getLocalHost().getHostAddress(), Server.getPort());
		//			Set<Node> nodes = NodeConnectionManager.getInstance().getNodes();
		//			System.out.println(nodes.size());
		//			for(Node n: nodes)
		//			{
		//				matchIndex.put(n.getHost(), -1);
		//				nextIndex.put(n.getHost(),0);
		//			}
		//		} catch (UnknownHostException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}

	@Override
	public void sendHeartBeat() {

		System.out.println("Sending heartbeat");
		

		int randomTime = Common.generateRandomTime(1, 3);


		heartBeatFuture = 
				scheduler1.scheduleWithFixedDelay(new Runnable() {

					@Override
					public void run()
					{

						Set<Node> allNodes = NodeConnectionManager.getInstance().getNodes();	
						RaftContext selfContext =  RaftContext.getContext();

						//update commitIndex
						int N = findMaxOccuringIndexInMatchIndex();
						if(N > selfContext.getCommitIndex() &&
								selfContext.getLog().get(N).getTerm() == selfContext.getCurrentTerm())
						{
							selfContext.setCommitIndex(N);
						}


						for(Node n: allNodes)
						{
							if(!NodeConnectionManager.getInstance().isNodeConnected(n)){
								//reset nextindex matchindex
								
								matchIndex.put(n.getHostAndPort(), -1);
								nextIndex.put(n.getHostAndPort(), 0);
							}
							sendHeartBeatTo(n);
						}
					}
				}, 0,randomTime , TimeUnit.SECONDS);

	}

	private int findMaxOccuringIndexInMatchIndex(){

		Set<Node> allNodes = NodeConnectionManager.getInstance().getNodes();
		int arr[] = new int[allNodes.size()]; 
		int inc = 0;

		//get all indexes
		for(Node n: allNodes){
			arr[inc++] = matchIndex.get(n.getHostAndPort());
		}

		Arrays.sort(arr);
		int count = 1;


		for (int i=1; i < arr.length; i++) {
			if(arr[i-1] == arr[i]){
				count++;				
				//System.out.println(count);
				if(count > arr.length/2){
					//System.out.println(arr[i-1]);
					return arr[i-1];
				}

			}
			else
				count = 1;
		}

		return -1;
	}

	@Override
	public void receiveHeartBeat(Message heartBeat) {
		RaftContext self = RaftContext.getContext();
		System.out.println("Leader received heartbeat...");
		// the case when the leader joins the n/w back, if its current term is less than the 
		//incoming heartbeats term, then this leader becomes a follower.
		System.out.println("self term: "+self.getCurrentTerm()+" HB term: "+heartBeat.getTerm()+" log size: "+(self.getLog().size()-1)+" HB index: "+heartBeat.getLastLogIndex());

		heartBeat.setMessageType(MessageType.AppendResponse);		

		if(self.getCurrentTerm()<=heartBeat.getTerm())
		{

			if(self.getLog().size()-1<=heartBeat.getLastLogIndex())
			{
				System.out.println("Leader becoming follower....");
				heartBeatFuture.cancel(true);
				clusterConnectFuture.cancel(true);

				self.setCurrentTerm(Math.max(heartBeat.getTerm(),self.getCurrentTerm()));
				self.setCurrentState(Constants.FOLLOWER);
				self.getCurrentState().startWorking();
			}

		}

		self.setCurrentTerm(Math.max(heartBeat.getTerm(),self.getCurrentTerm()));
		heartBeat.returned.term = self.getCurrentTerm();
		
		Node node = NodeConnectionManager.getInstance().getNode(heartBeat.getId().getHost(), heartBeat.getId().getPort());
		System.out.println("sent to: "+heartBeat.getId().getHost());
		heartBeat.setId(this.self);
		NodeConnectionManager.getInstance().sendMessageToNode(node, heartBeat);
		System.out.println("Received HeartBeat Response sent for term:  " + heartBeat.getTerm() + "in leader to "
				+ heartBeat.getId().getHost()+":  " + heartBeat.returned.success);

	}

	@Override
	public void startWorking() {
		self =  NodeConfig.self;
		Set<Node> nodes = NodeConnectionManager.getInstance().getNodes();
		RaftContext selfContext = RaftContext.getContext(); 
		System.out.println(nodes.size());
		for(Node n: nodes)
		{
			matchIndex.put(n.getHostAndPort(), -1);
			nextIndex.put(n.getHostAndPort(), selfContext.getLog().size());
		}

		sendHeartBeat();
		connectClusterNodes();
		//		heartBeatFuture.cancel(false);
		//sendHeartBeatToNewlyActiveNodes();

	}

	private void connectClusterNodes() {
		


		System.out.println("Connecting cluster nodes");
		

		int randomTime = Common.generateRandomTime(1, 2);


		clusterConnectFuture = 
				scheduler1.scheduleWithFixedDelay(new Runnable() {

					@Override
					public void run()
					{
						// read from the cluster map and try to establish connection with nodes of the other cluster
						ClusterConfList clusterConf = ClusterConfList.getClusterConfList();
						
//						ClusterConf selfCluster = clusterConf.getClusters().get(2);
								
						for(Integer n : clusterConf.getClusters().keySet())
						{
							ClusterConf nodes = clusterConf.getClusters().get(n);
							Random r = new Random();
							int rand = r.nextInt(nodes.getClusterNodes().size());
							ClusterNodes node  = nodes.getClusterNodes().get(rand);
							
							App.JoinMessage.Builder joinMessage = App.JoinMessage.newBuilder();
							joinMessage.setFromNodeId(NodeConfig.self.getNodeId());
							joinMessage.setToNodeId(Integer.parseInt(node.getNodeId()));
							joinMessage.setFromClusterId(2);
							joinMessage.setToClusterId(n);
							
							try {
								ClusterNodeConnector nc = new ClusterNodeConnector(node.getHost(), node.getPort());
								nc.run();
								nc.sendMsgStatic(joinMessage.build());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					}
				}, 0,randomTime , TimeUnit.SECONDS);

	
		
	}

	
	@Override
	public void requestVotes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveVoteRequest(Message candidateState) {
		// TODO Auto-generated method stub

		/*
		 * Ideally would never be called.
		 */

		// decide whether to vote or not depending on the current term

		// discard vote if ur current term is greater than or equal to the incoming term
		// cast ur vote once

		System.out.println("Received a vote reqest from " + candidateState.getId().getHost() + "for the term: "
				+ candidateState.getTerm());


		RaftContext self = RaftContext.getContext();
		//		self.writeToFile();


		candidateState.returned.success = Constants.FALSE;

		ArrayList<Entry> log = self.getLog();	




		if(!self.getVoteBank().contains(candidateState.getTerm()) &&
				candidateState.getLastLogTerm() >= self.getCurrentTerm() &&
				candidateState.getLastLogIndex() >= log.size()-1
				)
		{
			if(candidateState.getTerm() >= self.getCurrentTerm()){

				heartBeatFuture.cancel(true);
				self.setVotedFor(candidateState.getId());
				self.getVoteBank().add(candidateState.getTerm());
				self.setCurrentTerm(candidateState.getTerm());
				candidateState.returned.success = Constants.TRUE;


				self.setCurrentTerm(Math.max(candidateState.getTerm(), self.getCurrentTerm()));

				candidateState.returned.term = self.getCurrentTerm();
				candidateState.setMessageType(MessageType.VoteResponse);
				Node node = NodeConnectionManager.getInstance().getNodeFromAddress(candidateState.getId().getHost());
				NodeConnectionManager.getInstance().sendMessageToNode(node, candidateState);
				System.out.println("Vote sent for term:  " + candidateState.getTerm() + " to "
						+ candidateState.getId().getHost()+":  " + candidateState.returned.success);


				RaftContext.getContext().setCurrentState(Constants.FOLLOWER);
				RaftContext.getContext().getCurrentState().startWorking();




				//become a follower

			}
			else
				candidateState.returned.success = Constants.FALSE;
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
	public void receiveVoteResponse(Message m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveClientCommand(Message m) {


		Entry newClientEntry = new Entry();
		newClientEntry.setTerm(RaftContext.getContext().getCurrentTerm());
		newClientEntry.setCommand(m.getCommand());
		newClientEntry.setSender(m.getSender());
		newClientEntry.setReceiver(m.getReceiver());


		RaftContext.getContext().getLog().add(newClientEntry);
		System.out.println("Log saved in Leader's Log");
	}

	public void sendHeartBeatTo(Node n)
	{

		
		RaftContext selfContext =  RaftContext.getContext();

		//check if node is connected and update nextindex matchindex
		/*if(NodeConnectionManager.getInstance().isNodeConnected(n)){
			System.out.println("Resetting nextindex matchindex for:"+n.getHost());
			matchIndex.put(n.getHost(), -1);
			nextIndex.put(n.getHost(), 0);
		}*/

		Entry entry = null;
		int lastLogTerm = 0;

		//initialize last log term
		// only if log.matchindex.get(..) returns an entry
		if (selfContext.getLog().size()>0){	

			if(matchIndex.get(n.getHostAndPort())==-1)
			{
				lastLogTerm = 0;
			}
			else
			{
				lastLogTerm = selfContext.getLog().get(matchIndex.get(n.getHostAndPort())).getTerm();
			}

		}

		//next entry to send (empty heart beat or with entry)
		if(selfContext.getLog().size()-1 >= nextIndex.get(n.getHostAndPort())){
			if(nextIndex.get(n.getHostAndPort())>-1)
			{
				entry = selfContext.getLog().get(nextIndex.get(n.getHostAndPort()));
			}

		}			


		Message msg = new Message(MessageType.AppendEntries,
				RaftContext.getContext().getCurrentTerm(),self,
				matchIndex.get(n.getHostAndPort()),lastLogTerm);


		msg.setLeaderCommit(selfContext.getCommitIndex());
		msg.setEntry(entry);
		//msg.setClientCommand(clientCommand);
		System.out.println("Sending Heartbeat to: "+n.getHost()+":"+n.getPort());
		NodeConnectionManager.getInstance().sendMessageToNode(n, msg);


	}

	@Override
	public void receiveHearBeatResponse(Message heartBeatResponse) {


		// TODO Auto-generated method stub
		RaftContext self = RaftContext.getContext();
		//self.writeToFile();

		System.out.println("Host: "+heartBeatResponse.getId().getHostAndPort());
		System.out.println("MatchIndex: "+matchIndex.get(heartBeatResponse.getId().getHostAndPort()));
		System.out.println("NextIndex: "+nextIndex.get(heartBeatResponse.getId().getHostAndPort()));

		if (heartBeatResponse.returned.term == self.getCurrentTerm())
		{

			String hostAndPort = heartBeatResponse.getId().getHostAndPort();
			System.out.println("Recevied heartbeat response from  " + hostAndPort+"for the term: "+ heartBeatResponse.getTerm() + "  " 
					+ heartBeatResponse.returned.success);
			System.out.println("host: "+ hostAndPort + " nextIndex.get(host): " + nextIndex.get(hostAndPort));
			if(heartBeatResponse.returned.success==Constants.TRUE)
			{
				//update matchIndex and nextIndex

				matchIndex.put(hostAndPort, matchIndex.get(hostAndPort)+1);
				if(matchIndex.get(hostAndPort) == nextIndex.get(hostAndPort))
					nextIndex.put(hostAndPort, nextIndex.get(hostAndPort)+1);

				//increment count
				if(matchIndex.get(heartBeatResponse.getId().getHostAndPort()) == self.getCommitIndex())
					appendEntriesCount++;

				{
					//check if leader receives write response to be true from majority  
					if(appendEntriesCount>=(((NodeConnectionManager.getInstance().getNumberOfActiveNodes()+1)/2)+1))
					{
						appendEntriesCount=1;
						System.out.println("Leader Commit Index updated to : "+(self.getCommitIndex()+1));
						//commit						
						//self.writeToFile();
						//if(matchIndex.get(heartBeat.getId().getHost()) == self.getCommitIndex())
						self.setCommitIndex(self.getCommitIndex()+1);
					}
					else
					{
						System.out.println("In receive HB else of leader");
					}
				}

			}
			else{
				//false is returned
				if(heartBeatResponse.returned.success == Constants.FALSE){
					if(heartBeatResponse.getEntry() != null)
						nextIndex.put(hostAndPort, nextIndex.get(hostAndPort)-1);
					//matchIndex.put(host, matchIndex.get(host)-1);
				}
			}


			// Send back heart beat to the same node
			/*Node n = NodeConnectionManager.getInstance().getNodeFromAddress(heartBeatResponse.getId().getHost());
			sendHeartBeatTo(n);
			 */
		}





	}
}
