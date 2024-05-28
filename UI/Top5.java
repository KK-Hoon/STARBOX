package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import DB.Connect;

public class Top5 extends JFrame{
	private JComboBox<String> combo;
	private Connection con = Connect.makeConnection("coffee");
	private Statement st = null;
	private Vector<String> m_name = new Vector<String>();//메뉴번호
	private Vector<String> sum = new Vector<String>();//합계
	private Vector<String> m_no = new Vector<String>();
	private Container c = getContentPane();
	private CenterPanel cp;
	
	class NorthPanel extends JPanel{
		JLabel label = new JLabel("인기상품 Top5");
		JButton exit = new JButton("닫기");
		String s[] = {"음료", "푸드", "상품"};
		
		public NorthPanel() {//northpanel
			// TODO Auto-generated constructor stub
			combo = new JComboBox<String>(s);
			combo.addActionListener(new ComboListener());
			add(combo);
			label.setFont(new Font("", Font.BOLD, 18));
			add(label);
			exit.addActionListener(new ExitListener());
			add(exit);
			setBackground(Color.LIGHT_GRAY);
		}
	}
	class ExitListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			dispose();
		}
	}
	class ComboListener implements ActionListener{//combobox 액션리스너
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String s = combo.getSelectedItem().toString();
			
			switch(s) {
			case "음료":
				Getmenu("음료");
				break;
			case "푸드":
				Getmenu("푸드");
				break;
			case "상품":
				Getmenu("상품");
				break;
			}
			c.remove(cp);
			cp = new CenterPanel();
			c.add(cp, BorderLayout.CENTER);
			c.validate();
		}
	}
	
	class CenterPanel extends JPanel{//centerpanel
		
		public CenterPanel() {
			// TODO Auto-generated constructor stub
			if(m_no.size() < 1) {
				Getmenu("음료");
			}
			setLayout(null);
		}
		@Override
			protected void paintComponent(Graphics g) {
				// TODO Auto-generated method stub
				super.paintComponent(g);
				int rectx = 50;
				int recty = 70;
				g.drawLine(50, 50, 50, 350);
				
				for(int i=0; i<m_name.size(); i++) {
					String s = m_name.get(i) + "-" + sum.get(i) + "개";
					int r = (int)(Math.random() * 256);
					int g1 = (int)(Math.random() * 256);
					int b = (int)(Math.random() * 256);
					int width = Integer.parseInt(sum.get(i)) * 10;
					int height = 20;
					g.setColor(new Color(r,g1,b));
					g.fillRect(rectx+1, recty, width, height);
					g.setColor(Color.black);
					g.drawString(s, rectx+5, recty+35);
					recty += 60;
				}
				
			}
	}
	
	public void Getmenu(String o_group) {//top5 메뉴를 구하는 메소드
		try {
			m_name.clear();
			sum.clear();
			m_no.clear();
			st = con.createStatement();
			String sql = "select m_no, sum(o_count) from orderlist where o_group = '" + o_group + "' group by m_no order by sum(o_count) desc limit 5";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				m_no.add(rs.getString(1));//메뉴번호
				sum.add(rs.getString(2));//팔린갯수
			}
			for(int i=0; i<m_no.size(); i++) {
				sql = "select m_name from menu where m_no = '" + m_no.get(i) + "'";
				rs = st.executeQuery(sql);
				while(rs.next()) {
					m_name.add(rs.getString(1));//메뉴이름
				}
			}
		}
		catch(Exception e) {
			
		}
	}
	public Top5() {
		// TODO Auto-generated constructor stub
		setTitle("인기상품 Top5");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		c.add(new NorthPanel(), BorderLayout.NORTH);
		cp = new CenterPanel();
		c.add(cp, BorderLayout.CENTER);
		
		setSize(500, 500);
		setVisible(true);
	}
}
