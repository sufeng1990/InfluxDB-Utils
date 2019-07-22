package com.utils.influxdbutils;

import com.github.pagehelper.PageInfo;
import com.utils.influxdbutils.dto.ExampleIot;
import java.util.List;


/**
 * 条件实体类
 *
 * @author sufeng
 * @date 2019-07-06
 */
public interface Influx {

	/**
	 * 根据条件分页查询数据
	 *
	 * @param exampleIOT 条件
	 * @param pageNum 页数
	 * @param pageSize 条数
	 * @return 返回分页数据
	 * @throws Exception 异常
	 */
	PageInfo selectPageAndCountByExample(ExampleIot exampleIOT, int pageNum,
			int pageSize) throws Exception;

	/**
	 * 根据条件查询数据
	 *
	 * @param exampleIOT 条件
	 * @return 返回数据
	 * @throws Exception 异常
	 */
	List<?> selectByExample(ExampleIot exampleIOT) throws Exception;

	/**
	 * 根据条件查询条数
	 *
	 * @param exampleIOT 条件
	 * @return 返回数据
	 * @throws Exception 异常
	 */
	Long selectCountByExample(ExampleIot exampleIOT) throws Exception;

	/**
	 * 写入数据
	 *
	 * @param obj 数据
	 * @throws Exception 异常
	 */
	void insert(Object obj) throws Exception;

	/**
	 * 写入数据(可指定存储策略)
	 *
	 * @param obj 数据
	 * @throws Exception 异常
	 */
	void insert(Object obj, String retentionPolicy) throws Exception;

	/**
	 * 批量写入数据
	 *
	 * @param list 数据
	 * @throws Exception 异常
	 */
	void batchInsert(List<?> list) throws Exception;

	/**
	 * 批量写入数据
	 *
	 * @param list 数据
	 * @throws Exception 异常
	 */
	void batchInsert(List<?> list, String retentionPolicy) throws Exception;

  List<?> countGroupByCategoryId(ExampleIot iot, String sql);
}
