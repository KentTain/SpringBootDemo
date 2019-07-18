package com.example.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
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
@Table(name="cfg_ConfigEntity")
@NamedEntityGraph(name = "Graph.ConfigEntity.ConfigAttributes", 
	attributeNodes = {@NamedAttributeNode("configAttributes")})
public class ConfigEntity extends com.example.model.Entity implements java.io.Serializable{

	private static final long serialVersionUID = -4052400408764598999L;

	/**
	 * 配置Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ConfigId", unique = true, nullable = false)
	private int configId;

	/**
	 * 配置类型：KC.Enums.Core.ConfigType
	 */
	@Column(name = "ConfigType")
	private ConfigType configType;

	/**
	 * 配置标记
	 */
	@Column(name = "ConfigSign")
	private int configSign;

	/**
	 * 配置名称
	 */
	@Column(name = "ConfigName")
	private String configName;

	/**
	 * 配置描述
	 */
	@Column(name = "ConfigDescription")
	private String configDescription;

	/**
	 * 配置生成的XML
	 */
	@Column(name = "ConfigXml")
	private String configXml;

	/**
	 * 配置图片链接
	 */
	@Column(name = "ConfigImgUrl")
	private String configImgUrl;

	/**
	 * 配置状态：KC.Enums.Core.ConfigStatus
	 */
	@Column(name = "State")
	private ConfigStatus state;
	/**
	 * 配置代码
	 */
	@Column(name = "ConfigCode")
	private String configCode;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "configEntity")
	private Set<ConfigAttribute> configAttributes = new HashSet<ConfigAttribute>();
}
