package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.Utils;

/**
 * ״̬DAO
 * @author zjf
 *
 */
public class StateDao {

	public static void insertState(int state){
		Connection connection = Utils.getConnection();
		String sql = "insert into state values( seqState.nextval,?)";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setObject(1, state);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void updateState(int state){
		Connection connection = Utils.getConnection();
		String sql = "update state set state=? where id=2";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setObject(1, state);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ѯ״̬
	 * @return
	 */
	public static int queryState(){
		Connection connection = Utils.getConnection();
		String sql = "select state from state where id=2";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt("state");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(queryState());	
	}

}
