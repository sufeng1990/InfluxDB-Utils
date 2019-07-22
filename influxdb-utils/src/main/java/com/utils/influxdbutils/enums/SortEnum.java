package com.utils.influxdbutils.enums;

/**
 * 条件实体类
 *
 * @author sufeng
 * @date 2019-07-06
 */
public enum SortEnum {

	/**
	 * 倒序
	 */
	DESC("DESC"),
	/**
	 * 正序
	 */
	ASC("ASC");

	private String code;

	SortEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

}