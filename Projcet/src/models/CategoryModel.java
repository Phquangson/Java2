package models;

import entities.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryModel {

	public List<Category> findAll() {
		List<Category> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_category order by _created_date desc ";

		try (Connection c = ConnectDB.connection();
				PreparedStatement ps = c.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Category cat = new Category();
				cat.setId(rs.getInt("_id"));
				cat.setTitle(rs.getString("_title"));
				cat.setIdCreator(rs.getString("_id_creator"));
				cat.setIdUpdater(rs.getString("_id_updater"));
				cat.setCreatedDate(rs.getTimestamp("_created_date"));
				cat.setUpdatedDate(rs.getTimestamp("_updated_date"));
				list.add(cat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean insert(String title) {
		String sql = """
				    INSERT INTO tbl_category
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
				    UPDATE tbl_category
				    SET _title=?, _id_updater=UUID(), _updated_date=NOW()
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
				PreparedStatement ps = c.prepareStatement("DELETE FROM tbl_category WHERE _id=?")) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public char[] countCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	public static Category findById(int idCategory) {
		String sql = "SELECT * FROM tbl_category WHERE _id = ?";
		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, idCategory);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Category category = new Category();
					category.setId(rs.getInt("_id"));
					category.setTitle(rs.getString("_title"));
					return category;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Category findByTitle(String title) {
		String sql = "SELECT * FROM tbl_category WHERE _title = ?";
		try (Connection c = ConnectDB.connection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, title);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Category category = new Category();
					category.setId(rs.getInt("_id"));
					category.setTitle(rs.getString("_title"));
					return category;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
