package SouceCode;

import entities.Bill;
import entities.BillItem;
import entities.ReturnProduct;
import models.BillItemModel;
import models.BillModel;
import models.ReturnProductModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class JPanelOrderDetails extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private BillModel billModel = new BillModel();
    private ReturnProductModel returnModel = new ReturnProductModel();

    private JDateChooser dateFrom, dateTo;
    private JComboBox<String> cbType;
    private JTextField txtOriginalId, txtKeyword;
    private JLabel lblPaging;

    private int currentPage = 1;
    private int pageSize = 10;
    private List<OrderRecord> allRecords = new ArrayList<>();

    public JPanelOrderDetails() {
    	setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Order Details");
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(247, 222, 155));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.DARK_GRAY);
        lblTitle.setBorder(new EmptyBorder(12, 12, 12, 12));
        
//        northPanel.add(lblTitle, BorderLayout.NORTH);

        
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filterPanel.setPreferredSize(new Dimension(0, 100)); // cao hơn

        // ===== Dòng 1: Loại đơn + Số đơn gốc + Từ khóa =====
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row1.setBackground(Color.WHITE);

        row1.add(new JLabel("Order Type:"));
        cbType = new JComboBox<>(new String[]{"All", "Sales Order", "Return Order"});
        row1.add(cbType);

        row1.add(new JLabel("Original Order #:"));
        txtOriginalId = new JTextField(10);
        row1.add(txtOriginalId);

        row1.add(new JLabel("Keyword:"));
        txtKeyword = new JTextField(15);
        row1.add(txtKeyword);

        filterPanel.add(row1);

        // ===== Dòng 2: Từ ngày - đến ngày + nút Search =====
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row2.setBackground(Color.WHITE);

        row2.add(new JLabel("From:"));
        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setPreferredSize(new Dimension(120, 28));
        row2.add(dateFrom);

        row2.add(new JLabel("To:"));
        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setPreferredSize(new Dimension(120, 28));
        row2.add(dateTo);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(46, 139, 87));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 30));
        btnSearch.addActionListener(e -> loadOrders());
        filterPanel.add(btnSearch);
        btnSearch.addActionListener(e -> loadOrders());
        row2.add(btnSearch);
        filterPanel.add(row2);
        add(filterPanel, BorderLayout.NORTH);

        northPanel.add(lblTitle, BorderLayout.NORTH); 
        northPanel.add(filterPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // ===== Bảng =====
        tableModel = new DefaultTableModel(new String[]{
            "ON", "ID Order", "Creatinon Date", "Time", "Original Order Number", "Order Type", "Product", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setRowHeight(28);
        table.getTableHeader().setResizingAllowed(false); 
        table.getTableHeader().setReorderingAllowed(false);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Phân trang =====
        lblPaging = new JLabel("Displays 0 to 0 of a total of 0 orders.");
        JPanel pagingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pagingPanel.add(lblPaging);

        JButton btnPrev = new JButton("<");
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                showPage();
            }
        });
        pagingPanel.add(btnPrev);

        JButton btnNext = new JButton(">");
        btnNext.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) allRecords.size() / pageSize);
            if (currentPage < totalPages) {
                currentPage++;
                showPage();
            }
        });
        pagingPanel.add(btnNext);

        add(pagingPanel, BorderLayout.SOUTH);
    }

    // ===== Load dữ liệu =====
   private void loadOrders() {
    allRecords.clear();

    Date from = dateFrom.getDate();
    Date to = dateTo.getDate();
    String typeFilter = (String) cbType.getSelectedItem();
    String originalIdFilter = txtOriginalId.getText().trim();
    String keywordFilter = txtKeyword.getText().trim();

    if (from == null || to == null) {
        JOptionPane.showMessageDialog(this, "Please select both the start and end dates.!");
        return;
    }


    if ("All".equals(typeFilter) || "Sales Order".equals(typeFilter)) {
        List<BillItem> items = BillItemModel.findAll();
        for (BillItem item : items) {
            String idBill = item.getIdBill();
            Bill bill = billModel.findById(idBill);
            String billCode = (bill != null) ? bill.getCode() : "";

            if ((originalIdFilter.isEmpty() || idBill.contains(originalIdFilter)) &&
                (keywordFilter.isEmpty() || item.getProductName().contains(keywordFilter))) {
                String statusLabel = OrderStatus.getLabelByCode(item.getStatus());

                allRecords.add(new OrderRecord(
                	billCode,     
                    item.getCreatedDate(),
                    null,                
                    "Sales Order",
                    item.getProductName(),
                    statusLabel
                ));
            }
        }
    }


    // ===== Return Orders =====
    if ("All".equals(typeFilter) || "Return Order".equals(typeFilter)) {
        List<ReturnProduct> returns = returnModel.findByFilter(from, to);
        for (ReturnProduct r : returns) {
        	
        	String idBill = r.getIdBill();
            Bill bill = billModel.findById(idBill);
            String billCode = (bill != null) ? bill.getCode() : "";

            if ((originalIdFilter.isEmpty() || idBill.contains(originalIdFilter)) &&
                (keywordFilter.isEmpty() || r.getProductName().contains(keywordFilter))) {
                allRecords.add(new OrderRecord(
                    r.getId(),             // ID Order (Return)
                    r.getCreatedDate(),
                    billCode,              // Original Order Number (Sales Order code)
                    "Return Order",
                    r.getProductName(),
                    "Returned"
                ));
            }
        }
    }

    // Sắp xếp mới nhất lên đầu
    allRecords.sort(Comparator.comparing(OrderRecord::getCreatedDate).reversed());

    currentPage = 1;
    showPage();
}



   public enum OrderStatus {
	    INSTOCK(6, "Instock"),
	    SOLDOUT(7, "SoldOut"),
	    LOWINSTOCK(8, "LowInStock"),
	    PAID(9, "Paid"),
	    PENDING(10, "Pending");

	    private final int code;
	    private final String label;

	    OrderStatus(int code, String label) {
	        this.code = code;
	        this.label = label;
	    }

	    public int getCode() { return code; }
	    public String getLabel() { return label; }

	    public static String getLabelByCode(int code) {
	        for (OrderStatus s : values()) {
	            if (s.code == code) return s.label;
	        }
	        return "Unknown";
	    }
	}

    // ===== Hiển thị theo trang =====
    private void showPage() {
        tableModel.setRowCount(0);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, allRecords.size());

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        for (int i = start; i < end; i++) {
            OrderRecord rec = allRecords.get(i);
            tableModel.addRow(new Object[]{
                i + 1,
                rec.getId(),
                sdfDate.format(rec.getCreatedDate()),
                sdfTime.format(rec.getCreatedDate()),
                rec.getOriginalId() != null ? rec.getOriginalId() : "",
                rec.getType(),
                rec.getProductName(),
                rec.getStatus()
            });
        }

        lblPaging.setText("Display " + (start + 1) + " to " + end + "of total:  " + allRecords.size() + " Order Sale");
    }

    // ===== Inner class OrderRecord =====
    private static class OrderRecord {
        private Object id;
        private Date createdDate;
        private String originalId; // số đơn hàng gốc
        private String type;
        private String status;
        private String productName; // tên sản phẩm

        public OrderRecord(Object id, Date createdDate, String originalId, String type, String productName,String status) {
            this.id = id;
            this.createdDate = createdDate;
            this.originalId = originalId;	
            this.type = type;
            this.productName = productName;
            this.status = status;
        }

        public String getStatus() { return status; } 
        public void setStatus(String status) { this.status = status; }
        public Object getId() { return id; }
        public Date getCreatedDate() { return createdDate; }
        public String getOriginalId() { return originalId; }
        public String getType() { return type; }
        public String getProductName() { return productName; }
    }
}
