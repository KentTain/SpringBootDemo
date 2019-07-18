package com.example.model;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cfg_ConfigAttribute")
//@AttributeOverride(name = "config_id", column = @Column(name = "ConfigId"))
public class ConfigAttribute extends PropertyAttributeBase implements java.io.Serializable {

	private static final long serialVersionUID = -4826723150311364934L;

	@Column(name = "DisplayName")
	private String displayName;

	@Column(name = "Description")
	private String description;

	//@Column(name = "ConfigId")
	//private int ConfigId;

	@Column(name = "IsFileAttr")
	private boolean isFileAttr;

	@ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "ConfigId", nullable = false)
	private ConfigEntity configEntity;
}
