package DB;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

	public static Connection makeConnection(String s){
		String id = "root";
		String pass = "";
		String url = null;
		if(s == null) {
			url = "jdbc:mysql://localhost";
		}
		else {
			url = "jdbc:mysql://localhost/" + s;
		}
		
		Connection con = null;
		
		try {
			con = DriverManager.getConnection(url,id,pass);
			System.out.println("데이터베이스 연결 성공");
		}
		catch(Exception e) {
			System.out.println("데이터베이스 연결 실패!");
		}
		return con;
	}
}
