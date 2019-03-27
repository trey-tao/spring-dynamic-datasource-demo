package com.trey.dynamicdatasource.action;

import org.springframework.stereotype.Component;

/**
 * @FileName: OracleQueryStrategy.java
 * @Description: OracleQueryStrategy.java类说明
 * @Author: tao.shi
 * @Date: 2019/2/27 17:11
 */
@Component("ORACLE")
public class OracleQueryStrategy extends AbstractSqlQueryStrategy {

	private static String DRIVER = "oracle.jdbc.OracleDriver";

	@Override
	protected String getDriverName() {
		return DRIVER;
	}
}
