package com.trey.dynamicdatasource.action;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trey.dynamicdatasource.datasource.DynamicDataSourceContextHolder;
import com.trey.dynamicdatasource.datasource.DynamicRoutingDataSource;
import com.trey.dynamicdatasource.pojo.ExtendSource;
import com.trey.dynamicdatasource.pojo.SqlInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @FileName: AbstractSqlQueryStrategy.java
 * @Description: AbstractSqlQueryStrategy.java类说明
 * @Author: tao.shi
 * @Date: 2019/2/27 15:08
 */
@Slf4j
public abstract class AbstractSqlQueryStrategy implements DataBaseQuery {

	@Autowired
	@Qualifier("dynamicDataSource")
	private DynamicRoutingDataSource dynamicDataSource;

	protected static final String JDBC_URL = "jdbcUrl";
	protected static final String USERNAME = "userName";
	protected static final String PASSWORD = "password";

	protected abstract String getDriverName();


	@Override
	public Optional<Map<String,Object>> query(ExtendSource extendSource, String sql){
		/**
		 * 获取指定数据源，如果获取不到，则add
		 *
		 */
		SqlInfoVO sqlInfoVO = createSqlInfoVO(extendSource);
		DynamicDataSourceContextHolder.setDataSourceKey(sqlInfoVO.getProjectId());
		if(!DynamicRoutingDataSource.isExistDataSource(sqlInfoVO.getProjectId())) {
			dynamicDataSource.addDataSource(sqlInfoVO);
		}

		if (sqlInfoVO == null) {
			log.error("mysql source setting error, please check!");
		}
		Map<String,Object> resultMap = jdbcQuery(sql);
		if(resultMap.isEmpty()) {
			return Optional.empty();
		}
        return Optional.of(resultMap);
	}

	private Map<String,Object> jdbcQuery(String sql) {
		Statement stmt;
		Connection conn = null;
		try {
			conn = dynamicDataSource.getConnection();
			if (conn.isClosed()) {
				return Collections.EMPTY_MAP;
			}

			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			int columCount = md.getColumnCount();
			while(rs.next()) {
				Map<String,Object> rowData = new HashMap<>();
				for(int i = 1; i <= columCount; i++) {
					rowData.put(md.getColumnName(i), rs.getObject(i));
				}
				return rowData;

			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		} finally {
			DynamicDataSourceContextHolder.clearDataSourceKey();
		}
		return Collections.EMPTY_MAP;
	}

	private SqlInfoVO createSqlInfoVO(ExtendSource extendSource) {
		String connectionInfo = extendSource.getConnectionInfo();
		SqlInfoVO sqlInfoVO = new SqlInfoVO();
		sqlInfoVO.setDriverClassName(getDriverName());
		JSONObject jsonObject = JSON.parseObject(connectionInfo);
		sqlInfoVO.setUrl(jsonObject.getString(JDBC_URL));
		sqlInfoVO.setPassword(jsonObject.getString(PASSWORD));
		sqlInfoVO.setUsername(jsonObject.getString(USERNAME));
		sqlInfoVO.setProjectId(extendSource.getDbType() + extendSource.getId());
		return sqlInfoVO;
	}
}
