package com.example.model;

import java.util.Date;

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
public abstract class Entity extends EntityBase {

	private static final long serialVersionUID = -2841097052881018105L;

	private boolean IsDeleted;

	private String CreatedBy;

	private Date CreatedDate;

	private String ModifiedBy;

	private Date ModifiedDate;
}
