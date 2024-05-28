package UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import DB.Connect;
import UI.StarBox.EastPanel.Sou.ShopListener.BuyMethod;

public class Shopping extends JFrame{
	private String name;
	private Vector<String> colData = new Vector<String>();
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private DefaultTableModel model;
	private JTable table;
	private Connection con = Connect.makeConnection("coffee");
	private Statement st = null;
	private PreparedStatement psmt = null;
	private String u_no;
	private String u_id;
	private int user_p;
	private int sum = 0;
	private String grade = null;
	private Container c = getContentPane();
	
	class NorthPanel extends JPanel{//northpanel
		JLabel label = new JLabel(name +"회원님 장바구니");
		
		public NorthPanel() {
			// TODO Auto-generated constructor stub
			label.setFont(new Font("", Font.BOLD, 20));
			add(label);
		}
	}
	
	class CenterPanel extends JPanel{//centerpanel
		String col[] = {"메뉴명", "가격", "수량", "사이즈", "금액"};
		
		public CenterPanel() {
			// TODO Auto-generated constructor stub
			for(int i=0; i<col.length; i++) {//colData 초기화
				colData.add(col[i]);
			}
			try {
				st = con.createStatement();
				String sql = "select m_name, s_price, s_count, s_size, s_amount from shopping join menu where shopping.m_no = menu.m_no";
				ResultSet rs = st.executeQuery(sql);
				while(rs.next()) {
					Vector<String> v = new Vector<String>();
					for(int i=0; i<col.length; i++) {
						v.add(rs.getString(i+1));
					}
					rowData.add(v); //rowData 초기화
				}
				model = new DefaultTableModel(rowData, colData);
				table = new JTable(model);
				
			}
			catch(Exception e) {
				
			}
		}
	}
	
	class BuyMethod extends JPanel{//JOptionPane 안에 들어가는 패널
		JLabel label = new JLabel();
		String s[] = {BorderLayout.NORTH, BorderLayout.CENTER, BorderLayout.SOUTH};
		public BuyMethod(Vector<String> v) {
			// TODO Auto-generated constructor stub
			setLayout(new BorderLayout());
			for(int i=0; i<v.size(); i++) {
				label = new JLabel(v.get(i));
				add(label, s[i]);
			}
		}
	}
	
	class SouthPanel extends JPanel{//southpanel
		JButton btn[] = new JButton[3];
		
		public SouthPanel() {
			// TODO Auto-generated constructor stub
			String s[] = {"구매", "삭제", "닫기"};
			
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(s[i]);
				btn[i].addActionListener(new BtnListener());
			}
			
			if(rowData.size() == 0) {
				btn[0].setEnabled(false);
				btn[1].setEnabled(false);
			}
			
