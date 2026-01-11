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

public class JPanelReturnDetails extends JPanel {
	private JLabel lblCustomerName, lblPhone, lblCashier;
	private JTable tableDetails;
	private DefaultTableModel tableModel;
	private JLabel lblTotalQty, lblSubtotal, lblTotal;
	private JButton btnClose;

	private BillModel billModel = new BillModel();
	private CustomerModel customerModel = new CustomerModel();
	private StaffModel staffModel = new StaffModel();
	private BillItemModel billItemModel = new BillItemModel();
	private ProductModel productModel = new ProductModel();
	private ReturnProductModel returnProductModel = new ReturnProductModel();
	private Runnable onReload;
	private NumberFormat currencyVN = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

	private String billId;

	public JPanelReturnDetails(String orderId, Runnable onReload) {
		this.onReload = onReload;
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(15, 15, 15, 15));

		JLabel lblTitle = new JLabel("Invoice details: " + orderId);
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
		lblCustomerName = new JLabel("Customer: ");
		lblCustomerName.setFont(new Font("SansSerif", Font.PLAIN, 16));
		lblCashier = new JLabel("Cashier: ");
		lblCashier.setFont(new Font("SansSerif", Font.PLAIN, 16));
		row1.add(lblCustomerName, BorderLayout.WEST);
		row1.add(lblCashier, BorderLayout.EAST);

		lblPhone = new JLabel("Phone: ");
		lblPhone.setFont(new Font("SansSerif", Font.PLAIN, 16));

		infoBlock.add(row1);
		infoBlock.add(lblPhone);

		tableModel = new DefaultTableModel(new String[] { "Product Name", "Quantity Return", "Price", "Total" }, 0) {
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

		lblTotalQty = new JLabel("Total Quantity: ");
		lblSubtotal = new JLabel("Subtotal: ");

		Font f = new Font("SansSerif", Font.BOLD, 14);
		for (JLabel l : new JLabel[] { lblTotalQty, lblSubtotal }) {
			l.setFont(f);
		}

		summaryPanel.add(lblTotalQty);
		summaryPanel.add(lblSubtotal);

		// ===== Panel nút =====
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		btnClose = new JButton("Đóng");
		btnClose.setBackground(new Color(46, 139, 87));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 14));

		buttonPanel.add(btnClose, BorderLayout.EAST);

		// Khối SOUTH: summary + button
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setBackground(Color.WHITE);
		southPanel.add(summaryPanel, BorderLayout.CENTER);
		southPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(southPanel, BorderLayout.SOUTH);
		btnClose.addActionListener(e -> {
			if (onReload != null)
				onReload.run();
			Window w = SwingUtilities.getWindowAncestor(this);
			if (w != null)
				w.dispose();
		});
		loadData(orderId);
	}

	private void loadData(String orderId) {
		String billId = returnProductModel.findBillIdById(orderId);
		if (billId == null)
			return;

		Bill bill = billModel.findById(billId);
		if (bill == null)
			return;
		this.billId = bill.getId();
		Customer c = customerModel.findById(bill.getIdCustomer());
		if (c != null) {
			lblCustomerName.setText("Customer: " + c.getFullName());
			lblPhone.setText("Phone: " + c.getPhone());
		}
		Staff emp = staffModel.findById(bill.getIdCreator());
		if (emp != null) {
			lblCashier.setText("Cashier: " + emp.getFullName());
		}
		List<ReturnProduct> returns = returnProductModel.findByBillId(billId);
		int totalQty = 0;
		double totalRefund = 0;
		tableModel.setRowCount(0);

		for (ReturnProduct r : returns) {
			Product p = productModel.findById(r.getIdProduct());
			String productName = p != null ? p.getTitle() : "Product deleted";
			double price = p != null ? p.getPrice().doubleValue() : 0;
			double lineRefund = r.getQuantity() * price;
			tableModel.addRow(new Object[] { productName, r.getQuantity(), currencyVN.format(price),
					currencyVN.format(lineRefund) });

			totalQty += r.getQuantity();
			totalRefund += lineRefund;
		}

		lblTotalQty.setText("Total Quantity: " + totalQty);
		lblSubtotal.setText("Total  Price: " + currencyVN.format(totalRefund));
	}
}
