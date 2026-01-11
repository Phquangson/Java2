package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import entities.Staff;

public class StaffModel {

	public Staff findByUsername(String username) {
		Staff staff = null;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection()
					.prepareStatement("select * from tbl_staff WHERE _username = ?");
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				staff = new Staff();
				staff.setId(rs.getString("_id"));
				staff.setFullName(rs.getString("_full_name"));
				staff.setEmail(rs.getString("_email"));
				staff.setGender(rs.getInt("_gender"));
				staff.setPhone(rs.getString("_phone"));
				staff.setDob(rs.getDate("_dob"));
				staff.setLink(rs.getBytes("_link"));
				staff.setUsername(rs.getString("_username"));
				staff.setPassword(rs.getString("_password"));
				staff.setIsActive(rs.getInt("_is_active"));
				staff.setIdCreator(rs.getString("_id_creator"));
				staff.setIdUpdater(rs.getString("_id_updater"));
				staff.setCreatedDate(rs.getDate("_created_date"));
				staff.setUpdatedDate(rs.getDate("_updated_date"));
				staff.setIdPosition(rs.getInt("_id_position"));
				staff.setMustChangePassword(rs.getInt("_must_change_password"));

			}

		} catch (Exception e) {
			e.printStackTrace();
			staff = null;
		} finally {
			ConnectDB.disconnect();
		}
		return staff;
	}

	public Staff findById(String id) {
		Staff staff = null;
		String sql = "SELECT * FROM tbl_staff WHERE _id = ?";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					staff = mapResultSetToStaff(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return staff;
	}

	public boolean loginStaff(String username, String password) {
		Staff staff = findByUsername(username);
		if (staff != null) {
			return BCrypt.checkpw(password, staff.getPassword());
		}
		return false;
	}

	public boolean updatePassWarehouseManager(Staff staff) {
		boolean result = false;

		try {
			PreparedStatement preparedStatement = ConnectDB.connection()
					.prepareStatement("update tbl_staff set _password = ? where _id = ?");

			preparedStatement.setString(1, staff.getPassword());
			preparedStatement.setString(2, staff.getId());

			result = preparedStatement.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}

		return result;
	}

	private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
		Staff staff = new Staff();
		staff.setId(rs.getString("_id"));
		staff.setFullName(rs.getString("_full_name"));
		staff.setEmail(rs.getString("_email"));
		staff.setGender(rs.getInt("_gender"));
		staff.setPhone(rs.getString("_phone"));
		staff.setDob(rs.getDate("_dob"));
		staff.setLink(rs.getBytes("_link"));
		staff.setUsername(rs.getString("_username"));
		staff.setPassword(rs.getString("_password"));
		staff.setIsActive(rs.getInt("_is_active"));
		staff.setIdCreator(rs.getString("_id_creator"));
		staff.setIdUpdater(rs.getString("_id_updater"));
		staff.setCreatedDate(rs.getDate("_created_date"));
		staff.setUpdatedDate(rs.getDate("_updated_date"));
		staff.setIdPosition(rs.getInt("_id_position"));
		staff.setMustChangePassword(rs.getInt("_must_change_password"));
		return staff;
	}

	public List<Staff> findAll() {
		List<Staff> list = new ArrayList<Staff>();
		String sql = "SELECT * FROM tbl_staff WHERE _is_active = 1 ORDER BY _full_name";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Staff staff = mapResultSetToStaff(rs);
				list.add(staff);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return list;
	}

	public static class LoginResult {
		private final boolean success;
		private final boolean mustChangePassword;
		private final Staff staff;

		public LoginResult(boolean success, boolean mustChangePassword, Staff staff) {
			this.success = success;
			this.mustChangePassword = mustChangePassword;
			this.staff = staff;
		}

		public boolean isSuccess() {
			return success;
		}

		public boolean isMustChangePassword() {
			return mustChangePassword;
		}

		public Staff getStaff() {
			return staff;
		}
	}

	public LoginResult login(String username, String password) {
		Staff staff = findByUsername(username);

		if (staff == null) {
			return new LoginResult(false, false, null);
		}

		if (staff.getIsActive() != 1) {
			return new LoginResult(false, false, null);
		}

		if (!BCrypt.checkpw(password.trim(), staff.getPassword())) {
			return new LoginResult(false, false, null);
		}

		boolean mustChangePassword = staff.getMustChangePassword() == 1;

		return new LoginResult(true, mustChangePassword, staff);
	}

	public boolean confirmPasswordChanged(String staffId) {
		String sql = "UPDATE tbl_staff SET _must_change_password = 0, _updated_date = CURRENT_TIMESTAMP WHERE _id = ?";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, staffId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public boolean updatePassword(String staffId, String newHashedPassword) {
		String sql = "UPDATE tbl_staff SET _password = ?, _updated_date = CURRENT_TIMESTAMP WHERE _id = ?";
		boolean result = false;

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, newHashedPassword);
			ps.setString(2, staffId);
			result = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return result;
	}

	// ==================== CREATE STAFF - ĐÃ SỬA ĐỂ KHÔNG BỊ NULL LINK
	// ====================
	public boolean createStaff(Staff staff) {
		String sql = "INSERT INTO tbl_staff (" + "_id, _full_name, _email, _gender, _phone, _dob, _link, "
				+ "_username, _password, _is_active, _must_change_password, "
				+ "_id_creator, _id_updater, _created_date, _updated_date, _id_position"
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1, 0, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, staff.getId());
			ps.setString(2, staff.getFullName());
			ps.setString(3, staff.getEmail());
			ps.setInt(4, staff.getGender());
			ps.setString(5, staff.getPhone());
			ps.setDate(6, staff.getDob() != null ? new java.sql.Date(staff.getDob().getTime()) : null);

			// SỬA LỖI NULL CHO _link
			byte[] link = staff.getLink();
			if (link == null || link.length == 0) {
				ps.setBytes(7, new byte[0]); // Mảng rỗng thay vì null
			} else {
				ps.setBytes(7, link);
			}

			ps.setString(8, staff.getUsername());

			// Hash password
			String hashedPassword = BCrypt.hashpw(staff.getPassword(), BCrypt.gensalt());
			ps.setString(9, hashedPassword);

			ps.setString(10, staff.getIdCreator());
			ps.setString(11, staff.getIdUpdater());
			ps.setInt(12, staff.getIdPosition());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	// ==================== UPDATE STAFF - CŨNG XỬ LÝ ẢNH AN TOÀN
	// ====================
	public boolean updateStaff(Staff staff) {
		StringBuilder sql = new StringBuilder("UPDATE tbl_staff SET ");
		sql.append("_full_name = ?, _email = ?, _gender = ?, _phone = ?, _dob = ?, ");
		sql.append("_link = ?, _username = ?, _is_active = ?, ");
		sql.append("_id_updater = ?, _updated_date = CURRENT_TIMESTAMP, _id_position = ?");

		if (staff.getPassword() != null && !staff.getPassword().isEmpty()) {
			sql.append(", _password = ?");
		}

		sql.append(" WHERE _id = ?");

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql.toString())) {
			int index = 1;
			ps.setString(index++, staff.getFullName());
			ps.setString(index++, staff.getEmail());
			ps.setInt(index++, staff.getGender());
			ps.setString(index++, staff.getPhone());
			ps.setDate(index++, staff.getDob() != null ? new java.sql.Date(staff.getDob().getTime()) : null);

			// Xử lý ảnh khi update
			byte[] link = staff.getLink();
			if (link == null || link.length == 0) {
				ps.setBytes(index++, new byte[0]);
			} else {
				ps.setBytes(index++, link);
			}

			ps.setString(index++, staff.getUsername());
			ps.setInt(index++, staff.getIsActive());
			ps.setString(index++, staff.getIdUpdater());
			ps.setInt(index++, staff.getIdPosition());

			if (staff.getPassword() != null && !staff.getPassword().isEmpty()) {
				ps.setString(index++, staff.getPassword()); // Đã hash sẵn
			}

			ps.setString(index++, staff.getId());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public boolean deleteStaff(String staffId) {
		String sql = "DELETE FROM tbl_staff WHERE _id = ?";

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, staffId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectDB.disconnect();
		}
	}

	public int countStaff() {
		String sql = "SELECT COUNT(*) FROM tbl_staff WHERE _is_active = 1";
		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return 0;
	}

	public boolean isEmailExists(String email, String ignoreId) {
		String sql = "SELECT COUNT(*) FROM tbl_staff WHERE _email = ?";
		if (ignoreId != null) {
			sql += " AND _id <> ?";
		}

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, email);
			if (ignoreId != null) {
				ps.setString(2, ignoreId);
			}

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return false;
	}

	public boolean isPhoneExists(String phone, String ignoreId) {
		String sql = "SELECT COUNT(*) FROM tbl_staff WHERE _phone = ?";
		if (ignoreId != null) {
			sql += " AND _id <> ?";
		}

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, phone);
			if (ignoreId != null) {
				ps.setString(2, ignoreId);
			}

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return false;
	}

	public boolean isUsernameExists(String username, String ignoreId) {
		String sql = "SELECT COUNT(*) FROM tbl_staff WHERE _username = ?";
		if (ignoreId != null) {
			sql += " AND _id <> ?";
		}

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, username);
			if (ignoreId != null) {
				ps.setString(2, ignoreId);
			}

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return false;
	}

	// ==================== MỚI THÊM: KIỂM TRA TRÙNG FULL NAME ====================
	public boolean isFullNameExists(String fullName, String excludeId) {
		String sql = "SELECT COUNT(*) FROM tbl_staff WHERE _full_name = ?";
		if (excludeId != null) {
			sql += " AND _id <> ?";
		}

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, fullName);
			if (excludeId != null) {
				ps.setString(2, excludeId);
			}

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return false;
	}
	// ============================================================================

	public class PositionModel {
		public String findTitleById(int id) {
			String title = "";
			String sql = "SELECT _title FROM tbl_position WHERE _id = ?";
			try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					title = rs.getString("_title");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				ConnectDB.disconnect();
			}
			return title;
		}
	}

	public boolean updateAvatar(String staffId, byte[] newAvatar) {
		String sql = "UPDATE tbl_staff SET _link = ?, _updated_date = CURRENT_TIMESTAMP WHERE _id = ?";
		boolean result = false;

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			if (newAvatar == null || newAvatar.length == 0) {
				ps.setBytes(1, new byte[0]);
			} else {
				ps.setBytes(1, newAvatar);
			}
			ps.setString(2, staffId);
			result = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return result;
	}

	public boolean updateBasicInfo(Staff staff) {
		String sql = "UPDATE tbl_staff SET _full_name = ?, _email = ?, _gender = ?, _phone = ?, _dob = ?, "
				+ "_updated_date = CURRENT_TIMESTAMP WHERE _id = ?";
		boolean result = false;

		try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
			ps.setString(1, staff.getFullName());
			ps.setString(2, staff.getEmail());
			ps.setInt(3, staff.getGender());
			ps.setString(4, staff.getPhone());
			if (staff.getDob() != null) {
				ps.setDate(5, new java.sql.Date(staff.getDob().getTime()));
			} else {
				ps.setDate(5, null);
			}
			ps.setString(6, staff.getId());

			result = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectDB.disconnect();
		}
		return result;
	}
}