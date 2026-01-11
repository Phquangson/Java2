package models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import entities.Inventory;
import entities.Product;

public class InventoryModel {

	
	private Inventory map(ResultSet rs) throws Exception { Product product = new Product(); product.setId(rs.getString("_id")); product.setCode(rs.getString("_code")); product.setTitle(rs.getString("_title")); product.setLink(rs.getBytes("_link")); product.setIdStatus(rs.getInt("_id_status")); product.setIsPublic(rs.getInt("_is_public")); product.setPrice(rs.getBigDecimal("_price")); Inventory inventory = new Inventory(); inventory.setId(rs.getInt("_id"));  inventory.setStock(rs.getBigDecimal("_stock")); inventory.setProduct(product); return inventory; }
	
	public List<Inventory> findAll() {
		List<Inventory> inventories = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("""
					    SELECT p._code, p._title, p._link, p._id_status, p._is_public, p._price,
					           i._stock
					    FROM tbl_inventory i
					    JOIN tbl_product p ON p._id = i._id_product
					    ORDER BY p._created_date desc
					""");

			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Product product = new Product();
				product.setCode(resultSet.getString("_code"));
				product.setTitle(resultSet.getString("_title"));
				product.setLink(resultSet.getBytes("_link"));
				product.setIdStatus(resultSet.getInt("_id_status"));
				product.setIsPublic(resultSet.getInt("_is_public"));
				product.setPrice(resultSet.getBigDecimal("_price"));

				Inventory inventory = new Inventory();
				inventory.setStock(resultSet.getBigDecimal("_stock"));
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

	public List<Inventory> productSearch(String keyword) {
		List<Inventory> list = new ArrayList<>();
		String sql = """
				    SELECT i.*, p.*
				    FROM tbl_inventory i
				    JOIN tbl_product p ON i._id_product = p._id
				    WHERE p._title LIKE ? OR p._code LIKE ?
				    ORDER BY p._created_date DESC
				""";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			String searchPattern = "%" + keyword + "%";
			ps.setString(1, searchPattern);
			ps.setString(2, searchPattern);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Inventory inv = map(rs); // map dữ liệu inventory + product
				list.add(inv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	// ------------------- DASHBOARD COUNT -------------------

	public int countProduct() {
		String sql = "SELECT COUNT(*) FROM tbl_product";
		return count(sql);
	}

	public int countCategory() {
		String sql = "SELECT COUNT(*) FROM tbl_category";
		return count(sql);
	}

	public int countCoupon() {
		String sql = "SELECT COUNT(*) FROM tbl_coupon";
		return count(sql);
	}

	private int count(String sql) {
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return 0;
	}

	public List<Inventory> findByCategory(int categoryId) {
		List<Inventory> list = new ArrayList<>();
		String sql = """
				    SELECT i.*, p.*
				    FROM tbl_inventory i
				    JOIN tbl_product p ON _id_product = p._id
				    WHERE p._id_category = ?
				""";

		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, categoryId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Product product = new Product();
					product.setId(rs.getString("_id"));
					product.setCode(rs.getString("_code"));
					product.setTitle(rs.getString("_title"));
					product.setLink(rs.getBytes("_link"));
					product.setIsPublic(rs.getInt("_is_public"));
					product.setIdStatus(rs.getInt("_id_status"));

					Inventory inventory = new Inventory();
					inventory.setId(rs.getInt("_id"));
					inventory.setStock(rs.getBigDecimal("_stock"));
					inventory.setProduct(product);

					list.add(inventory);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean updateStock(String productId, int stock) {
		String sql = "UPDATE tbl_inventory SET _stock = ? WHERE _id_product = ?";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setInt(1, stock);
			ps.setString(2, productId);

			boolean ok = ps.executeUpdate() > 0;

			if (ok) {
				ProductModel pm = new ProductModel();
				pm.updateStatusByInventory(productId);
			}

			return ok;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Inventory> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
	    List<Inventory> list = new ArrayList<>();
	    String sql = """
	        SELECT i.*, p._title, p._price, p._code, p._link, p._id_status, p._is_public
	        FROM tbl_inventory i
	        JOIN tbl_product p ON i._id_product = p._id
	        WHERE p._price BETWEEN ? AND ?
	        ORDER BY p._price ASC
	    """;
	    try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
	        ps.setBigDecimal(1, minPrice);
	        ps.setBigDecimal(2, maxPrice);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Inventory inv = new Inventory();
	                Product p = new Product();
	                p.setId(rs.getString("_id_product"));
	                p.setTitle(rs.getString("_title"));
	                p.setPrice(rs.getBigDecimal("_price"));
	                p.setCode(rs.getString("_code"));
	                p.setLink(rs.getBytes("_link"));
	                p.setIdStatus(rs.getInt("_id_status"));
	                p.setIsPublic(rs.getInt("_is_public"));
	                inv.setProduct(p);
	                inv.setStock(rs.getBigDecimal("_stock"));
	                list.add(inv);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        ConnectDB.disconnect();
	    }
	    return list;
	}


}