			for(int i=0; i<btn.length; i++) {
				add(btn[i]);
			}
		}
	}
	
	class BtnListener implements ActionListener{//버튼 액션리스너
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String s = e.getActionCommand();
			switch(s) {
			case "구매":
				Buy();
				break;
			case "삭제":
				ShoppingDelete();
				break;
			case "닫기":
				new StarBox(u_id);
				dispose();
				break;
			}
		}
		public void SetIncrement() {//auto_increment 초기화
			try {
				String sql1 = "alter table shopping auto_increment = 1";
				String sql2 = "set @count = 0";
				String sql3 = "update shopping set s_no = @count:=@count+1";
				st.executeUpdate(sql1);
				st.executeUpdate(sql2);
				st.executeUpdate(sql3);
			}
			catch(Exception e){
				
			}
		}
		
		public void ShoppingDelete() {//선택한 메뉴를 삭제
			int index = table.getSelectedRow() + 1;//선택한 메뉴의 인덱스
			try {
				if(Integer.toString(table.getSelectedRow()) == null) {
					JOptionPane.showMessageDialog(null, "삭제할 메뉴를 선택해주세요", "메시지", JOptionPane.ERROR_MESSAGE);
				}
				else {
					st = con.createStatement();
					String sql = "delete from shopping where s_no = '" + index + "'";
					st.executeUpdate(sql);
					SetIncrement();
					rowData.remove(index-1);
					table.updateUI();
				}
			}
			catch(Exception e) {
				
			}
		}
		
		public void Buy(){//구매
			
			try {
				for(int i=0; i<rowData.size(); i++) {
					Vector<String> v = rowData.get(i);
					sum += Integer.parseInt(v.get(4));//장바구니에 담겨있는 상품의 총금액
				}
				if(user_p < sum) {//현금으로 구매
					user_p += sum * 0.05;
					Re_point(user_p);//user의 u_point 갱신
					No_point();//구매내역에 추가
				}
				else {//포인트로 구매 or 현금구매
					Vector<String> v = new Vector<String>();
					v.add("회원님의 총 포인트 : " + user_p);
					v.add("포인트로 결제하시겠습니까?");
					v.add("(아니오를 클릭 시 현금결제가 됩니다.)");
					int yn = JOptionPane.showConfirmDialog(null, new BuyMethod(v), "결제수단", JOptionPane.YES_NO_OPTION);
					if(yn == JOptionPane.YES_OPTION) {//포인트로 결제
						v.clear();
						user_p -= sum;
						v.add("포인트로 결제 완료되었습니다.");
						v.add("남은 포인트 : " + user_p);
						JOptionPane.showMessageDialog(null, new BuyMethod(v), "메세지", JOptionPane.INFORMATION_MESSAGE);
						Re_point(user_p);//포인트 갱신;
					}
					else{//현금결제
						user_p += sum * 0.05;
						Re_point(user_p);//user의 u_point 갱신
						No_point();//구매내역에 추가
					}
				}
				//승급
				String sql = "select sum(o_amount) from orderlist where u_no = '" + u_no + "'";
				ResultSet rs = st.executeQuery(sql);
				int tmoney = 0;
				while(rs.next()) {
					tmoney = Integer.parseInt(rs.getString(1));
				}
				String up = "";
				if(tmoney > 800000) {
					up = "Gold";
				}
				else if(tmoney > 500000) {
					up = "Silver";
				}
				else if(tmoney > 300000){
					up = "Bronze";
				}
				if(!up.equals("") && !up.equals(grade)) {
					Vector<String> v = new Vector<String>();
					sql = "update user set u_grade = '" + up +  "' where u_id = '" + u_id + "'";
					st.executeUpdate(sql);
					v.add("축하합니다!");
					v.add("회원님 등급이" + up + "로 승급하셨습니다"); 
					JOptionPane.showMessageDialog(null, new BuyMethod(v), "메시지", JOptionPane.INFORMATION_MESSAGE);
				}
				Delete();//장바구니 내역 삭제
				SetIncrement();//autoincrement초기화
			}
			catch(Exception e) {
				
			}
		}
	}
	public void Re_point(int point) {//포인트 갱신
		try {
			st = con.createStatement();
			String sql = "update user set u_point = '" + Integer.toString(point) + "' where u_name = '" + name + "'"; 
			st.executeUpdate(sql);
		}
		catch(Exception e) {
			
		}
	}
	public void Delete() {//장바구니 내역 삭제
		try {
			String sql = "delete from shopping";
			st.executeUpdate(sql);
			new StarBox(u_id);
			dispose();
		}
		catch(Exception e) {
			
		}
	}
	public void Point() {
		
	}
	
	public void No_point() {//직접구매
		try {
			for(int i=0; i<rowData.size(); i++) {
				Vector<String> v = rowData.get(i);
				Vector<String> shopping = Shopping(v);
				String sql = "insert into orderlist(o_date, u_no, m_no, o_group, o_size, o_price, o_count, o_amount) values(?,?,?,?,?,?,?,?)";
				psmt = con.prepareStatement(sql);
				for(int ii=0; ii<shopping.size(); ii++) {
					psmt.setString(ii+1, shopping.get(ii));
				}
				psmt.executeUpdate();
			}
			JOptionPane.showMessageDialog(null, "구매되었습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception e) {
			
		}
	}
	
	public Vector<String> Shopping(Vector<String> v){//shopping table에 추가할 데이터를 가진 벡터
		Vector<String> shopping = new Vector<String>();
		
		try {
			//o_date
			Date nowDate = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String date = simpleDateFormat.format(nowDate);
			shopping.add(date);
			
			//u_no
			shopping.add(u_no);
			
			//m_no, o_group
			st = con.createStatement();
			String sql = "select m_no, m_group from menu where m_name = '" + v.get(0) + "'";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				shopping.add(rs.getString(1));
				shopping.add(rs.getString(2));
			}
			
			//o_size
			shopping.add(v.get(3));
			
			//o_price
			shopping.add(v.get(1));
			
			//o_count
			shopping.add(v.get(2));
			
			//o_amount
			shopping.add(v.get(4));
		}
		catch(Exception e) {
			
		}
		
		return shopping;
	}
	
	public Shopping(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
		
		setTitle("장바구니");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			st = con.createStatement();
			String sql = "select * from user where u_name = '" + name + "'";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				user_p = rs.getInt(6);
				u_no = rs.getString(1);
				u_id = rs.getString(2);
				grade = rs.getString(7);
			}
		}
		catch(Exception e) {
			
		}
		
		c.add(new NorthPanel(), BorderLayout.NORTH);
		
		new CenterPanel();
		c.add(new JScrollPane(table), BorderLayout.CENTER);
		
		c.add(new SouthPanel(), BorderLayout.SOUTH);
		
		setSize(800, 400);
		setVisible(true);
	}
}
