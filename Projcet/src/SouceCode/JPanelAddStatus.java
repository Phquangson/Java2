package SouceCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import models.StatusModel;
import entities.Status;

public class JPanelAddStatus extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton btnAdd;
	private JTextField txtStatus;
	private JLabel lblTitle;

	private JPanelStatus parent;

	private Point initialClick;

	public JPanelAddStatus(JPanelStatus parent) {
		this.parent = parent;
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(400, 200));

		lblTitle = new JLabel("Add Status", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(60, 60, 60));
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setOpaque(true);
		add(lblTitle, BorderLayout.NORTH);

		JPanel panelCenter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
		panelCenter.setBackground(Color.WHITE);

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 16));
		lblStatus.setForeground(new Color(50, 50, 50));

		txtStatus = new JTextField(25);
		txtStatus.setFont(new Font("SansSerif", Font.PLAIN, 14));

		panelCenter.add(lblStatus);
		panelCenter.add(txtStatus);

		add(panelCenter, BorderLayout.CENTER);

		btnAdd = new JButton("Add Status");
		btnAdd.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnAdd.setBackground(Color.GRAY);
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFocusPainted(false);
		btnAdd.setPreferredSize(new Dimension(160, 40));

		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 16));
		btnClose.setBackground(new Color(255, 70, 70));
		btnClose.setForeground(Color.WHITE);
		btnClose.setFocusPainted(false);
		btnClose.setPreferredSize(new Dimension(120, 40));
		btnClose.addActionListener(e -> closeDialog());
		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		panelBottom.setBackground(Color.WHITE);
		panelBottom.add(btnClose);
		panelBottom.add(btnAdd);
		add(panelBottom, BorderLayout.SOUTH);

		// Action
		btnAdd.addActionListener(e -> {
			String statusText = txtStatus.getText().trim();
			if (!statusText.isEmpty()) {
				StatusModel model = new StatusModel();
				if (model.insert(statusText)) {
					JOptionPane.showMessageDialog(this, "Status added successfully!");
					if (parent != null) {
						parent.load();
					}
					closeDialog();
				} else {
					JOptionPane.showMessageDialog(this, "Failed to add status!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please enter a status before adding.");
			}
		});
		enableDrag();

	}

	private void closeDialog() {
		java.awt.Window w = SwingUtilities.getWindowAncestor(this);
		if (w != null) {
			w.dispose();
		}
	}

	public void setStatusData(entities.Status status) {
		txtStatus.setText(status.getTitle());
		lblTitle.setText("Update Status");
		btnAdd.setText("Update Status");

		for (ActionListener al : btnAdd.getActionListeners()) {
			btnAdd.removeActionListener(al);
		}

		btnAdd.addActionListener(e -> {
			String newTitle = txtStatus.getText().trim();
			if (!newTitle.isEmpty()) {
				StatusModel model = new StatusModel();
				if (model.update(status.getId(), newTitle)) {
					JOptionPane.showMessageDialog(this, "Status updated successfully!");
					if (parent != null) {
						parent.load();
					}
					closeDialog();
				} else {
					JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please enter a status before updating.");
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
				Window window = SwingUtilities.getWindowAncestor(JPanelAddStatus.this);
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

}
