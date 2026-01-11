package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class JPanelProductsReceiptNoteDetails extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableProducts;
	private JLabel lblTitle;

	public JPanelProductsReceiptNoteDetails() {
		setLayout(new BorderLayout(15, 15));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 20, 20, 20));

		lblTitle = new JLabel("List of products received into inventory");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setForeground(Color.DARK_GRAY);
		lblTitle.setBorder(new EmptyBorder(8, 8, 8, 8));
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

		String[] columnNames = { "No.", "Name", "Supplier", "Quantity", "Unit price", "Total amount" };
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableProducts = new JTable(tableModel);
		tableProducts.setRowHeight(26);
		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 13));
		tableProducts.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

		JScrollPane scrollPane = new JScrollPane(tableProducts);
		scrollPane.setBorder(BorderFactory.createTitledBorder(""));
		add(scrollPane, BorderLayout.CENTER);

		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFocusPainted(false);
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnClose.setPreferredSize(new Dimension(100, 35));
		btnClose.addActionListener(e -> {
			Window w = SwingUtilities.getWindowAncestor(this);
			if (w != null) {
				w.dispose();
			}
		});

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.add(btnClose);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void addProduct(int stt, String name, String supplier, String quantity, String unitPrice, String totalCost) {
		DefaultTableModel model = (DefaultTableModel) tableProducts.getModel();
		model.addRow(new Object[] { stt, name, supplier, quantity, unitPrice, totalCost });
	}
}
