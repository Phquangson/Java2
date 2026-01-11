package models;

import entities.Bill;
import entities.ReturnProduct;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReturnProductModel {

    public boolean create(ReturnProduct returnProduct) {
        String sql = "INSERT INTO tbl_return_product (_quantity, _reason, _id_product, _id_bill, _id_creator, _id_updater) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setInt(1, returnProduct.getQuantity());
            ps.setString(2, returnProduct.getReason());
            ps.setString(3, returnProduct.getIdProduct());
            ps.setString(4, returnProduct.getIdBill());
            ps.setString(5, returnProduct.getIdCreator());
            ps.setString(6, returnProduct.getIdUpdater());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            ConnectDB.disconnect();
        }
    }
    public ReturnProduct findById(String id) {
        String sql = "SELECT * FROM tbl_return_product WHERE _id = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return null;
    }
    
    public String findBillIdById(String recordId) {
        String sql = "SELECT _id_bill FROM tbl_return_product WHERE _id = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("_id_bill");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return null; 
    }


    public List<ReturnProduct> findByBillId(String billId) {
        List<ReturnProduct> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_return_product WHERE _id_bill = ?";
        try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
            ps.setString(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectDB.disconnect();
        }
        return list;
    }
    
    public List<ReturnProduct> findByFilter(Date from, Date to) {
    List<ReturnProduct> list = new ArrayList<>();

    
    Calendar cal = Calendar.getInstance();
    cal.setTime(to);
    cal.add(Calendar.DATE, 1);
    Date toPlusOne = cal.getTime();

    String sql = "SELECT * FROM tbl_return_product " +
                 "WHERE _created_date >= ? AND _created_date < ? " +
                 "ORDER BY _created_date DESC";

    try (PreparedStatement ps = ConnectDB.connection().prepareStatement(sql)) {
    	// Chuẩn hóa from về đầu ngày (00:00:00)
    	Calendar calFrom = Calendar.getInstance();
    	calFrom.setTime(from);
    	calFrom.set(Calendar.HOUR_OF_DAY, 0);
    	calFrom.set(Calendar.MINUTE, 0);
    	calFrom.set(Calendar.SECOND, 0);
    	calFrom.set(Calendar.MILLISECOND, 0);
    	Date fromStartOfDay = calFrom.getTime();

    	// Chuẩn hóa to về cuối ngày (23:59:59)
    	Calendar calTo = Calendar.getInstance();
    	calTo.setTime(to);
    	calTo.set(Calendar.HOUR_OF_DAY, 23);
    	calTo.set(Calendar.MINUTE, 59);
    	calTo.set(Calendar.SECOND, 59);
    	calTo.set(Calendar.MILLISECOND, 999);
    	Date toEndOfDay = calTo.getTime();

    	// Sau đó set vào SQL
    	ps.setTimestamp(1, new java.sql.Timestamp(fromStartOfDay.getTime()));
    	ps.setTimestamp(2, new java.sql.Timestamp(toEndOfDay.getTime()));


        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReturnProduct rp = new ReturnProduct();
                rp.setId(rs.getInt("_id"));
                rp.setIdBill(rs.getString("_id_bill"));
                rp.setIdProduct(rs.getString("_id_product"));
                rp.setQuantity(rs.getInt("_quantity"));
                rp.setReason(rs.getString("_reason"));
                rp.setIdCreator(rs.getString("_id_creator"));
                rp.setIdUpdater(rs.getString("_id_updater"));
                rp.setCreatedDate(rs.getTimestamp("_created_date"));
                rp.setUpdatedDate(rs.getTimestamp("_updated_date"));

                BillModel billModel = new BillModel();
                Bill bill = billModel.findById(rp.getIdBill());
                if (bill != null) {
                    rp.setBillCode(bill.getCode());
                }

                list.add(rp);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        ConnectDB.disconnect();
    }

    return list;
}




    
    private ReturnProduct map(ResultSet rs) throws SQLException {
        ReturnProduct rp = new ReturnProduct();
        rp.setId(rs.getInt("_id"));
        rp.setQuantity(rs.getInt("_quantity"));
        rp.setReason(rs.getString("_reason"));
        rp.setIdProduct(rs.getString("_id_product"));
        rp.setIdBill(rs.getString("_id_bill"));
        rp.setIdCreator(rs.getString("_id_creator"));
        rp.setIdUpdater(rs.getString("_id_updater"));
        rp.setCreatedDate(rs.getTimestamp("_created_date"));
        rp.setUpdatedDate(rs.getTimestamp("_updated_date"));
        return rp;
    }
}