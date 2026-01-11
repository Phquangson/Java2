package entities;

import java.util.Date;

public class Customer {

	private String Id;
	private String FullName;
	private String Phone;
	private String Address;
	private String City;
	private String District;
	private String IdCreator;
	private String IdUpdater;
	private Date CreatedDate;
	private Date UpdatedDate;

	public Customer() {
		super();
	}

	public Customer(String id, String fullName, String phone, String address, String city, String district,
			 		String idCreator, String idUpdater, Date createdDate, Date updatedDate) {
		super();
		Id = id;
		FullName = fullName;
		Phone = phone;
		Address = address;
		City = city;
		District = district;
		IdCreator = idCreator;
		IdUpdater = idUpdater;
		CreatedDate = createdDate;
		UpdatedDate = updatedDate;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getDistrict() {
		return District;
	}

	public void setDistrict(String district) {
		District = district;
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