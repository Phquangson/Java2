package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.util.List;
import entities.Inventory;
import entities.Product;

public class JPanelNotificationProduct extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;

	public JPanelNotificationProduct(List<Inventory> inventories) {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);

		JLabel title = new JLabel("Product Notifications", SwingConstants.CENTER);
		title.setOpaque(true);
		title.setBackground(new Color(255, 70, 70));
		title.setFont(new Font("SansSerif", Font.BOLD, 20));
		title.setForeground(Color.WHITE);
		title.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(title, BorderLayout.NORTH);

		DefaultTableModel model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		model.addColumn("No.");
		model.addColumn("Product Name");
		model.addColumn("Status");

		int stt = 1;
		for (Inventory inventory : inventories) {
			Product product = inventory.getProduct();

			String status = getStatusById(product.getIdStatus());

			if (!status.equals("In stock") && !status.equals("Delete")) {
				model.addRow(new Object[] { stt++, product.getTitle(), createStatusBadge(status) });
			}

		}

		table = new JTable(model);
		table.setRowHeight(70);
		table.setIntercellSpacing(new Dimension(0, 20));
		table.getColumnModel().getColumn(2).setCellRenderer(new BadgeRenderer());
		table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);
		JTableHeader header = table.getTableHeader();
		header.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		add(scrollPane, BorderLayout.CENTER);

		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnClose.setFocusPainted(false);
		btnClose.setPreferredSize(new Dimension(120, 35));

		btnClose.addActionListener(e -> {
			Window w = SwingUtilities.getWindowAncestor(btnClose);
			if (w != null) {
				w.dispose();
			}
		});

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.add(btnClose);

		add(bottomPanel, BorderLayout.SOUTH);
	}

	private String getStatusById(int id) {
		switch (id) {
		case 6:
			return "In stock";
		case 16:
			return "Out of stock";
		case 17:
			return "Need to import";
		case 18:
			return "Almost out of stock";
		case 19:
			return "Delete";
		default:
			return "Unknown";
		}
	}

	private StatusBadge createStatusBadge(String status) {
		Color bgColor = Color.WHITE;
		Color textColor = Color.BLACK;

		switch (status) {
		case "Out of stock":
			bgColor = new Color(255, 180, 180);
			textColor = new Color(150, 0, 0);
			break;

		case "Almost out of stock":
			bgColor = new Color(255, 230, 150);
			textColor = new Color(120, 80, 0);
			break;

		case "Need to import":
			bgColor = new Color(180, 220, 230);
			textColor = new Color(0, 90, 110);
			break;

		case "Delete":
			bgColor = new Color(200, 0, 0);
			textColor = Color.WHITE;
			break;
		}

		return new StatusBadge(status, bgColor, textColor);
	}

	public class StatusBadge extends JLabel {
		private Color bgColor;

		public StatusBadge(String text, Color bgColor, Color textColor) {
			super(text);
			this.bgColor = bgColor;
			setFont(new Font("SansSerif", Font.BOLD, 13));
			setForeground(textColor);
			setHorizontalAlignment(CENTER);
			setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(bgColor);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
			super.paintComponent(g);
			g2.dispose();
		}
	}

	private class BadgeRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			if (value instanceof StatusBadge) {
				JPanel panel = new JPanel(new GridBagLayout());
				panel.setOpaque(false);
				panel.add((StatusBadge) value);
				return panel;
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
