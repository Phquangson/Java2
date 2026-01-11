package entities;
//giday
import java.math.BigDecimal;
import java.util.Date;

public class Bill {
	private String Id;
	private String Code;
	private BigDecimal Subtotal; 
	private BigDecimal Discount;
	private BigDecimal Total;
	private int TotalQuantity;
	private String Note;
	private String PaymentMethod;
	private BigDecimal VAT;
	private String IdCreator;
	private String IdUpdater;
	private Date CreatedDate;
	private Date UpdatedDate;
	private String IdCustomer;
	private Integer IdCoupon;
	private int IdStatus;
	
	public Bill(String id, String code, BigDecimal subtotal, BigDecimal discount, BigDecimal total, int totalQuantity,
			String note, String paymentMethod, BigDecimal vAT, String idCreator, String idUpdater, Date createdDate,
			Date updatedDate, String idCustomer, int idCoupon, int idStatus) {
		super();
		Id = id;
		Code = code;
		Subtotal = subtotal;
		Discount = discount;
		Total = total;
		TotalQuantity = totalQuantity;
		Note = note;
		PaymentMethod = paymentMethod;
		VAT = vAT;
		IdCreator = idCreator;
		IdUpdater = idUpdater;
		CreatedDate = createdDate;
		UpdatedDate = updatedDate;
		IdCustomer = idCustomer;
		IdCoupon = idCoupon;
		IdStatus = idStatus;
	}
	
	public Bill() {
		super();

	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public BigDecimal getSubtotal() {
		return Subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		Subtotal = subtotal;
	}

	public BigDecimal getDiscount() {
		return Discount;
	}

	public void setDiscount(BigDecimal discount) {
		Discount = discount;
	}

	public BigDecimal getTotal() {
		return Total;
	}

	public void setTotal(BigDecimal total) {
		Total = total;
	}

	public int getTotalQuantity() {
		return TotalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		TotalQuantity = totalQuantity;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}

	public String getPaymentMethod() {
		return PaymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		PaymentMethod = paymentMethod;
	}

	public BigDecimal getVAT() {
		return VAT;
	}

	public void setVAT(BigDecimal vAT) {
		VAT = vAT;
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

	public String getIdCustomer() {
		return IdCustomer;
	}

	public void setIdCustomer(String idCustomer) {
		IdCustomer = idCustomer;
	}

	public Integer getIdCoupon() {
		return IdCoupon;
	}

	public void setIdCoupon(Integer idCoupon) {
		IdCoupon = idCoupon;
	}

	public int getIdStatus() {
		return IdStatus;
	}

	public void setIdStatus(int idStatus) {
		IdStatus = idStatus;
	}
	
}
