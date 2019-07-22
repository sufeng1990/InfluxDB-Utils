package com.utils.influxdbutils.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.utils.influxdbutils.Influx;
import com.utils.influxdbutils.dto.ExampleIot;
import com.utils.influxdbutils.utils.SqlHelper;
import java.util.List;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;
import org.influxdb.impl.InfluxDBResultMapper;

/**
 * 条件实体类
 *
 * @author sufeng
 * @date 2019-07-06
 */
public class InfluxImpl implements Influx {

	private InfluxDB db;
	/**
	 * 数据库
	 */
	private String database;
	/**
	 * 默认保存策略
	 */
	private final String retentionPolicy = "";


	public InfluxImpl(final InfluxDB db, final String database) {
		this.db = db;
		this.database = database;
	}

	@Override
	public PageInfo selectPageAndCountByExample(ExampleIot iot, int pageNum,
			int pageSize) {
		List<?> list = this.selectPageByExample(iot, pageNum, pageSize);
		long count = this.selectCountByExample(iot);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setList(list);
		pageInfo.setTotal(count);
		pageInfo.setPageSize(pageSize);
		pageInfo.setPageNum(pageNum);
		return pageInfo;
	}

	private List<?> selectPageByExample(ExampleIot iot, int pageNum, int pageSize) {
		Class<?> entityClass = iot.getEntityClass();
		String sql = SqlHelper.selectSql(iot, pageNum, pageSize);
		QueryResult queryResult = db.query(new Query(sql, database));
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		return resultMapper.toPOJO(queryResult, entityClass);
	}

	@Override
	public List<?> selectByExample(ExampleIot iot) {
		Class<?> entityClass = iot.getEntityClass();
		String sql = SqlHelper.selectCount(iot);
		QueryResult queryResult = db.query(new Query(sql, database));
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		return resultMapper.toPOJO(queryResult, entityClass);
	}

	@Override
	public Long selectCountByExample(ExampleIot iot) {
		String countSql = SqlHelper.selectCount(iot);
		QueryResult countResult = db.query(new Query(countSql, database));
		List<Series> series = countResult.getResults().get(0).getSeries();
		if (series == null) {
			return 0L;
		}
		List<String> columns = countResult.getResults().get(0).getSeries().get(0).getColumns();
		List<Object> resval = countResult.getResults().get(0).getSeries().get(0).getValues().get(0);
		Double d = (Double) resval.get(columns.indexOf("count"));
		return d.longValue();
	}

	@Override
	public void insert(Object obj) throws Exception {
		this.insert(obj, retentionPolicy);
	}

	@Override
	public void insert(Object obj, String retentionPolicy) throws Exception {
		Point point = SqlHelper.getPoint(obj);
		BatchPoints batchPoints = BatchPoints.database(database).build();
		List<Point> points = Lists.newArrayList();
		points.add(point);
		List<String> records = SqlHelper.getRecords(points, batchPoints);
		db.write(database, retentionPolicy, InfluxDB.ConsistencyLevel.ALL, records);
	}

	@Override
	public void batchInsert(List<?> list) throws Exception {
		this.batchInsert(list, retentionPolicy);
	}

	@Override
	public void batchInsert(List<?> list, String retentionPolicy) throws Exception {
		List<Point> points = Lists.newArrayList();
		BatchPoints batchPoints = BatchPoints.database(database).build();
		for (Object item : list) {
			points.add(SqlHelper.getPoint(item));
		}
		List<String> records = SqlHelper.getRecords(points, batchPoints);
		db.write(database, retentionPolicy, InfluxDB.ConsistencyLevel.ALL, records);
	}

	@Override
	public List<?> countGroupByCategoryId(ExampleIot iot, String sql) {
		Class<?> entityClass = iot.getEntityClass();
		QueryResult queryResult = db.query(new Query(sql, database));
		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		return resultMapper.toPOJO(queryResult, entityClass);
	}
}
