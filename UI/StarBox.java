package UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import DB.Connect;

public class StarBox extends JFrame{
	private Connection con = Connect.makeConnection("coffee");
	private PreparedStatement psmt = null;
	private Statement st = null;
	private JButton option[] = new JButton[4];
	private JButton group[] = new JButton[3];
	private String u_id;
	private String u_no;
	private Container c = getContentPane();
	private JScrollPane sp;
	private Vector<String> mname = new Vector<String>();
	private Vector<Integer> mprice = new Vector<Integer>();
	private Vector<Integer> mno = new Vector<Integer>();
	private Vector<String> mgroup = new Vector<String>();
	private EastPanel et;
	private String name = null;
	private String grade = null;
	private String point = null;
	private JLabel userlabel;
	class NorthPanel extends JPanel{
		
		public NorthPanel() {
			// TODO Auto-generated constructor stub
			setLayout(new BorderLayout());
			add(new North(), BorderLayout.NORTH);
			add(new Center(), BorderLayout.WEST);
		}
		
		class North extends JPanel{
			public North() {
				// TODO Auto-generated constructor stub
				setLayout(new BorderLayout());
				try {
					st = con.createStatement();
					String sql = "select u_name, u_grade, u_point from user where u_id = '" + u_id + "'";
					ResultSet rs = st.executeQuery(sql);
					while(rs.next()) {
						name = rs.getString(1);
						grade = rs.getString(2);
						point = rs.getString(3);
					}
					
					String s = "회원명 : " + name + " / 회원등급 : " + grade + " / 총 누적 포인트 : " + point;
					userlabel = new JLabel(s);
					userlabel.setFont(new Font("", Font.PLAIN, 16));
					add(userlabel, BorderLayout.WEST);
				}
				catch(Exception e) {
					
				}
			}
		}
		class Center extends JPanel{
			
			String s[] = {"구매내역", "장바구니", "인기 상품 Top5", "Logout"};
			
