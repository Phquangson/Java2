package SouceCode;

import entities.Bill;
import entities.BillItem;
import entities.Product;
import entities.ReturnProduct;
import models.BillItemModel;
import models.BillModel;
import models.ProductModel;
import models.ReturnProductModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JPanelBillDetail extends JPanel {

    private String billId;
    private JTable table;
    private DefaultTableModel tableModel;

    private BillItemModel billItemModel = new BillItemModel();
    private ProductModel productModel = new ProductModel();
    private BillModel billModel = new BillModel();
    private ReturnProductModel returnProductModel = new ReturnProductModel();

    private DecimalFormat currencyFormat = new DecimalFormat("#,##0 ₫");

    public JPanelBillDetail(String billId) {
        this.billId = billId;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("RETURN GOODS - BILL ID: " + billId);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(255, 99, 71));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // ===== PRODUCT DETAIL TABLE IN BILL =====
        tableModel = new DefaultTableModel(new String[]{
                "Product Code", "Product Name", "Quantity", "Selling Price", "Amount"
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Quantity

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Selling Price
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Amount

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BOTTOM: ONLY KEEP "RETURN GOODS" BUTTON =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        bottom.setBackground(Color.WHITE);

        JButton btnReturn = new JButton("RETURN GOODS");
        btnReturn.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnReturn.setBackground(new Color(128, 0, 128)); // Purple highlight
        btnReturn.setForeground(Color.WHITE);
        btnReturn.setFocusPainted(false);
        btnReturn.setPreferredSize(new Dimension(300, 70));

        bottom.add(btnReturn);
        add(bottom, BorderLayout.SOUTH);

        btnReturn.addActionListener(e -> returnProduct());

        loadItems();
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        List<BillItem> items = billItemModel.findByBillId(billId);
        for (BillItem i : items) {
            Product p = productModel.findById(i.getIdProduct());
            String productCode = p != null ? p.getCode() : "N/A";
            String productName = p != null ? p.getTitle() : "Product deleted";

            tableModel.addRow(new Object[]{
                    productCode,
                    productName,
                    i.getQuantity(),
                    currencyFormat.format(i.getPrice()),
                    currencyFormat.format(i.getTotal())
            });
        }
    }

    // ===== RETURN GOODS FUNCTION =====
    private void returnProduct() {
        List<BillItem> items = billItemModel.findByBillId(billId);
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The bill has no products to return!");
            return;
        }

        JComboBox<String> cmbItem = new JComboBox<>();
        Map<String, BillItem> itemMap = new HashMap<>();

        for (BillItem item : items) {
            Product p = productModel.findById(item.getIdProduct());
            String productName = p != null ? p.getTitle() : "Product deleted";
            String display = productName + " - Current quantity: " + item.getQuantity() + " - Price: " + currencyFormat.format(item.getPrice());
            cmbItem.addItem(display);
            itemMap.put(display, item);
        }

        JTextField txtQuantity = new JTextField("1", 10);
        JTextArea txtReason = new JTextArea(4, 30);
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Returned product:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(cmbItem, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; panel.add(new JLabel("Return quantity:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(txtQuantity, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Return reason:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; panel.add(new JScrollPane(txtReason), gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Return Goods - Bill " + billId, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            String selectedDisplay = (String) cmbItem.getSelectedItem();
            if (selectedDisplay == null) {
                JOptionPane.showMessageDialog(this, "Please select the product to return!");
                return;
            }

            BillItem selectedBillItem = itemMap.get(selectedDisplay);
            int returnQty = Integer.parseInt(txtQuantity.getText().trim());
            String reason = txtReason.getText().trim();

            if (returnQty <= 0 || returnQty > selectedBillItem.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Invalid return quantity! (Maximum: " + selectedBillItem.getQuantity() + ")");
                return;
            }

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the return reason!");
                return;
            }

            // Save return record
            ReturnProduct returnProduct = new ReturnProduct();
            returnProduct.setQuantity(returnQty);
            returnProduct.setReason(reason);
            returnProduct.setIdProduct(selectedBillItem.getIdProduct());
            returnProduct.setIdBill(billId);
            returnProduct.setIdCreator(Session.currentStaff.getId());
            returnProduct.setIdUpdater(Session.currentStaff.getId());

            if (!returnProductModel.create(returnProduct)) {
                JOptionPane.showMessageDialog(this, "Failed to save return information!");
                return;
            }

            // Update quantity in bill
            int newQty = selectedBillItem.getQuantity() - returnQty;
            selectedBillItem.setQuantity(newQty);
            selectedBillItem.setTotal(selectedBillItem.getPrice().multiply(BigDecimal.valueOf(newQty)));

            // SỬA LỖI: THÊM ID UPDATER TRƯỚC KHI UPDATE BILL ITEM
            selectedBillItem.setIdUpdater(Session.currentStaff.getId());

            billItemModel.update(selectedBillItem);

            // Add back to stock
            Product p = productModel.findById(selectedBillItem.getIdProduct());
            if (p != null) {
                p.setQuantity(p.getQuantity() + returnQty);
                productModel.update(p);
            }

            // Update bill total
            updateBillTotal();

            JOptionPane.showMessageDialog(this, "Return successful!\nQuantity has been updated.");
            loadItems();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Return quantity must be a valid number!");
        }
    }

    private void updateBillTotal() {
        Bill bill = billModel.findById(billId);
        if (bill == null) return;

        BigDecimal total = BigDecimal.ZERO;
        int totalQuantity = 0;
        List<BillItem> items = billItemModel.findByBillId(billId);
        for (BillItem i : items) {
            totalQuantity += i.getQuantity();
            total = total.add(i.getTotal());
        }

        bill.setTotalQuantity(totalQuantity);
        bill.setTotal(total);
        bill.setIdUpdater(Session.currentStaff.getId()); // Đã có, giữ nguyên
        billModel.update(bill);
    }
}