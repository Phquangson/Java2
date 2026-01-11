package SouceCode;

import java.awt.*;
import javax.swing.*;

import models.CategoryModel;
import models.InventoryModel;
import models.StaffModel;


public class JPanelDashboardStaffHome extends JPanel {
	private JLabel lblStaff, lblProduct, lblCategory, lblCoupon;
    public JPanelDashboardStaffHome() {
    	
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("WELCOME TO STAFF MANAGEMENT SYSTEM");
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblWelcome.setForeground(new Color(255, 99, 71));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblUser = new JLabel("Logged in as: " + Session.currentStaff.getFullName() + " (" + Session.currentStaff.getUsername() + ")");
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel(new GridLayout(2, 1, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.add(lblWelcome);
        panel.add(lblUser);

        add(panel, BorderLayout.CENTER);
        
        /////////////////////
        JLabel lblTitle = new JLabel("ADMIN DASHBOARD", JLabel.CENTER);
        lblTitle.setBounds(0, 30, 1000, 40);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(255, 112, 67));
        add(lblTitle);

        lblStaff = createCard("Total Staff", 150);
        lblProduct = createCard("Total Product", 350);
        lblCategory = createCard("Total Category", 550);
        lblCoupon = createCard("Total Coupon", 750);

        loadDashboardData();
    }
    
    public JLabel createCard(String title, int x) {
        JPanel panel = new JPanel(null);
        panel.setBounds(x, 120, 170, 120);
        panel.setBackground(new Color(255, 224, 178));
        panel.setBorder(BorderFactory.createLineBorder(
                new Color(255, 112, 67), 2));
        add(panel);

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setBounds(0, 15, 170, 25);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblTitle);

        JLabel lblValue = new JLabel("0", JLabel.CENTER);
        lblValue.setBounds(0, 50, 170, 40);
        lblValue.setFont(new Font("Arial", Font.BOLD, 26));
        lblValue.setForeground(new Color(255, 112, 67));
        panel.add(lblValue);

        return lblValue;
    }
    
    public void loadDashboardData() {
        try {
            StaffModel staffModel = new StaffModel();
            InventoryModel productModel = new InventoryModel();
            CategoryModel categoryModel = new CategoryModel();

            Integer staffCount = staffModel.countStaff();
            Integer productCount = productModel.countProduct();
            char[] categoryCount = categoryModel.countCategory();
            Integer couponCount = productModel.countCoupon();

            lblStaff.setText(staffCount != null ? staffCount.toString() : "0");
            lblProduct.setText(productCount != null ? productCount.toString() : "0");
            lblCategory.setText(categoryCount != null ? categoryCount.toString() : "0");
            lblCoupon.setText(couponCount != null ? couponCount.toString() : "0");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
    
