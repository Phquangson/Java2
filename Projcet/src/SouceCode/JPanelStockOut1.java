package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.toedter.calendar.JDateChooser;
import entities.Inventory;
import entities.InventoryActivity;
import entities.Product;
import models.InventoryActivityModel;

public class JPanelStockOut1 extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableProducts;
	private JLabel lblTotalAmount;
	private Runnable onStockOutSuccess;

	public JPanelStockOut1(Runnable onStockOutSuccess) {
		this.onStockOutSuccess = onStockOutSuccess;
		setLayout(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);

		// Header
		JPanel panelHeader = new JPanel(new BorderLayout());
		panelHeader.setBackground(Color.WHITE);

		JLabel lblTitle = new JLabel("Stock Out", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(Color.BLACK);
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
		panelHeader.add(lblTitle, BorderLayout.NORTH);

		// Top controls
		JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panelTop.setBackground(Color.WHITE);

		panelTop.add(new JLabel("Created date:"));
		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setDateFormatString("dd-MM-yyyy");
		dateChooser.setDate(new Date());
		dateChooser.setEnabled(false);
		dateChooser.setPreferredSize(new Dimension(150, 25));
		panelTop.add(dateChooser);

		panelTop.add(new JLabel("Code:"));
		JTextField txtCode = new JTextField(10);
		txtCode.setEditable(false);
		txtCode.setEnabled(false);
		txtCode.setText(new InventoryActivityModel().generateNextReceiptCode());
		panelTop.add(txtCode);

		// Thêm search trước Quantity
		panelTop.add(new JLabel("Search product:"));
		JTextField txtSearch = new JTextField(10);
		panelTop.add(txtSearch);

		// ComboBox sản phẩm
		panelTop.add(new JLabel("Products:"));
		JComboBox<Object> comboProducts = new JComboBox<>();
		comboProducts.setPreferredSize(new Dimension(210, 35));
		comboProducts.addItem("-- Select product --");
		List<Inventory> inventories = new InventoryActivityModel().findAllProducts();
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

					DecimalFormat df = new DecimalFormat("#.##");
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

		panelTop.add(comboProducts);

		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void filterProducts() {
				String keyword = txtSearch.getText().trim().toLowerCase();
				comboProducts.removeAllItems();
				comboProducts.addItem("-- Select product --");

				InventoryActivityModel model = new InventoryActivityModel();

				InventoryActivity act = model.findByCodeStockOut(keyword.toUpperCase());
				if (act != null) {
					Inventory inv = new Inventory();
					Product p = new Product();
					p.setCode(act.getCode());
					p.setTitle("StockOut by " + act.getIdCreator());
					p.setPrice(act.getTotalCost());
					inv.setProduct(p);
					inv.setStock(BigDecimal.ZERO);
					comboProducts.addItem(inv);
					return;
				}

				for (Inventory inv : inventories) {
					Product p = inv.getProduct();
					String title = p.getTitle().toLowerCase();
					String code = p.getCode().toLowerCase();
					if ((title.contains(keyword) || code.contains(keyword)) && p.getIsPublic() == 1) {
						comboProducts.addItem(inv);
					}
				}

			}

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				filterProducts();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				filterProducts();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				filterProducts();
			}
		});

		panelTop.add(new JLabel("Quantity:"));
		JSpinner spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
		spinnerQuantity.setPreferredSize(new Dimension(100, 25));
		panelTop.add(spinnerQuantity);

		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerQuantity, "#");
		spinnerQuantity.setEditor(editor);

		JFormattedTextField txt = editor.getTextField();
		((javax.swing.text.NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		((javax.swing.text.NumberFormatter) txt.getFormatter()).setCommitsOnValidEdit(true);

		JButton btnAdd = new JButton("Add");
		btnAdd.setBackground(Color.GRAY);
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFont(new Font("SansSerif", Font.BOLD, 14));
		panelTop.add(btnAdd);

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
				int result = fileChooser.showOpenDialog(JPanelStockOut1.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedImageFile = fileChooser.getSelectedFile();
					long fileSizeInBytes = selectedImageFile.length();
					double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);

					if (fileSizeInMB > 2.0) {
						JOptionPane.showMessageDialog(JPanelStockOut1.this,
								"Image is too large (" + String.format("%.2f", fileSizeInMB)
										+ " MB). Please select an image under 2MB.",
								"File too large", JOptionPane.ERROR_MESSAGE);
						return;
					}

					ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
					Image img = icon.getImage().getScaledInstance(lblImage.getWidth(), lblImage.getHeight(),
							Image.SCALE_SMOOTH);
					lblImage.setIcon(new ImageIcon(img));
				}
			}
		});

		panelTop.add(lblImage);

		// Table
		String[] columnNames = { "No.", "Name", "Quantity", "Unit price", "Total amount", "Update", "Delete" };
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
		tableProducts = new JTable(tableModel);

		tableProducts.setRowHeight(50);
		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 13));
		tableProducts.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
		JScrollPane scrollPane = new JScrollPane(tableProducts);
		scrollPane.setPreferredSize(new Dimension(850, 400));
		add(scrollPane, BorderLayout.CENTER);

		// Bottom
		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		panelBottom.setBackground(Color.WHITE);

		JLabel label = new JLabel("Total:");
		label.setFont(new Font("SansSerif", Font.BOLD, 22));
		panelBottom.add(label);
		lblTotalAmount = new JLabel("0 VND");
		lblTotalAmount.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTotalAmount.setForeground(Color.RED);
		panelBottom.add(lblTotalAmount);

		JButton btnStockOut = new JButton("Stock out");
		btnStockOut.setBackground(new Color(90, 156, 181));
		btnStockOut.setForeground(Color.WHITE);
		btnStockOut.setFont(new Font("SansSerif", Font.BOLD, 22));
		panelBottom.add(btnStockOut);

		JButton btnClose = new JButton("Close");
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 22));
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelBottom.add(btnClose);

		btnClose.addActionListener(e -> {
			Window window = SwingUtilities.getWindowAncestor(JPanelStockOut1.this);
			if (window != null) {
				window.dispose();
			}
		});

		add(panelBottom, BorderLayout.SOUTH);

		// btn add
		btnAdd.addActionListener(e -> {
			Object selectedObj = comboProducts.getSelectedItem();
			if (selectedObj == null || selectedObj instanceof String) {
				JOptionPane.showMessageDialog(this, "Please select a product!");
				return;
			}
			Inventory selectedInv = (Inventory) selectedObj;
			int quantity = (Integer) spinnerQuantity.getValue();
			int stock = selectedInv.getStock().intValue();

			if (quantity > stock) {
				JOptionPane.showMessageDialog(this, "Quantity exceeds stock! Only " + stock + " products left.");
				return;
			}

			long price = selectedInv.getProduct().getPrice().longValue();
			boolean found = false;

			for (int i = 0; i < tableModel.getRowCount(); i++) {
				String existingCode = tableModel.getValueAt(i, 1).toString();
				if (existingCode.equals(selectedInv.getProduct().getCode())) {
					int oldQuantity = (Integer) tableModel.getValueAt(i, 2);
					int newQuantity = oldQuantity + quantity;

					if (newQuantity > stock) {
						JOptionPane.showMessageDialog(this,
								"Total quantity exceeds stock! Only " + stock + " products left.");
						return;
					}

					long newTotal = newQuantity * price;
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
				long total = quantity * price;
				tableModel.addRow(new Object[] { no, selectedInv.getProduct().getCode(), quantity,
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

		Icon updateIcon = new ImageIcon(getClass().getResource("/resources/icon-edit.png"));
		Icon deleteIcon = new ImageIcon(getClass().getResource("/resources/icon-trash-32.png"));

		tableProducts.getColumn("Update").setCellRenderer(new ButtonRenderer(updateIcon));
		tableProducts.getColumn("Update")
				.setCellEditor(new ButtonEditor(new JCheckBox(), "Update", tableModel, tableProducts, updateIcon));

		tableProducts.getColumn("Delete").setCellRenderer(new ButtonRenderer(deleteIcon));
		tableProducts.getColumn("Delete")
				.setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", tableModel, tableProducts, deleteIcon));

		// btn stock out
		btnStockOut.addActionListener(e -> {
			DefaultTableModel modelTable = (DefaultTableModel) tableProducts.getModel();
			InventoryActivityModel invModel = new InventoryActivityModel();

			String receiptCode = invModel.generateNextReceiptCode();
			String creator = "warehouse";

			boolean allSuccess = true;

			for (int i = 0; i < modelTable.getRowCount(); i++) {
				String productCode = modelTable.getValueAt(i, 1).toString();
				int quantity = Integer.parseInt(modelTable.getValueAt(i, 2).toString());

				ImageIcon icon = (ImageIcon) lblImage.getIcon();
				byte[] imageBytes = null;
				if (icon != null && icon.getImage() != null) {
					try {
						if (icon.getDescription() != null) {
							File imgFile = new File(icon.getDescription());
							imageBytes = java.nio.file.Files.readAllBytes(imgFile.toPath());
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				InventoryActivity activity = invModel.stockOutProduct(productCode, quantity, receiptCode, creator,
						imageBytes);

				boolean updated = invModel.updateProductQuantityByCode(productCode, quantity);

				if (activity != null && updated) {
					System.out.println("Stocked out: " + activity.getDescription());
				} else {
					allSuccess = false;
					JOptionPane.showMessageDialog(this, "Unable to stock out product: " + productCode);
				}
			}

			if (allSuccess) {
				JOptionPane.showMessageDialog(this, "Stock Out successful");

				modelTable.setRowCount(0);
				lblTotalAmount.setText("0 VND");

				if (onStockOutSuccess != null) {
					onStockOutSuccess.run();
				}

				Window window = SwingUtilities.getWindowAncestor(JPanelStockOut1.this);
				if (window != null) {
					window.dispose();
				}
			}

		});

	}

	class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
		public ButtonRenderer(Icon icon) {
			setIcon(icon);
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		private JButton button;
		private String actionType;
		private DefaultTableModel model;
		private JTable table;
		private int row;

		public ButtonEditor(JCheckBox checkBox, String actionType, DefaultTableModel model, JTable table, Icon icon) {
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
								public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
										throws BadLocationException {
									if (string.matches("\\d+")) {
										super.insertString(fb, offset, string, attr);
									}
								}

								@Override
								public void replace(FilterBypass fb, int offset, int length, String text,
										AttributeSet attrs) throws BadLocationException {
									if (text.matches("\\d+")) {
										super.replace(fb, offset, length, text, attrs);
									}
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
								JOptionPane.showMessageDialog(table, "Quantity must be > 0!");
								return;
							}

							String productCode = model.getValueAt(row, 1).toString();
							InventoryActivityModel invModel = new InventoryActivityModel();
							List<Inventory> invs = invModel.findAllProducts();
							int stock = 0;
							for (Inventory inv : invs) {
								if (inv.getProduct().getCode().equals(productCode)) {
									stock = inv.getStock().intValue();
									break;
								}
							}

							if (newQty > stock) {
								JOptionPane.showMessageDialog(table,
										"Quantity exceeds stock! Only " + stock + " products left.");
								return;
							}

							long price = Long.parseLong(model.getValueAt(row, 3).toString().replaceAll("[^0-9]", ""));
							long newTotal = (long) newQty * price;

							model.setValueAt(newQty, row, 2);
							model.setValueAt(NumberFormat.getInstance(new Locale("vi", "VN")).format(newTotal) + " VND",
									row, 4);

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
					int confirm = JOptionPane.showConfirmDialog(table, "Are you sure you want to delete this product?",
							"Confirm deletion", JOptionPane.YES_NO_OPTION);
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
						lblTotalAmount.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(sum) + " VND");
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

}
