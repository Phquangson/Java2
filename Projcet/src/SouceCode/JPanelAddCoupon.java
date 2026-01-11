package SouceCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import models.CouponModel;
import models.TypeModel;
import entities.Coupon;
import entities.Type;

public class JPanelAddCoupon extends JPanel {

	private JButton btnAdd;
	private JLabel lblTitle;

	private JTextField txtCode;
	private JTextField txtTitle;
	private JTextField txtDiscount;
	private JTextField txtQuantity;
	private JComboBox<Type> cboType;
	private JCheckBox chkActive;
	private com.toedter.calendar.JDateChooser dateExpired;

	private JPanelCoupon parent;
	private Point initialClick;

	public JPanelAddCoupon(JPanelCoupon parent) {
		this.parent = parent;
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(450, 450));

		lblTitle = new JLabel("Add Coupon", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(60, 60, 60));
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setOpaque(true);
		add(lblTitle, BorderLayout.NORTH);

		JPanel panelCenter = new JPanel(new GridBagLayout());
		panelCenter.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		panelCenter.add(new JLabel("Code:"), gbc);
		gbc.gridx = 1;
		txtCode = new JTextField();
		panelCenter.add(txtCode, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		panelCenter.add(new JLabel("Title:"), gbc);
		gbc.gridx = 1;
		txtTitle = new JTextField();
		panelCenter.add(txtTitle, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		panelCenter.add(new JLabel("Discount:"), gbc);
		gbc.gridx = 1;
		txtDiscount = new JTextField();
		panelCenter.add(txtDiscount, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		panelCenter.add(new JLabel("Quantity:"), gbc);
		gbc.gridx = 1;
		txtQuantity = new JTextField();
		panelCenter.add(txtQuantity, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		panelCenter.add(new JLabel("Expired Date:"), gbc);
		gbc.gridx = 1;
		dateExpired = new com.toedter.calendar.JDateChooser();
		dateExpired.setDateFormatString("dd/MM/yyyy");
		panelCenter.add(dateExpired, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		panelCenter.add(new JLabel("Type:"), gbc);
		gbc.gridx = 1;
		cboType = new JComboBox<>();
		loadTypes();
		panelCenter.add(cboType, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		panelCenter.add(new JLabel("Active:"), gbc);
		gbc.gridx = 1;
		chkActive = new JCheckBox("Active");
		panelCenter.add(chkActive, gbc);

		add(panelCenter, BorderLayout.CENTER);

		btnAdd = new JButton("Add Coupon");
		btnAdd.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnAdd.setBackground(Color.GRAY);
		btnAdd.setForeground(Color.WHITE);

		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.addActionListener(e -> closeDialog());

		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		panelBottom.setBackground(Color.WHITE);
		panelBottom.add(btnClose);
		panelBottom.add(btnAdd);

		add(panelBottom, BorderLayout.SOUTH);

		btnAdd.addActionListener(e -> {
			try {
				if (!validateInput())
					return;

				Coupon c = new Coupon();
				c.setCode(txtCode.getText().trim());
				c.setTitle(txtTitle.getText().trim());
				c.setDiscountValue(new BigDecimal(txtDiscount.getText().trim().replace(".", "").replace(",", ".")));
				c.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
				c.setExpiredDate(dateExpired.getDate());
				c.setIdCreator("Super Admin");
				c.setIdUpdater("Super Admin");

				Type selectedType = (Type) cboType.getSelectedItem();
				if (selectedType != null) {
					c.setIdType(selectedType.getId());
				}
				c.setIsActive(chkActive.isSelected() ? 1 : 0);

				CouponModel model = new CouponModel();
				if (model.findByCode(txtCode.getText().trim()) != null) {
					JOptionPane.showMessageDialog(this, "Coupon code already exists!");
					return;
				}

				if (model.create(c)) {
					JOptionPane.showMessageDialog(this, "Coupon added successfully!");
					if (parent != null) {
						parent.load();
					}
					closeDialog();
				} else {
					JOptionPane.showMessageDialog(this, "Failed to add coupon", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		enableDrag();
	}

	private void loadTypes() {
		TypeModel typeModel = new TypeModel();
		List<Type> types = typeModel.findAll();
		cboType.removeAllItems();
		if (types != null) {
			for (Type t : types) {
				cboType.addItem(t);
			}
		}
	}

	private void closeDialog() {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w != null) {
			w.dispose();
		}
	}

	public void setCouponData(Coupon coupon) {
		txtCode.setText(coupon.getCode());
		txtTitle.setText(coupon.getTitle());
		txtDiscount.setText(coupon.getDiscountValue().toString());
		txtQuantity.setText(String.valueOf(coupon.getQuantity()));
		chkActive.setSelected(coupon.getIsActive() == 1);
		dateExpired.setDate(coupon.getExpiredDate());
		lblTitle.setText("Update Coupon");
		btnAdd.setText("Update Coupon");

		for (ActionListener al : btnAdd.getActionListeners()) {
			btnAdd.removeActionListener(al);
		}

		btnAdd.addActionListener(e -> {
			try {
				if (!validateInput())
					return;

				Coupon c = new Coupon();
				// giữ lại ID để update
				c.setId(coupon.getId());

				c.setCode(txtCode.getText().trim());
				c.setTitle(txtTitle.getText().trim());
				c.setDiscountValue(new BigDecimal(txtDiscount.getText().trim().replace(".", "").replace(",", ".")));
				c.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
				c.setExpiredDate(dateExpired.getDate());
				c.setIdUpdater("Super Admin");

				Type selectedType = (Type) cboType.getSelectedItem();
				if (selectedType != null) {
					c.setIdType(selectedType.getId());
				}
				c.setIsActive(chkActive.isSelected() ? 1 : 0);

				CouponModel model = new CouponModel();

				// không cần check findByCode nếu cho phép giữ nguyên code
				// nếu muốn check trùng code thì phải bỏ qua chính nó
				Coupon existing = model.findByCode(txtCode.getText().trim());
				if (existing != null && existing.getId() != coupon.getId()) {
					JOptionPane.showMessageDialog(this, "Coupon code already exists!");
					return;
				}

				if (model.update(c)) {
					JOptionPane.showMessageDialog(this, "Coupon updated successfully!");
					if (parent != null) {
						parent.load();
					}
					closeDialog();
				} else {
					JOptionPane.showMessageDialog(this, "Failed to update coupon", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private void enableDrag() {
		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				initialClick = e.getPoint();
			}
		});
		addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				Window window = SwingUtilities.getWindowAncestor(JPanelAddCoupon.this);
				if (window != null) {
					int thisX = window.getLocation().x;
					int thisY = window.getLocation().y;
					int xMoved = e.getX() - initialClick.x;
					int yMoved = e.getY() - initialClick.y;
					int X = thisX + xMoved;
					int Y = thisY + yMoved;
					window.setLocation(X, Y);
				}
			}
		});
	}

	private boolean validateInput() {
		if (txtCode.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Coupon code cannot be empty!");
			return false;
		}

		if (txtTitle.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Title cannot be empty!");
			return false;
		}

		try {
			BigDecimal discount = new BigDecimal(txtDiscount.getText().trim().replace(".", "").replace(",", "."));
			if (discount.compareTo(BigDecimal.ZERO) <= 0) {
				JOptionPane.showMessageDialog(this, "Discount must be greater than 0!");
				return false;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Invalid discount value!");
			return false;
		}

		try {
			int quantity = Integer.parseInt(txtQuantity.getText().trim());
			if (quantity < 0) {
				JOptionPane.showMessageDialog(this, "Quantity must be greater or equal to 0!");
				return false;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Invalid quantity value!");
			return false;
		}

		Date expired = dateExpired.getDate();
		if (expired == null) {
			JOptionPane.showMessageDialog(this, "Please select an expiration date!");
			return false;
		}
		if (expired.before(new Date())) {
			JOptionPane.showMessageDialog(this, "Expiration date must be in the future!");
			return false;
		}

		if (cboType.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Please select a coupon type!");
			return false;
		}

		return true;
	}

}