			public Center() {
				// TODO Auto-generated constructor stub
				for(int i=0; i<option.length; i++) {
					option[i] = new JButton(s[i]);
					option[i].addActionListener(new CenterListener());
					add(option[i]);
				}
			}
			class CenterListener implements ActionListener{
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String s = e.getActionCommand();
					switch(s) {
					case "구매내역":
						new Orderlist(name, u_id);
						dispose();
						break;
					case "장바구니":
						new Shopping(name);
						dispose();
						break;
					case "인기 상품 Top5":
						new Top5();
						break;
					case "Logout":
						new Login();
						dispose();
					}
				}
			}
		}
	}
	
	class WestPanel extends JPanel{
		
		String s[] = {"음료", "푸드", "상품"};
		
		public WestPanel() {
			// TODO Auto-generated constructor stub
			setLayout(new BorderLayout());
			for(int i=0; i<group.length; i++) {
				group[i] = new JButton(s[i]);
				group[i].addActionListener(new TypeListener());
			}
			add(new North(), BorderLayout.NORTH);
		}
		class North extends JPanel{
			
			public North() {
				// TODO Auto-generated constructor stub
				setLayout(new BorderLayout());
				add(new N(), BorderLayout.NORTH);
				add(new C(), BorderLayout.CENTER);
				add(new S(), BorderLayout.SOUTH);
			}
			
			class N extends JPanel{ //음료버튼
				public N() {
					// TODO Auto-generated constructor stub
					add(group[0]);
				}
			}
			class C extends JPanel{ //푸드버튼
				public C() {
					// TODO Auto-generated constructor stub
					add(group[1]);
				}
			}
			class S extends JPanel{ //상품버튼
				public S() {
					// TODO Auto-generated constructor stub
					add(group[2]);
				}
			}
		}
	}
	class TypeListener implements ActionListener{// 음료/푸드/상품 클릭리스너
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String s = e.getActionCommand();
			c.remove(sp);
			if(et != null) {
				c.remove(et);
			}
			CenterPanel cen = new CenterPanel(s);
			sp = new JScrollPane(cen);
			c.add(sp, BorderLayout.CENTER);
			setSize(740, 500);
			c.validate();
		}
	}
	class CenterPanel extends JPanel{// 센터패널 - 메뉴리스트
		
		public void Setcenter(String s) {
			String type = s;
			try {
				int num = 0; //메뉴갯수
				int row = 0; //행
				st = con.createStatement();
				String sql = "select count(*) from menu where m_group = '" + type + "'";
				ResultSet rs = st.executeQuery(sql);
				while(rs.next()) {
					num = rs.getInt(1);
				}
				if(num%3 == 0) {
					row = num/3;
				}
				else {
					row = num/3 + 1;
				}
				setLayout(new GridLayout(row, 3, 30, 10));
				sql = "select * from menu where m_group = '" + type + "'";
				rs = st.executeQuery(sql);
				mname.clear();
				mprice.clear();
				mno.clear();
				mgroup.clear();
				int i=0;
				while(rs.next()) {
					int m_no = rs.getInt(1);
					String m_group = rs.getString(2);
					String m_name = rs.getString(3);
					int m_price = rs.getInt(4);
					mname.add(m_name);
					mprice.add(m_price);
					mno.add(m_no);
					mgroup.add(m_group);
					add(new AddLabel(m_name, i));
					i++;
				}
			}
			catch(Exception e) {
				
			}
		}
		public CenterPanel(String s) {
			// TODO Auto-generated constructor stub
			Setcenter(s);
		}
		
		class AddLabel extends JPanel{ // 메뉴라벨
			JButton imagebtn = new JButton();
			JLabel menulabel = new JLabel();
			
			public AddLabel(String m_name, int i) {
				// TODO Auto-generated constructor stub
				setLayout(new BorderLayout()); 
				ImageIcon icon = new ImageIcon("C:\\Users\\rlaru\\OneDrive\\바탕 화면\\자바 프로젝트\\STARBOX\\STARBOX\\DataFiles\\이미지\\" + m_name + ".jpg");
				Image img = icon.getImage();
				Image changeImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
				icon = new ImageIcon(changeImg);
				imagebtn = new JButton(icon);
				imagebtn.addActionListener(new DetailListener(i));
				add(imagebtn);
				menulabel = new JLabel(m_name);
				menulabel.setHorizontalAlignment(JLabel.CENTER);
				add(menulabel, BorderLayout.SOUTH);
			}
		}
	}

	class DetailListener implements ActionListener{// 메뉴버튼 클릭했을때 액션리스너
		int indexx;
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			setSize(1200, 500);
			if(et != null) {
				c.remove(et);
			}
			et = new EastPanel(mname.get(indexx), mprice.get(indexx), mno.get(indexx), mgroup.get(indexx));
			c.add(et, BorderLayout.EAST);
			c.validate();
		}
		public DetailListener(int i) {
			// TODO Auto-generated constructor stub
			indexx = i;
		}
	}
	class EastPanel extends JPanel{ //메뉴 상세정보
		String name;
		int price;
		int no;
		String group;
		JButton btn[] = new JButton[2];
		String amountt[] = new String[10];
		JLabel label[] = new JLabel[5];
		JComboBox<String> amount;
		String sizee[] = {"M", "L"};
		JComboBox<String> size = new JComboBox<String>(sizee);
		JTextField jt[] = new JTextField[3];
		
		public EastPanel(String m, int p, int n, String g) {
			// TODO Auto-generated constructor stub
			setLayout(new GridLayout(3, 1));
			name = m;
			price = p;
			no = n;
			group = g;
			add(new JLabel(""));
			add(new Cen());
			add(new Sou());
		}
		class Sou extends JPanel{
			String s[] = {"장바구니에 담기", "구매하기"};
			JButton btn[] = new JButton[2];
			public Sou() {
				// TODO Auto-generated constructor stub
				for(int i=0; i<s.length; i++) {
					btn[i] = new JButton(s[i]);
					btn[i].addActionListener(new ShopListener());
					add(btn[i]);
				}
			}
			class ShopListener implements ActionListener{// 장바구니,구매하기 눌렀을때 액션리스너
				
				public void Buy(int user_p, int buy_p) {//현금으로 결제할 경우
					user_p += buy_p * 0.05;
					point = Integer.toString(user_p);
					Insert("orderlist");
					String ss = "회원명 : " + name + " / 회원등급 : " + grade + " / 총 누적 포인트 : " + point;
					userlabel.setText(ss);
					JOptionPane.showMessageDialog(null, "구매되었습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
				}
				
				public void Insert(String s) {//s테이블에 insert
					try {
						if(s.equals("shopping")) {//장바구니 테이블
							String sql = "insert into " + s + "(m_no, s_price, s_count, s_size, s_amount) values(?,?,?,?,?)";
							psmt = con.prepareStatement(sql);
							psmt.setInt(1, no);
							psmt.setString(2, jt[1].getText());
							psmt.setString(3, amount.getSelectedItem().toString()); 
							psmt.setString(4, size.getSelectedItem().toString());
							psmt.setString(5, jt[2].getText());
							psmt.executeUpdate();
						}
						else {//구매내역 테이블
							Date nowDate = new Date();
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
							String date = simpleDateFormat.format(nowDate);
							st= con.createStatement();
							ResultSet rs = st.executeQuery("select u_no from user where u_id = '" + u_id + "'");
							while(rs.next()) {
								u_no = rs.getString(1);
							}
							
							String sql = "insert into orderlist(o_date, u_no, m_no, o_group, o_size, o_price, o_count, o_amount) values(?,?,?,?,?,?,?,?)";
							String data[] = {date, u_no, Integer.toString(no), group, size.getSelectedItem().toString(),
											 jt[1].getText(), amount.getSelectedItem().toString(), jt[2].getText()};
							
							psmt = con.prepareStatement(sql);
							for(int i=0; i<data.length; i++) {
								psmt.setString(i+1, data[i]);
							}
							psmt.executeUpdate();
						}
					}
					catch(Exception e) {
						
					}
				}
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String s = e.getActionCommand();
					
					if(s.equals("장바구니에 담기")) {//장바구니 버튼 눌렀을 경우
						try {
							Insert("shopping");
							JOptionPane.showMessageDialog(null, "장바구니에 담았습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
						}
						catch(Exception ee) {
							
						}
					}
					else {//구매하기 버튼 눌렀을 경우
						int user_p = Integer.parseInt(point);
						int buy_p = Integer.parseInt(jt[2].getText());
						Vector<String> v = new Vector<String>();
						if(user_p < buy_p) {//포인트가 모자랄 경우
							Buy(user_p, buy_p);
						}
						else{//포인트가 많을 경우
							v.add("회원님의 총 포인트 : " + user_p);
							v.add("포인트로 결제하시겠습니까?");
							v.add("(아니오를 클릭 시 현금결제가 됩니다.)");
							int yn = JOptionPane.showConfirmDialog(null, new BuyMethod(v), "결제수단", JOptionPane.YES_NO_OPTION);
							if(yn == JOptionPane.YES_OPTION) {//포인트로 결제
								v.clear();
								point = Integer.toString(user_p - buy_p);
								v.add("포인트로 결제 완료되었습니다.");
								v.add("남은 포인트 : " + point);
								JOptionPane.showMessageDialog(null, new BuyMethod(v), "메세지", JOptionPane.INFORMATION_MESSAGE);
								
							}
							else {//현금으로 결제
								Buy(user_p, buy_p);
							}
						}
						try {//포인트 갱신, 승급
							//포인트갱신
							st = con.createStatement();
							String sql = "update user set u_point = '" + point + "' where u_id = '" + u_id + "'";
							st.executeUpdate(sql);
							//승급
							sql = "select sum(o_amount) from orderlist where u_no = '" + u_no + "'";
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
								grade = up;
								String ss = "회원명 : " + name + " / 회원등급 : " + grade + " / 총 누적 포인트 : " + point;
								userlabel.setText(ss);
								sql = "update user set u_grade = '" + up +  "' where u_id = '" + u_id + "'";
								st.executeUpdate(sql);
								v.add("축하합니다!");
								v.add("회원님 등급이" + up + "로 승급하셨습니다"); 
								JOptionPane.showMessageDialog(null, new BuyMethod(v), "메시지", JOptionPane.INFORMATION_MESSAGE);
							}
							
						}
						
						catch(Exception ee) {
							
						}
					}
					c.remove(et);
					StarBox.this.setSize(740, 500);
					c.validate();
				}
				
				class BuyMethod extends JPanel{
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
			}
		}
		
		class Cen extends JPanel{
	
			public Cen() {
				// TODO Auto-generated constructor stub
				setLayout(new GridLayout(1,2));
				add(new W() );
				add(new C());
			}
			class W extends JPanel{ // 선택한 상품 그림
				public W() {
					// TODO Auto-generated constructor stub
					ImageIcon icon = new ImageIcon("C:\\Users\\rlaru\\OneDrive\\바탕 화면\\자바 프로젝트\\STARBOX\\STARBOX\\DataFiles\\이미지\\" + name + ".jpg");
					Image img = icon.getImage();
					Image changeImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
					icon = new ImageIcon(changeImg);
					JLabel label = new JLabel(icon);
					add(label);
				}
			}
			class C extends JPanel{
				String s[] = {"주문메뉴 : ", "가격 : ", "수량 : ", "사이즈 : ", "총금액 : "};
				int total = 0;
				
				public C() {
					// TODO Auto-generated constructor stub
					setLayout(new GridLayout(5, 1));
					
					for(int i=1; i<=amountt.length; i++) {
						amountt[i-1] = Integer.toString(i);
					}
					
					for(int i=0; i<label.length; i++) {
						label[i] = new JLabel(s[i]);
						label[i].setHorizontalAlignment(JLabel.RIGHT);
					}
					amount = new JComboBox<String>(amountt);
					amount.addActionListener(new ComboListener());
					size.addActionListener(new ComboListener());
					
					for(int i=0; i<jt.length; i ++) {
						jt[i] = new JTextField(11);
						jt[i].setEnabled(false);
					}
					jt[0].setText(name);
					jt[1].setText(Integer.toString(price));
					jt[2].setText(Integer.toString(price));
					add(label[0]); add(jt[0]);
					add(label[1]); add(jt[1]);
					add(label[2]); add(amount);
					add(label[3]); add(size);
					add(label[4]); add(jt[2]);
				}
				class ComboListener implements ActionListener{//수량과 사이즈를 선택했을때 총금액이 나오는  액션리스너
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						int money = Integer.parseInt(jt[1].getText());//개당 가격
						int num = Integer.parseInt(amount.getSelectedItem().toString());//수량
						String s = size.getSelectedItem().toString();//사이즈
						int tm = 0;//회원등급 계산 안한 가격
					
						if(s.equals("M")) {
							tm = money * num;
							total = DC(tm);
						}
						else {
							tm = (money+1000) * num;
							total = DC(tm);
						}
						jt[2].setText(Integer.toString(total));
					}
					public int DC(int money) {
						int m = money;
						
						if(grade.equals("Bronze")) {//3퍼센트 할인
							m = (int)(money * 0.97);
							return  m;
						}
						else if(grade.equals("Silver")) {//5퍼센트할인
							m = (int)(money * 0.95);
							return  m;
						}
						else if(grade.equals("Gold")) {//10퍼센트할인
							m = (int)(money * 0.9);
							return  m;
						}
						else {
							return m;
						}
					}
				}
			}
			
		}
		
	}
	
	public StarBox(String id) {
		// TODO Auto-generated constructor stub
		this.u_id = id;
		setTitle("STARBOX");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		c.add(new NorthPanel(), BorderLayout.NORTH);
		c.add(new WestPanel(), BorderLayout.WEST);
		
		sp = new JScrollPane(new CenterPanel("음료"));
		c.add(sp, BorderLayout.CENTER);
		
		setSize(740, 500);
		setVisible(true);
	}
}
