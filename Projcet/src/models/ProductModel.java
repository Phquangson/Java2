package models;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entities.Product;

public class ProductModel {

	// ========= FIND ALL =========
	public List<Product> findAll() {
		List<Product> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_product ORDER BY _created_date DESC";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	// ========= FIND BY ID =========
	public Product findById(String id) {
		String sql = "SELECT * FROM tbl_product WHERE _id = ?";
		Product p = null;
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				p = map(rs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return p;
	}

	// ========= CREATE =========
	public boolean create(Product p) {
		String sql = """
				INSERT INTO tbl_product
				(_id, _title, _description, _price, _discount_price, _percent, _link, _code,
				 _id_creator, _id_updater, _quantity,
				 _id_status, _id_type, _id_category, _id_supplier, _is_public)
				VALUES (UUID(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
				        ?, ?, ?, ?, ?)
				""";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			setParams(ps, p, false);
			boolean result = ps.executeUpdate() > 0;
			if (result) {
				updateStatusByInventory(p.getId());
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	// ========= SET PARAMS =========
	private void setParams(PreparedStatement ps, Product p, boolean isUpdate) throws SQLException {
		ps.setString(1, p.getTitle());
		ps.setString(2, p.getDescription());
		ps.setBigDecimal(3, p.getPrice());
		ps.setBigDecimal(4, p.getDiscountPrice());
		ps.setBigDecimal(5, p.getPercent());

		ps.setBytes(6, p.getLink());
		ps.setString(7, p.getCode());

		if (!isUpdate) {
			ps.setString(8, p.getIdCreator());
			ps.setString(9, p.getIdUpdater());
			ps.setInt(10, p.getQuantity());
			ps.setInt(11, p.getIdType());
			ps.setInt(12, p.getIdCategory());
			ps.setInt(13, p.getIdSupplier());
			ps.setInt(14, p.getIsPublic());
		} else {
			ps.setString(8, p.getIdUpdater());
			ps.setInt(9, p.getQuantity());
			ps.setInt(10, p.getIdStatus());
			ps.setInt(11, p.getIdType());
			ps.setInt(12, p.getIdCategory());
			ps.setInt(13, p.getIdSupplier());
			ps.setInt(14, p.getIsPublic());
		}
	}

	// ========= UPDATE ========= (ĐÃ SỬA ĐÚNG – 15 tham số + 1 WHERE = 16)
	public boolean update(Product p) {
		String sql = """
				UPDATE tbl_product SET
				_title = ?,
				_description = ?,
				_price = ?,
				_discount_price = ?,
				_percent = ?,
				_link = ?,
				_code = ?,
				_id_updater = ?,
				_updated_date = CURRENT_TIMESTAMP,
				_quantity = ?,
				_id_status = ?,
				_id_type = ?,
				_id_category = ?,
				_id_supplier = ?,
				_is_public = ?
				WHERE _id = ?
				""";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			setParams(ps, p, true);
			ps.setString(15, p.getId()); // ✅ tham số cuối cho WHERE
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	// ========= DELETE =========
	public boolean delete(String id) {
		String sql = "DELETE FROM tbl_product WHERE _id=?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	// ========= COUNT (Dashboard) =========
	public int countProduct() {
		String sql = "SELECT COUNT(*) FROM tbl_product";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return 0;
	}

	// ========= MAP =========
	private Product map(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getString("_id"));
		p.setTitle(rs.getString("_title"));
		p.setDescription(rs.getString("_description"));
		p.setQuantity(rs.getInt("_quantity"));
		p.setPrice(rs.getBigDecimal("_price"));
		p.setDiscountPrice(rs.getBigDecimal("_discount_price"));
		p.setPercent(rs.getBigDecimal("_percent"));
		p.setLink(rs.getBytes("_link"));
		p.setCode(rs.getString("_code"));
		p.setIdCreator(rs.getString("_id_creator"));
		p.setIdUpdater(rs.getString("_id_updater"));
		p.setCreatedDate(rs.getTimestamp("_created_date"));
		p.setUpdatedDate(rs.getTimestamp("_updated_date"));
		p.setIdStatus(rs.getInt("_id_status"));
		p.setIdType(rs.getInt("_id_type"));
		p.setIdCategory(rs.getInt("_id_category"));
		p.setIdSupplier(rs.getInt("_id_supplier"));
		p.setIsPublic(rs.getInt("_is_public"));

		return p;
	}

	// Thêm vào class ProductModel
	public Product findByTitle(String title) {
		Product product = null;
		String sql = "SELECT * FROM tbl_product WHERE _title = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, title);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					product = map(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return product;
	}

	public Product findByCode(String code) {
		Product p = null;
		String sql = "SELECT * FROM tbl_product WHERE _code = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, code);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					p = map(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return p;
	}

	public boolean changePublic(String id, int isPublic) {
		String sql = "UPDATE tbl_product SET _is_public = ?, _updated_date = CURRENT_TIMESTAMP WHERE _id = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setInt(1, isPublic);
			ps.setString(2, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public String findLatestCodeByPrefix(String prefix) {
		String sql = "SELECT _code FROM tbl_product WHERE _code LIKE ? ORDER BY _code DESC LIMIT 1";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, prefix + "%");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("_code");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return null;
	}

	public boolean updateWithoutImage(Product p) {
		String sql = """
				UPDATE tbl_product SET
				_title = ?,
				_description = ?,
				_price = ?,
				_discount_price = ?,
				_percent = ?,
				_code = ?,
				_id_updater = ?,
				_updated_date = CURRENT_TIMESTAMP,
				_quantity = ?,
				_id_status = ?,
				_id_type = ?,
				_id_category = ?,
				_id_supplier = ?,
				_is_public = ?
				WHERE _id = ?
				""";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, p.getTitle());
			ps.setString(2, p.getDescription());
			ps.setBigDecimal(3, p.getPrice());
			ps.setBigDecimal(4, p.getDiscountPrice());
			ps.setBigDecimal(5, p.getPercent());
			ps.setString(6, p.getCode());
			ps.setString(7, p.getIdUpdater());
			ps.setInt(8, p.getQuantity());
			ps.setInt(9, p.getIdStatus());
			ps.setInt(10, p.getIdType());
			ps.setInt(11, p.getIdCategory());
			ps.setInt(12, p.getIdSupplier());
			ps.setInt(13, p.getIsPublic());
			ps.setString(14, p.getId());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public boolean updateAuto(Product p) {
		if (p.getLink() != null) {
			return update(p);
		} else {
			return updateWithoutImage(p);
		}
	}

	public boolean existsByTitleExcludingId(String title, String id) {
		String sql = "SELECT COUNT(*) FROM tbl_product WHERE _title = ? AND _id <> ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, title);
			ps.setString(2, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return false;
	}

	public boolean updateStatusByInventory(String productId) {
		int statusId;

		String sqlStock = "SELECT _stock FROM tbl_inventory WHERE _id_product = ?";
		int stock = 0;

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sqlStock)) {
			ps.setString(1, productId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					stock = rs.getInt("_stock");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (stock > 60) {
			statusId = 6;
		} else if (stock > 30) {
			statusId = 18;
		} else if (stock > 0) {
			statusId = 17;
		} else {
			statusId = 16;
		}

		String sqlUpdate = """
				    UPDATE tbl_product
				    SET _id_status = ?, _updated_date = CURRENT_TIMESTAMP
				    WHERE _id = ?
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sqlUpdate)) {
			ps.setInt(1, statusId);
			ps.setString(2, productId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public void syncAllStatusByInventory() {
		String sql = """
				    SELECT p._id, p._id_status, i._stock
				    FROM tbl_product p
				    JOIN tbl_inventory i ON p._id = i._id_product
				    WHERE p._id_status <> 19
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				int statusId = rs.getInt("_id_status");
				String productId = rs.getString("_id");

				if (statusId == 19)
					continue;

				updateStatusByInventory(productId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
	}

	public boolean softDelete(String productId) {
		String sql = "UPDATE tbl_product SET _id_status = 19 WHERE _id = ?";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, productId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public boolean changeStatus(String productId, int newStatusId) {
		String sql = "UPDATE tbl_product SET _id_status = ?, _updated_date = CURRENT_TIMESTAMP WHERE _id = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setInt(1, newStatusId);
			ps.setString(2, productId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}
	
	public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
	    List<Product> list = new ArrayList<>();
	    String sql = "SELECT * FROM tbl_product WHERE _price BETWEEN ? AND ? ORDER BY _price ASC";
	    try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
	        ps.setBigDecimal(1, minPrice);
	        ps.setBigDecimal(2, maxPrice);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(map(rs));
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