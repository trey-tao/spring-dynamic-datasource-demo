package com.trey.dynamicdatasource;

import com.alibaba.fastjson.JSON;
import com.trey.dynamicdatasource.action.DataBaseQuery;
import com.trey.dynamicdatasource.action.DataBaseQueryContext;
import com.trey.dynamicdatasource.pojo.ExtendSource;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @FileName: Test.java
 * @Description: Test.java类说明
 * @Author: tao.shi
 * @Date: 2019/3/6 14:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

	@Autowired
	private DataBaseQueryContext dataBaseQueryContext;

	@org.junit.Test
	public void test() {
		ExtendSource extendSource = new ExtendSource();
		extendSource.setConnectionInfo("{\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\"jdbc:mysql://127.0.0.1:3306/test\"}");
		extendSource.setDbType("MYSQL");
		extendSource.setId(1L);
		DataBaseQuery dataBaseQuery = dataBaseQueryContext.build(extendSource.getDbType());
		String sql = "select service_name as name, url as url from test where service_code = 'test';";
		Optional optional = dataBaseQuery.query(extendSource,sql);
		Assert.assertTrue(optional.isPresent());
		System.out.println(JSON.toJSONString(optional.get()));
	}

}
