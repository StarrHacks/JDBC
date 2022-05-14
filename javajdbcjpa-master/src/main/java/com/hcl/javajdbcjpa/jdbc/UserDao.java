package com.hcl.javajdbcjpa.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class UserDao {
	private static String jdbcURL = "jdbc:h2:mem:test";
	private static String jdbcUsername = "su";
	private static String jdbcPassword = "";

	private static final String CREATE_TABLE_SQL = "create table users (" 
			+ "  id  int primary key,"
			+ "  name varchar(20)," 
			+ "  email varchar(30)," 
			+ "  country varchar(20) ) ";

	private static final String INSERT_USERS_SQL = "INSERT INTO users" 
			+ "  (id, name, email, country) VALUES "
			+ " (?, ?, ?, ?);";

	private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id =?";
	private static final String SELECT_ALL_USERS = "select * from users";
	private static final String DELETE_USERS_SQL = "delete from users where id = ?;";
	private static final String UPDATE_USERS_SQL = "update users set name = ?,email= ?, country =? where id = ?;";
	private static String name;
	private static String Email;
	private static String country;

	public UserDao() {
	}


	public static void main(String[] args) throws SQLException {
		
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);

		UserDao ud = new UserDao();

		// try with resources
		try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
			ud.createTable(connection);
			System.out.println(" Insert new user name, email. and Country ");
			String name = input.next();
			String email = input.next();
			String country = input.next();
			
			
			
			ud.insertUser(connection, new User(1, "name", "email", "country"));
			List<User> list = ud.selectAllUsers(connection);
			System.out.println("user count: " + list.size());
			System.out.println("user name: " + list.get(0).getName());
			System.out.println("user name: " + list.get(0).getEmail());
			System.out.println("user name: " + list.get(0).getCountry());
			
			System.out.println("Insert new user name, email, and country: ");
			name = input.next();
			email = input.next();
			country = input.next();
			
			
			
			ud.updateUser(connection, new User(1, "name", "email", "country"));
			list = ud.selectAllUsers(connection);
			System.out.println("user name: " + list.get(0).getName());
			System.out.println("user name: " + list.get(0).getEmail());
			System.out.println("user name: " + list.get(0).getCountry());
			System.out.println("want ot delete the user? (y/n)");
			String yn = input.next();
			if (yn.equalsIgnoreCase("y"))
			
			ud.deleteUser(connection, 1);
			else if (yn.equalsIgnoreCase("n"))
			System.out.println("user count:" + list.size());
			
			list = ud.selectAllUsers(connection);
			System.out.println("user count:" + list.size());
			input.close();
		}
	}

	public void createTable(Connection connection) throws SQLException {

		
		try (Statement statement = connection.createStatement()) {

	
			statement.execute(CREATE_TABLE_SQL);
		} 
	}

	public void insertUser(Connection connection, User user) throws SQLException {
		System.out.println(INSERT_USERS_SQL);
		
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
			preparedStatement.setLong(1, user.getId());
			preparedStatement.setString(2, user.getName());
			preparedStatement.setString(3, user.getEmail());
			preparedStatement.setString(4, user.getCountry());
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
			System.out.println("Inserted user Successfully");
		}
		
	}

	public User selectUser(Connection connection, int id) {
		User user = null;
	
		try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
			preparedStatement.setInt(1, id);
			System.out.println(preparedStatement);
		
			ResultSet rs = preparedStatement.executeQuery();

		
			while (rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				user = new User(id, name, email, country);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return user;
	}

	public List<User> selectAllUsers(Connection connection) {

	
		List<User> users = new ArrayList<>();
		
		try (Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, 
				ResultSet.CONCUR_UPDATABLE);) {
			System.out.println(statement);
		
			ResultSet rs = statement.executeQuery(SELECT_ALL_USERS);
			
			ResultSetMetaData dm = rs.getMetaData();
			System.out.println("dm=" + dm);

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				users.add(new User(id, name, email, country));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return users;
	}

	public boolean deleteUser(Connection connection, int id) throws SQLException {
		boolean rowDeleted;
		try (PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);) {
			statement.setInt(1, id);
			rowDeleted = statement.executeUpdate() > 0;
		}
		return rowDeleted;
	}

	public boolean updateUser(Connection connection, User user) throws SQLException {
		boolean rowUpdated;
		try (PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getCountry());
			statement.setInt(4, user.getId());

			rowUpdated = statement.executeUpdate() > 0;
		}
		return rowUpdated;
	}

	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

}