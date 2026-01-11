package SouceCode;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class JPanelSuccess extends JPanel {

	private static final long serialVersionUID = 1L;

	public JPanelSuccess() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());

		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBorder(new EmptyBorder(20, 40, 20, 40));

		JPanel rowPanel = new JPanel();
		rowPanel.setOpaque(false);
		rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
		rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel titleLabel = new JLabel("\u2714"); // ✔
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
		titleLabel.setForeground(new Color(0, 128, 0));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel messageLabel = new JLabel("The operation was successfully completed.");
		messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		messageLabel.setForeground(new Color(34, 139, 34));
		messageLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

		rowPanel.add(titleLabel);
		rowPanel.add(messageLabel);

		content.add(rowPanel);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setOpaque(false);

		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("SansSerif", Font.BOLD, 14));
		btnClose.setBackground(new Color(34, 139, 34));
		btnClose.setForeground(Color.WHITE);
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnClose.setFocusPainted(false);
		btnClose.setPreferredSize(new Dimension(90, 30));

		btnClose.addActionListener(e -> {
			Window w = SwingUtilities.getWindowAncestor(btnClose);
			if (w != null) {
				w.dispose();
			}
		});

		bottomPanel.add(btnClose);
		content.add(bottomPanel);

		add(content, BorderLayout.CENTER);
	}

	public static void showSuccessDialog(JFrame parent) {
		JDialog dialog = new JDialog(parent);
		dialog.setModalityType(Dialog.ModalityType.MODELESS);
		dialog.setUndecorated(true);

		JPanelSuccess panel = new JPanelSuccess();
		dialog.getContentPane().add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);

		dialog.setVisible(true);
	}
}
