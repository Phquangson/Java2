package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class JPanelProductsIssueNoteDetails extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableProducts;

	public JPanelProductsIssueNoteDetails() {
		setLayout(new BorderLayout(15, 15));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 20, 20, 20));

		JLabel lblTitle = new JLabel("List of products shipped from warehouse");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setForeground(Color.DARK_GRAY);
		lblTitle.setBorder(new EmptyBorder(8, 8, 8, 8));
		add(lblTitle, BorderLayout.NORTH);

		// Cho phép kéo thả di chuyển cửa sổ qua lblTitle
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

		String[] columnNames = { "No.", "Name", "Quantity", "Unit price", "Total amount" };
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
		tableProducts.setIntercellSpacing(new Dimension(0, 5));

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(40);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(2).setPreferredWidth(80);
		tableProducts.getColumnModel().getColumn(3).setPreferredWidth(100);
		tableProducts.getColumnModel().getColumn(4).setPreferredWidth(120);

		tableProducts.setPreferredScrollableViewportSize(new Dimension(600, 200));

		JScrollPane scrollPane = new JScrollPane(tableProducts);
		scrollPane.setBorder(BorderFactory.createTitledBorder(""));
		add(scrollPane, BorderLayout.CENTER);

		// Thêm nút Close
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

	public void addProduct(int stt, String name, String quantity, String unitPrice, String totalCost) {
		DefaultTableModel model = (DefaultTableModel) tableProducts.getModel();
		model.addRow(new Object[] { stt, name, quantity, unitPrice, totalCost });
	}
}
