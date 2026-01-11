package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import entities.Supplier;
import models.SupplierModel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class JPanelSupplier extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable jTableSupplier;
	private JTextField txtName, txtCode, txtPhone, txtEmail, txtAddress;
	private int selectedId = -1;
	private List<Supplier> suppliers;
	private JButton btnAdd;
	private JButton btnUpdate;
	private JButton btnCancel;

	public JPanelSupplier() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);

		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createMainSplitPane(), BorderLayout.CENTER);

		init();
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.setBackground(new Color(247, 222, 155));

		JLabel titleLabel = new JLabel("Supplier");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		titleLabel.setForeground(new Color(0, 0, 0));

		headerPanel.add(titleLabel);
		return headerPanel;
	}

	private JPanel createMainSplitPane() {
		JPanel tablePanel = createTablePanel();
		return tablePanel;
	}

	private JTextField addFormRow(JPanel panel, int row, String labelText) {
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.gridx = 0;
		gbcLabel.gridy = row;
		gbcLabel.insets = new Insets(6, 8, 6, 8);
		gbcLabel.anchor = GridBagConstraints.WEST;

		JLabel label = new JLabel(labelText);
		label.setPreferredSize(new Dimension(70, 25));
		panel.add(label, gbcLabel);

		GridBagConstraints gbcField = new GridBagConstraints();
		gbcField.gridx = 1;
		gbcField.gridy = row;
		gbcField.insets = new Insets(6, 8, 6, 8);
		gbcField.anchor = GridBagConstraints.WEST;
		gbcField.fill = GridBagConstraints.NONE;

		JTextField textField = new JTextField(15);
		textField.setPreferredSize(new Dimension(160, 25));
		panel.add(textField, gbcField);

		return textField;
	}

	private JPanel createTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(Color.WHITE);

		String[] columns = { "STT", "Name", "Phone", "Email", "Address", "Update", "Delete" };
		DefaultTableModel model = new DefaultTableModel(columns, 0);
		jTableSupplier = new JTable(model);

		jTableSupplier.setRowHeight(50);
		jTableSupplier.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
		jTableSupplier.setFont(new Font("SansSerif", Font.PLAIN, 14));
		jTableSupplier.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		tablePanel.add(createSearchPanel(), BorderLayout.NORTH);
		tablePanel.add(new JScrollPane(jTableSupplier), BorderLayout.CENTER);

		return tablePanel;
	}

	private JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchPanel.setBorder(new TitledBorder("Search supplier"));
		searchPanel.setBackground(Color.WHITE);

		JTextField searchField = new JTextField(15);

		searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void updateSearch() {
				String keyword = searchField.getText().trim();
				SupplierModel supplierModel = new SupplierModel();
				if (keyword.isEmpty()) {
					loadDataToJTable(supplierModel.findAll());
				} else {
					List<Supplier> result = supplierModel.findByNameSupplier(keyword);
					loadDataToJTable(result);
				}
			}

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				updateSearch();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				updateSearch();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				updateSearch();
			}
		});

		JButton searchButton = new JButton(new ImageIcon(getClass().getResource("/resources/icon-search.png")));
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//		JButton clearButton = new JButton(new ImageIcon(getClass().getResource("/resources/icon-trash.png")));
//		clearButton.setBackground(new Color(192, 192, 192));
//		clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton addButton = new JButton("Add");
		addButton.setIcon(new ImageIcon(JPanelSupplier.class.getResource("/resources/icon-add.png")));
		addButton.setFont(new Font("SansSerif", Font.BOLD, 13));
		addButton.setBackground(new Color(192, 192, 192));
		addButton.setForeground(new Color(0, 0, 0));
		addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		addButton.addActionListener(e -> {
			JPanelAddSupplier addSupplierPanel = new JPanelAddSupplier(JPanelSupplier.this);

			JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Supplier", true);
			dialog.setUndecorated(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setSize(600, 400);
			dialog.setLocationRelativeTo(this);
			dialog.getContentPane().add(addSupplierPanel);
			dialog.setVisible(true);
		});

		searchPanel.add(addButton);

		searchPanel.add(addButton);

		JLabel label = new JLabel("Search:");
		label.setFont(new Font("Tahoma", Font.BOLD, 13));
		searchPanel.add(label);
		searchPanel.add(searchField);
//		searchPanel.add(searchButton);
//		searchPanel.add(clearButton);
		searchPanel.add(addButton);

		searchButton.addActionListener(e -> {
			String keyword = searchField.getText().trim();
			SupplierModel supplierModel = new SupplierModel();
			List<Supplier> result = supplierModel.findByNameSupplier(keyword);
			if (result != null && !result.isEmpty()) {
				loadDataToJTable(result);
			} else {
				JOptionPane.showMessageDialog(this, "No supplier found!");
			}
		});

//		clearButton.addActionListener(e -> {
//			searchField.setText("");
//
//			SupplierModel supplierModel = new SupplierModel();
//			loadDataToJTable(supplierModel.findAll());
//		});

		return searchPanel;
	}

	void init() {
		SupplierModel supplierModel = new SupplierModel();
		loadDataToJTable(supplierModel.findAll());
	}

	private void loadDataToJTable(List<Supplier> suppliers) {
		this.suppliers = suppliers;
		DefaultTableModel tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 5 || column == 6;
			}
		};

		tableModel.addColumn("STT");
		tableModel.addColumn("Code");
		tableModel.addColumn("Name");
		tableModel.addColumn("Phone");
		tableModel.addColumn("Address");
		tableModel.addColumn("Edit");
		tableModel.addColumn("Delete");

		if (suppliers == null || suppliers.isEmpty()) {
			// giữ nguyên header, nhưng không hiển thị nút
			tableModel.addRow(new Object[] { "", "", "The supplier does not exist", "", "", "", "" });
		} else {
			int stt = 1;
			for (Supplier supplier : suppliers) {
				tableModel.addRow(new Object[] { stt++, supplier.getCode(), supplier.getName(), supplier.getPhone(),
						supplier.getAddress(), "Edit", "Delete" });
			}
		}

		jTableSupplier.setModel(tableModel);

		if (suppliers != null && !suppliers.isEmpty()) {
			// chỉ gắn renderer/editor khi có dữ liệu
			ImageIcon editIcon = new ImageIcon(getClass().getResource("/resources/icon-edit.png"));
			ImageIcon trashIcon = new ImageIcon(getClass().getResource("/resources/icon-trash-32.png"));

			jTableSupplier.getColumn("Edit").setCellRenderer(new ButtonRenderer(editIcon));
			jTableSupplier.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), editIcon, "edit"));

			jTableSupplier.getColumn("Delete").setCellRenderer(new ButtonRenderer(trashIcon));
			jTableSupplier.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), trashIcon, "delete"));
		}

		jTableSupplier.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				int col = jTableSupplier.columnAtPoint(e.getPoint());
				if (col == jTableSupplier.getColumn("Edit").getModelIndex()
						|| col == jTableSupplier.getColumn("Delete").getModelIndex()) {
					jTableSupplier.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					jTableSupplier.setCursor(Cursor.getDefaultCursor());
				}
			}
		});

		// nếu muốn header cũng đổi con trỏ
		jTableSupplier.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				jTableSupplier.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
		jTableSupplier.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				jTableSupplier.getTableHeader().setCursor(Cursor.getDefaultCursor());
			}
		});

	}

	class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
		private ImageIcon icon;

		public ButtonRenderer(ImageIcon icon) {
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			this.icon = icon;
			setIcon(icon);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		private JButton button;
		private boolean clicked;
		private String actionType;
		private int row;

		public ButtonEditor(JCheckBox checkBox, ImageIcon icon, String actionType) {
			super(checkBox);
			this.actionType = actionType;
			button = new JButton();
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setIcon(icon);

			button.addActionListener(e -> {
				if ("edit".equals(actionType)) {
					Supplier supplier = suppliers.get(row);
					JPanelAddSupplier editPanel = new JPanelAddSupplier(JPanelSupplier.this);
					editPanel.setSupplierData(supplier);

					JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(JPanelSupplier.this),
							"Edit Supplier", true);
					dialog.setUndecorated(true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setSize(600, 400);
					dialog.setLocationRelativeTo(JPanelSupplier.this);
					dialog.getContentPane().add(editPanel);
					dialog.setVisible(true);
				}
				if ("delete".equals(actionType)) {
					Supplier supplier = suppliers.get(row);
					int confirm = JOptionPane.showConfirmDialog(JPanelSupplier.this,
							"Are you sure you want to delete supplier: " + supplier.getName() + "?", "Confirm Delete",
							JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						SupplierModel model = new SupplierModel();
						if (model.delete(supplier.getId())) {
							JOptionPane.showMessageDialog(JPanelSupplier.this, "Supplier deleted successfully!");
							init();
						} else {
							JOptionPane.showMessageDialog(JPanelSupplier.this, "Delete failed!", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				fireEditingStopped();
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			this.row = row;
			clicked = true;
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			clicked = false;
			return "";
		}

	}

	private JPanel createFormPanel() {
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(new TitledBorder("Add"));
		formPanel.setBackground(Color.WHITE);
		formPanel.setPreferredSize(new Dimension(400, 260));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 8, 6, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;

		int row = 0;
		txtCode = addFormRow(formPanel, row++, "Code:");
		txtName = addFormRow(formPanel, row++, "Name:");
		txtPhone = addFormRow(formPanel, row++, "Phone:");
//	    txtEmail = addFormRow(formPanel, row++, "Email:");
		txtAddress = addFormRow(formPanel, row++, "Address:");

		txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyTyped(java.awt.event.KeyEvent e) {
				char c = e.getKeyChar();
				if (!Character.isDigit(c)) {
					e.consume();
				}
				if (txtPhone.getText().length() >= 10) {
					e.consume();
				}
			}
		});

		gbc.gridx = 1;
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.CENTER;

		btnAdd = new JButton("Add Supplier");
		btnAdd.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnAdd.setPreferredSize(new Dimension(160, 30));
		btnAdd.setBackground(Color.GRAY);
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnAdd.addActionListener(e -> {
			String code = txtCode.getText().trim();
			String name = txtName.getText().trim();
			String phone = txtPhone.getText().trim();
//	        String email = (txtEmail != null ? txtEmail.getText().trim() : "");
			String address = txtAddress.getText().trim();

			if (code.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please enter all the required information!");
				return;
			}

			SupplierModel supplierModel = new SupplierModel();
			List<Supplier> suppliers = supplierModel.findAll();

			for (Supplier s : suppliers) {
				if (s.getCode() != null && s.getCode().trim().equalsIgnoreCase(code)) {
					JOptionPane.showMessageDialog(this, "The code already exists!");
					return;
				}
				if (s.getName() != null && s.getName().trim().equalsIgnoreCase(name)) {
					JOptionPane.showMessageDialog(this, "The name already exists!");
					return;
				}
				if (s.getPhone() != null && s.getPhone().trim().equals(phone)) {
					JOptionPane.showMessageDialog(this, "This phone number already exists!");
					return;
				}
			}

//	            if (s.getEmail() != null && s.getEmail().equalsIgnoreCase(email)) {
//	                JOptionPane.showMessageDialog(this, "Email already exists!");
//	                return;
//	            }

			if (!phone.matches("0\\d{9}")) {
				JOptionPane.showMessageDialog(this, "Phone numbers must start with 0 and have 10 digits!");
				return;
			}

//	        // Validate email
//	        if (!email.contains("@")) {
//	            JOptionPane.showMessageDialog(this, "Email must be contains character @");
//	            return;
//	        }

			Supplier supplier = new Supplier();
			supplier.setCode(code);
			supplier.setName(name);
			supplier.setPhone(phone);
//	        supplier.setEmail(email);
			supplier.setAddress(address);
			supplier.setIdCreator(UUID.randomUUID().toString());
			supplier.setCreatedDate(new java.util.Date());
			supplier.setIdUpdater(UUID.randomUUID().toString());

			if (supplierModel.create(supplier)) {
				JOptionPane.showMessageDialog(this, "Supplier added successfully!");
				loadDataToJTable(supplierModel.findAll());

				clearForm();
				switchToAddMode();
			} else {
				JOptionPane.showMessageDialog(this, "Add failed!");
			}

			switchToAddMode();

		});

		// Nút Add
		GridBagConstraints gbcAdd = new GridBagConstraints();
		gbcAdd.gridx = 1;
		gbcAdd.gridy = row;
		gbcAdd.anchor = GridBagConstraints.CENTER;
		formPanel.add(btnAdd, gbcAdd);

		btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnCancel.setPreferredSize(new Dimension(160, 30));
		btnCancel.setBackground(Color.GRAY);
		btnCancel.setForeground(Color.WHITE);
		btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnCancel.addActionListener(e -> {
			clearForm();
			JOptionPane.showMessageDialog(this, "Form has been cleared!");
			switchToAddMode();
		});

		btnUpdate = new JButton("Update");
		btnUpdate.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnUpdate.setPreferredSize(new Dimension(160, 30));
		btnUpdate.setBackground(Color.GRAY);
		btnUpdate.setForeground(Color.WHITE);
		btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));

		btnUpdate.setVisible(false);
		btnCancel.setVisible(false);

		btnUpdate.addActionListener(e -> {
			String code = txtCode.getText().trim();
			String name = txtName.getText().trim();
			String phone = txtPhone.getText().trim();
			String address = txtAddress.getText().trim();

			if (code.isEmpty() || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please enter all the required information!");
				return;
			}

			SupplierModel supplierModel = new SupplierModel();
			List<Supplier> allSuppliers = supplierModel.findAll();

			for (Supplier s : allSuppliers) {
				// bỏ qua chính supplier đang update (so sánh id)
				if (s.getId() != selectedId) {
					if (s.getCode() != null && s.getCode().trim().equalsIgnoreCase(code)) {
						JOptionPane.showMessageDialog(this, "The code already exists!");
						return;
					}
					if (s.getName() != null && s.getName().trim().equalsIgnoreCase(name)) {
						JOptionPane.showMessageDialog(this, "The name already exists!");
						return;
					}
					if (s.getPhone() != null && s.getPhone().trim().equals(phone)) {
						JOptionPane.showMessageDialog(this, "This phone number already exists!");
						return;
					}
				}
			}

			if (!phone.matches("0\\d{9}")) {
				JOptionPane.showMessageDialog(this, "Phone numbers must start with 0 and have 10 digits!");
				return;
			}

			Supplier supplier = new Supplier();
			supplier.setId(selectedId);
			supplier.setCode(code);
			supplier.setName(name);
			supplier.setPhone(phone);
			supplier.setAddress(address);
			supplier.setIdUpdater(UUID.randomUUID().toString());
			supplier.setUpdatedDate(new java.util.Date());

			if (supplierModel.update(supplier)) {
				JOptionPane.showMessageDialog(this, "Updated successfully");
				loadDataToJTable(supplierModel.findAll());
				clearForm();
				selectedId = -1;
				switchToAddMode();
			} else {
				JOptionPane.showMessageDialog(this, "Update failed");
			}
		});

		gbc.gridy = row + 1;
		GridBagConstraints gbcUpdate = new GridBagConstraints();
		gbcUpdate.gridx = 1;
		gbcUpdate.gridy = row + 1;
		gbcUpdate.anchor = GridBagConstraints.CENTER;
		formPanel.add(btnUpdate, gbcUpdate);

		GridBagConstraints gbcCancel = new GridBagConstraints();
		gbcCancel.gridx = 1;
		gbcCancel.gridy = row + 2;
		gbcCancel.anchor = GridBagConstraints.CENTER;
		formPanel.add(btnCancel, gbcCancel);

		return formPanel;
	}

	private void clearForm() {
		txtCode.setText("");
		txtName.setText("");
		txtPhone.setText("");
		txtAddress.setText("");
		if (txtEmail != null) {
			txtEmail.setText("");
		}
		selectedId = -1;
	}

	private void switchToAddMode() {
		btnAdd.setVisible(true);
		btnUpdate.setVisible(false);
		btnCancel.setVisible(false);
	}

	private void switchToEditMode() {
		btnAdd.setVisible(false);
		btnUpdate.setVisible(true);
		btnCancel.setVisible(true);
	}

}
