package SouceCode;

import entities.Position;
import entities.Staff;
import models.PositionModel;
import models.StaffModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import com.toedter.calendar.JDateChooser;
import java.util.UUID;

public class JPanelAddStaff extends JPanel {

	// ================== FIELDS ==================
	private JTextField txtFullName, txtEmail, txtUsername, txtPhone;
	private JPasswordField txtPassword;
	private JDateChooser dateDob;
	private JComboBox<String> cmbGender, cmbActive;
	private JComboBox<Integer> cmbMustChangePassword;
	private JComboBox<Position> cmbPosition;
	private JLabel lblImage;
	private byte[] imageBytes = null;

	private JButton btnSave, btnClose, btnClear, btnChooseImage;

	private Staff currentStaff;
	private Runnable onSuccess;
	private JDialog dialog;

	// ================== COLORS ==================
	private static final Color PRIMARY_COLOR = new Color(250, 172, 104);
	private static final Color PRIMARY_HOVER = new Color(255, 195, 140);
	private static final Color CLOSE_COLOR = new Color(255, 70, 70);
	private static final Color CLOSE_HOVER = new Color(255, 110, 110);
	private static final Color DELETE_COLOR = new Color(220, 53, 69);
	private static final Color DELETE_HOVER = new Color(255, 99, 114);

	// ================== CONSTRUCTOR ==================
	public JPanelAddStaff(Staff staff, Runnable onSuccess, JDialog ownerDialog) {
		this.currentStaff = staff;
		this.onSuccess = onSuccess;
		this.dialog = ownerDialog;
		initUI();

		if (staff != null) {
			loadStaffData(staff);
			btnSave.setText("Update Staff");
			txtPassword.setText("");
			txtPassword.setEnabled(false);
		}
	}

