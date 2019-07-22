package com.utils.influxdbutils;

/**
 * 条件实体类
 *
 * @author sufeng
 * @date 2019-07-06
 */
public class MapperException extends RuntimeException {

	public MapperException() {
		super();
	}

	public MapperException(String message) {
		super(message);
	}

	public MapperException(String message, Throwable cause) {
		super(message, cause);
	}

	public MapperException(Throwable cause) {
		super(cause);

	}
}