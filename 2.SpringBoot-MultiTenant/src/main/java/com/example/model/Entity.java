package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
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

	@Column(name = "createdby")
	private String CreatedBy;

	@Column(name = "createddate")
	private Date CreatedDate;

	@Column(name = "modifiedby")
	private String ModifiedBy;

	@Column(name = "modifieddate")
	private Date ModifiedDate;

}
