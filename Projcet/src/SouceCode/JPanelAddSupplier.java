package SouceCode;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class JPanelAddSupplier extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField txtCode, txtName, txtPhone;
	private JTextArea txtAddress;
	private JButton btnAdd;

	private JPanelAddProduct parent;
	private JPanelSupplier parentSupplier;

	private Integer editingSupplierId = null;

	private JLabel lblTitle;

	public JPanelAddSupplier(JPanelAddProduct parent) {
		this.parent = parent;
		this.parentSupplier = null;
		initUI();
	}

	public JPanelAddSupplier(JPanelSupplier parentSupplier) {
		this.parent = null;
		this.parentSupplier = parentSupplier;
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(500, 400));

		lblTitle = new JLabel("Add Supplier", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(60, 60, 60));
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setOpaque(true);
		add(lblTitle, BorderLayout.NORTH);

		final Point dragPoint = new Point();

		lblTitle.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				dragPoint.x = e.getX();
				dragPoint.y = e.getY();
			}
		});

		lblTitle.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				Window w = SwingUtilities.getWindowAncestor(lblTitle);
				if (w != null) {
					Point p = w.getLocation();
					w.setLocation(p.x + e.getX() - dragPoint.x, p.y + e.getY() - dragPoint.y);
				}
			}
		});

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		txtCode = createTextField(formPanel, gbc, "Code:", 0);
		txtName = createTextField(formPanel, gbc, "Name:", 1);
		txtPhone = createTextField(formPanel, gbc, "Phone:", 2);

		((AbstractDocument) txtPhone.getDocument()).setDocumentFilter(new PhoneNumberFilter());

		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(new JLabel("Address:"), gbc);
		gbc.gridx = 1;
		txtAddress = new JTextArea(4, 20);
		txtAddress.setLineWrap(true);
		txtAddress.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(txtAddress);
		formPanel.add(scrollPane, gbc);

		add(formPanel, BorderLayout.CENTER);

		btnAdd = new JButton("Add Supplier");
		btnAdd.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnAdd.setBackground(new Color(147, 189, 87));
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFocusPainted(false);
		btnAdd.setPreferredSize(new Dimension(160, 40));
		btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFocusPainted(false);
		btnClose.setPreferredSize(new Dimension(160, 40));
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		btnClose.addActionListener(e -> {
			Window w = SwingUtilities.getWindowAncestor(this);
			if (w != null) {
				w.dispose();
			}
		});

		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelBottom.setBackground(Color.WHITE);
		panelBottom.add(btnClose);
		panelBottom.add(btnAdd);

		add(panelBottom, BorderLayout.SOUTH);

		btnAdd.addActionListener(e -> {
			if (!validateInput())
				return;

			models.SupplierModel model = new models.SupplierModel();

			if (editingSupplierId == null) {
				entities.Supplier supplier = new entities.Supplier();
				supplier.setCode(txtCode.getText().trim());
				supplier.setName(txtName.getText().trim());
				supplier.setPhone(txtPhone.getText().trim());
				supplier.setAddress(txtAddress.getText().trim());
				supplier.setIdCreator("30da09e0-8741-438e-a76d-c606045cb74d");
				supplier.setCreatedDate(new java.util.Date());
				supplier.setIdUpdater("30da09e0-8741-438e-a76d-c606045cb74d");

				if (model.create(supplier)) {
					JOptionPane.showMessageDialog(this, "Supplier added successfully!");
					if (parent != null) {
						parent.refreshSuppliers();
					}
					if (parentSupplier != null) {
						parentSupplier.init();
					}
					closeDialog();
				}
			} else {
				entities.Supplier supplier = new entities.Supplier();
				supplier.setId(editingSupplierId);
				supplier.setCode(txtCode.getText().trim());
				supplier.setName(txtName.getText().trim());
				supplier.setPhone(txtPhone.getText().trim());
				supplier.setAddress(txtAddress.getText().trim());
				supplier.setIdUpdater("30da09e0-8741-438e-a76d-c606045cb74d");
				supplier.setUpdatedDate(new java.util.Date());

				if (model.update(supplier)) {
					JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
					if (parent != null) {
						parent.refreshSuppliers();
					}
					if (parentSupplier != null) {
						parentSupplier.init();
					}
					closeDialog();
				} else {
					JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}

	private void handleAddOrUpdate() {
		if (!validateInput())
			return;
		String code = txtCode.getText().trim();
		String name = txtName.getText().trim();
		String phone = txtPhone.getText().trim();
		String address = txtAddress.getText().trim();
		models.SupplierModel model = new models.SupplierModel();

		if (editingSupplierId == null) {
			if (model.existsByCode(code) || model.existsByName(name) || model.existsByPhone(phone)
					|| model.existsByAddress(address)) {
				JOptionPane.showMessageDialog(this, "Supplier already exists!", "Duplicate",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			entities.Supplier supplier = new entities.Supplier();
			supplier.setCode(code);
			supplier.setName(name);
			supplier.setPhone(phone);
			supplier.setAddress(address);
			supplier.setIdCreator("30da09e0-8741-438e-a76d-c606045cb74d");
			supplier.setCreatedDate(new java.util.Date());
			supplier.setIdUpdater("30da09e0-8741-438e-a76d-c606045cb74d");
			if (model.create(supplier)) {
				JOptionPane.showMessageDialog(this, "Supplier added successfully!");
				clearForm();
				if (parent != null)
					parent.refreshSuppliers();
				if (parentSupplier != null)
					parentSupplier.init();
				closeDialog();
			} else {
				JOptionPane.showMessageDialog(this, "Failed to add supplier!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			entities.Supplier supplier = new entities.Supplier();
			supplier.setId(editingSupplierId);
			supplier.setCode(code);
			supplier.setName(name);
			supplier.setPhone(phone);
			supplier.setAddress(address);
			supplier.setUpdatedDate(new java.util.Date());
			if (model.update(supplier)) {
				JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
				if (parentSupplier != null)
					parentSupplier.init();
				closeDialog();
			} else {
				JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void setSupplierData(entities.Supplier supplier) {
		txtCode.setText(supplier.getCode());
		txtName.setText(supplier.getName());
		txtPhone.setText(supplier.getPhone());
		txtAddress.setText(supplier.getAddress());
		editingSupplierId = supplier.getId();

		btnAdd.setText("Update Supplier");
		lblTitle.setText("Update Supplier");
	}

	private void closeDialog() {
		java.awt.Window w = SwingUtilities.getWindowAncestor(this);
		if (w != null)
			w.dispose();
	}

	private JTextField createTextField(JPanel panel, GridBagConstraints gbc, String label, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		panel.add(new JLabel(label), gbc);
		gbc.gridx = 1;
		JTextField field = new JTextField(20);
		panel.add(field, gbc);
		return field;
	}

	private boolean validateInput() {
		if (txtCode.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Code cannot be empty.");
			return false;
		}
		if (txtName.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Name cannot be empty.");
			return false;
		}
		String phone = txtPhone.getText().trim();
		if (phone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Phone cannot be empty.");
			return false;
		}
		if (phone.length() != 10) {
			JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.");
			return false;
		}
		if (!phone.matches("0\\d{9}")) {
			JOptionPane.showMessageDialog(this, "Phone number must start with 0 and be valid.");
			return false;
		}
		if (txtAddress.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Address cannot be empty.");
			return false;
		}
		return true;
	}

	private void clearForm() {
		txtCode.setText("");
		txtName.setText("");
		txtPhone.setText("");
		txtAddress.setText("");
	}

	static class PhoneNumberFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {
			if (string != null && string.matches("\\d+")) {
				if (fb.getDocument().getLength() + string.length() <= 10) {
					super.insertString(fb, offset, string, attr);
				} else {
					JOptionPane.showMessageDialog(null, "Phone number cannot exceed 10 digits.");
				}
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			if (text != null && text.matches("\\d*")) {
				if (fb.getDocument().getLength() - length + text.length() <= 10) {
					super.replace(fb, offset, length, text, attrs);
				} else {
					JOptionPane.showMessageDialog(null, "Phone number cannot exceed 10 digits.");
				}
			}
		}
	}

}
