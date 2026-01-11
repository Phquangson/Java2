package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entities.Coupon;

public class CouponModel {

	private Coupon map(ResultSet rs) throws SQLException {
		Coupon c = new Coupon();
		c.setId(rs.getInt("_id"));
		c.setCode(rs.getString("_code"));
		c.setTitle(rs.getString("_title"));
		c.setDiscountValue(rs.getBigDecimal("_discount_value"));
		c.setMinBillAmount(rs.getBigDecimal("_min_bill_amount"));
		c.setQuantity(rs.getInt("_quantity"));
		c.setExpiredDate(rs.getDate("_expired_date"));
		c.setIdCreator(rs.getString("_id_creator"));
		c.setIdUpdater(rs.getString("_id_upader"));
		c.setCreatedDate(rs.getTimestamp("_created_date"));
		c.setUpdatedDate(rs.getTimestamp("_updated_date"));
		c.setIdType(rs.getInt("_id_type"));
		c.setIsActive(rs.getInt("_is_active"));
		return c;
	}

	// ================= FIND ALL =================
	public List<Coupon> findAll() {
		List<Coupon> list = new ArrayList<>();
		String sql = """
				    SELECT c._id, c._code, c._title, c._discount_value, c._min_bill_amount,
				           c._quantity, c._expired_date, c._id_creator, c._id_upader,
				           c._created_date, c._updated_date, c._id_type, c._is_active,
				           t._title AS type_title
				    FROM tbl_coupon c
				    LEFT JOIN tbl_type t ON c._id_type = t._id
				    ORDER BY c._id DESC
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Coupon c = new Coupon();
				c.setId(rs.getInt("_id"));
				c.setCode(rs.getString("_code"));
				c.setTitle(rs.getString("_title"));
				c.setDiscountValue(rs.getBigDecimal("_discount_value"));
				c.setMinBillAmount(rs.getBigDecimal("_min_bill_amount"));
				c.setQuantity(rs.getInt("_quantity"));
				c.setExpiredDate(rs.getDate("_expired_date"));
				c.setIdCreator(rs.getString("_id_creator"));
				c.setIdUpdater(rs.getString("_id_upader"));
				c.setCreatedDate(rs.getTimestamp("_created_date"));
				c.setUpdatedDate(rs.getTimestamp("_updated_date"));
				c.setIdType(rs.getInt("_id_type"));
				c.setIsActive(rs.getInt("_is_active"));
				c.setTypeTitle(rs.getString("type_title"));

				list.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean create(Coupon c) {
		String sql = """
				    INSERT INTO tbl_coupon
				    (_code,_title,_discount_value,_min_bill_amount,_quantity,
				     _expired_date,_id_creator,_id_upader,_created_date,_updated_date,
				     _id_type,_is_active)
				    VALUES (?,?,?,?,?,?,?, ?,NOW(),NOW(),?,?)
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, c.getCode());
			ps.setString(2, c.getTitle());
			ps.setBigDecimal(3, c.getDiscountValue());
			ps.setBigDecimal(4, c.getMinBillAmount());
			ps.setInt(5, c.getQuantity());
			ps.setDate(6, c.getExpiredDate() == null ? null : new java.sql.Date(c.getExpiredDate().getTime()));
			ps.setString(7, c.getIdCreator());
			ps.setString(8, c.getIdUpdater());
			ps.setInt(9, c.getIdType());
			ps.setInt(10, c.getIsActive());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(Coupon c) {
		String sql = """
				    UPDATE tbl_coupon SET
				    _title=?, _discount_value=?, _min_bill_amount=?, _quantity=?,
				    _expired_date=?, _id_upader=?, _updated_date=NOW(),
				    _id_type=?, _is_active=?
				    WHERE _id=?
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, c.getTitle());
			ps.setBigDecimal(2, c.getDiscountValue());
			ps.setBigDecimal(3, c.getMinBillAmount());
			ps.setInt(4, c.getQuantity());
			ps.setDate(5, c.getExpiredDate() == null ? null : new java.sql.Date(c.getExpiredDate().getTime()));
			ps.setString(6, c.getIdUpdater());
			ps.setInt(7, c.getIdType());
			ps.setInt(8, c.getIsActive());
			ps.setInt(9, c.getId());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(int id) {
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement("DELETE FROM tbl_coupon WHERE _id=?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Coupon findByCode(String code) {
		String sql = """
				    SELECT c._id, c._code, c._title, c._discount_value, c._min_bill_amount,
				           c._quantity, c._expired_date, c._id_creator, c._id_upader,
				           c._created_date, c._updated_date, c._id_type, c._is_active,
				           t._title AS type_title
				    FROM tbl_coupon c
				    LEFT JOIN tbl_type t ON c._id_type = t._id
				    WHERE c._code = ?
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, code);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Coupon c = new Coupon();
					c.setId(rs.getInt("_id"));
					c.setCode(rs.getString("_code"));
					c.setTitle(rs.getString("_title"));
					c.setDiscountValue(rs.getBigDecimal("_discount_value"));
					c.setMinBillAmount(rs.getBigDecimal("_min_bill_amount"));
					c.setQuantity(rs.getInt("_quantity"));
					c.setExpiredDate(rs.getDate("_expired_date"));
					c.setIdCreator(rs.getString("_id_creator"));
					c.setIdUpdater(rs.getString("_id_upader"));
					c.setCreatedDate(rs.getTimestamp("_created_date"));
					c.setUpdatedDate(rs.getTimestamp("_updated_date"));
					c.setIdType(rs.getInt("_id_type"));
					c.setIsActive(rs.getInt("_is_active"));
					c.setTypeTitle(rs.getString("type_title"));
					return c;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Coupon findById(int id) {
		String sql = """
				    SELECT c._id, c._code, c._title, c._discount_value, c._min_bill_amount,
				           c._quantity, c._expired_date, c._id_creator, c._id_upader,
				           c._created_date, c._updated_date, c._id_type, c._is_active,
				           t._title AS type_title
				    FROM tbl_coupon c
				    LEFT JOIN tbl_type t ON c._id_type = t._id
				    WHERE c._id = ?
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Coupon c = new Coupon();
					c.setId(rs.getInt("_id"));
					c.setCode(rs.getString("_code"));
					c.setTitle(rs.getString("_title"));
					c.setDiscountValue(rs.getBigDecimal("_discount_value"));
					c.setMinBillAmount(rs.getBigDecimal("_min_bill_amount"));
					c.setQuantity(rs.getInt("_quantity"));
					c.setExpiredDate(rs.getDate("_expired_date"));
					c.setIdCreator(rs.getString("_id_creator"));
					c.setIdUpdater(rs.getString("_id_upader"));
					c.setCreatedDate(rs.getTimestamp("_created_date"));
					c.setUpdatedDate(rs.getTimestamp("_updated_date"));
					c.setIdType(rs.getInt("_id_type"));
					c.setIsActive(rs.getInt("_is_active"));
					c.setTypeTitle(rs.getString("type_title"));
					return c;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean toggleActive(int id) {
		String sql = """
				    UPDATE tbl_coupon
				    SET _is_active = CASE WHEN _is_active = 1 THEN 0 ELSE 1 END,
				        _updated_date = NOW()
				    WHERE _id = ?
				""";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public List<Coupon> findActiveCoupons() {
	    List<Coupon> list = new ArrayList<>();
	    String sql = "SELECT * FROM tbl_coupon WHERE _is_active = 1";
	    try (Connection conn = ConnectDB.connection();
	         PreparedStatement pst = conn.prepareStatement(sql);
	         ResultSet rs = pst.executeQuery()) {

	        while (rs.next()) {
	            Coupon c = new Coupon();
	            c.setId(rs.getInt("_id"));
	            c.setCode(rs.getString("_code"));
	            c.setTitle(rs.getString("_title"));
	            c.setDiscountValue(rs.getBigDecimal("_discount_value"));
	            c.setMinBillAmount(rs.getBigDecimal("_min_bill_amount"));
	            c.setQuantity(rs.getInt("_quantity"));
	            c.setExpiredDate(rs.getDate("_expired_date"));
	            c.setIdType(rs.getInt("_id_type"));
	            c.setIsActive(rs.getInt("_is_active"));
	            list.add(c);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

}
