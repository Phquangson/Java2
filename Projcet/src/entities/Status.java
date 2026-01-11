package entities;

import java.util.Date;

public class Status {
	private int id;
	private int idGroup;
	private String title;
	private String idCreator;
	private String idUpdater;
	private Date createdDate;
	private Date updatedDate;

	public Status() {
	}

	public Status(int id, int idGroup, String title, String idCreator, String idUpdater, Date createdDate,
			Date updatedDate) {
		this.id = id;
		this.idGroup = idGroup;
		this.title = title;
		this.idCreator = idCreator;
		this.idUpdater = idUpdater;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}

	// Getters & Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(int idGroup) {
		this.idGroup = idGroup;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdCreator() {
		return idCreator;
	}

	public void setIdCreator(String idCreator) {
		this.idCreator = idCreator;
	}

	public String getIdUpdater() {
		return idUpdater;
	}

	public void setIdUpdater(String idUpdater) {
		this.idUpdater = idUpdater;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return (title == null || title.isEmpty()) ? "Status ID: " + id : title;
	}
}
