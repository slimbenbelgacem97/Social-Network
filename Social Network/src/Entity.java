import java.util.ArrayList;
import java.util.Date;



public class Entity {
	String name,gender;
	Date ceartiondate;
	Member owner;
	ArrayList<Member> managerList ;
	ArrayList<Post> discussion ;
	ArrayList<Member> members ;
	public Entity(String name, String gender, Member owner) {
		this.name = name;
		this.gender = gender;
		this.owner = owner;
		managerList = new ArrayList<>();
		discussion = new ArrayList<>();
		members = new ArrayList<>();
		this.ceartiondate= new Date(System.currentTimeMillis());
		this.managerList.add(owner);
	}
	public void post(String content, Member auther) {
		Post post = new Post(content, auther);
		this.discussion.add(post);
	}
	String refuse ="Acc?s refus?";
	public void deletePost(Member manager, Post post) {

		if(this.managerList.contains(manager)) {
			this.discussion.remove(post);
		}else {
			System.out.println(refuse);
		}
	}
	public void addManager(Member owner, Member member) {
		if(owner.equals(this.owner)) {
			this.managerList.add(member);
		}else {
			System.out.println(refuse);
		}
	}
	public void deleteManager(Member owner , Member member) {
		if(owner.equals(this.owner)) {
			this.managerList.remove(member);
		}else {
			System.out.println(refuse);
		}
	}
	public void deleteMember(Member manager , Member member) {
		if(this.managerList.contains(manager)) {
			this.members.remove(member);
		}else {
			System.out.println(refuse);
		}
	}
}
