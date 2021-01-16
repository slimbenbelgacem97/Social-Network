
public class Page extends Entity {
	public int likes;

	public Page(String name, String gender, Member owner) {
		super(name, gender, owner);
		this.likes = 0;
	}
}
