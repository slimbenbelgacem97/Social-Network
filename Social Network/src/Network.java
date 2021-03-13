import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;



public class Network {
	static ArrayList<Member> members = new ArrayList<>();
	static ArrayList<Entity> entities = new ArrayList<>();
	
	public static void createMember(String name) {
		Member m = new Member(name);
		members.add(m);
		//System.out.println("Bonjour "+m.name+"!\nBienvenue dans notre réseau ^_^");
		System.out.println();
	}
	
	
	
	public static ArrayList<Member> suggestFirends(Member member) {
		ArrayList<Member> marque=new ArrayList<Member>();
		ArrayList<Member> file = new ArrayList<Member>();
		ArrayList<Integer> cout=new ArrayList<Integer>();
		
		/************initialisation********************/
		cout.add(0);
		cout.add(0);
		file.add(null);
		file.add(null);
		
		/********************************/
		 
		
//		Integer k=0;
//		 for (Member m : member.friends) {
//
//			 for (Member m1 : m.friends) {
//
//				 if(!member.equals(m1) && !member.friends.contains(m1) && !marque.contains(m1)) {
//
//					 	for (Member m2 : m1.friends) {if(member.friends.contains(m2)) k++;}
//
//					 	//pages
//
//					 	for (Page p : m1.likedPages) {if(member.likedPages.contains(p)) k++;}
//
//					 	int h=-1;
//
//					 	//rechercher min cout
//					 	 for (int i =0; i< cout.size();i++) {
//					 		 if(k>cout.get(i))
//					 			 h=i;
//					 	 }
//
//					 	if(h!=-1){
//					 		file.remove(h);
//					 		cout.remove(h);
//					 		file.add(m1);
//					 		cout.add(k);
//					 		k=0;
//					 	}
//
//					 	marque.add(m1);
//
//			   }
//			 }
//		 }
		 
		
		 	
		 
//		 for (Page p1 : member.likedPages) {
//
//			 for (Member m3 : p1.members) {
//
//				if(!member.equals(m3) && !member.friends.contains(m3) && !marque.contains(m3)) {
//
//				 k=0;
//				 for (Page p : m3.likedPages) {if(member.likedPages.contains(p)) k++;}
//
//				 int h=0;
//
//
//				 for (int i =0; i< cout.size();i++)
//			 		 if(k>cout.get(i))
//			 			 h=i;
//
//
//			 	if(h!=-1){
//			 		file.remove(h);
//			 		cout.remove(h);
//			 		file.add(m3);
//			 		cout.add(k);
//			 		k=0;
//			 	}
//
//				 marque.add(m3);
//
//			   }}
//		}
			 
		 
		 
		 return file;
		
	}
	
	
	/*public static ArrayList<Member> suggestFirends(Member member) {
		ArrayList<Member> marque=new ArrayList<Member>();
		ArrayList<Member> file = new ArrayList<Member>();
		ArrayList<Integer> cout=new ArrayList<Integer>();
		
		/************initialisation********************
		cout.add(0);
		cout.add(0);
		file.add(null);
		file.add(null);
		
		/************fin initialisation********************
		
		member.Profile(member);
		Integer k=0;
		 for (Member m : members) {
			 
			 
			 if(!member.equals(m) && !member.friends.contains(m) && !marque.contains(m)) {
				 	
				 //System.out.println(m.name);
				 
				 for (Member m1 : m.friends) {
				 		if(member.friends.contains(m1))
				 			k++;}
				 	
				 	
				 	Iterator i = cout.iterator();
				 	int h=0;
				 	
				 	while(i.hasNext() &&((Integer) i.next()) > k )
				 			h++;
				 	
				 	if(h<2){
				 		;
				 		file.remove(h);
				 		cout.remove(h);
				 		file.add(m);
				 		cout.add(k);
				 		k=0;
				 	}
				 	
				 	marque.add(m);
				
			   }
		 }
		 return file;
		
	}*/
	
	
	
	public static void main(String[] args) {
		/*System.out.println("Inscrir à notre réseau et touver des amis!\nEnter votre nom:");
		DataInputStream s= new DataInputStream(System.in);
		
		try {
			createMember(s.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		createMember("slim");
		createMember("slim1");
		createMember("slim2");
		createMember("slim3");
		createMember("slim4");
		createMember("slim5");
		createMember("slim6");
		createMember("slim7");
		Member member = members.get(0);
		Member member1 = members.get(1);
		Member member2 = members.get(2);
		Member member3 = members.get(3);
		Member member7 = members.get(7);
		Member member4 = members.get(4);
		Member member5 = members.get(5);
		Member member6 = members.get(6);
		
		
		member.friends.add(member1);
		member.friends.add(member2);
		member.friends.add(member3);
		 

		member1.friends.add(member);
		member2.friends.add(member);
		member3.friends.add(member);
		
		member1.friends.add(member4);
		member4.friends.add(member1);
		
		
		member1.friends.add(member5);
		member2.friends.add(member5);
		member5.friends.add(member1);
		member5.friends.add(member2);
		
		
		member1.friends.add(member6);
		member2.friends.add(member6);
		member3.friends.add(member6);
		member6.friends.add(member1);
		member6.friends.add(member2);
		member6.friends.add(member3);
		
		
		member7.createPage();
		member7.createPage();
		member7.createPage();
		Page p1 =(Page) entities.get(0);
		Page p2 = (Page) entities.get(1);
		Page p3 = (Page) entities.get(2);
		member.likePage(p1);
		member.likePage(p2);
		member.likePage(p3);
		
		
		
		
		
		//createGroup();
		//System.out.println(member.equals(members.get(1)));
		
		
		member.displaySugg();
	}
}
