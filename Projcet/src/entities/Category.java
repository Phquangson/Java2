package entities;

import java.util.Date;

public class Category {

	private int Id;
	private int IdGroup;
	private String Title;
	private String IdCreator;
	private String IdUpdater;
	private Date CreatedDate;
	private Date UpdatedDate;

	public Category(int id, int idGroup, String title, String idCreator, String idUpdater, Date createdDate,
			Date updatedDate) {
		super();
		Id = id;
		IdGroup = idGroup;
		Title = title;
		IdCreator = idCreator;
		IdUpdater = idUpdater;
		CreatedDate = createdDate;
		UpdatedDate = updatedDate;
	}

	public Category(int id, String title) {
		this.Id = id;
		this.Title = title;
	}

	public Category() {
		super();
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getIdGroup() {
		return IdGroup;
	}

	public void setIdGroup(int idGroup) {
		IdGroup = idGroup;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
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

	public String toString() {
		return this.getTitle();
	}
}
