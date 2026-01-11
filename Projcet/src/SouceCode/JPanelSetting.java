package SouceCode;

import javax.swing.*;
import java.awt.*;

public class JPanelSetting extends JPanel {

	private JPanel content;

	public JPanelSetting() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// ===== Header (tiêu đề + menu) =====
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBackground(Color.WHITE);

		// Tiêu đề
		JLabel lblTitle = new JLabel("Setting", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(0, 0, 0));
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setOpaque(true);
		lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTitle.setMaximumSize(new Dimension(2147483647, 40)); // cho phép giãn ngang
		lblTitle.setPreferredSize(new Dimension(0, 40));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER); // căn giữa nội dung

		headerPanel.add(lblTitle);

		headerPanel.add(Box.createVerticalStrut(12)); // khoảng cách dưới tiêu đề

		// Menu nút
		JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
		menuPanel.setBackground(Color.WHITE);

		JButton btnStatus = createStyledButton("Status");
		JButton btnCategory = createStyledButton("Category");
		JButton btnType = createStyledButton("Type");
		JButton btnPosition = createStyledButton("Position");
		JButton btnCoupon = createStyledButton("Coupon");

		menuPanel.add(btnStatus);
		menuPanel.add(btnCategory);
		menuPanel.add(btnType);
		menuPanel.add(btnPosition);
		menuPanel.add(btnCoupon);

		headerPanel.add(menuPanel);

		add(headerPanel, BorderLayout.NORTH);

		// ===== Nội dung chính =====
		content = new JPanel(new BorderLayout());
		content.setBackground(Color.WHITE);
		add(content, BorderLayout.CENTER);

		// ===== Action =====
		btnStatus.addActionListener(e -> show(new JPanelStatus()));
		btnCategory.addActionListener(e -> show(new JPanelCategory()));
		btnType.addActionListener(e -> show(new JPanelType()));
		btnPosition.addActionListener(e -> show(new JPanelPosition()));
		btnCoupon.addActionListener(e -> show(new JPanelCoupon()));

		show(new JPanelStatus());
	}

	private JButton createStyledButton(String text) {
		JButton btn = new JButton(text);
		btn.setPreferredSize(new Dimension(130, 42));
		btn.setFont(new Font("SansSerif", Font.BOLD, 14));
		btn.setBackground(new Color(192, 192, 192));
		btn.setFocusPainted(false);
		btn.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
						BorderFactory.createEmptyBorder(5, 15, 5, 15)));

		// hiệu ứng hover
		Color normalBg = new Color(192, 192, 192);
		Color hoverBg = new Color(247, 222, 155);

		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btn.setBackground(hoverBg);
				btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				btn.setBackground(normalBg);
				btn.setCursor(Cursor.getDefaultCursor());
			}
		});
		
		

		return btn;
	}

	private void show(JPanel p) {
		content.removeAll();
		content.add(p, BorderLayout.CENTER);
		content.revalidate();
		content.repaint();
	}
}
