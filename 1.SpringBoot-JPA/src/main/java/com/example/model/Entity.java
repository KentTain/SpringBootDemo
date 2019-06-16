package com.example.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Entity extends EntityBase {

	private boolean IsDeleted;

	private String CreatedBy;

	private Date CreatedDate;

	private String ModifiedBy;

	private Date ModifiedDate;
}
