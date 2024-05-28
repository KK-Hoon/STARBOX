package UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ManagerMenu extends JFrame{
	
	class CenterPanel extends JPanel{
		String s[] = {"메뉴 등록", "메뉴 관리", "로그아웃"};
		JButton btn[] = new JButton[3];
		
		public CenterPanel() {
			// TODO Auto-generated constructor stub
			setLayout(new GridLayout(3,1));
			for(int i=0; i<btn.length; i++) {
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
			case"메뉴 등록":
				dispose();
				new Addmenu();
				break;
			case"메뉴 관리":
				dispose();
				new Updatemenu();
				break;
			case"로그아웃":
				new Login();
				dispose();
				break;
			}
		}
	}
	
	public ManagerMenu() {
		// TODO Auto-generated constructor stub
		setTitle("관리자 메뉴");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		
		c.add(new CenterPanel(), BorderLayout.CENTER);
		
		setSize(300,200);
		setVisible(true);
	}
}
