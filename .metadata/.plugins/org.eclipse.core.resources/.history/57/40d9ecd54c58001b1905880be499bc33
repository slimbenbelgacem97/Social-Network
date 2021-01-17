import java.util.ArrayList;
import java.util.Date;

public class Entity {
	String name,gender;
	Date ceartiondate;
	Member owner;
	ArrayList<Member> managerList = new ArrayList<>();
	public Entity(String name, String gender, Member owner) {
		this.name = name;
		this.gender = gender;
		this.owner = owner;
		this.ceartiondate= new Date(System.currentTimeMillis());
		this.managerList.add(owner);
	}
}
