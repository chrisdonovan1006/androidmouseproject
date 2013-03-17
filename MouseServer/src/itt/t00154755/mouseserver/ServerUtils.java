package itt.t00154755.mouseserver;

public class ServerUtils {

	public void error(String tag, Exception e){
		System.out.println(tag);
		e.printStackTrace();
	}
	
	public void info(String s){
		System.out.println(s);
	}
	
}
