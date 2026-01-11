package SouceCode;

import entities.Bill;

import entities.Customer;
import entities.Status;
import models.BillModel;
import models.CustomerModel;
import models.StatusModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

public class JPanelManagerBill extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    private BillModel billModel = new BillModel();
    private CustomerModel customerModel = new CustomerModel();
    private StatusModel statusModel = new StatusModel();

    private DecimalFormat currencyFormat = new DecimalFormat("#,##0 ₫"); // Định dạng tiền tệ

    public JPanelManagerBill() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== TOP: SEARCH =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        top.setBackground(Color.WHITE);
        txtSearch = new JTextField(30);
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(255, 99, 71));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSearch.addActionListener(e -> loadTable(billModel.findByKeyword(txtSearch.getText().trim())));
        top.add(new JLabel("Tìm theo mã bill:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(new String[]{
                "STT", "Bill Code", "Customer", "Total Amount", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(247, 222, 155));
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Căn phải cột "Tổng tiền"
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Cột Tổng tiền

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BOTTOM: BUTTONS =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        bottom.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Create blank bill");
        JButton btnViewDetail = new JButton("See details");
        JButton btnUpdateStatus = new JButton("Update status");
        JButton btnDelete = new JButton("Delete bill");

        styleButton(btnAdd, new Color(50, 205, 50));
        styleButton(btnViewDetail, new Color(255, 165, 0));
        styleButton(btnUpdateStatus, new Color(30, 144, 255));
        styleButton(btnDelete, Color.RED);

        bottom.add(btnAdd);
        bottom.add(btnViewDetail);
        bottom.add(btnUpdateStatus);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> createEmptyBill());
        btnViewDetail.addActionListener(e -> viewDetail());
        btnUpdateStatus.addActionListener(e -> updateStatus());
        btnDelete.addActionListener(e -> deleteBill());

        loadTable(billModel.findAll());
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 45));
    }

    private void loadTable(List<Bill> list) {
        tableModel.setRowCount(0);
        int stt = 1;
        for (Bill b : list) {
            // Lấy tên khách hàng
            Customer customer = customerModel.findById(b.getIdCustomer());
            String customerName = customer != null ? customer.getFullName() : "Retail customers";

            // Lấy tên status
            Status status = null;
			try {
				status = StatusModel.findById(b.getIdStatus());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String statusName = status != null ? status.getTitle() : "Not determined";

            // Định dạng tổng tiền
            String totalFormatted = "0 ₫";
            if (b.getTotal() != null) {
                totalFormatted = currencyFormat.format(b.getTotal());
            }

            tableModel.addRow(new Object[]{
                    stt++,
                    b.getCode(),
                    customerName,
                    totalFormatted,
                    statusName
            });
        }
    }

    private void createEmptyBill() {
        Bill b = new Bill();
        b.setId(UUID.randomUUID().toString());
        b.setCode("BILL-" + System.currentTimeMillis());
        b.setIdCreator(Session.currentStaff.getId());
        b.setIdUpdater(Session.currentStaff.getId());
        b.setIdStatus(11); // Thay bằng ID "New" thật trong tbl_status
        b.setIdCustomer("KHACH_LE_DEFAULT"); // ID khách lẻ đã tạo trước

        if (billModel.create(b)) {
            JOptionPane.showMessageDialog(this, "Blank bill created successfully!");
            loadTable(billModel.findAll());
        } else {
            JOptionPane.showMessageDialog(this, "Bill creation failed!");
        }
    }

    private void viewDetail() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to view the details!");
            return;
        }
        String billCode = (String) tableModel.getValueAt(row, 1);
        Bill selectedBill = billModel.findByCode(billCode);
        if (selectedBill == null) {
            JOptionPane.showMessageDialog(this, "No bill found!");
            return;
        }

        JFrame frame = new JFrame("Invoice details - " + billCode);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(this);
        frame.add(new JPanelBillDetail(selectedBill.getId()));
        frame.setVisible(true);
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to update its status!");
            return;
        }
        String billCode = (String) tableModel.getValueAt(row, 1);
        Bill selectedBill = billModel.findByCode(billCode);
        if (selectedBill == null) return;

        List<Status> statuses = statusModel.findAll();
        JComboBox<Status> cmbStatus = new JComboBox<>();
        for (Status s : statuses) {
            cmbStatus.addItem(s);
        }
        cmbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Status) {
                    setText(((Status) value).getTitle());
                }
                return this;
            }
        });

        int result = JOptionPane.showConfirmDialog(this, cmbStatus, "Update invoice status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Status selectedStatus = (Status) cmbStatus.getSelectedItem();
            selectedBill.setIdStatus(selectedStatus.getId());
            selectedBill.setIdUpdater(Session.currentStaff.getId());

            if (billModel.update(selectedBill)) {
                JOptionPane.showMessageDialog(this, "Status update successful!");
                loadTable(billModel.findAll());
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!");
            }
        }
    }

    private void deleteBill() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select the bill to delete!");
            return;
        }
        String billCode = (String) tableModel.getValueAt(row, 1);
        Bill selectedBill = billModel.findByCode(billCode);
        if (selectedBill == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the invoice? " + billCode + "?\n(Warning: If the bill includes products, it may not be possible to delete it)", 
                "Confirm deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (billModel.delete(selectedBill.getId())) {
                JOptionPane.showMessageDialog(this, "Invoice deleted successfully!");
                loadTable(billModel.findAll());
            } else {
                JOptionPane.showMessageDialog(this, "Clear failure! (The bill may contain products)");
            }
        }
    }
}