package com.example.model;

public enum ConfigStatus {
	/**
	 * 草稿
	 */
	Draft("草稿", 0),
	/**
	 * 启用
	 */
	Start("启用", 1),
	/**
	 * 停用
	 */
	End("停用", 2);
	
	// 定义一个 private 修饰的实例变量
	private String desc;
	private int index;

	// 定义一个带参数的构造器，枚举类的构造器只能使用 private 修饰
	private ConfigStatus(String desc, int index) {
		this.desc = desc;
		this.index = index;
	}

	// 普通方法
	public static String getDesc(int index) {
		for (AttributeDataType c : AttributeDataType.values()) {
			if (c.getIndex() == index) {
				return c.getDesc();
			}
		}
		return null;
	}

	// 定义 get set 方法
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
