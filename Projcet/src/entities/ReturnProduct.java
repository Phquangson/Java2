package entities;

import java.math.BigDecimal;
import java.util.Date;

public class ReturnProduct {
    private int id;
    private int quantity;
    private String reason;
    private String idProduct;
    private String idBill;
    private String idCreator;
    private String idUpdater;
    private Date createdDate;
    private Date updatedDate;
    private String productName;
    private BigDecimal RefundAmount;
    private String billCode;
    
    

    public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	// Constructors
    public BigDecimal getRefundAmount() { return RefundAmount; } 
    public void setRefundAmount(BigDecimal refundAmount) { this.RefundAmount = refundAmount; }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getIdProduct() { return idProduct; }
    public void setIdProduct(String idProduct) { this.idProduct = idProduct; }

    public String getIdBill() { return idBill; }
    public void setIdBill(String idBill) { this.idBill = idBill; }

    public String getIdCreator() { return idCreator; }
    public void setIdCreator(String idCreator) { this.idCreator = idCreator; }

    public String getIdUpdater() { return idUpdater; }
    public void setIdUpdater(String idUpdater) { this.idUpdater = idUpdater; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
}