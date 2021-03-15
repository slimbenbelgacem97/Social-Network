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
	static Member m;
	static Scanner s= new Scanner(System.in);
	static ArrayList<Member> lm = new ArrayList<>();
	static ArrayList<Member> search = new ArrayList<>();
	static Result result;
	static Record record;
	static String choice="Invalid choice!";

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
		Network.conn.createSession();
	   mainLoop:
	   while(true) {
	   		// Main Menu
		   System.out.println("1:Create Member");
		   System.out.println("2:Login");
		   System.out.println("3:Close app");
		   try {
			   String ch =s.next();
			   switch (ch) {
			   		// Create Member
				   case "1" -> Network.createNewMember();
				   //Login
				   case "2" -> {
					   System.out.println("Login:");
					   userActivities(s.next());
				   }
				   case "3"->{
					   System.out.println("GOODBYE!!");
					   conn.close();
					   break mainLoop;
				   }
				   default -> System.out.println("Invalid choice");
			   }
		} catch (Exception e) {	System.out.println(e.getMessage());}
	  }
	}
	private static void userActivities(String ch) {
		try (Session session = Network.conn.driver.session()) {
			//userLogin
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
						case "1" -> research(ch);
						// Display Friends List
						case "2" -> {
							ArrayList<Member> friends;
							String friendsCypher = "MATCH (a:Member),(b:Member)where (a) -[:friend]->(b) and (b) -[:friend]->(a) and  a.login=\"" + m.login + "\"  return  b.name as name, b.login as login";
							friends = prepareList(friendsCypher);
							displayFriends:
							while (true) {
								System.out.println("\n1: Display friend");
								System.out.println("2: post");
								System.out.println("3: Display post");
								System.out.println("0: back");
								ch = s.next();
								int index;
								switch (ch) {
									// Display Friend Profile
									case "1" -> {
										System.out.println("select the nember of a friend ");
										index = Integer.parseInt(s.next());
										System.out.println(m.profile(friends.get(index)));
									}
									// Post on a firend wall
									case "2" -> {
										System.out.println("select the nember of a friend to post on his wall");
										index = Integer.parseInt(s.next());
										System.out.println("Message:\n");
										String msg = s.next();
										m.postWall(friends.get(index), msg);
										System.out.println("Posted!");
									}
									// Break while
									case "0" -> {
										break displayFriends;
									}
									// Display post
									case "3" -> {
										result = session.run("MATCH (m:Member)-[:postedby]->(p:post)-[:postedto]->(c:Member) where m.login=\"" + m.login + "\"  RETURN p.ceacreationDate as date, m.name as name , p.message as msg , c.name as to");
										System.out.println("Your posts are:");

										while (result.hasNext()) {
											record = result.next();
											System.out.println("name: " + record.get(1).asString() + "| Creation Date : " + record.get(0).asString() + " | context: " + record.get(2).asString() + " | to: " + record.get(3).asString());
										}
									}

									default -> System.err.println("Invalid choice");
								}

							}
						}
						//Groups
						case "3" -> {
							loopGroup:
							while (true) {
								System.out.println("1: Create a Group");
								System.out.println("2: Display your groups");
								System.out.println("3: Join a Group");
								System.out.println("0: back");
								ch = s.next();

								switch (ch) {
									//Create a Group
									case "1" -> {
										System.out.println("Name your group:\n");
										String nameg = s.next();
										System.out.println("The gender of your group:\n");
										String genderG = s.next();
										m.createGroup(nameg, genderG);
									}
									//  Display your groups
									case "2" -> {
										result = conn.executeGet("MATCH p=(a:Member)-[r:owner]->(g:Group)<-[:joined]-(b:Member) where a.login=\"" + m.login + "\" RETURN g.name as name , g.creationDate as date,g.gender as gender,b.name as member  ");
										System.out.println("Your Groups are:");
										while (result.hasNext()) {
											record = result.next();
											System.out.println("name: " + record.get("name").asString() + "| Creation Date : " + record.get("date").asString() + " | Gender: " + record.get("gender").asString() + " | Mebmers: " + record.get("member").asString());
										}
									}
									// Join a Group
									case "3" -> {
										System.out.println("Enter the name of the group that you want to join:");
										m.joinGroup(s.next());
									}
									//Back
									case "0" -> {
										break loopGroup;
									}
									default -> System.err.println(choice);
								}

							}
						}
						// Pages
						case "4" ->{
						loopPage: while (true) {
							System.out.println("1: Create a Page");
							System.out.println("2: Display your pages");
							System.out.println("3: Like a page");
							System.out.println("0: back");
							ch = s.next();
							switch (ch) {
								//Create a Page
								case "1"-> {
									System.out.println("Name your page:\n");
									String nameg = s.next();
									System.out.println("The gender of your page:\n");
									String genderG = s.next();
									m.createPage(nameg, genderG);
								}
								//Display your pages
								case "2"->{
									result = conn.executeGet("MATCH p=(a:Member)-[r:owner]->(g:Page)<-[:follower]-(b:Member) where a.login=\"" + m.login + "\" RETURN g.name as name , g.creationDate as date,g.gender as gender,Collect(b.name) as member  ");
									System.out.println("Your pages are:");
									while (result.hasNext()) {
										record = result.next();
										System.out.println("name: " + record.get("name").asString() + "| Creation Date : " + record.get("date").asString() + " | Gender: " + record.get("gender").asString() + " | followers: " + record.get("member").toString());
									}
								}
								// Like a page
								case "3"->{
									System.out.println("Enter the name of the page that you want to follow :");
									m.likePage(s.next());
								}
								// Back
								case "0"->{break loopPage;}
								default->{System.err.println(choice);}

							}
						}
					}
						// suggestion
						case "5"->{
							LinkedHashMap<String,Member> l = Network.suggestFirends(m);
							int ii = 0;

							Member mm;
							for (Map.Entry<String,Member> o : l.entrySet()) {
								mm = o.getValue();
								if(mm!=null){
									System.out.println(ii + ": " + mm.name + " |" + o.getKey() + "|");
									lm.add(mm);
									ii++;
								}
							}
							sendAction(lm);
						}
						// Display
						case "6"-> {
							String requestCypher="MATCH (b:Member) -[:Request]->(a:Member)   where a.login=\"" + m.login + "\"  return  b.name as name, b.login as login";
							result = conn.executeGet(requestCypher);
							int i1 = 0;
							ArrayList<String> requestList = new ArrayList<>();
							while (result.hasNext()) {
								record = result.next();
								System.out.println(i1 + ": " + record.get("name").asString());
								requestList.add(record.get("login").asString());
								i1++;
							}
							displayLoop:
							while (true) {
								System.out.println("\n1: Accept");
								System.out.println("2: Reject");
								System.out.println("0: back");
								ch = s.next();
								int ind;
								switch (ch) {
									// Accept
									case "1"-> {
										System.out.println("Select the nember of the invitation that you want accept ");
										ind = Integer.parseInt(s.next());
										m.accept(requestList.get(ind));
										System.out.println("Your are friends now!");
									}
									//Reject
									case "2"-> {
										System.out.println("Select the nember of the invitation that you want accept ");
										ind = Integer.parseInt(s.next());
										m.refuse(requestList.get(ind));
									}
									//Back
									case "0"->{break displayLoop;}
									default->System.err.println(choice);

								}
							}
						}

						default->System.err.println(choice);
					}
				}
			} else {
				System.out.println("Welcome! Create your aucount now!");
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	private static void sendAction(ArrayList<Member>list) {
		String ch;
		int index;
		label:while (true) {
			System.out.println("1: Display Profile");
			System.out.println("2: Send Request");
			System.out.println("0: Back");
			String action = s.next(); //
			//Display Profile or Send Request

			switch (action) {
				//Display Profile
				case "1" -> {
					System.out.println("select the number of a member ");
					index =  Integer.parseInt(s.next());
					System.out.println(m.profile(list.get(index)));
					System.out.println("1: sendRequest");
					System.out.println("0: back");
					ch = s.next();
					if (ch.equals("1")) {
						m.sendRequest((list.get(index).login));
					} else if (ch.equals("0")) {
						break label;
					} else {
						System.err.println(choice);
					}
				}
				//Send Request
				case "2" ->{
					System.out.println("select the number of a member ");
					index =  Integer.parseInt(s.next());
					m.sendRequest((search.get(index).login));
				}
				//Back
				case "0" -> {
					break label;
				}
				default -> System.out.println("invalide input");
			}
		}
	}
	private static void research(String ch) {
		while (true) {
			System.out.println("1:Member");
			System.out.println("2:Page");
			System.out.println("3:Group");
			System.out.println("4:All");
			System.out.println("0:back");
			ch = s.next();
			if (ch.equals("0"))
				break;
			switch (ch) {
				//Research Member
				case "1"->{
					System.out.println("Enter the member's name:");
					researchMember(s.next());
				}

				// Research Page
				case "2"-> {
					System.out.println("Enter the page's name:");
					researchPage(s.next());
				}
				//Research Group
				case "3"-> {
					System.out.println("Enter the group's name:");
					researchGroup(s.next());
				}
				// Research All
				case "4"-> {
					System.out.println("Enter the name:");
					researchAll(s.next());
				}
				default -> System.err.println(choice);
			}
		}
	}
	private static void researchAll(String next) {
		result = conn.executeGet("MATCH (n)where n.name=\"" + next + "\" RETURN n.name as name, labels(n)  LIMIT 25");
		while (result.hasNext()) {
			record = result.next();
			System.out.println("Group(s) found:\n");
			System.out.println(record.get("name").asString() + "  |  " + record.get("labels").asString());
		}
	}
	private static void researchGroup(String next) {

		result = conn.executeGet("MATCH (n:Group)where n.name=\"" +next + "\" RETURN  n.name as name LIMIT 25");
		while (result.hasNext()) {
			record = result.next();
			System.out.println("Group(s) found:\n");
			System.out.println(record.get("name").asString());
		}
	}
	private static void researchPage(String next) {

		result = conn.executeGet("MATCH (n:Page)where n.name=\"" + next + "\" RETURN  n.name as name");
		while (result.hasNext()) {
			record = result.next();
			System.out.println("Page(s) found:\n");
			System.out.println(record.get("name").asString());
		}
	}
	private static void researchMember(String next) {
		String cypher ="MATCH (n:Member) where n.name=\"" + next + "\"RETURN   n.name as name ,n.login as login";

		System.out.println("Member(s) found:\n");
		search= prepareList(cypher);
		sendAction(search);
	}
	private static ArrayList<Member> prepareList(String cypher){
		ArrayList<Member> list = new ArrayList<>();
		result = conn.executeGet(cypher);
		int i=0;
		while (result.hasNext()) {
			record = result.next();
			System.out.println(i + ": " + record.get("name").asString());
			Member me = new Member(record.get("login").asString(), record.get("name").asString());
			list.add(me);
			i++;
		}
		return list;
	}
}