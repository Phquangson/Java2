package entities;

import java.util.Date;

public class Staff {

	private String Id;
	private String FullName;
	private String Email;
	private int Gender;
	private String Phone;
	private Date Dob;
	private byte[] Link;
	private String Username;
	private String Password;
	private int IsActive;
	private String IdCreator;
	private String IdUpdater;
	private Date CreatedDate;
	private Date UpdatedDate;
	private int IdPosition;
	private int MustChangePassword;
	public Staff(String id, String fullName, String email, int gender, String phone, Date dob, byte[] link,
			String username, String password, int isActive, String idCreator, String idUpdater, Date createdDate,
			Date updatedDate, int idPosition,int mustChangePassword) {
		super();
		Id = id;
		FullName = fullName;
		Email = email;
		Gender = gender;
		Phone = phone;
		Dob = dob;
		Link = link;
		Username = username;
		Password = password;
		IsActive = isActive;
		IdCreator = idCreator;
		IdUpdater = idUpdater;
		CreatedDate = createdDate;
		UpdatedDate = updatedDate;
		IdPosition = idPosition;
		MustChangePassword = mustChangePassword;
	}
	
	public Staff() {
		super();
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

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public int getGender() {
		return Gender;
	}

	public void setGender(int gender) {
		Gender = gender;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public Date getDob() {
		return Dob;
	}

	public void setDob(Date dob) {
		Dob = dob;
	}

	public byte[] getLink() {
		return Link;
	}

	public void setLink(byte[] link) {
		Link = link;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public int getIsActive() {
		return IsActive;
	}

	public void setIsActive(int isActive) {
		IsActive = isActive;
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

	public int getIdPosition() {
		return IdPosition;
	}

	public void setIdPosition(int idPosition) {
		IdPosition = idPosition;
	}
	public int getMustChangePassword() {
	    return MustChangePassword;
	}
	public void setMustChangePassword(int mustChangePassword) {
	    MustChangePassword = mustChangePassword;
	}
	
}
