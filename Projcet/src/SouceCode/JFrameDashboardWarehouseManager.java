package SouceCode;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import entities.Inventory;
import entities.Product;
import entities.Staff;
import models.InventoryModel;
import models.ProductModel;
import models.StaffModel;

public class JFrameDashboardWarehouseManager extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel jpanelMain;
	private JButton jbuttonProducts;
	private JPanel panel_1;
	private JPanel panel;
	private boolean isMaximized = false;
	private Rectangle normalBounds;
	private JLabel lblNewLabel;
	private JButton jbuttonChangePass;
	private JButton jbuttonLogout;
	private JButton jbuttonStockOut;
	private JButton jbuttonStockIn;
	private JButton jbuttonProductReceiptNote;
	private JButton jbuttonProductsIssueNote;
	private JButton jbuttonDashboard;
	private JButton jbuttonAddSupplier;
	private Runnable onOpenProfile;
	private Staff currentStaff;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(() -> {
			try {
				// giả lập staff đăng nhập
				Staff staffLogin = new Staff();

				JFrameDashboardWarehouseManager frame = new JFrameDashboardWarehouseManager(staffLogin);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JFrameDashboardWarehouseManager(Staff staff) {
		this.currentStaff = staff;
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 983, 631);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel.setBackground(new Color(247, 222, 155));
		panel.setForeground(new Color(247, 222, 155));
		contentPane.add(panel, BorderLayout.WEST);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		Dimension btnSize = new Dimension(250, 50);

		// Logo (kiểm tra null để tránh lỗi)
		ImageIcon icon = null;
		java.net.URL logoURL = getClass().getResource("/resources/img-logo-warehouse-manager.png");
		if (logoURL != null) {
			icon = new ImageIcon(logoURL);
			Image img = icon.getImage();
			Image scaledImg = img.getScaledInstance(250, 150, Image.SCALE_SMOOTH);
			lblNewLabel = new JLabel(new ImageIcon(scaledImg));
		} else {
			lblNewLabel = new JLabel("Logo not found");
			System.err.println("No logo found: /resources/img-logo-warehouse-manager.png");
		}
		panel.add(lblNewLabel);

		// Dashboard button
		jbuttonDashboard = createMenuButton("Dashboard", "/resources/icon-dashboard.png");
		panel.add(jbuttonDashboard);
		panel.add(Box.createVerticalStrut(8));

		// Trong constructor JFrameDashboardWarehouseManager
		jbuttonDashboard.addActionListener(e -> {
			clearScreen();
			ProductModel productModel = new ProductModel();
			java.util.List<Product> productList = productModel.findAll();

			JPanelDashboardWarehouse panelDashboard = new JPanelDashboardWarehouse(productList);
			jpanelMain.add(panelDashboard);
			panelDashboard.setVisible(true);
		});

		// Notification button
		JButton jbuttonNotification = createMenuButton("Notification", "/resources/icon-notification.png");
		panel.add(jbuttonNotification);
		panel.add(Box.createVerticalStrut(8));

		// Icon gốc
		ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/icon-notification.png"));

		// Timer lắc chuông (bắt đầu ngay khi mở app)
		Timer timer = new Timer(100, new ActionListener() {
			int count = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				double angle = (count % 2 == 0) ? Math.toRadians(10) : Math.toRadians(-10);
				Image img = originalIcon.getImage();
				int w = img.getWidth(null);
				int h = img.getHeight(null);

				BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = rotated.createGraphics();
				g2d.rotate(angle, w / 2, h / 2);
				g2d.drawImage(img, 0, 0, null);
				g2d.dispose();

				jbuttonNotification.setIcon(new ImageIcon(rotated));
				count++;
			}
		});
		timer.start();

		// Khi click thì dừng lắc
		jbuttonNotification.addActionListener(e -> {
			clearScreen();
			JPanelNotification panelNotification = new JPanelNotification();
			jpanelMain.add(panelNotification);
			panelNotification.setVisible(true);

			timer.stop();
			jbuttonNotification.setIcon(originalIcon);
		});

		// Add supplier button
		jbuttonAddSupplier = createMenuButton("Supplier", "/resources/icon-supplier.png");
		jbuttonAddSupplier.addActionListener(e -> do_jbuttonAddSupplier_actionPerformed(e));
		panel.add(jbuttonAddSupplier);
		panel.add(Box.createVerticalStrut(8));

		// Products button
		jbuttonProducts = createMenuButton("Product", "/resources/icon-product.png");
		jbuttonProducts.addActionListener(e -> do_jbuttonStudents_actionPerformed(e));
		panel.add(jbuttonProducts);
		panel.add(Box.createVerticalStrut(8));

		// Stock in button
//        jbuttonStockIn = createMenuButton("Stock in", "/resources/icon-in-stock.png");
//        jbuttonStockIn.addActionListener(e -> do_jbuttonStockIn_actionPerformed(e));
//        panel.add(jbuttonStockIn);

		// Products Receipt Note button
		jbuttonProductReceiptNote = createMenuButton("Import", "/resources/icon-import.png");
		jbuttonProductReceiptNote.addActionListener(e -> {
			clearScreen();
			JPanelProductsReceiptNote panelProductsReceiptNote = new JPanelProductsReceiptNote();
			jpanelMain.add(panelProductsReceiptNote);
			panelProductsReceiptNote.setVisible(true);
		});
		panel.add(jbuttonProductReceiptNote);
		panel.add(Box.createVerticalStrut(8));

		// Stock out button
//        jbuttonStockOut = createMenuButton("Stock out", "/resources/icon-stock-out.png");
//        jbuttonStockOut.addActionListener(e -> do_jbuttonStockOut_actionPerformed(e));
//        panel.add(jbuttonStockOut);

		// Products Issue Note button
		jbuttonProductsIssueNote = createMenuButton("Export", "/resources/icon-export.png");
		jbuttonProductsIssueNote.addActionListener(e -> {
			clearScreen();
			JPanelProductsIssueNote panelProductsIssueNote = new JPanelProductsIssueNote();
			jpanelMain.add(panelProductsIssueNote);
			panelProductsIssueNote.setVisible(true);
		});
		// Products Issue Note button
		panel.add(jbuttonProductsIssueNote);

		// ===== đẩy các nút bên dưới xuống đáy =====
		panel.add(Box.createVerticalGlue());

		// Profile button
		JButton jbuttonProfile = createMenuButton("Profile", "/resources/icon-profile.png");
		jbuttonProfile.addActionListener(e -> {
			clearScreen();
			JPanelProfile panelProfile = new JPanelProfile(currentStaff); // truyền staff vào
			jpanelMain.add(panelProfile);
			panelProfile.setVisible(true);
		});

		panel.add(jbuttonProfile);
		panel.add(Box.createVerticalStrut(8));

		// Change password button
		jbuttonChangePass = createMenuButton("Change password", "/resources/icon-change-pass.png");
		jbuttonChangePass.addActionListener(e -> do_jbuttonChangePass_actionPerformed(e));
		panel.add(jbuttonChangePass);
		panel.add(Box.createVerticalStrut(8));
		// Logout button
		jbuttonLogout = createMenuButton("Log out", "/resources/icon-logout.png");
		jbuttonLogout.addActionListener(e -> do_jbuttonLogout_actionPerformed(e));
		panel.add(jbuttonLogout);

		jpanelMain = new JPanel();
		contentPane.add(jpanelMain, BorderLayout.CENTER);
		jpanelMain.setLayout(new BorderLayout(0, 0));

		panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		contentPane.add(panel_1, BorderLayout.NORTH);

		JButton btnMin = new JButton("-");
		btnMin.setPreferredSize(new Dimension(30, 30));
		btnMin.setFocusable(false);
		btnMin.setBackground(new Color(255, 99, 71));
		btnMin.setForeground(Color.WHITE);
		btnMin.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnMin.setBorder(null);
		btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
		panel_1.add(btnMin);
		btnMin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton btnMax = new JButton("❐");
		btnMax.setPreferredSize(new Dimension(30, 30));
		btnMax.setFocusable(false);
		btnMax.setBackground(new Color(255, 99, 71));
		btnMax.setForeground(Color.WHITE);
		btnMax.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnMax.setBorder(null);
		panel_1.add(btnMax);
		btnMax.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		btnMax.addActionListener(e -> {
			if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
				setExtendedState(JFrame.NORMAL);
				btnMax.setText("❐");
			} else {
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				btnMax.setText("🗗");
			}
		});

		// ===== CUSTOM CLOSE BUTTON (X) =====
		JButton btnClose = new JButton("X");
		btnClose.setPreferredSize(new Dimension(30, 30));
		btnClose.setFocusable(false);
		btnClose.setBackground(new Color(255, 99, 71));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnClose.setBorder(null);
		panel_1.add(btnClose);
		btnClose.addActionListener(e -> dispose());
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// --- Drag window ---
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

		init();
	}

	// Hàm tạo button menu an toàn (kiểm tra icon null)
	private JButton createMenuButton(String text, String iconPath) {
		JButton btn = new JButton(text);
		btn.setPreferredSize(new Dimension(250, 50));
		btn.setMaximumSize(new Dimension(250, 50));
		btn.setHorizontalTextPosition(SwingConstants.RIGHT);
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setForeground(Color.GRAY);
		btn.setFont(new Font("SansSerif", Font.BOLD, 16));
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setBackground(Color.WHITE);
		btn.setAlignmentX(Component.LEFT_ALIGNMENT);

		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		if (iconPath != null) {
			java.net.URL imgURL = getClass().getResource(iconPath);
			if (imgURL != null) {
				ImageIcon icon = new ImageIcon(imgURL);
				btn.setIcon(icon);
			} else {
				System.err.println("Icon not found: " + iconPath);
			}
		}

		return btn;
	}

	private void init() {
		clearScreen();
		ProductModel productModel = new ProductModel();
		java.util.List<Product> productList = productModel.findAll();

		JPanelDashboardWarehouse panelDashboard = new JPanelDashboardWarehouse(productList);

		jpanelMain.add(panelDashboard);
		panelDashboard.setVisible(true);
		SwingUtilities.invokeLater(() -> checkLowStockProducts());
	}

	private void checkLowStockProducts() {
		InventoryModel inventoryModel = new InventoryModel();
		java.util.List<Inventory> inventories = inventoryModel.findAll();

		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Inventory alert", true);
		dialog.setUndecorated(true);
		dialog.setSize(800, 600);
		dialog.setLocationRelativeTo(this);

		JPanelNotificationProduct notificationPanel = new JPanelNotificationProduct(inventories);
		dialog.getContentPane().add(notificationPanel);

		dialog.setVisible(true);
	}

	protected void do_jbuttonStudents_actionPerformed(ActionEvent e) {
		clearScreen();
		JPanelProducts panelStudents = new JPanelProducts();
		jpanelMain.add(panelStudents);
		panelStudents.setVisible(true);
	}

	private void clearScreen() {
		jpanelMain.removeAll();
		jpanelMain.revalidate();
		jpanelMain.repaint();
	}

	protected void do_jbuttonChangePass_actionPerformed(ActionEvent e) {
		clearScreen();
		JPanelChangePass panelChangePass = new JPanelChangePass();
		jpanelMain.add(panelChangePass);
		panelChangePass.setVisible(true);
	}

	protected void do_jbuttonLogout_actionPerformed(ActionEvent e) {
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout",
				JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.YES_OPTION) {
			Session.isLoggedIn = false;
			Session.username = null;
			JFrameLogin frameLoginWarehouse = new JFrameLogin();
			frameLoginWarehouse.setVisible(true);
			this.dispose();
		}
	}

	protected void do_jbuttonAddSupplier_actionPerformed(ActionEvent e) {
		clearScreen();
		JPanelSupplier panelAddSupplier = new JPanelSupplier();
		jpanelMain.add(panelAddSupplier);
		panelAddSupplier.setVisible(true);
	}
}