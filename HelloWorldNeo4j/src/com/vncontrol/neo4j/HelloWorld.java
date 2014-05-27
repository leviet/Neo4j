package com.vncontrol.neo4j;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class HelloWorld {
	private static final String DB_PATH= "data/example";
	private enum RelTypes implements RelationshipType{
		 KNOWS;
	 }
	private static GraphDatabaseService graphDB;
	private Node firstNode;
	private Node secondNode;
	private Relationship relationship;
	
	public static void main(String[] args){
		HelloWorld hello = new HelloWorld();
		hello.createData();
		hello.showData();
		hello.removeData();
		hello.shutdown();
	}
	
	public void createData(){
		//create new graphDatabaseService
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		//begin transition
		Transaction transaction = graphDB.beginTx();
		try{
			//create node data
			firstNode = graphDB.createNode();
			firstNode.setProperty("name", "Hello");
			
			secondNode = graphDB.createNode();
			secondNode.setProperty("name", "World");
			
			//create raletionship
			relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
			relationship.setProperty("relationship", "knows");
			
			//transaction success
			transaction.success();
		}finally{
			//finish transaction
			//transaction.finish();
		}
	}
	
	public void showData(){
		try{
			System.out.print(firstNode.getProperty("name"));
			System.out.print(relationship.getProperty("relationship"));
			System.out.print(secondNode.getProperty("name"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void removeData(){
		Transaction transaction = graphDB.beginTx();
		try{
			firstNode.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
			
			//delete
			firstNode.delete();
			secondNode.delete();
		}finally{
			transaction.success();
		}
	}
	
	public void shutdown(){
		graphDB.shutdown();
	}
}

 