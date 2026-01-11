package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.mindrot.jbcrypt.BCrypt;
import entities.Staff;
import models.StaffModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JPanelChangePass extends JPanel {

    private static final long serialVersionUID = 1L;
    private JPasswordField jpasswordFieldNowPass;
    private JPasswordField jpasswordFieldNewPass;
    private JPasswordField jpasswordFieldConfirmPass;
    private ImageIcon iconShow;
    private ImageIcon iconHide;

    public JPanelChangePass() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(247, 222, 155));
        JLabel lblTitleHeader = new JLabel("Change Password");
        lblTitleHeader.setForeground(Color.BLACK);
        lblTitleHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        panelHeader.add(lblTitleHeader);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelFormWrapper = new JPanel(new GridBagLayout()); 
        panelFormWrapper.setBackground(Color.WHITE);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new TitledBorder(null, "Change Password Info",
                TitledBorder.LEADING, TitledBorder.TOP, null, Color.GRAY));
        panelForm.setPreferredSize(new Dimension(600, 300));

        iconShow = new ImageIcon(getClass().getResource("/resources/icon-eye-view.png"));
        iconHide = new ImageIcon(getClass().getResource("/resources/icon-eye-hide.png"));

        panelForm.add(createPasswordRow("Now Password:", jpasswordFieldNowPass = new JPasswordField(25)));

        panelForm.add(createPasswordRow("New Password:", jpasswordFieldNewPass = new JPasswordField(25)));

        panelForm.add(createPasswordRow("Confirm Password:", jpasswordFieldConfirmPass = new JPasswordField(25)));

        JPanel rowButton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rowButton.setBackground(Color.WHITE);
        JButton btnUpdate = new JButton("Update Password");
        btnUpdate.setPreferredSize(new Dimension(200, 40));
        btnUpdate.setBackground(Color.DARK_GRAY);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnUpdate.addActionListener(e -> do_btnChange_actionPerformed(e));
        rowButton.add(btnUpdate);
        panelForm.add(rowButton);

        panelFormWrapper.add(panelForm, new GridBagConstraints());
        add(panelFormWrapper, BorderLayout.CENTER);
    }

    private JPanel createPasswordRow(String labelText, JPasswordField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        row.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(140, 25));
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        row.add(lbl);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(300, 35));

        field.setBounds(0, 0, 300, 35);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 35)
        ));
        layeredPane.add(field, JLayeredPane.DEFAULT_LAYER);

        JButton btnToggle = new JButton(iconHide);
        btnToggle.setBounds(265, 5, 25, 25);
        btnToggle.setBorderPainted(false);
        btnToggle.setContentAreaFilled(false);
        btnToggle.setFocusPainted(false);
        btnToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        layeredPane.add(btnToggle, JLayeredPane.PALETTE_LAYER);

        btnToggle.addActionListener(e -> {
            if (field.getEchoChar() == 0) {
                field.setEchoChar('*');
                btnToggle.setIcon(iconHide);
            } else {
                field.setEchoChar((char) 0);
                btnToggle.setIcon(iconShow);
            }
        });

        row.add(layeredPane);
        return row;
    }



    protected void do_btnChange_actionPerformed(ActionEvent e) {
        Staff staff = Session.currentStaff;
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "No staff is logged in!");
            return;
        }

        StaffModel staffModel = new StaffModel();

        String nowPassword = new String(jpasswordFieldNowPass.getPassword());
        String newPassword = new String(jpasswordFieldNewPass.getPassword());
        String confirmPassword = new String(jpasswordFieldConfirmPass.getPassword());

        if (nowPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all the necessary information");
            return;
        }

        if (!BCrypt.checkpw(nowPassword, staff.getPassword())) {
            JOptionPane.showMessageDialog(this, "The current password is incorrect");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New password and confirm password do not match");
            return;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        staff.setPassword(hashedPassword);

        if (staffModel.updatePassWarehouseManager(staff)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully");
            jpasswordFieldNowPass.setText("");
            jpasswordFieldNewPass.setText("");
            jpasswordFieldConfirmPass.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Password change failed");
        }
    }
}
