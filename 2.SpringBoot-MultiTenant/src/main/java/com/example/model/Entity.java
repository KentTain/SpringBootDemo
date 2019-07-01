package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

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
	private static final long serialVersionUID = 3862416351900991824L;

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

		if (CreatedBy != null ? !CreatedBy.equals(entity.CreatedBy) : entity.CreatedBy != null)
			return false;
		if (CreatedDate != null ? !CreatedDate.equals(entity.CreatedDate) : entity.CreatedDate != null)
			return false;
		if (ModifiedBy != null ? !ModifiedBy.equals(entity.ModifiedBy) : entity.ModifiedBy != null)
			return false;
		if (ModifiedDate != null ? !ModifiedDate.equals(entity.ModifiedDate) : entity.ModifiedDate != null)
			return false;
		if (IsDeleted && !entity.IsDeleted)
			return false;
		if (!IsDeleted && entity.IsDeleted)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (CreatedBy != null ? CreatedBy.hashCode() : 0 );
		result = 31 * result + (CreatedDate != null ? CreatedDate.hashCode() : 0 );
		result = 31 * result + (ModifiedBy != null ? ModifiedBy.hashCode() : 0 );
		result = 31 * result + (ModifiedDate != null ? ModifiedDate.hashCode() : 0 );
		result = 31 * result + new Boolean(IsDeleted).hashCode();
		return result;
	}
}
