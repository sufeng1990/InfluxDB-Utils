package com.utils.influxdbutils.dto;


import com.google.common.collect.Lists;
import com.utils.influxdbutils.MapperException;
import com.utils.influxdbutils.enums.ExampleEnum;
import com.utils.influxdbutils.enums.SortEnum;
import com.utils.influxdbutils.utils.SqlHelper;
import com.utils.influxdbutils.utils.TimeUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * 条件实体类
 *
 * @author sufeng
 * @date 2019-07-06
 */
public class ExampleIot {

	private boolean exists;
	private boolean notNull;
	/**
	 * 查询或者写入的实体
	 */
	private Class<?> entityClass;
	/**
	 * 属性和对应参数
	 */
	private List<String> list;
	/**
	 * 排序
	 */
	private SortEnum sortEnum;
	/**
	 * group by
	 */
	private String groupBy;
	/**
	 * 自定义sql串
	 */
	private String condition;

	/**
	 * 默认exists为true
	 */
	public ExampleIot(Class<?> entityClass) {
		this(entityClass, true);
	}

	/**
	 * 带exists参数的构造方法，默认notNull为false，允许为空
	 *
	 * @param exists - true时，如果字段不存在就抛出异常，false时，如果不存在就不使用该字段的条件
	 */
	public ExampleIot(Class<?> entityClass, boolean exists) {
		this(entityClass, exists, false);
	}


	/**
	 * 带exists参数的构造方法
	 *
	 * @param exists - true时，如果字段不存在就抛出异常，false时，如果不存在就不使用该字段的条件
	 * @param notNull - true时，如果值为空，就会抛出异常，false时，如果为空就不使用该字段的条件
	 */
	public ExampleIot(Class<?> entityClass, boolean exists, boolean notNull) {
		this.exists = exists;
		this.notNull = notNull;
		this.entityClass = entityClass;
		this.list = Lists.newArrayList();
	}


	public Class<?> getEntityClass() {
		return this.entityClass;
	}

	public List<String> getList() {
		return this.list;
	}

	public SortEnum getSortEnum() {
		return this.sortEnum;
	}

	public String getGroupBy() {
		return this.groupBy;
	}

	public String getCondition() {
		return this.condition;
	}


	/**
	 * 等于
	 *
	 * @param property 字段
	 * @param value 值
	 */
	public void andEqualTo(String property, Object value) {
		if (exists && StringUtils.isEmpty(property)) {
			throw new MapperException("andEqualTo(), roperty cannot be empty");
		}
		if (notNull && value == null) {
			throw new MapperException("andEqualTo(), value for condition cannot be null");
		}
		if (StringUtils.equals(ExampleEnum.TIME.getCode(), property) && value instanceof Number) {
			value = TimeUtils.getUTCTimeStr((Long) value);
		}
		if (value != null) {
			this.list.add(SqlHelper.camel2Underline(property) + " = '" + value + "'");
		}
	}

	/**
	 * 不等于
	 *
	 * @param property 字段
	 * @param value 值
	 */
	public void andNotEqualTo(String property, Object value) {
		if (exists && StringUtils.isEmpty(property)) {
			throw new MapperException("andNotEqualTo(), roperty cannot be empty");
		}
		if (notNull && value == null) {
			throw new MapperException("andNotEqualTo(), value for condition cannot be null");
		}
		if (StringUtils.equals(ExampleEnum.TIME.getCode(), property) && value instanceof Number) {
			value = TimeUtils.getUTCTimeStr((Long) value);
		}
		if (value != null) {
			this.list.add(SqlHelper.camel2Underline(property) + " != '" + value + "'");
		}
	}

	/**
	 * 大于
	 *
	 * @param property 字段
	 * @param value 值
	 */
	public void andGreaterThan(String property, Object value) {
		if (exists && StringUtils.isEmpty(property)) {
			throw new MapperException("orGreaterThan(), roperty cannot be empty");
		}
		if (notNull && value == null) {
			throw new MapperException("orGreaterThan(), value for condition cannot be null");
		}
		if (StringUtils.equals(ExampleEnum.TIME.getCode(), property) && value instanceof Number) {
			value = TimeUtils.getUTCTimeStr((Long) value);
		}
		if (value != null) {
			this.list.add(SqlHelper.camel2Underline(property) + " > '" + value + "'");
		}
	}

	/**
	 * 大于等于
	 *
	 * @param property 字段
	 * @param value 值
	 */
	public void andGreaterThanOrEqualTo(String property, Object value) {
		if (StringUtils.equals(ExampleEnum.TIME.getCode(), property) && value instanceof Number) {
			value = TimeUtils.getUTCTimeStr((Long) value);
		}
		if (value != null) {
			this.list.add(SqlHelper.camel2Underline(property) + " >= '" + value + "'");
		}
	}

	/**
	 * 小于
	 *
	 * @param property 字段
	 * @param value 值
	 */
	public void andLessThan(String property, Object value) {
		if (StringUtils.equals(ExampleEnum.TIME.getCode(), property) && value instanceof Number) {
			value = TimeUtils.getUTCTimeStr((Long) value);
		}
		if (value != null) {
			this.list.add(SqlHelper.camel2Underline(property) + " < '" + value + "'");
		}
	}

	/**
	 * 小于等于
	 *
	 * @param property 字段
	 * @param value 值
	 */
	public void andLessThanOrEqualTo(String property, Object value) {
		if (StringUtils.equals(ExampleEnum.TIME.getCode(), property) && value instanceof Number) {
			value = TimeUtils.getUTCTimeStr((Long) value);
		}
		if (value != null) {
			this.list.add(SqlHelper.camel2Underline(property) + " <= '" + value + "'");
		}
	}

	/**
	 * like
	 *
	 * @param property 字段
	 * @param value 值
	 */
	public void andLike(String property, String value) {
		if (value != null) {
			this.list.add(SqlHelper.camel2Underline(property) + " =~/" + value + "*/");
		}
	}

	/**
	 * in操作
	 *
	 * @param property 字段
	 * @param iterable 属性
	 */
	public void andIn(String property, Iterable<?> iterable) {
		if (Objects.isNull(iterable)) {
			return;
		}
		Iterator it = iterable.iterator();
		if (!it.hasNext()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("(");

		while (it.hasNext()) {
			sb.append(SqlHelper.camel2Underline(property));
			sb.append(" = '");
			sb.append(it.next());
			sb.append("'");
			if (it.hasNext()) {
				sb.append(" or ");
			}
		}
		sb.append(")");
		list.add(sb.toString());
	}


	/**
	 * 添加排序条件
	 *
	 * @param sortEnum 目前只支持对time排序 value为"SortEnum.ASC, SortEnum.DESC"
	 */
	public void sort(SortEnum sortEnum) {
		this.sortEnum = sortEnum;
	}

	/**
	 * 添加 group by
	 *
	 * @param property 字段名
	 */
	public void groupBy(String property) {
		this.groupBy = property;
	}

	/**
	 * 手写条件
	 *
	 * @param condition 例如 "length(countryname)<5"
	 */
	public void andCondition(String condition) {
		if (StringUtils.isEmpty(condition)) {
			return;
		}
		this.list.add(condition);
	}

}
