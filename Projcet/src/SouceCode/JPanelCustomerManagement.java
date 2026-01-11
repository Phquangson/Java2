package SouceCode;

import entities.Bill;
import entities.BillItem;
import entities.Customer;
import models.BillItemModel;
import models.BillModel;
import models.CustomerModel;
import models.ProductModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class JPanelCustomerManagement extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch, txtName, txtPhone, txtAddress, txtCity, txtDistrict;

    private CustomerModel customerModel = new CustomerModel();
    private BillModel billModel = new BillModel();
    private BillItemModel billItemModel = new BillItemModel();
    private ProductModel productModel = new ProductModel();

    private DecimalFormat currencyFormat = new DecimalFormat("#,##0 ₫");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public JPanelCustomerManagement() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // ===== PHẦN NORTH: TIÊU ĐỀ + SEARCH =====
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);

        // Tiêu đề giống hệt mẫu (nền vàng nhạt, chữ đen đậm)
        JLabel lblTitle = new JLabel("CUSTOMER MANAGEMENT");
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(247, 222, 155));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(Color.DARK_GRAY);
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10)); // Padding đẹp

        northPanel.add(lblTitle, BorderLayout.CENTER);

        // Panel search (nằm dưới tiêu đề)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);

        txtSearch = new JTextField(25);
        JButton btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(255, 99, 71));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSearch.addActionListener(e -> loadTable(customerModel.search(txtSearch.getText().trim())));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        northPanel.add(searchPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // ===== MAIN PANEL =====
        JPanel mainPanel = new JPanel(new BorderLayout(15, 10));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        // ===== LEFT: TABLE =====
        JPanel leftPanel = new JPanel(new BorderLayout(10, 5));
        leftPanel.setBackground(Color.WHITE);

        // Table
        model = new DefaultTableModel(
                new String[]{"ID", "Full Name", "Phone", "Address", "City", "District"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(247, 222, 155));
        table.setRowHeight(45);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // ẨN CỘT ID
        table.removeColumn(table.getColumnModel().getColumn(0));

        // Căn giữa cột Phone
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollTable = new JScrollPane(table);
        leftPanel.add(scrollTable, BorderLayout.CENTER);
        mainPanel.add(leftPanel, BorderLayout.CENTER);

        // ===== RIGHT: FORM + BUTTONS =====
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(380, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));

        txtName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtAddress = new JTextField(20);
        txtCity = new JTextField(20);
        txtDistrict = new JTextField(20);

        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.insets = new Insets(8, 10, 8, 5);
        gbcLabel.anchor = GridBagConstraints.WEST;

        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.insets = new Insets(8, 0, 8, 10);
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.weightx = 1.0;
        gbcField.gridwidth = GridBagConstraints.REMAINDER;

        int y = 0;
        addFormRow(formPanel, gbcLabel, gbcField, y++, "Full Name:", txtName);
        addFormRow(formPanel, gbcLabel, gbcField, y++, "Phone:", txtPhone);
        addFormRow(formPanel, gbcLabel, gbcField, y++, "Address:", txtAddress);
        addFormRow(formPanel, gbcLabel, gbcField, y++, "City:", txtCity);
        addFormRow(formPanel, gbcLabel, gbcField, y++, "District:", txtDistrict);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnAdd = new JButton("Add New");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        styleButton(btnAdd, new Color(50, 205, 50));
        styleButton(btnUpdate, new Color(255, 165, 0));
        styleButton(btnDelete, Color.RED);
        styleButton(btnClear, new Color(100, 149, 237));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        rightPanel.add(btnPanel, BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // ===== EVENTS =====
        btnSearch.addActionListener(e -> loadTable(customerModel.search(txtSearch.getText().trim())));
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());

        // Fill form khi chọn dòng
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        // RIGHT-CLICK MENU: View Bills
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewBillsItem = new JMenuItem("View Bills");
        viewBillsItem.setFont(new Font("SansSerif", Font.PLAIN, 14));
        viewBillsItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String customerId = (String) model.getValueAt(row, 0);
                String customerName = (String) model.getValueAt(row, 1);
                showCustomerBills(customerId, customerName);
            }
        });
        popupMenu.add(viewBillsItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        table.setRowSelectionInterval(row, row);
                    }
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        });

        loadTable(customerModel.findAll());
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 45));
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbcLabel, GridBagConstraints gbcField, int y, String label, JComponent field) {
        gbcLabel.gridy = y;
        panel.add(new JLabel(label), gbcLabel);
        gbcField.gridy = y;
        panel.add(field, gbcField);
    }

    private void loadTable(List<Customer> list) {
        model.setRowCount(0);
        for (Customer c : list) {
            model.addRow(new Object[]{
                    c.getId(),
                    c.getFullName(),
                    c.getPhone(),
                    c.getAddress(),
                    c.getCity(),
                    c.getDistrict()
            });
        }
    }

    private void showCustomerBills(String customerId, String customerName) {
        List<Bill> bills = billModel.findByCustomerId(customerId);

        if (bills.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer " + customerName + " has no bills yet!");
            return;
        }

        // Tạo dialog lớn chứa cả danh sách bill và chi tiết
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bills of " + customerName, true);
        dialog.setSize(1200, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Phần trên: Danh sách bill
        String[] columns = {"Bill Code", "Created Time", "Total Amount", "Status"};
        DefaultTableModel billTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Bill b : bills) {
            String status = b.getIdStatus() == 9 ? "Processing" : "Completed";
            billTableModel.addRow(new Object[]{
                    b.getCode(),
                    b.getCreatedDate() != null ? dateFormat.format(b.getCreatedDate()) : "N/A",
                    currencyFormat.format(b.getTotal()),
                    status
            });
        }

        JTable billTable = new JTable(billTableModel);
        billTable.setRowHeight(35);
        billTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {{
            setHorizontalAlignment(SwingConstants.RIGHT);
        }});

        JScrollPane billScroll = new JScrollPane(billTable);
        billScroll.setPreferredSize(new Dimension(1200, 300));

        // Phần dưới: Chi tiết bill (ban đầu trống)
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Bill Detail"));
        detailPanel.add(new JLabel("Select a bill to view details", SwingConstants.CENTER), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, billScroll, detailPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.4);

        dialog.add(splitPane, BorderLayout.CENTER);

        // Click vào bill → load chi tiết vào panel dưới
        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = billTable.getSelectedRow();
                if (row != -1) {
                    String billCode = (String) billTableModel.getValueAt(row, 0);
                    Bill selectedBill = billModel.findByCode(billCode);
                    if (selectedBill != null) {
                        detailPanel.removeAll();
                        detailPanel.add(new JPanelBillDetail(selectedBill.getId()), BorderLayout.CENTER);
                        detailPanel.revalidate();
                        detailPanel.repaint();
                    }
                }
            }
        });

        dialog.setVisible(true);
    }

    private void addCustomer() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter customer name!");
            return;
        }

        Customer c = new Customer();
        c.setId(UUID.randomUUID().toString());
        c.setFullName(txtName.getText().trim());
        c.setPhone(txtPhone.getText().trim());
        c.setAddress(txtAddress.getText().trim());
        c.setCity(txtCity.getText().trim());
        c.setDistrict(txtDistrict.getText().trim());
        c.setIdCreator(Session.currentStaff.getId());
        c.setIdUpdater(Session.currentStaff.getId());

        if (customerModel.create(c)) {
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            loadTable(customerModel.findAll());
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Add failed!");
        }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update!");
            return;
        }

        String customerId = (String) model.getValueAt(row, 0);
        Customer selected = customerModel.findById(customerId);
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Customer not found!");
            return;
        }

        String fullName = txtName.getText().trim();
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer name cannot be empty!");
            return;
        }

        selected.setFullName(fullName);
        selected.setPhone(txtPhone.getText().trim());
        selected.setAddress(txtAddress.getText().trim());
        selected.setCity(txtCity.getText().trim());
        selected.setDistrict(txtDistrict.getText().trim());
        selected.setIdUpdater(Session.currentStaff.getId());

        if (customerModel.update(selected)) {
            JOptionPane.showMessageDialog(this, "Update successful!");
            loadTable(customerModel.findAll());
        } else {
            JOptionPane.showMessageDialog(this, "Update failed!");
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete!");
            return;
        }

        String customerName = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete customer: " + customerName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String customerId = (String) model.getValueAt(row, 0);
            if (customerModel.delete(customerId)) {
                JOptionPane.showMessageDialog(this, "Delete successful!");
                loadTable(customerModel.findAll());
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed! (Customer may have bills)");
            }
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            clearForm();
            return;
        }

        txtName.setText((String) model.getValueAt(row, 1));
        txtPhone.setText((String) model.getValueAt(row, 2));
        txtAddress.setText((String) model.getValueAt(row, 3));
        txtCity.setText((String) model.getValueAt(row, 4));
        txtDistrict.setText((String) model.getValueAt(row, 5));
    }

    private void clearForm() {
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtCity.setText("");
        txtDistrict.setText("");
    }
}