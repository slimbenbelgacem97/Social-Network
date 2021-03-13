import org.jetbrains.annotations.NotNull;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;


public class Network {
	public static  Connexion conn ;
	private static String login="login";

	public static void createNewMember() throws IOException{
		BufferedReader h = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Welcome to our network! Sign Up and find new friends.\nEnter your login:");
		String login = h.readLine();
		System.out.println("Enter your name:");
		String name = h.readLine();
			try {
				Network.conn.executeSet("CREATE (a:Member{login:\""+login+"\",name:\""+name+"\"})" );
			}catch (Exception e) {e.printStackTrace();}
	}

	public static @NotNull
	LinkedHashMap<String , Member> suggestFirends(Member member) {
		ArrayList<Member> file= new ArrayList<>();
		ArrayList<Integer> friendCost= new ArrayList<>();
		ArrayList<Integer> pageCost= new ArrayList<>();
		for(int i=0; i<2;i++) {
			friendCost.add(0);
			pageCost.add(0);
			file.add(null);
		}


		Result result = Network.conn.executeGet( "MATCH (a:Member)-[:friend]->(b:Member)-[:friend]->(c:Member) where  a.login='"+member.login+"' and  a.login<>c.login \n"
				+" return DISTINCT  c.login as login , c.name as name" );
		Record record;
		int fc;
		int pc;
		 while(result.hasNext()){
	    	   System.out.println("1");
	    	   pc = 0;
	    	   record = result.next();
	    	   Member m = new Member(record.get(login).asString(), record.get("name").asString());
	    	   Result result1 = Network.conn.executeGet( "MATCH (a:Member)-[r:friend]->(c:Member),(b:Member)-[:friend]->(c) where   a.login='"+member.login+"' and  b.login='"+record.get("login").asString()+"'    return  count(r)  as cout" );
	    	   fc = result1.next().get("cout").asInt();
	    	   Result result2 = Network.conn.executeGet( "match (a:Member)<-[r:follower]-(p:Page),(b:Member)<-[:follower]-(p:Page) where a.name='"+member.login+"' and b.login='"+record.get("login").asString()+"' return count(r)" );
	    	   pc+=result2.next().get(0).asInt();
	    	   int indmin=0;
    	   		for(int i =1; i< pageCost.size();i++) {
    	   			if(friendCost.get(indmin)+pageCost.get(indmin)>friendCost.get(i)+pageCost.get(indmin))
    	   				indmin = i;
    	   		}
    	   		if(friendCost.get(indmin)+pageCost.get(indmin)<fc+pc) {
    	   			friendCost.set(indmin, fc);
    	   			pageCost.set(indmin, pc);
    	   			file.set(indmin, m);
    	   		}
		 }
		 result = Network.conn.executeGet( "match (a:Member)<-[:follower]-(:Page)-[:follower]->(b:Member) where a.login='"+member.login+"' and b.login <> a.login  return DISTINCT  b.login as login , b.name as name" );
		 while(result.hasNext()){
    		   record = result.next();
    		   Member m = new Member(record.get(login).asString(), record.get("name").asString());
    		   Result result1 = Network.conn.executeGet( "match (a:Member)<-[r:follower]-(:Page)-[:follower]->(b:Member) where a.login='"+m.login+"' and b.login='"+member.login+"' return count(r) as cout" );
    		   pc = result1.next().get("cout").asInt();
    		   int indmin=0;
    		   for(int i =1; i< pageCost.size();i++) {
    			   if(friendCost.get(indmin)+pageCost.get(indmin)>friendCost.get(i)+pageCost.get(indmin))
   	   				indmin = i;
    		   }
    		   if(friendCost.get(indmin)+pageCost.get(indmin)<pc) {
   	   			friendCost.set(indmin, 0);
   	   			pageCost.set(indmin, pc);
   	   			file.set(indmin, m);
   	   		}
    	  }

		 LinkedHashMap<String, Member> h= new LinkedHashMap<>();
		 for(int i =0 ;i<file.size();i++) {
			 h.put("friends : "+friendCost.get(i)+" pages: "+pageCost.get(i),file.get(i));

		 }
		 return h;
	}
	public static void main(String[] args) throws Exception {
		Network.conn= new Connexion( "bolt://localhost:7687", "neo4j", "social" );
		Network.conn.CreateSession();

	   while(true) {

		   System.out.println("1:Create Member");
		   System.out.println("2:Login");
		   System.out.println("3:Close app");

		   try {
			   Scanner s= new Scanner(System.in);
			   String ch =s.next();
			   if(ch.equals("3")){
			   System.out.println("GOODBYE!!");
			   conn.close();
			   break;
		   }
			   Member m;
			   switch (ch) {
			   		// Create Member
				   case "1" -> Network.createNewMember();
				   //Login
				   case "2" -> {
					   System.out.println("Login:");
					   ch = s.next();
					   try (Session session = Network.conn.driver.session()) {
						   Result result = session.run("MATCH (a:Member) where a.login=\"" + ch + "\" return  a.login as login , a.name as name");

						   if (result.hasNext()) {
							   Record record = result.next();
							   m = new Member(record.get(login).asString(), record.get("name").asString());
							   System.out.println("Welcome back " + record.get("name").asString() + "!");
							   while (true) {

								   System.out.println("1:Research");
								   System.out.println("2:Display Friends List");
								   System.out.println("3:Groups");
								   System.out.println("4:Pages");
								   System.out.println("5:Sugggetion");
								   System.out.println("6:display request List");
								   System.out.println("0: back");

								   ch = s.next();
								   if (ch.equals("0"))// Back
									   break;
								   switch (ch) {
								   	   // Research
									   case "1": {
										   while (true) {
											   System.out.println("1:Member");
											   System.out.println("2:Page");
											   System.out.println("3:Group");
											   System.out.println("4:All");
											   System.out.println("0:back");
											   ch = s.next();
											   if (ch.equals("0"))
												   break;

											   switch (ch) {    //Research Member
												   case "1":{
													   System.out.println("Enter the member's name:");
													   result = session.run("MATCH (n:Member) where n.name=\"" + s.next() + "\"RETURN   n.name as name ,n.login as login");
													   System.out.println("Member(s) found:\n");
													   ArrayList<Member> sereach = new ArrayList<>();
													   int i = 0;
													   while (result.hasNext()) {
														   record = result.next();
														   System.out.println(i + ": " + record.get("name").asString());
														   sereach.add(new Member(record.get("login").asString(), record.get("name").asString()));
														   i++;
													   }
													   while (true) {
														   System.out.println("1: display Profile");
														   System.out.println("2: sendRequest");
														   System.out.println("0: back");
														   ch = s.next();
														   if (ch.equals("1") || ch.equals("2")) {
															   System.out.println("select the nember of a member ");
															   int index = new Integer(s.next()).intValue();
															   if (ch.equals("1")) {
																   System.out.println(m.Profile(sereach.get(index)));
																   System.out.println("1: sendRequest");
																   System.out.println("0: back");
																   ch = s.next();
																   if (ch.equals("1"))
																	   m.sendRequest((sereach.get(index).login));
																   else if (ch.equals("0")) {
																   } else {
																	   System.out.println("invalide input");
																   }
															   } else if (ch.equals("2"))
																   m.sendRequest((sereach.get(index).login));
														   } else if (ch.equals("0")) break;
														   else System.out.println("invalide input");
													   }
												   }
													   break;
													   // Research Page
												   case "2":{
													   System.out.println("Enter the page's name:");
													   result = session.run("MATCH (n:Page)where n.name=\"" + s.next() + "\" RETURN  n.name as name");
													   while (result.hasNext()) {
														   record = result.next();
														   System.out.println("Page(s) found:\n");
														   System.out.println(record.get("name").asString());
													   }
												   }
													   break;
													   //Research Group
												   case "3": {
													   System.out.println("Enter the group's name:");
													   result = session.run("MATCH (n:Group)where n.name=\"" + s.next() + "\" RETURN  n.name as name LIMIT 25");
													   while (result.hasNext()) {
														   record = result.next();
														   System.out.println("Group(s) found:\n");
														   System.out.println(record.get("name").asString());
													   }
												   }
													   break;
												   case "4":  // Research All
													   System.out.println("Enter the name:");
													   result = session.run("MATCH (n)where n.name=\"" + s.next() + "\" RETURN n.name as name, labels(n)  LIMIT 25");
													   while (result.hasNext()) {
														   record = result.next();
														   System.out.println("Group(s) found:\n");
														   System.out.println(record.get("name").asString() + "  |  " + record.get("labels").asString());
													   }
													   break;

												   default:
													   break;
											   }
										   }
									   }
										   break;
								   	  // Display Friends List
									   case "2": {
										   result = session.run("MATCH (a:Member),(b:Member)where (a) -[:friend]->(b) and (b) -[:friend]->(a) and  a.login=\"" + m.login + "\"  return  b.name as name, b.login as login");
										   int i = 0;
										   ArrayList<Member> friends = new ArrayList<>();
										   while (result.hasNext()) {
											   record = result.next();
											   System.out.println(i + ": " + record.get("name").asString());
											   friends.add(new Member(record.get("login").asString(), record.get("name").asString()));
											   i++;
										   }

										   label:
										   while (true) {
											   System.out.println("\n1: Display friend");
											   System.out.println("2: post");
											   System.out.println("3: Display post");
											   System.out.println("0: back");
											   ch = s.next();

											   switch (ch) {
												   case "1": {    // Display Friend Profile
													   System.out.println("select the nember of a friend ");
													   int index = new Integer(s.next()).intValue();
													   System.out.println(m.Profile(friends.get(index)));

													   break;
												   }
												   case "2": {    // Post on a firend wall
													   System.out.println("select the nember of a friend to post on his wall");
													   int index = new Integer(s.next()).intValue();

													   System.out.println("Message:\n");
													   String msg = s.next();
													   m.postWall(friends.get(index), msg);
													   System.out.println("Posted!");
													   break;
												   }
												   case "0": // Break while
													   break label;
												   case "3": { // Display post
													   result = session.run("MATCH (m:Member)-[:postedby]->(p:post)-[:postedto]->(c:Member) where m.login=\"" + m.login + "\"  RETURN p.ceacreationDate as date, m.name as name , p.message as msg , c.name as to");
													   System.out.println("Your posts are:");

													   while (result.hasNext()) {
														   record = result.next();
														   System.out.println("name: " + record.get(1).asString() + "| Creation Date : " + record.get(0).asString() + " | context: " + record.get(2).asString() + " | to: " + record.get(3).asString());
													   }
												   }
												   break;
												   default:
													   System.out.println("Invalid choice");
													   break;
											   }

										   }
									   }
										   break;
								   	  //Groups
									   case "3": {
										   while (true) {
											   System.out.println("1: Create a Group");
											   System.out.println("2: Display your groups");
											   System.out.println("3: Join a Group");
											   System.out.println("0: back");
											   ch = s.next();
											   if (ch.equals("0"))
												   break ;
											   switch (ch) { //Create a Group
												   case "1":{
													   System.out.println("Name your group:\n");
													   String nameg = s.next();
													   System.out.println("The gender of your group:\n");
													   String genderG = s.next();
													   m.createGroup(nameg, genderG);
												   }
													   break;
												   case "2":{//  Display your groups
													   result = session.run("MATCH p=(a:Member)-[r:owner]->(g:Group)<-[:joined]-(b:Member) where a.login=\"" + m.login + "\" RETURN g.name as name , g.creationDate as date,g.gender as gender,b.name as member  ");
													   System.out.println("Your Groups are:");
													   while (result.hasNext()) {
														   record = result.next();
														   System.out.println("name: " + record.get("name").asString() + "| Creation Date : " + record.get("date").asString() + " | Gender: " + record.get("gender").asString() + " | Mebmers: " + record.get("member").asString());
													   }
												   }
													   break;
												   case "3":{	// Join a Group
													   System.out.println("Enter the name of the group that you want to join:");
													   m.joinGroup(s.next());
												   }
													   break;
												   default:
													   break;
											   }

										   }
									   }
										   break;
									   case "4":// Pages
										   while (true) {
											   System.out.println("1: Create a Page");
											   System.out.println("2: Display your pages");
											   System.out.println("3: Like a page");
											   System.out.println("0: back");
											   ch = s.next();
											   if (ch.equals("0"))
												   break;
											   switch (ch) {
												   case "1":{ //Create a Page
													   System.out.println("Name your page:\n");
													   String nameg = s.next();
													   System.out.println("The gender of your page:\n");
													   String genderG = s.next();
													   m.createPage(nameg, genderG);
												   }
													   break;
												   case "2": {	//Display your pages
													   result = session.run("MATCH p=(a:Member)-[r:owner]->(g:Page)<-[:follower]-(b:Member) where a.login=\"" + m.login + "\" RETURN g.name as name , g.creationDate as date,g.gender as gender,Collect(b.name) as member  ");
													   System.out.println("Your pages are:");
													   while (result.hasNext()) {
														   record = result.next();
														   System.out.println("name: " + record.get("name").asString() + "| Creation Date : " + record.get("date").asString() + " | Gender: " + record.get("gender").asString() + " | followers: " + record.get("member").toString());
													   }
												   }
													   break;
												   case "3":{ // Like a page
													   System.out.println("Enter the name of the page that you want to follow :");
													   m.likePage(s.next());
												   }
												   break;

												   default:
													   break;
											   }

										   }
										   break;

									   case "5": // suggestion
										   LinkedHashMap l = Network.suggestFirends(m);
										   int ii = 0;
										   ArrayList<Member> lm = new ArrayList<Member>();
										   Member mm;
										   for (Object o : l.entrySet()) {
											   Map.Entry mapentry = (Map.Entry) o;
											   mm = (Member) mapentry.getValue();
											   if(mm!=null){
											   		System.out.println(ii + ": " + mm.name + " |" + mapentry.getKey() + "|");
											   		lm.add(mm);
											   		ii++;
											   }
										   }
										   while (true) {
											   System.out.println("1: display Profile");
											   System.out.println("2: sendRequest");
											   System.out.println("0: back");
											   ch = s.next();
											   if (ch.equals("1") || ch.equals("2")) {
												   System.out.println("select the nember of a member ");
												   int index = new Integer(s.next()).intValue();
												   if (ch.equals("1")) {
													   System.out.println(m.Profile(lm.get(index)));
													   System.out.println("1: sendRequest");
													   System.out.println("0: back");
													   ch = s.next();
													   if (ch.equals("1"))
														   m.sendRequest((lm.get(index).login));
													   else if (ch.equals("0")) {
													   } else {
														   System.out.println("invalide input");
													   }
												   } else if (ch.equals("2"))
													   m.sendRequest((lm.get(index).login));
											   } else if (ch.equals("0")) break;
											   else System.out.println("invalide input");
										   }
										   break;
									   case "6": // Display
										   result = session.run("MATCH (b:Member) -[:Request]->(a:Member)   where a.login=\"" + m.login + "\"  return  b.name as name, b.login as login");
										   int i1 = 0;
										   ArrayList<String> requesList = new ArrayList<>();
										   while (result.hasNext()) {
											   record = result.next();
											   System.out.println(i1 + ": " + record.get("name").asString());
											   requesList.add(record.get("login").asString());
											   i1++;
										   }
										   while (true) {
											   System.out.println("\n1: Accept");
											   System.out.println("2: Reject");
											   System.out.println("0: back");
											   ch = s.next();
											   if (ch.equals("0"))
												   break;
											   switch (ch) {
												   case "1":
													   System.out.println("Select the nember of the invitation that you want accept ");
													   int ind = new Integer(s.next()).intValue();
													   m.accept(requesList.get(ind));
													   System.out.println("Your are friends now!");
													   break;
												   case "2":
													   System.out.println("Select the nember of the invitation that you want accept ");
													   ind = new Integer(s.next()).intValue();
													   m.refuse(requesList.get(ind));
												   default:
													   break;
											   }
										   }
										   break;
									   case "8":

										   break;
									   default:
										   break;
								   }
							   }
						   } else {
							   System.out.println("Welcome! Create your aucount now!");
						   }

					   } catch (Exception e) {
						   System.out.println(e.getMessage());
					   }
				   }
				   default -> System.out.println("Invalid choice");
			   }
		} catch (Exception e) {	System.out.println(e.getMessage());		}



	  }
	}
}