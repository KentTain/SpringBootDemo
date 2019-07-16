package com.example.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
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
@javax.persistence.Entity
@Table(name="cfg_SysSequence")
@Inheritance(strategy=InheritanceType.JOINED)
public class SysSequence extends EntityBase implements java.io.Serializable {

	private static final long serialVersionUID = 3396289557966673057L;

	/**
	 * 序列名称
	 */
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SequencName", unique = true, nullable = false)
	public String SequencName;
	/**
	 * 当前值
	 */
	@Column(name = "CurrentValue")
	public int CurrentValue;
	/**
	 * 初试值
	 */
	@Column(name = "InitValue")
	public int InitValue;
	/**
	 * 最大值
	 */
	@Column(name = "MaxValue")
	public int MaxValue;
	/**
	 * 步长
	 */
	@Column(name = "StepValue")
	public int StepValue;
	/**
	 * 前缀
	 */
	@Column(name = "PreFixString", length = 12)
	public String PreFixString;
	/**
	 * 后缀
	 */
	@Column(name = "PostFixString", length = 12)
	public String PostFixString;
	/**
	 * 描述
	 */
	@Column(name = "Comments", length = 256)
	public String Comments;
	/**
	 * 当前时间
	 */
	@Column(name = "CurrDate")
	public String CurrDate;
}
