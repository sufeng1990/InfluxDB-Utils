package com.utils.influxdbutils.enums;

/**
 * @author sufeng
 * @date 2019-07-10
 */
public enum ExampleEnum {

	/**
	 * 查询常用字段
	 */
	TIME("time");

	private String code;

	ExampleEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
