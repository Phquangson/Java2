package models;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entities.BillItem;

public class BillItemModel {

    public static List<BillItem> findAll() {
        List<BillItem> list = new ArrayList<>();
        String sql = """
            SELECT bi._id, bi._id_bill, bi._id_product, bi._quantity,
                   bi._price, bi._total,
                   p._title AS product_name,
                   b._created_date,
                   b._id_status AS status
            FROM tbl_bill_item bi
            JOIN tbl_product p ON bi._id_product = p._id
            JOIN tbl_bill b ON bi._id_bill = b._id
            ORDER BY b._created_date DESC
            """;
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapToBillItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return list;
    }

    public BillItem findById(int id) {
        BillItem item = null;
        String sql = "SELECT * FROM tbl_bill_item WHERE _id = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item = mapToBillItem(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return item;
    }

    public List<BillItem> findByBillId(String billId) {
        List<BillItem> list = new ArrayList<>();
        String sql = "SELECT bi.*, p._title AS product_name FROM tbl_bill_item bi "
                   + "JOIN tbl_product p ON bi._id_product = p._id "
                   + "WHERE bi._id_bill = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToBillItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return list;
    }

    public List<BillItem> findByKeyword(String keyword) {
        List<BillItem> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String sql = """
            SELECT bi.*, p._title AS product_name
            FROM tbl_bill_item bi
            JOIN tbl_product p ON bi._id_product = p._id
            WHERE bi._id_bill LIKE ? OR bi._id_product LIKE ?
            """;
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            String searchPattern = "%" + keyword.trim() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToBillItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return list;
    }

    public boolean create(BillItem item) {
        String sql = "INSERT INTO tbl_bill_item (_quantity, _price, _total, _id_bill, _id_product, _id_creator, _id_updater) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setInt(1, item.getQuantity());
            ps.setBigDecimal(2, item.getPrice());
            ps.setBigDecimal(3, item.getTotal());
            ps.setString(4, item.getIdBill());
            ps.setString(5, item.getIdProduct());
            ps.setString(6, item.getIdCreator());
            ps.setString(7, item.getIdUpdater());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectDB.disconnect();
        }
    }

    public boolean update(BillItem item) {
        String sql = "UPDATE tbl_bill_item SET _quantity = ?, _price = ?, _total = ?, _id_updater = ?, _updated_date = CURRENT_TIMESTAMP "
                   + "WHERE _id = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setInt(1, item.getQuantity());
            ps.setBigDecimal(2, item.getPrice());
            ps.setBigDecimal(3, item.getTotal());
            ps.setString(4, item.getIdUpdater());
            ps.setInt(5, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectDB.disconnect();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM tbl_bill_item WHERE _id = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectDB.disconnect();
        }
    }

    // SỬA CHÍNH: mapToBillItem chỉ dùng các cột CHẮC CHẮN CÓ trong mọi query
    private static BillItem mapToBillItem(ResultSet rs) throws SQLException {
        BillItem bi = new BillItem();
        bi.setId(rs.getInt("_id"));
        bi.setIdBill(rs.getString("_id_bill"));
        bi.setIdProduct(rs.getString("_id_product"));
        bi.setQuantity(rs.getInt("_quantity"));
        
        // Luôn lấy từ cột gốc trong tbl_bill_item
        bi.setPrice(rs.getBigDecimal("_price"));
        bi.setTotal(rs.getBigDecimal("_total"));
        
        // Các trường bổ sung (nếu có trong query JOIN)
        if (hasColumn(rs, "product_name")) {
            bi.setProductName(rs.getString("product_name"));
        }
        if (hasColumn(rs, "_created_date")) {
            bi.setCreatedDate(rs.getTimestamp("_created_date"));
        }
        if (hasColumn(rs, "status")) {
            bi.setStatus(rs.getInt("status"));
        }
        
        return bi;
    }

    // Hàm hỗ trợ kiểm tra cột tồn tại (an toàn khi query khác nhau)
    private static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}