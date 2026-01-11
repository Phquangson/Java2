package entities;

import java.util.Date;

public class StaffActivity {
    private int Id;
    private String Action;
    private String Description;
    private String IdCreator;
    private String IdUpdater;
    private Date CreatedDate;
    private Date UpdatedDate;

    public StaffActivity() {
        super();
    }

    public StaffActivity(int id, String action, String description, String idCreator, String idUpdater,
                         Date createdDate, Date updatedDate) {
        super();
        Id = id;
        Action = action;
        Description = description;
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

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
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

    // Getters & Setters
    
}