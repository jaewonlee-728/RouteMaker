package group12.RouteMaker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.jdbc2.optional.SimpleDataSource;

import org.sqlite.SQLiteDataSource;

import com.google.gson.Gson;
import com.heroku.sdk.jdbc.DatabaseUrl;


public class RMService {
    
	private String dbUrl;
	private Properties prop;
	
	public RMService(String url) throws RMServiceException {
		Connection conn = null; 
		dbUrl = url;
		prop = new Properties();
		
		try {
//			Statement stmt = conn.createStatement();
			
			String user = "postgres";
			String pass = "sing0627";
			prop.put("user", user);
			prop.put("password", pass);
			conn = DriverManager.getConnection(dbUrl, prop);
			if (conn != null) {
				System.out.println("welcome to routemaker");
			}
			Statement stmt = conn.createStatement();
			
			String sql = "CREATE TABLE  IF NOT EXISTS users ("
					+ "email text PRIMARY KEY," 
					+ "password text,"
					+ "firstName text,"
					+ "lastName text,"
					+ "agegroup text,"
					+ "currentlyEditingTripId text,"
					+ "preferenceList text[],"
					+ "currentTripIds text[],"
					+ "pastTripIds text[])" ;
			
			String sql1 = "CREATE TABLE IF NOT EXISTS trip ("
  				  + "tripId text PRIMARY KEY NOT NULL,"
  				  + "dayId text[],"
  				  + "cityCode text,"
  				  + "totalCost integer,"
  				  + "startDate text,"
  				  + "endDate text)";
			
  		  	String sql2 = "CREATE TABLE IF NOT EXISTS day ("
  				  + "dayId text PRIMARY KEY NOT NULL,"
  				  + "eventId text[],"
  				  + "date text,"
  				  + "dailyCost integer,"
  				  + "startTime text,"
  				  + "endTime text)";
  		  	
  		  	String sql3 = "CREATE TABLE IF NOT EXISTS event ("
  				  + "eventId text PRIMARY KEY NOT NULL,"
  				  + "dayId text,"
  				  + "siteId text,"
  				  + "duration double precision,"
  				  + "startTime integer,"
  				  + "endTime integer)";

//			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.executeUpdate(sql1);
			stmt.executeUpdate(sql2);
			stmt.executeUpdate(sql3);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RMServiceException("Failed to create schema at startup", e);
		}
	}
	
	public boolean createUser(String body) throws RMServiceException, SQLException {
		Connection conn = null;
		boolean ret = false;
		
  		conn = DriverManager.getConnection(dbUrl, prop);
 		Statement stmt = conn.createStatement();
  		User user = new Gson().fromJson(body, User.class);
  		String sql = "SELECT email FROM users";
		ResultSet rs = stmt.executeQuery(sql);
		List<String> usernames = new ArrayList<String>();
		while (rs.next()) {
			usernames.add(rs.getString("email"));
		}
		if (!usernames.contains(user.getEmail())) {
			String sql1 = "INSERT INTO users (email, password, firstname, lastname, agegroup, preferencelist) "
			+ "VALUES ('" + user.getEmail() + "', '" + user.getPassword() + "', '" + user.getFirstName() + "', "
					+ "'" + user.getLastName() + "', '" + user.getAgeGroup() + "', '" + user.preferenceToString() + "');";
			stmt.executeUpdate(sql1);
			ret = true;
		} 
		return ret;	
 	}
	
	public User updateUser(String body) throws RMServiceException, SQLException {
		Connection conn = null;
  		conn = DriverManager.getConnection(dbUrl, prop);
  		Statement stmt = conn.createStatement();
  		User user = new Gson().fromJson(body, User.class);
  		String sql = "UPDATE users SET email='"+ user.getEmail() + "', password='" + user.getPassword() + "', "
  				+ "firstname='" + user.getFirstName() + "', lastname='" + user.getLastName() + "', agegroup='" + user.getAgeGroup() + "', "
				+ "preferencelist='" + user.preferenceToString() + "' WHERE email= '" + user.email + "';";
  		stmt.executeUpdate(sql);	
		return user;
	}
	
	
	public User getUser(String email) throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(dbUrl, prop);
     	Statement stmt = conn.createStatement();
     	ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email='"+ email +"';");
 
