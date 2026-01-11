package SouceCode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import entities.Inventory;
import entities.Product;
import entities.Status;
import entities.Supplier;
import entities.Type;
import models.CategoryModel;
import models.InventoryActivityModel;
import models.ProductModel;
import models.StatusModel;
import models.SupplierModel;
import models.TypeModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import entities.Category;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class JPanelAddProduct extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField txtCode, txtName, txtStock, txtPrice;
	private JComboBox<Supplier> cbSupplier;
	private JButton btnAdd;
	private JComboBox<Status> cbStatus;
	private JComboBox<Category> cbCategory;
	private JComboBox<Type> cbType;
	private JPanelStockIn1 parentStockIn;
	private JPanelProducts parentProducts;
	private JDialog dialog;

	private JLabel lblImage;
	private JLabel lblTitle;
	private File selectedImageFile;

	public JPanelAddProduct(JPanelStockIn1 parentStockIn, JPanelProducts parentProducts, JDialog dialog) {
		this.parentStockIn = parentStockIn;
		this.parentProducts = parentProducts;
		this.dialog = dialog;
		initUI();
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(750, 550));

		final Point dragPoint = new Point();
		dialog.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				dragPoint.x = e.getX();
				dragPoint.y = e.getY();
			}
		});
		dialog.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				Point p = dialog.getLocation();
				dialog.setLocation(p.x + e.getX() - dragPoint.x, p.y + e.getY() - dragPoint.y);
			}
		});

		JLabel lblTitle = new JLabel("Add Product", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(60, 60, 60));
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setOpaque(true);
		add(lblTitle, BorderLayout.NORTH);

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		SupplierModel supplierModel = new SupplierModel();
		java.util.List<Supplier> suppliers = supplierModel.findAll();

//		gbc.gridx = 0;
//		gbc.gridy = 2;
//		formPanel.add(new JLabel("Stock:"), gbc);
//		gbc.gridx = 1;
//		txtStock = new JTextField(15);
//		((AbstractDocument) txtStock.getDocument()).setDocumentFilter(new QuantityFormatFilter());
//		formPanel.add(txtStock, gbc);

//		gbc.gridx = 0;
//		gbc.gridy = 4;
//		formPanel.add(new JLabel("Total:"), gbc);
//		gbc.gridx = 1;
//		txtTotal = new JTextField();
//		txtTotal.setEditable(false);
//		txtTotal.setBackground(new Color(240, 240, 240));
//		formPanel.add(txtTotal, gbc);

		// Supplier
		gbc.gridx = 0;
		gbc.gridy = 0;
		formPanel.add(new JLabel("Supplier:"), gbc);
		gbc.gridx = 1;
		cbSupplier = new JComboBox<>();
		cbSupplier.addItem(new Supplier(0, "-- Select Supplier --"));
		if (suppliers != null) {
			for (Supplier s : suppliers) {
				cbSupplier.addItem(s);
			}
		}
		cbSupplier.setSelectedIndex(0);
		formPanel.add(cbSupplier, gbc);

		// Name
		txtName = createTextField(formPanel, gbc, "Name:", 1);

		// Price
		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(new JLabel("Price:"), gbc);
		gbc.gridx = 1;
		txtPrice = new JTextField(15);
		((AbstractDocument) txtPrice.getDocument()).setDocumentFilter(new PriceFormatFilter());
		formPanel.add(txtPrice, gbc);

		// Category
		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(new JLabel("Category:"), gbc);
		gbc.gridx = 1;
		CategoryModel categoryModel = new CategoryModel();
		java.util.List<Category> categories = categoryModel.findAll();
		cbCategory = new JComboBox<>();
		cbCategory.addItem(new Category(0, "-- Select Category --"));
		if (categories != null) {
			for (Category cat : categories) {
				cbCategory.addItem(cat);
			}
		}
		cbCategory.setSelectedIndex(0);
		formPanel.add(cbCategory, gbc);

		cbCategory.addActionListener(e -> {
			Category selectedCategory = (Category) cbCategory.getSelectedItem();
			if (selectedCategory != null && selectedCategory.getId() != 0) {
				String[] words = removeDiacritics(selectedCategory.getTitle().trim()).split("\\s+");
				StringBuilder prefixBuilder = new StringBuilder();
				for (String word : words) {
					if (!word.isEmpty()) {
						prefixBuilder.append(Character.toUpperCase(word.charAt(0)));
					}
				}
				String prefix = prefixBuilder.toString();

				ProductModel model = new ProductModel();
				String latestCode = model.findLatestCodeByPrefix(prefix);
				int nextNumber = 1000001;
				if (latestCode != null && latestCode.startsWith(prefix)) {
					try {
						String numberPart = latestCode.substring(prefix.length());
						nextNumber = Integer.parseInt(numberPart) + 1;
					} catch (NumberFormatException ex) {
					}
				}
				txtCode.setText(prefix + nextNumber);
			}
		});

		// Code
		txtCode = createTextField(formPanel, gbc, "Code:", 4);
		txtCode.setEditable(false);
		txtCode.setEnabled(false);

		// Type
		gbc.gridx = 0;
		gbc.gridy = 5;
		formPanel.add(new JLabel("Type:"), gbc);
		gbc.gridx = 1;
		TypeModel typeModel = new TypeModel();
		java.util.List<Type> types = typeModel.findAll();
		cbType = new JComboBox<>();
		cbType.addItem(new Type(0, "-- Select Type --"));
		if (types != null) {
			for (Type t : types) {
				cbType.addItem(t);
			}
		}
		cbType.setSelectedIndex(0);
		formPanel.add(cbType, gbc);

		add(formPanel, BorderLayout.CENTER);

