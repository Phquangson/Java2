package SouceCode;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import models.StatusModel;
import entities.Status;
import javax.swing.border.TitledBorder;

public class JPanelStatus extends JPanel {

	JTable table;
	private JTextField txtTitle;
	StatusModel model = new StatusModel();

	public JPanelStatus() {
		setMaximumSize(new Dimension(3276, 3276));
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//		JLabel lblTitle = new JLabel("Status", SwingConstants.CENTER);
//		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
//		lblTitle.setForeground(Color.BLACK);
//		lblTitle.setBackground(new Color(247, 222, 155));
//		lblTitle.setOpaque(true);
//		lblTitle.setPreferredSize(new Dimension(0, 40));
//		add(lblTitle, BorderLayout.NORTH);

		JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		formPanel
				.setBorder(new TitledBorder(null, "Search status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		formPanel.setBackground(Color.WHITE);

		txtTitle = new JTextField(15);
		JButton btnAdd = new JButton("Add");
		btnAdd.setIcon(new ImageIcon(JPanelStatus.class.getResource("/resources/icon-add.png")));
		styleButton(btnAdd);

		JLabel label = new JLabel("Status:");
		label.setFont(new Font("SansSerif", Font.BOLD, 14));
		formPanel.add(label);
		formPanel.add(txtTitle);

		JButton btnClear = new JButton("");
		btnClear.setBackground(new Color(192, 192, 192));
		btnClear.setIcon(new ImageIcon(JPanelStatus.class.getResource("/resources/icon-trash.png")));
		styleButton(btnClear);
		formPanel.add(btnClear);

		btnClear.addActionListener(e -> {
			txtTitle.setText("");
			load();
		});
		formPanel.add(btnAdd);

		txtTitle.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void updateSearch() {
				String keyword = txtTitle.getText().trim();
				if (keyword.isEmpty()) {
					load();
				} else {
					searchAndLoad(keyword);
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

		DefaultTableModel modelTable = new DefaultTableModel(new Object[] { "No.", "Status", "ID", "Update", "Delete" },
				0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				Object idValue = getValueAt(row, 2);
				if (!(idValue instanceof Integer)) {
					return false;
				}
				return column == 3 || column == 4;
			}
		};

		table = new JTable(modelTable);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelTable);
		table.setRowSorter(sorter);

		table.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				table.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});

		table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				table.getTableHeader().setCursor(Cursor.getDefaultCursor());
			}
		});

		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(0);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(80);

		table.getColumnModel().getColumn(2).setMinWidth(0);
		table.getColumnModel().getColumn(2).setMaxWidth(0);
		table.getColumnModel().getColumn(2).setPreferredWidth(0);

		table.getColumn("Update").setCellRenderer(new ButtonRenderer("Update"));
		table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

		table.getColumn("Update").setCellEditor(new ButtonEditor(new JCheckBox(), "Update", this));
		table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", this));

		table.setRowHeight(50);
		table.setIntercellSpacing(new Dimension(0, 10));
		table.setShowGrid(false);
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.add(formPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);

		load();

		btnAdd.addActionListener(e -> {
			JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Status", true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			dialog.setUndecorated(true);

			JPanelAddStatus panelAddStatus = new JPanelAddStatus(this);
			dialog.getContentPane().add(panelAddStatus);

			dialog.pack();
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);

			load();
		});

	}

	class ButtonRenderer extends JButton implements TableCellRenderer {
		private String action;

		public ButtonRenderer(String action) {
			this.action = action;
			setFont(new Font("SansSerif", Font.BOLD, 12));
			setFocusPainted(false);
			setContentAreaFilled(false);
			setBorderPainted(false);

			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value == null || value.toString().isEmpty()) {
				setIcon(null);
				setText("");
			} else {
				if (action.equals("Update")) {
					setIcon(new ImageIcon(getClass().getResource("/resources/icon-edit.png")));
				} else if (action.equals("Delete")) {
					setIcon(new ImageIcon(getClass().getResource("/resources/icon-trash-32.png")));
				}
			}
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		private JButton button;
		private String action;
		private JPanelStatus parent;
		private int row;

		public ButtonEditor(JCheckBox checkBox, String action, JPanelStatus parent) {
			super(checkBox);
			this.action = action;
			this.parent = parent;
			button = new JButton();
			button.setFocusPainted(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			if (action.equals("Update")) {
				button.setIcon(new ImageIcon(getClass().getResource("/resources/icon-edit.png")));
			} else if (action.equals("Delete")) {
				button.setIcon(new ImageIcon(getClass().getResource("/resources/icon-trash-32.png")));
			}

			button.addActionListener(e -> {
				Object idValue = parent.table.getValueAt(row, 2);

				if (!(idValue instanceof Integer)) {
					return;
				}

				int id = (Integer) idValue;
				String title = parent.table.getValueAt(row, 1).toString();

				if (action.equals("Update")) {
					Status status = StatusModel.findById(id);
					if (status != null) {
						JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Update Status",
								true);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

						dialog.setUndecorated(true);

						JPanelAddStatus panelAddStatus = new JPanelAddStatus(parent);
						panelAddStatus.setStatusData(status);

						dialog.getContentPane().add(panelAddStatus);
						dialog.pack();
						dialog.setLocationRelativeTo(parent);
						dialog.setVisible(true);

						parent.load();
					}
				}

				else if (action.equals("Delete")) {
					int confirm = JOptionPane.showConfirmDialog(parent, "Delete this status?", "Confirm",
							JOptionPane.YES_NO_OPTION);
					if (confirm == JOptionPane.YES_OPTION) {
						boolean deleted = parent.model.delete(id);
						if (deleted) {
							JOptionPane.showMessageDialog(parent, "Status deleted successfully");
							parent.load();
						} else {
							JOptionPane.showMessageDialog(parent, "Delete failed", "Error", JOptionPane.ERROR_MESSAGE);
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

	void load() {
		DefaultTableModel m = (DefaultTableModel) table.getModel();
		m.setRowCount(0);
		int stt = 1;
		for (Status s : model.findAll()) {
			m.addRow(new Object[] { stt++, s.getTitle(), s.getId(), "Update", "Delete" });
		}
	}

	private boolean isDuplicate(String title, Integer ignoreId) {
		for (Status s : model.findAll()) {
			if (s.getTitle().equalsIgnoreCase(title)) {
				if (ignoreId == null || s.getId() != ignoreId) {
					return true;
				}
			}
		}
		return false;
	}

	private void searchAndLoad(String keyword) {
		DefaultTableModel m = (DefaultTableModel) table.getModel();
		m.setRowCount(0);
		int stt = 1;
		boolean found = false;

		for (Status s : model.findAll()) {
			if (s.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
				m.addRow(new Object[] { stt++, s.getTitle(), s.getId(), "Update", "Delete" });
				found = true;
			}
		}

		if (!found) {
			m.addRow(new Object[] { "", "No matching statuses found", null, "", "" });

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		}
	}

}
