package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import entities.Supplier;

public class SupplierModel {
	public List<Supplier> findAll() {
		List<Supplier> suppliers = new ArrayList<Supplier>();
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("select * from tbl_supplier order by _created_date desc");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Supplier supplier = new Supplier();
				supplier.setId(resultSet.getInt("_id"));
				supplier.setCode(resultSet.getString("_code"));
				supplier.setName(resultSet.getString("_name"));
				supplier.setPhone(resultSet.getString("_phone"));
				supplier.setAddress(resultSet.getString("_address"));
				supplier.setIdCreator(resultSet.getString("_id_creator"));
				supplier.setIdUpdater(resultSet.getString("_id_updater"));
				supplier.setCreatedDate(resultSet.getDate("_created_date"));
				supplier.setUpdatedDate(resultSet.getDate("_updated_date"));
				suppliers.add(supplier);
			}
		} catch (Exception e) {
			e.printStackTrace();
			suppliers = null;
			// TODO: handle exception
		} finally {
			ConnectDB.disconnect();
		}
		return suppliers;
	}

	public boolean create(Supplier supplier) {
		boolean result = true;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement(
					"INSERT INTO tbl_supplier(_code, _name, _phone, _address, _id_creator, _created_date, _id_updater) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?)");

			preparedStatement.setString(1, supplier.getCode());
			preparedStatement.setString(2, supplier.getName());
			preparedStatement.setString(3, supplier.getPhone());
			preparedStatement.setString(4, supplier.getAddress());
			preparedStatement.setString(5, supplier.getIdCreator());
			preparedStatement.setTimestamp(6, new java.sql.Timestamp(supplier.getCreatedDate().getTime()));
			preparedStatement.setString(7, supplier.getIdUpdater());

			result = preparedStatement.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			ConnectDB.disconnect();
		}
		return result;
	}

	public boolean update(Supplier supplier) {
		boolean result = true;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement(
					"UPDATE tbl_supplier SET _code = ?, _name = ?, _phone = ?, _address = ?, _id_updater = ?, _updated_date = ? WHERE _id = ?");
			preparedStatement.setString(1, supplier.getCode());
			preparedStatement.setString(2, supplier.getName());
			preparedStatement.setString(3, supplier.getPhone());
			preparedStatement.setString(4, supplier.getAddress());
			preparedStatement.setString(5, supplier.getIdUpdater());
			preparedStatement.setTimestamp(6, new java.sql.Timestamp(supplier.getUpdatedDate().getTime()));
			preparedStatement.setInt(7, supplier.getId());
			result = preparedStatement.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			ConnectDB.disconnect();
		}

		return result;
	}

	public boolean delete(int id) {
		boolean result = true;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection()
					.prepareStatement("delete from tbl_supplier where _id = ?");
			preparedStatement.setInt(1, id);
			result = preparedStatement.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			ConnectDB.disconnect();
		}

		return result;
	}

	public List<Supplier> findByNameSupplier(String keyword) {
		List<Supplier> suppliers = new ArrayList<Supplier>();
		try {
			PreparedStatement preparedStatement = ConnectDB.connection()
					.prepareStatement("select * from tbl_supplier where _name like ?");
			preparedStatement.setString(1, "%" + keyword + "%");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Supplier supplier = new Supplier();
				supplier.setCode(resultSet.getString("_code"));
				supplier.setName(resultSet.getString("_name"));
				supplier.setPhone(resultSet.getString("_phone"));
				supplier.setAddress(resultSet.getString("_address"));
				suppliers.add(supplier);
			}
		} catch (Exception e) {
			e.printStackTrace();
			suppliers = null;
			// TODO: handle exception
		} finally {
			ConnectDB.disconnect();
		}

		return suppliers;
	}

	public boolean existsByCode(String code) {
		boolean exists = false;
		try {
			PreparedStatement ps = ConnectDB.connection()
					.prepareStatement("SELECT COUNT(*) FROM tbl_supplier WHERE _code = ?");
			ps.setString(1, code);
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

	public boolean existsByName(String name) {
		boolean exists = false;
		try {
			PreparedStatement ps = ConnectDB.connection()
					.prepareStatement("SELECT COUNT(*) FROM tbl_supplier WHERE _name = ?");
			ps.setString(1, name);
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

	public boolean existsByPhone(String phone) {
		boolean exists = false;
		try {
			PreparedStatement ps = ConnectDB.connection()
					.prepareStatement("SELECT COUNT(*) FROM tbl_supplier WHERE _phone = ?");
			ps.setString(1, phone);
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

	public boolean existsByAddress(String address) {
		boolean exists = false;
		try {
			PreparedStatement ps = ConnectDB.connection()
					.prepareStatement("SELECT COUNT(*) FROM tbl_supplier WHERE _address = ?");
			ps.setString(1, address);
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

}
