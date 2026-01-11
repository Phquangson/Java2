package entities;

import java.util.Date;

public class Position {
    private int Id;
    private String Title;
    private String IdCreator;
    private String IdUpdater;
    private Date CreatedDate;
    private Date UpdatedDate;

    public Position() {
        super();
    }

    public Position(int id, String title, String idCreator, String idUpdater,
                    Date createdDate, Date updatedDate) {
        super();
        Id = id;
        Title = title;
        IdCreator = idCreator;
        IdUpdater = idUpdater;
        CreatedDate = createdDate;
        UpdatedDate = updatedDate;
    }

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
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

	@Override
	public String toString() {
	    return Title;
	}

    
}