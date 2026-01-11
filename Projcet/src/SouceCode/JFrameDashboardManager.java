package SouceCode;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Date;
public class JFrameDashboardManager extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel jpanelMain;
    private JPanel panel;
    private boolean isMaximized = false;
    private Rectangle normalBounds;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                JFrameDashboardManager frame = new JFrameDashboardManager();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public JFrameDashboardManager() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1300, 750); // Tăng kích thước để chứa form khách hàng
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // ===== MENU BÊN TRÁI =====
        panel = new JPanel();
        panel.setBackground(new Color(247, 222, 155));
        panel.setPreferredSize(new Dimension(260, 0));
        contentPane.add(panel, BorderLayout.WEST);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Logo
        JLabel lblNewLabel = new JLabel();
        java.net.URL logoURL = getClass().getResource("/resources/img-logo-warehouse-manager.png");
        if (logoURL != null) {
            ImageIcon icon = new ImageIcon(logoURL);
            Image img = icon.getImage().getScaledInstance(260, 150, Image.SCALE_SMOOTH);
            lblNewLabel.setIcon(new ImageIcon(img));
        } else {
            lblNewLabel.setText("Logo not found");
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblNewLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Các nút menu
        JButton btnDashboard = createMenuButton("Manager Dashboard", "/resources/icon-dashboard.png");
        JButton btnCreateBill = createMenuButton("Create Bill", "/resources/icon-add-bill.png");
        JButton btnSalesReport = createMenuButton("Sales report", "/resources/salereport.png");
        JButton btnOrderList = createMenuButton("Order List", "/resources/orderlist.png");
        JButton btnOrderDetails = createMenuButton("Order details", "/resources/orderlist.png");
        JButton btnChangePass = createMenuButton("Change Password", "/resources/icon-change-pass.png");
        JButton btnLogout = createMenuButton("Logout", "/resources/icon-logout.png");

        panel.add(btnDashboard);
        panel.add(btnCreateBill);
        panel.add(btnSalesReport);
        panel.add(btnOrderList);
        panel.add(btnOrderDetails); 
        panel.add(Box.createVerticalGlue()); 
        panel.add(btnChangePass);
        panel.add(btnLogout);

        // ===== MAIN CONTENT =====
        jpanelMain = new JPanel();
        jpanelMain.setLayout(new BorderLayout());
        contentPane.add(jpanelMain, BorderLayout.CENTER);

        // ===== TOP BAR (Min, Max, Close) =====
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTop.setBackground(Color.WHITE);
        contentPane.add(panelTop, BorderLayout.NORTH);

        JButton btnMin = new JButton("-");
        styleTopButton(btnMin);
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));
        panelTop.add(btnMin);

        JButton btnMax = new JButton("❐");
        styleTopButton(btnMax);
        btnMax.addActionListener(e -> toggleMaximize(btnMax));
        panelTop.add(btnMax);

        JButton btnClose = new JButton("X");
        styleTopButton(btnClose);
        btnClose.addActionListener(e -> System.exit(0));
        panelTop.add(btnClose);

        // ===== DRAG WINDOW =====s
        enableDrag();
       
        btnDashboard.addActionListener(e -> loadPanel(new JPanelSalesReport()));
        btnCreateBill.addActionListener(e -> loadPanel(new JPanelSalesDashboard()));
        btnSalesReport.addActionListener(e -> loadPanel(new JPanelSalesReport()));
        btnOrderList.addActionListener(e -> loadPanel(new JPanelOrderList()));
        btnOrderDetails.addActionListener(e -> loadPanel(new JPanelOrderDetails())); 
        btnChangePass.addActionListener(e -> loadPanel(new JPanelChangePass()));
        btnLogout.addActionListener(e -> logout());

        loadPanel(new JPanelSalesReport());
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(250, 55));
        btn.setMaximumSize(new Dimension(250, 55));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMargin(new Insets(10, 20, 10, 20));

        if (iconPath != null) {
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaled = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaled));
            }
        }

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setForeground(Color.DARK_GRAY);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private void styleTopButton(JButton btn) {
        btn.setPreferredSize(new Dimension(40, 30));
        btn.setFocusable(false);
        btn.setBackground(new Color(255, 99, 71));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn.setBorder(null);
    }

    private void toggleMaximize(JButton btnMax) {
        if (!isMaximized) {
            normalBounds = getBounds();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(0, 0, screen.width, screen.height);
            isMaximized = true;
        } else {
            setBounds(normalBounds);
            isMaximized = false;
        }
    }

    private void enableDrag() {
        final Point[] dragPoint = new Point[1];
        contentPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragPoint[0] = e.getLocationOnScreen();
                dragPoint[0].x -= getX();
                dragPoint[0].y -= getY();
            }
        });
        contentPane.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - dragPoint[0].x, e.getYOnScreen() - dragPoint[0].y);
            }
        });
    }

    private void loadPanel(JPanel panel) {
        jpanelMain.removeAll();
        jpanelMain.add(panel, BorderLayout.CENTER);
        jpanelMain.revalidate();
        jpanelMain.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Sign out", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Session.isLoggedIn = false;
            Session.username = null;
            new JFrameLogin().setVisible(true);
            this.dispose();
        }
    }
}