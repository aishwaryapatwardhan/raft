package src.raft.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import src.raft.comm.nodes.Node;
import src.raft.context.Entry;

public class Persistence implements Serializable{
	
	private static final long serialVersionUID = 10L;
	
	private int persistentCurrentTerm;
	private Node persistentVotedFor;
	private ArrayList<Entry> persistentLog;
	
	public Persistence(){
		this.persistentCurrentTerm = 0;
		this.persistentVotedFor = null;
		this.persistentLog = new ArrayList<Entry>();
	}
	
	public Persistence(int currentTerm, Node votedFor, ArrayList<Entry> log){
		persistentCurrentTerm = currentTerm;
		persistentVotedFor = votedFor;
		persistentLog = log;
	}
	

	public int getPersistentCurrentTerm() {
		return persistentCurrentTerm;
	}

	public Node getPersistentVotedFor() {
		return persistentVotedFor;
	}

	public ArrayList<Entry> getPersistentLog() {
		return persistentLog;
	}
	
	@Override
	public String toString(){
		
		return new StringBuffer("Current Term: ")
		.append(persistentCurrentTerm)
		.append(" Voted For: ")
		.append(persistentVotedFor)
		.append(" Log: ")
		.append(persistentLog)
		.toString();
	}

	public void writeToFile(){
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				ObjectOutputStream oos = null;
//				try{
//					System.out.println("Writing to file");
//					File file = new File("file.txt");
//					if(!file.exists()){
//						file.createNewFile();
//					}
//					FileOutputStream fout = new FileOutputStream("file.txt");
//					oos  = new ObjectOutputStream(fout);   
//					oos.writeObject(Persistence.this);
//					
//					System.out.println("Commited to file...");
//
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//				finally{
//					if(oos != null){
//						try {
//							oos.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		}).start();
		
	}

	public Persistence readFromFile(){

//		ObjectInputStream ois = null;
//		Persistence pObj = null;
//		FileInputStream fin = null;
//		try {
//			System.out.println("Reading from file");
//			File file = new File("file.txt");
//			if(file.exists() && !file.isDirectory()) {
//				fin = new FileInputStream("file.txt");
//				ois = new ObjectInputStream(fin);
//				pObj = (Persistence) ois.readObject();
//				this.persistentCurrentTerm = pObj.getPersistentCurrentTerm();
//				this.persistentVotedFor = pObj.getPersistentVotedFor();
//				this.persistentLog = pObj.getPersistentLog();
//				
//				return pObj;
//			}
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		finally
//		{
//			try {
//				if(ois!=null)
//				{
//					ois.close();	
//				}
//				if(fin!=null)
//				{
//					fin.close();
//				}
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		}

		return null;

	}
	//test
//	public static void main(String[] args) {
//		
//		ArrayList<Entry> log = new ArrayList<Entry>();
//		Entry e = new Entry();
//		e.setTerm(12);
//		e.setCommand("test");
//		log.add(e);
//		Persistence pWrite = new Persistence(2, null, log);
//		System.out.println("pWrite object: ");
//		System.out.println("Persistent term: "+pWrite.getPersistentCurrentTerm());
//		System.out.println("Persistent voted for: "+pWrite.getPersistentVotedFor());
//		System.out.println("Persistant Log size: "+pWrite.getPersistentLog().size());
//		pWrite.writeToFile();
//		
//		
//		Persistence pRead = new Persistence();
//		pRead.readFromFile();
//		System.out.println("pRead object: ");
//		System.out.println("Persistent term: "+pRead.getPersistentCurrentTerm());
//		System.out.println("Persistent voted for: "+pRead.getPersistentVotedFor());
//		System.out.println("Persistant Log size: "+pRead.getPersistentLog().size());
//	}


}
