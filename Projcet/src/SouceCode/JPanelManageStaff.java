package SouceCode;

import entities.Staff;
import entities.Position;
import models.StaffModel;
import models.PositionModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.util.List;

public class JPanelManageStaff extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;
    private JButton btnAdd;

    private StaffModel staffModel = new StaffModel();
    private PositionModel positionModel = new PositionModel();

    // Màu đồng bộ với AddProduct
    private static final Color PRIMARY_COLOR = new Color(250, 172, 104);
    private static final Color PRIMARY_HOVER = new Color(255, 195, 140);

    public JPanelManageStaff() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // TITLE
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(247, 222, 155));
        JLabel lblTitle = new JLabel("MANAGE STAFF", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(new Color(255, 99, 71));
        titlePanel.add(lblTitle);
        add(titlePanel, BorderLayout.NORTH);

        // SEARCH + ADD BUTTON + TABLE
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Staff Search"));

        txtSearch = new JTextField(25);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);

        JButton btnClearSearch = new JButton("Clear");
        btnClearSearch.addActionListener(e -> txtSearch.setText(""));
        searchPanel.add(btnClearSearch);

        // Nút Add đẹp như Add Product
        btnAdd = new JButton("Add Staff");
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAdd.setBackground(PRIMARY_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setPreferredSize(new Dimension(140, 40));
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/resources/icon-add.png")));

        // Hover effect
        btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btnAdd.setBackground(PRIMARY_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btnAdd.setBackground(PRIMARY_COLOR);
            }
        });

        btnAdd.addActionListener(e -> openAddDialog());
        searchPanel.add(btnAdd);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // TABLE
        String[] columnNames = {"No", "Full Name", "Email", "Username", "Phone", "Gender", "Position", "Status", "ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(247, 222, 155));

        // Ẩn cột ID
        table.getColumnModel().getColumn(8).setMinWidth(0);
        table.getColumnModel().getColumn(8).setMaxWidth(0);
        table.getColumnModel().getColumn(8).setWidth(0);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Tìm kiếm realtime
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Click chọn dòng để sửa (khi chọn xong mới mở dialog)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int viewRow = table.getSelectedRow();
                int modelRow = table.convertRowIndexToModel(viewRow);
                String id = (String) tableModel.getValueAt(modelRow, 8);
                Staff staff = staffModel.findById(id);
                if (staff != null) {
                    // Chỉ mở dialog một lần khi chọn xong
                    openUpdateDialog(staff);
                    // Xóa chọn để tránh mở nhiều lần nếu người dùng click lại
                    table.clearSelection();
                }
            }
        });

        loadData();
    }

    // ==================== SỬA CHÍNH TẠI ĐÂY ====================
    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add New Staff", true);
        JPanelAddStaff panel = new JPanelAddStaff(null, this::loadData, dialog);

        dialog.setContentPane(panel);
        dialog.pack();                                   // <<< TỰ ĐỘNG TÍNH KÍCH THƯỚC ĐÚNG
        dialog.setMinimumSize(new Dimension(900, 650)); // Không cho thu nhỏ quá
        dialog.setLocationRelativeTo(this);              // Hiện giữa màn hình cha
        dialog.setVisible(true);
    }

    private void openUpdateDialog(Staff staff) {
        JDialog dialog = new JDialog((Frame) null, "Update Staff", true);
        JPanelAddStaff panel = new JPanelAddStaff(staff, this::loadData, dialog);

        dialog.setContentPane(panel);
        dialog.pack();                                   // <<< TỰ ĐỘNG TÍNH KÍCH THƯỚC ĐÚNG
        dialog.setMinimumSize(new Dimension(900, 650));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    // ==========================================================

    public void loadData() {
        tableModel.setRowCount(0);
        List<Staff> list = staffModel.findAll();
        int no = 1;

        // Lấy danh sách Position để hiển thị tên đúng
        java.util.Map<Integer, String> positionMap = new java.util.HashMap<>();
        for (Position p : positionModel.findAll()) {
            positionMap.put(p.getId(), p.getTitle());
        }

        for (Staff s : list) {
            String positionName = positionMap.getOrDefault(s.getIdPosition(), "Unknown");
            tableModel.addRow(new Object[]{
                    no++,
                    s.getFullName(),
                    s.getEmail(),
                    s.getUsername(),
                    s.getPhone(),
                    s.getGender() == 0 ? "Male" : "Female",
                    positionName,
                    s.getIsActive() == 1 ? "Active" : "Inactive",
                    s.getId()
            });
        }
    }
}