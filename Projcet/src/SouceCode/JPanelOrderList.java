package SouceCode;

import entities.Bill;
import entities.ReturnProduct;
import models.BillModel;
import models.ReturnProductModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;

import SouceCode.JPanelNotification.ButtonEditor;
import SouceCode.JPanelNotification.ButtonRenderer;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class JPanelOrderList extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private BillModel billModel = new BillModel();
    private ReturnProductModel returnModel = new ReturnProductModel();
    private JDateChooser dateFrom, dateTo;
    private JComboBox<String> cbStatus;
    private JLabel lblTotalOrders, lblTotalAmount;
    private JTextField txtSearch;

    public JPanelOrderList() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ===== Panel chứa tiêu đề + filter =====
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Order List");
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(247, 222, 155));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.DARK_GRAY);
        lblTitle.setBorder(new EmptyBorder(12, 12, 12, 12));
        northPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("From:"));
        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setPreferredSize(new Dimension(150, 28));
        filterPanel.add(dateFrom);

        filterPanel.add(new JLabel("To:"));
        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setPreferredSize(new Dimension(150, 28));
        filterPanel.add(dateTo);

        filterPanel.add(new JLabel("Order Type:"));
        cbStatus = new JComboBox<>(new String[]{"All", "Sales Order", "Return Order"});
        filterPanel.add(cbStatus);
        
        filterPanel.add(new JLabel("Search:")); 
        txtSearch = new JTextField(15); 
        filterPanel.add(txtSearch);

        JButton btnFilter = new JButton("Search");
        btnFilter.setBackground(new Color(46, 139, 87));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnFilter.setFocusPainted(false);
        btnFilter.setPreferredSize(new Dimension(100, 30));
        btnFilter.addActionListener(e -> loadOrders());
        filterPanel.add(btnFilter);

        northPanel.add(filterPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // ===== Bảng =====
        tableModel = new DefaultTableModel(new String[]{
            "Order ID", "Created Date", "Amount", "Type", "OriginalBill", "Action"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // chỉ cột Action cho phép click
            }
        };

        table = new JTable(tableModel);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(200, 230, 255));
        table.getTableHeader().setResizingAllowed(false); 
        table.getTableHeader().setReorderingAllowed(false);

        
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(200, 230, 255));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }

        // Renderer riêng cho cột Amount (căn phải)
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (isSelected) {
                    c.setBackground(new Color(200, 230, 255));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);

        // Renderer + Editor cho cột Action
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        // ===== Summary Panel =====
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        summaryPanel.setBackground(new Color(245, 245, 245));

        lblTotalOrders = new JLabel("Total Orders: 0");
        lblTotalOrders.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblTotalAmount = new JLabel("Total Amount: 0 ₫");
        lblTotalAmount.setFont(new Font("SansSerif", Font.BOLD, 14));

        summaryPanel.add(lblTotalOrders);
        summaryPanel.add(lblTotalAmount);

        add(summaryPanel, BorderLayout.SOUTH);
    }

  
    private void loadOrders() {
    Date from = dateFrom.getDate();
    Date to = dateTo.getDate();
    String keyword = txtSearch.getText().trim().toLowerCase();

    if (from == null || to == null) {
        JOptionPane.showMessageDialog(this,
            "Please select the full start and end dates.",
            "Date information is missing",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (from.after(to)) {
        JOptionPane.showMessageDialog(this,
            "The start date must not be later than the end date.",
            "Invalid date range",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    tableModel.setRowCount(0);
    String statusFilter = (String) cbStatus.getSelectedItem();

    
    List<OrderRecord> records = new ArrayList<>();

    // ===== Sales Order =====
    if ("All".equals(statusFilter) || "Sales Order".equals(statusFilter)) {
        List<Bill> bills = billModel.findByFilter(from, to, statusFilter);
        for (Bill b : bills) {
            if (b.getIdStatus() == 9) {
                if (keyword.isEmpty() || b.getCode().toLowerCase().contains(keyword)) {
                    records.add(new OrderRecord(
                        b.getCode(),
                        b.getCreatedDate(),
                        b.getTotal(),
                        "Sales Order",
                        "" // không có bill gốc
                    ));
                }
            }
        }
    }

    if ("All".equals(statusFilter) || "Return Order".equals(statusFilter)) {
        List<ReturnProduct> returns = returnModel.findByFilter(from, to);
        for (ReturnProduct r : returns) {
            if (keyword.isEmpty() || String.valueOf(r.getId()).toLowerCase().contains(keyword)) {
                records.add(new OrderRecord(
                    r.getId(),
                    r.getCreatedDate(),
                    r.getRefundAmount(),
                    "Return Order",
                    r.getBillCode()
                ));
            }
        }
    }


    // ===== Sort by newest date =====
    records.sort(Comparator.comparing(OrderRecord::getCreatedDate).reversed());

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    int totalOrders = 0;
    java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;

    for (OrderRecord rec : records) {
        totalOrders++;
        if (rec.getAmount() != null) {
            totalAmount = totalAmount.add(rec.getAmount());
        }
        tableModel.addRow(new Object[]{
            rec.getId(),
            rec.getCreatedDate() != null ? sdf.format(rec.getCreatedDate()) : "",
            rec.getAmount() != null ? rec.getAmount() + " ₫" : "",
            rec.getType(),
            rec.getOriginalBillCode(),
            "View"
        });
    }

    lblTotalOrders.setText("Total Orders: " + totalOrders);
    lblTotalAmount.setText("Total Amount: " + totalAmount + " ₫");
}

 // ===== Inner class OrderRecord =====
    // ===== Inner class OrderRecord =====
private static class OrderRecord {
    private Object id;
    private Date createdDate;
    private java.math.BigDecimal amount;
    private String type;
    private String originalBillCode; 

    public OrderRecord(Object id, Date createdDate, java.math.BigDecimal amount, String type, String originalBillCode) {
        this.id = id;
        this.createdDate = createdDate;
        this.amount = amount;
        this.type = type;
        this.originalBillCode = originalBillCode;
    }

    public Object getId() { return id; }
    public Date getCreatedDate() { return createdDate; }
    public java.math.BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
    public String getOriginalBillCode() { return originalBillCode; }
}



    

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "View" : value.toString());
            return this;
        }
    }

    // Editor xử lý click nút
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String orderId;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            orderId = table.getValueAt(row, 0).toString(); // lấy mã đơn từ cột 0
            button.setText("View");
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
            	JPanelBillDetails detailPanel = new JPanelBillDetails(orderId, () -> {
            	    loadOrders();
            	});


                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(button), "Order Detail", true);
                dialog.getContentPane().add(detailPanel);
                dialog.pack();
                dialog.setLocationRelativeTo(button);
                dialog.setVisible(true);
            }
            clicked = false;
            return "View";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }


}

