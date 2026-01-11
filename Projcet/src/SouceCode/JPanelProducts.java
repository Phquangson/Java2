package SouceCode;

import javax.swing.JPanel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import entities.Category;
import entities.Inventory;
import entities.Product;
import models.CategoryModel;
import models.InventoryModel;
import models.ProductModel;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.ListSelectionModel;

import java.text.DecimalFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

public class JPanelProducts extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable jtableProduct;
	private JTextField jtextFieldProductName;
	private JButton jbuttonClear;
	private JButton jbuttonAdd;
	private JComboBox<Category> jcomboBoxCategory;
	private boolean isSearchMode = false;

	/**
	 * Create the panel.
	 */
	public JPanelProducts() {
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel.setBackground(new Color(247, 222, 155));
		add(panel);

		JLabel lblNewLabel = new JLabel("Manage Products");
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
		panel.add(lblNewLabel);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		panel_3.setBorder(new TitledBorder(null, "Product search", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.add(panel_3);

		JLabel lblSearch = new JLabel("Search:");
		lblSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
		panel_3.add(lblSearch);

		jtextFieldProductName = new JTextField();
		jtextFieldProductName.setColumns(20);
		panel_3.add(jtextFieldProductName);

		jbuttonClear = new JButton("");
		jbuttonClear.setBackground(new Color(192, 192, 192));
		jbuttonClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_btnNewButton_2_actionPerformed(e);
			}
		});
