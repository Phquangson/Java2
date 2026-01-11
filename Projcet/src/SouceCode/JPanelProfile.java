package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

import entities.Staff;

import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.io.File;
import java.nio.file.Files;
import models.StaffModel;

import com.toedter.calendar.JDateChooser;
import java.util.Date;

public class JPanelProfile extends JPanel {

	private Staff staff;
	private Map<String, JTextField> fields = new HashMap<>();
	private JComboBox<String> genderCombo;
	private JDateChooser dateChooser;

	public JPanelProfile(Staff staff) {
		this.staff = staff;

		setLayout(new BorderLayout());
		setBackground(new Color(245, 245, 245));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel lblTitle = new JLabel("Profile", SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(33, 33, 33));
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
		lblTitle.setPreferredSize(new Dimension(0, 40));
		add(lblTitle, BorderLayout.NORTH);

		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), new EmptyBorder(40, 50, 40, 50)));
		card.setPreferredSize(new Dimension(550, 650));

		JLabel avatar = createAvatar();
		avatar.setPreferredSize(new Dimension(150, 150));
		avatar.setMaximumSize(new Dimension(150, 150));
		avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

		avatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		avatar.setToolTipText("Click to change avatar");

		avatar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int option = fileChooser.showOpenDialog(JPanelProfile.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						byte[] avatarBytes = java.nio.file.Files.readAllBytes(file.toPath());
						models.StaffModel staffModel = new models.StaffModel();
						boolean success = staffModel.updateAvatar(staff.getId(), avatarBytes);
						if (success) {
							staff.setLink(avatarBytes);
							avatar.repaint();
							JOptionPane.showMessageDialog(JPanelProfile.this, "Avatar updated successfully!");
						} else {
							JOptionPane.showMessageDialog(JPanelProfile.this, "Failed to update avatar.");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(JPanelProfile.this, "Error loading image.");
					}
				}
			}
		});

		card.add(avatar);
		card.add(Box.createVerticalStrut(30));

		if (staff != null) {
			card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

			card.add(createInfo("Username:", staff.getUsername()));
			card.add(Box.createVerticalStrut(10));
			card.add(createInfo("Full name:", staff.getFullName()));
			card.add(Box.createVerticalStrut(10));
			card.add(createGenderInfo("Gender:", staff.getGender() == 1 ? "Male" : "Female"));
			card.add(Box.createVerticalStrut(10));
			card.add(createInfo("Email:", staff.getEmail()));
			card.add(Box.createVerticalStrut(10));
			card.add(createDateChooser("Date of Birth:", staff.getDob()));
			card.add(Box.createVerticalStrut(10));
			card.add(createInfo("Role:", getRoleTitle(staff.getIdPosition())));
			card.add(Box.createVerticalStrut(10));
			card.add(createInfo("Phone:", staff.getPhone()));
			card.add(Box.createVerticalStrut(10));
		}

		JButton btnCancel = new JButton("Cancel");
		styleButton(btnCancel, new Color(220, 53, 69), Color.WHITE);

		btnCancel.addActionListener(e -> {
			if (staff != null) {
				if (fields.containsKey("Full name:")) {
					fields.get("Full name:").setText(staff.getFullName());
				}
				if (genderCombo != null) {
					genderCombo.setSelectedItem(staff.getGender() == 1 ? "Male" : "Female");
				}
				if (fields.containsKey("Email:")) {
					fields.get("Email:").setText(staff.getEmail());
				}
				if (fields.containsKey("Phone:")) {
					fields.get("Phone:").setText(staff.getPhone());
				}
			}
		});

		JButton btnEdit = new JButton("Update");
		styleButton(btnEdit, new Color(255, 193, 7), Color.BLACK);

		btnEdit.addActionListener(e -> {
			StaffModel staffModel = new StaffModel();
			staff.setFullName(fields.get("Full name:").getText());
			staff.setEmail(fields.get("Email:").getText());
			staff.setGender("Male".equals(genderCombo.getSelectedItem()) ? 1 : 0);
			staff.setPhone(fields.get("Phone:").getText());
			staff.setDob(dateChooser.getDate());

			boolean success = staffModel.updateBasicInfo(staff);
			if (success) {
				JOptionPane.showMessageDialog(this, "Information updated successfully!");
			} else {
				JOptionPane.showMessageDialog(this, "Failed to update information.");
			}
		});

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnPanel.setBackground(Color.WHITE);
		btnPanel.add(btnCancel);
		btnPanel.add(btnEdit);
		card.add(btnPanel);

		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setBackground(new Color(245, 245, 245));
		centerPanel.add(card);
		add(centerPanel, BorderLayout.CENTER);
	}

	private JLabel createAvatar() {
		return new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int w = getWidth();
				int h = getHeight();

				if (staff != null && staff.getLink() != null && staff.getLink().length > 0) {
					try {
						BufferedImage img = ImageIO.read(new ByteArrayInputStream(staff.getLink()));
						if (img != null) {
							Image scaledImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);

							BufferedImage circleBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
							Graphics2D gCircle = circleBuffer.createGraphics();
							gCircle.setClip(new Ellipse2D.Float(0, 0, w, h));
							gCircle.drawImage(scaledImg, 0, 0, null);
							gCircle.dispose();

							g2.drawImage(circleBuffer, 0, 0, null);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					g2.setColor(new Color(230, 230, 230));
					g2.fillOval(0, 0, w, h);
					g2.setColor(new Color(180, 180, 180));
					g2.setStroke(new BasicStroke(2));
					g2.drawOval(0, 0, w, h);

					g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
					FontMetrics fm = g2.getFontMetrics();
					String text = "Avatar";
					int x = (w - fm.stringWidth(text)) / 2;
					int y = (h + fm.getAscent()) / 2 - 4;
					g2.setColor(Color.DARK_GRAY);
					g2.drawString(text, x, y);
				}
				g2.dispose();
			}
		};
	}

	private JPanel createInfo(String label, String value) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setMaximumSize(new Dimension(450, 40));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 10);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel lbl = new JLabel(label);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lbl.setPreferredSize(new Dimension(120, 35));

		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lbl, gbc);

		if ("Username:".equals(label) || "Role:".equals(label)) {
			JLabel txtVal = new JLabel(value);
			txtVal.setFont(new Font("Segoe UI", Font.PLAIN, 18));
			txtVal.setForeground(new Color(80, 80, 80));
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(txtVal, gbc);

		} else if ("Gender:".equals(label)) {
			JComboBox<String> comboGender = new JComboBox<>(new String[] { "Male", "Female" });
			comboGender.setFont(new Font("Segoe UI", Font.PLAIN, 18));
			comboGender.setSelectedItem(value);
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(comboGender, gbc);

		} else if ("Phone:".equals(label)) {
			JTextField txtVal = new JTextField(value);
			txtVal.setFont(new Font("Segoe UI", Font.PLAIN, 18));
			txtVal.setForeground(new Color(80, 80, 80));
			txtVal.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

			((AbstractDocument) txtVal.getDocument()).setDocumentFilter(new DocumentFilter() {
				@Override
				public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
						throws BadLocationException {
					if (string != null && string.matches("\\d+")
							&& fb.getDocument().getLength() + string.length() <= 10) {
						super.insertString(fb, offset, string, attr);
					}
				}

				@Override
				public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
						throws BadLocationException {
					if (text != null && text.matches("\\d+")
							&& fb.getDocument().getLength() - length + text.length() <= 10) {
						super.replace(fb, offset, length, text, attrs);
					}
				}
			});

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(txtVal, gbc);

			fields.put(label, txtVal);

		} else {
			JTextField txtVal = new JTextField(value);
			txtVal.setFont(new Font("Segoe UI", Font.PLAIN, 18));
			txtVal.setForeground(new Color(80, 80, 80));
			txtVal.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(txtVal, gbc);

			fields.put(label, txtVal);
		}

		return panel;
	}

	private JPanel createGenderInfo(String label, String value) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setMaximumSize(new Dimension(450, 40));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 10);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel lbl = new JLabel(label);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lbl.setPreferredSize(new Dimension(120, 35));

		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lbl, gbc);

		genderCombo = new JComboBox<>(new String[] { "Male", "Female" });
		genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		genderCombo.setSelectedItem(value);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(genderCombo, gbc);

		return panel;
	}

	private void styleButton(JButton btn, Color bg, Color fg) {
		btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btn.setBackground(bg);
		btn.setForeground(fg);
		btn.setFocusPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(170, 40));
		btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));

		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(bg.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(bg);
			}
		});
	}

	private String getRoleTitle(int id) {
		switch (id) {
		case 1:
			return "Admin";
		case 2:
			return "Warehouse Manager";
		case 3:
			return "Manager";
		case 4:
			return "Sales";
		case 5:
			return "Super Admin";
		default:
			return "Unknown";
		}
	}

	private JPanel createDateChooser(String label, Date value) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setMaximumSize(new Dimension(450, 40));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 10);
		gbc.anchor = GridBagConstraints.WEST;
		JLabel lbl = new JLabel(label);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lbl.setPreferredSize(new Dimension(120, 35));
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lbl, gbc);
		dateChooser = new JDateChooser();
		dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateChooser.setDate(value);
		dateChooser.setDateFormatString("dd/MM/yyyy");
		dateChooser.setMaxSelectableDate(new Date());

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(dateChooser, gbc);

		return panel;
	}

}
