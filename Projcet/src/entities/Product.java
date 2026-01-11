package entities;

import java.math.BigDecimal;
import java.util.Date;

public class Product {
    private String Id;
    private String Title;
    private String Description;
    private BigDecimal Price;
    private BigDecimal DiscountPrice;
    private BigDecimal Percent;
    private byte[] Link;
    private String Code;
    private String IdCreator;
    private String IdUpdater;
    private Date CreatedDate;
    private Date UpdatedDate;
    private int IdStatus;
    private int IdType;
    private int IdCategory;
    private int IdSupplier;
    private String SupplierName;   // thêm tên nhà cung cấp
    private int IsPublic;
    private int Quantity;

    public Product(String id, String title, String description, BigDecimal price, BigDecimal discountPrice,
                   BigDecimal percent, byte[] link, String code, String idCreator, String idUpdater, Date createdDate,
                   Date updatedDate, int idStatus, int idType, int idCategory, int idSupplier, String supplierName,
                   int isPublic, int quantity) {
        super();
        Id = id;
        Title = title;
        Description = description;
        Price = price;
        DiscountPrice = discountPrice;
        Percent = percent;
        Link = link;
        Code = code;
        IdCreator = idCreator;
        IdUpdater = idUpdater;
        CreatedDate = createdDate;
        UpdatedDate = updatedDate;
        IdStatus = idStatus;
        IdType = idType;
        IdCategory = idCategory;
        IdSupplier = idSupplier;
        SupplierName = supplierName;
        IsPublic = isPublic;
        Quantity = quantity;
    }

    public Product() {
        super();
    }

    public Product(String id, String title, String description, BigDecimal price, BigDecimal discountPrice,
                   BigDecimal percent, byte[] link, String code, String idCreator, String idUpdater,
                   Date createdDate, Date updatedDate, int idStatus, int idType, int idCategory,
                   int idSupplier, String supplierName, int isPublic) {
        super();
        Id = id;
        Title = title;
        Description = description;
        Price = price;
        DiscountPrice = discountPrice;
        Percent = percent;
        Link = link;
        Code = code;
        IdCreator = idCreator;
        IdUpdater = idUpdater;
        CreatedDate = createdDate;
        UpdatedDate = updatedDate;
        IdStatus = idStatus;
        IdType = idType;
        IdCategory = idCategory;
        IdSupplier = idSupplier;
        SupplierName = supplierName;
        IsPublic = isPublic;
    }

    // Getter & Setter cho SupplierName
    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getIdSupplier() {
        return IdSupplier;
    }

    public void setIdSupplier(int idSupplier) {
        IdSupplier = idSupplier;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public BigDecimal getPrice() {
        return Price;
    }

    public void setPrice(BigDecimal price) {
        Price = price;
    }

    public BigDecimal getDiscountPrice() {
        return DiscountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        DiscountPrice = discountPrice;
    }

    public BigDecimal getPercent() {
        return Percent;
    }

    public void setPercent(BigDecimal percent) {
        Percent = percent;
    }

    public byte[] getLink() {
        return Link;
    }

    public void setLink(byte[] link) {
        Link = link;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
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

    public int getIdStatus() {
        return IdStatus;
    }

    public void setIdStatus(int idStatus) {
        IdStatus = idStatus;
    }

    public int getIdType() {
        return IdType;
    }

    public void setIdType(int idType) {
        IdType = idType;
    }

    public int getIdCategory() {
        return IdCategory;
    }

    public void setIdCategory(int idCategory) {
        IdCategory = idCategory;
    }

    public int getIsPublic() {
        return IsPublic;
    }

    public void setIsPublic(int isPublic) {
        IsPublic = isPublic;
    }

    public void setPercent(int int1) {
        // TODO Auto-generated method stub
    }
}
