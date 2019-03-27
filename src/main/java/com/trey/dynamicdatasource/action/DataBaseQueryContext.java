package com.trey.dynamicdatasource.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @FileName: DataBaseQueryContext.java
 * @Description: DataBaseQueryContext.java类说明
 * @Author: tao.shi
 * @Date: 2019/2/27 15:15
 */
@Component
public class DataBaseQueryContext {

	@Autowired
	private Map<String, DataBaseQuery> contextStrategy = new ConcurrentHashMap<>();

	public DataBaseQuery build(String type) {
		return contextStrategy.get(type);
	}

}
