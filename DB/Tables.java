package DB;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.mysql.cj.result.Field;

public class Tables {

	private Connection con = Connect.makeConnection(null);
	private Statement st = null;
	private PreparedStatement psmt = null;
	
	public void CreateDB() {
		String createdb = "create database if not exists coffee";
		String dropdb = "drop database if exists coffee";
		
		try {
			st = con.createStatement();
			st.executeUpdate(dropdb);
			st.executeUpdate(createdb);
			System.out.println("DB 생성완료");
			con = Connect.makeConnection("coffee");
			CreateTables();
			InsertTables();
		}
		catch(Exception e) {
			System.out.println("DB 생성 실패!");
		}
	}
	
	public void CreateTables(){
		String menu = "create table menu(m_no int not null primary key auto_increment, "
				    + "m_group varchar(10), "
				    + "m_name varchar(30), "
				    + "m_price int)";
		
		String user = "create table user(u_no int not null primary key auto_increment, "
				    + "u_id varchar(20), "
				    + "u_pw varchar(4), "
				    + "u_name varchar(5), "
				    + "u_bd varchar(14), "
				    + "u_point int, "
				    + "u_grade varchar(10))";
		
		String orderlist = "create table orderlist(o_no int not null primary key auto_increment, "
				         + "o_date date, "
				         + "u_no int, "
				         + "m_no int, "
				         + "o_group varchar(10), "
				         + "o_size varchar(1), "
				         + "o_price int, "
				         + "o_count int, "
				         + "o_amount int, "
				         + "foreign key(u_no) references user(u_no), "
				         + "foreign key(m_no) references menu(m_no))";
		
		String shopping = "create table shopping(s_no int not null primary key auto_increment, "
				        + "m_no int, "
				        + "s_price int, "
				        + "s_count int, "
				        + "s_size varchar(1), "
				        + "s_amount int, "
				        + "foreign key(m_no) references menu(m_no))";
		try {
			st = con.createStatement();
			st.executeUpdate(menu);
			st.executeUpdate(user);
			st.executeUpdate(orderlist);
			st.executeUpdate(shopping);
		}
		catch(Exception e){
			System.out.println("테이블 만들기 실패!");
		}
	}
	
	public void InsertTables() {
		
		String table[] = {"menu", "user", "orderlist"};
		String sql[] = {"insert into menu values(?,?,?,?)",
						"insert into user values(?,?,?,?,?,?,?)",
						"insert into orderlist values(?,?,?,?,?,?,?,?,?)"};
		
		try {
			for(int i=0; i<table.length; i++) {
				psmt = con.prepareStatement(sql[i]);
				Scanner fscanner = new Scanner(new FileInputStream("C:\\Users\\rlaru\\OneDrive\\바탕 화면\\자바 프로젝트\\STARBOX\\STARBOX\\DataFiles\\" + table[i] + ".txt"));
				fscanner.nextLine();
				while(fscanner.hasNext()) {
					StringTokenizer st = new StringTokenizer(fscanner.nextLine(), "\t");
					int n = 1;
					while(st.hasMoreTokens()) {
						String s = st.nextToken();
						System.out.println(s);
						psmt.setString(n++, s);
					}
					psmt.executeUpdate();
					n = 0;
				}
			}
		}
		catch(Exception e) {
			System.out.println("오류");
		}
	}
	
	public Tables() {
		// TODO Auto-generated constructor stub
		CreateDB();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 System.setProperty("file.encoding","UTF-8");
         try{
             java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
           charset.setAccessible(true);
           charset.set(null,null);
         }
         catch(Exception e){
         }
         new Tables();
	}

}
