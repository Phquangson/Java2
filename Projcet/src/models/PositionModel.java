package models;

import entities.Position;
import entities.Type;

import java.sql.*;
import java.util.*;

public class PositionModel {

	public List<Position> findAll() {
		List<Position> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_position order by _created_date desc";

		try (Connection c = ConnectDB.connection();
				PreparedStatement ps = c.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Position p = new Position();
				p.setId(rs.getInt("_id"));
				p.setTitle(rs.getString("_title"));
				list.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean insert(String title) {
		String sql = """
				    INSERT INTO tbl_position
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
				PreparedStatement ps = c.prepareStatement("UPDATE tbl_position SET _title=? WHERE _id=?")) {

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
				PreparedStatement ps = c.prepareStatement("DELETE FROM tbl_position WHERE _id=?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Position findById(int idPosition) {
		String sql = "SELECT * FROM tbl_position WHERE _id = ?";
		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, idPosition);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Position position = new Position();
					position.setId(rs.getInt("_id"));
					position.setTitle(rs.getString("_title"));
					return position;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
