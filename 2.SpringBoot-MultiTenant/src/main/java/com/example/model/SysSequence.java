package com.example.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
	@Column(name = "SequenceName", unique = true, nullable = false)
	private String sequenceName;
	/**
	 * 当前值
	 */
	@Column(name = "CurrentValue")
	private int currentValue;
	/**
	 * 初试值
	 */
	@Column(name = "InitValue")
	private int initValue;
	/**
	 * 最大值
	 */
	@Column(name = "MaxValue")
	private int maxValue;
	/**
	 * 步长
	 */
	@Column(name = "StepValue")
	private int stepValue;
	/**
	 * 前缀
	 */
	@Column(name = "PreFixString", length = 12)
	private String preFixString;
	/**
	 * 后缀
	 */
	@Column(name = "PostFixString", length = 12)
	private String postFixString;
	/**
	 * 描述
	 */
	@Column(name = "Comments", length = 256)
	private String comments;
	/**
	 * 当前时间
	 */
	@Column(name = "CurrDate")
	private String currDate;
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		if (!super.equals(o))
			return false;

		SysSequence node = (SysSequence) o;

		if (sequenceName != null ? !sequenceName.equals(node.sequenceName) : node.sequenceName != null)
			return false;
		if (preFixString != null ? !preFixString.equals(node.preFixString) : node.preFixString != null)
			return false;
		if (postFixString != null ? !postFixString.equals(node.postFixString) : node.postFixString != null)
			return false;
		if (currentValue != node.currentValue)
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + currentValue;
		result = 31 * result + (sequenceName != null ? sequenceName.hashCode() : 0 );
		result = 31 * result + (preFixString != null ? preFixString.hashCode() : 0 );
		result = 31 * result + (postFixString != null ? postFixString.hashCode() : 0 );
		return result;
	}
}
