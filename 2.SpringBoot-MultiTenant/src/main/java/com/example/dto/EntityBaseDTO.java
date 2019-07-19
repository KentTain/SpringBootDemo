package com.example.dto;

import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class EntityBaseDTO implements java.io.Serializable {

	private static final long serialVersionUID = -41103916458918570L;

}
