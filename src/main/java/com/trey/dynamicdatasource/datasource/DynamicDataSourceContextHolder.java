package com.trey.dynamicdatasource.datasource;

public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> dbProjectContextHolder = new ThreadLocal<>();

    /**
     * To switch DataSource
     *
     * @param key the key
     */
    public static void setDataSourceKey(String key) {
	    dbProjectContextHolder.set(key);
    }

    /**
     * Get current DataSource
     *
     * @return data source key
     */
    public static String getDataSourceKey() {
        return dbProjectContextHolder.get();
    }

    /**
     * To set DataSource as default
     */
    public static void clearDataSourceKey() {
	    dbProjectContextHolder.remove();
    }

}
