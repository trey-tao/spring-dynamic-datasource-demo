package com.trey.dynamicdatasource.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置
 * 
 * @author Taven
 *
 */
@Configuration
public class DataSourceConfigurer{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigurer.class);

	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;

	public Map<String, Object> getProperties() {
		Map<String, Object> map = new HashMap<>();
		map.put("driverClassName", driverClassName);
		map.put("url", url);
		map.put("username", username);
		map.put("password", password);
		return map;
	}

	public DataSource dataSource() {
		DataSource dataSource = null;
		try {
			dataSource = DruidDataSourceFactory.createDataSource(getProperties());
		} catch (Exception e) {
			LOGGER.error("Create DataSource Error : {}", e);
			throw new RuntimeException();
		}
		return dataSource;
	}

    /**
     * 注册动态数据源
     * 
     * @return
     */
    @Bean("dynamicDataSource")
    public DynamicRoutingDataSource dynamicDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
	    Map<Object, Object> dataSourceMap = new HashMap<>(1);
	    dataSourceMap.put("default_db", dataSource());
	    // 设置默认数据源
	    dynamicRoutingDataSource.setDefaultTargetDataSource(dataSource());
	    dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
        return dynamicRoutingDataSource;
    }
}
