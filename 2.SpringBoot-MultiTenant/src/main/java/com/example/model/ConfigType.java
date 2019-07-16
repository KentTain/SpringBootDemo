package com.example.model;

/**
 * @author tianc 配置类型
 */
public enum ConfigType {

	/**
	 * 电子邮件
	 */
	EmailConfig("电子邮件", 1),
	/**
	 * 短信平台
	 */
	SmsConfig("短信平台", 2),
	/**
	 * 支付方式
	 */
	PaymentMethod("支付方式", 3),
	/**
	 * ID5
	 */
	ID5("ID5", 4),
	/**
	 * 呼叫平台
	 */
	CallConfig("CallConfig", 5),
	/**
	 * 物流平台
	 */
	LogisticsPlatform("物流平台", 6),
	/**
	 * 微信公众号
	 */
	WeixinConfig("微信公众号", 7),
	/**
	 * 未知
	 */
	UNKNOWN("未知", 99);
	
	// 定义一个 private 修饰的实例变量
	private String desc;
	private int index;

	// 定义一个带参数的构造器，枚举类的构造器只能使用 private 修饰
	private ConfigType(String desc, int index) {
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
