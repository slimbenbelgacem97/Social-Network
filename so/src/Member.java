import org.neo4j.driver.Result;

import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;


public class Member {
	String login;
	String name;

	public Member(String login, String name) {
		this.login = login;
		this.name = name;
	
		
	}
	public void sendRequest(String login) {
		
		Network.conn.executeSet("MATCH (a:Member), (b:Member)\n"
				+ "WHERE a.login = '"+this.login+"' AND b.login = '"+login+"'\n"
				+ "CREATE (a)-[:Request]->(b)\n");
		
	}
	public void accept(String login) {
		
			Network.conn.executeSet("MATCH (a:Member)<-[r:Request]-(b:Member)\n"
					+ "WHERE a.login = '"+this.login+"' AND b.login = '"+login+"'\n"
					+ "delete r CREATE (a)-[:friend]->(b)\n"
					+ "CREATE (b)-[:friend]->(a)\n");
		
		
	}
	public void refuse(String login) {
		
		
		Network.conn.executeSet("MATCH (a:Member)<-[r:friend]-(b:Member)\n"
				+ "WHERE a.login = '"+this.login+"' AND b.login = '"+login+"'\n"
				+ "DELETE r");
		
	}
	public void postWall(Member member,String msg)throws Neo4jException {
		Post post = new Post(msg, this);
		Network.conn.executeSet("MATCH (a:Member), (b:Member)\n"
					+ "WHERE a.login = '"+this.login+"' AND b.login = '"+member.login+"'\n"
					+ "CREATE (p:post{ceacreationDate:'"+post.ceartiondate+"',message:'"+post.content+"'})\n"
					+ "CREATE (a)-[r:postedby]->(p)\n"
					+ "CREATE (p) -[c:postedto]->(b)");
	}
	public void likePage(String page) throws Neo4jException {
		Network.conn.executeSet("MATCH(a:Member),(g:Page) where a.login='"+login+"'and g.name='"+page
				+"'CREATE (a)-[r:follower]->(g) ");
		
	}
	public void joinGroup(String group) throws Neo4jException {
		Network.conn.executeSet("MATCH(a:Member),(g:Group) where a.login='"+login+"'and g.name='"+group
				+"'CREATE (a)-[r:joined]->(g) ");
	}
	public void createPage(String name,String gender) throws Neo4jException {
			Network.conn.executeSet("MATCH (a:Member) WHERE a.login=\""+this.login+"\""
								   + "CREATE (p:Page{name:\""+name+"\",creationDate:\""+System.currentTimeMillis()+"\",gender:\""+gender+"\"})"
								   + "CREATE (a)-[:owner]->(p)\n"
								   + "CREATE (p)<-[:follower]-(a)" );
	}
	public void createGroup(String name,String gender)throws  Neo4jException{
		Network.conn.executeSet( "MATCH (a:Member) WHERE a.login=\""+this.login+"\""
									+ "CREATE (g:Group{name:\""+name+"\",creationDate:\""+System.currentTimeMillis()+"\",gender:\""+gender+"\"})"
									+ "CREATE (a)-[:owner]->(g)\n"
									+ "CREATE (a)-[:joined]->(g)" );
	}
	public String Profile(Member member) {
		String friendName = null;
		Record record;

		Result result = Network.conn.executeGet("MATCH (a:Member)-[:friend]->(b:Member)where   a.login=\"" + this.login + "\"  return  b.name , b.login  ");
		StringBuilder profiles = null;
		while (result.hasNext()) {
			record = result.next();
			//Name
			friendName = record.get(0).asString();
			Result result1 = Network.conn.executeGet("MATCH (a:Member)-[:friend]->(b:Member)where   a.login=\"" + member.login + "\"  return  b.name AS firendname");
			//Friends
			StringBuilder friends = new StringBuilder();
			while (result1.hasNext()) {
				record = result1.next();
				friends.append(record.get(0).asString()).append("\t");
			}
			//Posts
			StringBuilder posts = new StringBuilder();
			Result result2 = Network.conn.executeGet("MATCH (m:Member)-[:postedby]->(p:post) where m.login=\"" + member.login + "\"  RETURN p.ceacreationDate as date, m.name as name , p.message as msg");
			while (result2.hasNext()) {
				record = result2.next();
				posts.append("name: ").append(record.get("name").asString()).append("| Creation Date : ").append(record.get("date").asString()).append(" | context: ").append(record.get("msg").asString()).append("\n");
			}
			//Pages
			Result result3 = Network.conn.executeGet("MATCH (a:Member)<-[:follower]-(g:Page) where a.login=\"" + member.login + "\" RETURN g.name as name , g.cereationDate as date,g.gender as gender  ");

			StringBuilder pages = new StringBuilder();
			while (result3.hasNext()) {
				record = result3.next();
				pages.append("name: ").append(record.get("name").asString()).append("| Creation Date : ").append(record.get("date").asString()).append(" | Gender: ").append(record.get("gender").asString()).append(" | like: ").append(record.get("member").asString()).append("\n");
			}
			//Groups
			StringBuilder groups = new StringBuilder();
			Result result4 = Network.conn.executeGet("MATCH (a:Member)-[r:owner]->(g:Group)where a.login=\"" + member.login + "\" RETURN g.name as name , g.creationDate as date,g.gender as gender ");
			while (result4.hasNext()) {
				record = result4.next();
				groups.append("name: ").append(record.get("name").asString()).append("| Creation Date : ").append(record.get("date").asString()).append(" | Gender: ").append(record.get("gender").asString()).append("\n");
			}
			profiles = new StringBuilder();
			profiles.append("[name= ").append(friendName).append("\nFriends: ").append(friends).append("\nGroups:").append(groups).append("\nLiked Pages: ").append(pages).append("\nPosts:").append(posts).append("]\n");
		}


		assert profiles != null;
		return profiles.toString();
	}

}
	

