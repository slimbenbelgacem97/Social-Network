import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;



public class Network {
	static ArrayList<Member> members = new ArrayList<>();
	static ArrayList<Entity> entities = new ArrayList<>();
	
	public static void createMember(String name) {
		Member m = new Member(name);
		members.add(m);
		System.out.println("Bonjour "+m.name+"!\nBienvenue dans notre réseau ^_^");
		System.out.println();
	}
	
	public static ArrayList<Member> suggestFirends() {
		return null;
		
	}
	
	
	public static void main(String[] args) {
		System.out.println("Inscrir à notre réseau et touver des amis!\nEnter votre nom:");
		DataInputStream s= new DataInputStream(System.in);
		
		try {
			createMember(s.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createMember("slim");
		Member member = members.get(0);
		//member.post();
		member.friends.add(members.get(1));
		member.createGroup();
		System.out.println(member.equals(members.get(1)));
		
		for (Member m  : members ) {
			System.out.println(m.Profile(m));
		}
	}
}
