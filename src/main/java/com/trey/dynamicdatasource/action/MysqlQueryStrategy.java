package com.trey.dynamicdatasource.action;

import org.springframework.stereotype.Component;

/**
 * @FileName: MysqlQueryStrategy.java
 * @Description: MysqlQueryStrategy.java类说明
 * @Author: tao.shi
 * @Date: 2019/2/27 15:10
 */
@Component("MYSQL")
public class MysqlQueryStrategy extends AbstractSqlQueryStrategy {

	private static final String DRIVER = "com.mysql.jdbc.Driver";


	@Override
	protected String getDriverName() {
		return DRIVER;
	}
}
