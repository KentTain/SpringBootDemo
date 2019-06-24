package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@AttributeOverride(name="created_by",column=@Column(name="CreatedBy"))
@AttributeOverride(name="created_date",column=@Column(name="CreatedDate"))
@AttributeOverride(name="modified_by",column=@Column(name="ModifiedBy"))
@AttributeOverride(name="modified_date",column=@Column(name="ModifiedDate"))
@AttributeOverride(name="is_deleted",column=@Column(name="IsDeleted"))
public abstract class Entity extends EntityBase implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Column(name = "isdeleted")
	private boolean IsDeleted;
	public boolean getIsDeleted() {
		return IsDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.IsDeleted = isDeleted;
	}

	@Column(name = "createdby")
	private String CreatedBy;
	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		this.CreatedBy = createdBy;
	}

	@Column(name = "createddate")
	private Date CreatedDate;
	public Date getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.CreatedDate = createdDate;
	}

	@Column(name = "modifiedby")
	private String ModifiedBy;

	public String getModifiedBy() {
		return ModifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.ModifiedBy = modifiedBy;
	}

	@Column(name = "modifieddate")
	private Date ModifiedDate;

	public Date getModifiedDate() {
		return ModifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.ModifiedDate = modifiedDate;
	}
}
