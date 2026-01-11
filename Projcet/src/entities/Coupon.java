package entities;

import java.math.BigDecimal;
import java.util.Date;

public class Coupon {

    private int Id;
    private String Code;
    private String Title;
    private BigDecimal DiscountValue;
    private BigDecimal MinBillAmount;
    private int Quantity;
    private Date ExpiredDate;
    private String IdCreator;
    private String IdUpdater;
    private Date CreatedDate;
    private Date UpdatedDate;
    private Integer IdType;
    private int IsActive;
    private String TypeTitle;

    public Coupon() {
        super();
    }

    public Coupon(int id, String code, String title, BigDecimal discountValue, BigDecimal minBillAmount, int quantity,
                  Date expiredDate, String idCreator, String idUpdater, Date createdDate, Date updatedDate,
                  Integer idType, int isActive, String typeTitle) {
        super();
        Id = id;
        Code = code;
        Title = title;
        DiscountValue = discountValue;
        MinBillAmount = minBillAmount;
        Quantity = quantity;
        ExpiredDate = expiredDate;
        IdCreator = idCreator;
        IdUpdater = idUpdater;
        CreatedDate = createdDate;
        UpdatedDate = updatedDate;
        IdType = idType;
        IsActive = isActive;
        TypeTitle = typeTitle;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public BigDecimal getDiscountValue() {
        return DiscountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        DiscountValue = discountValue;
    }

    public BigDecimal getMinBillAmount() {
        return MinBillAmount;
    }

    public void setMinBillAmount(BigDecimal minBillAmount) {
        MinBillAmount = minBillAmount;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public Date getExpiredDate() {
        return ExpiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        ExpiredDate = expiredDate;
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

    public Integer getIdType() {
        return IdType;
    }

    public void setIdType(Integer idType) {
        IdType = idType;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    // getter/setter cho TypeTitle
    public String getTypeTitle() {
        return TypeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        TypeTitle = typeTitle;
    }

    @Override
    public String toString() {
        if (Code == null || Title == null) {
            return "Coupon ID: " + Id;
        }
        return Code + " - " + Title + " (Giảm " + DiscountValue + ")"
                + (TypeTitle != null ? " | Loại: " + TypeTitle : "");
    }
}
