package com.example.model;

import java.util.*;
/**
 * @author tianc 数据类型
 */
public enum AttributeDataType {

	/**
	 * 字符串
	 */
	String("字符串", 0),

	/**
	 * 布尔型
	 */
	Bool("布尔型", 1),

	/**
	 * 整型
	 */
	Int("整型", 2),

	/**
	 * 数值型
	 */
	Decimal("数值型", 3),

	/**
	 * 金额
	 */
	Currancy("金额", 4),

	/**
	 * 日期型
	 */
	Datetime("日期型", 5),

	/**
	 * 文本型
	 */
	Text("文本型", 6),

	/**
	 * 其他
	 */
	Other("其他", 99);

	// 定义一个 private 修饰的实例变量
	private String desc;
	private int index;
	private static Map<Integer, AttributeDataType> map = new HashMap<Integer, AttributeDataType>();
	static {
        for (AttributeDataType type : AttributeDataType.values()) {
            map.put(type.index, type);
        }
    }
	public static AttributeDataType valueOf(int index) {
        return map.get(index);
    }
	
	// 定义一个带参数的构造器，枚举类的构造器只能使用 private 修饰
	private AttributeDataType(String desc, int index) {
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
