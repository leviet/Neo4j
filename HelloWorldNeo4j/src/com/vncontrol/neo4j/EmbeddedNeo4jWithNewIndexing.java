package com.vncontrol.neo4j;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

public class EmbeddedNeo4jWithNewIndexing {
	
	private static final String DB_PATH= "data/indexs";
	private static GraphDatabaseService graphDB;
	private IndexDefinition indexDefinition;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EmbeddedNeo4jWithNewIndexing emb = new EmbeddedNeo4jWithNewIndexing();
		emb.createIndexs();
		emb.addUser();
		emb.findUser(45);
	}
	
	public void createIndexs(){
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		//create index
		try ( Transaction tx = graphDB.beginTx() )
        {
            Schema schema = graphDB.schema();
            indexDefinition = schema.indexFor( DynamicLabel.label( "User" ) )
                    .on( "username" )
                    .create();
            tx.success();
        }
		//wait create index
		try ( Transaction tx = graphDB.beginTx() )
        {
            Schema schema = graphDB.schema();
            schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
        }
	}
	
	public void addUser(){
		try ( Transaction tx = graphDB.beginTx() )
        {
            Label label = DynamicLabel.label( "User" );

            // Create some users
            for ( int id = 0; id < 100; id++ )
            {
                Node userNode = graphDB.createNode( label );
                userNode.setProperty( "username", "user" + id + "@neo4j.org" );
            }
            System.out.println( "Users created" );
            tx.success();
        }
	}
	
	public void findUser(int idUser){
		// START SNIPPET: findUsers
        Label label = DynamicLabel.label( "User" );
        String nameToFind = "user" + idUser + "@neo4j.org";
        try ( Transaction tx = graphDB.beginTx() )
        {
            try ( ResourceIterator<Node> users =
                    graphDB.findNodesByLabelAndProperty( label, "username", nameToFind ).iterator() )
            {
                ArrayList<Node> userNodes = new ArrayList<>();
                while ( users.hasNext() )
                {
                    userNodes.add( users.next() );
                }

                for ( Node node : userNodes )
                {
                    System.out.println( "The username of user " + idUser + " is " + node.getProperty( "username" ) );
                }
            }
        }
        // END SNIPPET: findUsers
	}

}