//		jbuttonClear.setIcon(new ImageIcon(JPanelProducts.class.getResource("/resources/icon-trash.png")));
//		jbuttonClear.setFont(new Font("SansSerif", Font.PLAIN, 16));
//		panel_3.add(jbuttonClear);

		jbuttonAdd = new JButton("Add");
		jbuttonAdd.setBackground(new Color(192, 192, 192));
		jbuttonAdd.setFont(new Font("SansSerif", Font.BOLD, 13));
		jbuttonAdd.setIcon(new ImageIcon(JPanelProducts.class.getResource("/resources/icon-add.png")));
		jbuttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_btnAddProduct_actionPerformed(e);
			}
		});
		panel_3.add(jbuttonAdd);

		JTextField jtextFieldMinPrice = new JTextField(10);
		JTextField jtextFieldMaxPrice = new JTextField(10);
		JLabel label_1 = new JLabel("Price from:");
		label_1.setFont(new Font("SansSerif", Font.BOLD, 13));
		panel_3.add(label_1);
		panel_3.add(jtextFieldMinPrice);
		JLabel label_2 = new JLabel("to:");
		label_2.setFont(new Font("SansSerif", Font.BOLD, 13));
		panel_3.add(label_2);
		panel_3.add(jtextFieldMaxPrice);

		JButton jbuttonSearchPrice = new JButton("Search by Price");
		jbuttonSearchPrice.setIcon(new ImageIcon(JPanelProducts.class.getResource("/resources/icon-search.png")));
		jbuttonSearchPrice.setFont(new Font("SansSerif", Font.BOLD, 13));
		jbuttonSearchPrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BigDecimal min = new BigDecimal(jtextFieldMinPrice.getText().trim());
					BigDecimal max = new BigDecimal(jtextFieldMaxPrice.getText().trim());
					InventoryModel inventoryModel = new InventoryModel();
					List<Inventory> result = inventoryModel.findByPriceRange(min, max);
					loadDataToJTable(result);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Please enter valid numbers!");
				}
			}
		});
		panel_3.add(jbuttonSearchPrice);

		JPanel panelCategory = new JPanel();
		panelCategory.setBackground(Color.WHITE);
		panelCategory.setBorder(new TitledBorder(null, "Category", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		jcomboBoxCategory = new JComboBox<>();
		loadCategoriesToComboBox();
		panelCategory.add(jcomboBoxCategory);

		jcomboBoxCategory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Category selectedCategory = (Category) jcomboBoxCategory.getSelectedItem();
				if (selectedCategory != null && selectedCategory.getId() > 0) {

					InventoryModel inventoryModel = new InventoryModel();
					List<Inventory> inventories = inventoryModel.findByCategory(selectedCategory.getId());
					loadDataToJTable(inventories);
				} else {
					InventoryModel inventoryModel = new InventoryModel();
					loadDataToJTable(inventoryModel.findAll());
				}
			}
		});

		jcomboBoxCategory.addItem(new Category() {
			@Override
			public int getId() {
				return 0;
			}

			@Override
			public String toString() {
				return "-- Select Category --";
			}
		});

		panel_1.add(panelCategory);

		jbuttonAdd.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				jbuttonAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
		jbuttonClear.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				jbuttonClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});

		JPanel panel_2 = new JPanel();
		add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		panel_2.add(scrollPane, BorderLayout.CENTER);

		jtableProduct = new JTable();
		jtableProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(jtableProduct);

		jtableProduct.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = jtableProduct.rowAtPoint(e.getPoint());
				int col = jtableProduct.columnAtPoint(e.getPoint());

				if (col == 1 && row >= 0) {
					Object value = jtableProduct.getValueAt(row, col);
					if (value instanceof byte[]) {
						byte[] bytes = (byte[]) value;
						ImageIcon icon = new ImageIcon(bytes);

						Image scaledImage = icon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
						ImageIcon scaledIcon = new ImageIcon(scaledImage);

						JDialog dialog = new JDialog((Frame) null, "Product Image", true);
						JLabel label = new JLabel(scaledIcon);
						label.setHorizontalAlignment(JLabel.CENTER);

						dialog.getContentPane().add(new JScrollPane(label));
						dialog.setSize(600, 600);
						dialog.setLocationRelativeTo(jtableProduct);
						dialog.setVisible(true);
					}
				}

				if (col == 7 && row >= 0) {
					String productCode = jtableProduct.getValueAt(row, 2).toString();
					ProductModel productModel = new ProductModel();
					Product p = productModel.findByCode(productCode);

					if (p != null) {
						int confirm = JOptionPane.showConfirmDialog(jtableProduct,
								"Do you want to change the public status of this product?", "Confirm change",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

						if (confirm == JOptionPane.YES_OPTION) {
							int newStatus = (p.getIsPublic() == 1) ? 0 : 1;
							boolean result = productModel.changePublic(p.getId(), newStatus);

							if (result) {
								JOptionPane.showMessageDialog(jtableProduct, "The state change was successful");
								refreshProducts();
							} else {
								JOptionPane.showMessageDialog(jtableProduct, "Change the status failed");
							}
						}
					}
				}

				if (col == 8 && row >= 0) {
					String productCode = jtableProduct.getValueAt(row, 2).toString();
					ProductModel productModel = new ProductModel();
					Product p = productModel.findByCode(productCode);

					if (p != null) {
						openEditProduct(p);
					}
				}

				if (col == 9 && row >= 0) {
					int confirm = JOptionPane.showConfirmDialog(jtableProduct, "Do you want to delete this product?",
							"Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (confirm == JOptionPane.YES_OPTION) {
						String productCode = jtableProduct.getValueAt(row, 2).toString();

						ProductModel productModel = new ProductModel();
						Product p = productModel.findByCode(productCode);

						if (p != null) {
							boolean statusChanged = productModel.changeStatus(p.getId(), 19);

							boolean publicChanged = productModel.changePublic(p.getId(), 0);

							if (statusChanged && publicChanged) {
								JOptionPane.showMessageDialog(jtableProduct,
										"Product moved to Delete and set Deactive!", "Success",
										JOptionPane.INFORMATION_MESSAGE);
								refreshProducts();
							} else {
								JOptionPane.showMessageDialog(jtableProduct, "Failed to update product status!",
										"Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}

			}
		});

		init();
	}

	private void init() {
		ProductModel pm = new ProductModel();
//		pm.syncAllStatusByInventory();
		InventoryModel inventoryModel = new InventoryModel();
		loadDataToJTable(inventoryModel.findAll());

		jtextFieldProductName.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void updateSearch() {
				String keyword = jtextFieldProductName.getText().trim();
				InventoryModel inventoryModel = new InventoryModel();
				List<Inventory> result = inventoryModel.productSearch(keyword);
				if (keyword.isEmpty()) {
					isSearchMode = false;
					loadDataToJTable(inventoryModel.findAll());
				} else if (result == null || result.isEmpty()) {
					isSearchMode = true;
					loadDataToJTable(null);
				} else {
					isSearchMode = true;
					loadDataToJTable(result);
				}
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
	}

	private void loadDataToJTable(List<Inventory> inventories) {
		DefaultTableModel tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableModel.addColumn("No.");
		tableModel.addColumn("Image");
		tableModel.addColumn("Code");
		tableModel.addColumn("Name Product");
		tableModel.addColumn("Stock");
		tableModel.addColumn("Price");
		tableModel.addColumn("Status");
		tableModel.addColumn("Public");
		tableModel.addColumn("Edit");
		tableModel.addColumn("Delete");

		if (inventories == null || inventories.isEmpty()) {
			tableModel.addRow(new Object[] { "", "", "The product does not exist", null, "", "", "", "", "" });

		} else {
			int stt = 1;
			ProductModel productModel = new ProductModel();

			for (Inventory inventory : inventories) {
				Product product = inventory.getProduct();
				BigDecimal stockBD = inventory.getStock();

				product = productModel.findByCode(product.getCode());

				String status = getStatusById(product.getIdStatus());

				String publicStatus;
				if (product.getIsPublic() == 1) {
					publicStatus = "Active";
				} else if (product.getIsPublic() == 0) {
					publicStatus = "Deactive";
				} else {
					publicStatus = "Unknown";
				}

				DecimalFormat df = new DecimalFormat("#,###");
				String formattedPrice = df.format(product.getPrice()) + " VND";

				Object[] row = { stt++, product.getLink(), product.getCode(), product.getTitle(), stockBD,
						formattedPrice, status, publicStatus, "EDIT", "DELETE" };

				tableModel.addRow(row);
			}

		}

		jtableProduct.setModel(tableModel);

		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		jtableProduct.setRowSorter(sorter);

		sorter.setComparator(0, (o1, o2) -> {
			try {
				Integer i1 = Integer.parseInt(o1.toString());
				Integer i2 = Integer.parseInt(o2.toString());
				return i1.compareTo(i2);
			} catch (Exception e) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		sorter.setComparator(4, (o1, o2) -> {
			try {
				BigDecimal b1 = new BigDecimal(o1.toString());
				BigDecimal b2 = new BigDecimal(o2.toString());
				return b1.compareTo(b2);
			} catch (Exception e) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		sorter.setComparator(5, (o1, o2) -> {
			try {
				BigDecimal b1 = new BigDecimal(o1.toString());
				BigDecimal b2 = new BigDecimal(o2.toString());
				return b1.compareTo(b2);
			} catch (Exception e) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		jtableProduct.getTableHeader().setReorderingAllowed(false);
		jtableProduct.setRowHeight(70);
		jtableProduct.setIntercellSpacing(new Dimension(0, 20));
		jtableProduct.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

		jtableProduct.getColumnModel().getColumn(0).setMinWidth(50);
		jtableProduct.getColumnModel().getColumn(0).setMaxWidth(50);
		jtableProduct.getColumnModel().getColumn(0).setPreferredWidth(50);

		jtableProduct.getColumnModel().getColumn(1).setCellRenderer(new ImageRender());

		jtableProduct.getColumnModel().getColumn(3).setMinWidth(200);
		jtableProduct.getColumnModel().getColumn(3).setMaxWidth(400);
		jtableProduct.getColumnModel().getColumn(3).setPreferredWidth(300);

		jtableProduct.getColumnModel().getColumn(4).setMinWidth(150);
		jtableProduct.getColumnModel().getColumn(4).setMaxWidth(150);
		jtableProduct.getColumnModel().getColumn(4).setPreferredWidth(150);

		jtableProduct.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.LEFT);
				return lbl;
			}
		});

		jtableProduct.getColumnModel().getColumn(6).setCellRenderer(new StatusRender());
		jtableProduct.getColumnModel().getColumn(7).setCellRenderer(new PublicRender());

		jtableProduct.getColumnModel().getColumn(8).setCellRenderer(new EditRender());
		jtableProduct.getColumnModel().getColumn(9).setCellRenderer(new DeleteRender());

		jtableProduct.getColumnModel().getColumn(8).setPreferredWidth(30);
		jtableProduct.getColumnModel().getColumn(9).setPreferredWidth(30);

		jtableProduct.getTableHeader().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				jtableProduct.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});

		jtableProduct.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				jtableProduct.getTableHeader().setCursor(Cursor.getDefaultCursor());
			}
		});

		jtableProduct.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = jtableProduct.columnAtPoint(e.getPoint());
				if (col == 1 || col == 7 || col == 8 || col == 9) {
					jtableProduct.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					jtableProduct.setCursor(Cursor.getDefaultCursor());
				}
			}
		});

	}

	private class EditRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			JLabel label = new JLabel();
			label.setHorizontalAlignment(JLabel.CENTER);

			if (value != null && !"".equals(value.toString())) {
				label.setIcon(new ImageIcon(JPanelProducts.class.getResource("/resources/icon-edit.png")));
				label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			return label;
		}
	}

	private class DeleteRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			JLabel label = new JLabel();
			label.setHorizontalAlignment(JLabel.CENTER);

			if (value != null && !"".equals(value.toString())) {
				label.setIcon(new ImageIcon(JPanelProducts.class.getResource("/resources/icon-trash-32.png")));
				label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			return label;
		}
	}

	private class StatusRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			String status = value.toString();
			Color bgColor = Color.WHITE;
			Color textColor = Color.WHITE;

			switch (status) {
			case "In stock":
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

	private String getStatusById(int statusId) {
		switch (statusId) {
		case 6:
			return "In stock";
		case 16:
			return "Out of stock";
		case 17:
			return "Need to import";
		case 18:
			return "Almost out of stock";
		case 19:
			return "Delete";
		default:
			return "Unknown";
		}
	}

	private class PublicRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			String publicStatus = value.toString();
			Color bgColor = Color.WHITE;
			Color textColor = Color.WHITE;

			switch (publicStatus) {
			case "Active":
				bgColor = new Color(198, 255, 198);
				textColor = new Color(0, 100, 0);
				break;
			case "Deactive":
				bgColor = new Color(255, 204, 204);
				textColor = new Color(139, 0, 0);
				break;
			}

			StatusBadge badge = new StatusBadge(publicStatus, bgColor, textColor);
			badge.setPreferredSize(new Dimension(100, 30));

			JPanel wrapper = new JPanel(new GridBagLayout());
			wrapper.setOpaque(false);

			if (!isSearchMode) { // chỉ thêm icon khi không search
				JLabel iconLabel = new JLabel();
				iconLabel.setIcon(new ImageIcon(JPanelProducts.class.getResource("/resources/icon-change.png")));
				iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

				JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
				inner.setOpaque(false);
				inner.add(badge);
				inner.add(iconLabel);

				wrapper.add(inner, new GridBagConstraints());
			} else {
				wrapper.add(badge, new GridBagConstraints());
			}

			return wrapper;
		}
	}

	private class ImageRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JLabel jlabel = new JLabel();
			jlabel.setHorizontalAlignment(JLabel.CENTER);
			jlabel.setVerticalAlignment(JLabel.TOP);

			if (value instanceof byte[]) {
				byte[] bytes = (byte[]) value;
				Image image = new ImageIcon(bytes).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				jlabel.setIcon(new ImageIcon(image));
			}

			jlabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					jlabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			});

			return jlabel;
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	protected void do_btnNewButton_2_actionPerformed(ActionEvent e) {
		jtextFieldProductName.setText("");
		InventoryModel inventoryModel = new InventoryModel();
		loadDataToJTable(inventoryModel.findAll());
	}

	protected void do_btnAddProduct_actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog((Frame) null, "Add Product", true);
		dialog.setUndecorated(true);

		JPanelAddProduct addProductPanel = new JPanelAddProduct(null, this, dialog);

		dialog.getContentPane().add(addProductPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);

		InventoryModel inventoryModel = new InventoryModel();
		loadDataToJTable(inventoryModel.findAll());
	}

	public void refreshProducts() {
		InventoryModel inventoryModel = new InventoryModel();
		loadDataToJTable(inventoryModel.findAll());
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

	private void loadCategoriesToComboBox() {
		CategoryModel categoryModel = new CategoryModel();
		List<Category> categories = categoryModel.findAll();

		jcomboBoxCategory.removeAllItems();

		jcomboBoxCategory.addItem(new Category() {
			@Override
			public String toString() {
				return "-- Select Category --";
			}
		});

		if (categories != null && !categories.isEmpty()) {
			for (Category cat : categories) {
				jcomboBoxCategory.addItem(cat);
			}
		}

		jcomboBoxCategory.setSelectedIndex(0);
	}

	private void openEditProduct(Product product) {
		JDialog dialog = new JDialog((Frame) null, "Update Product", true);
		dialog.setUndecorated(true);

		JPanelAddProduct panelUpdate = new JPanelAddProduct(null, this, dialog, product);

		dialog.getContentPane().add(panelUpdate);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	protected void do_jbuttonAdd_1_actionPerformed(ActionEvent e) {
	}
}
