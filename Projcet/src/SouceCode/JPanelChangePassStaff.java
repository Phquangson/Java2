package SouceCode;

import java.awt.*;
import javax.swing.*;
import SouceCode.Session;
import models.StaffModel;
import org.mindrot.jbcrypt.BCrypt;

import entities.Staff;

public class JPanelChangePassStaff extends JPanel {
    private JPasswordField txtOld, txtNew, txtConfirm;
    private StaffModel staffModel = new StaffModel();

    public JPanelChangePassStaff() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("CHANGE PASSWORD");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(255, 99, 71));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        txtOld = new JPasswordField(25);
        add(txtOld, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        txtNew = new JPasswordField(25);
        add(txtNew, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        txtConfirm = new JPasswordField(25);
        add(txtConfirm, gbc);

        JButton btnChange = new JButton("Change Password");
        btnChange.setBackground(new Color(50, 205, 50));
        btnChange.setForeground(Color.WHITE);
        btnChange.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(btnChange, gbc);

        btnChange.addActionListener(e -> changePassword());
        }


    private void changePassword() {
        String oldPass = new String(txtOld.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all the information!");
            return;
        }

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "The verification password doesn't match!");
            return;
        }

        Staff current = Session.currentStaff;
        if (!BCrypt.checkpw(oldPass, current.getPassword())) {
            JOptionPane.showMessageDialog(this, "The old password is wrong!");
            return;
        }

        String hashed = BCrypt.hashpw(newPass, BCrypt.gensalt());
        if (staffModel.updatePassword(current.getId(), hashed)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
            txtOld.setText("");
            txtNew.setText("");
            txtConfirm.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Password change failed!");
        }
    }
}