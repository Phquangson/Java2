package SouceCode;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import entities.Staff;
import models.StaffModel;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class JFrameLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField jtextFieldUsername;
	private JPasswordField jpasswordFieldPassword;
	private ImageIcon iconShow;
	private ImageIcon iconHide;
	private boolean isPasswordVisible = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrameLogin frame = new JFrameLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JFrameLogin() {

		setForeground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 980, 569);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 381, 569);
		panel.setBackground(Color.WHITE);
		contentPane.add(panel);
		panel.setLayout(null);

		iconShow = new ImageIcon(getClass().getResource("/resources/icon-eye-view.png"));
		iconHide = new ImageIcon(getClass().getResource("/resources/icon-eye-hide.png"));

		JButton btnTogglePass = new JButton(iconHide);
		btnTogglePass.setBounds(310, 304, 28, 28);
		btnTogglePass.setBorderPainted(false);
		btnTogglePass.setContentAreaFilled(false);
		btnTogglePass.setFocusPainted(false);
		btnTogglePass.setOpaque(false);
		panel.add(btnTogglePass);

		btnTogglePass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (isPasswordVisible) {
					jpasswordFieldPassword.setEchoChar('*');
					btnTogglePass.setIcon(iconHide);
				} else {
					jpasswordFieldPassword.setEchoChar((char) 0);
					btnTogglePass.setIcon(iconShow);
				}
				isPasswordVisible = !isPasswordVisible;
			}
		});

		JButton btnClose = new JButton("X");
		btnClose.setBounds(940, 10, 30, 30);
		btnClose.setFocusable(false);
		btnClose.setBackground(new Color(255, 99, 71));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnClose.setBorder(null);

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JLabel lblNewLabel_1_2 = new JLabel("LOG IN");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel_1_2.setBounds(30, 69, 319, 42);
		panel.add(lblNewLabel_1_2);

		JLabel lblNewLabel_1_1_1 = new JLabel("Welcome to the Smart Warehouse Management System");
		lblNewLabel_1_1_1.setForeground(Color.GRAY);
		lblNewLabel_1_1_1.setFont(new Font("Tahoma", Font.ITALIC, 13));
		lblNewLabel_1_1_1.setBounds(30, 107, 328, 42);
		panel.add(lblNewLabel_1_1_1);

		JLabel lblNewLabel_1 = new JLabel("Username");
		lblNewLabel_1.setForeground(Color.GRAY);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(30, 174, 79, 36);
		panel.add(lblNewLabel_1);

		jtextFieldUsername = new JTextField();
		jtextFieldUsername.setBounds(30, 221, 309, 30);
		panel.add(jtextFieldUsername);

		JLabel lblNewLabel_1_1 = new JLabel("Password");
		lblNewLabel_1_1.setForeground(Color.GRAY);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1_1.setBounds(30, 262, 79, 30);
		panel.add(lblNewLabel_1_1);

		setUndecorated(true);

		final int[] mouseX = { 0 };
		final int[] mouseY = { 0 };

		contentPane.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				mouseX[0] = evt.getX();
				mouseY[0] = evt.getY();
			}
		});

		contentPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				int x = evt.getXOnScreen();
				int y = evt.getYOnScreen();
				setLocation(x - mouseX[0], y - mouseY[0]);
			}
		});

		contentPane.add(btnClose);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(379, 0, 601, 569);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(JFrameLogin.class.getResource("/resources/img-login-warehouse.png")));
		lblNewLabel.setBounds(0, 0, 600, 569);
		panel_1.add(lblNewLabel);

		jpasswordFieldPassword = new JPasswordField();
		jpasswordFieldPassword.setBounds(29, 304, 310, 30);
		panel.add(jpasswordFieldPassword);

		JButton jbuttonLogin = new JButton("Log in");
		jbuttonLogin.setFont(new Font("Dialog", Font.BOLD, 17));
		jbuttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_jbuttonLogin_actionPerformed(e);
			}
		});

		jpasswordFieldPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				do_jbuttonLogin_actionPerformed(e);
			}
		});

		jtextFieldUsername.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				do_jbuttonLogin_actionPerformed(e);
			}
		});

		jbuttonLogin.setForeground(Color.WHITE);
		jbuttonLogin.setBackground(new Color(90, 156, 181));
		jbuttonLogin.setBounds(99, 378, 173, 50);

		jbuttonLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel.add(jbuttonLogin);
	}

	// Btn Log in
	protected void do_jbuttonLogin_actionPerformed(ActionEvent e) {
		StaffModel staffModel = new StaffModel();
		String username = jtextFieldUsername.getText().trim();
		String password = new String(jpasswordFieldPassword.getPassword());

		if (staffModel.loginStaff(username, password)) {
			Staff staff = staffModel.findByUsername(username);
			Session.currentStaff = staff;

			JDialog successDialog = new JDialog(this, true);
			successDialog.setUndecorated(true);

			JPanelSuccess panel = new JPanelSuccess();
			successDialog.getContentPane().add(panel);
			successDialog.pack();
			successDialog.setLocationRelativeTo(this);

			successDialog.setVisible(true);

			switch (staff.getIdPosition()) {
			case 1:
				JFrameDashboardAdmin adminFrame = new JFrameDashboardAdmin(staff);
				adminFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				adminFrame.setVisible(true);
				break;
			case 2:
				JFrameDashboardWarehouseManager wmFrame = new JFrameDashboardWarehouseManager(staff);
				wmFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				wmFrame.setVisible(true);
				break;

			case 3:
				JFrameDashboardManager managerFrame = new JFrameDashboardManager();
				managerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				managerFrame.setVisible(true);
				break;
			case 4:
				JFrameDashboardSales salesFrame = new JFrameDashboardSales();
				salesFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				salesFrame.setVisible(true);
				break;
			case 5:
				JFrameDashboardSupperAdmin saFrame = new JFrameDashboardSupperAdmin(staff);
				saFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				saFrame.setVisible(true);
				break;
			default:
				JOptionPane.showMessageDialog(null, "Unknown role", "Error", JOptionPane.ERROR_MESSAGE);
			}

			JFrameLogin.this.setVisible(false);

		} else {
			JOptionPane.showMessageDialog(null, "Login failed", "Log in", JOptionPane.ERROR_MESSAGE);
		}
	}
	

}