//		gbc.gridx = 0;
//		gbc.gridy = 8;
//		formPanel.add(new JLabel("Status:"), gbc);
//		gbc.gridx = 1;
//		StatusModel stautusModel = new StatusModel();
//		java.util.List<Status> statuses = stautusModel.findAll();
//		cbStatus = new JComboBox<>();
//		cbStatus.addItem(new Status(0, 0, "-- Select Status --", "", "", null, null));
//		if (statuses != null) {
//			for (Status st : statuses) {
//				cbStatus.addItem(st);
//			}
//		}
//		cbStatus.setSelectedIndex(0);
//		formPanel.add(cbStatus, gbc);

		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFocusPainted(false);
		btnClose.setPreferredSize(new Dimension(140, 40));
		btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);

		btnClose.addActionListener(e -> {
			if (dialog != null) {
				dialog.dispose();
			}
		});

		mainPanel.add(formPanel, BorderLayout.CENTER);

		JPanel imagePanel = new JPanel(new GridBagLayout());
		imagePanel.setBackground(Color.WHITE);
		imagePanel.setPreferredSize(new Dimension(350, 200));

		lblImage = new JLabel();
		lblImage.setPreferredSize(new Dimension(250, 250));
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		lblImage.setVerticalAlignment(SwingConstants.CENTER);
		lblImage.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));
		lblImage.setOpaque(true);
		lblImage.setBackground(new Color(250, 250, 250));
		lblImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		ImageIcon defaultIcon = new ImageIcon(getClass().getResource("/resources/img-push.png"));
		Image scaled = defaultIcon.getImage().getScaledInstance(250, 200, Image.SCALE_SMOOTH);
		lblImage.setIcon(new ImageIcon(scaled));
		lblImage.setText("");

		GridBagConstraints imgGbc = new GridBagConstraints();
		imgGbc.gridx = 0;
		imgGbc.gridy = 0;
		imgGbc.anchor = GridBagConstraints.CENTER;
		imgGbc.insets = new Insets(0, 0, 0, 0);

		imagePanel.add(lblImage, imgGbc);
		mainPanel.add(imagePanel, BorderLayout.EAST);

		add(mainPanel, BorderLayout.CENTER);

		lblImage.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));
		lblImage.setBackground(new Color(250, 250, 250));
		lblImage.setOpaque(true);

		lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showOpenDialog(JPanelAddProduct.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedImageFile = fileChooser.getSelectedFile();
					long fileSizeInBytes = selectedImageFile.length();
					double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
					if (fileSizeInMB > 2.0) {
						JOptionPane.showMessageDialog(JPanelAddProduct.this,
								"Photo is too large (" + String.format("%.2f", fileSizeInMB)
										+ " MB). Please select images under 2MB",
								"File is too large", JOptionPane.ERROR_MESSAGE);
						selectedImageFile = null;
						return;
					}
					ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
					Image img = icon.getImage();

					int imgWidth = icon.getIconWidth();
					int imgHeight = icon.getIconHeight();

					int maxSize = 250;
					double scale = Math.min((double) maxSize / imgWidth, (double) maxSize / imgHeight);

					int newW = (int) (imgWidth * scale);
					int newH = (int) (imgHeight * scale);

					Image scaledImg = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

					lblImage.setIcon(new ImageIcon(scaledImg));
					lblImage.setText("");

					lblImage.setPreferredSize(new Dimension(newW, newH));
					lblImage.revalidate();
					lblImage.repaint();

				}
			}
		});

		btnAdd = new JButton("Add Product");
		btnAdd.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnAdd.setBackground(new Color(250, 172, 104));
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFocusPainted(false);
		btnAdd.setPreferredSize(new Dimension(190, 40));
		btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JButton btnAddSupplier = new JButton("Add Supplier");
		btnAddSupplier.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnAddSupplier.setBackground(new Color(147, 189, 87));
		btnAddSupplier.setForeground(Color.WHITE);
		btnAddSupplier.setFocusPainted(false);
		btnAddSupplier.setPreferredSize(new Dimension(140, 40));
		btnAddSupplier.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelBottom.setBackground(Color.WHITE);
		panelBottom.add(btnClose);
		panelBottom.add(btnAdd);
		panelBottom.add(btnAddSupplier);

		add(panelBottom, BorderLayout.SOUTH);

		btnAddSupplier.addActionListener(e -> {
			JDialog dialogSupplier = new JDialog((Frame) null, "Add Supplier", true);
			dialogSupplier.setUndecorated(true);
			dialogSupplier.getContentPane().add(new JPanelAddSupplier(this));
			dialogSupplier.pack();
			dialogSupplier.setLocationRelativeTo(this);
			dialogSupplier.setVisible(true);
		});

		btnAdd.addActionListener(e -> {
			if (validateInput(false)) {
				calculateTotal();

				InventoryActivityModel model = new InventoryActivityModel();

				if (model.existsByCodeOrTitle(getProductCode(), getProductName())) {
					JOptionPane.showMessageDialog(this, "Product code or name already exists in database.",
							"Duplicate Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				Product p = new Product();
				p.setCode(getProductCode());
				p.setTitle(getProductName());
				p.setPrice(BigDecimal.valueOf(getProductPriceLong()));
				p.setQuantity(0);

				p.setIdStatus(16);

				if (selectedImageFile != null) {
					try {
						byte[] imageBytes = java.nio.file.Files.readAllBytes(selectedImageFile.toPath());
						p.setLink(imageBytes);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				Supplier selectedSupplier = (Supplier) cbSupplier.getSelectedItem();
				if (selectedSupplier != null) {
					p.setIdSupplier(selectedSupplier.getId());
				}

				p.setIdCreator("1f05aa86-d6c8-11f0-b2b8-2c8db1d70194");
				p.setIdUpdater("1f05aa86-d6c8-11f0-b2b8-2c8db1d70194");
				p.setCreatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
				p.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
				p.setIsPublic(1);

				lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
					@Override
					public void mouseClicked(java.awt.event.MouseEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						int result = fileChooser.showOpenDialog(JPanelAddProduct.this);
						if (result == JFileChooser.APPROVE_OPTION) {
							selectedImageFile = fileChooser.getSelectedFile();
							ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
							Image img = icon.getImage().getScaledInstance(lblImage.getWidth(), lblImage.getHeight(),
									Image.SCALE_SMOOTH);
							lblImage.setIcon(new ImageIcon(img));
							lblImage.setText("");
						}
					}
				});

				Category selectedCategory = (Category) cbCategory.getSelectedItem();
				if (selectedCategory != null) {
					p.setIdCategory(selectedCategory.getId());
				}

				Type selectedType = (Type) cbType.getSelectedItem();
				if (selectedType != null) {
					p.setIdType(selectedType.getId());
				}

//				Status selectedStatus = (Status) cbStatus.getSelectedItem();
//				if (selectedStatus != null) {
//					p.setIdStatus(selectedStatus.getId());
//				}

				boolean success = model.create(p, 0);

				if (success) {
					JOptionPane.showMessageDialog(this, "Product added successfully!");
					if (parentStockIn != null) {
						parentStockIn.refreshProductsAndAddRow(p, 0);
					}
					if (parentProducts != null) {
						parentProducts.refreshProducts();
					}
					if (dialog != null) {
						dialog.dispose();
					}
				}

				else {
					JOptionPane.showMessageDialog(this, "Failed to add product.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

//		txtStock.getDocument().addDocumentListener(new SimpleDocListener(() -> calculateTotal()));
//		txtPrice.getDocument().addDocumentListener(new SimpleDocListener(() -> calculateTotal()));
	}

	private JTextField createTextField(JPanel panel, GridBagConstraints gbc, String label, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		panel.add(new JLabel(label), gbc);
		gbc.gridx = 1;
		JTextField field = new JTextField(15);
		panel.add(field, gbc);
		return field;
	}

	private void handleAddProduct() {
		if (!validateInput(false))
			return;

		InventoryActivityModel model = new InventoryActivityModel();
		if (model.existsByCodeOrTitle(getProductCode(), getProductName())) {
			JOptionPane.showMessageDialog(this, "Product code or name already exists.", "Duplicate Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Product p = buildProductFromForm();
		boolean success = model.create(p, 0);

		if (success) {
			JOptionPane.showMessageDialog(this, "Product added successfully!");
			if (parentProducts != null)
				parentProducts.refreshProducts();
			if (dialog != null)
				dialog.dispose();
		}
	}

	private Product buildProductFromForm() {
		Product p = new Product();

		p.setCode(getProductCode());
		p.setTitle(getProductName());
		p.setPrice(BigDecimal.valueOf(getProductPriceLong()));
		p.setQuantity(0); // mặc định khi thêm mới

		// Gán các liên kết
		Supplier supplier = (Supplier) cbSupplier.getSelectedItem();
		Category category = (Category) cbCategory.getSelectedItem();
		Type type = (Type) cbType.getSelectedItem();

		if (supplier != null)
			p.setIdSupplier(supplier.getId());
		if (category != null)
			p.setIdCategory(category.getId());
		if (type != null)
			p.setIdType(type.getId());

		// Gán thông tin hệ thống
		p.setIdStatus(6); // trạng thái mặc định
		p.setIsPublic(1); // hiển thị mặc định
		p.setIdCreator("1f05aa86-d6c8-11f0-b2b8-2c8db1d70194");
		p.setIdUpdater("1f05aa86-d6c8-11f0-b2b8-2c8db1d70194");
		p.setCreatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
		p.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));

		// Gán ảnh nếu có
		if (selectedImageFile != null) {
			try {
				byte[] imageBytes = java.nio.file.Files.readAllBytes(selectedImageFile.toPath());
				p.setLink(imageBytes);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return p;
	}

	private void handleUpdateProduct(Product product) {
		if (!validateInput(true))
			return;

		ProductModel model = new ProductModel();

		if (model.existsByTitleExcludingId(getProductName(), product.getId())) {
			JOptionPane.showMessageDialog(this, "Product name already exists in database.", "Duplicate Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		product.setTitle(getProductName());
		product.setPrice(BigDecimal.valueOf(getProductPriceLong()));
		product.setIdSupplier(((Supplier) cbSupplier.getSelectedItem()).getId());
		product.setIdCategory(((Category) cbCategory.getSelectedItem()).getId());
		product.setIdType(((Type) cbType.getSelectedItem()).getId());
		product.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));

		if (selectedImageFile != null) {
			try {
				product.setLink(java.nio.file.Files.readAllBytes(selectedImageFile.toPath()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		boolean success = model.updateAuto(product);

		if (success) {
			JOptionPane.showMessageDialog(this, "Product updated successfully!");
			if (parentProducts != null)
				parentProducts.refreshProducts();
			if (dialog != null)
				dialog.dispose();
		}
	}

	private boolean validateInput(boolean isUpdate) {
		Supplier selectedSupplier = (Supplier) cbSupplier.getSelectedItem();
		if (selectedSupplier == null || selectedSupplier.getId() == 0) {
			JOptionPane.showMessageDialog(this, "Supplier must be selected.");
			return false;
		}

		if (getProductName().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Name cannot be empty.");
			return false;
		}

		String pVal = txtPrice.getText().replaceAll("\\D", "");
		if (pVal.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Price cannot be empty.");
			return false;
		}

		long price = getProductPriceLong();
		if (price < 1000 || price > 1_000_000_000) {
			JOptionPane.showMessageDialog(this, "Price must be between 1.000 VND and 1.000.000.000 VND",
					"Invalid Price", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		Category selectedCategory = (Category) cbCategory.getSelectedItem();
		if (selectedCategory == null || selectedCategory.getId() == 0) {
			JOptionPane.showMessageDialog(this, "Category must be selected.");
			return false;
		}

		Type selectedType = (Type) cbType.getSelectedItem();
		if (selectedType == null || selectedType.getId() == 0) {
			JOptionPane.showMessageDialog(this, "Type must be selected.");
			return false;
		}

		if (!isUpdate) {
			if (selectedImageFile == null) {
				JOptionPane.showMessageDialog(this, "You must select an image.");
				return false;
			}
		}

		return true;
	}

	public void calculateTotal() {
		try {
			long price = getProductPriceLong();
			long total = 0;
		} catch (Exception e) {
		}
	}

	public String getProductCode() {
		return txtCode.getText().trim();
	}

	public String getProductName() {
		return txtName.getText().trim();
	}

	public int getProductStock() {
		return 0;
	}

	public long getProductPriceLong() {
		String val = txtPrice.getText().replaceAll("\\D", "");
		return val.isEmpty() ? 0L : Long.parseLong(val);
	}

	public double getProductPrice() {
		return (double) getProductPriceLong();
	}

	public long getProductTotalLong() {
		return 0;
	}

	public double getProductTotal() {
		return (double) getProductTotalLong();
	}

	public String getProductSupplier() {
		Supplier s = (Supplier) cbSupplier.getSelectedItem();
		return s != null ? s.getName() : "";
	}

	static class QuantityFormatFilter extends DocumentFilter {
		private final NumberFormat nf = NumberFormat.getIntegerInstance(new Locale("vi", "VN"));

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {
			if (string.matches("\\d+")) {
				super.insertString(fb, offset, string, attr);
				format(fb);
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			if (text.matches("\\d*")) {
				super.replace(fb, offset, length, text, attrs);
				format(fb);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			super.remove(fb, offset, length);
			format(fb);
		}

		private void format(FilterBypass fb) throws BadLocationException {
			String raw = fb.getDocument().getText(0, fb.getDocument().getLength()).replaceAll("\\D", "");
			if (!raw.isEmpty()) {
				if (raw.length() > 6) {
					JOptionPane.showMessageDialog(null, "Maximum number of digits 6", "Input error",
							JOptionPane.ERROR_MESSAGE);
					raw = raw.substring(0, 6);
				}
				String formatted = nf.format(Long.parseLong(raw));
				fb.replace(0, fb.getDocument().getLength(), formatted, null);
			}
		}
	}

	static class PriceFormatFilter extends DocumentFilter {
		private final NumberFormat nf = NumberFormat.getIntegerInstance(new Locale("vi", "VN"));

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {
			if (string.matches("\\d+")) {
				super.insertString(fb, offset, string, attr);
				format(fb);
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			if (text.matches("\\d*")) {
				super.replace(fb, offset, length, text, attrs);
				format(fb);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			super.remove(fb, offset, length);
			format(fb);
		}

		private void format(FilterBypass fb) throws BadLocationException {
			String raw = fb.getDocument().getText(0, fb.getDocument().getLength()).replaceAll("\\D", "");
			if (!raw.isEmpty()) {
				if (raw.length() > 9) {
					JOptionPane.showMessageDialog(null, "Maximum price is 9 digits.", "Input error",
							JOptionPane.ERROR_MESSAGE);
					raw = raw.substring(0, 9);
				}
				String formatted = nf.format(Long.parseLong(raw));
				fb.replace(0, fb.getDocument().getLength(), formatted, null);
			}
		}
	}

	static class SimpleDocListener implements DocumentListener {
		private final Runnable callback;

		public SimpleDocListener(Runnable callback) {
			this.callback = callback;
		}

		public void insertUpdate(DocumentEvent e) {
			callback.run();
		}

		public void removeUpdate(DocumentEvent e) {
			callback.run();
		}

		public void changedUpdate(DocumentEvent e) {
			callback.run();
		}
	}

	public Type getSelectedType() {
		return (Type) cbType.getSelectedItem();
	}

	public String getProductType() {
		Type t = (Type) cbType.getSelectedItem();
		return t != null ? t.getTitle() : "";
	}

//	public Status getSelectedStatus() {
//		return (Status) cbStatus.getSelectedItem();
//	}
//
//	public String getProductStatus() {
//		Status s = (Status) cbStatus.getSelectedItem();
//		return s != null ? s.getTitle() : "";
//	}

	public Category getSelectedCategory() {
		return (Category) cbCategory.getSelectedItem();
	}

	public String getProductCategory() {
		Category cat = (Category) cbCategory.getSelectedItem();
		return cat != null ? cat.getTitle() : "";
	}

	public static String removeDiacritics(String input) {
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(normalized).replaceAll("");
	}

	private void clearForm() {
		txtCode.setText("");
		txtName.setText("");
		txtPrice.setText("");

		if (cbSupplier.getItemCount() > 0)
			cbSupplier.setSelectedIndex(0);
		if (cbCategory.getItemCount() > 0)
			cbCategory.setSelectedIndex(0);
		if (cbType.getItemCount() > 0)
			cbType.setSelectedIndex(0);
		if (cbStatus.getItemCount() > 0)
			cbStatus.setSelectedIndex(0);

		selectedImageFile = null;
		lblImage.setIcon(null);
		lblImage.setText("");
	}

	private void initUI() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(650, 550));

		lblTitle = new JLabel("Add Product", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(60, 60, 60));
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setOpaque(true);
		add(lblTitle, BorderLayout.NORTH);
	}

	public void refreshSuppliers() {
		cbSupplier.removeAllItems();
		SupplierModel supplierModel = new SupplierModel();
		java.util.List<Supplier> suppliers = supplierModel.findAll();

		cbSupplier.addItem(new Supplier(0, "-- Select Supplier --"));
		if (suppliers != null) {
			for (Supplier s : suppliers) {
				cbSupplier.addItem(s);
			}
		}
		cbSupplier.setSelectedIndex(0);
	}

	public JPanelAddProduct(JPanelStockIn1 parentStockIn, JPanelProducts parentProducts, JDialog dialog,
			Product product) {
		this(parentStockIn, parentProducts, dialog);

		btnAdd.setText("Update Product");
		lblTitle.setText("Update Product");

		// render dữ liệu
		txtCode.setText(product.getCode());
		txtName.setText(product.getTitle());
		txtPrice.setText(product.getPrice() != null ? product.getPrice().toString() : "");
		cbSupplier.setSelectedItem(findSupplierById(product.getIdSupplier()));
		cbCategory.setSelectedItem(findCategoryById(product.getIdCategory()));
		cbType.setSelectedItem(findTypeById(product.getIdType()));

		if (product.getLink() != null) {
			ImageIcon icon = new ImageIcon(product.getLink());
			Image img = icon.getImage().getScaledInstance(250, 200, Image.SCALE_SMOOTH);
			lblImage.setIcon(new ImageIcon(img));

			// Gán selectedImageFile để validateInput không báo lỗi
			try {
				// Tạo file tạm từ byte[] nếu cần, hoặc đơn giản bỏ qua validate ảnh khi update
				selectedImageFile = null; // hoặc giữ nguyên null vì update không bắt buộc ảnh
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// gắn nút Update
		for (ActionListener al : btnAdd.getActionListeners()) {
			btnAdd.removeActionListener(al); // XÓA listener thêm mới
		}
		btnAdd.addActionListener(e -> handleUpdateProduct(product));

	}

	private Supplier findSupplierById(int id) {
		for (int i = 0; i < cbSupplier.getItemCount(); i++) {
			Supplier s = cbSupplier.getItemAt(i);
			if (s.getId() == id)
				return s;
		}
		return null;
	}

	private Category findCategoryById(int id) {
		for (int i = 0; i < cbCategory.getItemCount(); i++) {
			Category c = cbCategory.getItemAt(i);
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	private Type findTypeById(int id) {
		for (int i = 0; i < cbType.getItemCount(); i++) {
			Type t = cbType.getItemAt(i);
			if (t.getId() == id)
				return t;
		}
		return null;
	}

}
