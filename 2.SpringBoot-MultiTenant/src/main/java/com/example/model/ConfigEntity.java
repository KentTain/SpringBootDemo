package com.example.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="cfg_ConfigEntity")
public class ConfigEntity extends com.example.model.Entity implements java.io.Serializable{

	private static final long serialVersionUID = -4052400408764598999L;

	/**
	 * 配置Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ConfigId", unique = true, nullable = false)
	private int ConfigId;

	/**
	 * 配置类型：KC.Enums.Core.ConfigType
	 */
	@Column(name = "ConfigType")
	private ConfigType ConfigType;

	/**
	 * 配置标记
	 */
	@Column(name = "ConfigSign")
	private int ConfigSign;

	/**
	 * 配置名称
	 */
	@Column(name = "ConfigName")
	private String ConfigName;

	/**
	 * 配置描述
	 */
	@Column(name = "ConfigDescription")
	private String ConfigDescription;

	/**
	 * 配置生成的XML
	 */
	@Column(name = "ConfigXml")
	private String ConfigXml;

	/**
	 * 配置图片链接
	 */
	@Column(name = "ConfigImgUrl")
	private String ConfigImgUrl;

	/**
	 * 配置状态：KC.Enums.Core.ConfigStatus
	 */
	@Column(name = "State")
	private ConfigStatus State;
	/**
	 * 配置代码
	 */
	@Column(name = "ConfigCode")
	private String ConfigCode;

	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="PropertyAttributeId")
	private List<ConfigAttribute> ConfigAttributes = new ArrayList<ConfigAttribute>();
}
