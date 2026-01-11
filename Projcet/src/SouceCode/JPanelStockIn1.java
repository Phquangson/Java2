package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import entities.Product;

import models.InventoryActivityModel;
import entities.Inventory;
import entities.InventoryActivity;

public class JPanelStockIn1 extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableProducts;
	private JLabel lblTotalAmount;
	private byte[] selectedImageBytes;

	public JPanelStockIn1() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);

		JPanel panelHeader = new JPanel(new BorderLayout());
		panelHeader.setBackground(Color.WHITE);

		JLabel lblStockIn = new JLabel("Stock In", SwingConstants.CENTER);
		lblStockIn.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblStockIn.setForeground(Color.BLACK);
		lblStockIn.setOpaque(true);
		lblStockIn.setBackground(new Color(247, 222, 155));
		lblStockIn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		panelHeader.add(lblStockIn, BorderLayout.NORTH);

		final Point dragPoint = new Point();
		lblStockIn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				dragPoint.x = e.getX();
				dragPoint.y = e.getY();
			}
		});
		lblStockIn.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				Window w = SwingUtilities.getWindowAncestor(JPanelStockIn1.this);
				if (w != null) {
					Point p = w.getLocation();
					w.setLocation(p.x + e.getX() - dragPoint.x, p.y + e.getY() - dragPoint.y);
				}
			}
		});

		JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panelTop.setBackground(Color.WHITE);

		JLabel lblDate = new JLabel("Created date:");
		lblDate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setDateFormatString("dd-MM-yyyy");
		dateChooser.setDate(new Date());
		dateChooser.setEnabled(false);
		dateChooser.setPreferredSize(new Dimension(150, 25));
		panelTop.add(lblDate);
		panelTop.add(dateChooser);

		JLabel lblIssueCode = new JLabel("Code:");
		lblIssueCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JTextField txtIssueCode = new JTextField(10);
		txtIssueCode.setEditable(false);
		txtIssueCode.setEnabled(false);
		InventoryActivityModel model = new InventoryActivityModel();
		txtIssueCode.setText(model.generateNextIssueCode());
		panelTop.add(lblIssueCode);
		panelTop.add(txtIssueCode);

		List<Inventory> inventories = model.findAllProducts();

		JComboBox<Object> comboProducts = new JComboBox<>();
		comboProducts.setPreferredSize(new Dimension(210, 35));

		JTextField txtSearchProduct = new JTextField(10);
		panelTop.add(new JLabel("Search:"));
		panelTop.add(txtSearchProduct);

		txtSearchProduct.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void search() {
				String keyword = txtSearchProduct.getText().trim();
				InventoryActivityModel model = new InventoryActivityModel();
				List<Inventory> result;

				if (keyword.isEmpty()) {
					result = model.findAllProducts();
				} else {
					result = model.productSearch(keyword);
				}

				comboProducts.removeAllItems();
				comboProducts.addItem("-- Select product --");
				if (result != null) {
					for (Inventory inv : result) {
						comboProducts.addItem(inv);
					}
				}
			}

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				search();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				search();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				search();
			}
		});

		JLabel lblProduct = new JLabel("Products:");
		lblProduct.setFont(new Font("Tahoma", Font.PLAIN, 14));

		comboProducts.addItem("-- Select product --");

		for (Inventory inv : inventories) {
			Product p = inv.getProduct();
			if (p.getIsPublic() == 1) {
				comboProducts.addItem(inv);
			}
		}

		comboProducts.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value instanceof Inventory) {
					Inventory inv = (Inventory) value;
					Product p = inv.getProduct();

					// Format số thập phân cho tồn kho
					DecimalFormat df = new DecimalFormat("#.##"); // hiển thị tối đa 2 số thập phân
					String stockFormatted = df.format(inv.getStock());

					String displayText = "<html><div style='line-height:1.3;'>" + "<b>" + p.getCode() + "</b> - "
							+ p.getTitle() + "<br/>" + "Giá: "
							+ NumberFormat.getInstance(new Locale("vi", "VN")).format(p.getPrice()) + " VND"
							+ " | Tồn kho: <b>" + stockFormatted + "</b>" + "</div></html>";

					setText(displayText);
					setToolTipText(p.getCode() + " - " + p.getTitle() + " | Giá: "
							+ NumberFormat.getInstance(new Locale("vi", "VN")).format(p.getPrice()) + " VND"
							+ " | Tồn kho: " + stockFormatted);

					if (inv.getStock().doubleValue() == 0.0) {
						setForeground(Color.RED);
					} else {
						setForeground(isSelected ? list.getSelectionForeground() : Color.BLACK);
					}
				} else {
					String text = value != null ? value.toString() : "";
					setText(text);
					setToolTipText(text);
				}
				return this;
			}
		});

		comboProducts.setSelectedIndex(0);

		panelTop.add(lblProduct);
		panelTop.add(comboProducts);

		JLabel lblQuantity = new JLabel("Quantity:");
		lblQuantity.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JSpinner spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
		spinnerQuantity.setPreferredSize(new Dimension(100, 25));

		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerQuantity, "#");
		spinnerQuantity.setEditor(editor);

		JFormattedTextField txt = editor.getTextField();
		((javax.swing.text.NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

		((javax.swing.text.NumberFormatter) txt.getFormatter()).setCommitsOnValidEdit(true);

		panelTop.add(lblQuantity);
		panelTop.add(spinnerQuantity);

		JButton btnAdd = new JButton("Add");
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setBackground(new Color(250, 172, 104));
		btnAdd.setFont(new Font("SansSerif", Font.BOLD, 14));
		panelTop.add(btnAdd);
		btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton btnOther = new JButton("Other products");
		btnOther.setForeground(Color.WHITE);
		btnOther.setBackground(new Color(250, 172, 104));
		btnOther.setFont(new Font("SansSerif", Font.BOLD, 14));
		panelTop.add(btnOther);
		btnOther.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		btnOther.addActionListener(e -> {
			Window window = SwingUtilities.getWindowAncestor(JPanelStockIn1.this);
			JDialog dialog = new JDialog((Frame) null, "Add Product", true);
			JPanelAddProduct addProductPanel = new JPanelAddProduct(this, null, dialog);
			dialog.getContentPane().add(addProductPanel);
			dialog.pack();
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
		});

		panelHeader.add(panelTop, BorderLayout.CENTER);

		add(panelHeader, BorderLayout.NORTH);
		JLabel lblImage = new JLabel();
		lblImage.setPreferredSize(new Dimension(60, 60));
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		lblImage.setVerticalAlignment(SwingConstants.CENTER);
		lblImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		lblImage.setOpaque(true);
		lblImage.setBackground(new Color(250, 250, 250));
		lblImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		ImageIcon defaultIcon = new ImageIcon(getClass().getResource("/resources/img-push.png"));
		Image scaled = defaultIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		lblImage.setIcon(new ImageIcon(scaled));

		lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showOpenDialog(JPanelStockIn1.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedImageFile = fileChooser.getSelectedFile();
					long fileSizeInBytes = selectedImageFile.length();
					double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);

					if (fileSizeInMB > 2.0) {
						JOptionPane.showMessageDialog(JPanelStockIn1.this,
								"Image is too large (" + String.format("%.2f", fileSizeInMB)
										+ " MB). Please select an image under 2MB.",
								"File too large", JOptionPane.ERROR_MESSAGE);
						return;
					}

					ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
					Image img = icon.getImage().getScaledInstance(lblImage.getWidth(), lblImage.getHeight(),
							Image.SCALE_SMOOTH);
					lblImage.setIcon(new ImageIcon(img));

					// đọc file thành byte[]
					try (java.io.FileInputStream fis = new java.io.FileInputStream(selectedImageFile)) {
						selectedImageBytes = fis.readAllBytes();
					} catch (Exception ex) {
						ex.printStackTrace();
						selectedImageBytes = null;
					}
				}
			}
		});

		panelTop.add(lblImage);

		String[] columnNames = { "No.", "Name", "Quantity", "Unit price", "Total amount", "Update", "Delete" };
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
		tableProducts = new JTable(tableModel);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		tableProducts.setRowSorter(sorter);
		tableProducts.setDefaultEditor(Object.class, null);
		tableProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		tableProducts.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				tableProducts.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});

		tableProducts.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				tableProducts.getTableHeader().setCursor(Cursor.getDefaultCursor());
			}
		});

		sorter.setComparator(0, (o1, o2) -> {
			return Integer.compare(Integer.parseInt(o1.toString()), Integer.parseInt(o2.toString()));
		});

		sorter.setComparator(2, (o1, o2) -> {
			return Integer.compare(Integer.parseInt(o1.toString()), Integer.parseInt(o2.toString()));
		});

		sorter.setComparator(3, (o1, o2) -> {
			long v1 = Long.parseLong(o1.toString().replaceAll("[^0-9]", ""));
			long v2 = Long.parseLong(o2.toString().replaceAll("[^0-9]", ""));
			return Long.compare(v1, v2);
		});

		sorter.setComparator(4, (o1, o2) -> {
			long v1 = Long.parseLong(o1.toString().replaceAll("[^0-9]", ""));
			long v2 = Long.parseLong(o2.toString().replaceAll("[^0-9]", ""));
			return Long.compare(v1, v2);
		});

		tableProducts.setRowHeight(50);
		tableProducts.setIntercellSpacing(new Dimension(0, 10));

		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 13));
		tableProducts.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

		JScrollPane scrollPane = new JScrollPane(tableProducts);
		scrollPane.setPreferredSize(new Dimension(850, 400));
		add(scrollPane, BorderLayout.CENTER);

		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		panelBottom.setBackground(Color.WHITE);

		JLabel lblTotal = new JLabel("Total:");
		lblTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
		panelBottom.add(lblTotal);

		lblTotalAmount = new JLabel("0 VND");
		lblTotalAmount.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTotalAmount.setForeground(Color.RED);
		panelBottom.add(lblTotalAmount);

		JButton btnStockOut = new JButton("Stock in");
		btnStockOut.setMinimumSize(new Dimension(100, 23));
		btnStockOut.setMaximumSize(new Dimension(100, 23));
		btnStockOut.setBackground(new Color(90, 156, 181));
		btnStockOut.setForeground(Color.WHITE);
		btnStockOut.setFont(new Font("SansSerif", Font.BOLD, 22));
		panelBottom.add(btnStockOut);
		btnStockOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton btnClose = new JButton("Close");
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 22));
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelBottom.add(btnClose);

		btnClose.addActionListener(e -> {
			Window window = SwingUtilities.getWindowAncestor(JPanelStockIn1.this);
			if (window != null) {
				window.dispose();
			}
		});

		add(panelBottom, BorderLayout.SOUTH);

		btnAdd.addActionListener(e -> {
			Object selectedObj = comboProducts.getSelectedItem();
			if (selectedObj == null || selectedObj instanceof String) {
				JOptionPane.showMessageDialog(this, "Please select a product!");
				return;
			}
			Inventory selectedInv = (Inventory) selectedObj;

			String productCode = selectedInv.getProduct().getCode();
			String productName = selectedInv.getProduct().getTitle();
			int quantity = (Integer) spinnerQuantity.getValue();
			long price = selectedInv.getProduct().getPrice().longValue();
			long total = (long) quantity * price;

			boolean found = false;
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				String existingCode = tableModel.getValueAt(i, 1).toString();
				if (existingCode.equals(productCode)) {
					int oldQuantity = (Integer) tableModel.getValueAt(i, 2);
					int newQuantity = oldQuantity + quantity;
					long newTotal = (long) newQuantity * price;

					tableModel.setValueAt(newQuantity, i, 2);
					tableModel.setValueAt(NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " VND", i,
							3);
					tableModel.setValueAt(NumberFormat.getInstance(new Locale("vi", "VN")).format(newTotal) + " VND", i,
							4);

					found = true;
					break;
				}
			}

			if (!found) {
				int no = tableModel.getRowCount() + 1;
				tableModel.addRow(new Object[] { no, productCode, quantity,
						NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " VND",
						NumberFormat.getInstance(new Locale("vi", "VN")).format(total) + " VND" });
			}

			long sum = 0;
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				String val = tableModel.getValueAt(i, 4).toString().replaceAll("[^0-9]", "");
				if (!val.isEmpty())
					sum += Long.parseLong(val);
			}
			lblTotalAmount.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(sum) + " VND");
		});

		class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
			public ButtonRenderer(Icon icon) {
				setIcon(icon);
				setOpaque(false);
				setContentAreaFilled(false);
				setBorderPainted(false);
			}

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				return this;
			}
		}

		btnStockOut.addActionListener(e -> {
			DefaultTableModel modelTable = (DefaultTableModel) tableProducts.getModel();
			InventoryActivityModel invModel = new InventoryActivityModel();

			if (modelTable.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "No products to stock in!", "Notice", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String receiptCode = invModel.generateNextIssueCode();
			String creator = "warehouse";

			for (int i = 0; i < modelTable.getRowCount(); i++) {
				String productCode = modelTable.getValueAt(i, 1).toString();
				int quantity = Integer.parseInt(modelTable.getValueAt(i, 2).toString());

				InventoryActivity activity = invModel.stockInProduct(productCode, quantity, receiptCode, creator,
						selectedImageBytes);

				if (activity != null) {
					System.out.println("Stocked: " + activity.getDescription());
				} else {
					JOptionPane.showMessageDialog(this, "Unable to stock in product: " + productCode, "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			JOptionPane.showMessageDialog(this, "Stock In successful");

			modelTable.setRowCount(0);
			lblTotalAmount.setText("0 VND");

			Window window = SwingUtilities.getWindowAncestor(JPanelStockIn1.this);
			if (window != null) {
				window.dispose();
			}
		});

		class ButtonEditor extends DefaultCellEditor {
			private JButton button;
			private String actionType;
			private DefaultTableModel model;
			private JTable table;
			private int row;

			public ButtonEditor(JCheckBox checkBox, String actionType, DefaultTableModel model, JTable table,
					Icon icon) {
				super(checkBox);
				this.actionType = actionType;
				this.model = model;
				this.table = table;
				button = new JButton();
				button.setIcon(icon);
				button.setOpaque(false);
				button.setContentAreaFilled(false);
				button.setBorderPainted(false);

				button.addActionListener(e -> {
					fireEditingStopped();
					if ("Update".equals(actionType)) {
						JTextField txtField = new JTextField(15);
						((AbstractDocument) txtField.getDocument())
								.setDocumentFilter(new javax.swing.text.DocumentFilter() {
									@Override
									public void insertString(FilterBypass fb, int offset, String string,
											AttributeSet attr) throws BadLocationException {
										if (string.matches("\\d+"))
											super.insertString(fb, offset, string, attr);
									}

									@Override
									public void replace(FilterBypass fb, int offset, int length, String text,
											AttributeSet attrs) throws BadLocationException {
										if (text.matches("\\d+"))
											super.replace(fb, offset, length, text, attrs);
									}
								});

						JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
						panelInput.add(new JLabel("Quantity:"));
						panelInput.add(txtField);

						int option = JOptionPane.showConfirmDialog(table, panelInput, "New quantity",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

						if (option == JOptionPane.OK_OPTION) {
							try {
								int newQty = Integer.parseInt(txtField.getText());
								if (newQty <= 0) {
									JOptionPane.showMessageDialog(table, "Right quantity > 0!");
									return;
								}
								long price = Long
										.parseLong(model.getValueAt(row, 3).toString().replaceAll("[^0-9]", ""));
								long newTotal = (long) newQty * price;

								model.setValueAt(newQty, row, 2);
								model.setValueAt(
										NumberFormat.getInstance(new Locale("vi", "VN")).format(newTotal) + " VND", row,
										4);

								long sum = 0;
								for (int i = 0; i < model.getRowCount(); i++) {
									String val = model.getValueAt(i, 4).toString().replaceAll("[^0-9]", "");
									if (!val.isEmpty())
										sum += Long.parseLong(val);
								}
								lblTotalAmount
										.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(sum) + " VND");

							} catch (Exception ex) {
								JOptionPane.showMessageDialog(table, "Invalid number");
							}
						}
					} else if ("Delete".equals(actionType)) {
						int confirm = JOptionPane.showConfirmDialog(table,
								"Are you sure you want to delete this product?", "Confirm deletion",
								JOptionPane.YES_NO_OPTION);
						if (confirm == JOptionPane.YES_OPTION) {
							model.removeRow(row);
							for (int i = 0; i < model.getRowCount(); i++) {
								model.setValueAt(i + 1, i, 0);
							}
							long sum = 0;
							for (int i = 0; i < model.getRowCount(); i++) {
								String val = model.getValueAt(i, 4).toString().replaceAll("[^0-9]", "");
								if (!val.isEmpty())
									sum += Long.parseLong(val);
							}
							lblTotalAmount
									.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(sum) + " VND");

						}
					}
				});

			}

			@Override
			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
					int column) {
				this.row = row;
				return button;
			}

			@Override
			public Object getCellEditorValue() {
				return actionType;
			}
		}

		Icon updateIcon = new ImageIcon(getClass().getResource("/resources/icon-edit.png"));
		Icon deleteIcon = new ImageIcon(getClass().getResource("/resources/icon-trash-32.png"));

		tableProducts.getColumn("Update").setCellRenderer(new ButtonRenderer(updateIcon));
		tableProducts.getColumn("Update")
				.setCellEditor(new ButtonEditor(new JCheckBox(), "Update", tableModel, tableProducts, updateIcon));

		tableProducts.getColumn("Delete").setCellRenderer(new ButtonRenderer(deleteIcon));
		tableProducts.getColumn("Delete")
				.setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", tableModel, tableProducts, deleteIcon));

	}

	public void refreshProductsAndAddRow(Product p, int stock) {
		InventoryActivityModel model = new InventoryActivityModel();
		List<Inventory> inventories = model.findAllProducts();

		JComboBox<Object> comboProducts = null;
		for (Component comp : ((JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH))
				.getComponents()) {
			if (comp instanceof JPanel) {
				for (Component inner : ((JPanel) comp).getComponents()) {
					if (inner instanceof JComboBox) {
						comboProducts = (JComboBox<Object>) inner;
						break;
					}
				}
			}
		}

		if (comboProducts != null) {
			comboProducts.removeAllItems();
			comboProducts.addItem("-- Select product --");
			for (Inventory inv : inventories) {
				if (p.getIsPublic() == 1) {
					comboProducts.addItem(inv);
				}
			}

			comboProducts.setSelectedIndex(0);
		}

		long price = p.getPrice().longValue();
		long total = (long) stock * price;
		DefaultTableModel modelTable = (DefaultTableModel) tableProducts.getModel();
		int no = modelTable.getRowCount() + 1;
		modelTable.addRow(new Object[] { no, p.getTitle(), stock,
				NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " VND",
				NumberFormat.getInstance(new Locale("vi", "VN")).format(total) + " VND" });

		long sum = 0;
		for (int i = 0; i < modelTable.getRowCount(); i++) {
			String val = modelTable.getValueAt(i, 4).toString().replaceAll("[^0-9]", "");
			if (!val.isEmpty()) {
				sum += Long.parseLong(val);
			}
		}
		lblTotalAmount.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(sum) + " VND");
	}

}
