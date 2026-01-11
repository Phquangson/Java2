package models;

import entities.Category;
import entities.Type;
import java.sql.*;
import java.util.*;

public class TypeModel {

	public List<Type> findAll() {
		List<Type> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_type order by _created_date desc";

		try (Connection c = ConnectDB.connection();
				PreparedStatement ps = c.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Type t = new Type();
				t.setId(rs.getInt("_id"));
				t.setTitle(rs.getString("_title"));
				list.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean insert(String title) {
		String sql = """
				    INSERT INTO tbl_type
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
		String sql = """
				    UPDATE tbl_type
				    SET _title=?, _updated_date=NOW()
				    WHERE _id=?
				""";

		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {

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
				PreparedStatement ps = c.prepareStatement("DELETE FROM tbl_type WHERE _id=?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Type findById(int idType) {
		String sql = "SELECT * FROM tbl_type WHERE _id = ?";
		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, idType);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Type type = new Type();
					type.setId(rs.getInt("_id"));
					type.setTitle(rs.getString("_title"));
					return type;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
