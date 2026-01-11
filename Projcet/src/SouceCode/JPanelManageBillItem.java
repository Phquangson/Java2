package SouceCode;

import entities.Bill;
import entities.BillItem;
import entities.Product;
import models.BillItemModel;
import models.BillModel;
import models.ProductModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class JPanelManageBillItem extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    private BillItemModel billItemModel = new BillItemModel();
    private ProductModel productModel = new ProductModel();
    private BillModel billModel = new BillModel();

    private DecimalFormat currencyFormat = new DecimalFormat("#,##0 ₫");

    public JPanelManageBillItem() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== TOP: SEARCH BY BILL ID =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        top.setBackground(Color.WHITE);
        txtSearch = new JTextField(25);
        JButton btnSearch = new JButton("Tìm kiếm theo Bill ID");
        btnSearch.setBackground(new Color(255, 99, 71));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSearch.addActionListener(e -> {
            String billId = txtSearch.getText().trim();
            if (billId.isEmpty()) {
                loadTable(billItemModel.findAll());
            } else {
                loadTable(billItemModel.findByBillId(billId));
            }
        });
        top.add(new JLabel("Bill ID:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        // ===== TABLE - HIỂN THỊ CHI TIẾT BILL ITEM =====
        tableModel = new DefaultTableModel(new String[]{
                "Item ID", "Tên sản phẩm", "Số lượng", "Giá bán", "Thành tiền"
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

        // Căn giữa số lượng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Căn phải giá và thành tiền
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BOTTOM: CHỈ GIỮ NÚT "TRẢ HÀNG" (RETURN GOODS) =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottom.setBackground(Color.WHITE);

        JButton btnReturnGoods = new JButton("Trả hàng");
        btnReturnGoods.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnReturnGoods.setBackground(new Color(30, 144, 255));
        btnReturnGoods.setForeground(Color.WHITE);
        btnReturnGoods.setFocusPainted(false);
        btnReturnGoods.setPreferredSize(new Dimension(250, 60));

        bottom.add(btnReturnGoods);
        add(bottom, BorderLayout.SOUTH);

        // Nút "Trả hàng" – mở chức năng return goods
        btnReturnGoods.addActionListener(e -> returnGoods());

        loadTable(billItemModel.findAll());
    }

    private void loadTable(List<BillItem> list) {
        tableModel.setRowCount(0);
        for (BillItem i : list) {
            Product p = productModel.findById(i.getIdProduct());
            String productName = p != null ? p.getTitle() : "Sản phẩm đã xóa";

            tableModel.addRow(new Object[]{
                    i.getId(),
                    productName,
                    i.getQuantity(),
                    currencyFormat.format(i.getPrice()),
                    currencyFormat.format(i.getTotal())
            });
        }
    }

    // Chức năng trả hàng (return goods)
    private void returnGoods() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần trả hàng!");
            return;
        }

        int itemId = (Integer) tableModel.getValueAt(row, 0);
        int currentQuantity = (Integer) tableModel.getValueAt(row, 2);
        String productName = (String) tableModel.getValueAt(row, 1);

        String input = JOptionPane.showInputDialog(this, 
                "Nhập số lượng trả hàng cho sản phẩm:\n" + productName,
                currentQuantity);
        if (input == null) return;

        try {
            int returnQty = Integer.parseInt(input.trim());
            if (returnQty <= 0 || returnQty > currentQuantity) {
                JOptionPane.showMessageDialog(this, "Số lượng trả phải lớn hơn 0 và không vượt quá số lượng đã mua!");
                return;
            }

            BillItem item = billItemModel.findById(itemId);
            if (item == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy chi tiết hóa đơn!");
                return;
            }

            // Cập nhật số lượng trong bill item
            int newQty = currentQuantity - returnQty;
            item.setQuantity(newQty);
            item.setTotal(item.getPrice().multiply(BigDecimal.valueOf(newQty)));
            item.setIdUpdater(Session.currentStaff.getId());

            if (newQty == 0) {
                // Nếu trả hết → xóa item
                if (billItemModel.delete(itemId)) {
                    // Cộng lại tồn kho
                    Product p = productModel.findById(item.getIdProduct());
                    if (p != null) {
                        p.setQuantity(p.getQuantity() + returnQty);
                        productModel.update(p);
                    }
                    updateBillTotal(item.getIdBill());
                    JOptionPane.showMessageDialog(this, "Trả hàng thành công! Sản phẩm đã được xóa khỏi hóa đơn.");
                } else {
                    JOptionPane.showMessageDialog(this, "Trả hàng thất bại!");
                    return;
                }
            } else {
                // Nếu trả một phần → cập nhật item
                if (billItemModel.update(item)) {
                    // Cộng lại tồn kho
                    Product p = productModel.findById(item.getIdProduct());
                    if (p != null) {
                        p.setQuantity(p.getQuantity() + returnQty);
                        productModel.update(p);
                    }
                    updateBillTotal(item.getIdBill());
                    JOptionPane.showMessageDialog(this, "Trả hàng thành công! Số lượng đã được cập nhật.");
                } else {
                    JOptionPane.showMessageDialog(this, "Trả hàng thất bại!");
                    return;
                }
            }

            // Refresh bảng theo bill hiện tại
            loadTable(billItemModel.findByBillId(item.getIdBill()));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng trả phải là số hợp lệ!");
        }
    }

    private void updateBillTotal(String billId) {
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
        bill.setIdUpdater(Session.currentStaff.getId());
        billModel.update(bill);
    }
}