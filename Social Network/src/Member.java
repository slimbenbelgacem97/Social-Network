import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Member {
	String name;
	ArrayList<Member> friends;
	ArrayList<Post> wall = new ArrayList<>();
	ArrayList<Member> requestsList = new ArrayList<>();
	ArrayList<Page> likedPages= new ArrayList<>();
	ArrayList<Group> groups = new ArrayList<>();  
	
	public Member(String name) {
		
		this.name = name;
		friends = new ArrayList<Member>();
		
	}
	
	public void addRequest(Member member) {
		
		if(friends.indexOf(member)<0) {
			requestsList.add(this);
		}
		
	}
	public void accept(Member member) {
		
		friends.add(member);
		requestsList.remove(member);
		System.out.println("La demande est accept?e! Vous amis avec "+ member.name);
		
	}
	public void refuse(Member member) {
		
		requestsList.remove(member);
		System.out.println("La demande est r?jet?e.");
		
	}
	public void postWall(Member member) {
		
		System.out.println("Exprimer vous!");
		DataInputStream s = new DataInputStream(System.in);
		try {
			Post post = new Post(s.readLine(), this);
			member.wall.add(post);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	public void likePage(Page page) {
		
		this.likedPages.add(page);
		page.likes++;
		//aaaaaa
		page.members.add(this);
		
	}
	public void joinGroup(Group group) {
		
		this.groups.add(group);
		group.jointedMembers++;
		
	}
	
	public void displaySugg() {
		ArrayList<Member> m = Network.suggestFirends(this);
		for(Member member: m)
			System.out.println(member.Profile(member));
		
	}
	
	public void createPage() {
		System.out.println("Cr?er votre page!\nEntrer Le nom de votre page");
		DataInputStream s = new DataInputStream(System.in);
		try {
			String name=s.readLine();
			System.out.println("Quel est le genre de votre page");
			String gender= s.readLine();
			Page page = new Page(name, gender, this);
			this.likedPages.add(page);
			Network.entities.add(page);
			//aaaa
			page.members.add(this);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createGroup() {
		System.out.println("Cr?er votre groupe!\nEntrer Le nom de votre groupe");
		DataInputStream s = new DataInputStream(System.in);
		try {
			String name=s.readLine();
			System.out.println("Quel est le genre de votre groupe");
			String gender= s.readLine();
			Group group = new Group(name, gender, this);
			this.groups.add(group);
			Network.entities.add(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String Profile(Member member) {
		
		String posts="";
		String friends ="";
		String groups ="";
		String pages ="";
		
		for(Post p : member.wall)
			posts += p.toString()+"\n";
		
		for(Member m : member.friends)
			friends+=m.name+"\t|";
		
		for(Page p : member.likedPages)
			pages += p.name+"\t|";
		
		for(Group g : member.groups)
			groups+=g.name+"\t|";
		
		return "[name= "+member.name+
				 "\nFriends=  "+friends+
				"Groups "+groups+
				"\nLiked Pages"+pages+
				"\nPosts = "+ posts+ "]";
		
	}

	
	
	
}
