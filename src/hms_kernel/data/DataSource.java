package hms_kernel.data;

import legion.data.MySqlDataSource;

public class DataSource {
	private final static String ACCESS_DATA_SOURCE = "jdbc:ucanaccess://d:/MyModule/database/HMS.accdb"; // 正式資料庫
	private final static String ACCESS_DATA_SOURCE_TEST = "jdbc:ucanaccess://d:/MyModule/database/DatabaseAccountTest.accdb"; // 測試資料庫
	
	public final static String getAccessDataSource() {
		return ACCESS_DATA_SOURCE;
//		return ACCESS_DATA_SOURCE_TEST;
	}
	
	
	private final static MySqlDataSource DATA_SOURCE_DEV = new MySqlDataSource("localhost", "hms_dev", "root",
			"root!87570620");
	
	private final static MySqlDataSource DATA_SOURCE = new MySqlDataSource("localhost", "hms", "root",
			"root!87570620");
	
	public final static MySqlDataSource getDataSource() {
//		return DATA_SOURCE_DEV; 
		return DATA_SOURCE;
	}
	
	
}