     	String pw = null;
     	String firstName = null;
     	String lastName = null;
     	String ageGroup = null;
     	String preference = null;
     	List<String> preferences = new ArrayList<>();
     	String currentlyEditingTripId = null;
     	String currentTripId = null;
     	List<String> currentTripIds = new ArrayList<>();
     	String pastTripId = null;
     	List<String> pastTripIds = new ArrayList<>();

     	User user = null;
     	
     	while(rs.next()) {
     		pw = rs.getString("password");
     		lastName = rs.getString("lastname");
     		firstName = rs.getString("firstname");
     		ageGroup = rs.getString("agegroup");
			preference= rs.getString("preferencelist");
			preferences = stringToList(preference, preferences);
     		user = new User(email, firstName, lastName, pw, ageGroup, preferences);
     		
     		currentlyEditingTripId = rs.getString("currentlyeditingtripid"); 
     		user.setCurrentlyEditingTripId(currentlyEditingTripId);      		 
     		
     		currentTripId = rs.getString("currenttripids");
     		currentTripIds = stringToList(currentTripId, currentTripIds);
     		user.setCurrentTripIds(currentTripIds);
     		
     		pastTripId = rs.getString("pasttripids");
     		pastTripIds = stringToList(pastTripId, pastTripIds);
     		user.setPastTripIds(pastTripIds);
     	}
     	return user;
	}
	
	public User currToPassTripIds(User user) throws SQLException, ParseException {
		Connection conn = null;
		conn = DriverManager.getConnection(dbUrl, prop);
     	Statement stmt = conn.createStatement();
		
		//Update currentTrip Id to past Trip Id
     	Date date = Calendar.getInstance().getTime();
     	String inputDate = new SimpleDateFormat("yyyyMMdd").format(date);
     	DateFormat d = new SimpleDateFormat("yyyyMMdd");
     	Calendar today = Calendar.getInstance();
     	today.setTime(d.parse(inputDate));
     	
		List<String> nonCurrTripIds = new ArrayList<>(); 
     	for (String tripId : user.getCurrentTripIds()) {
     		String endDate = null;
     		ResultSet rs = stmt.executeQuery("SELECT enddate as enddate FROM trip WHERE tripid = '" + tripId + "';");
     		
     		Calendar endCal = null;
     		while(rs.next()) {
     			endDate = rs.getString("enddate");
     			endCal = Trip.changeDateFormat(endDate);
     		}
     		
     		if (today.compareTo(endCal) > 0) {
     			nonCurrTripIds.add(tripId);
     		}         		
     	}			
     	if (nonCurrTripIds.size() != 0) {
     		for (String tripId : nonCurrTripIds) {
     			user.moveCurrTrip(tripId);
     		}
     	   	String sql = "UPDATE users SET currentTripIds = '" + listToString(user.currentTripIds) + "', pastTripIds = '" + listToString(user.pastTripIds) + "' where email = '" + user.getEmail() +"';";
     	   	stmt.executeUpdate(sql);
     	}
		return user;
	}
	
	public Trip getTripInfo(String tripId) throws SQLException, ParseException {
		Connection conn = null;
		conn = DriverManager.getConnection(dbUrl, prop);
     	Statement stmt = conn.createStatement();
		
     	Trip trip = null;

		String dayId = null;
        List<String> dayIds = new ArrayList<>(); 
        String cityCode = null;
        String startDate = null;
        String endDate = null;

      	ResultSet rs = stmt.executeQuery("SELECT * FROM trip WHERE tripid = '" + tripId + "';");
      	
  	 	while(rs.next()) {
  	 		cityCode = rs.getString("citycode");
  	 		dayId = rs.getString("dayid");
  	 		dayIds = stringToList(dayId, dayIds);
  	 		startDate = rs.getString("startdate");
  	 		endDate = rs.getString("enddate");
  	 		trip = new Trip(tripId, cityCode, dayIds, startDate, endDate);
  	 	}
		return trip;
	}
	
	public String createNewTripId() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(dbUrl, prop);
     	Statement stmt = conn.createStatement();
     	String sql = "SELECT COUNT(tripId) AS total FROM trip;";
		ResultSet rs = stmt.executeQuery(sql);
		int count = 0;
	  	while(rs.next()) {
	  		count = rs.getInt("total");
	  	}
	  	String tripId = "t" + (count + 1);
	  	System.out.println(tripId);
	  	return tripId;
	}
	
	public void createTrip(String email, String tripId) throws SQLException {
   		Connection conn = null;
   		conn = DriverManager.getConnection(dbUrl, prop);
   		System.out.println("222222222222222222222");
   		Statement stmt = conn.createStatement();
   		System.out.println("222222222222222222222");
   		String sql = "UPDATE users SET currentlyeditingtripid = '"+ tripId +"' WHERE email = '" + email + "';";
   		System.out.println("222222222222222222222");
   		stmt.executeUpdate(sql);
   		System.out.println("222222222222222222222");
   		String sql1 = "INSERT INTO trip(tripid) VALUES ('" + tripId + "');";
   		System.out.println("222222222222222222222");
   		stmt.executeUpdate(sql1);
	}
	
	public void updateTrip(List<String> dayIds, String cityCode, String startDate, String endDate, String tripId) throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(dbUrl, prop);
		Statement stmt = conn.createStatement();
		
		String sql = "UPDATE trip SET dayid = '" + listToString(dayIds) + "', citycode = '" + cityCode + "', "
				  + "startdate = '" + startDate + "', enddate = '" + endDate + "' "
				  + "WHERE tripid = '" + tripId + "';";
		stmt.executeUpdate(sql);
	}

	public List<String> createDay(long diff, Calendar startDate, String startTime) throws SQLException {
		Connection conn = null;
   		conn = DriverManager.getConnection(dbUrl, prop);
   		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT COUNT(dayId) AS total FROM day;");
		int count = 0;
		while(rs.next()) {
			count = rs.getInt("total");
		}
		List<String> dayIds = new ArrayList<>();
		Calendar date = startDate;
		String inputDate = null;
		for (int i = 0; i < diff; i++) {
			String str = "d" + (count + 1);
			if (i != 0) {
				  date.add(Calendar.DAY_OF_MONTH, 1);
			}
			inputDate = new SimpleDateFormat("yyyyMMdd").format(date.getTime());
			String sql1 = "INSERT INTO day(dayId, date, starttime) VALUES ('" + str + "', '" + inputDate + "', '" + startTime + "');";
			stmt.executeUpdate(sql1);
			dayIds.add(str);
			count++;
		}
		return dayIds;
	}
	
	public static class RMServiceException extends Exception {
	    public RMServiceException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
	
  	/**
  	 * Method to convert a string to a list of strings.
  	 * @param str String to convert
  	 * @param strList working list of strings
  	 * @return list of strings
  	 */
	private static List<String> stringToList(String str, List<String> strList) {
		if (str != null) {
			str = str.replace("{", "").replace("}","");
			for (String s : str.split(",")) {
				strList.add(s);
			}
		}
		return strList;
	}
	
	/**
	 * Method to convert a list to string. 
	 * @param strList list of strings
	 * @return A converted string
	 */
	public static String listToString(List<String> strList) {
    	String ret = "{";
    	for (String s : strList) {
    		ret += s + " ";
    	}
    	ret = ret.trim();
    	ret += "}";
    	ret = ret.replaceAll(" ", ","); 
    	return ret;
    }

}





