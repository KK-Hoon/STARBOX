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
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import DB.Connect;

public class Orderlist extends JFrame{
	private String u_id;
	private String u_name;
	private String u_no;
	private Vector<String> colData = new Vector<String>();
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private DefaultTableModel model;
	private JTable table;
	private Connection con = Connect.makeConnection("coffee");
	private Statement st = null;
	private PreparedStatement psmt = null;
	private DecimalFormat df = new DecimalFormat("###,###");
	
	class NorthPanel extends JPanel{//northpanel 
		JLabel label = new JLabel(u_name + "회원님 구매내역");
		
		public NorthPanel() {
			// TODO Auto-generated constructor stub
			label.setFont(new Font("", Font.BOLD, 20));
			add(label);
		}
	}
	
	class CenterPanel extends JPanel{//centerpanel 
		String col[] = {"구매일자", "메뉴명", "가격", "사이즈", "수량", "총금액"};
		
		public CenterPanel() {
			// TODO Auto-generated constructor stub
			for(int i=0; i<col.length; i++) {//colData 초기화
				colData.add(col[i]);
			}
			
			try {//JTable
				st = con.createStatement();
				String sql = "select o_date, m_name, m_price, o_size, o_count, o_amount from orderlist join menu "
						+ "where u_no = '" + u_no + "' and menu.m_no = orderlist.m_no";
				ResultSet rs = st.executeQuery(sql);
				while(rs.next()) {
					Vector<String> v = new Vector<String>();
					for(int i=0; i<col.length; i++) {//사이즈가 없을때
						if(rs.getString(i+1).equals("X")) {
							v.add("");
						}
						else if(i==2 || i==5){//천단위 쉼표
							String money = df.format(rs.getInt(i+1));
							v.add(money);
						}
						else {
							v.add(rs.getString(i+1));
						}
					}
					rowData.add(v);
				}
				model = new DefaultTableModel(rowData, colData);
				table = new JTable(model);
				
				//Jtable 가운데 정렬
				DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
				dtcr.setHorizontalAlignment(SwingConstants.CENTER);
				TableColumnModel tcm = table.getColumnModel();
				for(int i=0; i<tcm.getColumnCount(); i++){
					tcm.getColumn(i).setCellRenderer(dtcr);
					tcm.getColumn(i).setPreferredWidth(70);
				}
				table.getColumn("메뉴명").setPreferredWidth(160);
			}
			catch(Exception e) {
				
			}
		}
	}
	
	class SouthPanel extends JPanel{//southpanel
		JLabel label = new JLabel("총 결제 금액");
		JTextField jt = new JTextField(20);
		JButton btn = new JButton("닫기");
		int sum = 0;
		
		public SouthPanel() {
			// TODO Auto-generated constructor stub
			try {
				st = con.createStatement();
				String sql = "select sum(o_amount) from orderlist where u_no = '" + u_no + "'";
				ResultSet rs = st.executeQuery(sql);
				while(rs.next()) {
					sum = rs.getInt(1);
				}
				String money = df.format(sum);
				jt.setText(money);
				add(label);
				jt.setEnabled(false);
				jt.setHorizontalAlignment(JTextField.RIGHT);
				add(jt);
				btn.addActionListener(new BtnListener());
				add(btn);
			}
			catch(Exception e) {
				
			}
		}
	}
	class BtnListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			dispose();
			new StarBox(u_id);
		}
	}
	public Orderlist(String name, String uid) {
		// TODO Auto-generated constructor stub
		this.u_id = uid;//user id
		this.u_name = name;//user name
		try {
			st = con.createStatement();
			String sql = "select u_no from user where u_id = '" + u_id + "'";
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				u_no = rs.getString(1);
			}
		}
		catch(Exception e) {
			
		}
		setTitle("구매내역");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		
		c.add(new NorthPanel(), BorderLayout.NORTH);
		
		new CenterPanel();
		c.add(new JScrollPane(table), BorderLayout.CENTER);
		
		c.add(new SouthPanel(), BorderLayout.SOUTH);
		
		setSize(800, 400);
		setVisible(true);
	}
}
