package models;

import java.beans.Statement;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import entities.Inventory;
import entities.InventoryActivity;
import entities.Product;

public class InventoryActivityModel {

	public List<Inventory> findAllProducts() {
		List<Inventory> inventories = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("""
					    SELECT p._code,
					           p._title,
					           p._price,
					           p._is_public,
					           p._id_status,
					           s._name,
					           i._stock,
					           (p._price * i._stock) AS _total_cost
					    FROM tbl_inventory i
					    JOIN tbl_product p ON p._id = i._id_product
					    JOIN tbl_supplier s ON s._id = p._id_supplier
					    ORDER BY p._title;
					""");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Product product = new Product();
				product.setCode(resultSet.getString("_code"));
				product.setTitle(resultSet.getString("_title"));
				product.setSupplierName(resultSet.getString("_name"));
				product.setPrice(resultSet.getBigDecimal("_price"));

				// thêm isPublic và idStatus
				product.setIsPublic(resultSet.getInt("_is_public")); // hoặc getBoolean nếu cột là boolean
				product.setIdStatus(resultSet.getInt("_id_status"));

				Inventory inventory = new Inventory();
				inventory.setStock(resultSet.getBigDecimal("_stock"));
				inventory.setTotalPrice(resultSet.getBigDecimal("_total_cost"));
				inventory.setProduct(product);

				inventories.add(inventory);
			}

		} catch (Exception e) {
			e.printStackTrace();
			inventories = null;
		} finally {
			ConnectDB.disconnect();
		}
		return inventories;
	}

	public boolean create(Product p, int stock) {
		String sqlProduct = """
				    INSERT INTO tbl_product
				    (_id, _code, _title, _description, _price, _quantity, _id_supplier,
				     _id_creator, _id_updater, _created_date, _updated_date, _is_public, _link,
				     _id_category, _id_status, _id_type)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = ConnectDB.connection(); PreparedStatement ps = conn.prepareStatement(sqlProduct)) {

			String productId = java.util.UUID.randomUUID().toString();

			ps.setString(1, productId);
			ps.setString(2, p.getCode());
			ps.setString(3, p.getTitle());
			ps.setString(4, p.getDescription() != null ? p.getDescription() : "");
			ps.setBigDecimal(5, p.getPrice());

			ps.setInt(6, p.getQuantity());

			if (p.getIdSupplier() > 0) {
				ps.setInt(7, p.getIdSupplier());
			} else {
				ps.setNull(7, java.sql.Types.INTEGER);
			}

			ps.setString(8, p.getIdCreator());
			ps.setString(9, p.getIdUpdater());
			ps.setTimestamp(10, p.getCreatedDate() != null ? new java.sql.Timestamp(p.getCreatedDate().getTime())
					: new java.sql.Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(11, p.getUpdatedDate() != null ? new java.sql.Timestamp(p.getUpdatedDate().getTime())
					: new java.sql.Timestamp(System.currentTimeMillis()));
			ps.setInt(12, p.getIsPublic());

			if (p.getLink() != null) {
				ps.setBytes(13, p.getLink());
			} else {
				ps.setNull(13, java.sql.Types.BLOB);
			}

			if (p.getIdCategory() > 0) {
				ps.setInt(14, p.getIdCategory());
			} else {
				ps.setNull(14, java.sql.Types.INTEGER);
			}

			if (p.getIdStatus() > 0) {
				ps.setInt(15, p.getIdStatus());
			} else {
				ps.setNull(15, java.sql.Types.INTEGER);
			}

			if (p.getIdType() > 0) {
				ps.setInt(16, p.getIdType());
			} else {
				ps.setNull(16, java.sql.Types.INTEGER);
			}

			int rows = ps.executeUpdate();
			if (rows > 0) {
				try (PreparedStatement psInv = conn.prepareStatement("""
						    INSERT INTO tbl_inventory
						    (_id_product, _stock, _id_creator, _id_updater, _created_date, _updated_date)
						    VALUES (?, ?, ?, ?, ?, ?)
						""")) {
					psInv.setString(1, productId);
					psInv.setInt(2, stock);
					psInv.setString(3, p.getIdCreator());
					psInv.setString(4, p.getIdUpdater());
					psInv.setTimestamp(5,
							p.getCreatedDate() != null ? new java.sql.Timestamp(p.getCreatedDate().getTime())
									: new java.sql.Timestamp(System.currentTimeMillis()));
					psInv.setTimestamp(6,
							p.getUpdatedDate() != null ? new java.sql.Timestamp(p.getUpdatedDate().getTime())
									: new java.sql.Timestamp(System.currentTimeMillis()));
					psInv.executeUpdate();
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return false;
	}

	public boolean existsByCodeOrTitle(String code, String title) {
		boolean exists = false;
		try (PreparedStatement ps = ConnectDB.connection()
				.prepareStatement("SELECT COUNT(*) FROM tbl_product WHERE _code = ? OR _title = ?")) {
			ps.setString(1, code);
			ps.setString(2, title);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				exists = rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return exists;
	}

	public List<Inventory> productSearch(String keyword) {
		List<Inventory> inventories = new ArrayList<>();
		String sql = """
				    SELECT i._id AS inventory_id, p._id AS product_id,
				           p._code, p._title, p._price,
				           i._stock, (p._price * i._stock) AS _total_price
				    FROM tbl_inventory i
				    JOIN tbl_product p ON p._id = i._id_product
				    WHERE (p._code LIKE ? OR p._title LIKE ?)
				      AND p._is_public = 1
				    ORDER BY p._title
				""";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			String searchPattern = "%" + keyword + "%";
			ps.setString(1, searchPattern);
			ps.setString(2, searchPattern);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.setCode(rs.getString("_code"));
				product.setTitle(rs.getString("_title"));
				product.setPrice(rs.getBigDecimal("_price"));
				product.setIsPublic(1); // vì đã lọc ở SQL

				Inventory inventory = new Inventory();
				inventory.setStock(rs.getBigDecimal("_stock"));
				inventory.setTotalPrice(rs.getBigDecimal("_total_price"));
				inventory.setId(rs.getInt("inventory_id"));
				inventory.setIdProduct(rs.getString("product_id"));
				inventory.setProduct(product);

				inventories.add(inventory);
			}
		} catch (Exception e) {
			e.printStackTrace();
			inventories = null;
		} finally {
			ConnectDB.disconnect();
		}
		return inventories;
	}

	public String generateNextReceiptCode() {
		String nextCode = "PX01";
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("""
					    SELECT MAX(_code) AS _max_code
					    FROM tbl_inventory_activity
					    WHERE _id_status = 7
					""");
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next() && rs.getString("_max_code") != null) {
				String maxCode = rs.getString("_max_code");
				int number = Integer.parseInt(maxCode.replaceAll("[^0-9]", ""));
				number++;
				nextCode = "PX" + String.format("%02d", number);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return nextCode;
	}

	public String generateNextIssueCode() {
		String nextCode = "PN01";
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("""
					    SELECT MAX(_code) AS _max_code
					    FROM tbl_inventory_activity
					    WHERE _id_status = 6
					""");
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next() && rs.getString("_max_code") != null) {
				String maxCode = rs.getString("_max_code");
				int number = Integer.parseInt(maxCode.replaceAll("[^0-9]", ""));
				number++;
				nextCode = "PN" + String.format("%02d", number);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return nextCode;
	}

	public InventoryActivity stockOutProduct(String productCode, int quantity, String receiptCode, String creator,
			byte[] imageBytes) {
		InventoryActivity activity = null;
		try {
			PreparedStatement psCheck = ConnectDB.connection().prepareStatement("""
					    SELECT i._id AS inventory_id, i._stock, p._price, p._id AS product_id
					    FROM tbl_inventory i
					    JOIN tbl_product p ON p._id = i._id_product
					    WHERE p._code = ?
					""");
			psCheck.setString(1, productCode);
			ResultSet rs = psCheck.executeQuery();

			if (rs.next()) {
				int currentStock = rs.getInt("_stock");
				BigDecimal unitPrice = rs.getBigDecimal("_price");
				String productId = rs.getString("product_id");
				int inventoryId = rs.getInt("inventory_id");

				if (quantity <= 0 || quantity > currentStock) {
					return null;
				}

				// Trừ stock
				PreparedStatement psUpdate = ConnectDB.connection().prepareStatement("""
						    UPDATE tbl_inventory
						    SET _stock = _stock - ?
						    WHERE _id = ?
						""");
				psUpdate.setInt(1, quantity);
				psUpdate.setInt(2, inventoryId);
				psUpdate.executeUpdate();

				BigDecimal totalCost = unitPrice.multiply(BigDecimal.valueOf(quantity));

				// Ghi activity kèm ảnh
				PreparedStatement psInsert = ConnectDB.connection().prepareStatement(
						"""
								    INSERT INTO tbl_inventory_activity
								    (_code, _id_product, _id_inventory, _change, _total_cost, _id_creator, _id_updater, _id_status, _created_date, _updated_date, _unit_price, _link)
								    VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)
								""",
						PreparedStatement.RETURN_GENERATED_KEYS);

				psInsert.setString(1, receiptCode);
				psInsert.setString(2, productId);
				psInsert.setInt(3, inventoryId);
				psInsert.setBigDecimal(4, BigDecimal.valueOf(-quantity));
				psInsert.setBigDecimal(5, totalCost);
				psInsert.setString(6, creator);
				psInsert.setString(7, creator);
				psInsert.setInt(8, 7);
				psInsert.setBigDecimal(9, unitPrice);

				// Tham số 10: ảnh
				if (imageBytes != null) {
					psInsert.setBytes(10, imageBytes);
				} else {
					psInsert.setNull(10, java.sql.Types.BLOB);
				}

				int rows = psInsert.executeUpdate();
				if (rows > 0) {
					ResultSet genKeys = psInsert.getGeneratedKeys();
					int newId = 0;
					if (genKeys.next()) {
						newId = genKeys.getInt(1);
					}

					activity = new InventoryActivity();
					activity.setId(newId);
					activity.setCode(receiptCode);
					activity.setChange(BigDecimal.valueOf(-quantity));
					activity.setTotalCost(totalCost);
					activity.setUnitPrice(unitPrice);
					activity.setIdProduct(productId);
					activity.setIdCreator(creator);
					activity.setIdUpdater(creator);
					activity.setIdStatus(7);
					activity.setIdInventory(inventoryId);
					activity.setCreatedDate(new Date());
					activity.setUpdatedDate(new Date());
					activity.setDescription("Stock out product " + productCode);

					if (imageBytes != null) {
						Byte[] imageObj = new Byte[imageBytes.length];
						for (int i = 0; i < imageBytes.length; i++) {
							imageObj[i] = imageBytes[i];
						}
						activity.setLink(imageObj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity = null;
		} finally {
			ConnectDB.disconnect();
		}
		return activity;
	}

	public boolean updateStockOutQuantity(int activityId, int oldQuantity, int newQuantity, BigDecimal unitPrice) {
		boolean success = false;
		try {
			PreparedStatement psCheck = ConnectDB.connection().prepareStatement("""
					    SELECT _id_inventory FROM tbl_inventory_activity WHERE _id = ?
					""");
			psCheck.setInt(1, activityId);
			ResultSet rs = psCheck.executeQuery();

			if (rs.next()) {
				int inventoryId = rs.getInt("_id_inventory");
				int diff = newQuantity - oldQuantity;

				PreparedStatement psUpdateStock;
				if (diff > 0) {
					psUpdateStock = ConnectDB.connection().prepareStatement("""
							    UPDATE tbl_inventory SET _stock = _stock - ? WHERE _id = ?
							""");
					psUpdateStock.setInt(1, diff);
					psUpdateStock.setInt(2, inventoryId);
				} else if (diff < 0) {
					psUpdateStock = ConnectDB.connection().prepareStatement("""
							    UPDATE tbl_inventory SET _stock = _stock + ? WHERE _id = ?
							""");
					psUpdateStock.setInt(1, Math.abs(diff));
					psUpdateStock.setInt(2, inventoryId);
				} else {
					return true;
				}
				psUpdateStock.executeUpdate();
				psUpdateStock.close();

				BigDecimal newChange = BigDecimal.valueOf(-newQuantity);
				BigDecimal newTotalCost = unitPrice.multiply(BigDecimal.valueOf(newQuantity));

				PreparedStatement psUpdateAct = ConnectDB.connection().prepareStatement("""
						    UPDATE tbl_inventory_activity
						    SET _change = ?, _total_cost = ?, _unit_price = ?, _updated_date = NOW()
						    WHERE _id = ?
						""");
				psUpdateAct.setBigDecimal(1, newChange);
				psUpdateAct.setBigDecimal(2, newTotalCost);
				psUpdateAct.setBigDecimal(3, unitPrice);
				psUpdateAct.setInt(4, activityId);

				int rows = psUpdateAct.executeUpdate();
				success = rows > 0;
				psUpdateAct.close();
			}

			psCheck.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		} finally {
			ConnectDB.disconnect();
		}
		return success;
	}

	public boolean deleteStockOut(String productCode, int quantity) {
		boolean success = false;
		try {
			PreparedStatement psCheck = ConnectDB.connection().prepareStatement("""
					    SELECT i._id AS inventory_id, i._stock
					    FROM tbl_inventory i
					    JOIN tbl_product p ON p._id = i._id_product
					    WHERE p._code = ?
					""");
			psCheck.setString(1, productCode);
			ResultSet rs = psCheck.executeQuery();

			if (rs.next()) {
				int inventoryId = rs.getInt("inventory_id");
				int currentStock = rs.getInt("_stock");

				PreparedStatement psUpdate;
				if (currentStock == 0) {
					psUpdate = ConnectDB.connection().prepareStatement("""
							    UPDATE tbl_inventory
							    SET _stock = ?
							    WHERE _id = ?
							""");
					psUpdate.setInt(1, quantity);
					psUpdate.setInt(2, inventoryId);
				} else {
					psUpdate = ConnectDB.connection().prepareStatement("""
							    UPDATE tbl_inventory
							    SET _stock = _stock + ?
							    WHERE _id = ?
							""");
					psUpdate.setInt(1, quantity);
					psUpdate.setInt(2, inventoryId);
				}
				psUpdate.executeUpdate();

				PreparedStatement psDelete = ConnectDB.connection().prepareStatement("""
						    DELETE FROM tbl_inventory_activity
						    WHERE _id_inventory = ?
						      AND _change = ?
						""");
				psDelete.setInt(1, inventoryId);
				psDelete.setBigDecimal(2, BigDecimal.valueOf(-quantity));
				int rows = psDelete.executeUpdate();

				if (rows > 0) {
					success = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		} finally {
			ConnectDB.disconnect();
		}
		return success;
	}

	public boolean saveInventoryActivity(InventoryActivity activity) {
		boolean success = false;
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement(
					"""
							    INSERT INTO tbl_inventory_activity
							    (_code, _id_product, _id_inventory, _change, _total_cost, _id_creator, _id_updater, _id_status, _created_date, _updated_date, _description, _unit_price)
							    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
							""",
					PreparedStatement.RETURN_GENERATED_KEYS);

			ps.setString(1, activity.getCode());
			ps.setString(2, activity.getIdProduct());
			ps.setInt(3, activity.getIdInventory());
			ps.setBigDecimal(4, activity.getChange());
			ps.setBigDecimal(5, activity.getTotalCost());
			ps.setString(6, activity.getIdCreator());
			ps.setString(7, activity.getIdUpdater());
			ps.setInt(8, activity.getIdStatus());
			ps.setTimestamp(9, new java.sql.Timestamp(activity.getCreatedDate().getTime()));
			ps.setTimestamp(10, new java.sql.Timestamp(activity.getUpdatedDate().getTime()));
			ps.setString(11, activity.getDescription());
			ps.setBigDecimal(12, activity.getUnitPrice());

			int rows = ps.executeUpdate();
			if (rows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					activity.setId(rs.getInt(1));
				}
				success = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		} finally {
			ConnectDB.disconnect();
		}
		return success;
	}

	public List<InventoryActivity> findAllActivities() {
		List<InventoryActivity> activities = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT _code, _id_creator, _created_date, _total_cost
					    FROM tbl_inventory_activity
					    WHERE _id_status = 7   -- chỉ lấy các phiếu xuất kho
					    ORDER BY _created_date DESC
					""");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InventoryActivity activity = new InventoryActivity();
				activity.setCode(rs.getString("_code"));
				activity.setIdCreator(rs.getString("_id_creator"));
				activity.setCreatedDate(rs.getTimestamp("_created_date"));
				activity.setTotalCost(rs.getBigDecimal("_total_cost"));
				activities.add(activity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return activities;
	}

	// Tìm sản phẩm theo ReceiptCode cho phiếu nhập kho (Stock In)
	public List<InventoryActivity> findProductsByReceiptCodeStockIn(String receiptCode) {
		List<InventoryActivity> list = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT p._code, p._title, ia._change, ia._total_cost, ia._unit_price,
					           s._id AS _id_supplier, s._name AS supplier_name
					    FROM tbl_inventory_activity ia
					    JOIN tbl_product p ON p._id = ia._id_product
					    JOIN tbl_supplier s ON s._id = p._id_supplier   -- lấy supplier từ product
					    WHERE ia._code = ? AND ia._id_status = 6
					""");
			ps.setString(1, receiptCode);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InventoryActivity act = new InventoryActivity();
				act.setIdProduct(rs.getString("_code"));
				act.setDescription(rs.getString("_title"));
				act.setChange(rs.getBigDecimal("_change"));
				act.setTotalCost(rs.getBigDecimal("_total_cost"));
				act.setUnitPrice(rs.getBigDecimal("_unit_price"));
				act.setIdSupplier(rs.getInt("_id_supplier"));
				act.setNameSupplier(rs.getString("supplier_name"));
				list.add(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	// Tìm sản phẩm theo ReceiptCode cho phiếu xuất kho (Stock Out)
	public List<InventoryActivity> findProductsByReceiptCodeStockOut(String receiptCode) {
		List<InventoryActivity> list = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT p._code, p._title, ia._change, ia._total_cost, ia._unit_price
					    FROM tbl_inventory_activity ia
					    JOIN tbl_product p ON p._id = ia._id_product
					    WHERE ia._code = ? AND ia._id_status = 7
					""");
			ps.setString(1, receiptCode);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InventoryActivity act = new InventoryActivity();
				act.setIdProduct(rs.getString("_code"));
				act.setDescription(rs.getString("_title"));
				act.setChange(rs.getBigDecimal("_change"));
				act.setTotalCost(rs.getBigDecimal("_total_cost"));
				act.setUnitPrice(rs.getBigDecimal("_unit_price"));
				list.add(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	public boolean deleteStockOutById(int activityId) {
		boolean success = false;
		try {
			PreparedStatement psCheck = ConnectDB.connection().prepareStatement("""
					    SELECT _id_inventory, ABS(_change) AS qty
					    FROM tbl_inventory_activity
					    WHERE _id = ?
					""");
			psCheck.setInt(1, activityId);
			ResultSet rs = psCheck.executeQuery();

			if (rs.next()) {
				int inventoryId = rs.getInt("_id_inventory");
				int quantity = rs.getInt("qty");

				PreparedStatement psUpdate = ConnectDB.connection().prepareStatement("""
						    UPDATE tbl_inventory
						    SET _stock = _stock + ?
						    WHERE _id = ?
						""");
				psUpdate.setInt(1, quantity);
				psUpdate.setInt(2, inventoryId);
				psUpdate.executeUpdate();

				PreparedStatement psDelete = ConnectDB.connection().prepareStatement("""
						    DELETE FROM tbl_inventory_activity
						    WHERE _id = ?
						""");
				psDelete.setInt(1, activityId);
				int rows = psDelete.executeUpdate();

				if (rows > 0) {
					success = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		} finally {
			ConnectDB.disconnect();
		}
		return success;
	}

	public boolean increaseStockOutById(int activityId, int addQuantity) {
		boolean success = false;
		try {
			PreparedStatement psGet = ConnectDB.connection().prepareStatement("""
					    SELECT ia._id_inventory, p._price, ia._change
					    FROM tbl_inventory_activity ia
					    JOIN tbl_product p ON p._id = ia._id_product
					    WHERE ia._id = ?
					""");

			psGet.setInt(1, activityId);
			ResultSet rs = psGet.executeQuery();

			if (!rs.next()) {
				return false;
			}

			int inventoryId = rs.getInt("_id_inventory");
			BigDecimal unitPrice = rs.getBigDecimal("_unit_price");
			BigDecimal currentChange = rs.getBigDecimal("_change");
			PreparedStatement psInv = ConnectDB.connection().prepareStatement("""
					    UPDATE tbl_inventory
					    SET _stock = _stock - ?
					    WHERE _id = ?
					""");
			psInv.setInt(1, addQuantity);
			psInv.setInt(2, inventoryId);
			psInv.executeUpdate();

			BigDecimal newChange = currentChange.subtract(BigDecimal.valueOf(addQuantity));
			BigDecimal addCost = unitPrice.multiply(BigDecimal.valueOf(addQuantity));
			PreparedStatement psUpdAct = ConnectDB.connection().prepareStatement("""
					    UPDATE tbl_inventory_activity
					    SET _change = ?, _total_cost = _total_cost + ?
					    WHERE _id = ?
					""");
			psUpdAct.setBigDecimal(1, newChange);
			psUpdAct.setBigDecimal(2, addCost);
			psUpdAct.setInt(3, activityId);

			int rows = psUpdAct.executeUpdate();
			success = rows > 0;

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		} finally {
			ConnectDB.disconnect();
		}
		return success;
	}

	public InventoryActivity stockOutProducts(String productCode, int quantity, String receiptCode, String creator) {
		InventoryActivity activity = null;
		try {
			PreparedStatement psCheck = ConnectDB.connection().prepareStatement("""
					    SELECT i._id AS inventory_id, i._stock, p._price, p._id AS product_id
					    FROM tbl_inventory i
					    JOIN tbl_product p ON p._id = i._id_product
					    WHERE p._code = ?
					""");
			psCheck.setString(1, productCode);
			ResultSet rs = psCheck.executeQuery();

			if (rs.next()) {
				int currentStock = rs.getInt("_stock");
				BigDecimal unitPrice = rs.getBigDecimal("_price");
				String productId = rs.getString("product_id");
				int inventoryId = rs.getInt("inventory_id");

				if (quantity <= 0 || quantity > currentStock) {
					return null;
				}

				// 1. Trừ stock
				PreparedStatement psUpdate = ConnectDB.connection().prepareStatement("""
						    UPDATE tbl_inventory
						    SET _stock = _stock - ?
						    WHERE _id = ?
						""");
				psUpdate.setInt(1, quantity);
				psUpdate.setInt(2, inventoryId);
				psUpdate.executeUpdate();

				BigDecimal totalCost = unitPrice.multiply(BigDecimal.valueOf(quantity));

				// 2. Ghi activity
				PreparedStatement psInsert = ConnectDB.connection().prepareStatement(
						"""
								    INSERT INTO tbl_inventory_activity
								    (_code, _id_product, _id_inventory, _change, _total_cost, _id_creator, _id_updater, _id_status, _created_date, _updated_date, _unit_price)
								    VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?)
								""",
						PreparedStatement.RETURN_GENERATED_KEYS);

				psInsert.setString(1, receiptCode);
				psInsert.setString(2, productId);
				psInsert.setInt(3, inventoryId);
				psInsert.setBigDecimal(4, BigDecimal.valueOf(-quantity));
				psInsert.setBigDecimal(5, totalCost);
				psInsert.setString(6, creator);
				psInsert.setString(7, creator);
				psInsert.setInt(8, 7); // xuất kho
				psInsert.setBigDecimal(9, unitPrice);

				int rows = psInsert.executeUpdate();
				if (rows > 0) {
					ResultSet genKeys = psInsert.getGeneratedKeys();
					int newId = 0;
					if (genKeys.next()) {
						newId = genKeys.getInt(1);
					}

					activity = new InventoryActivity();
					activity.setId(newId);
					activity.setCode(receiptCode);
					activity.setChange(BigDecimal.valueOf(-quantity));
					activity.setTotalCost(totalCost);
					activity.setUnitPrice(unitPrice);
					activity.setIdProduct(productCode);
					activity.setIdCreator(creator);
					activity.setIdUpdater(creator);
					activity.setIdStatus(7);
					activity.setIdInventory(inventoryId);
					activity.setCreatedDate(new Date());
					activity.setUpdatedDate(new Date());
					activity.setDescription("Stock out product " + productCode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity = null;
		} finally {
			ConnectDB.disconnect();
		}
		return activity;
	}

	public List<InventoryActivity> findGroupedActivitiesStockOut() {
		List<InventoryActivity> list = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT _code, _id_creator, MAX(_created_date) AS created_date,
					           SUM(_total_cost) AS total_cost,
					           MAX(_link) AS _link
					    FROM tbl_inventory_activity
					    WHERE _id_status = 7
					    GROUP BY _code, _id_creator
					    ORDER BY created_date DESC
					""");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InventoryActivity act = new InventoryActivity();
				act.setCode(rs.getString("_code"));
				act.setIdCreator(rs.getString("_id_creator"));
				act.setCreatedDate(rs.getTimestamp("created_date"));
				act.setTotalCost(rs.getBigDecimal("total_cost"));

				byte[] imageBytes = rs.getBytes("_link");
				if (imageBytes != null && imageBytes.length > 0) {
					Byte[] imageObj = new Byte[imageBytes.length];
					for (int i = 0; i < imageBytes.length; i++) {
						imageObj[i] = imageBytes[i];
					}
					act.setLink(imageObj);
				}

				list.add(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	public List<InventoryActivity> findGroupedActivitiesStockIn() {
		List<InventoryActivity> list = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT _code, _id_creator,
					           MAX(_created_date) AS created_date,
					           SUM(_total_cost) AS total_cost,
					           MAX(_link) AS _link
					    FROM tbl_inventory_activity
					    WHERE _id_status = 6
					    GROUP BY _code, _id_creator
					    ORDER BY created_date DESC
					""");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InventoryActivity act = new InventoryActivity();
				act.setCode(rs.getString("_code"));
				act.setIdCreator(rs.getString("_id_creator"));
				act.setCreatedDate(rs.getTimestamp("created_date"));
				act.setTotalCost(rs.getBigDecimal("total_cost"));

				byte[] imageBytes = rs.getBytes("_link");
				if (imageBytes != null && imageBytes.length > 0) {
					Byte[] imageObj = new Byte[imageBytes.length];
					for (int i = 0; i < imageBytes.length; i++) {
						imageObj[i] = imageBytes[i];
					}
					act.setLink(imageObj);
				}

				list.add(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	public InventoryActivity findByCodeStockIn(String code) {
		InventoryActivity act = null;
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT _code, _id_creator, _created_date, _total_cost, _link
					    FROM tbl_inventory_activity
					    WHERE _id_status = 6 AND _code = ?
					    LIMIT 1
					""");
			ps.setString(1, code);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				act = new InventoryActivity();
				act.setCode(rs.getString("_code"));
				act.setIdCreator(rs.getString("_id_creator"));
				act.setCreatedDate(rs.getTimestamp("_created_date"));
				act.setTotalCost(rs.getBigDecimal("_total_cost"));

				byte[] imageBytes = rs.getBytes("_link");
				if (imageBytes != null && imageBytes.length > 0) {
					Byte[] imageObj = new Byte[imageBytes.length];
					for (int i = 0; i < imageBytes.length; i++) {
						imageObj[i] = imageBytes[i];
					}
					act.setLink(imageObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return act;
	}

	public InventoryActivity findByCodeStockOut(String code) {
		InventoryActivity act = null;
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT _code, _id_creator, _created_date, _total_cost, _link
					    FROM tbl_inventory_activity
					    WHERE _id_status = 7 AND _code = ?
					    LIMIT 1
					""");
			ps.setString(1, code);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				act = new InventoryActivity();
				act.setCode(rs.getString("_code"));
				act.setIdCreator(rs.getString("_id_creator"));
				act.setCreatedDate(rs.getTimestamp("_created_date"));
				act.setTotalCost(rs.getBigDecimal("_total_cost"));

				byte[] imageBytes = rs.getBytes("_link");
				if (imageBytes != null && imageBytes.length > 0) {
					Byte[] imageObj = new Byte[imageBytes.length];
					for (int i = 0; i < imageBytes.length; i++) {
						imageObj[i] = imageBytes[i];
					}
					act.setLink(imageObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return act;
	}

	public Integer findInventoryIdByProductCode(String productCode) {
		Integer inventoryId = null;
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT i._id
					    FROM tbl_inventory i
					    JOIN tbl_product p ON p._id = i._id_product
					    WHERE p._code = ?
					""");
			ps.setString(1, productCode);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				inventoryId = rs.getInt("_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return inventoryId;
	}

	public InventoryActivity stockInProduct(String productCode, int quantity, String receiptCode, String creator,
			byte[] imageBytes) {
		InventoryActivity activity = null;
		try {
			PreparedStatement psCheck = ConnectDB.connection().prepareStatement("""
					    SELECT i._id AS inventory_id, i._stock, p._price, p._id AS product_id
					    FROM tbl_inventory i
					    JOIN tbl_product p ON p._id = i._id_product
					    WHERE p._code = ?
					""");
			psCheck.setString(1, productCode);
			ResultSet rs = psCheck.executeQuery();

			if (rs.next()) {
				int currentStock = rs.getInt("_stock");
				BigDecimal unitPrice = rs.getBigDecimal("_price");
				String productId = rs.getString("product_id");
				int inventoryId = rs.getInt("inventory_id");

				if (quantity <= 0) {
					return null;
				}

				// cập nhật tồn kho
				PreparedStatement psUpdate = ConnectDB.connection().prepareStatement("""
						    UPDATE tbl_inventory
						    SET _stock = _stock + ?
						    WHERE _id = ?
						""");
				psUpdate.setInt(1, quantity);
				psUpdate.setInt(2, inventoryId);
				psUpdate.executeUpdate();

				BigDecimal totalCost = unitPrice.multiply(BigDecimal.valueOf(quantity));

				// thêm bản ghi nhập kho kèm ảnh
				PreparedStatement psInsert = ConnectDB.connection().prepareStatement(
						"""
								INSERT INTO tbl_inventory_activity
								(_code, _id_product, _id_inventory, _change, _total_cost, _id_creator, _id_updater, _id_status, _created_date, _updated_date, _unit_price, _link)
								VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)
								""",
						PreparedStatement.RETURN_GENERATED_KEYS);

				psInsert.setString(1, receiptCode);
				psInsert.setString(2, productId);
				psInsert.setInt(3, inventoryId);
				psInsert.setBigDecimal(4, BigDecimal.valueOf(quantity));
				psInsert.setBigDecimal(5, totalCost);
				psInsert.setString(6, creator);
				psInsert.setString(7, creator);
				psInsert.setInt(8, 6); // nhập kho
				psInsert.setBigDecimal(9, unitPrice);
				psInsert.setBytes(10, imageBytes); // lưu ảnh vào cột _link

				int rows = psInsert.executeUpdate();
				if (rows > 0) {
					ResultSet genKeys = psInsert.getGeneratedKeys();
					int newId = 0;
					if (genKeys.next()) {
						newId = genKeys.getInt(1);
					}

					activity = new InventoryActivity();
					activity.setId(newId);
					activity.setCode(receiptCode);
					activity.setChange(BigDecimal.valueOf(quantity));
					activity.setTotalCost(totalCost);
					activity.setUnitPrice(unitPrice);
					activity.setIdProduct(productCode);
					activity.setIdCreator(creator);
					activity.setIdUpdater(creator);
					activity.setIdStatus(6); // nhập kho
					activity.setIdInventory(inventoryId);
					activity.setCreatedDate(new Date());
					activity.setUpdatedDate(new Date());
					activity.setDescription("Stock in product " + productCode);
					activity.setLink(imageBytes != null ? toWrapperArray(imageBytes) : null); // set vào entity
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity = null;
		} finally {
			ConnectDB.disconnect();
		}
		return activity;
	}

// tiện ích chuyển byte[] sang Byte[]
	private Byte[] toWrapperArray(byte[] bytes) {
		Byte[] result = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}

	public boolean updateProductQuantityByCode(String productCode, int quantity) {
		boolean success = false;
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    UPDATE tbl_product
					    SET _quantity = _quantity + ?
					    WHERE _code = ?
					""");

			ps.setInt(1, quantity);
			ps.setString(2, productCode);

			int rows = ps.executeUpdate();
			success = rows > 0;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return success;
	}

	public void refreshProductsAndAddRow(JTable tableProducts, JComboBox<Object> comboProducts, JLabel lblTotalAmount,
			Product p, int stock) {
		try {
			comboProducts.removeAllItems();
			comboProducts.addItem("-- Select product --");
			List<Inventory> inventories = findAllProducts();
			for (Inventory inv : inventories) {
				comboProducts.addItem(inv);
			}
			comboProducts.setSelectedIndex(0);

			DefaultTableModel model = (DefaultTableModel) tableProducts.getModel();
			long price = p.getPrice().longValue();
			long total = (long) stock * price;
			int no = model.getRowCount() + 1;
			model.addRow(new Object[] { no, p.getTitle(), stock,
					NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " VND",
					NumberFormat.getInstance(new Locale("vi", "VN")).format(total) + " VND" });

			long sum = 0;
			for (int i = 0; i < model.getRowCount(); i++) {
				String val = model.getValueAt(i, 4).toString().replaceAll("[^0-9]", "");
				if (!val.isEmpty()) {
					sum += Long.parseLong(val);
				}
			}
			lblTotalAmount.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(sum) + " VND");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshProductsAndAddRowFromInventory(JTable tableProducts, JComboBox<Object> comboProducts,
			JLabel lblTotalAmount, Product p) {
		try {
			comboProducts.removeAllItems();
			comboProducts.addItem("-- Select product --");
			List<Inventory> inventories = findAllProducts();
			for (Inventory inv : inventories) {
				comboProducts.addItem(inv);
			}
			comboProducts.setSelectedIndex(0);

			BigDecimal stock = BigDecimal.ZERO;
			for (Inventory inv : inventories) {
				if (inv.getProduct().getCode().equals(p.getCode())) {
					stock = inv.getStock();
					break;
				}
			}

			DefaultTableModel model = (DefaultTableModel) tableProducts.getModel();
			long price = p.getPrice().longValue();
			BigDecimal total = stock.multiply(p.getPrice());
			int no = model.getRowCount() + 1;
			model.addRow(new Object[] { no, p.getTitle(), stock,
					NumberFormat.getInstance(new Locale("vi", "VN")).format(price) + " VND",
					NumberFormat.getInstance(new Locale("vi", "VN")).format(total) + " VND" });

			BigDecimal sum = BigDecimal.ZERO;
			for (int i = 0; i < model.getRowCount(); i++) {
				String val = model.getValueAt(i, 4).toString().replaceAll("[^0-9]", "");
				if (!val.isEmpty()) {
					sum = sum.add(new BigDecimal(val));
				}
			}
			lblTotalAmount.setText(NumberFormat.getInstance(new Locale("vi", "VN")).format(sum) + " VND");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<InventoryActivity> searchStockOut(String keyword) {
		List<InventoryActivity> list = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT _code, _id_creator, MAX(_created_date) AS created_date,
					           SUM(_total_cost) AS total_cost
					    FROM tbl_inventory_activity
					    WHERE _id_status = 7
					      AND _code LIKE ?
					    GROUP BY _code, _id_creator
					    ORDER BY created_date DESC
					""");
			ps.setString(1, "%" + keyword + "%");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InventoryActivity act = new InventoryActivity();
				act.setCode(rs.getString("_code"));
				act.setIdCreator(rs.getString("_id_creator"));
				act.setCreatedDate(rs.getTimestamp("created_date"));
				act.setTotalCost(rs.getBigDecimal("total_cost"));
				list.add(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	public List<InventoryActivity> searchStockIn(String keyword) {
		List<InventoryActivity> list = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectDB.connection().prepareStatement("""
					    SELECT _code, _id_creator, MAX(_created_date) AS created_date,
					           SUM(_total_cost) AS total_cost
					    FROM tbl_inventory_activity
					    WHERE _id_status = 6
					      AND (_code LIKE ? OR _id_creator LIKE ?)
					    GROUP BY _code, _id_creator
					    ORDER BY created_date DESC
					""");
			ps.setString(1, "%" + keyword + "%");
			ps.setString(2, "%" + keyword + "%");

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InventoryActivity act = new InventoryActivity();
				act.setCode(rs.getString("_code"));
				act.setIdCreator(rs.getString("_id_creator"));
				act.setCreatedDate(rs.getTimestamp("created_date"));
				act.setTotalCost(rs.getBigDecimal("total_cost"));
				list.add(act);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	public int getTotalImportByYear(int year) {
		InventoryActivityModel model = new InventoryActivityModel();
		List<InventoryActivity> stockIn = model.findGroupedActivitiesStockIn();

		int totalImport = stockIn.stream()
				.filter(a -> a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getYear() == year)
				.map(a -> a.getTotalCost().intValue()).reduce(0, Integer::sum);

		return totalImport;
	}

	public int getTotalExportByYear(int year) {
		InventoryActivityModel model = new InventoryActivityModel();
		List<InventoryActivity> stockOut = model.findGroupedActivitiesStockOut();

		int totalExport = stockOut.stream()
				.filter(a -> a.getCreatedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).getYear() == year)
				.map(a -> a.getTotalCost().intValue()).reduce(0, Integer::sum);

		return totalExport;
	}

}
