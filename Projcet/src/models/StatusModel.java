package models;

import entities.Status;
import java.sql.*;
import java.util.*;

public class StatusModel {

	public List<Status> findAll() {
		List<Status> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_status order by _created_date desc";

		try (Connection c = ConnectDB.connection();
				PreparedStatement ps = c.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Status s = new Status();
				s.setId(rs.getInt("_id"));
				s.setTitle(rs.getString("_title"));
				list.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean insert(String title) {
		String sql = """
				    INSERT INTO tbl_status
				    (_title,_id_creator,_id_updater,_created_date,_updated_date)
				    VALUES (?,UUID(),UUID(),NOW(),NOW())
				""";

		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {

			ps.setString(1, title);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(int id, String title) {
		try (Connection c = ConnectDB.connection();
				PreparedStatement ps = c.prepareStatement("UPDATE tbl_status SET _title=? WHERE _id=?")) {

			ps.setString(1, title);
			ps.setInt(2, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(int id) {
		try (Connection c = ConnectDB.connection();
				PreparedStatement ps = c.prepareStatement("DELETE FROM tbl_status WHERE _id=?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Status findById(int idStatus) {
		String sql = "SELECT * FROM tbl_status WHERE _id = ?";
		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, idStatus);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Status s = new Status();
					s.setId(rs.getInt("_id"));
					s.setTitle(rs.getString("_title"));
					return s;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Status findByTitle(String title) {
		String sql = "SELECT * FROM tbl_status WHERE _title = ?";
		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, title);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Status s = new Status();
					s.setId(rs.getInt("_id"));
					s.setTitle(rs.getString("_title"));
					return s;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
