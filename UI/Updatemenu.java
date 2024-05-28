package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import DB.Connect;

public class Updatemenu extends JFrame{
	
	private Container c = getContentPane();
	private JComboBox<String> combo;
	private JTextField jt_search;
	private JTextField jt[] = new JTextField[2];
	private JComboBox<String> combo2;
	private JLabel photo = new JLabel("");
	private JTable table;
	private DefaultTableModel model;
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private Vector<String> colData = new Vector<String>();
	private Statement st = null;
	private PreparedStatement psmt = null;
	private Connection con = Connect.makeConnection("coffee");
	private JScrollPane js;
	private String filePath;
	private ImageIcon icon;
	private String m_name;
	
	class NorthPanel extends JPanel{
		
		JLabel label = new JLabel("검색");
		String type[] = {"전체", "음료", "푸드", "상품"};
		JButton btn = new JButton("찾기");
		
		public NorthPanel() {//northpanel
			// TODO Auto-generated constructor stub
			setLayout(new BorderLayout());
			add(new N_WestPanel(), BorderLayout.WEST);
			
		}
		class N_WestPanel extends JPanel{//northpanel의 westpanel
			
			public N_WestPanel() {
				// TODO Auto-generated constructor stub
				//라벨추가
				label.setHorizontalAlignment(JLabel.LEFT);
				add(label);
				
				//combobox추가
				combo = new JComboBox<String>(type);
				combo.addActionListener(new ComboListener());
				add(combo);
				
				//jtextfield추가
				jt_search = new JTextField(20);
				add(jt_search);
				
				//btn추가
				btn.addActionListener(new BtnListener());
				add(btn);
			}
		}
	}
	
	class CenterPanel extends JPanel{//centerpanel
		
		String type[] = {"음료", "푸드", "상품"};
		String s[] = {"분류", "메뉴명", "가격"};
		JButton btn_photo = new JButton("사진선택");
		JButton btn[] = new JButton[3];
		JLabel label[] = new JLabel[3];
		String btn_s[] = {"삭제", "수정", "취소"};
		
		public CenterPanel() {
			// TODO Auto-generated constructor stub
			setLayout(null);
			
			//jlabel 추가
			int labelx = 5;
			int labely = 20;
			for(int i=0; i<label.length; i++) {
				label[i] = new JLabel(s[i]);
				label[i].setBounds(labelx, labely, 70, 30);
				add(label[i]);
				labely += 50;
			}
			
			//combobox추가
			combo2 = new JComboBox<String>(type);
			combo2.setBounds(70, 20, 50, 30);
			add(combo2);
			
			//jtextfield추가
			int jtx = 70;
			int jty = 70;
			for(int i=0; i<jt.length; i++) {
				jt[i] = new JTextField(20);
				jt[i].setBounds(jtx, jty, 160, 30);
				add(jt[i]);
				jty += 50;
			}
			//btn_photo추가
			btn_photo.setBounds(240, 140, 140, 30);
			btn_photo.addActionListener(new BtnListener());
			add(btn_photo);
			
			//photo추가
			photo.setBounds(240, 10, 140, 140);
			photo.setBorder(BorderFactory.createLineBorder(Color.black));
			add(photo);
			
			//btn 추가
			int btn_x = 90;
			int btn_y = 180;
			
			for(int i=0; i<btn.length; i++) {
				btn[i] = new JButton(btn_s[i]);
				btn[i].addActionListener(new BtnListener());
				btn[i].setBounds(btn_x, btn_y, 60, 30);
				add(btn[i]);
				btn_x += 80;
			}
		}
	}
	
	class ComboListener implements ActionListener{//콤보박스 액션리스너
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JComboBox<String> com = (JComboBox<String>)e.getSource();
			
