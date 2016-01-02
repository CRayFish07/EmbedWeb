package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;


public class Utils {
	/**
	 * ����һ������
	 */
	public static Connection connection;
	public static ResultSet resultSet;
	public static PreparedStatement preparedStatement;
	/**
	 * ��ȡһ������
	 * @return
	 */
	public static Connection getConnection(){
		try {
			Properties properties = new Properties();
			properties.load(Utils.class.getResourceAsStream("properties.properties"));
			String url = properties.getProperty("url");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			Class.forName("oracle.jdbc.OracleDriver");
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	public static void main(String[] args) {
		System.out.println(getConnection());
	}
}
