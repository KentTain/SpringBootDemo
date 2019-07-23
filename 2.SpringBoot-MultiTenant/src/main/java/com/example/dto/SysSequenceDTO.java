package com.example.dto;

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
public class SysSequenceDTO extends EntityBaseDTO implements java.io.Serializable {

	private static final long serialVersionUID = -8950076225487054729L;
	/**
	 * 序列名称
	 */
	private String sequenceName;
	/**
	 * 当前值
	 */
	private int currentValue;
	/**
	 * 初试值
	 */
	private int initValue;
	/**
	 * 最大值
	 */
	private int maxValue;
	/**
	 * 步长
	 */
	private int stepValue;
	/**
	 * 前缀
	 */
	private String preFixString;
	/**
	 * 后缀
	 */
	private String postFixString;
	/**
	 * 描述
	 */
	private String comments;
	/**
	 * 当前时间
	 */
	private String currDate;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		if (!super.equals(o))
			return false;

		SysSequenceDTO node = (SysSequenceDTO) o;

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
		result = 31 * result + (sequenceName != null ? sequenceName.hashCode() : 0);
		result = 31 * result + (preFixString != null ? preFixString.hashCode() : 0);
		result = 31 * result + (postFixString != null ? postFixString.hashCode() : 0);
		return result;
	}
}
