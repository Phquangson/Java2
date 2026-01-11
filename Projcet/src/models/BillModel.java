package models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import entities.Bill;

public class BillModel {

	// ========= FIND ALL =========
	public List<Bill> findAll() {
		List<Bill> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_bill ORDER BY _created_date DESC";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapToBill(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}

		return list;
	}

	// ========= FIND BY CODE (MỚI THÊM) =========
	public Bill findByCode(String code) {
		Bill bill = null;
		String sql = "SELECT * FROM tbl_bill WHERE _code = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, code);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					bill = mapToBill(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return bill;
	}

	// ========= FIND BY KEYWORD =========
	public List<Bill> findByKeyword(String keyword) {
		List<Bill> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_bill WHERE _code LIKE ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, "%" + keyword + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapToBill(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	// ========= FIND BY CUSTOMER ID =========
	public List<Bill> findByCustomerId(String customerId) {
		List<Bill> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_bill WHERE _id_customer = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, customerId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapToBill(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	// ========= CREATE =========
	public boolean create(Bill bill) {
		String sql = "INSERT INTO tbl_bill (_id, _code, _subtotal, _discount, _total, _total_quantity, _note, _payment_method, _VAT, _id_creator, _id_updater, _id_customer, _id_coupon, _id_status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, bill.getId());
			ps.setString(2, bill.getCode());
			ps.setBigDecimal(3, bill.getSubtotal());
			ps.setBigDecimal(4, bill.getDiscount());
			ps.setBigDecimal(5, bill.getTotal());
			ps.setInt(6, bill.getTotalQuantity());
			ps.setString(7, bill.getNote());
			ps.setString(8, bill.getPaymentMethod());
			ps.setBigDecimal(9, bill.getVAT());
			ps.setString(10, bill.getIdCreator());
			ps.setString(11, bill.getIdUpdater());
			ps.setString(12, bill.getIdCustomer());
			ps.setObject(13, bill.getIdCoupon());
			ps.setInt(14, bill.getIdStatus());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public Bill findById(String id) {
		Bill b = null;
		String sql = "SELECT * FROM tbl_bill WHERE _id = ?";
		try (Connection conn = ConnectDB.connection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				b = new Bill();
				b.setCode(rs.getString("_code"));
				b.setIdCustomer(rs.getString("_id_customer"));
				b.setTotal(rs.getBigDecimal("_total"));
				b.setCreatedDate(rs.getTimestamp("_created_date"));
				b.setUpdatedDate(rs.getTimestamp("_updated_date"));
				b.setIdStatus(rs.getInt("_id_status"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	public List<Bill> findByFilter(Date from, Date to, String statusFilter) {
		List<Bill> list = new ArrayList<>();

		StringBuilder sql = new StringBuilder(
				"SELECT b._code, b._id_customer, b._total, b._created_date, b._updated_date, b._id_status "
						+ "FROM tbl_bill b WHERE 1=1");

		if (from != null) {
			sql.append(" AND b._created_date >= ?");
		}
		if (to != null) {
			sql.append(" AND b._created_date < ?"); // dùng < thay vì <=
		}

		if ("Đơn hàng bán".equalsIgnoreCase(statusFilter)) {
			sql.append(" AND b._id_status = 9"); // Paid
		} else if ("Đơn hàng chờ".equalsIgnoreCase(statusFilter)) {
			sql.append(" AND b._id_status = 10"); // Pending
		}

		sql.append(" ORDER BY b._created_date DESC");

		try (Connection conn = ConnectDB.connection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int index = 1;

			// Ngày bắt đầu: set về 00:00:00
			if (from != null) {
				Calendar calFrom = Calendar.getInstance();
				calFrom.setTime(from);
				calFrom.set(Calendar.HOUR_OF_DAY, 0);
				calFrom.set(Calendar.MINUTE, 0);
				calFrom.set(Calendar.SECOND, 0);
				calFrom.set(Calendar.MILLISECOND, 0);
				stmt.setTimestamp(index++, new Timestamp(calFrom.getTimeInMillis()));
			}

			// Ngày kết thúc: cộng thêm 1 ngày, set về 00:00:00
			if (to != null) {
				Calendar calTo = Calendar.getInstance();
				calTo.setTime(to);
				calTo.add(Calendar.DATE, 1); // cộng thêm 1 ngày
				calTo.set(Calendar.HOUR_OF_DAY, 0);
				calTo.set(Calendar.MINUTE, 0);
				calTo.set(Calendar.SECOND, 0);
				calTo.set(Calendar.MILLISECOND, 0);
				stmt.setTimestamp(index++, new Timestamp(calTo.getTimeInMillis()));
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Bill b = new Bill();
				b.setCode(rs.getString("_Code"));
				b.setIdCustomer(rs.getString("_id_customer"));
				b.setTotal(rs.getBigDecimal("_total"));
				b.setCreatedDate(rs.getTimestamp("_created_date"));
				b.setUpdatedDate(rs.getTimestamp("_updated_date"));
				b.setIdStatus(rs.getInt("_id_status"));
				list.add(b);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// ========= UPDATE =========
	public boolean update(Bill bill) {
		String sql = "UPDATE tbl_bill SET _code = ?, _subtotal = ?, _discount = ?, _total = ?, _total_quantity = ?, _note = ?, _payment_method = ?, _VAT = ?, _id_updater = ?, _updated_date = CURRENT_TIMESTAMP, _id_customer = ?, _id_coupon = ?, _id_status = ? WHERE _id = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, bill.getCode());
			ps.setBigDecimal(2, bill.getSubtotal());
			ps.setBigDecimal(3, bill.getDiscount());
			ps.setBigDecimal(4, bill.getTotal());
			ps.setInt(5, bill.getTotalQuantity());
			ps.setString(6, bill.getNote());
			ps.setString(7, bill.getPaymentMethod());
			ps.setBigDecimal(8, bill.getVAT());
			ps.setString(9, bill.getIdUpdater());
			ps.setString(10, bill.getIdCustomer());
			ps.setObject(11, bill.getIdCoupon());
			ps.setInt(12, bill.getIdStatus());
			ps.setString(13, bill.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	// ========= DELETE =========
	public boolean delete(String id) {
		String sql = "DELETE FROM tbl_bill WHERE _id = ?";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	// ========= MAP TO BILL =========
	private Bill mapToBill(ResultSet rs) throws SQLException {
		Bill b = new Bill();
		b.setId(rs.getString("_id"));
		b.setCode(rs.getString("_code"));
		b.setSubtotal(rs.getBigDecimal("_subtotal"));
		b.setDiscount(rs.getBigDecimal("_discount"));
		b.setTotal(rs.getBigDecimal("_total"));
		b.setTotalQuantity(rs.getInt("_total_quantity"));
		b.setNote(rs.getString("_note"));
		b.setPaymentMethod(rs.getString("_payment_method"));
		b.setVAT(rs.getBigDecimal("_VAT"));
		b.setIdCreator(rs.getString("_id_creator"));
		b.setIdUpdater(rs.getString("_id_updater"));
		b.setCreatedDate(rs.getDate("_created_date"));
		b.setUpdatedDate(rs.getDate("_updated_date"));
		b.setIdCustomer(rs.getString("_id_customer"));
		// FIX: _id_coupon có thể NULL
		Object couponObj = rs.getObject("_id_coupon");
		b.setIdCoupon(couponObj != null ? (Integer) couponObj : null);
		b.setIdStatus(rs.getInt("_id_status"));
		return b;
	}

	public BigDecimal getRevenueByMonth(int year, Integer month) {
		BigDecimal revenue = BigDecimal.ZERO;

		StringBuilder sql = new StringBuilder(
				"SELECT SUM(_total) FROM tbl_bill " + "WHERE _id_status = 9 AND YEAR(_created_date) = ?");

		if (month != null) {
			sql.append(" AND MONTH(_created_date) = ?");
		}

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql.toString())) {
			ps.setInt(1, year);
			if (month != null) {
				ps.setInt(2, month);
			}

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				revenue = rs.getBigDecimal(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}

		return revenue != null ? revenue : BigDecimal.ZERO;
	}

	public int countBill(int year, Integer month) {

		int total = 0;

		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) FROM tbl_bill WHERE _id_status = 9 AND YEAR(_created_date) = ?");

		if (month != null) {
			sql.append(" AND MONTH(_created_date) = ?");
		}

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql.toString())) {

			ps.setInt(1, year);
			if (month != null) {
				ps.setInt(2, month);
			}

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}

		return total;
	}

}