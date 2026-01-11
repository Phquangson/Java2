package SouceCode;

import entities.Bill;
import models.BillModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JPanelSalesReport extends JPanel {

    private JDateChooser dateFrom, dateTo;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnSearch;

    private JLabel lblTotalBills, lblRevenue, lblAvg;

    private BillModel billModel = new BillModel();

    public JPanelSalesReport() {
    setLayout(new BorderLayout(10, 10));
    setBackground(Color.WHITE);
    setBorder(new EmptyBorder(15, 15, 15, 15));

    // ===== Panel chứa tiêu đề + filter =====
    JPanel northPanel = new JPanel(new BorderLayout());
    northPanel.setBackground(Color.WHITE);

    JLabel lblTitle = new JLabel("Sales Report");
    lblTitle.setOpaque(true);
    lblTitle.setBackground(new Color(247, 222, 155));
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
    lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
    lblTitle.setForeground(Color.DARK_GRAY);
    lblTitle.setBorder(new EmptyBorder(12, 12, 12, 12));
    northPanel.add(lblTitle, BorderLayout.NORTH);
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
    filterPanel.setBackground(Color.WHITE);

    filterPanel.add(new JLabel("From the:"));
    dateFrom = new JDateChooser();
    dateFrom.setDateFormatString("dd/MM/yyyy");
    dateFrom.setPreferredSize(new Dimension(150, 28));
    filterPanel.add(dateFrom);

    filterPanel.add(new JLabel("To the:"));
    dateTo = new JDateChooser();
    dateTo.setDateFormatString("dd/MM/yyyy");
    dateTo.setPreferredSize(new Dimension(150, 28));
    filterPanel.add(dateTo);

    btnSearch = new JButton("Search");
    btnSearch.setBackground(new Color(46, 139, 87));
    btnSearch.setForeground(Color.WHITE);
    btnSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
    btnSearch.setFocusPainted(false);
    btnSearch.setPreferredSize(new Dimension(100, 30));
    btnSearch.addActionListener(e -> loadReport());
    filterPanel.add(btnSearch);

    northPanel.add(filterPanel, BorderLayout.SOUTH);
    add(northPanel, BorderLayout.NORTH);

    // ===== Table =====
    tableModel = new DefaultTableModel(new String[]{
        "Date Range", "Store", "Total Bills", "Revenue"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // khóa toàn bộ bảng
        }
    };

    table = new JTable(tableModel);
    table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
    table.setRowHeight(28);
    table.setSelectionBackground(new Color(200, 230, 255));

    // Renderer highlight dòng TOTAL + chữ đen khi chọn
    DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (row == table.getRowCount() - 1) { // dòng TOTAL
                c.setBackground(new Color(247, 222, 155));
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                c.setForeground(Color.BLACK);
            } else {
                if (isSelected) {
                    c.setBackground(new Color(200, 230, 255));
                    c.setForeground(Color.BLACK);             
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
            return c;
        }
    };

    // Áp dụng cho tất cả cột
    for (int i = 0; i < table.getColumnCount(); i++) {
        table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
    }

    // Khóa không cho chỉnh cột
    JTableHeader header = table.getTableHeader();
    header.setReorderingAllowed(false);
    TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
        columnModel.getColumn(i).setResizable(false);
    }

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    add(scrollPane, BorderLayout.CENTER);

    // ===== Summary Panel =====
    JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
    summaryPanel.setBackground(new Color(245, 245, 245));

    lblTotalBills = new JLabel("Total Bills: 0");
    lblTotalBills.setFont(new Font("SansSerif", Font.BOLD, 14));

    lblRevenue = new JLabel("Revenue: 0 ₫");
    lblRevenue.setFont(new Font("SansSerif", Font.BOLD, 14));

    lblAvg = new JLabel("Avg per Bill: 0 ₫");
    lblAvg.setFont(new Font("SansSerif", Font.BOLD, 14));

    summaryPanel.add(lblTotalBills);
    summaryPanel.add(lblRevenue);
    summaryPanel.add(lblAvg);

    add(summaryPanel, BorderLayout.SOUTH);
}


    private void loadReport() {
    Date from = dateFrom.getDate();
    Date to = dateTo.getDate();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    DecimalFormat df = new DecimalFormat("#,###");

    if (from == null || to == null) {
        JOptionPane.showMessageDialog(this,
            "Please select both the start and end dates.",
            "Missing date information",
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
    List<Bill> bills = billModel.findAll();

    int grandTotalBills = 0;
    BigDecimal grandRevenue = BigDecimal.ZERO;

    // Lặp qua từng ngày
    Calendar cal = Calendar.getInstance();
    cal.setTime(from);

    while (!cal.getTime().after(to)) {
        Date currentDay = cal.getTime();

        int totalBills = 0;
        BigDecimal revenue = BigDecimal.ZERO;

        for (Bill b : bills) {
            Date billDate = b.getCreatedDate();
            if (billDate != null) {
                // So sánh theo ngày (bỏ giờ phút giây)
                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
                if (dayFormat.format(billDate).equals(dayFormat.format(currentDay))) {
                    totalBills++;
                    revenue = revenue.add(b.getTotal());
                }
            }
        }

        // Cộng dồn vào tổng
        grandTotalBills += totalBills;
        grandRevenue = grandRevenue.add(revenue);

        // Thêm dòng cho ngày hiện tại
        tableModel.addRow(new Object[]{
            sdf.format(currentDay),
            "Store",
            String.valueOf(totalBills),
            df.format(revenue) + " ₫"
        });

        // sang ngày tiếp theo
        cal.add(Calendar.DATE, 1);
    }

    // Thêm dòng tổng cộng cuối cùng
    tableModel.addRow(new Object[]{
        "TOTAL (" + sdf.format(from) + " - " + sdf.format(to) + ")",
        "Store",
        String.valueOf(grandTotalBills),
        df.format(grandRevenue) + " ₫"
    });

    // Cập nhật summary panel
    lblTotalBills.setText("Total Bills: " + grandTotalBills);
    lblRevenue.setText("Revenue: " + df.format(grandRevenue) + " ₫");
    BigDecimal avg = grandTotalBills > 0 ? 
        grandRevenue.divide(BigDecimal.valueOf(grandTotalBills), 0, RoundingMode.HALF_UP) 
        : BigDecimal.ZERO;
    lblAvg.setText("Avg per Bill: " + df.format(avg) + " ₫");
}

}
