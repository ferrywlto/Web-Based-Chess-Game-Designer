package webBaseChessGameDesigner.system;
import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author  Ferry To
 */
public class DatabaseManager 
{
	protected static Driver driver; 
	protected static String userName = "root";
	protected static String passWord = "subai";
	protected static DatabaseManager dbm; 
	
	Connection conn;
	Statement statement;
	
	protected static void setupMySQLDriver()
	{
		if(driver != null) return;
		else {
			try {
				driver = (Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
			}
			catch(Exception e){e.printStackTrace();}
		}
	}
	
	public static DatabaseManager getManager()
	{
		if(dbm == null)
		{
			dbm = new DatabaseManager();
		}
		return dbm;
	}
	
	// Overloaded constructor with default username and password for connecting database
	protected DatabaseManager()
	{
		try{
			setupMySQLDriver();
			conn = getMySQLConnection(userName, passWord);
		}
		catch(SQLException se){se.printStackTrace();}
	}
		
	protected DatabaseManager(String user, String pass)
	{
		try{
			setupMySQLDriver();
			userName = user;
			passWord = pass;
			conn = getMySQLConnection(user, pass);
		}
		catch(SQLException se){se.printStackTrace();}
	}
	

	public Connection getMySQLConnection(String username, String password) throws SQLException
	{
		String connStr = "jdbc:mysql://localhost/wbcgd?user="+username+"&password="+password;
		return DriverManager.getConnection(connStr);
	}
	
	public int execCommand(String cmdStr) throws SQLException
	{
		if(conn == null || conn.isClosed()) conn = getMySQLConnection(userName, passWord);
		// For debug purpose.
		Messager.printMsg(this,"SQL to execute: "+cmdStr);
		Statement sql = conn.createStatement();
		return sql.executeUpdate(cmdStr);
	}
	
	public ResultSet exeCommand(String cmdStr)
	{
		try
		{
			if(conn == null || conn.isClosed()) conn = getMySQLConnection(userName, passWord);
			// For debug purpose.
			Messager.printMsg(this,"SQL to execute: "+cmdStr);
			Statement sql = conn.createStatement();
			return sql.executeQuery(cmdStr);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Messager.printMsg(this,"Invalid SQL encountered.");
			return null;
		}
	}
	
	// Close database connection. 
	// Must be called after SQL statement completed to execute.
	public void closeDBC()
	{
		try
		{
			if(conn != null && !conn.isClosed())
				conn.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	// Used for validate user login.
	public boolean validateLogin(String user, String pass)
	{
		try
		{
			String SQL = "SELECT count(*) AS result FROM User WHERE UID='"+user+"' AND UPass='"+pass+"'";
			ResultSet rs = exeCommand(SQL);
			rs.beforeFirst();
			rs.next();
			int rsCount = rs.getInt("result");
			return (rsCount == 1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	// See whether user have the created maximum number of chess.
	public boolean checkChessCount(String user, int count)
	{
		int rsCount = listChessCount(user);
		return (rsCount > count);
	}
	
	public boolean checkChessNameExist(String user, String chessName)
	{
		try
		{
			String SQL = "SELECT count(*) AS result FROM Chess WHERE UID='"+user+"' AND name='"+chessName+"'";
			ResultSet rs = exeCommand(SQL);
			rs.beforeFirst();
			rs.next();
			int rsCount = rs.getInt("result");
			return (rsCount > 0);			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}		
	}
	public int listChessCount(String user)
	{
		try
		{
			String SQL = "SELECT count(*) AS result FROM Chess WHERE UID='"+user+"'";
			ResultSet rs = exeCommand(SQL);
			rs.beforeFirst();
			rs.next();
			int rsCount = rs.getInt("result");
			return rsCount;			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	
	public String[][] listChessNames(String user)
	{
		try
		{
			String SQL = "SELECT name, description FROM Chess WHERE UID='"+user+"'";
			ResultSet rs = exeCommand(SQL);
			ArrayList<String[]> names = new ArrayList<String[]>(0);
			rs.beforeFirst();
			while(rs.next()) {
				String[] temp = new String[2];
				temp[0] = rs.getString("name");
				temp[1] = rs.getString("description");
				names.add(temp);
			}
			// Created for casting back in ArrayList.toArray();
			String[][] x = new String[names.size()][];
			return (names.toArray(x));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String getChessFileName(String user, String chess)
	{
		try
		{
			String SQL = "SELECT location FROM Chess WHERE UID='"+user+"' AND name='"+chess+"'";
			ResultSet rs = exeCommand(SQL);
			
			// Return null if no rows actually fetched.
			if(!rs.first()) return null;
			rs.beforeFirst();
			rs.next();
			
			return rs.getString("location");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	// 
	public boolean addChessDefinition(String user, String chessName, String desc, String fileName)
	{
		try
		{
			String SQL = "INSERT INTO Chess VALUES('"+user+"','"+chessName+"','"+desc+"','"+fileName+"')";
			int count = execCommand(SQL);
			return (count==1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public void printResultSet(ResultSet rs)
	{
		try
		{
			rs.beforeFirst();
			while(rs.next())
			{
				Messager.printMsg(this,"User Name: "+rs.getString("UID")+"\tPassword: "+rs.getString("UPass"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return;
		}
	}
}
