package com.example.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class Entity extends EntityBase {
	@Column(name = "IsDeleted")
	private boolean IsDeleted;
	@Column(name = "CreatedBy")
	private String CreatedBy;
	@Column(name = "CreatedDate")
	private Date CreatedDate;
	@Column(name = "ModifiedBy")
	private String ModifiedBy;
	@Column(name = "ModifiedDate")
	private Date ModifiedDate;
}
