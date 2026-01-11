package entities;

import java.math.BigDecimal;
import java.util.Date;

public class BillItem {

	private int Id;
	private int Quantity;
	private BigDecimal Price;
	private BigDecimal Total;
	private String IdBill;
	private String IdProduct;
	private String IdCreator;
	private String IdUpdater;
	private Date CreatedDate;
	private Date UpdatedDate;
	private String ProductName;
	private int Status;
	public BillItem() {
		super();
	}

	public BillItem(int id, int quantity, BigDecimal price, BigDecimal total, String idBill, String idProduct,
			String idCreator, String idUpdater, Date createdDate, Date updatedDate, String productName, int status) {
		super();
		Id = id;
		Quantity = quantity;
		Price = price;
		Total = total;
		IdBill = idBill;
		IdProduct = idProduct;
		IdCreator = idCreator;
		IdUpdater = idUpdater;
		CreatedDate = createdDate;
		UpdatedDate = updatedDate;
		Status = status;
		ProductName = productName;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}
	
	
	
	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getQuantity() {
		return Quantity;
	}

	public void setQuantity(int quantity) {
		Quantity = quantity;
	}

	public BigDecimal getPrice() {
		return Price;
	}

	public void setPrice(BigDecimal price) {
		Price = price;
	}

	public BigDecimal getTotal() {
		return Total;
	}

	public void setTotal(BigDecimal total) {
		Total = total;
	}

	public String getIdBill() {
		return IdBill;
	}

	public void setIdBill(String idBill) {
		IdBill = idBill;
	}

	public String getIdProduct() {
		return IdProduct;
	}

	public void setIdProduct(String idProduct) {
		IdProduct = idProduct;
	}

	public String getIdCreator() {
		return IdCreator;
	}

	public void setIdCreator(String idCreator) {
		IdCreator = idCreator;
	}

	public String getIdUpdater() {
		return IdUpdater;
	}

	public void setIdUpdater(String idUpdater) {
		IdUpdater = idUpdater;
	}

	public Date getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(Date createdDate) {
		CreatedDate = createdDate;
	}

	public Date getUpdatedDate() {
		return UpdatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		UpdatedDate = updatedDate;
	}

}