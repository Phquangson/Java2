package SouceCode;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import entities.Notification;
import models.NotificationModel;

public class JPanelNotificationSales extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField txtCreator;
	private JTextField txtTitle;
	private JTextArea txtDescription;

	public JPanelNotificationSales() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel panelHeader = new JPanel();
		panelHeader.setBackground(new Color(247, 222, 155));
		JLabel lblTitleHeader = new JLabel("Notification");
		lblTitleHeader.setForeground(Color.BLACK);
		lblTitleHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
		panelHeader.add(lblTitleHeader);
		add(panelHeader, BorderLayout.NORTH);

		JPanel panelFormWrapper = new JPanel(new GridBagLayout());
		panelFormWrapper.setBackground(Color.WHITE);

		JPanel panelForm = new JPanel();
		panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
		panelForm.setBackground(Color.WHITE);
		panelForm.setBorder(new TitledBorder(null, "Notification Info", TitledBorder.LEADING, TitledBorder.TOP, null,
				Color.DARK_GRAY));
		panelForm.setPreferredSize(new Dimension(600, 380));

		JPanel rowCreator = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		rowCreator.setBackground(new Color(192, 192, 192));
		JLabel lblCreator = new JLabel("Creator:");
		lblCreator.setPreferredSize(new Dimension(80, 25));

		txtCreator = new JTextField("Sale", 35);
		txtCreator.setEditable(false);
		txtCreator.setEnabled(false);

		rowCreator.add(lblCreator);
		rowCreator.add(txtCreator);
		panelForm.add(rowCreator);

		JPanel rowTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		rowTitle.setBackground(new Color(192, 192, 192));
		JLabel lblTitle = new JLabel("Title:");
		lblTitle.setPreferredSize(new Dimension(80, 25));
		txtTitle = new JTextField(35);
		rowTitle.add(lblTitle);
		rowTitle.add(txtTitle);
		panelForm.add(rowTitle);

		JPanel rowDescription = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		rowDescription.setBackground(new Color(192, 192, 192));
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setPreferredSize(new Dimension(80, 25));
		txtDescription = new JTextArea(6, 35);
		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		JScrollPane scrollDesc = new JScrollPane(txtDescription);
		scrollDesc.setPreferredSize(new Dimension(400, 120));
		rowDescription.add(lblDescription);
		rowDescription.add(scrollDesc);
		panelForm.add(rowDescription);

		JPanel rowButton = new JPanel(new FlowLayout(FlowLayout.CENTER));
		rowButton.setBackground(Color.LIGHT_GRAY);
		JButton btnSave = new JButton("Save Notification");
		btnSave.setPreferredSize(new Dimension(200, 40));
		btnSave.setBackground(Color.DARK_GRAY);
		btnSave.setForeground(Color.WHITE);
		btnSave.setFont(new Font("SansSerif", Font.BOLD, 16));

		btnSave.addActionListener(e -> {
			String creator = getCreator();
			String title = getTitle();
			String description = getDescription();

			Notification notification = new Notification();
			notification.setTitle(title);
			notification.setContent(description);
			notification.setIdCreator(creator);

			notification.setIdUpdater("staff");

			NotificationModel model = new NotificationModel();
			boolean success = model.insertNotification(notification);

			if (success) {
				JOptionPane.showMessageDialog(this, "Notification sent successfully.");
				txtTitle.setText("");
				txtDescription.setText("");
			} else {
				JOptionPane.showMessageDialog(this, "Save the failure notification!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		rowButton.add(btnSave);
		panelForm.add(rowButton);

		panelFormWrapper.add(panelForm, new GridBagConstraints());
		add(panelFormWrapper, BorderLayout.CENTER);
	}

	public String getCreator() {
		return txtCreator.getText();
	}

	public String getTitle() {
		return txtTitle.getText();
	}

	public String getDescription() {
		return txtDescription.getText();
	}
}
