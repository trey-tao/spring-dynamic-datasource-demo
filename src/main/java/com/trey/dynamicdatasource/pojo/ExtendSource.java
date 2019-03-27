package com.trey.dynamicdatasource.pojo;

import lombok.Data;

/**
 * 扩展数据源的结构
 * @FileName: ExtendSource.java
 * @Description: ExtendSource.java类说明
 * @Author: tao.shi
 * @Date: 2019/2/26 11:45
 */
@Data
public class ExtendSource{
	private Long Id;
	private String dbName;
	private String dbType;
	private String connectionInfo;
}
