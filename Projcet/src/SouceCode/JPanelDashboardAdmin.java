package SouceCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import entities.InventoryActivity;
import entities.Product;
import entities.Staff;
import models.BillModel;
import models.InventoryActivityModel;
import models.ProductModel;

import java.time.LocalDate;

import javax.swing.table.JTableHeader;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class JPanelDashboardAdmin extends JPanel {

	private static final long serialVersionUID = 1L;

	private DefaultCategoryDataset monthlyRevenueDataset = new DefaultCategoryDataset();
	private DefaultCategoryDataset yearlyRevenueDataset = new DefaultCategoryDataset();

	private JLabel lblTotalRevenue;
	private JLabel lblCurrentMonthRevenue;
	private JLabel lblTotalOrders;
	private JLabel lblMonthOrders;
	private JLabel lblGrowth;
	private BillModel billModel = new BillModel();

	public JPanelDashboardAdmin(List<Staff> staffList) {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		add(createHeaderPanel());
		add(Box.createVerticalStrut(10));

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBackground(Color.WHITE);
		content.setBorder(new EmptyBorder(15, 15, 15, 15));

		content.add(createFilterPanel());
		content.add(Box.createVerticalStrut(15));
		content.add(createKpiPanel());
		content.add(Box.createVerticalStrut(15));
		content.add(createSalesChartPanel());

		content.add(Box.createVerticalStrut(20));
		content.add(createStaffTablePanel(staffList));

		add(content);
	}

	private JPanel createFilterPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
		panel.setBackground(Color.WHITE);

		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();

		panel.add(new JLabel("Year"));

		JComboBox<String> cboYear = new JComboBox<>();
		cboYear.addItem("- Select Year --");

		for (int y = currentYear; y >= currentYear - 4; y--) {
			cboYear.addItem(String.valueOf(y));
		}

		cboYear.setSelectedIndex(0);
		panel.add(cboYear);

		panel.add(new JLabel("Month"));

		JComboBox<String> cboMonth = new JComboBox<>(new String[] { "-- Select Month --", "January", "February",
				"March", "April", "May", "June", "July", "August", "September", "October", "November", "December" });

		cboMonth.setSelectedIndex(0);
		panel.add(cboMonth);

		cboYear.addActionListener(e -> reloadDashboard(cboYear, cboMonth));
		cboMonth.addActionListener(e -> reloadDashboard(cboYear, cboMonth));

		return panel;
	}

	private JPanel createKpiPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
		panel.setBackground(Color.WHITE);

		lblTotalRevenue = new JLabel("0 VNĐ");
		lblCurrentMonthRevenue = new JLabel("0 VNĐ");
		lblTotalOrders = new JLabel("0");
		lblMonthOrders = new JLabel("0");

		panel.add(createKpiCard("TOTAL REVENUE", lblTotalRevenue));
		panel.add(createKpiCard("MONTH REVENUE", lblCurrentMonthRevenue));
		panel.add(createKpiCard("TOTAL ORDER", lblTotalOrders));
		panel.add(createKpiCard("MONTH ORDERS", lblMonthOrders));

		// ❌ bỏ updateKpiData(LocalDate.now().getYear(),
		// LocalDate.now().getMonthValue());
		// ✅ để mặc định là 0

		return panel;
	}

	private JPanel createKpiCard(String title, JLabel valueLabel) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(Color.WHITE);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)),
				new EmptyBorder(10, 10, 10, 10)));

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

		valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		valueLabel.setForeground(new Color(0, 128, 0));

		card.add(lblTitle, BorderLayout.NORTH);
		card.add(valueLabel, BorderLayout.CENTER);

		return card;
	}

	private JPanel createBarChart(String title, DefaultCategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart(title, "", "Millions", dataset);
		return new ChartPanel(chart);
	}

	private JPanel createLineChart(String title, DefaultCategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createLineChart(title, "", "%", dataset);
		return new ChartPanel(chart);
	}

	private JPanel createDonutChart(String title, DefaultPieDataset dataset) {
		JFreeChart chart = ChartFactory.createRingChart(title, dataset, false, true, false);
		return new ChartPanel(chart);
	}

	private JPanel createHeaderPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(247, 222, 155));
		panel.setBorder(new EmptyBorder(5, 20, 5, 20));
		panel.setPreferredSize(new Dimension(0, 40));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		JLabel lblTitle = new JLabel("Dashboard");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
		lblTitle.setForeground(Color.BLACK);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setVerticalAlignment(SwingConstants.CENTER);

		panel.add(lblTitle, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createStaffTablePanel(List<Staff> staffList) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)),
				new EmptyBorder(10, 10, 10, 10)));

		JLabel lblTitle = new JLabel("All Staffs");
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
		lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
		panel.add(lblTitle, BorderLayout.NORTH);

		String[] columns = { "No.", "Avatar", "Name", "Email", "Status" };
		Object[][] data = new Object[staffList.size()][5];

		for (int i = 0; i < staffList.size(); i++) {
			Staff s = staffList.get(i);
			data[i][0] = i + 1;
			data[i][1] = s.getLink();
			data[i][2] = s.getFullName();
			data[i][3] = s.getEmail();
			data[i][4] = s.getIsActive() == 1 ? "Active" : "Inactive";
		}

		JTable table = new JTable(data, columns) {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int column) {
				return column == 1 ? ImageIcon.class : String.class;
			}
		};

		table.setRowHeight(70);
		table.setIntercellSpacing(new Dimension(0, 20));
		table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
		table.setFont(new Font("SansSerif", Font.PLAIN, 13));
		table.setFillsViewportHeight(true);

		table.getColumnModel().getColumn(1).setCellRenderer(new AvatarRenderer());
		table.getColumnModel().getColumn(1).setPreferredWidth(60);

		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(0).setMaxWidth(100);

		table.getColumnModel().getColumn(1).setPreferredWidth(280);
		table.getColumnModel().getColumn(1).setMinWidth(280);
		table.getColumnModel().getColumn(1).setMaxWidth(280);

		table.getColumnModel().getColumn(4).setPreferredWidth(170);
		table.getColumnModel().getColumn(4).setMinWidth(170);
		table.getColumnModel().getColumn(4).setMaxWidth(170);
		table.getColumnModel().getColumn(4).setCellRenderer(new StaffStatusRender());

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(0, 260));
		panel.add(scrollPane, BorderLayout.CENTER);

		JTableHeader header = table.getTableHeader();

		header.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = header.columnAtPoint(e.getPoint());
				if (col >= 0) {
					header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					header.setCursor(Cursor.getDefaultCursor());
				}
			}
		});

		header.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				header.setCursor(Cursor.getDefaultCursor());
			}
		});

		table.setAutoCreateRowSorter(true);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() != 2)
					return;

				int viewRow = table.rowAtPoint(e.getPoint());
				int viewCol = table.columnAtPoint(e.getPoint());

				if (viewRow < 0 || viewCol != 1)
					return;

				int modelRow = table.convertRowIndexToModel(viewRow);
				Object value = table.getModel().getValueAt(modelRow, 1);

				if (value instanceof byte[]) {
					handleAvatarClick((byte[]) value);
				}
			}
		});

		table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = table.columnAtPoint(e.getPoint());
				if (col == 1) {
					table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					table.setCursor(Cursor.getDefaultCursor());
				}
			}
		});

		return panel;
	}

	private void handleAvatarClick(byte[] imgBytes) {

		ImageIcon icon = new ImageIcon(imgBytes);
		Image scaled = icon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);

		JLabel imageLabel = new JLabel(new ImageIcon(scaled));
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		imageLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Staff Avatar",
				Dialog.ModalityType.APPLICATION_MODAL);

		dialog.getContentPane().add(new JScrollPane(imageLabel));
		dialog.setSize(600, 600);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}

	private static class AvatarRenderer extends JLabel implements javax.swing.table.TableCellRenderer {

		public AvatarRenderer() {
			setHorizontalAlignment(CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			if (value instanceof byte[]) {
				byte[] imgBytes = (byte[]) value;
				ImageIcon icon = new ImageIcon(imgBytes);

				Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				setIcon(new ImageIcon(img));
			} else {
				setIcon(null);
			}

			return this;
		}
	}

	private JPanel createSalesChartPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
		panel.setBackground(Color.WHITE);

		JFreeChart monthChart = ChartFactory.createLineChart("MONTH REVENUE", "Month", "Triệu VNĐ",
				monthlyRevenueDataset);

		CategoryPlot plot = monthChart.getCategoryPlot();
		CategoryAxis axis = plot.getDomainAxis();

		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		ChartPanel chartMonth = new ChartPanel(monthChart);

		JFreeChart yearChart = ChartFactory.createBarChart("YEAR REVENUE", "Year", "Triệu VNĐ", yearlyRevenueDataset);

		ChartPanel chartYear = new ChartPanel(yearChart);

		Dimension size = new Dimension(0, 500);
		chartMonth.setPreferredSize(size);
		chartYear.setPreferredSize(size);

		panel.add(chartMonth);
		panel.add(chartYear);

		axis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
		axis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));

		return panel;
	}

	private JComboBox<String> createProductComboBox() {
		ProductModel productModel = new ProductModel();
		List<Product> products = productModel.findAll();

		JComboBox<String> comboBox = new JComboBox<>();

		for (Product p : products) {
			comboBox.addItem(p.getCode() + " - " + p.getTitle());
		}

		return comboBox;
	}

	private void reloadDashboard(JComboBox<String> cboYear, JComboBox<String> cboMonth) {
		if (cboYear.getSelectedIndex() == 0) {
			updateKpiData(0, 0);
			monthlyRevenueDataset.clear();
			yearlyRevenueDataset.clear();
			return;
		}

		int year = Integer.parseInt((String) cboYear.getSelectedItem());
		int monthIndex = cboMonth.getSelectedIndex();
		int month = (monthIndex == 0) ? 0 : monthIndex;

		updateKpiData(year, month);
		updateMonthlyChart(year);
		updateYearlyChart(year);
	}

	private void updateKpiData(int year, int month) {
		if (year == 0) {
			lblTotalRevenue.setText("0 VNĐ");
			lblCurrentMonthRevenue.setText("0 VNĐ");
			lblTotalOrders.setText("0");
			lblMonthOrders.setText("0");
			return;
		}

		BigDecimal totalRevenue = billModel.getRevenueByMonth(year, null);
		BigDecimal monthRevenue = (month == 0) ? BigDecimal.ZERO : billModel.getRevenueByMonth(year, month);

		lblTotalRevenue.setText(formatMoney(totalRevenue));
		lblCurrentMonthRevenue.setText(formatMoney(monthRevenue));

		int totalOrders = billModel.countBill(year, null);
		int monthOrders = (month == 0) ? 0 : billModel.countBill(year, month);

		lblTotalOrders.setText(String.valueOf(totalOrders));
		lblMonthOrders.setText(String.valueOf(monthOrders));
	}

	private String formatMoney(BigDecimal money) {
		if (money == null)
			return "0 VNĐ";
		return String.format("%,d VNĐ", money.longValue());
	}

	private void updateYearlyChart(int currentYear) {
		yearlyRevenueDataset.clear();

		for (int y = currentYear; y >= currentYear - 4; y--) {
			BigDecimal revenue = billModel.getRevenueByMonth(y, null);

			if (revenue == null) {
				revenue = BigDecimal.ZERO;
			}

			yearlyRevenueDataset.addValue(revenue.doubleValue() / 1_000_000, "Revenue", String.valueOf(y));
		}
	}

	private void updateMonthlyChart(int year) {
		monthlyRevenueDataset.clear();

		for (int m = 1; m <= 12; m++) {
			BigDecimal revenue = billModel.getRevenueByMonth(year, m);

			if (revenue == null) {
				revenue = BigDecimal.ZERO;
			}

			monthlyRevenueDataset.addValue(revenue.doubleValue() / 1_000_000, "Revenue", "Month " + m);
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
			setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int arc = 20;
			g2.setColor(bgColor);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

			super.paintComponent(g);
			g2.dispose();
		}
	}

	private class StaffStatusRender extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			String status = value.toString();

			Color bgColor = Color.WHITE;
			Color textColor = Color.WHITE;

			if ("Active".equals(status)) {
				bgColor = new Color(198, 255, 198);
				textColor = new Color(0, 100, 0);
			} else if ("Deactive".equals(status)) {
				bgColor = new Color(255, 204, 204);
				textColor = new Color(139, 0, 0);
			}

			StatusBadge badge = new StatusBadge(status, bgColor, textColor);
			badge.setPreferredSize(new Dimension(120, 30));

			JPanel wrapper = new JPanel(new GridBagLayout());
			wrapper.setOpaque(false);
			wrapper.add(badge);

			return wrapper;
		}
	}

}
