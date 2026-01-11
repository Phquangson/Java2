package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import models.BillItemModel;
import models.BillModel;
import models.CustomerModel;
import models.ProductModel;
import models.ReturnProductModel;
import models.StaffModel;

import entities.Bill;
import entities.BillItem;
import entities.Customer;
import entities.Product;
import entities.ReturnProduct;
import entities.Staff;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JPanelBillDetails extends JPanel {
    private JLabel lblCustomerName, lblPhone, lblCashier;
    private JTable tableDetails;
    private DefaultTableModel tableModel;
    private JLabel lblCoupon, lblTotalQty, lblSubtotal, lblDiscount, lblVAT, lblTotal;
    private JButton btnReturn, btnClose;

    private BillModel billModel = new BillModel();
    private CustomerModel customerModel = new CustomerModel();
    private StaffModel staffModel = new StaffModel();
    private BillItemModel billItemModel = new BillItemModel();
    private ProductModel productModel = new ProductModel();
    private ReturnProductModel returnProductModel = new ReturnProductModel();
    private Runnable onReload;
    private NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    private String billId;
    public JPanelBillDetails(String orderId,Runnable onReload) {
    	this.onReload = onReload;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        
        JLabel lblTitle = new JLabel("Chi tiết hóa đơn: " + orderId);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(247, 222, 155));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

       
        JPanel infoBlock = new JPanel();
        infoBlock.setLayout(new BoxLayout(infoBlock, BoxLayout.Y_AXIS));
        infoBlock.setBackground(Color.WHITE);
        infoBlock.setBorder(new EmptyBorder(10, 10, 0, 10));

        
        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBackground(Color.WHITE);
        lblCustomerName = new JLabel("Khách hàng: ");
        lblCustomerName.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblCashier = new JLabel("Thu ngân: ");
        lblCashier.setFont(new Font("SansSerif", Font.PLAIN, 16));
        row1.add(lblCustomerName, BorderLayout.WEST);
        row1.add(lblCashier, BorderLayout.EAST);

        
        lblPhone = new JLabel("Phone: ");
        lblPhone.setFont(new Font("SansSerif", Font.PLAIN, 16));

        infoBlock.add(row1);
        infoBlock.add(lblPhone);


        tableModel = new DefaultTableModel(new String[]{
        		"Product Name", "Quantity", "Price", "Total"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tableDetails = new JTable(tableModel);
        tableDetails.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tableDetails.setRowHeight(28);

        // Khóa không cho resize và reorder
        tableDetails.getTableHeader().setResizingAllowed(false);
        tableDetails.getTableHeader().setReorderingAllowed(false);

        // Renderer căn phải cho cột tiền
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tableDetails.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tableDetails.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(tableDetails);

        // Panel trung gian CENTER: info + table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(infoBlock, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // ===== Panel tổng kết =====
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblCoupon   = new JLabel("Coupon: ");
        lblTotalQty = new JLabel("Total Quantity: ");
        lblSubtotal = new JLabel("Subtotal: ");
        lblDiscount = new JLabel("Discount: ");
        lblVAT      = new JLabel("VAT (8%): ");
        lblTotal    = new JLabel("TOTAL: ");

        Font f = new Font("SansSerif", Font.BOLD, 14);
        for (JLabel l : new JLabel[]{lblCoupon, lblTotalQty, lblSubtotal, lblDiscount, lblVAT, lblTotal}) {
            l.setFont(f);
        }

        summaryPanel.add(lblCoupon);
        summaryPanel.add(lblTotalQty);
        summaryPanel.add(lblSubtotal);
        summaryPanel.add(lblDiscount);
        summaryPanel.add(lblVAT);
        summaryPanel.add(lblTotal);

        // ===== Panel nút =====
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnReturn = new JButton("Return Goods");
        btnReturn.setBackground(new Color(220, 53, 69));
        btnReturn.setForeground(Color.WHITE);
        btnReturn.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnClose = new JButton("Đóng");
        btnClose.setBackground(new Color(46, 139, 87));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 14));

        buttonPanel.add(btnReturn, BorderLayout.WEST);
        buttonPanel.add(btnClose, BorderLayout.EAST);

        // Khối SOUTH: summary + button
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(summaryPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
        btnReturn.addActionListener(
        		e -> { returnProduct();
        if (onReload != null) onReload.run();
        });
        btnClose.addActionListener(
        		e -> { if (onReload != null) onReload.run(); 
        		Window w = SwingUtilities.getWindowAncestor(this); 
        		if (w != null) w.dispose(); });
        loadData(orderId);
    }

    private void loadData(String orderId) {
        Bill bill = billModel.findByCode(orderId);
        if (bill == null) return;
        this.billId = bill.getId();

        Customer c = customerModel.findById(bill.getIdCustomer());
        if (c != null) {
            lblCustomerName.setText("Khách hàng: " + c.getFullName());
            lblPhone.setText("SĐT: " + c.getPhone());
        }

        Staff emp = staffModel.findById(bill.getIdCreator());
        if (emp != null) {
            lblCashier.setText("Thu ngân: " + emp.getFullName());
        }

        List<BillItem> items = billItemModel.findByBillId(bill.getId());
        int totalQty = 0;
        double subtotal = 0;
        for (BillItem d : items) {
            double lineTotal = d.getQuantity() * d.getPrice().doubleValue();
            tableModel.addRow(new Object[]{
                d.getProductName(),
                d.getQuantity(),
                currencyVN.format(d.getPrice()),
                currencyVN.format(lineTotal)
            });
            totalQty += d.getQuantity();
            subtotal += lineTotal;
        }

        double discount = bill.getDiscount() != null ? bill.getDiscount().doubleValue() : 0;
        double vat = subtotal * 0.08;
        double total = subtotal - discount + vat;

        lblCoupon.setText("Coupon: " + (bill.getIdCoupon() != null ? bill.getIdCoupon() : ""));
        lblTotalQty.setText("Total Quantity: " + totalQty);
        lblSubtotal.setText("Subtotal: " + currencyVN.format(subtotal));
        lblDiscount.setText("Discount: " + currencyVN.format(discount));
        lblVAT.setText("VAT (8%): " + currencyVN.format(vat));
        lblTotal.setText("TOTAL: " + currencyVN.format(total));
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
                    currencyVN.format(i.getPrice()),
                    currencyVN.format(i.getTotal())
            });
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
    
    private void returnProduct() { 
    	if (billId == null) { 
    		JOptionPane.showMessageDialog(this, "Không tìm thấy ID hóa đơn!"); 
    		return; 
    	} 
    	List<BillItem> items = billItemModel.findByBillId(billId); 
    	if (items.isEmpty()) { 
    		JOptionPane.showMessageDialog(this, "Hóa đơn không có sản phẩm để trả!"); 
    		return; 
    		}
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The bill has no products to return!");
            return;
        }

        JComboBox<String> cmbItem = new JComboBox<>();
        Map<String, BillItem> itemMap = new HashMap<>();

        for (BillItem item : items) {
            Product p = productModel.findById(item.getIdProduct());
            String productName = p != null ? p.getTitle() : "Product deleted";
            String display = productName + " - Current quantity: " + item.getQuantity() + " - Price: " + currencyVN.format(item.getPrice());
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

            updateBillTotal();

            JOptionPane.showMessageDialog(this, "Return successful!\nQuantity has been updated.");
            loadItems();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Return quantity must be a valid number!");
        }
    	
    	}
}
