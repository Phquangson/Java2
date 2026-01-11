package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entities.Customer;

public class CustomerModel {

    // ===== FIND ALL =====
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_customer";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return list;
    }

    // ===== FIND BY ID =====
    public Customer findById(String id) {
        Customer customer = null;
        String sql = "SELECT * FROM tbl_customer WHERE _id = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return customer;
    }

    // ===== SEARCH (keyword / name / phone / id) =====
    public List<Customer> search(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = """
            SELECT * FROM tbl_customer
            WHERE _id LIKE ?
               OR _full_name LIKE ?
               OR _phone LIKE ?
            """;
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return list;
    }

    // ===== CREATE =====
    public boolean create(Customer c) {
        String sql = """
            INSERT INTO tbl_customer
            (_id, _full_name, _phone, _address, _city, _district,
             _id_creator, _id_updater, _created_date, _updated_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, c.getFullName());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getCity());
            ps.setString(6, c.getDistrict());
            ps.setString(7, c.getIdCreator());
            ps.setString(8, c.getIdUpdater());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectDB.disconnect();
        }
    }

    // ===== UPDATE =====
    public boolean update(Customer c) {
        String sql = """
            UPDATE tbl_customer SET
                _full_name = ?,
                _phone = ?,
                _address = ?,
                _city = ?,
                _district = ?,
                _id_updater = ?,
                _updated_date = CURRENT_TIMESTAMP
            WHERE _id = ?
        """;
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getCity());
            ps.setString(5, c.getDistrict());
            ps.setString(6, c.getIdUpdater());
            ps.setString(7, c.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectDB.disconnect();
        }
    }

    // ===== DELETE =====
    public boolean delete(String id) {
        String sql = "DELETE FROM tbl_customer WHERE _id = ?";
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

    // ===== MAP RESULTSET =====
    private Customer mapResultSet(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getString("_id"));
        c.setFullName(rs.getString("_full_name"));
        c.setPhone(rs.getString("_phone"));
        c.setAddress(rs.getString("_address"));
        c.setCity(rs.getString("_city"));
        c.setDistrict(rs.getString("_district"));
        c.setIdCreator(rs.getString("_id_creator"));
        c.setIdUpdater(rs.getString("_id_updater"));
        c.setCreatedDate(rs.getTimestamp("_created_date"));
        c.setUpdatedDate(rs.getTimestamp("_updated_date"));
        return c;
    }

    // ===== FIND BY PHONE (ĐÃ SỬA LỖI) =====
    public Customer findByPhone(String phone) {
        Customer c = null;
        String sql = "SELECT * FROM tbl_customer WHERE _phone = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = mapResultSet(rs); // Sửa từ map(rs) → mapResultSet(rs)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return c;
    }
}