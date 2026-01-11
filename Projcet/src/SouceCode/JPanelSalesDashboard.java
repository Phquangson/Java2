package SouceCode;

import entities.Bill;
import entities.BillItem;
import entities.Coupon;
import entities.Customer;
import entities.Product;
import entities.Category;
import models.BillItemModel;
import models.BillModel;
import models.CouponModel;
import models.CustomerModel;
import models.ProductModel;
import models.CategoryModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class JPanelSalesDashboard extends JPanel {

	private JTable table;
	private DefaultTableModel tableModel;
	private JLabel lblTotalQuantity;
	private JLabel lblSubTotal;
	private JLabel lblDiscount;
	private JLabel lblVat;
	private JLabel lblTotal;

	private DecimalFormat currencyFormat = new DecimalFormat("#,##0 ₫");

	private JComboBox<Coupon> cmbCoupon;

	private ProductModel productModel = new ProductModel();
	private CouponModel couponModel = new CouponModel();
	private BillModel billModel = new BillModel();
	private BillItemModel billItemModel = new BillItemModel();
	private CustomerModel customerModel = new CustomerModel();
	private CategoryModel categoryModel = new CategoryModel();

	private Bill tempBill = null;
	private List<BillItem> tempBillItems = new ArrayList<>();
	private Customer tempCustomer = null;
	private Map<String, BillItem> productToBillItem = new HashMap<>();

	public JPanelSalesDashboard() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);

		// ===== PHẦN NORTH: TIÊU ĐỀ + NÚT THÊM/XÓA =====
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(Color.WHITE);

		JLabel lblTitle = new JLabel("SALES DASHBOARD");
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		lblTitle.setForeground(Color.DARK_GRAY);
		lblTitle.setBorder(new EmptyBorder(10, 10, 10, 10));

		northPanel.add(lblTitle, BorderLayout.NORTH);

		JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
		topButtons.setBackground(Color.WHITE);

		JButton btnAddProduct = new JButton("Add Product");
		btnAddProduct.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnAddProduct.setBackground(new Color(50, 205, 50));
		btnAddProduct.setForeground(Color.WHITE);
		btnAddProduct.addActionListener(e -> showAddProductDialog());

		JButton btnDeleteProduct = new JButton("Delete Product");
		btnDeleteProduct.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnDeleteProduct.setBackground(Color.RED);
		btnDeleteProduct.setForeground(Color.WHITE);
		btnDeleteProduct.addActionListener(e -> deleteProduct());

		topButtons.add(btnAddProduct);
		topButtons.add(btnDeleteProduct);

		northPanel.add(topButtons, BorderLayout.CENTER);

		add(northPanel, BorderLayout.NORTH);

		// ===== TABLE DETAIL =====
		tableModel = new DefaultTableModel(
				new String[] { "Product Code", "Product Name", "Selling Price", "Quantity", "Amount" }, 0) {
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
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					if (row != -1) {
						editQuantity(row);
					}
				}
			}
		});

		add(new JScrollPane(table), BorderLayout.CENTER);

		// ===== BOTTOM =====
		JPanel bottom = new JPanel(new GridLayout(7, 2, 10, 10));
		bottom.setBackground(Color.WHITE);
		bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		cmbCoupon = new JComboBox<>();
		cmbCoupon.addItem(null);

		List<Coupon> activeCoupons = couponModel.findActiveCoupons();
		for (Coupon c : activeCoupons) {
			cmbCoupon.addItem(c);
		}

		cmbCoupon.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value == null) {
					setText("-- Select Coupon --");
				} else if (value instanceof Coupon) {
					Coupon c = (Coupon) value;
					setText(c.getCode() + " - " + c.getTitle() + " (Còn " + c.getQuantity() + ")");
				}
				return this;
			}
		});

		cmbCoupon.addActionListener(e -> updateBillTotal());

		bottom.add(new JLabel("Coupon:"));
		bottom.add(cmbCoupon);

		lblTotalQuantity = new JLabel("0");
		bottom.add(new JLabel("Total Quantity:"));
		bottom.add(lblTotalQuantity);

		lblSubTotal = new JLabel("0 ₫");
		bottom.add(new JLabel("Subtotal:"));
		bottom.add(lblSubTotal);

		lblDiscount = new JLabel("0 ₫");
		bottom.add(new JLabel("Discount:"));
		bottom.add(lblDiscount);

		lblVat = new JLabel("0 ₫");
		bottom.add(new JLabel("VAT (8%):"));
		bottom.add(lblVat);

		lblTotal = new JLabel("0 ₫", SwingConstants.RIGHT);
		lblTotal.setFont(new Font("SansSerif", Font.BOLD, 20));
		lblTotal.setForeground(new Color(255, 99, 71));

		bottom.add(new JLabel("TOTAL:"));
		bottom.add(lblTotal);

		JButton btnSave = new JButton("Save Bill");
		btnSave.setFont(new Font("SansSerif", Font.BOLD, 18));
		btnSave.setBackground(new Color(255, 99, 71));
		btnSave.setForeground(Color.WHITE);
		btnSave.addActionListener(e -> saveBillWithPayment());

		bottom.add(new JLabel());
		bottom.add(btnSave);

		add(bottom, BorderLayout.SOUTH);

		updateBillTotal();
	}

	private void saveBillWithPayment() {
		if (tempBill == null || tempBillItems.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có sản phẩm nào để lưu hóa đơn!");
			return;
		}

		BigDecimal finalTotal = calculateTotal();

		JDialog paymentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
				"Thanh toán - Tổng tiền: " + currencyFormat.format(finalTotal), true);
		paymentDialog.setSize(550, 500);
		paymentDialog.setLocationRelativeTo(this);
		paymentDialog.setLayout(new BorderLayout(10, 10));

		JPanel paymentPanel = new JPanel(new GridBagLayout());
		paymentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		paymentPanel.setBackground(Color.WHITE);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel lblAmount = new JLabel("Số tiền cần thanh toán:");
		lblAmount.setFont(new Font("SansSerif", Font.BOLD, 24));
		lblAmount.setForeground(new Color(255, 99, 71));

		JLabel lblAmountValue = new JLabel(currencyFormat.format(finalTotal));
		lblAmountValue.setFont(new Font("SansSerif", Font.BOLD, 30));
		lblAmountValue.setForeground(new Color(255, 99, 71));

		JRadioButton rbCash = new JRadioButton("Tiền mặt");
		JRadioButton rbQR = new JRadioButton("Chuyển khoản QR");
		ButtonGroup group = new ButtonGroup();
		group.add(rbCash);
		group.add(rbQR);
		rbCash.setSelected(true);

		rbCash.setFont(new Font("SansSerif", Font.PLAIN, 18));
		rbQR.setFont(new Font("SansSerif", Font.PLAIN, 18));

		java.net.URL qrURL = getClass().getResource("/resources/qrcodebank.png");
		ImageIcon qrIcon = null;
		if (qrURL != null) {
			qrIcon = new ImageIcon(qrURL);
			Image qrImage = qrIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
			qrIcon = new ImageIcon(qrImage);
		}

		JLabel lblQR = new JLabel(qrIcon != null ? qrIcon : new ImageIcon());
		lblQR.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel qrPanel = new JPanel();
		qrPanel.setBackground(Color.WHITE);
		qrPanel.setBorder(BorderFactory.createTitledBorder("Quét QR để chuyển khoản"));
		qrPanel.add(lblQR);
		qrPanel.setVisible(false);

		rbQR.addActionListener(e -> qrPanel.setVisible(true));
		rbCash.addActionListener(e -> qrPanel.setVisible(false));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		paymentPanel.add(lblAmount, gbc);
		gbc.gridy = 1;
		paymentPanel.add(lblAmountValue, gbc);

		gbc.gridy = 2;
		gbc.gridwidth = 1;
		paymentPanel.add(rbCash, gbc);
		gbc.gridx = 1;
		paymentPanel.add(rbQR, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		paymentPanel.add(qrPanel, gbc);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		buttonPanel.setBackground(Color.WHITE);

		JButton btnConfirm = new JButton("Xác nhận thanh toán");
		btnConfirm.setFont(new Font("SansSerif", Font.BOLD, 18));
		btnConfirm.setBackground(new Color(50, 205, 50));
		btnConfirm.setForeground(Color.WHITE);
		btnConfirm.setPreferredSize(new Dimension(200, 50));

		JButton btnCancel = new JButton("Hủy & Lưu tạm (Pending)");
		btnCancel.setFont(new Font("SansSerif", Font.BOLD, 18));
		btnCancel.setBackground(new Color(255, 165, 0));
		btnCancel.setForeground(Color.WHITE);
		btnCancel.setPreferredSize(new Dimension(250, 50));

		buttonPanel.add(btnConfirm);
		buttonPanel.add(btnCancel);

		gbc.gridy = 4;
		paymentPanel.add(buttonPanel, gbc);

		paymentDialog.add(paymentPanel, BorderLayout.CENTER);

		// Lấy trạng thái radio button để xác định phương thức thanh toán
		btnConfirm.addActionListener(e -> {
			String paymentMethod = rbCash.isSelected() ? "Cash" : "Transfer";
			saveBillToDatabase(9, paymentMethod); // Paid
			JOptionPane.showMessageDialog(paymentDialog,
					"Thanh toán thành công!\nHóa đơn đã được lưu với trạng thái Paid.");
			paymentDialog.dispose();
			resetAll();
		});

		btnCancel.addActionListener(e -> {
			String paymentMethod = rbCash.isSelected() ? "Cash" : "Transfer";
			saveBillToDatabase(10, paymentMethod); // Pending
			JOptionPane.showMessageDialog(paymentDialog, "Hóa đơn đã được lưu tạm với trạng thái Pending.");
			paymentDialog.dispose();
			resetAll();
		});

		paymentDialog.setVisible(true);
	}

	// Sửa method này để nhận thêm paymentMethod
	private void saveBillToDatabase(int statusId, String paymentMethod) {
		String customerPhone = tempCustomer.getPhone().trim();
		String customerName = tempCustomer.getFullName().trim();
		if (!customerPhone.matches("^0[0-9]{9}$")) {
			JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!");
			return;
		}

		Customer existingCustomer = customerModel.findByPhone(customerPhone);
		String finalCustomerId;
		if (existingCustomer != null) {
			if (!existingCustomer.getFullName().equals(customerName)) {
				existingCustomer.setFullName(customerName);
				existingCustomer.setIdUpdater(Session.currentStaff.getId());
				customerModel.update(existingCustomer);
			}
			finalCustomerId = existingCustomer.getId();
		} else {
			tempCustomer.setId(UUID.randomUUID().toString());
			tempCustomer.setIdCreator(Session.currentStaff.getId());
			tempCustomer.setIdUpdater(Session.currentStaff.getId());
			if (!customerModel.create(tempCustomer)) {
				JOptionPane.showMessageDialog(this, "Không thể tạo khách hàng mới!");
				return;
			}
			finalCustomerId = tempCustomer.getId();
		}

		tempBill.setId(UUID.randomUUID().toString());
		tempBill.setCode("BILL-" + System.currentTimeMillis());
		tempBill.setIdCustomer(finalCustomerId);
		tempBill.setSubtotal(calculateSubtotal());
		tempBill.setDiscount(calculateDiscount());
		tempBill.setVAT(calculateVAT());
		tempBill.setTotal(calculateTotal());
		tempBill.setTotalQuantity(calculateTotalQuantity());

		Coupon selectedCoupon = (Coupon) cmbCoupon.getSelectedItem();
		Integer couponId = (selectedCoupon != null) ? selectedCoupon.getId() : null;
		tempBill.setIdCoupon(couponId);

		tempBill.setIdCreator(Session.currentStaff.getId());
		tempBill.setIdUpdater(Session.currentStaff.getId());
		tempBill.setIdStatus(statusId);
		tempBill.setPaymentMethod(paymentMethod); // ← CẬP NHẬT PAYMENT METHOD

		if (!billModel.create(tempBill)) {
			JOptionPane.showMessageDialog(this, "Lưu hóa đơn thất bại!");
			return;
		}

		for (BillItem item : tempBillItems) {
			item.setIdBill(tempBill.getId());
			item.setIdCreator(Session.currentStaff.getId());
			item.setIdUpdater(Session.currentStaff.getId());
			if (!billItemModel.create(item)) {
				JOptionPane.showMessageDialog(this, "Lưu chi tiết hóa đơn thất bại!");
				return;
			}

			Product p = productModel.findById(item.getIdProduct());
			if (p != null) {
				p.setQuantity(p.getQuantity() - item.getQuantity());
				productModel.update(p);
			}
		}

		if (selectedCoupon != null && selectedCoupon.getId() != 5) {
			if (selectedCoupon.getQuantity() > 0) {
				selectedCoupon.setQuantity(selectedCoupon.getQuantity() - 1);
				selectedCoupon.setIdUpdater(Session.currentStaff.getId());
				couponModel.update(selectedCoupon);
			}
		}
	}

	private BigDecimal calculateSubtotal() {
		BigDecimal sum = BigDecimal.ZERO;
		for (BillItem i : tempBillItems)
			sum = sum.add(i.getTotal());
		return sum;
	}

	private BigDecimal calculateDiscount() {
		Coupon c = (Coupon) cmbCoupon.getSelectedItem();
		return c != null ? c.getDiscountValue() : BigDecimal.ZERO;
	}

	private BigDecimal calculateVAT() {
		return calculateSubtotal().multiply(new BigDecimal("0.08"));
	}

	private BigDecimal calculateTotal() {
		return calculateSubtotal().subtract(calculateDiscount()).add(calculateVAT());
	}

	private int calculateTotalQuantity() {
		int sum = 0;
		for (BillItem i : tempBillItems)
			sum += i.getQuantity();
		return sum;
	}

	private void updateBillTotal() {
		BigDecimal subtotal = BigDecimal.ZERO;
		int totalQty = 0;
		for (BillItem item : tempBillItems) {
			subtotal = subtotal.add(item.getTotal());
			totalQty += item.getQuantity();
		}

		BigDecimal discount = BigDecimal.ZERO;
		Coupon coupon = (Coupon) cmbCoupon.getSelectedItem();
		if (coupon != null) {
			discount = coupon.getDiscountValue();
		}

		BigDecimal vat = subtotal.multiply(new BigDecimal("0.08"));
		BigDecimal total = subtotal.subtract(discount).add(vat);

		lblTotalQuantity.setText(String.valueOf(totalQty));
		lblSubTotal.setText(currencyFormat.format(subtotal));
		lblDiscount.setText(currencyFormat.format(discount));
		lblVat.setText(currencyFormat.format(vat));
		lblTotal.setText(currencyFormat.format(total));
	}

	private void resetAll() {
		tempBill = null;
		tempCustomer = null;
		tempBillItems.clear();
		productToBillItem.clear();
		tableModel.setRowCount(0);
		cmbCoupon.setSelectedIndex(0);
		updateBillTotal();
	}

	private void showAddProductDialog() {
		List<Product> allProducts = productModel.findAll();

		List<Product> publicProducts = allProducts.stream().filter(p -> p.getIsPublic() == 1 && p.getIdStatus() != 19)
				.collect(Collectors.toList());

		if (publicProducts.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Không có sản phẩm nào khả dụng để bán!\n" + "Lý do có thể:\n"
							+ "- Tất cả sản phẩm bị ẩn (_is_public = 0)\n" + "- Hoặc bị xóa (_id_status = 19)",
					"Không có sản phẩm", JOptionPane.WARNING_MESSAGE);
			return;
		}

		List<Category> categories = categoryModel.findAll();

		DefaultListModel<String> categoryListModel = new DefaultListModel<>();
		categoryListModel.addElement("Tất cả danh mục");
		Map<String, Integer> categoryMap = new HashMap<>();
		categoryMap.put("Tất cả danh mục", 0);
		for (Category c : categories) {
			categoryListModel.addElement(c.getTitle());
			categoryMap.put(c.getTitle(), c.getId());
		}

		JList<String> listCategory = new JList<>(categoryListModel);
		listCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCategory.setSelectedIndex(0);
		listCategory.setVisibleRowCount(8);

		JScrollPane scrollCategory = new JScrollPane(listCategory);
		scrollCategory.setPreferredSize(new Dimension(400, 150));

		JTextField txtSearchProduct = new JTextField(30);
		txtSearchProduct.setFont(new Font("SansSerif", Font.PLAIN, 16));

		DefaultListModel<Product> productListModel = new DefaultListModel<>();
		JList<Product> listProduct = new JList<>(productListModel);
		listProduct.setCellRenderer(new ProductListCellRenderer());
		listProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollProduct = new JScrollPane(listProduct);
		scrollProduct.setPreferredSize(new Dimension(600, 300));

		Map<String, Product> productMap = new HashMap<>();

		Runnable refreshList = () -> {
			String searchText = txtSearchProduct.getText().trim().toLowerCase();
			String selectedCategory = listCategory.getSelectedValue();

			List<Product> baseList;
			if ("Tất cả danh mục".equals(selectedCategory)) {
				baseList = publicProducts;
			} else {
				int categoryId = categoryMap.get(selectedCategory);
				baseList = publicProducts.stream().filter(p -> p.getIdCategory() == categoryId)
						.collect(Collectors.toList());
			}

			List<Product> finalList = baseList.stream().filter(p -> p.getTitle().toLowerCase().contains(searchText)
					|| p.getCode().toLowerCase().contains(searchText)).collect(Collectors.toList());

			productListModel.clear();
			productMap.clear();
			for (Product p : finalList) {
				productListModel.addElement(p);
				productMap.put(p.getTitle() + p.getCode(), p);
			}
		};

		refreshList.run();

		listCategory.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				refreshList.run();
			}
		});

		txtSearchProduct.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				refreshList.run();
			}

			public void removeUpdate(DocumentEvent e) {
				refreshList.run();
			}

			public void changedUpdate(DocumentEvent e) {
				refreshList.run();
			}
		});

		JTextField txtQuantity = new JTextField("1", 10);
		JTextField txtCustomerName = new JTextField(20);
		JTextField txtCustomerPhone = new JTextField(20);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Danh mục:"), gbc);
		gbc.gridx = 1;
		panel.add(scrollCategory, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		panel.add(new JLabel("Tìm kiếm sản phẩm:"), gbc);
		gbc.gridy = 2;
		panel.add(txtSearchProduct, gbc);

		gbc.gridy = 3;
		panel.add(scrollProduct, gbc);

		gbc.gridy = 4;
		gbc.gridwidth = 1;
		panel.add(new JLabel("Số lượng:"), gbc);
		gbc.gridx = 1;
		panel.add(txtQuantity, gbc);

		if (tempBill == null) {
			gbc.gridx = 0;
			gbc.gridy = 5;
			panel.add(new JLabel("Tên khách hàng:"), gbc);
			gbc.gridx = 1;
			panel.add(txtCustomerName, gbc);
			gbc.gridx = 0;
			gbc.gridy = 6;
			panel.add(new JLabel("Số điện thoại (*):"), gbc);
			gbc.gridx = 1;
			panel.add(txtCustomerPhone, gbc);
		}

		int result = JOptionPane.showConfirmDialog(this, panel, "Thêm sản phẩm", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION)
			return;

		Product selectedProduct = listProduct.getSelectedValue();
		if (selectedProduct == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm!");
			return;
		}

		int quantity;
		try {
			quantity = Integer.parseInt(txtQuantity.getText().trim());
			if (quantity <= 0)
				throw new Exception();
			if (selectedProduct.getQuantity() < quantity) {
				JOptionPane.showMessageDialog(this,
						"Tồn kho không đủ! Chỉ còn " + selectedProduct.getQuantity() + " sản phẩm");
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Số lượng phải là số hợp lệ và lớn hơn 0!");
			return;
		}

		if (tempBill == null) {
			String customerName = txtCustomerName.getText().trim();
			String customerPhone = txtCustomerPhone.getText().trim();
			if (customerName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!");
				return;
			}
			if (customerPhone.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng!");
				return;
			}
			if (!customerPhone.matches("^0[0-9]{9}$")) {
				JOptionPane.showMessageDialog(this,
						"Số điện thoại phải có 10 số và bắt đầu bằng 0!\nVí dụ: 0987654321");
				return;
			}

			Customer existing = customerModel.findByPhone(customerPhone);
			if (existing != null) {
				tempCustomer = existing;
				if (!existing.getFullName().equals(customerName)) {
					existing.setFullName(customerName);
					existing.setIdUpdater(Session.currentStaff.getId());
					customerModel.update(existing);
				}
			} else {
				tempCustomer = new Customer();
				tempCustomer.setId(UUID.randomUUID().toString());
				tempCustomer.setFullName(customerName);
				tempCustomer.setPhone(customerPhone);
				tempCustomer.setAddress("N/A");
				tempCustomer.setCity("Hanoi");
				tempCustomer.setDistrict("District 1");
				tempCustomer.setIdCreator(Session.currentStaff.getId());
				tempCustomer.setIdUpdater(Session.currentStaff.getId());
			}

			tempBill = new Bill();
			tempBill.setCode("TEMP-BILL-" + System.currentTimeMillis());
			tempBill.setIdCustomer(tempCustomer.getId());
			tempBill.setIdCreator(Session.currentStaff.getId());
			tempBill.setIdUpdater(Session.currentStaff.getId());
			tempBill.setIdStatus(10);
		}

		BillItem existingItem = productToBillItem.get(selectedProduct.getCode());
		if (existingItem != null) {
			int newQty = existingItem.getQuantity() + quantity;
			existingItem.setQuantity(newQty);
			existingItem.setTotal(selectedProduct.getPrice().multiply(BigDecimal.valueOf(newQty)));
			int row = findRowByProductCode(selectedProduct.getCode());
			if (row != -1) {
				tableModel.setValueAt(newQty, row, 3);
				tableModel.setValueAt(currencyFormat.format(existingItem.getTotal()), row, 4);
			}
		} else {
			BillItem newItem = new BillItem();
			newItem.setIdProduct(selectedProduct.getId());
			newItem.setQuantity(quantity);
			newItem.setPrice(selectedProduct.getPrice());
			newItem.setTotal(selectedProduct.getPrice().multiply(BigDecimal.valueOf(quantity)));
			tempBillItems.add(newItem);
			productToBillItem.put(selectedProduct.getCode(), newItem);
			tableModel.addRow(new Object[] { selectedProduct.getCode(), selectedProduct.getTitle(),
					currencyFormat.format(selectedProduct.getPrice()), quantity,
					currencyFormat.format(newItem.getTotal()) });
		}

		updateBillTotal();
		JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
	}

	private int findRowByProductCode(String code) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (tableModel.getValueAt(i, 0).equals(code)) {
				return i;
			}
		}
		return -1;
	}

	// Renderer hiển thị hình nhỏ trong danh sách
	private static class ProductListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof Product) {
				Product p = (Product) value;
				setText(p.getTitle() + " | " + p.getCode() + " | Giá: "
						+ new DecimalFormat("#,##0 ₫").format(p.getPrice()) + " | Tồn: " + p.getQuantity());

				if (p.getLink() != null && p.getLink().length > 0) {
					ImageIcon icon = new ImageIcon(p.getLink());
					Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
					setIcon(new ImageIcon(img));
				} else {
					setIcon(null);
				}
				setHorizontalTextPosition(SwingConstants.RIGHT);
				setVerticalTextPosition(SwingConstants.CENTER);
			}
			return this;
		}
	}

	private void editQuantity(int row) {
		String productCode = (String) tableModel.getValueAt(row, 0);
		int oldQty = (Integer) tableModel.getValueAt(row, 3);
		String input = JOptionPane.showInputDialog(this, "Sửa số lượng:", oldQty);
		if (input == null)
			return;
		try {
			int newQty = Integer.parseInt(input.trim());
			if (newQty <= 0) {
				JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
				return;
			}
			Product p = productModel.findByCode(productCode);
			if (p.getQuantity() + oldQty < newQty) {
				JOptionPane.showMessageDialog(this, "Tồn kho không đủ!");
				return;
			}
			BillItem item = productToBillItem.get(productCode);
			if (item != null) {
				item.setQuantity(newQty);
				item.setTotal(p.getPrice().multiply(BigDecimal.valueOf(newQty)));
			}
			tableModel.setValueAt(newQty, row, 3);
			tableModel.setValueAt(currencyFormat.format(item.getTotal()), row, 4);
			updateBillTotal();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Số lượng phải là số!");
		}
	}

	private void deleteProduct() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm khỏi hóa đơn?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			String productCode = (String) tableModel.getValueAt(row, 0);
			BillItem item = productToBillItem.get(productCode);
			if (item != null) {
				tempBillItems.remove(item);
				productToBillItem.remove(productCode);
			}
			tableModel.removeRow(row);
			updateBillTotal();
			JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!");
		}
	}
}