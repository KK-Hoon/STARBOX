package DB;

public class Driver_Test {

	
	public Driver_Test() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("����");
		}
		catch(Exception e) {
			System.out.println("������");
		}
	}
}
