package com.example.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@AttributeOverride(name="propertyattribute_id",column=@Column(name="PropertyAttributeId"))
public abstract class PropertyAttributeBase extends Entity implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -299509474244820226L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PropertyAttributeId", unique = true, nullable = false)
	private int PropertyAttributeId;
	
	@Column(name = "DataType")
	private AttributeDataType DataType;
	
	@Column(name = "Name")
	private String Name;
	
	@Column(name = "Value")
	private String Value;
	
	@Column(name = "Value1")
	private String Value1;
	
	@Column(name = "Value2")
	private String Value2;
	
	@Column(name = "CanEdit")
	private boolean CanEdit;
	
	@Column(name = "IsProviderAttr")
	private boolean IsProviderAttr;
	
	@Column(name = "Index")
	private int Index;
}
