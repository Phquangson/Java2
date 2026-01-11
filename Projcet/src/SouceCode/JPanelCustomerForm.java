package SouceCode;

import entities.Customer;
import models.CustomerModel;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class JPanelCustomerForm extends JPanel {

    private JTextField txtName, txtPhone, txtEmail;
    private Customer customer; // Có thể null khi thêm mới
    private CustomerModel model = new CustomerModel();
    private boolean isEditMode; // Phân biệt thêm mới hay sửa

    public JPanelCustomerForm(Customer c) {
        this.customer = c;
        this.isEditMode = (c != null); // Nếu truyền customer có dữ liệu → chế độ sửa

        setLayout(new GridLayout(0, 2, 10, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);

        add(new JLabel("Họ tên:"));
        add(txtName);
        add(new JLabel("Số điện thoại:"));
        add(txtPhone);
        add(new JLabel("Email:"));
        add(txtEmail);

        JButton btnSave = new JButton(isEditMode ? "Cập nhật" : "Thêm mới");
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSave.setBackground(new Color(50, 205, 50));
        btnSave.setForeground(Color.WHITE);

        // Để nút Save chiếm 2 cột
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(btnSave);
        add(new JLabel()); // Ô trống
        add(btnPanel);

        // Nếu là sửa → điền dữ liệu cũ
        if (isEditMode && customer != null) {
            txtName.setText(customer.getFullName());
            txtPhone.setText(customer.getPhone());
            // txtEmail nếu có
        }

        btnSave.addActionListener(e -> save());
    }

    private void save() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên khách hàng!");
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
            return;
        }

        // Kiểm tra định dạng số điện thoại (10 số, bắt đầu bằng 0)
        if (!phone.matches("^0[0-9]{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!");
            return;
        }

        if (isEditMode && customer != null) {
            // CHẾ ĐỘ SỬA
            customer.setFullName(name);
            customer.setPhone(phone);
            customer.setIdUpdater(Session.currentStaff.getId()); // Nếu có

            if (model.update(customer)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                return;
            }
        } else {
            // CHẾ ĐỘ THÊM MỚI
            Customer newCustomer = new Customer();
            newCustomer.setId(UUID.randomUUID().toString()); // Nếu ID là String UUID
            newCustomer.setFullName(name);
            newCustomer.setPhone(phone);
            newCustomer.setIdCreator(Session.currentStaff.getId());
            newCustomer.setIdUpdater(Session.currentStaff.getId());

            if (model.create(newCustomer)) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng mới thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại!");
                return;
            }
        }

        // Đóng form sau khi lưu thành công
        SwingUtilities.getWindowAncestor(this).dispose();
    }
}