	// ================== UI ==================
	private void initUI() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 40, 30, 40));

		add(createTitle(), BorderLayout.NORTH);
		add(createMainContent(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}

	private JLabel createTitle() {
		JLabel lblTitle = new JLabel(currentStaff == null ? "Add New Staff" : "Update Staff", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
		lblTitle.setForeground(new Color(255, 99, 71));
		return lblTitle;
	}

	private JPanel createMainContent() {
		JPanel content = new JPanel(new BorderLayout(50, 0));
		content.setBackground(Color.WHITE);
		content.setBorder(new EmptyBorder(20, 0, 20, 0));

		content.add(createPhotoPanel(), BorderLayout.WEST);
		content.add(createFormWithScroll(), BorderLayout.CENTER);

		return content;
	}

	private JPanel createPhotoPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		panel.setPreferredSize(new Dimension(220, 600));
		panel.setMinimumSize(new Dimension(220, 400));
		panel.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));

		lblImage = new JLabel("No Image", SwingConstants.CENTER);
		lblImage.setPreferredSize(new Dimension(170, 210));
		lblImage.setMinimumSize(new Dimension(170, 210));
		lblImage.setMaximumSize(new Dimension(170, 210));
		lblImage.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
		lblImage.setOpaque(true);
		lblImage.setBackground(Color.WHITE);
		lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnChooseImage = createStyledButton("Choose Photo", PRIMARY_COLOR, PRIMARY_HOVER);
		btnChooseImage.setMaximumSize(new Dimension(170, 40));
		btnChooseImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnChooseImage.addActionListener(e -> chooseImage());

		panel.add(Box.createVerticalGlue());
		panel.add(lblImage);
		panel.add(Box.createVerticalStrut(20));
		panel.add(btnChooseImage);
		panel.add(Box.createVerticalGlue());

		return panel;
	}

	private JScrollPane createFormWithScroll() {
		JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(Color.WHITE);

		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(12, 10, 12, 10);
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1.0;
		int row = 0;

		addFormRow(form, g, row++, "Full Name:", txtFullName = new JTextField(30));
		addFormRow(form, g, row++, "Email:", txtEmail = new JTextField(30));
		addFormRow(form, g, row++, "Username:", txtUsername = new JTextField(30));
		addFormRow(form, g, row++, "Phone:", txtPhone = new JTextField(30));

		dateDob = new JDateChooser();
		dateDob.setDateFormatString("dd/MM/yyyy");
		dateDob.setMaxSelectableDate(new java.util.Date());
		addFormRow(form, g, row++, "Date of Birth:", dateDob);

		txtPassword = new JPasswordField(30);
		addFormRow(form, g, row++, "Password:", txtPassword);

		cmbGender = new JComboBox<>(new String[] { "Male", "Female" });
		addFormRow(form, g, row++, "Gender:", cmbGender);

		cmbPosition = new JComboBox<>();
		loadPositions();
		addFormRow(form, g, row++, "Position:", cmbPosition);

		cmbActive = new JComboBox<>(new String[] { "Active", "Inactive" });
		addFormRow(form, g, row++, "Active:", cmbActive);

		cmbMustChangePassword = new JComboBox<>(new Integer[] { 1, 0 });
		addFormRow(form, g, row++, "Must Change Password:", cmbMustChangePassword);

		// Giới hạn phone: chỉ số, tối đa 10 chữ số, không mất dữ liệu khi quá
		((AbstractDocument) txtPhone.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				if (string == null || !string.matches("\\d+"))
					return;
				if ((fb.getDocument().getLength() + string.length()) <= 10) {
					super.insertString(fb, offset, string, attr);
				}
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				if (text == null || !text.matches("\\d+"))
					return;
				if ((fb.getDocument().getLength() - length + text.length()) <= 10) {
					super.replace(fb, offset, length, text, attrs);
				}
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				super.remove(fb, offset, length);
			}
		});

		JScrollPane scroll = new JScrollPane(form);
		scroll.setBorder(null);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		return scroll;
	}

	private void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent comp) {
		g.gridx = 0;
		g.gridy = row;
		g.anchor = GridBagConstraints.EAST;
		panel.add(new JLabel(label), g);
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		panel.add(comp, g);
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
		panel.setBackground(Color.WHITE);

		btnClose = createStyledButton("Close", CLOSE_COLOR, CLOSE_HOVER);
		btnClose.setPreferredSize(new Dimension(140, 40));
		btnClose.addActionListener(e -> dialog.dispose());

		btnClear = createStyledButton("Clear", PRIMARY_COLOR, PRIMARY_HOVER);
		btnClear.setPreferredSize(new Dimension(140, 40));
		btnClear.addActionListener(e -> clearForm());

		btnSave = createStyledButton(currentStaff == null ? "Add Staff" : "Update Staff", PRIMARY_COLOR, PRIMARY_HOVER);
		btnSave.setPreferredSize(new Dimension(190, 40));
		btnSave.addActionListener(e -> saveStaff());

		if (currentStaff != null) {
			JButton btnDelete = createStyledButton("Delete", DELETE_COLOR, DELETE_HOVER);
			btnDelete.setPreferredSize(new Dimension(140, 40));
			btnDelete.addActionListener(e -> deleteStaff());
			panel.add(btnDelete);
		}

		panel.add(btnClose);
		panel.add(btnClear);
		panel.add(btnSave);

		return panel;
	}

	private JButton createStyledButton(String text, Color bg, Color hover) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("SansSerif", Font.BOLD, 16));
		btn.setBackground(bg);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setOpaque(true);
		btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(hover);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(bg);
			}
		});

		return btn;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(920, 700);
	}

	// ================== LOGIC ==================
	private void loadPositions() {
		PositionModel model = new PositionModel();
		cmbPosition.removeAllItems();
		for (Position p : model.findAll()) {
			cmbPosition.addItem(p);
		}
	}

	private void chooseImage() {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				imageBytes = Files.readAllBytes(chooser.getSelectedFile().toPath());
				ImageIcon icon = new ImageIcon(imageBytes);
				Image scaled = icon.getImage().getScaledInstance(170, 210, Image.SCALE_SMOOTH);
				lblImage.setIcon(new ImageIcon(scaled));
				lblImage.setText("");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Cannot load image!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadStaffData(Staff s) {
		txtFullName.setText(s.getFullName());
		txtEmail.setText(s.getEmail());
		txtUsername.setText(s.getUsername());
		txtPhone.setText(s.getPhone());
		dateDob.setDate(s.getDob());
		cmbGender.setSelectedItem(s.getGender() == 0 ? "Male" : "Female");
		cmbActive.setSelectedItem(s.getIsActive() == 1 ? "Active" : "Inactive");
		cmbMustChangePassword.setSelectedItem(s.getMustChangePassword());

		for (int i = 0; i < cmbPosition.getItemCount(); i++) {
			if (cmbPosition.getItemAt(i).getId() == s.getIdPosition()) {
				cmbPosition.setSelectedIndex(i);
				break;
			}
		}

		txtUsername.setEditable(false);

		if (s.getLink() != null && s.getLink().length > 0) {
			ImageIcon icon = new ImageIcon(s.getLink());
			Image scaled = icon.getImage().getScaledInstance(170, 210, Image.SCALE_SMOOTH);
			lblImage.setIcon(new ImageIcon(scaled));
			lblImage.setText("");
			imageBytes = s.getLink();
		} else {
			lblImage.setIcon(null);
			lblImage.setText("No Image");
			imageBytes = null;
		}
	}

	private void deleteStaff() {
		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete staff:\n" + currentStaff.getFullName() + " ("
						+ currentStaff.getUsername() + ") ?\n\nThis action cannot be undone!",
				"Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			try {
				StaffModel staffModel = new StaffModel();
				boolean success = staffModel.deleteStaff(currentStaff.getId());

				if (success) {
					JOptionPane.showMessageDialog(this, "Staff deleted successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					if (dialog != null)
						dialog.dispose();
					if (onSuccess != null)
						onSuccess.run();
				} else {
					JOptionPane.showMessageDialog(this,
							"Failed to delete staff.\nPossible reasons: staff is referenced in other records.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error deleting staff: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// ================== SAVE STAFF - ĐÃ THÊM KIỂM TRA TRÙNG EMAIL VÀ PHONE
	// ==================
	private void saveStaff() {
		if (!validateInput())
			return;

		String fullName = txtFullName.getText().trim();
		String email = txtEmail.getText().trim();
		String phone = txtPhone.getText().trim();
		String currentId = (currentStaff != null) ? currentStaff.getId() : null;

		StaffModel staffModel = new StaffModel();

		// Kiểm tra trùng Full Name
		if (staffModel.isFullNameExists(fullName, currentId)) {
			showError("Full name '" + fullName + "' already exists!\nPlease use a different name.");
			txtFullName.requestFocus();
			return;
		}

		// Kiểm tra trùng Email
		if (staffModel.isEmailExists(email, currentId)) {
			showError("Email '" + email + "' already exists!\nPlease use a different email.");
			txtEmail.requestFocus();
			return;
		}

		// Kiểm tra trùng Phone
		if (staffModel.isPhoneExists(phone, currentId)) {
			showError("Phone number '" + phone + "' already exists!\nPlease use a different phone number.");
			txtPhone.requestFocus();
			return;
		}

		try {
			Staff staff = (currentStaff != null) ? currentStaff : new Staff();

			staff.setFullName(fullName);
			staff.setEmail(email);
			staff.setUsername(txtUsername.getText().trim());
			staff.setPhone(phone);
			staff.setDob(dateDob.getDate());
			staff.setGender(cmbGender.getSelectedItem().equals("Male") ? 0 : 1);

			// Xử lý ảnh: không để null
			if (imageBytes == null || imageBytes.length == 0) {
				staff.setLink(new byte[0]);
			} else {
				staff.setLink(imageBytes);
			}

			Position pos = (Position) cmbPosition.getSelectedItem();
			if (pos == null) {
				JOptionPane.showMessageDialog(this, "Please select a position!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			staff.setIdPosition(pos.getId());

			staff.setIsActive(cmbActive.getSelectedItem().equals("Active") ? 1 : 0);
			staff.setMustChangePassword((Integer) cmbMustChangePassword.getSelectedItem());

			String currentUserId = getCurrentUserId();

			if (currentStaff == null) {
				staff.setId(UUID.randomUUID().toString());
				staff.setPassword(new String(txtPassword.getPassword()));

				if (currentUserId == null) {
					JOptionPane.showMessageDialog(this, "Cannot create staff: Unknown creator!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				staff.setIdCreator(currentUserId);
				staff.setIdUpdater(currentUserId);

				boolean success = staffModel.createStaff(staff);
				if (!success) {
					JOptionPane.showMessageDialog(this, "Failed to add staff. Please check your input.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				if (currentUserId != null) {
					staff.setIdUpdater(currentUserId);
				}
				boolean success = staffModel.updateStaff(staff);
				if (!success) {
					JOptionPane.showMessageDialog(this, "Failed to update staff.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			JOptionPane.showMessageDialog(this,
					"Staff " + (currentStaff == null ? "added" : "updated") + " successfully!", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			if (dialog != null)
				dialog.dispose();
			if (onSuccess != null)
				onSuccess.run();

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String getCurrentUserId() {
		return "your-admin-uuid-here";
	}

	private boolean validateInput() {
		if (txtFullName.getText().trim().isEmpty()) {
			showError("Please enter full name!");
			txtFullName.requestFocus();
			return false;
		}
		if (txtEmail.getText().trim().isEmpty()) {
			showError("Please enter email!");
			txtEmail.requestFocus();
			return false;
		}
		if (txtUsername.getText().trim().isEmpty()) {
			showError("Please enter username!");
			txtUsername.requestFocus();
			return false;
		}

		String phone = txtPhone.getText().trim();
		if (phone.isEmpty()) {
			showError("Please enter phone number!");
			txtPhone.requestFocus();
			return false;
		}
		if (!phone.matches("\\d{10}")) {
			showError("Phone number must be exactly 10 digits!");
			txtPhone.requestFocus();
			return false;
		}

		if (dateDob.getDate() == null) {
			showError("Please select date of birth!");
			return false;
		}
		if (dateDob.getDate().after(new java.util.Date())) {
			showError("Date of birth cannot be in the future!");
			return false;
		}

		if (currentStaff == null) {
			if (new String(txtPassword.getPassword()).trim().isEmpty()) {
				showError("Please enter password for new staff!");
				txtPassword.requestFocus();
				return false;
			}
		}
		return true;
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
	}

	private void clearForm() {
		txtFullName.setText("");
		txtEmail.setText("");
		txtUsername.setText("");
		txtPhone.setText("");
		txtPassword.setText("");
		dateDob.setDate(null);
		cmbGender.setSelectedIndex(0);
		cmbActive.setSelectedIndex(0);
		cmbMustChangePassword.setSelectedIndex(0);
		cmbPosition.setSelectedIndex(0);
		lblImage.setIcon(null);
		lblImage.setText("No Image");
		imageBytes = null;
		txtUsername.setEditable(true);
	}
}