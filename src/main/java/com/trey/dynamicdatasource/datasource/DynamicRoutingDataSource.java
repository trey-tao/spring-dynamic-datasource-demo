package com.trey.dynamicdatasource.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.trey.dynamicdatasource.pojo.SqlInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源
 * 
 * @author Taven
 *
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static Map<Object, Object> targetTargetDataSources = new ConcurrentHashMap<>();
    private static Map<Object, DynamicDataSourceTimer> timerMap = new ConcurrentHashMap<>();

	/**
	 * 通过定时任务周期性清除不使用的数据源
	 */
	@Scheduled(initialDelay= 10 * 60 * 1000, fixedRate= 10 * 60 * 1000)
	public void clearTask() {
		// 遍历timetMap，判断
		clearIdleDDS();
	}

	private void clearIdleDDS() {
		timerMap.forEach((k,v) -> {
			if(v.checkAndClose()) {
				delDatasources(k.toString());
			}
		});
	}

	private void updateTimer(String lookupKey) {
		// 更新时间戳
		DynamicDataSourceTimer timer = timerMap.get(lookupKey);
		if(timer == null) {
			return ;
		}
		timer.refreshTime();
	}


	/**
     * 设置当前数据源
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
    	// 每次设置当前数据源key时，更新timeMap中的时间
	    String lookupKey = DynamicDataSourceContextHolder.getDataSourceKey();
		updateTimer(lookupKey);
	    return lookupKey;
    }

	@Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
    	super.setTargetDataSources(targetDataSources);
		DynamicRoutingDataSource.targetTargetDataSources = targetDataSources;
		targetDataSources.forEach((k,v) -> {
			DataSource dataSource = (DataSource)v;
			timerMap.put(k,new DynamicDataSourceTimer(dataSource));
		});
	}

	/**
     * 是否存在当前key的 DataSource
     * 
     * @param key
     * @return 存在返回 true, 不存在返回 false
     */
    public static boolean isExistDataSource(String key) {
    	return targetTargetDataSources.containsKey(key);
    }

    /**
     * 动态增加数据源
     * 
     * @param sqlInfoVO 数据源属性
     * @return
     */
    public synchronized boolean addDataSource(SqlInfoVO sqlInfoVO) {
    	try {
    		Connection connection = null;
    		// 排除连接不上的错误
    		try { 
				Class.forName(sqlInfoVO.getDriverClassName());
				connection = DriverManager.getConnection(
						sqlInfoVO.getUrl(),
						sqlInfoVO.getUsername(),
						sqlInfoVO.getPassword());
			} catch (Exception e) {
    			e.printStackTrace();
				return false;
			} finally {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			}
		    //获取要添加的数据库名
    		String projectId = sqlInfoVO.getProjectId();
    		if (StringUtils.isBlank(projectId)) {
			    return false;
		    }
    		if (DynamicRoutingDataSource.isExistDataSource(projectId)) {
			    return true;
		    }
    		DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(beanToMap(sqlInfoVO));
			druidDataSource.init();
			Map<Object, Object> targetMap = DynamicRoutingDataSource.targetTargetDataSources;
			targetMap.put(projectId, druidDataSource);
			this.setTargetDataSources(targetMap);
			this.afterPropertiesSet();
			logger.info("dataSource [{}] has been added" + projectId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
    	return true;
    }

	// 删除数据源
	public synchronized boolean delDatasources(String datasourceid) {
		Map<Object, Object> dynamicTargetDataSources2 = DynamicRoutingDataSource.targetTargetDataSources;
		if (dynamicTargetDataSources2.containsKey(datasourceid)) {
			Set<DruidDataSource> druidDataSourceInstances = DruidDataSourceStatManager.getDruidDataSourceInstances();
			for (DruidDataSource l : druidDataSourceInstances) {
				if (datasourceid.equals(l.getName())) {
					System.out.println(l);
					dynamicTargetDataSources2.remove(datasourceid);
					DruidDataSourceStatManager.removeDataSource(l);
					// 将map赋值给父类的TargetDataSources
					setTargetDataSources(dynamicTargetDataSources2);
					// 将TargetDataSources中的连接信息放入resolvedDataSources管理
					super.afterPropertiesSet();
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private <T> Map<String, Object> beanToMap(T bean) {
		Map<String, Object> map = new HashMap<>();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet()) {
				map.put(key+"", beanMap.get(key));
			}
		}
		return map;
	}



}
