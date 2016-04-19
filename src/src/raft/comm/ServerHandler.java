package src.raft.comm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.protobuf.ByteString;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import src.raft.context.RaftContext;
import src.raft.util.ImageViewer;
import src.raft.util.MessageProtoAdapter;
import src.raft.util.MessageType;
import test.proto.Protomessage.Message;

@SuppressWarnings("unused")
public class ServerHandler extends SimpleChannelInboundHandler<Message> {

	private ExecutorService workerPool;
	private static int ChunkCount = 1;
	private static HashMap<Integer, byte[]> chunks = new HashMap<Integer, byte[]>();
	private static HashMap<Integer,HashMap<Integer,byte[]>> file = new HashMap<Integer,HashMap<Integer,byte[]>>();
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
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

		final src.raft.util.Message m = MessageProtoAdapter.reverseAdapt(msg);
		String ip = ctx.channel().remoteAddress().toString();
		ip = ip.substring(1, ip.length() - 1).split(":")[0];
		if (!msg.getMessageType().equals(MessageType.ClientCommand)) {
			m.getId().setHost(ip);
		}
		if (m.getMessageType().equals(MessageType.ClientCommand)) {
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
				if (m.getMessageType().equals(MessageType.AppendEntries)) {
					RaftContext.getContext().getCurrentState().receiveHeartBeat(m);
				} else if (m.getMessageType().equals(MessageType.RequestVote)) {
					RaftContext.getContext().getCurrentState().receiveVoteRequest(m);
				} else if (m.getMessageType().equals(MessageType.VoteResponse)) {
					RaftContext.getContext().getCurrentState().receiveVoteResponse(m);
				} else if (m.getMessageType().equals(MessageType.AppendResponse)) {
					RaftContext.getContext().getCurrentState().receiveHearBeatResponse(m);
				} else if (m.getMessageType().equals(MessageType.ClientImage)) {
					FileOutputStream fileOuputStream = null;
					try {
						fileOuputStream = new FileOutputStream("incoming/"+m.getCommand());
						int numberOfChunk = m.getNumberOfChunk();
						if (ChunkCount == numberOfChunk) {
							chunks.put(m.getChunkId(), m.getImage().toByteArray());
							int size = chunks.size() * 1024;
							TreeMap<Integer, byte[]> sortedChunks = new TreeMap<Integer, byte[]>(chunks);
							ByteBuffer bb = ByteBuffer.allocate(size);
							Set<Integer> ids = sortedChunks.keySet();
							for (Integer id : ids) {
								bb.put(sortedChunks.get(id));
							}
							byte[] bFile = bb.array();
							fileOuputStream.write(bFile);
							ImageViewer.view("incoming/"+m.getCommand());
							System.out.println("**************** Previewing " + m.getCommand() + " ************");
							m.setImage(ByteString.copyFrom(bFile));
							for (int n : RaftContext.getContext().getClusterMap().keySet()) {
								Channel ch = RaftContext.getContext().getClusterMap().get(n);
								ch.writeAndFlush(m);
							}
							/*MongoClient client = new MongoClient("127.0.0.1",27017);
							DB db = client.getDB("ImageDB");
							GridFS fs = new GridFS(db);
							GridFSInputFile in = fs.createFile(bFile);
							in.setFilename(m.getCommand());
							in.save();
							client.close();
							System.out.println("Document inserted successfully");*/
							Create_Data();
							ChunkCount = 1;
						} else {
							ChunkCount++;
							chunks.put(m.getChunkId(), m.getImage().toByteArray());
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
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
	/*
	public void saveImageApp(byte[] imageBytes) {
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("imageDB");
			
			GridFS fs = new GridFS(db);
			GridFSInputFile in = fs.createFile(imageBytes);
			in.save();
			System.out.println("Document inserted successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}*/
	/*public void SaveImage( String filename) throws IOException
	{
		System.out.println("abcde---kf;;fs;fsd;lf;ldsf;lsd-------------------------------------------------------------------------------");
		
		try {
			System.out.println("abcde---kf;;fs;fsd;lf;ldsf;lsd-------------------------------------------------------------------------------");

			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			@SuppressWarnings("deprecation")
			DB db = mongoClient.getDB("ash2");
			System.out.println("abcde---kf;;fs;fsd;lf;ldsf;lsd-------------------------------------------------------------------------------");
			
	/*		GridFS fs = new GridFS(db);
			GridFSInputFile in = fs.createFile("filename");
			in.save();
			*/
		/*	
			String newFileName = "abcde";
			File imageFile = new File("incoming/test.jpg");
			GridFS gfsPhoto = new GridFS(db, "photo");
			GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);
			gfsFile.setFilename(newFileName);
			gfsFile.save();
			
			
			
			
			
			System.out.println("Document inserted successfully");
			
			GridFSDBFile imageForOutput = gfsPhoto.findOne(newFileName);
			System.out.println("Inserted Image :");
			
			System.out.println(imageForOutput);
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}*/
	
	public void Create_Data ()
	{
				System.out.println("inside cassendra ");
		      //queries
		      String query1 = "" ;
		                             
		     
		      //Creating Cluster object
		      Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		 
		      //Creating Session object
		      Session session = cluster.connect("tp");
		       
		      //Executing the query
		      session.execute(query1);
		        
		      
		        
		      System.out.println("Data created");
		   
		}
	
}