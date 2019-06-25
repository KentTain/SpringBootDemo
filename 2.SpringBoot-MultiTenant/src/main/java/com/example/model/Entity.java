package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.example.StringExtensions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@AttributeOverride(name = "created_by", column = @Column(name = "CreatedBy"))
@AttributeOverride(name = "created_date", column = @Column(name = "CreatedDate"))
@AttributeOverride(name = "modified_by", column = @Column(name = "ModifiedBy"))
@AttributeOverride(name = "modified_date", column = @Column(name = "ModifiedDate"))
@AttributeOverride(name = "is_deleted", column = @Column(name = "IsDeleted"))
public abstract class Entity extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "isdeleted")
	private boolean IsDeleted;

	@Column(name = "createdby")
	private String CreatedBy;

	@Column(name = "createddate")
	private Date CreatedDate;

	@Column(name = "modifiedby")
	private String ModifiedBy;

	@Column(name = "modifieddate")
	private Date ModifiedDate;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Entity))
			return false;
		if (!super.equals(o))
			return false;

		Entity entity = (Entity) o;

		if (!getCreatedBy().equals(entity.getCreatedBy()))
			return false;
		if (!getCreatedDate().equals(entity.getCreatedDate()))
			return false;
		if (!getModifiedBy().equals(entity.getModifiedBy()))
			return false;
		if (!getModifiedDate().equals(entity.getModifiedDate()))
			return false;
		if (isIsDeleted() && !entity.isIsDeleted())
			return false;
		if (!isIsDeleted() && entity.isIsDeleted())
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		if (!StringExtensions.isNullOrEmpty(getCreatedBy()))
			result = 31 * result + getCreatedBy().hashCode();
		result = 31 * result + getCreatedDate().hashCode();
		if (!StringExtensions.isNullOrEmpty(getModifiedBy()))
			result = 31 * result + getModifiedBy().hashCode();
		result = 31 * result + getModifiedDate().hashCode();
		result = 31 * result + new Boolean(isIsDeleted()).hashCode();
		return result;
	}
}
