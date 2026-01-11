package SouceCode;

import entities.Bill;
import entities.Customer;
import entities.Staff;
import entities.Status;
import models.BillModel;
import models.CustomerModel;
import models.StaffModel;
import models.StatusModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class JPanelManageBill extends JPanel {
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField txtSearch;
	private BillModel billModel = new BillModel();
	private CustomerModel customerModel = new CustomerModel();
	private StatusModel statusModel = new StatusModel();
	private StaffModel staffModel = new StaffModel();
	private DecimalFormat currencyFormat = new DecimalFormat("#,##0 ₫");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public JPanelManageBill() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);

		// ===== PHẦN NORTH: TIÊU ĐỀ + SEARCH =====
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(Color.WHITE);

		JLabel lblTitle = new JLabel("BILL MANAGEMENT");
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		lblTitle.setForeground(Color.DARK_GRAY);
		lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
		northPanel.add(lblTitle, BorderLayout.CENTER);

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
		top.setBackground(Color.WHITE);
		txtSearch = new JTextField(30);
		JButton btnSearch = new JButton("Search");
		btnSearch.setBackground(new Color(255, 99, 71));
		btnSearch.setForeground(Color.WHITE);
		btnSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnSearch.addActionListener(e -> loadTable(billModel.findByKeyword(txtSearch.getText().trim())));
		top.add(new JLabel("Search by bill code:"));
		top.add(txtSearch);
		top.add(btnSearch);
		northPanel.add(top, BorderLayout.SOUTH);

		add(northPanel, BorderLayout.NORTH);

		// ===== TABLE VỚI CỘT MỚI: Payment Method =====
		tableModel = new DefaultTableModel(new String[] { "No.", "Bill Code", "Customer", "Created By", "Created Time",
				"Total Amount", "Payment Method", "Status" }, 0) {
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

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Payment Method
		table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Status

		add(new JScrollPane(table), BorderLayout.CENTER);

		// ===== BOTTOM: BUTTONS =====
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
		bottom.setBackground(Color.WHITE);

		JButton btnViewDetail = new JButton("View Details");
		JButton btnUpdateStatus = new JButton("Update Status");
		styleButton(btnViewDetail, new Color(255, 165, 0));
		styleButton(btnUpdateStatus, new Color(30, 144, 255));

		bottom.add(btnViewDetail);
		bottom.add(btnUpdateStatus);

		add(bottom, BorderLayout.SOUTH);

		btnViewDetail.addActionListener(e -> viewDetail());
		btnUpdateStatus.addActionListener(e -> updateStatus());

		loadTable(billModel.findAll());
	}

	private void styleButton(JButton btn, Color bg) {
		btn.setForeground(Color.WHITE);
		btn.setBackground(bg);
		btn.setFont(new Font("SansSerif", Font.BOLD, 16));
		btn.setFocusPainted(false);
		btn.setPreferredSize(new Dimension(200, 50));
	}

	private void loadTable(List<Bill> list) {
		tableModel.setRowCount(0);
		int stt = 1;
		for (Bill b : list) {
			Customer customer = customerModel.findById(b.getIdCustomer());
			String customerName = customer != null ? customer.getFullName() : "Khách lẻ";
			Staff creator = staffModel.findById(b.getIdCreator());
			String creatorName = creator != null ? creator.getFullName() : "Unknown";
			Status status = statusModel.findById(b.getIdStatus());
			String statusName = status != null ? status.getTitle() : "Unknown";
			String createdTime = b.getCreatedDate() != null ? dateFormat.format(b.getCreatedDate()) : "N/A";
			String totalFormatted = b.getTotal() != null ? currencyFormat.format(b.getTotal()) : "0 ₫";
			String paymentMethod = b.getPaymentMethod() != null ? b.getPaymentMethod() : "Cash";

			tableModel.addRow(new Object[] { stt++, b.getCode(), customerName, creatorName, createdTime, totalFormatted,
					paymentMethod, // ← CỘT MỚI
					statusName });
		}
	}

	private void viewDetail() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xem chi tiết!");
			return;
		}
		String billCode = (String) tableModel.getValueAt(row, 1);
		Bill selectedBill = billModel.findByCode(billCode);
		if (selectedBill == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
			return;
		}
		JFrame frame = new JFrame("Chi tiết hóa đơn - " + billCode);
		frame.setSize(1000, 700);
		frame.setLocationRelativeTo(this);
		frame.add(new JPanelBillDetail(selectedBill.getId()));
		frame.setVisible(true);
	}

	private void updateStatus() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để cập nhật trạng thái!");
			return;
		}
		String billCode = (String) tableModel.getValueAt(row, 1);
		Bill selectedBill = billModel.findByCode(billCode);
		if (selectedBill == null)
			return;

		List<Status> statuses = statusModel.findAll();
		JComboBox<Status> cmbStatus = new JComboBox<>();
		for (Status s : statuses) {
			cmbStatus.addItem(s);
		}
		cmbStatus.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Status) {
					setText(((Status) value).getTitle());
				}
				return this;
			}
		});

		int result = JOptionPane.showConfirmDialog(this, cmbStatus, "Cập nhật trạng thái hóa đơn",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			Status selectedStatus = (Status) cmbStatus.getSelectedItem();
			selectedBill.setIdStatus(selectedStatus.getId());
			selectedBill.setIdUpdater(Session.currentStaff.getId());
			if (billModel.update(selectedBill)) {
				JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
				loadTable(billModel.findAll());
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
			}
		}
	}
}