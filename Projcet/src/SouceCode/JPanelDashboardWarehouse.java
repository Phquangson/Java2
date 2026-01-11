package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import entities.InventoryActivity;
import entities.Product;
import entities.Staff;
import models.InventoryActivityModel;
import models.ProductModel;

import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;

import java.text.DecimalFormat;

public class JPanelDashboardWarehouse extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable tableProducts;
	private JLabel lblExportTotal, lblExportTop, lblImportTotal, lblImportTop;

	private DefaultCategoryDataset lineDataset;
	private DefaultPieDataset pieDataset;

	private JPanel panelHeader;
	private JPanel chartPanel;
	private JScrollPane scrollPane;

	private JLabel lblImportYear, lblExportYear;

	public JPanelDashboardWarehouse(List<Product> productList) {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		panelHeader = new JPanel(new GridBagLayout());
		panelHeader.setBackground(new Color(247, 222, 155));
		panelHeader.setPreferredSize(new Dimension(0, 40));
		panelHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		JLabel lblTitle = new JLabel("Convenience Store Dashboard");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
		panelHeader.add(lblTitle);

		add(panelHeader);
		add(Box.createVerticalStrut(10));

		add(createFilterPanel());

		lineDataset = new DefaultCategoryDataset();
		pieDataset = new DefaultPieDataset();

		chartPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		chartPanel.setPreferredSize(new Dimension(0, 400));
		chartPanel.setBackground(Color.WHITE);

		chartPanel.add(new ChartPanel(ChartFactory.createLineChart("Revenue by week", "Day", "VND", lineDataset)));
		chartPanel.add(new ChartPanel(ChartFactory.createRingChart("Turnover ratio", pieDataset, true, true, false)));
		add(chartPanel);

		add(Box.createVerticalStrut(15));

		scrollPane = new JScrollPane(createProductTable(productList));
		scrollPane.setBorder(BorderFactory.createTitledBorder("All Products"));
		scrollPane.setPreferredSize(new Dimension(0, 500));

		add(scrollPane);
	}

	private JTable createProductTable(List<Product> productList) {

		String[] columnNames = { "No.", "Image", "Code", "Name", "Status" };

		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}

			public Class<?> getColumnClass(int c) {
				return c == 1 ? Object.class : String.class;
			}
		};

		int index = 1;
		for (Product p : productList) {
			model.addRow(
					new Object[] { index++, p.getLink(), p.getCode(), p.getTitle(), getStatusText(p.getIdStatus()) });
		}

		tableProducts = new JTable(model);
		tableProducts.setRowHeight(60);
		tableProducts.setFont(new Font("SansSerif", Font.PLAIN, 14));
		tableProducts.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(100);
		tableProducts.getColumnModel().getColumn(0).setMaxWidth(100);
		tableProducts.getColumnModel().getColumn(0).setMinWidth(100);
		tableProducts.getColumnModel().getColumn(0).setResizable(false);

		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setMaxWidth(200);
		tableProducts.getColumnModel().getColumn(1).setMinWidth(200);
		tableProducts.getColumnModel().getColumn(1).setResizable(false);

		tableProducts.getColumnModel().getColumn(4).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(4).setMaxWidth(200);
		tableProducts.getColumnModel().getColumn(4).setMinWidth(200);
		tableProducts.getColumnModel().getColumn(4).setResizable(false);

		tableProducts.getColumnModel().getColumn(1).setCellRenderer(new ImageRender());
		tableProducts.getColumnModel().getColumn(4).setCellRenderer(new StatusRender());

		tableProducts.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				tableProducts.getTableHeader().setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		tableProducts.setRowSorter(sorter);

		tableProducts.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tableProducts.getSelectedRow();
				int col = tableProducts.getSelectedColumn();

				if (col == 1 && row >= 0) {
					Object value = tableProducts.getValueAt(row, col);
					if (value instanceof byte[]) {
						ImageIcon icon = new ImageIcon((byte[]) value);
						Image img = icon.getImage();

						Image scaledImg = img.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
						JLabel lbl = new JLabel(new ImageIcon(scaledImg));

						JDialog dialog = new JDialog((Frame) null, "Zoom Image", true);
						dialog.getContentPane().add(lbl);
						dialog.pack();
						dialog.setLocationRelativeTo(null);
						dialog.setVisible(true);
					}
				}
			}
		});

		return tableProducts;
	}

	private class ImageRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			JLabel jlabel = new JLabel();
			jlabel.setHorizontalAlignment(JLabel.CENTER);

			if (value instanceof byte[]) {
				try {
					ImageIcon icon = new ImageIcon((byte[]) value);
					Image img = icon.getImage();

					BufferedImage scaledImg = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = scaledImg.createGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2d.drawImage(img, 0, 0, 50, 50, null);
					g2d.dispose();

					jlabel.setIcon(new ImageIcon(scaledImg));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			return jlabel;
		}
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

	private String getStatusText(int id) {
		switch (id) {
		case 6:
			return "In Stock";
		case 18:
			return "Almost out of stock";
		case 17:
			return "Need to import";
		case 16:
			return "Out of stock";
		case 19:
			return "Delete";
		default:
			return "Unknown";
		}
	}

	private class StatusRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			String status = value.toString();
			Color bgColor = Color.WHITE;
			Color textColor = Color.BLACK;

			switch (status) {
			case "In Stock":
				bgColor = new Color(198, 255, 198);
				textColor = new Color(0, 100, 0);
				break;
			case "Almost out of stock":
				bgColor = new Color(255, 243, 205);
				textColor = new Color(133, 100, 4);
				break;
			case "Need to import":
				bgColor = new Color(209, 236, 241);
				textColor = new Color(12, 84, 96);
				break;
			case "Out of stock":
				bgColor = new Color(255, 204, 204);
				textColor = new Color(139, 0, 0);
				break;
			case "Delete":
				bgColor = new Color(183, 28, 28);
				textColor = new Color(255, 235, 238);
				break;
			}

			StatusBadge badge = new StatusBadge(status, bgColor, textColor);
			badge.setPreferredSize(new Dimension(150, 30));

			JPanel wrapper = new JPanel(new GridBagLayout());
			wrapper.setOpaque(false);
			wrapper.add(badge, new GridBagConstraints());

			return wrapper;
		}
	}

	private void updateChartDataFromDatabase(int year, int monthIndex) {
		lineDataset.clear();
		pieDataset.clear();

		InventoryActivityModel model = new InventoryActivityModel();
		List<InventoryActivity> list = model.findGroupedActivitiesStockOut();

		int[] values = new int[7];
		java.time.Month month = java.time.Month.of(monthIndex + 1);

		for (InventoryActivity a : list) {
			var date = a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

			if (date.getYear() == year && date.getMonth() == month) {
				values[date.getDayOfWeek().getValue() - 1] += a.getTotalCost().intValue();
			}
		}

		String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
		for (int i = 0; i < 7; i++) {
			lineDataset.addValue(values[i], "Revenue", days[i]);
			pieDataset.setValue(days[i], values[i]);
		}

		chartPanel.removeAll();
		chartPanel.add(new ChartPanel(ChartFactory.createLineChart("Revenue by week", "Day", "VND", lineDataset)));
		chartPanel.add(new ChartPanel(ChartFactory.createRingChart("Turnover ratio", pieDataset, true, true, false)));

		chartPanel.revalidate();
		chartPanel.repaint();
	}

	private JPanel createFilterPanel() {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setBackground(Color.WHITE);

		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
		groupPanel.setBackground(Color.WHITE);

		JPanel monthRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		monthRow.setBackground(Color.WHITE);

		JLabel lblYear = new JLabel("Year");
		lblYear.setFont(new Font("SansSerif", Font.PLAIN, 13));

		int currentYear = LocalDate.now().getYear();

		String[] years = new String[6];
		years[0] = "-- Select Year --";
		for (int i = 0; i < 5; i++) {
			years[i + 1] = String.valueOf(currentYear - i);
		}

		JComboBox<String> cboYear = new JComboBox<>(years);
		cboYear.setFont(new Font("SansSerif", Font.PLAIN, 14));

		monthRow.add(lblYear);
		monthRow.add(cboYear);

		JLabel lblMonth = new JLabel("Month");
		lblMonth.setFont(new Font("SansSerif", Font.PLAIN, 13));

		String[] months = { "-- Select Month --", "January", "February", "March", "April", "May", "June", "July",
				"August", "September", "October", "November", "December" };

		JComboBox<String> cboMonth = new JComboBox<>(months);
		cboMonth.setFont(new Font("SansSerif", Font.PLAIN, 14));

		monthRow.add(lblMonth);
		monthRow.add(cboMonth);

		// ===== ROW : ALL BOXES =====
		JPanel allBoxRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
		allBoxRow.setBackground(Color.WHITE);

		JPanel importYearBox = createBoxPanel();
		lblImportYear = new JLabel("Year import: 0 VND");
		lblImportYear.setFont(new Font("SansSerif", Font.BOLD, 14));
		importYearBox.add(lblImportYear);

		JPanel exportYearBox = createBoxPanel();
		lblExportYear = new JLabel("Year export: 0 VND");
		lblExportYear.setFont(new Font("SansSerif", Font.BOLD, 14));
		exportYearBox.add(lblExportYear);

		JPanel importBox = createBoxPanel();
		JLabel lblImport = new JLabel("Month import: 0 VND");
		lblImport.setFont(new Font("SansSerif", Font.BOLD, 14));
		importBox.add(lblImport);

		JPanel exportBox = createBoxPanel();
		JLabel lblExport = new JLabel("Motnh export: 0 VND");
		lblExport.setFont(new Font("SansSerif", Font.BOLD, 14));
		exportBox.add(lblExport);

		// add all 4 boxes to one row

		allBoxRow.add(importYearBox);
		allBoxRow.add(exportYearBox);
		allBoxRow.add(importBox);
		allBoxRow.add(exportBox);

		// add to groupPanel
		groupPanel.add(monthRow);
		groupPanel.add(Box.createVerticalStrut(2));
		groupPanel.add(allBoxRow);

		cboMonth.addActionListener(e -> {
			int monthIndex = cboMonth.getSelectedIndex();
			int yearIndex = cboYear.getSelectedIndex();

			if (monthIndex == 0 || yearIndex == 0) {
				lineDataset.clear();
				pieDataset.clear();

				chartPanel.removeAll();
				chartPanel.add(
						new ChartPanel(ChartFactory.createLineChart("Revenue by week", "Day", "VND", lineDataset)));
				chartPanel.add(
						new ChartPanel(ChartFactory.createRingChart("Turnover ratio", pieDataset, true, true, false)));
				chartPanel.revalidate();
				chartPanel.repaint();

				lblImport.setText("Total import: 0 VND");
				lblExport.setText("Total export: 0 VND");
				return;
			}

			int year = Integer.parseInt((String) cboYear.getSelectedItem());
			updateImportExport(year, monthIndex, lblImport, lblExport);
			updateChartDataFromDatabase(year, monthIndex - 1);

			updateYearTotal(year, lblImportYear, lblExportYear);
		});

		cboYear.addActionListener(e -> {
			int yearIndex = cboYear.getSelectedIndex();
			if (yearIndex == 0) {
				lineDataset.clear();
				pieDataset.clear();

				chartPanel.removeAll();
				chartPanel.add(
						new ChartPanel(ChartFactory.createLineChart("Revenue by week", "Day", "VND", lineDataset)));
				chartPanel.add(
						new ChartPanel(ChartFactory.createRingChart("Turnover ratio", pieDataset, true, true, false)));
				chartPanel.revalidate();
				chartPanel.repaint();

				lblImport.setText("Total import: 0 VND");
				lblExport.setText("Total export: 0 VND");
				lblImportYear.setText("Year import: 0 VND");
				lblExportYear.setText("Year export: 0 VND");
				return;
			}

			int year = Integer.parseInt((String) cboYear.getSelectedItem());

			updateYearTotal(year, lblImportYear, lblExportYear);

			updateChartDataYear(year);
		});

		groupPanel.add(monthRow);
		groupPanel.add(Box.createVerticalStrut(2));
		groupPanel.add(allBoxRow);

		wrapper.add(groupPanel);
		wrapper.add(Box.createVerticalStrut(5));

		return wrapper;
	}

	private JPanel createBoxPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(new Dimension(220, 60));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
				new EmptyBorder(8, 12, 8, 12)));
		return panel;
	}

	private void updateImportExport(int year, int monthIndex, JLabel lblImport, JLabel lblExport) {
		InventoryActivityModel model = new InventoryActivityModel();

		List<InventoryActivity> stockIn = model.findGroupedActivitiesStockIn();
		List<InventoryActivity> stockOut = model.findGroupedActivitiesStockOut();

		java.time.Month month = java.time.Month.of(monthIndex);

		int totalImport = stockIn.stream()
				.filter(a -> a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getYear() == year
						&& a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getMonth() == month)
				.map(a -> a.getTotalCost().intValue()).reduce(0, Integer::sum);

		int totalExport = stockOut.stream()
				.filter(a -> a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getYear() == year
						&& a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getMonth() == month)
				.map(a -> a.getTotalCost().intValue()).reduce(0, Integer::sum);

		DecimalFormat df = new DecimalFormat("#,###");

		lblImport.setText("Total import: " + df.format(totalImport) + " VND");
		lblExport.setText("Total export: " + df.format(totalExport) + " VND");
	}

	private void updateYearTotal(int year, JLabel lblImportYear, JLabel lblExportYear) {
		InventoryActivityModel model = new InventoryActivityModel();

		List<InventoryActivity> stockIn = model.findGroupedActivitiesStockIn();
		List<InventoryActivity> stockOut = model.findGroupedActivitiesStockOut();

		int totalImport = stockIn.stream()
				.filter(a -> a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getYear() == year)
				.map(a -> a.getTotalCost().intValue()).reduce(0, Integer::sum);

		int totalExport = stockOut.stream()
				.filter(a -> a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getYear() == year)
				.map(a -> a.getTotalCost().intValue()).reduce(0, Integer::sum);

		DecimalFormat df = new DecimalFormat("#,###");

		lblImportYear.setText("Year import: " + df.format(totalImport) + " VND");
		lblExportYear.setText("Year export: " + df.format(totalExport) + " VND");
	}

	private void updateChartDataYear(int year) {
		lineDataset.clear();
		pieDataset.clear();

		InventoryActivityModel model = new InventoryActivityModel();
		List<InventoryActivity> list = model.findGroupedActivitiesStockOut();

		int[] values = new int[12];
		for (InventoryActivity a : list) {
			var date = a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
			if (date.getYear() == year) {
				values[date.getMonthValue() - 1] += a.getTotalCost().intValue();
			}
		}

		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		for (int i = 0; i < 12; i++) {
			lineDataset.addValue(values[i], "Revenue", months[i]);
			pieDataset.setValue(months[i], values[i]);
		}

		chartPanel.removeAll();

		chartPanel.add(new ChartPanel(ChartFactory.createLineChart("Revenue by month", "Month", "VND", lineDataset)));

		chartPanel.add(new ChartPanel(ChartFactory.createBarChart("Turnover by month", "Month", "VND", lineDataset)));

		chartPanel.revalidate();
		chartPanel.repaint();

	}

}
