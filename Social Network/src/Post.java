import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
	String content;
	Date ceartiondate;
	String auther;
	public Post(String content, Member auther) {
		this.content = content;
		this.auther = auther.name;
		
		ceartiondate = new Date(System.currentTimeMillis());
	}
	
	@Override
	public String toString() {
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
		return  content + "\n" + formatter.format(ceartiondate) + "," + auther + "\n ==========================";
	}
	
	

}
