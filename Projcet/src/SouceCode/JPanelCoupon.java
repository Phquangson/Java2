package SouceCode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import models.CouponModel;
import entities.Coupon;

public class JPanelCoupon extends JPanel {

	private JTable table;
	private JTextField txtSearch;
	private CouponModel model = new CouponModel();

	public JPanelCoupon() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		formPanel
				.setBorder(new TitledBorder(null, "Search coupon", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		formPanel.setBackground(Color.WHITE);

		JLabel lblSearch = new JLabel("Title:");
		lblSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
		txtSearch = new JTextField(15);

		JButton btnClear = new JButton("");
		btnClear.setBackground(new Color(192, 192, 192));
		btnClear.setIcon(new ImageIcon(JPanelCoupon.class.getResource("/resources/icon-trash.png")));
		styleButton(btnClear);

		JButton btnAdd = new JButton("Add");
		btnAdd.setIcon(new ImageIcon(JPanelCoupon.class.getResource("/resources/icon-add.png")));
		styleButton(btnAdd);

		btnAdd.addActionListener(e -> {
			JDialog dialog = new JDialog((Frame) null, "Add Coupon", true);
			dialog.setUndecorated(true);

			JPanelAddCoupon panel = new JPanelAddCoupon(this);
			dialog.getContentPane().add(panel);
			dialog.pack();
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
		});

		formPanel.add(lblSearch);
		formPanel.add(txtSearch);
		formPanel.add(btnClear);
		formPanel.add(btnAdd);

		DefaultTableModel modelTable = new DefaultTableModel(new Object[] { "No.", "Code", "Title", "Discount",
				"Quantity", "Expired Date", "Type", "Active", "ID", "Edit", "Delete" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 9 || column == 10;
			}
		};

		table = new JTable(modelTable);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelTable);
		table.setRowSorter(sorter);

		// Ẩn cột ID
		table.getColumnModel().getColumn(8).setMinWidth(0);
		table.getColumnModel().getColumn(8).setMaxWidth(0);
		table.getColumn("Active").setCellRenderer(new PublicRender());

		// Renderer cho Edit/Delete
		table.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
		table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

		// Editor cho Edit/Delete
		table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), "Edit", this));
		table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", this));

		table.setRowHeight(70);
		table.setIntercellSpacing(new Dimension(0, 20));
		table.setShowGrid(false);
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				table.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});

		table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());

				if (col == table.getColumnModel().getColumnIndex("Active")) {
					table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					table.setCursor(Cursor.getDefaultCursor());
				}
			}
		});

		// Bắt sự kiện click chuột trên bảng
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());

				// Nếu click vào cột Active
				if (col == table.getColumnModel().getColumnIndex("Active")) {
					int id = Integer.parseInt(table.getValueAt(row, 8).toString());
					String currentStatus = table.getValueAt(row, col).toString();

					int confirm = JOptionPane.showConfirmDialog(JPanelCoupon.this,
							"Coupon hiện đang ở trạng thái " + currentStatus + ".\nBạn có muốn thay đổi không?",
							"Xác nhận", JOptionPane.YES_NO_OPTION);

					if (confirm == JOptionPane.YES_OPTION) {
						CouponModel model = new CouponModel();
						if (model.toggleActive(id)) {
							JOptionPane.showMessageDialog(JPanelCoupon.this, "Thay đổi trạng thái thành công!");
							load(); // reload lại bảng
						} else {
							JOptionPane.showMessageDialog(JPanelCoupon.this, "Thay đổi trạng thái thất bại!");
						}
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.add(formPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);

		loadData(modelTable);
	}

	private void styleButton(JButton btn) {
		btn.setFont(new Font("SansSerif", Font.BOLD, 13));
		btn.setBackground(new Color(220, 220, 220));
		btn.setFocusPainted(false);
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				btn.setCursor(Cursor.getDefaultCursor());
			}
		});
	}

	private void loadData(DefaultTableModel m) {
		m.setRowCount(0);
		List<Coupon> list = model.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		int stt = 1;
		for (Coupon c : list) {
			String expired = c.getExpiredDate() != null ? sdf.format(c.getExpiredDate()) : "";
			String active = c.getIsActive() == 1 ? "Active" : "Deactive";
			String type = c.getTypeTitle() != null ? c.getTypeTitle() : "";
			m.addRow(new Object[] { stt++, c.getCode(), c.getTitle(), c.getDiscountValue(), c.getQuantity(), expired,
					type, active, c.getId(), "Edit", "Delete" });
		}
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {
		private String action;

		public ButtonRenderer(String action) {
			this.action = action;
			setFocusPainted(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (action.equals("Edit")) {
				setIcon(new ImageIcon(getClass().getResource("/resources/icon-edit.png")));
			} else if (action.equals("Delete")) {
				setIcon(new ImageIcon(getClass().getResource("/resources/icon-trash-32.png")));
			}
			return this;
		}
	}

	// Editor cho nút
	class ButtonEditor extends DefaultCellEditor {
		private JButton button;
		private String action;
		private JPanelCoupon parent;
		private int row;

		public ButtonEditor(JCheckBox checkBox, String action, JPanelCoupon parent) {
			super(checkBox);
			this.action = action;
			this.parent = parent;
			button = new JButton();
			button.setFocusPainted(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			if (action.equals("Edit")) {
				button.setIcon(new ImageIcon(getClass().getResource("/resources/icon-edit.png")));
			} else if (action.equals("Delete")) {
				button.setIcon(new ImageIcon(getClass().getResource("/resources/icon-trash-32.png")));
			}

			button.addActionListener(e -> {
				if (action.equals("Edit")) {
					int id = Integer.parseInt(parent.table.getValueAt(row, 8).toString());
					CouponModel model = new CouponModel();
					Coupon coupon = model.findById(id);

					if (coupon != null) {
						JDialog dialog = new JDialog((Frame) null, "Edit Coupon", true);
						dialog.setUndecorated(true);

						JPanelAddCoupon panel = new JPanelAddCoupon(parent);
						panel.setCouponData(coupon);

						dialog.getContentPane().add(panel);
						dialog.pack();
						dialog.setLocationRelativeTo(parent);
						dialog.setVisible(true);
					}
				} else if (action.equals("Delete")) {
					int id = Integer.parseInt(parent.table.getValueAt(row, 8).toString());
					int confirm = JOptionPane.showConfirmDialog(parent, "Are you sure to delete this coupon?");
					if (confirm == JOptionPane.YES_OPTION) {
						CouponModel model = new CouponModel();
						if (model.delete(id)) {
							JOptionPane.showMessageDialog(parent, "Deleted successfully!");
							parent.load();
						} else {
							JOptionPane.showMessageDialog(parent, "Delete failed!");
						}
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
			return "";
		}
	}

	private class PublicRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			String publicStatus = value.toString();
			Color bgColor = Color.WHITE;
			Color textColor = Color.WHITE;

			switch (publicStatus) {
			case "Active":
				bgColor = new Color(198, 255, 198);
				textColor = new Color(0, 100, 0);
				break;
			case "Deactive":
				bgColor = new Color(255, 204, 204);
				textColor = new Color(139, 0, 0);
				break;
			}

			StatusBadge badge = new StatusBadge(publicStatus, bgColor, textColor);
			badge.setPreferredSize(new Dimension(120, 30));

			JPanel wrapper = new JPanel(new GridBagLayout());
			wrapper.setOpaque(false);
			wrapper.add(badge, new GridBagConstraints());

			// thêm cursor cho wrapper
			wrapper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			return wrapper;
		}
	}

	public class StatusBadge extends JLabel {
		private Color bgColor;
		private Color textColor;

		public StatusBadge(String text, Color bgColor, Color textColor) {
			super(text);
			this.bgColor = bgColor;
			this.textColor = textColor;
			setFont(new Font("SansSerif", Font.BOLD, 13));
			setForeground(textColor);
			setOpaque(false);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int arc = 20;
			int width = getWidth();
			int height = getHeight();

			g2.setColor(bgColor);
			g2.fillRoundRect(0, 0, width, height, arc, arc);
			super.paintComponent(g);
			g2.dispose();
		}
	}

	public void load() {
		DefaultTableModel modelTable = (DefaultTableModel) table.getModel();
		loadData(modelTable);
	}

}

