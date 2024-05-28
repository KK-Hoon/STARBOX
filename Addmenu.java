package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DB.Connect;

public class Addmenu extends JFrame{
	private JTextField jt[] = new JTextField[2];
	private JComboBox<String> combo;
	private ImageIcon icon;
	private Container c = getContentPane();
	private JLabel photo = new JLabel("");
	private Connection con = Connect.makeConnection("coffee");
	private Statement st = null;
	private PreparedStatement psmt = null;
	private String filePath = null;
	
	class CenterPanel extends JPanel{
		String type[] = {"음료", "푸드", "상품"};
		String s[] = {"분류", "메뉴명", "가격"};
		JButton btn = new JButton("사진등록");
		JLabel label[] = new JLabel[3];
		
		
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
			combo = new JComboBox<String>(type);
			combo.setBounds(70, 20, 50, 30);
			add(combo);
			
			//jtextfield추가
			int jtx = 70;
			int jty = 70;
			for(int i=0; i<jt.length; i++) {
				jt[i] = new JTextField(15);
				jt[i].setBounds(jtx, jty, 160, 30);
				add(jt[i]);
				jty += 50;
			}
			//btn추가
			btn.setBounds(240, 140, 140, 30);
			btn.addActionListener(new BtnListener());
			add(btn);
			
			//photo추가
			photo.setBounds(240, 10, 140, 140);
			photo.setBorder(BorderFactory.createLineBorder(Color.black));
			add(photo);
		}
	}
	
	class SouthPanel extends JPanel{
		String s[] = {"등록", "취소"};
		JButton btn[] = new JButton[2];
		
		public SouthPanel() {
			// TODO Auto-generated constructor stub
			for(int i=0; i<s.length; i++) {
				btn[i] = new JButton(s[i]);
				btn[i].addActionListener(new BtnListener());
				add(btn[i]);
			}
		}
	}
	
	class BtnListener implements ActionListener{
		 @Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String s = e.getActionCommand();
			
			switch(s) {
			case"등록":
				Addmenu();
				break;
			case"취소":
				new ManagerMenu();
				dispose();
				break;
			case"사진등록":
				Addphoto();
			}
		}
	}
	
	//사진등록 메소드
	public void Addphoto() {
		JFileChooser chooser = new JFileChooser();
		int ret = chooser.showOpenDialog(null);
		filePath = chooser.getSelectedFile().getPath();
		icon = new ImageIcon(filePath);
		Image img = icon.getImage();
		Image changeImg = img.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
		icon = new ImageIcon(changeImg);
		photo.setIcon(icon);
	}	
	
	//메뉴등록 메소드
	public void Addmenu() {
		String type = combo.getSelectedItem().toString();
		String menu = jt[0].getText();
		String price = jt[1].getText();
		
		//빈칸존재할경우
		if(type.equals("") || menu.equals("") || price.equals("")) {
			JOptionPane.showMessageDialog(null, "빈칸이 존재합니다.", "메세지", JOptionPane.ERROR_MESSAGE);
		}
		//가격이 숫자가 아닐경우
		else if(!IsDigit(price)) {
			JOptionPane.showMessageDialog(null, "가격은 숫자로 입력해주세요.", "메세지", JOptionPane.ERROR_MESSAGE);
		}
		else {
			try {
				st = con.createStatement();
				String sql = "select * from menu where m_name = '" + menu + "'";
				ResultSet rs = st.executeQuery(sql);
				if(rs.next()) {
					JOptionPane.showMessageDialog(null, "이미 존재하는 메뉴명입니다.", "메세지", JOptionPane.ERROR_MESSAGE);
				}
				else {
//					//메뉴 db에 저장
					psmt = con.prepareStatement("insert into menu(m_group, m_name, m_price) values(?,?,?)");
					psmt.setString(1, type);
					psmt.setString(2, menu);
					psmt.setString(3, price);
					psmt.executeUpdate();
					
					//메뉴 img 저장
					Image img = ImageIO.read(new File(filePath));
					Image reimg = img.getScaledInstance(300, 313, Image.SCALE_SMOOTH);
					BufferedImage newImage = new BufferedImage(300, 313, BufferedImage.TYPE_INT_RGB);
					
					Graphics g = newImage.getGraphics();
					g.drawImage(reimg, 0, 0, null);
					g.dispose();
					
					ImageIO.write(newImage, "jpg", new File("C:\\Users\\pc 1-12\\Desktop\\db_coffee\\Coffee\\DataFiles\\이미지\\" + menu + ".jpg"));
					JOptionPane.showMessageDialog(null, "메뉴가 등록되었습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
					dispose();
					new ManagerMenu();
				}
			}
			catch(Exception e) {
				
			}
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
	
	public Addmenu() {
		// TODO Auto-generated constructor stub
		setTitle("메뉴추가");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		c.add(new CenterPanel(), BorderLayout.CENTER);
		c.add(new SouthPanel(), BorderLayout.SOUTH);
		
		setSize(400, 250);
		setVisible(true);
	}
}
