package com.utils.influxdbutils.utils;

import static java.util.regex.Pattern.compile;

import com.google.common.collect.Maps;
import com.utils.influxdbutils.dto.ExampleIot;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.util.CollectionUtils;


/**
 * 条件实体类
 *
 * @author sufeng
 * @date 2019-07-06
 */
@Slf4j
public class SqlHelper {


	/**
	 * 动态表名(根据实体类名称生成)(驼峰)
	 *
	 * @param defaultTableName 表名
	 * @return sql
	 */
	private static String fromTable(String defaultTableName) {
		return " FROM "
				+ defaultTableName
				+ " ";
	}

	/**
	 * 分页
	 *
	 * @param pageNum 页数
	 * @param pageSize 条数
	 * @return sql
	 */
	private static String fromLimit(int pageNum, int pageSize) {
		if (pageNum == 0) {
			pageNum = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}
		return " LIMIT "
				+ pageSize
				+ " OFFSET "
				+ (pageNum - 1) * pageSize;
	}

	/**
	 * Example查询中的where结构,当where条件为null时,返回""
	 *
	 * @param condition 条件集合
	 * @return sql
	 */
	private static String exampleWhereClause(List<String> condition) {
		if (CollectionUtils.isEmpty(condition)) {
			log.info("WHERE, there is at least one condition behind");
			return "";
		}
		StringBuilder whereCondition = new StringBuilder(" WHERE ");
		int i = 1;
		for (String item : condition) {
			whereCondition.append(item);
			if (i >= 1 && i < condition.size()) {
				whereCondition.append(" and ");
			}
			i++;
		}
		return whereCondition.toString();
	}

	/**
	 * 拼接完整查询sql
	 *
	 * @param iot 条件
	 * @param pageNum 页数
	 * @param pageSize 条数
	 * @return sql
	 */
	public static String selectSql(ExampleIot iot, int pageNum, int pageSize) {
		return select(iot) + SqlHelper.fromLimit(pageNum, pageSize);
	}

	/**
	 * select头
	 *
	 * @param iot 条件
	 * @return sql
	 */
	private static String select(ExampleIot iot) {
		return getSql(iot, "SELECT *");
	}

	/**
	 * 条数查询
	 *
	 * @param iot 条件
	 * @return sql
	 */
	public static String selectCount(ExampleIot iot) {
		return getSql(iot, "SELECT COUNT(connect_id)");
	}

	/**
	 * @param iot 条件
	 * @param select sql头信息
	 * @return sql
	 */
	private static String getSql(ExampleIot iot, String select) {
		String tableName = SqlHelper.getTableName(iot.getEntityClass());
		List<String> condition = iot.getList();
		StringBuilder sql = new StringBuilder(select);
		sql.append(SqlHelper.fromTable(tableName));
		sql.append(SqlHelper.exampleWhereClause(condition));
		if (StringUtils.isNotEmpty(iot.getGroupBy())) {
			sql.append(SqlHelper.groupBy(iot.getGroupBy()));
		}
		if (iot.getSortEnum() != null) {
			sql.append(SqlHelper.sort(iot.getSortEnum().getCode()));
		}
		return sql.toString();
	}

	private static String groupBy(String groupBy) {
		return " GROUP BY "
				+ groupBy
				+ " ";
	}

	/**
	 * 拼接排序类型
	 *
	 * @param code 排序方式
	 * @return 返回字符串
	 */
	private static String sort(String code) {
		return " ORDER BY "
				+ code
				+ " ";
	}

	/**
	 * 生成一条point
	 *
	 * @param object 数据
	 * @return point
	 * @throws Exception 异常
	 */
	public static Point getPoint(Object object) throws Exception {
		Map<String, String> tagMap = Maps.newHashMap();
		Map<String, Object> fieldMap = Maps.newHashMap();
		Class<?> entityClass = object.getClass();
		Field[] fields = entityClass.getDeclaredFields();
		long time = 0;
		Measurement measurement = entityClass.getAnnotation(Measurement.class);
		String tableName = measurement.name();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			String str = field.getName();
			String name = "get" + str.substring(0, 1).toUpperCase() + str.substring(1);
			Object obj = entityClass.getMethod(name).invoke(object);
			if (column == null || obj == null || StringUtils.equals(str, "time")) {
				continue;
			}
			if (StringUtils.equals(str, "msgts")) {
				long date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(obj.toString()).getTime();
				time = date * 1000000 + (int) ((Math.random() * 9 + 1) * 100000);
			}
			if (column.tag()) {
				tagMap.put(column.name(), obj.toString());
			} else {
				fieldMap.put(column.name(), obj);
			}
		}
		return pointBuilder(tableName, time, tagMap, fieldMap);
	}

	/**
	 * 多条point集合
	 *
	 * @param points point集合
	 */
	public static List<String> getRecords(List<Point> points, BatchPoints batchPoints) {
		points.forEach(batchPoints::point);
		List<String> records = new ArrayList<>();
		records.add(batchPoints.lineProtocol());
		return records;
	}

	/**
	 * 写入point时间, 表, 索引数据, 基础数据
	 *
	 * @param measurement 表
	 * @param time 时间
	 * @param tags 索引
	 * @param fields 数据
	 * @return point
	 */
	private static Point pointBuilder(String measurement, long time, Map<String, String> tags,
			Map<String, Object> fields) {
		Point point = Point.measurement(measurement).tag(tags).fields(fields).build();
		if (time > 0) {
			point = Point.measurement(measurement).time(time, TimeUnit.NANOSECONDS).tag(tags)
					.fields(fields).build();
		}
		return point;
	}

	/**
	 * 驼峰法转下划线
	 *
	 * @param line 源字符串
	 * @return 转换后的字符串
	 */
	public static String camel2Underline(String line) {
		if (line == null || "".equals(line)) {
			return "";
		}
		line = String.valueOf(line.charAt(0)).toUpperCase()
				.concat(line.substring(1));
		StringBuilder sb = new StringBuilder();
		Pattern pattern = compile("[A-Z]([a-z\\d]+)?");
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String word = matcher.group();
			sb.append(word.toLowerCase());
			sb.append(matcher.end() == line.length() ? "" : "_");
		}
		return sb.toString();
	}

	/**
	 * 根据className生成表名称
	 */
	private static String getTableName(Class<?> entityClass) {
		Measurement measurement = entityClass.getAnnotation(Measurement.class);
		if (measurement == null || StringUtils.isEmpty(measurement.name())) {
			return SqlHelper.camel2Underline(entityClass.getSimpleName());
		}
		return measurement.name();
	}

}
