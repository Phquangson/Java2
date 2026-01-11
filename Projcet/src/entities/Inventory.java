package entities;

import java.math.BigDecimal;
import java.util.Date;

public class Inventory {
	private int Id;
	private int OnHold;
	private int Sold;
	private BigDecimal MinStock;
	private BigDecimal Stock;
	private String IdProduct;
	private String IdCreator;
	private String IdUpdater;
	private Date CreatedDate;
	private Date UpdatedDate;

	private Product product;
	private BigDecimal TotalPrice;
	private int statusId;

	public Inventory() {
		super();
	}

	public Inventory(int id, int onHold, int sold, BigDecimal minStock, BigDecimal stock, String idProduct,
			String idCreator, String idUpdater, Date createdDate, Date updatedDate, Product product,
			BigDecimal totalPrice) {
		super();
		Id = id;
		OnHold = onHold;
		Sold = sold;
		MinStock = minStock;
		Stock = stock;
		IdProduct = idProduct;
		IdCreator = idCreator;
		IdUpdater = idUpdater;
		CreatedDate = createdDate;
		UpdatedDate = updatedDate;
		this.product = product;
		TotalPrice = totalPrice;
	}

	// Getters & Setters

	public int getId() {
		return Id;
	}

	public BigDecimal getTotalPrice() {
		return TotalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		TotalPrice = totalPrice;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getOnHold() {
		return OnHold;
	}

	public void setOnHold(int onHold) {
		OnHold = onHold;
	}

	public int getSold() {
		return Sold;
	}

	public void setSold(int sold) {
		Sold = sold;
	}

	public BigDecimal getMinStock() {
		return MinStock;
	}

	public void setMinStock(BigDecimal minStock) {
		MinStock = minStock;
	}

	public BigDecimal getStock() {
		return Stock;
	}

	public void setStock(BigDecimal stock) {
		Stock = stock;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String toString() {
		return this.getProduct().getTitle();
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
}