public class Group extends Entity {
	public int jointedMembers;
	public Group(String name, String gender, Member owner) {
		super(name, gender, owner);
		this.jointedMembers = 0;
	}
	@Override
	public void deleteMember(Member manager , Member member) {

	}
	
}
