package SouceCode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.mindrot.jbcrypt.BCrypt;

import entities.Staff;
import models.StaffModel;

public class JFrameChangePasswordFirstTime extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JLabel lblMessage;

    private final Staff currentStaff;
    private final StaffModel staffModel = new StaffModel();

    public JFrameChangePasswordFirstTime(Staff staff) {
        this.currentStaff = staff;

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // ================= LOGO =================
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/img-logo-warehouse-manager.png"));
        if (icon.getImage() != null) {
            Image img = icon.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            lblLogo.setBounds(175, 20, 150, 120);
            contentPane.add(lblLogo);
        }

        // ================= TITLE =================
        JLabel lblTitle = new JLabel("CHANGE YOUR PASSWORD FOR THE FIRST TIME");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(255, 99, 71));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(50, 150, 400, 40);
        contentPane.add(lblTitle);

        JLabel lblInfo = new JLabel(
                "<html>You are logging in for the first time.<br>Please enter a new password to continue.</html>");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBounds(50, 190, 400, 60);
        contentPane.add(lblInfo);

        // ================= NEW PASSWORD =================
        JLabel lblNew = new JLabel("New password:");
        lblNew.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblNew.setBounds(80, 260, 120, 30);
        contentPane.add(lblNew);

        txtNewPassword = new JPasswordField();
        txtNewPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtNewPassword.setBounds(210, 260, 210, 35);
        contentPane.add(txtNewPassword);

        // ================= CONFIRM PASSWORD =================
        JLabel lblConfirm = new JLabel("Confirm:");
        lblConfirm.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblConfirm.setBounds(80, 300, 120, 30);
        contentPane.add(lblConfirm);

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtConfirmPassword.setBounds(210, 300, 210, 35);
        contentPane.add(txtConfirmPassword);

        // ================= MESSAGE =================
        lblMessage = new JLabel("");
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setBounds(50, 340, 400, 25);
        contentPane.add(lblMessage);

        // ================= BUTTON CHANGE =================
        JButton btnChange = new JButton("CHANGE PASSWORD");
        btnChange.setBackground(new Color(50, 205, 50));
        btnChange.setForeground(Color.WHITE);
        btnChange.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnChange.setBounds(150, 380, 200, 45);
        btnChange.setFocusPainted(false);
        contentPane.add(btnChange);

        // ================= BUTTON CLOSE =================
        JButton btnClose = new JButton("X");
        btnClose.setBounds(470, 5, 25, 25);
        btnClose.setBackground(new Color(255, 99, 71));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnClose.setBorder(null);
        btnClose.setFocusPainted(false);

        btnClose.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "You are required to change your password the first time you use the system!",
                    "Notification",
                    JOptionPane.WARNING_MESSAGE);
        });
        contentPane.add(btnClose);

        // ================= EVENTS =================
        btnChange.addActionListener(e -> performChangePassword());
        txtConfirmPassword.addActionListener(e -> performChangePassword());
    }

    // =====================================================
    // ================= CHANGE PASSWORD ===================
    // =====================================================
    private void performChangePassword() {
        String newPass = new String(txtNewPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("Please enter your full password!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError("The verification password does not match!");
            return;
        }

        if (newPass.length() < 6) {
            showError("Passwords must be at least 6 characters long!");
            return;
        }

        String hashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());

        boolean updated = staffModel.updatePassword(currentStaff.getId(), hashedPassword);
        boolean confirmed = staffModel.confirmPasswordChanged(currentStaff.getId());

        if (updated && confirmed) {
        	// ⭐ SET SESSION TRƯỚC KHI VÀO DASHBOARD
            Session.currentStaff = currentStaff;
            Session.isLoggedIn = true;

//            JFrameDashboardAdmin dashboard = new JFrameDashboardAdmin(staff);
//            dashboard.setVisible(true);
            this.dispose();
        } else {
            showError("Password change failed! Please try again.");
        }
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setForeground(Color.RED);
    }
}