			if(com == combo) {
				try {
					String type = com.getSelectedItem().toString();
					getTable(type, "");
					c.remove(js);
					js = new JScrollPane(table);
					c.add(js, BorderLayout.WEST);
					c.validate();
				}
				catch(Exception ee) {
					
				}
			}
			else {
				
			}
			jt_search.setText("");
		}
	}
	class BtnListener implements ActionListener{//버튼액션리스너
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String s = e.getActionCommand();
			int index = table.getSelectedRow();
			
			switch(s) {
				case "찾기":
					getTable(combo.getSelectedItem().toString(), jt_search.getText());
					break;
				case "삭제":
					if(index<0 || index>rowData.size()) {
						JOptionPane.showMessageDialog(null, "삭제할 메뉴를 선택해주세요.", "메세지", JOptionPane.ERROR_MESSAGE);
					}
					else {
						try {
							st = con.createStatement();
							String sql = "delete from menu where m_name = '" + jt[0].getText() + "'";
							st.executeUpdate(sql);
							JOptionPane.showMessageDialog(null, "삭제되었습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
							jt[0].setText("");
							jt[1].setText("");
							photo.setIcon(null);
							getTable("전체", "");
						}
						catch(Exception ee) {
							
						}
					}
					break;
				case "수정":
					if(index<0 || index>rowData.size()) {
						JOptionPane.showMessageDialog(null, "삭제할 메뉴를 선택해주세요.", "메세지", JOptionPane.ERROR_MESSAGE);
					}
					else if(jt[0].getText().equals("") || jt[1].getText().equals("")){
						JOptionPane.showMessageDialog(null, "빈칸이 존재합니다.", "메세지", JOptionPane.ERROR_MESSAGE);
					}
					else if(!IsDigit(jt[1].getText())) {
						JOptionPane.showMessageDialog(null, "가격을 다시 입력해주세요.", "메세지", JOptionPane.ERROR_MESSAGE);
					}
					else {
						try {
							st = con.createStatement();
							String sql = "select * from menu where not m_name = '" + m_name + "' and m_name = '" + jt[0].getText() + "'";
							ResultSet rs = st.executeQuery(sql);
							if(rs.next()) {
								JOptionPane.showMessageDialog(null, "이미 존재하는 메뉴명입니다.", "메세지", JOptionPane.ERROR_MESSAGE);
							}
							else {
								sql = "update menu set m_name = '" + jt[0].getText() + "', m_price = '" + jt[1].getText() + "' where m_name = '" + m_name +"'";
								st.executeUpdate(sql);
								JOptionPane.showMessageDialog(null, "수정되었습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
								getTable("전체", "");
							}
							
						}
						catch(Exception ee){
							
						}
					}
					break;
				case "취소":{
					new ManagerMenu();
					dispose();
					break;
				}
			}
			c.remove(js);
			js = new JScrollPane(table);
			c.add(js, BorderLayout.WEST);
			c.validate();
		}
	}
	
	//숫자판별 메소드
		public boolean IsDigit(String s) {
			try {
				Integer.parseInt(s);
				return true;
			}
			catch(NumberFormatException e) {
				return false;
			}
		}
	
	public void getTable(String type, String search) {//table 초기화 메소드
		String s[] = {"분류", "메뉴명", "가격"};
		String sql = "";
		
		colData.clear();
		rowData.clear();
		
		for(int i=0; i<s.length; i++) {//colData초기화
			colData.add(s[i]);
		}
		
		try {
			st = con.createStatement();
			
			//rowData초기화
			if(search.equals("")) {
				if(type.equals("전체")) {
					sql = "select * from menu";
				}
				else {
					sql = "select * from menu where m_group = '" + type + "'";
				}
			}
			else {
				if(type.equals("전체")) {
					sql = "select * from menu where m_name like '%" + search + "%'";
				}
				else {
					sql = "select * from menu where m_group = '" + type + "' and m_name like '%" + search + "%'";
				}
			}
			
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				Vector<String> v = new Vector<String>();
				for(int i=0; i<3; i++) {
					v.add(rs.getString(i+2));
				}
				rowData.add(v);
			}
			
			model = new DefaultTableModel(rowData, colData);
			table = new JTable(model);
			table.addMouseListener(new TableListener());
			
			//테이블 정렬
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			TableColumnModel tcm = table.getColumnModel();
			for(int i=0; i<tcm.getColumnCount(); i++){
				if(i==0 || i==2) {
					tcm.getColumn(i).setCellRenderer(dtcr);
				}
				tcm.getColumn(i).setPreferredWidth(70);
			}
			table.getColumn("메뉴명").setPreferredWidth(160);
		}
		catch(Exception e) {
			
		}
	}
	
	class TableListener extends MouseAdapter{//테이블 클릭리스너
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			int index = table.getSelectedRow();
			Vector<String> v = rowData.get(index);
			combo2.setSelectedItem(v.get(0));//분류 콤보
			jt[0].setText(v.get(1));//메뉴명 라벨
			m_name = v.get(1);
			jt[1].setText(v.get(2));//가격 라벨
			Addphoto(v.get(1));
		}
	}
	
	public void Addphoto(String menu) {//사진등록 메소드
		filePath = "C:\\Users\\pc 1-12\\Desktop\\db_coffee\\Coffee\\DataFiles\\이미지\\" + menu + ".jpg";
		icon = new ImageIcon(filePath);
		Image img = icon.getImage();
		Image changeImg = img.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
		icon = new ImageIcon(changeImg);
		photo.setIcon(icon);
	}
	
	public Updatemenu() {
		// TODO Auto-generated constructor stub
		setTitle("메뉴수정");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		c.add(new NorthPanel(), BorderLayout.NORTH);
		
		getTable("전체", "");
		js = new JScrollPane(table);
		c.add(js, BorderLayout.WEST);
		
		c.add(new CenterPanel(), BorderLayout.CENTER);
		
		c.add(new JLabel(""), BorderLayout.SOUTH);
		setSize(900, 300);
		setVisible(true);
	}
}
