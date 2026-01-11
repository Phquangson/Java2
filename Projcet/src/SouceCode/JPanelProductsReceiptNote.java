package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import SouceCode.JPanelProductsIssueNote.DetailButtonEditor;
import SouceCode.JPanelProductsIssueNote.DetailButtonRenderer;
import SouceCode.JPanelProductsIssueNote.ExcelButtonEditor;
import SouceCode.JPanelProductsIssueNote.ExcelButtonRenderer;
import SouceCode.JPanelProductsIssueNote.PdfButtonEditor;
import SouceCode.JPanelProductsIssueNote.PdfButtonRenderer;
import entities.InventoryActivity;
import models.InventoryActivityModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class JPanelProductsReceiptNote extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableReceiptNote;
	private JTextField textField;

	public JPanelProductsReceiptNote() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);

		// Header
		JPanel panelHeader = new JPanel(new GridBagLayout());
		panelHeader.setBackground(new Color(247, 222, 155));
		panelHeader.setPreferredSize(new Dimension(0, 40));

		JLabel lblTitle = new JLabel("Product Receipt Note");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		lblTitle.setForeground(new Color(0, 0, 0));

		panelHeader.add(lblTitle, new GridBagConstraints());

		JPanel panelTop = new JPanel(new BorderLayout());
		panelTop.add(panelHeader, BorderLayout.NORTH);

		add(panelTop, BorderLayout.NORTH);

		// Table
		String[] columnNames = { "No.", "Image", "Receipt Code", "Creator", "Created Time", "Total Amount", "Action",
				"PDF", "Excel" };

		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 6 || column == 7 || column == 8;
			}
		};

		tableReceiptNote = new JTable(tableModel);
		tableReceiptNote.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableReceiptNote.setRowHeight(70);
		tableReceiptNote.setFillsViewportHeight(true);

		tableReceiptNote.setIntercellSpacing(new Dimension(0, 20));

		tableReceiptNote.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

		tableReceiptNote.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = tableReceiptNote.columnAtPoint(e.getPoint());
				if (col == tableReceiptNote.getColumn("Image").getModelIndex()
						|| col == tableReceiptNote.getColumn("Action").getModelIndex()
						|| col == tableReceiptNote.getColumn("PDF").getModelIndex()) {

					tableReceiptNote.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					tableReceiptNote.setCursor(Cursor.getDefaultCursor());
				}
			}
		});

		tableReceiptNote.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tableReceiptNote.rowAtPoint(e.getPoint());
				int col = tableReceiptNote.columnAtPoint(e.getPoint());

				if (col == 1) {
					String receiptCode = tableReceiptNote.getValueAt(row, 2).toString();

					InventoryActivityModel model = new InventoryActivityModel();
					InventoryActivity act = model.findByCodeStockIn(receiptCode);

					if (act != null && act.getLink() != null) {
						Byte[] obj = act.getLink();
						byte[] primitive = new byte[obj.length];
						for (int i = 0; i < obj.length; i++) {
							primitive[i] = obj[i];
						}

						ImageIcon originalIcon = new ImageIcon(primitive);
						Image scaledImage = originalIcon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
						ImageIcon scaledIcon = new ImageIcon(scaledImage);

						JDialog dialog = new JDialog((Frame) null, "Product Image", true);
						JLabel label = new JLabel(scaledIcon);
						label.setHorizontalAlignment(JLabel.CENTER);

						dialog.getContentPane().add(new JScrollPane(label));
						dialog.setSize(600, 600);
						dialog.setLocationRelativeTo(tableReceiptNote);
						dialog.setVisible(true);
					}
				}
			}
		});

		tableReceiptNote.getColumnModel().getColumn(0).setPreferredWidth(60);
		tableReceiptNote.getColumnModel().getColumn(1).setPreferredWidth(120);
		tableReceiptNote.getColumnModel().getColumn(2).setPreferredWidth(220);
		tableReceiptNote.getColumnModel().getColumn(3).setPreferredWidth(100);
		tableReceiptNote.getColumnModel().getColumn(4).setPreferredWidth(120);
		tableReceiptNote.getColumnModel().getColumn(5).setPreferredWidth(80);
		tableReceiptNote.getColumnModel().getColumn(5).setPreferredWidth(80);

		tableReceiptNote.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				tableReceiptNote.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
		tableReceiptNote.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				tableReceiptNote.getTableHeader().setCursor(Cursor.getDefaultCursor());
			}
		});

		tableReceiptNote.getColumn("PDF").setCellRenderer(new PdfButtonRenderer());
		tableReceiptNote.getColumn("PDF").setCellEditor(new PdfButtonEditor(new JCheckBox()));

		JScrollPane scrollPane = new JScrollPane(tableReceiptNote);
		add(scrollPane, BorderLayout.CENTER);

		// Search panel
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

		textField = new JTextField();
		textField.setColumns(20);
		panel_3.add(textField);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(
				(DefaultTableModel) tableReceiptNote.getModel());
		tableReceiptNote.setRowSorter(sorter);

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
		sorter.setComparator(0, (o1, o2) -> {
			Integer i1 = Integer.parseInt(o1.toString());
			Integer i2 = Integer.parseInt(o2.toString());
			return i1.compareTo(i2);
		});
		sorter.setComparator(3, (o1, o2) -> {
			try {
				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				return df.parse(o1.toString()).compareTo(df.parse(o2.toString()));
			} catch (Exception e) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void updateSearch() {
				String keyword = textField.getText().trim();
				DefaultTableModel tableModel = (DefaultTableModel) tableReceiptNote.getModel();
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
					for (int i = 0; i < tableReceiptNote.getColumnCount(); i++) {
						tableReceiptNote.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
					}

					// Xóa renderer/editor của các cột tương tác
					tableReceiptNote.getColumn("Action").setCellRenderer(null);
					tableReceiptNote.getColumn("Action").setCellEditor(null);
					tableReceiptNote.getColumn("PDF").setCellRenderer(null);
					tableReceiptNote.getColumn("PDF").setCellEditor(null);
					tableReceiptNote.getColumn("Excel").setCellRenderer(null);
					tableReceiptNote.getColumn("Excel").setCellEditor(null);
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
				tableReceiptNote.getColumn("Action").setCellRenderer(new DetailButtonRenderer());
				tableReceiptNote.getColumn("Action").setCellEditor(new DetailButtonEditor(new JCheckBox()));
				tableReceiptNote.getColumn("PDF").setCellRenderer(new PdfButtonRenderer());
				tableReceiptNote.getColumn("PDF").setCellEditor(new PdfButtonEditor(new JCheckBox()));
				tableReceiptNote.getColumn("Excel").setCellRenderer(new ExcelButtonRenderer());
				tableReceiptNote.getColumn("Excel").setCellEditor(new ExcelButtonEditor(new JCheckBox()));
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
//		jbuttonClear.setBackground(new Color(192, 192, 192));
//		jbuttonClear.setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-trash.png")));
//		jbuttonClear.setFont(new Font("SansSerif", Font.PLAIN, 16));
//		panel_3.add(jbuttonClear);
//		jbuttonClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//		jbuttonClear.addActionListener(e -> {
//			textField.setText("");
//			init();
//		});

		JButton jbuttonAddReceipt = new JButton("Add");
		jbuttonAddReceipt
				.setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-add.png")));
		jbuttonAddReceipt.setFont(new Font("SansSerif", Font.BOLD, 13));
		jbuttonAddReceipt.setBackground(new Color(192, 192, 192));
		jbuttonAddReceipt.setForeground(new Color(0, 0, 0));
		jbuttonAddReceipt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		jbuttonAddReceipt.addActionListener(e -> {
			JPanelStockIn1 stockInPanel = new JPanelStockIn1();

			JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add new entry form", true);
			dialog.setUndecorated(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setResizable(true);
			dialog.setSize(1400, 800);
			dialog.setLocationRelativeTo(this);

			dialog.getContentPane().add(stockInPanel);

			dialog.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosed(java.awt.event.WindowEvent e) {
					init();
				}
			});

			dialog.setVisible(true);
		});

		panel_3.add(jbuttonAddReceipt);

		panelTop.add(panel_1, BorderLayout.CENTER);

		tableReceiptNote.getColumn("Action").setCellRenderer(new DetailButtonRenderer());
		tableReceiptNote.getColumn("Action").setCellEditor(new DetailButtonEditor(new JCheckBox()));

		tableReceiptNote.getColumn("Excel").setCellRenderer(new ExcelButtonRenderer());
		tableReceiptNote.getColumn("Excel").setCellEditor(new ExcelButtonEditor(new JCheckBox()));

		init();
	}

	private void init() {
		InventoryActivityModel model = new InventoryActivityModel();
		List<InventoryActivity> activities = model.findGroupedActivitiesStockIn();

		DefaultTableModel tableModel = (DefaultTableModel) tableReceiptNote.getModel();
		tableModel.setRowCount(0);

		int stt = 1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

		for (InventoryActivity act : activities) {
			String formattedDate = act.getCreatedDate() != null ? dateFormat.format(act.getCreatedDate()) : "";
			String formattedTotal = act.getTotalCost() != null ? nf.format(act.getTotalCost()) + " VND" : "0 VND";

			ImageIcon icon = null;
			if (act.getLink() != null) {
				Byte[] obj = act.getLink();
				byte[] primitive = new byte[obj.length];
				for (int i = 0; i < obj.length; i++) {
					primitive[i] = obj[i];
				}

				icon = new ImageIcon(primitive);
				Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				icon = new ImageIcon(img);
			}

			tableModel.addRow(
					new Object[] { stt++, icon, act.getCode(), act.getIdCreator(), formattedDate, formattedTotal, "" });
		}

		tableReceiptNote.getColumn("Image").setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				if (value instanceof ImageIcon) {
					setIcon((ImageIcon) value);
					setText("");
				} else {
					setIcon(null);
					setText(value == null ? "" : value.toString());
				}
			}

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setVerticalAlignment(SwingConstants.CENTER);
				return lbl;
			}
		});

		tableReceiptNote.getColumn("Action").setCellRenderer(new DetailButtonRenderer());
		tableReceiptNote.getColumn("Action").setCellEditor(new DetailButtonEditor(new JCheckBox()));
		tableReceiptNote.getColumn("PDF").setCellRenderer(new PdfButtonRenderer());
		tableReceiptNote.getColumn("PDF").setCellEditor(new PdfButtonEditor(new JCheckBox()));
		tableReceiptNote.getColumn("Excel").setCellRenderer(new ExcelButtonRenderer());
		tableReceiptNote.getColumn("Excel").setCellEditor(new ExcelButtonEditor(new JCheckBox()));
	}

	class DetailButtonRenderer extends JButton implements TableCellRenderer {
		public DetailButtonRenderer() {
			setOpaque(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-details.png")));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			return this;
		}
	}

	public void refreshReceiptNotes() {
		InventoryActivityModel model = new InventoryActivityModel();
		List<InventoryActivity> activities = model.findGroupedActivitiesStockOut();

		DefaultTableModel tableModel = (DefaultTableModel) tableReceiptNote.getModel();
		tableModel.setRowCount(0);

		int stt = 1;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

		for (InventoryActivity act : activities) {
			String formattedDate = act.getCreatedDate() != null ? dateFormat.format(act.getCreatedDate()) : "";
			String formattedTotal = act.getTotalCost() != null ? nf.format(act.getTotalCost()) + " VND" : "0 VND";

			tableModel.addRow(
					new Object[] { stt++, act.getCode(), act.getIdCreator(), formattedDate, formattedTotal, "" });
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
			button.setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-details.png")));
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			button.addActionListener(e -> {
				fireEditingStopped();

				DefaultTableModel model = (DefaultTableModel) tableReceiptNote.getModel();
				String receiptCode = model.getValueAt(row, 2).toString(); // Receipt Code
				String creator = model.getValueAt(row, 3).toString(); // Creator
				String createdDate = model.getValueAt(row, 4).toString();
				String totalAmount = model.getValueAt(row, 5).toString();

				InventoryActivityModel invModel = new InventoryActivityModel();
				List<InventoryActivity> products = invModel.findProductsByReceiptCodeStockIn(receiptCode);

				NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
				JPanelProductsReceiptNoteDetails detailsPanel = new JPanelProductsReceiptNoteDetails();

				int stt = 1;
				for (InventoryActivity p : products) {
					String supplier = p.getNameSupplier();
					detailsPanel.addProduct(stt++, p.getDescription(), supplier, String.valueOf(p.getChange().abs()),
							nf.format(p.getUnitPrice()) + " VND", nf.format(p.getTotalCost()) + " VND");
				}

				JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(tableReceiptNote), true);
				dialog.setUndecorated(true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setResizable(true);
				dialog.getContentPane().add(detailsPanel);
				dialog.setSize(700, 400);
				dialog.setLocationRelativeTo(tableReceiptNote);
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
			setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-pdf.png")));
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
			button.setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-pdf.png")));
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			button.addActionListener(e -> {
				fireEditingStopped();
				DefaultTableModel model = (DefaultTableModel) tableReceiptNote.getModel();
				String receiptCode = model.getValueAt(row, 2).toString();
				String creator = model.getValueAt(row, 3).toString();
				String createdDate = model.getValueAt(row, 4).toString();
				String totalAmount = model.getValueAt(row, 5).toString();

				InventoryActivityModel invModel = new InventoryActivityModel();
				List<InventoryActivity> products = invModel.findProductsByReceiptCodeStockIn(receiptCode);

				try {
					String fileName = "E:/Data/Receipt_" + receiptCode + ".pdf";
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

					// Header row
					String[] headers = { "No.", "Product Name", "Quantity", "Unit Price", "Total Cost" };
					for (String h : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(h, new com.itextpdf.text.Font(
								com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setBorderWidth(1f);
						table.addCell(cell);
					}

					// Data rows
					int stt = 1;
					NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
					for (InventoryActivity p : products) {
						PdfPCell c1 = new PdfPCell(new Phrase(String.valueOf(stt++)));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						c1.setBorderWidth(1f);
						table.addCell(c1);

						PdfPCell c2 = new PdfPCell(new Phrase(p.getDescription()));
						c2.setBorderWidth(1f);
						table.addCell(c2);

						PdfPCell c3 = new PdfPCell(new Phrase(p.getChange().abs().toString()));
						c3.setHorizontalAlignment(Element.ALIGN_CENTER);
						c3.setBorderWidth(1f);
						table.addCell(c3);

						PdfPCell c4 = new PdfPCell(new Phrase(nf.format(p.getUnitPrice()) + " VND"));
						c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
						c4.setBorderWidth(1f);
						table.addCell(c4);

						PdfPCell c5 = new PdfPCell(new Phrase(nf.format(p.getTotalCost()) + " VND"));
						c5.setHorizontalAlignment(Element.ALIGN_RIGHT);
						c5.setBorderWidth(1f);
						table.addCell(c5);
					}

					// Grand Total row
					PdfPCell totalLabel = new PdfPCell(new Phrase("GRAND TOTAL",
							new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12)));
					totalLabel.setColspan(4);
					totalLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
					totalLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
					totalLabel.setBorderWidth(1f);
					table.addCell(totalLabel);

					PdfPCell totalValue = new PdfPCell(new Phrase(totalAmount,
							new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12)));
					totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
					totalValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
					totalValue.setBorderWidth(1f);
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
			setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-excel.png")));
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
			button.setIcon(new ImageIcon(JPanelProductsReceiptNote.class.getResource("/resources/icon-excel.png")));
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			button.addActionListener(e -> {
				fireEditingStopped();
				DefaultTableModel model = (DefaultTableModel) tableReceiptNote.getModel();
				String receiptCode = model.getValueAt(row, 2).toString();
				String totalAmount = model.getValueAt(row, 5).toString();

				InventoryActivityModel invModel = new InventoryActivityModel();
				List<InventoryActivity> products = invModel.findProductsByReceiptCodeStockIn(receiptCode);

				try {
					String fileName = "E:/Data/Receipt_" + receiptCode + ".xls";
					org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
					org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Receipt Note");

					org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
					titleFont.setBold(true);
					titleFont.setFontHeightInPoints((short) 16);

					org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
					titleStyle.setFont(titleFont);
					titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

					org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
					org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
					titleCell.setCellValue("PRODUCT RECEIPT NOTE");
					titleCell.setCellStyle(titleStyle);
					sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

					org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
					headerFont.setBold(true);

					org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
					headerStyle.setFont(headerFont);
					headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
					headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

					org.apache.poi.ss.usermodel.CellStyle qtyStyle = workbook.createCellStyle();
					qtyStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

					org.apache.poi.ss.usermodel.CellStyle currencyStyle = workbook.createCellStyle();
					currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0 \"VND\""));
					currencyStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
					currencyStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					currencyStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					currencyStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					currencyStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

					org.apache.poi.ss.usermodel.Row header = sheet.createRow(2);
					String[] headers = { "No.", "Product Name", "Supplier", "Quantity", "Unit Price", "Total Cost" };
					for (int i = 0; i < headers.length; i++) {
						org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
						cell.setCellValue(headers[i]);
						cell.setCellStyle(headerStyle);
					}

					int stt = 1;
					int rowIndex = 3;
					for (InventoryActivity p : products) {
						org.apache.poi.ss.usermodel.Row r = sheet.createRow(rowIndex++);
						r.createCell(0).setCellValue(stt++);
						r.createCell(1).setCellValue(p.getDescription());
						r.createCell(2).setCellValue(p.getNameSupplier());

						org.apache.poi.ss.usermodel.Cell qtyCell = r.createCell(3);
						qtyCell.setCellValue(p.getChange().abs().doubleValue());
						qtyCell.setCellStyle(qtyStyle);

						org.apache.poi.ss.usermodel.Cell unitPriceCell = r.createCell(4);
						unitPriceCell.setCellValue(p.getUnitPrice().doubleValue());
						unitPriceCell.setCellStyle(currencyStyle);

						org.apache.poi.ss.usermodel.Cell totalCell = r.createCell(5);
						totalCell.setCellValue(p.getTotalCost().doubleValue());
						totalCell.setCellStyle(currencyStyle);
					}

					org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowIndex);

					org.apache.poi.ss.usermodel.CellStyle totalStyle = workbook.createCellStyle();
					totalStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
					totalStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
					totalStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					totalStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					totalStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
					totalStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

					sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex, rowIndex, 0, 4));

					for (int col = 0; col <= 4; col++) {
						org.apache.poi.ss.usermodel.Cell cell = totalRow.getCell(col);
						if (cell == null)
							cell = totalRow.createCell(col);
						if (col == 0) {
							cell.setCellValue("GRAND TOTAL");
						}
						cell.setCellStyle(totalStyle);
					}

					org.apache.poi.ss.usermodel.Cell totalValue = totalRow.createCell(5);
					totalValue.setCellValue(Double.parseDouble(totalAmount.replaceAll("[^\\d]", "")));
					totalValue.setCellStyle(currencyStyle);

					for (int i = 0; i < headers.length; i++) {
						sheet.autoSizeColumn(i);
					}
					sheet.setColumnWidth(4, 20 * 256);
					sheet.setColumnWidth(5, 25 * 256);

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
