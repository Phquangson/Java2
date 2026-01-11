package entities;

import java.math.BigDecimal;
import java.util.Date;

public class InventoryActivity {

	private int Id;
	private BigDecimal Change;
	private String Description;
	private String Code;
	private BigDecimal TotalCost;
	private String IdProduct;
	private int IdStatus;
	private int IdInventory;
	private Byte[] Link;
	private String IdCreator;
	private String IdUpdater;
	private Date CreatedDate;
	private Date UpdatedDate;
	private BigDecimal UnitPrice;

	private int IdSupplier;
	private String NameSupplier;

	public InventoryActivity() {
		super();
	}

	public InventoryActivity(int id, BigDecimal change, String description, String code, BigDecimal totalCost,
			String idProduct, int idStatus, int idInventory, Byte[] link, String idCreator, String idUpdater,
			Date createdDate, Date updatedDate, BigDecimal unitPrice, int idSupplier, String nameSupplier) {
		super();
		Id = id;
		Change = change;
		Description = description;
		Code = code;
		TotalCost = totalCost;
		IdProduct = idProduct;
		IdStatus = idStatus;
		IdInventory = idInventory;
		Link = link;
		IdCreator = idCreator;
		IdUpdater = idUpdater;
		CreatedDate = createdDate;
		UpdatedDate = updatedDate;
		UnitPrice = unitPrice;
		IdSupplier = idSupplier;
		NameSupplier = nameSupplier;
	}

	public Byte[] getLink() {
		return Link;
	}

	public void setLink(Byte[] link) {
		Link = link;
	}

	public int getIdSupplier() {
		return IdSupplier;
	}

	public void setIdSupplier(int idSupplier) {
		IdSupplier = idSupplier;
	}

	public String getNameSupplier() {
		return NameSupplier;
	}

	public void setNameSupplier(String string) {
		NameSupplier = string;
	}

	public String getIdProduct() {
		return IdProduct;
	}

	public void setIdProduct(String idProduct) {
		IdProduct = idProduct;
	}

	public BigDecimal getUnitPrice() {
		return UnitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		UnitPrice = unitPrice;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public BigDecimal getChange() {
		return Change;
	}

	public void setChange(BigDecimal change) {
		Change = change;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public BigDecimal getTotalCost() {
		return TotalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		TotalCost = totalCost;
	}

	public int getIdStatus() {
		return IdStatus;
	}

	public void setIdStatus(int idStatus) {
		IdStatus = idStatus;
	}

	public int getIdInventory() {
		return IdInventory;
	}

	public void setIdInventory(int idInventory) {
		IdInventory = idInventory;
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
