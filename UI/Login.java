package UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import DB.Connect;

public class Login extends JFrame{
	
	private JTextField id = new JTextField(20);
	private JPasswordField pass = new JPasswordField(20);
	private JButton login = new JButton("로그인");
	private JButton btn[] = new JButton[2]; 
	private Connection con = Connect.makeConnection("coffee");
	private Statement st = null;
	
	class NorthPanel extends JPanel{
		JLabel label = new JLabel("STARBOX");
		public NorthPanel() {
			// TODO Auto-generated constructor stub
			label.setFont((new Font("San-Serif", Font.BOLD, 30)));
			add(label);
		}
	}
	
	class CenterPanel extends JPanel{
		JLabel label[] = new JLabel[2];
		String s[] = {"ID : ", "PW : "}; 
		
		public CenterPanel() {
			// TODO Auto-generated constructor stub
			for(int i=0; i<label.length; i++) {
				label[i] = new JLabel(s[i]);
			}
			add(new ID());
			add(new PW());
		}
		class ID extends JPanel {
			public ID() {
				// TODO Auto-generated constructor stub
				add(label[0]);
				add(id);
			}
		}
		class PW extends JPanel {
			public PW() {
				// TODO Auto-generated constructor stub
				add(label[1]);
				add(pass);
			}
		}
	}
	
	class SouthPanel extends JPanel{
		String s[] = {"회원가입", "종료"};
		
		public SouthPanel() {
			// TODO Auto-generated constructor stub
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(s[i]);
				btn[i].addActionListener(new MyActionListener());
				add(btn[i]);
			}
		}
	}
	
	class MyActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String s = e.getActionCommand();
			String idd = id.getText();
			String password = new String(pass.getPassword());
			switch(s) {
				case "로그인":
					try {
						if(idd.equals("") || password.equals("")) {
							JOptionPane.showMessageDialog(null, "빈칸이 존재합니다.", "메세지", JOptionPane.ERROR_MESSAGE);
						}
						else if(idd.equals("admin") && password.equals("1234")) {
							dispose();
							new ManagerMenu();
						}
						else {
							st = con.createStatement();
							String sql = "select u_pw from user where u_id = '" + idd + "'";
							ResultSet rs = st.executeQuery(sql);
							int log = 0;
							while(rs.next()) {
								if(rs.getString(1).equals(password)) {
									new StarBox(idd);
									dispose();
									log = 1;
								}
							}
							if(log == 0) {
								JOptionPane.showMessageDialog(null, "회원정보가 틀립니다.다시입력해주세요", "메세지", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					catch(Exception ee) {
						
					}
					break;
				case "회원가입":
					new Join();
					break;
				case "종료":
					dispose();
					break;
			}
		}
	}
	public Login() {
		// TODO Auto-generated constructor stub
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		
		c.add(new NorthPanel(), BorderLayout.NORTH);
		c.add(new CenterPanel(), BorderLayout.CENTER);
		login.addActionListener(new MyActionListener());
		c.add(login, BorderLayout.EAST);
		c.add(new SouthPanel(), BorderLayout.SOUTH);
		
		setSize(350, 200);
		setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Login();
	}

}
