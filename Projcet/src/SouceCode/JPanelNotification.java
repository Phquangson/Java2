package SouceCode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import entities.Notification;
import models.NotificationModel;

public class JPanelNotification extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableNotifications;
	private DefaultTableModel tableModel;

	public JPanelNotification() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel lblTitle = new JLabel("Staff Notifications");
		lblTitle.setOpaque(true);
		lblTitle.setBackground(new Color(247, 222, 155));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		lblTitle.setForeground(Color.DARK_GRAY);
		lblTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(lblTitle, BorderLayout.NORTH);

		tableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "ID", "No.", "Staff", "Title", "Description", "Sent At", "Status" }) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableNotifications = new JTable(tableModel);

		tableNotifications.setAutoCreateRowSorter(true);

		tableNotifications.removeColumn(tableNotifications.getColumnModel().getColumn(0));

		tableNotifications.setFont(new Font("SansSerif", Font.PLAIN, 14));
		tableNotifications.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
		tableNotifications.setRowHeight(70);
		tableNotifications.setIntercellSpacing(new java.awt.Dimension(0, 20));
		tableNotifications.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

//		tableNotifications.getColumn("Action").setCellRenderer(new ButtonRenderer());
//		tableNotifications.getColumn("Action").setCellEditor(new ButtonEditor());

		JScrollPane scrollPane = new JScrollPane(tableNotifications);

		tableNotifications.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = tableNotifications.getSelectedRow();
				if (row != -1) {
					// vì đã remove cột ID, cần lấy theo modelIndex
					int modelRow = tableNotifications.convertRowIndexToModel(row);

					String staff = tableModel.getValueAt(modelRow, 2).toString();
					String title = tableModel.getValueAt(modelRow, 3).toString();
					String description = tableModel.getValueAt(modelRow, 4).toString();
					String sentAt = tableModel.getValueAt(modelRow, 5).toString();

					JPanelNoticationDetails detailsPanel = new JPanelNoticationDetails();
					detailsPanel.setNotificationDetails(staff, title, description, sentAt);

					javax.swing.JDialog dialog = new javax.swing.JDialog();
					dialog.setTitle("Notification Details");
					dialog.setUndecorated(true);
					dialog.setModal(true);
					dialog.getContentPane().add(detailsPanel);
					dialog.setSize(500, 400);
					dialog.setLocationRelativeTo(JPanelNotification.this);
					dialog.setVisible(true);

					Object currentStatus = tableModel.getValueAt(modelRow, 6);
					if (currentStatus != null && "Unseen".equalsIgnoreCase(currentStatus.toString())) {
						tableModel.setValueAt("Seen", modelRow, 6);

						int notificationId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
						NotificationModel model = new NotificationModel();
						model.updateStatus(notificationId, 14);
					}
				}
			}
		});

		tableNotifications.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				tableNotifications.getTableHeader().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
			}
		});
		tableNotifications.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				tableNotifications.getTableHeader().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			}
		});

		tableNotifications.getColumn("Status").setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				String statusText = value != null ? value.toString() : "Unknown";
				Color bgColor = Color.LIGHT_GRAY;
				Color textColor = Color.BLACK;

				if ("Unseen".equalsIgnoreCase(statusText)) {
					bgColor = new Color(255, 204, 204);
					textColor = new Color(139, 0, 0);
				} else if ("Seen".equalsIgnoreCase(statusText)) {
					bgColor = new Color(198, 255, 198);
					textColor = new Color(0, 100, 0);
				}

				StatusBadge badge = new StatusBadge(statusText, bgColor, textColor);
				badge.setPreferredSize(new Dimension(120, 30));

				JPanel wrapper = new JPanel(new GridBagLayout());
				wrapper.setOpaque(false);
				wrapper.add(badge, new GridBagConstraints());

				return wrapper;
			}
		});

		add(scrollPane, BorderLayout.CENTER);

		loadNotificationsFromDatabase();
	}

	public class StatusBadge extends JLabel {
		private Color bgColor;
		private Color textColor;

		public StatusBadge(String text, Color bgColor, Color textColor) {
			super(text);
			this.bgColor = bgColor;
			this.textColor = textColor;
			setFont(new Font("SansSerif", Font.BOLD, 13));
			setForeground(textColor);
			setOpaque(false);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int arc = 20;
			int width = getWidth();
			int height = getHeight();

			g2.setColor(bgColor);
			g2.fillRoundRect(0, 0, width, height, arc, arc);
			super.paintComponent(g);
			g2.dispose();
		}
	}

	public void addNotification(String staff, String title, String description) {
		int stt = tableModel.getRowCount() + 1;
		String sentAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

		Vector<Object> row = new Vector<>();
		row.add(0);
		row.add(stt);
		row.add(staff);
		row.add(title);
		row.add(description);
		row.add(sentAt);
		row.add("Unseen");
		row.add("");

		tableModel.insertRow(0, row);
	}

	private void loadNotificationsFromDatabase() {
		NotificationModel model = new NotificationModel();
		var notifications = model.findAllNotification();

		int stt = 1;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

		for (Notification n : notifications) {
			Vector<Object> row = new Vector<>();
			row.add(n.getId());
			row.add(stt++);
			row.add(n.getIdCreator());
			row.add(n.getTitle());
			row.add(n.getContent());

			LocalDateTime ldt = n.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault())
					.toLocalDateTime();
			row.add(ldt.format(formatter));

			row.add(n.getStatusTitle() != null ? n.getStatusTitle() : "Unknown");

			row.add("");
			tableModel.addRow(row);
		}

	}

	class ButtonRenderer extends JButton implements TableCellRenderer {
		public ButtonRenderer() {
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icon-details.png")));
			setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		private JButton button;
		private int row;

		public ButtonEditor() {
			button = new JButton();
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icon-details.png")));
			button.addActionListener(this);
			button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

		@Override
		public void actionPerformed(ActionEvent e) {
			String staff = tableModel.getValueAt(row, 2).toString();
			String title = tableModel.getValueAt(row, 3).toString();
			String description = tableModel.getValueAt(row, 4).toString();
			String sentAt = tableModel.getValueAt(row, 5).toString();

			JPanelNoticationDetails detailsPanel = new JPanelNoticationDetails();
			detailsPanel.setNotificationDetails(staff, title, description, sentAt);

			javax.swing.JDialog dialog = new javax.swing.JDialog();
			dialog.setTitle("Notification Details");
			dialog.setUndecorated(true);
			dialog.setModal(true);
			dialog.getContentPane().add(detailsPanel);
			dialog.setSize(500, 400);
			dialog.setLocationRelativeTo(JPanelNotification.this);
			dialog.setVisible(true);

			Object currentStatus = tableModel.getValueAt(row, 6);

			if (currentStatus != null && "Unseen".equalsIgnoreCase(currentStatus.toString())) {
				tableModel.setValueAt("Seen", row, 6);

				int notificationId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

				NotificationModel model = new NotificationModel();
				model.updateStatus(notificationId, 14);
			}

			fireEditingStopped();
		}

	}
}
