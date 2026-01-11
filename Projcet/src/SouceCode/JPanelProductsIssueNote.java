package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import entities.InventoryActivity;
import models.InventoryActivityModel;

import java.awt.*;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import entities.InventoryActivity;
import models.InventoryActivityModel;

public class JPanelProductsIssueNote extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableIssueNote;
	private JTextField textField;

	public JPanelProductsIssueNote() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);

		JPanel panelHeader = new JPanel(new GridBagLayout());
		panelHeader.setBackground(new Color(247, 222, 155));
		panelHeader.setPreferredSize(new Dimension(0, 40));

		JLabel lblTitle = new JLabel("Product Issue Note");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(0, 0, 0));

		panelHeader.add(lblTitle, new GridBagConstraints());

		JPanel panelTop = new JPanel(new BorderLayout());
		panelTop.add(panelHeader, BorderLayout.NORTH);

		add(panelTop, BorderLayout.NORTH);

		String[] columnNames = { "No.", "Image", "Receipt Code", "Creator", "Created Time", "Total Amount", "Action",
				"PDF", "Excel" };

		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 6 || column == 7 || column == 8;
			}
		};

		tableIssueNote = new JTable(tableModel);
		tableIssueNote.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableIssueNote.setRowHeight(70);
		tableIssueNote.setFillsViewportHeight(true);

		tableIssueNote.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				int col = tableIssueNote.columnAtPoint(e.getPoint());
				if (col == tableIssueNote.getColumn("Image").getModelIndex()
						|| col == tableIssueNote.getColumn("Action").getModelIndex()
						|| col == tableIssueNote.getColumn("PDF").getModelIndex()
						|| col == tableIssueNote.getColumn("Excel").getModelIndex()) {
					tableIssueNote.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					tableIssueNote.setCursor(Cursor.getDefaultCursor());
				}
			}
		});

		tableIssueNote.setIntercellSpacing(new Dimension(0, 20));

		tableIssueNote.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

		tableIssueNote.getColumnModel().getColumn(0).setPreferredWidth(60);
		tableIssueNote.getColumnModel().getColumn(1).setPreferredWidth(100);
		tableIssueNote.getColumnModel().getColumn(2).setPreferredWidth(120);
		tableIssueNote.getColumnModel().getColumn(3).setPreferredWidth(220);
		tableIssueNote.getColumnModel().getColumn(4).setPreferredWidth(100);
		tableIssueNote.getColumnModel().getColumn(5).setPreferredWidth(120);
		tableIssueNote.getColumnModel().getColumn(6).setPreferredWidth(80);
		tableIssueNote.getColumnModel().getColumn(7).setPreferredWidth(80);

		JScrollPane scrollPane = new JScrollPane(tableIssueNote);
		add(scrollPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Product search", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBackground(Color.WHITE);
		panel_1.add(panel_3);

		JLabel lblSearch = new JLabel("Search:");
		lblSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
		panel_3.add(lblSearch);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tableIssueNote.getModel());
		tableIssueNote.setRowSorter(sorter);

		tableIssueNote.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				tableIssueNote.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
		tableIssueNote.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				tableIssueNote.getTableHeader().setCursor(Cursor.getDefaultCursor());
			}
		});

		sorter.setComparator(0, (o1, o2) -> {
			try {
				Integer i1 = Integer.parseInt(o1.toString());
				Integer i2 = Integer.parseInt(o2.toString());
				return i1.compareTo(i2);
			} catch (Exception e) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		sorter.setComparator(3, (o1, o2) -> {
			try {
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				return df.parse(o1.toString()).compareTo(df.parse(o2.toString()));
			} catch (Exception e) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		sorter.setComparator(4, (o1, o2) -> {
			try {
				String s1 = o1.toString().replaceAll("[^0-9]", "");
				String s2 = o2.toString().replaceAll("[^0-9]", "");
				Long v1 = s1.isEmpty() ? 0L : Long.parseLong(s1);
				Long v2 = s2.isEmpty() ? 0L : Long.parseLong(s2);
				return v1.compareTo(v2);
			} catch (Exception e) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		textField = new JTextField();
		textField.setColumns(20);
		panel_3.add(textField);
		textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void updateSearch() {
				String keyword = textField.getText().trim();
				DefaultTableModel tableModel = (DefaultTableModel) tableIssueNote.getModel();
				tableModel.setRowCount(0);

				// Nếu keyword rỗng thì reset bảng về dữ liệu ban đầu
				if (keyword.isEmpty()) {
					init();
					return;
				}

				InventoryActivityModel model = new InventoryActivityModel();
				List<InventoryActivity> result = model.searchStockOut(keyword);

				int stt = 1;
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

				if (result == null || result.isEmpty()) {
					tableModel.addRow(new Object[] { "", null, "No delivery slip found", "", "", "", "", "", "" });

					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
					for (int i = 0; i < tableIssueNote.getColumnCount(); i++) {
						tableIssueNote.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
					}

					// Xóa renderer/editor của các cột tương tác
					tableIssueNote.getColumn("Action").setCellRenderer(null);
					tableIssueNote.getColumn("Action").setCellEditor(null);
					tableIssueNote.getColumn("PDF").setCellRenderer(null);
					tableIssueNote.getColumn("PDF").setCellEditor(null);
					tableIssueNote.getColumn("Excel").setCellRenderer(null);
					tableIssueNote.getColumn("Excel").setCellEditor(null);
					return;
				}

				for (InventoryActivity act : result) {
					String formattedDate = act.getCreatedDate() != null ? dateFormat.format(act.getCreatedDate()) : "";
					String formattedTotal = act.getTotalCost() != null ? nf.format(act.getTotalCost()) + " VND"
							: "0 VND";

					ImageIcon icon = null;
					if (act.getLink() != null) {
						Byte[] byteObjects = act.getLink();
						byte[] imageBytes = new byte[byteObjects.length];
						for (int i = 0; i < byteObjects.length; i++) {
							imageBytes[i] = byteObjects[i];
						}
						icon = new ImageIcon(
								new ImageIcon(imageBytes).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
					}

					tableModel.addRow(new Object[] { stt++, icon, act.getCode(), act.getIdCreator(), formattedDate,
							formattedTotal, "", "", "" });
				}

				// Gán lại renderer/editor cho các cột tương tác
				tableIssueNote.getColumn("Action").setCellRenderer(new DetailButtonRenderer());
				tableIssueNote.getColumn("Action").setCellEditor(new DetailButtonEditor(new JCheckBox()));
				tableIssueNote.getColumn("PDF").setCellRenderer(new PdfButtonRenderer());
				tableIssueNote.getColumn("PDF").setCellEditor(new PdfButtonEditor(new JCheckBox()));
				tableIssueNote.getColumn("Excel").setCellRenderer(new ExcelButtonRenderer());
				tableIssueNote.getColumn("Excel").setCellEditor(new ExcelButtonEditor(new JCheckBox()));
			}

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				updateSearch();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				updateSearch();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				updateSearch();
			}
		});

//		JButton jbuttonClear = new JButton("");
//		jbuttonClear.setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-trash.png")));
//		jbuttonClear.setFont(new Font("SansSerif", Font.PLAIN, 16));
//		panel_3.add(jbuttonClear);
//
//		jbuttonClear.addActionListener(e -> {
//			textField.setText("");
//			init();
//		});

//		jbuttonClear.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
//			@Override
//			public void mouseMoved(java.awt.event.MouseEvent e) {
//				jbuttonClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//			}
//		});

		JButton jbuttonAddExportSlip = new JButton("Add");
		jbuttonAddExportSlip
				.setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-add.png")));
		jbuttonAddExportSlip.setFont(new Font("SansSerif", Font.BOLD, 13));
		jbuttonAddExportSlip.setBackground(new Color(192, 192, 192));
		jbuttonAddExportSlip.setForeground(Color.BLACK);

		jbuttonAddExportSlip.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				jbuttonAddExportSlip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});

		jbuttonAddExportSlip.addActionListener(e -> {
			JPanelStockOut1 stockOut1Panel = new JPanelStockOut1(() -> {
				init();
			});

			JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add new entry form", true);
			dialog.setUndecorated(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setResizable(true);
			dialog.setSize(1400, 800);
			dialog.setLocationRelativeTo(this);

			final Point point = new Point();

			dialog.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
					point.x = e.getX();
					point.y = e.getY();
				}
			});

			dialog.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				@Override
				public void mouseDragged(java.awt.event.MouseEvent e) {
					int x = dialog.getLocation().x + e.getX() - point.x;
					int y = dialog.getLocation().y + e.getY() - point.y;
					dialog.setLocation(x, y);
				}
			});

			dialog.getContentPane().add(stockOut1Panel);
			dialog.setVisible(true);
		});

		panel_3.add(jbuttonAddExportSlip);

		panelTop.add(panel_1, BorderLayout.CENTER);

		tableIssueNote.getColumn("Action").setCellRenderer(new DetailButtonRenderer());
		tableIssueNote.getColumn("Action").setCellEditor(new DetailButtonEditor(new JCheckBox()));

		tableIssueNote.getColumn("PDF").setCellRenderer(new PdfButtonRenderer());
		tableIssueNote.getColumn("PDF").setCellEditor(new PdfButtonEditor(new JCheckBox()));

		tableIssueNote.getColumn("Excel").setCellRenderer(new ExcelButtonRenderer());
		tableIssueNote.getColumn("Excel").setCellEditor(new ExcelButtonEditor(new JCheckBox()));

		init();
	}

	private TitledBorder createSearchBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title, TitledBorder.LEADING,
				TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12), Color.DARK_GRAY);
	}

	private void init() {
		InventoryActivityModel model = new InventoryActivityModel();
		List<InventoryActivity> activities = model.findGroupedActivitiesStockOut();

		DefaultTableModel tableModel = (DefaultTableModel) tableIssueNote.getModel();
		tableModel.setRowCount(0);
		tableIssueNote.getColumn("Image").setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = new JLabel();
				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				if (value instanceof ImageIcon) {
					lbl.setIcon((ImageIcon) value);
				}

				if (isSelected) {
					lbl.setBackground(table.getSelectionBackground());
					lbl.setOpaque(true);
				} else {
					lbl.setBackground(Color.WHITE);
					lbl.setOpaque(true);
				}

				return lbl;
			}
		});

		tableIssueNote.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = tableIssueNote.rowAtPoint(e.getPoint());
				int col = tableIssueNote.columnAtPoint(e.getPoint());

				if (col == tableIssueNote.getColumn("Image").getModelIndex()) {
					Object receiptObj = tableIssueNote.getValueAt(row, 2);
					if (receiptObj != null) {
						String receiptCode = receiptObj.toString();
						InventoryActivityModel model = new InventoryActivityModel();
						InventoryActivity act = model.findByCodeStockOut(receiptCode);

						if (act != null && act.getLink() != null) {
							byte[] imageBytes = new byte[act.getLink().length];
							for (int i = 0; i < act.getLink().length; i++) {
								imageBytes[i] = act.getLink()[i];
							}

							ImageIcon originalIcon = new ImageIcon(imageBytes);
							Image scaledImage = originalIcon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
							ImageIcon scaledIcon = new ImageIcon(scaledImage);

							JDialog dialog = new JDialog(
									(Frame) SwingUtilities.getWindowAncestor(JPanelProductsIssueNote.this),
									"Product Image", true);
							JLabel label = new JLabel(scaledIcon);
							label.setHorizontalAlignment(JLabel.CENTER);

							dialog.getContentPane().add(new JScrollPane(label));
							dialog.setSize(600, 600);
							dialog.setLocationRelativeTo(tableIssueNote);
							dialog.setVisible(true);
						}
					}
				}

			}
		});

		int stt = 1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

		for (InventoryActivity act : activities) {
			String formattedDate = act.getCreatedDate() != null ? dateFormat.format(act.getCreatedDate()) : "";
			String formattedTotal = act.getTotalCost() != null ? nf.format(act.getTotalCost()) + " VND" : "0 VND";

			ImageIcon icon = null;
			if (act.getLink() != null) {
				byte[] imageBytes = new byte[act.getLink().length];
				for (int i = 0; i < act.getLink().length; i++) {
					imageBytes[i] = act.getLink()[i];
				}
				icon = new ImageIcon(imageBytes);
				Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				icon = new ImageIcon(img);
			}

			tableModel.addRow(new Object[] { stt++, icon, act.getCode(), act.getIdCreator(), formattedDate,
					formattedTotal, "", "" });
		}
		// Gán lại renderer/editor cho các cột tương tác sau khi reset
		tableIssueNote.getColumn("Action").setCellRenderer(new DetailButtonRenderer());
		tableIssueNote.getColumn("Action").setCellEditor(new DetailButtonEditor(new JCheckBox()));
		tableIssueNote.getColumn("PDF").setCellRenderer(new PdfButtonRenderer());
		tableIssueNote.getColumn("PDF").setCellEditor(new PdfButtonEditor(new JCheckBox()));
		tableIssueNote.getColumn("Excel").setCellRenderer(new ExcelButtonRenderer());
		tableIssueNote.getColumn("Excel").setCellEditor(new ExcelButtonEditor(new JCheckBox()));

	}

	class DetailButtonRenderer extends JButton implements TableCellRenderer {
		public DetailButtonRenderer() {
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-details.png")));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class DetailButtonEditor extends DefaultCellEditor {
		private JButton button;
		private int row;

		public DetailButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-details.png")));

			button.addActionListener(e -> {
				DefaultTableModel model = (DefaultTableModel) tableIssueNote.getModel();

				Object receiptObj = model.getValueAt(row, 2);
				Object creatorObj = model.getValueAt(row, 3);
				Object createdObj = model.getValueAt(row, 4);
				Object totalObj = model.getValueAt(row, 5);

				if (receiptObj == null || creatorObj == null || createdObj == null || totalObj == null) {
					return;
				}

				String receiptCode = receiptObj.toString();
				String creator = creatorObj.toString();
				String createdDate = createdObj.toString();
				String totalAmount = totalObj.toString();

				if (creator.equals("No delivery slip found")) {
					return;
				}

				InventoryActivityModel invModel = new InventoryActivityModel();
				List<InventoryActivity> products = invModel.findProductsByReceiptCodeStockOut(receiptCode);

				NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
				JPanelProductsIssueNoteDetails detailsPanel = new JPanelProductsIssueNoteDetails();

				int stt = 1;
				for (InventoryActivity p : products) {
					detailsPanel.addProduct(stt++, p.getDescription(), String.valueOf(p.getChange().abs()),
							nf.format(p.getUnitPrice()) + " VND", nf.format(p.getTotalCost()) + " VND");
				}

				JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(JPanelProductsIssueNote.this),
						"Product details", true);
				dialog.setUndecorated(true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setResizable(true);
				dialog.getContentPane().add(new JScrollPane(detailsPanel));
				dialog.setSize(700, 500);
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			});

		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			this.row = row;
			return button;
		}
	}

	class PdfButtonRenderer extends JButton implements TableCellRenderer {
		public PdfButtonRenderer() {
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-pdf.png")));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class PdfButtonEditor extends DefaultCellEditor {
		private JButton button;
		private int row;

		public PdfButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-pdf.png")));
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			button.addActionListener(e -> {
				fireEditingStopped();
				DefaultTableModel model = (DefaultTableModel) tableIssueNote.getModel();
				String receiptCode = model.getValueAt(row, 2).toString();
				String creator = model.getValueAt(row, 3).toString();
				String createdDate = model.getValueAt(row, 4).toString();
				String totalAmount = model.getValueAt(row, 5).toString();

				InventoryActivityModel invModel = new InventoryActivityModel();
				List<InventoryActivity> products = invModel.findProductsByReceiptCodeStockOut(receiptCode);

				try {
					String fileName = "E:/Data/Issue_" + receiptCode + ".pdf";
					Document document = new Document();
					PdfWriter.getInstance(document, new FileOutputStream(fileName));
					document.open();

					Paragraph title = new Paragraph("PRODUCT ISSUE NOTE", new com.itextpdf.text.Font(
							com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
					title.setAlignment(Element.ALIGN_CENTER);
					document.add(title);
					document.add(new Paragraph(" "));

					document.add(new Paragraph("Receipt Code: " + receiptCode));
					document.add(new Paragraph("Creator: " + creator));
					document.add(new Paragraph("Created Date: " + createdDate));
					document.add(new Paragraph(" "));

					PdfPTable table = new PdfPTable(5);
					table.setWidthPercentage(100);
					table.setSpacingBefore(10f);
					table.setSpacingAfter(10f);

					float[] columnWidths = { 1f, 4f, 2f, 2f, 3f };
					table.setWidths(columnWidths);

					String[] headers = { "No.", "Product Name", "Quantity", "Unit Price", "Total Cost" };
					for (String h : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(h));
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}

					int stt = 1;
					NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
					for (InventoryActivity p : products) {
						table.addCell(String.valueOf(stt++));
						table.addCell(p.getDescription());
						table.addCell(p.getChange().abs().toString());
						table.addCell(nf.format(p.getUnitPrice()) + " VND");
						table.addCell(nf.format(p.getTotalCost()) + " VND");
					}

					PdfPCell totalLabel = new PdfPCell(new Phrase("GRAND TOTAL",
							new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12)));
					totalLabel.setColspan(4);
					totalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
					totalLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
					totalLabel.setBorderWidth(1f);
					table.addCell(totalLabel);

					PdfPCell totalValue = new PdfPCell(new Phrase(totalAmount));
					totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
					totalValue.setBackgroundColor(BaseColor.WHITE);
					table.addCell(totalValue);

					document.add(table);
					document.close();

					JOptionPane.showMessageDialog(null, "PDF file exported successfully: " + fileName);

					java.awt.Desktop.getDesktop().open(new java.io.File(fileName));

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error exporting PDF: " + ex.getMessage());
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			this.row = row;
			return button;
		}
	}

	class ExcelButtonRenderer extends JButton implements TableCellRenderer {
		public ExcelButtonRenderer() {
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-excel.png")));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	class ExcelButtonEditor extends DefaultCellEditor {
		private JButton button;
		private int row;

		public ExcelButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setIcon(new ImageIcon(JPanelProductsIssueNote.class.getResource("/resources/icon-excel.png")));
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			button.addActionListener(e -> {
				fireEditingStopped();
				DefaultTableModel model = (DefaultTableModel) tableIssueNote.getModel();
				String receiptCode = model.getValueAt(row, 2).toString();
				String totalAmount = model.getValueAt(row, 5).toString();

				InventoryActivityModel invModel = new InventoryActivityModel();
				List<InventoryActivity> products = invModel.findProductsByReceiptCodeStockOut(receiptCode);

				try {
					String fileName = "E:/Data/Issue_" + receiptCode + ".xls";
					org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
					org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Issue Note");

					// Title style
					org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
					titleFont.setBold(true);
					titleFont.setFontHeightInPoints((short) 16);

					org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
					titleStyle.setFont(titleFont);
					titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

					org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
					org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
					titleCell.setCellValue("PRODUCT ISSUE NOTE");
					titleCell.setCellStyle(titleStyle);
					sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

					// Header style
					org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
					headerFont.setBold(true);

					org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
					headerStyle.setFont(headerFont);
					headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
					headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

					// Quantity style
					org.apache.poi.ss.usermodel.CellStyle qtyStyle = workbook.createCellStyle();
					qtyStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

					// Currency style
					org.apache.poi.ss.usermodel.CellStyle currencyStyle = workbook.createCellStyle();
					currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0 \"VND\""));
					currencyStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
					currencyStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					currencyStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					currencyStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					currencyStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

					// Header row
					org.apache.poi.ss.usermodel.Row header = sheet.createRow(2);
					String[] headers = { "No.", "Product name", "Quantity", "Unit price", "Total amount" };
					for (int i = 0; i < headers.length; i++) {
						org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
						cell.setCellValue(headers[i]);
						cell.setCellStyle(headerStyle);
					}

					// Data rows
					int stt = 1;
					int rowIndex = 3;
					for (InventoryActivity p : products) {
						org.apache.poi.ss.usermodel.Row r = sheet.createRow(rowIndex++);
						r.createCell(0).setCellValue(stt++);
						r.createCell(1).setCellValue(p.getDescription());

						org.apache.poi.ss.usermodel.Cell qtyCell = r.createCell(2);
						qtyCell.setCellValue(p.getChange().abs().doubleValue());
						qtyCell.setCellStyle(qtyStyle);

						org.apache.poi.ss.usermodel.Cell unitPriceCell = r.createCell(3);
						unitPriceCell.setCellValue(p.getUnitPrice().doubleValue());
						unitPriceCell.setCellStyle(currencyStyle);

						org.apache.poi.ss.usermodel.Cell totalCell = r.createCell(4);
						totalCell.setCellValue(p.getTotalCost().doubleValue());
						totalCell.setCellStyle(currencyStyle);
					}

					// Grand Total row
					org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowIndex);

					org.apache.poi.ss.usermodel.CellStyle totalStyle = workbook.createCellStyle();
					totalStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
					totalStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
					totalStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					totalStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					totalStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					totalStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

					sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex, rowIndex, 0, 3));

					for (int col = 0; col <= 3; col++) {
						org.apache.poi.ss.usermodel.Cell cell = totalRow.getCell(col);
						if (cell == null)
							cell = totalRow.createCell(col);
						if (col == 0) {
							cell.setCellValue("GRAND TOTAL");
						}
						cell.setCellStyle(totalStyle);
					}

					org.apache.poi.ss.usermodel.Cell totalValue = totalRow.createCell(4);
					totalValue.setCellValue(Double.parseDouble(totalAmount.replaceAll("[^\\d]", "")));
					totalValue.setCellStyle(currencyStyle);

					// Auto-size columns
					for (int i = 0; i < headers.length; i++) {
						sheet.autoSizeColumn(i);
					}
					sheet.setColumnWidth(3, 20 * 256);
					sheet.setColumnWidth(4, 25 * 256);

					java.io.FileOutputStream fos = new java.io.FileOutputStream(fileName);
					workbook.write(fos);
					fos.close();
					workbook.close();

					JOptionPane.showMessageDialog(null, "Excel file exported successfully: " + fileName);
					java.awt.Desktop.getDesktop().open(new java.io.File(fileName));

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error exporting Excel: " + ex.getMessage());
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			this.row = row;
			return button;
		}
	}
}
