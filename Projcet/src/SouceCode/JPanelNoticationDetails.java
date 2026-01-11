package SouceCode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class JPanelNoticationDetails extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel lblTitle;
	private JLabel lblStaff;
	private JLabel lblSentAt;
	private JTextArea txtDescription;

	private Point dragPoint = new Point();

	public JPanelNoticationDetails() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// Title
		lblTitle = new JLabel("Notification Title");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setForeground(Color.DARK_GRAY);
		add(lblTitle, BorderLayout.NORTH);

		// Info panel
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBackground(Color.WHITE);

		lblStaff = new JLabel("Staff: ");
		lblStaff.setFont(new Font("SansSerif", Font.PLAIN, 16));
		lblStaff.setForeground(Color.BLACK);

		lblSentAt = new JLabel("Sent At: ");
		lblSentAt.setFont(new Font("SansSerif", Font.PLAIN, 16));
		lblSentAt.setForeground(Color.GRAY);

		infoPanel.add(lblStaff);
		infoPanel.add(lblSentAt);

		add(infoPanel, BorderLayout.WEST);

		// Description
		txtDescription = new JTextArea();
		txtDescription.setFont(new Font("SansSerif", Font.PLAIN, 15));
		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		txtDescription.setEditable(false);
		txtDescription.setBackground(new Color(250, 250, 250));
		txtDescription.setBorder(BorderFactory.createTitledBorder("Description"));

		add(txtDescription, BorderLayout.CENTER);

		// Close button
		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFocusPainted(false);
		btnClose.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
		btnClose.setPreferredSize(new Dimension(100, 35));
		btnClose.addActionListener(e -> {
			java.awt.Window w = SwingUtilities.getWindowAncestor(this);
			if (w != null) {
				w.dispose();
			}
		});

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.add(btnClose);
		add(bottomPanel, BorderLayout.SOUTH);

		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				dragPoint.x = e.getX();
				dragPoint.y = e.getY();
			}
		});
		addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				Window w = SwingUtilities.getWindowAncestor(JPanelNoticationDetails.this);
				if (w != null) {
					Point p = w.getLocation();
					w.setLocation(p.x + e.getX() - dragPoint.x, p.y + e.getY() - dragPoint.y);
				}
			}
		});
	}

	public void setNotificationDetails(String staff, String title, String description, String sentAt) {
		lblTitle.setText(title);
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));

		lblStaff.setText("Staff: " + staff);
		lblSentAt.setText("Sent At: " + sentAt);
		txtDescription.setText(description);
	}
}
