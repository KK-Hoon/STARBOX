package UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DB.Connect;

public class Join extends JFrame{
	
	private JTextField jt[] = new JTextField[3];
	private Vector<JComboBox<String>> combo = new Vector<JComboBox<String>>();
	private JButton btn[] = new JButton[2];
	private Connection con = Connect.makeConnection("coffee");
	private PreparedStatement psmt = null;
	private Statement st = null;
	
	class NorthPanel extends JPanel{
		String s[] = {"       이름", "   아이디", "비밀번호"};
		JLabel label[] = new JLabel[3];
		
		public NorthPanel() {
			// TODO Auto-generated constructor stubs
			setLayout(new BorderLayout());
			
			for(int i=0; i<s.length; i++) {
				label[i] = new JLabel(s[i]);
				jt[i] = new JTextField(20);
			}
			add(new North(), BorderLayout.NORTH);
			add(new Center(), BorderLayout.CENTER);
			add(new South(), BorderLayout.SOUTH);
		}
		class North extends JPanel{
			
			public North() {
				// TODO Auto-generated constructor stub
				add(label[0]);
				add(jt[0]);
			}
		}
		class Center extends JPanel{
			
			public Center() {
				// TODO Auto-generated constructor stub
				add(label[1]);
				add(jt[1]);
			}
		}
		class South extends JPanel{
			
			public South() {
				// TODO Auto-generated constructor stub
				add(label[2]);
				add(jt[2]);
			}
		}
	}
	
	class CenterPanel extends JPanel{
		
		JLabel label = new JLabel("생년월일");
		JLabel ymd[] = new JLabel[3];
		String s[] = {"년", "월", "일"};
		
		public CenterPanel() {
			// TODO Auto-generated constructor stub
			add(label);
			
			
			for(int i=0; i<3; i++) {
				LocalDate now = LocalDate.now();
				int year = now.getYear();
				Vector<String> v = new Vector<String>();
				if(i == 0) {
					for(int ii=year; ii>=1900; ii--) {
						v.add(Integer.toString(ii));
					}
				}
				else if(i == 1) {
					for(int ii=1; ii<=12; ii++) {
						String s = String.format("%02d", ii);
						v.add(s);
					}
				}
				else {
					for(int ii=1; ii<30; ii++) {
						String s = String.format("%02d", ii);
						v.add(s);
					}
				}
				JComboBox<String> j = new JComboBox<String>(v);
				if(i != 2) {
					j.addActionListener(new DateListener());
				}
				combo.add(j);
			}
			
			for(int i=0; i<ymd.length; i++) {
				ymd[i] = new JLabel(s[i]);
				add(combo.get(i));
				add(ymd[i]);
			}
		}
		
		class DateListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int year = Integer.parseInt(combo.get(0).getSelectedItem().toString());
				int month = Integer.parseInt(combo.get(1).getSelectedItem().toString());
				LocalDate newDate = LocalDate.of(year, month, 1);
				int length = newDate.lengthOfMonth();
				combo.get(2).removeAllItems();;
				for(int i=1; i<=length; i++) {
					String s = String.format("%02d", i);
					combo.get(2).addItem(s);
				}
			}
		}
	}
	
	class SouthPanel extends JPanel{
		
		String s[] = {"가입완료", "취소"};
		public SouthPanel() {
			// TODO Auto-generated constructor stub
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(s[i]);
				btn[i].addActionListener(new MyActionListener());
				add(btn[i]);
			}
		}
		class MyActionListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String s = e.getActionCommand();
				switch(s) {
				case "가입완료":
					String name = jt[0].getText();
					String id = jt[1].getText();
					String pw = jt[2].getText();
					String bd = combo.get(0).getSelectedItem().toString() + "-" + 
							    combo.get(1).getSelectedItem().toString() + "-" +
							    combo.get(2).getSelectedItem().toString();
					if(name.equals("") || id.equals("") || pw.equals("")) {
						JOptionPane.showMessageDialog(null, "누락된 항목이 있습니다.", "메세지", JOptionPane.ERROR_MESSAGE);
					}
					else {
						try {
							String sql = "select * from user where u_id = '" + id + "'";
							st = con.createStatement();
							ResultSet rs = st.executeQuery(sql);
							if(rs.next()) {
								JOptionPane.showMessageDialog(null, "아이디가 중복되었습니다.", "메세지", JOptionPane.ERROR_MESSAGE);
							}
							else {
								sql = "insert into user(u_id, u_pw, u_name, u_bd, u_point, u_grade) values(?,?,?,?,?,?)";
								psmt = con.prepareStatement(sql);
								String info[] = {id, pw, name, bd, "0", "일반"};
								for(int i=0; i<info.length; i++) {
									psmt.setString(i+1, info[i]);
								}
								psmt.executeUpdate();
								JOptionPane.showMessageDialog(null, "가입완료 되었습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
								dispose();
							}
						}
						catch(Exception ee) {
							
						}
					}
					break;
				case "취소":
					dispose();
					break;
				}
			}
		}
	}
	
	public Join() {
		// TODO Auto-generated constructor stub
		setTitle("회원가입");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		
		c.add(new NorthPanel(), BorderLayout.NORTH);
		c.add(new CenterPanel(), BorderLayout.CENTER);
		c.add(new SouthPanel(), BorderLayout.SOUTH);
		
		setSize(350, 200);
		setVisible(true);
	}
}
