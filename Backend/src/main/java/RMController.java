import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import com.heroku.sdk.jdbc.DatabaseUrl;

import java.util.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RMController {
	
  public static void main(String[] args) {

	  port(Integer.valueOf(System.getenv("PORT")));
      staticFileLocation("/public");
      
      /**
       * Home Page
       */
      get("/", "application/json", (request, response) -> {
    	  Connection connection = null;
    	  //List<Site> sites = run();
    	  try {
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
    		  connection = DatabaseUrl.extract().getConnection();
    		  Statement stmt = connection.createStatement();
    		  stmt.executeUpdate(sql1);
    		  stmt.executeUpdate(sql2);
    		  stmt.executeUpdate(sql3);
    		  response.status(200);
    		  return "Welcome to RouteMaker";
    		  } catch (Exception e) {
    			  System.err.println(e);
    			  response.status(501);
    			  }
    	  return "456";
    	  }, new JsonTransformer());
      
      /**
       * Inputs an email and returns the total number of tripIds that were create from the Trip table???? 
       * [WHEN DO WE USE THIS??????????????]
       */
      get("/trip/:email", (request, response) -> {
    	  Connection connection = null;
    	  try {
    		  connection = DatabaseUrl.extract().getConnection();
    		  Statement stmt = connection.createStatement();
    		  //String email = request.params(":email");
    		  ResultSet rs = stmt.executeQuery("SELECT COUNT(tripId) AS total FROM trip;");
    		  int count = 0;
    		  while(rs.next()) {
    			  count = rs.getInt("total");
    			  }
    		  return count;
    		  } catch (Exception e) {
    			  return Collections.EMPTY_MAP;
    			  } finally {
    				  if (connection != null) try{connection.close();} catch(SQLException e){}
    				  }
    	  }, new JsonTransformer());
      
      /**
       * Inputs an email and returns the currentlyEditingTripId of the user.
       */
      put("/trip/current/:email", (request, response) -> {
    	  Connection connection = null;
    	  try {
    		  connection = DatabaseUrl.extract().getConnection();
    		  Statement stmt = connection.createStatement();
    		  String email = request.params(":email");
    		  String sql1 = "SELECT COUNT(tripId) AS total FROM trip;";
    		  ResultSet rs1 = stmt.executeQuery(sql1);
    		  int count = 0;
    		  while(rs1.next()) {
    			  count = rs1.getInt("total");
    		  }
    		  String tripId = "t" + (count + 1);
    		  String sql2 = "UPDATE users SET currentlyeditingtripid = '"+ tripId +"' WHERE email = '" + email + "';";
    		  stmt.executeUpdate(sql2);
    		  String sql3 = "INSERT INTO trip(tripid) VALUES ('" + tripId + "');";
    		  stmt.executeUpdate(sql3);
    		  response.status(200);
    		  return tripId;
    		  } catch (Exception e) {
    			  System.out.println(e);
    			  response.status(501);
    			  return Collections.EMPTY_MAP;
    		  }
    	  },new JsonTransformer());

      /**
       * Inputs the number of days for the trip and the startTime and store them in day and trip table.
       */
      post("/trip/dayIds/:temp", (request, response) -> {
    	  Connection connection = null;
    	  try {
    		  connection = DatabaseUrl.extract().getConnection();
    		  Statement stmt = connection.createStatement();
    		  String temp = request.params(":temp");
    		  String[] parts = new String[2];
    		  String delimeter = "-";
    		  parts = temp.split(delimeter);
    		  int diff = Integer.parseInt(parts[0]);
    		  String tripId = parts[1];
    		  int startTime = Integer.parseInt(parts[2]);
    		  ResultSet rs = stmt.executeQuery("SELECT COUNT(dayId) AS total FROM day;");
    		  int count = 0;
    		  while(rs.next()) {
    			  count = rs.getInt("total");
    		  }
    		  ArrayList<String> dayIds = new ArrayList<>();
    		  for (int i = 0; i < diff; i++) {
    			  String str = "d" + (count + 1);
    			  String sql1 = "INSERT INTO day(dayId, starttime) VALUES ('" + str + "', '" + startTime + "');";
    			  stmt.executeUpdate(sql1);
    			  dayIds.add(str);
    			  count++;
    		  }
    		  String sql2 = "UPDATE trip SET dayid ='" + listToString(dayIds) + "' WHERE tripid = '" + tripId + "';";
    		  stmt.executeUpdate(sql2);
    		  return true;
    		  } catch (Exception e) {
    			  response.status(501);
    			  return Collections.EMPTY_MAP;
    		  } finally {
    			  if (connection != null) try{connection.close();} catch(SQLException e){}
    		  }
    	  }, new JsonTransformer());
      
      /**
       * Inputs the number of days, start Time and a Trip object and stores them in the Database accordingly.
       */
      post("/trip/:days/:startTime", (request, response) -> {
    	  Connection connection = null;
    	  try {
    		  connection = DatabaseUrl.extract().getConnection();
    		  Statement stmt = connection.createStatement();
    		  String days = request.params(":days");
    		  long diff = Long.parseLong(days);
    		  String startTime = request.params(":startTime");
    		  Trip trip = new Gson().fromJson(request.body(), Trip.class);
    		  ResultSet rs = stmt.executeQuery("SELECT COUNT(dayId) AS total FROM day;");
    		  int count = 0;
    		  while(rs.next()) {
    			  count = rs.getInt("total");
    		  }
    		  ArrayList<String> dayIds = new ArrayList<>();
    		  String start = new SimpleDateFormat("yyyyMMdd").format(trip.getStartDate().getTime());
    		  Calendar date = trip.getStartDate();
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
    		  String endDate = new SimpleDateFormat("yyyyMMdd").format(trip.getEndDate().getTime());
    		  String sql2 = "UPDATE trip SET dayid = '" + listToString(dayIds) + "', citycode = '" + trip.getCityCode() + "', "
    				  + "startdate = '" + start + "', enddate = '" + endDate + "' "
    				  + "WHERE tripid = '" + trip.getTripId() + "';";
    		  stmt.executeUpdate(sql2);
    		  return true;
    		  } catch (Exception e) {
    			  System.out.println(e);
    			  response.status(501);
    			  return Collections.EMPTY_MAP;
    		  } finally {
    			  if (connection != null) try{connection.close();} catch(SQLException e){}
    		  }
    	  }, new JsonTransformer());
      
      /**
       * Inputs a tripId and returns the list of dayIds that are stored in the particular trip. 
       */
      get("/trip/:tripId/dayIds", (request, response) -> {
    	  Connection connection = null;
    	  try {
    		  connection = DatabaseUrl.extract().getConnection();
    		  Statement stmt = connection.createStatement();
    		  String tripId = request.params(":tripId");
    		  String dayId = null;
    		  List<String> dayIds = new ArrayList<>();
    		  ResultSet rs = stmt.executeQuery("SELECT dayid FROM trip WHERE tripid = '" + tripId + "';");
    		  while(rs.next()) {
    			  dayId = rs.getString("dayid");
    			  dayIds = stringToList(dayId, dayIds);
    		  }
    		  return dayIds;
    	  } catch (Exception e) {
    		  return Collections.EMPTY_MAP;
    	  } finally {
    		  if (connection != null) try{connection.close();} catch(SQLException e){}
    	  }
      }, new JsonTransformer());
      
      /**
       * Inputs a city code and returns a City object including all information of a city.
       */
      get("/city/:cityCode", (request, response) -> {
    	  Connection connection = null;
    	  try {
    		  connection = DatabaseUrl.extract().getConnection();
    		  Statement stmt = connection.createStatement();
    		  String cityCode = request.params(":cityCode");
    		  ResultSet rs = stmt.executeQuery("SELECT * FROM city WHERE citycode='"+ cityCode +"';");
    		  City temp = null;
    		  while(rs.next()) {
    			  cityCode = rs.getString("citycode");
    			  String cityName = rs.getString("cityname");
    			  String state = rs.getString("state");
    			  double latitude = Double.parseDouble(rs.getString("latitude"));
    			  double longitude = Double.parseDouble(rs.getString("longitude"));
    			  temp = new City(cityCode, cityName, state, latitude, longitude);
    		  }
    		  return temp;
    	  } catch (Exception e) {
    		  return Collections.EMPTY_MAP;
    	  } finally {
    		  if (connection != null) try{connection.close();} catch(SQLException e){}
    	  }
      }, new JsonTransformer());
      
      /**
       * Inputs an email address and returns the user information.
       */
      get("/users/:email", (request, response) -> {
    	  Connection connection = null;      
          try {
          	connection = DatabaseUrl.extract().getConnection();
          	Statement stmt = connection.createStatement();       	
          	String email = request.params(":email");
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email='"+ email +"';");
          	String pw = null;
          	String firstName = null;
          	String lastName = null;
          	String ageGroup = null;
          	String preference = null;
          	List<String> preferences = new ArrayList<>();
          	String currentTripId = null;
          	List<String> currentTripIds = new ArrayList<>();
          	String pastTripId = null;
          	List<String> pastTripIds = new ArrayList<>();

          	User user = null;
          	String currentlyEditingTripId = null;
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

          	//Update currentTrip Id to past Trip Id
          	Date date = Calendar.getInstance().getTime();
            String inputDate = new SimpleDateFormat("yyyyMMdd").format(date);
          	DateFormat d = new SimpleDateFormat("yyyyMMdd");
    		Calendar today = Calendar.getInstance();
    		today.setTime(d.parse(inputDate));
          	
    		List<String> nonCurrTripIds = new ArrayList<>(); 
          	for (String tripId : currentTripIds) {
          		String endDate = null;
          		ResultSet rs1 = stmt.executeQuery("SELECT enddate as enddate FROM trip WHERE tripid = '" + tripId + "';");
          		
          		Calendar endCal = null;
          		while(rs1.next()) {
          			endDate = rs1.getString("enddate");
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
          	   	String sql = "UPDATE users SET currentTripIds = '" + listToString(user.getCurrentTripIds()) + "', pastTripIds = '" + listToString(user.getPastTripIds()) + "' where email = '" + email +"';";
          	   	stmt.executeUpdate(sql);
          	}
          	return user;
          } catch (Exception e) {
        	  return Collections.EMPTY_MAP;
          } finally {
          	if (connection != null) try{connection.close();} catch(SQLException e){}
          }
      }, new JsonTransformer());
      
      /**
       * Stores a user's information during sign up process.
       * Accepts and email address from the frontend and check 
       * the database for its availability, and if it can be used
       * signs up a user and returns true, otherwise returns false.
       */      
      post("/users/sign_up", (request, response) -> {
      	Connection connection = null;
      	try {
      		connection = DatabaseUrl.extract().getConnection();
      		Statement stmt = connection.createStatement();
      		User user = new Gson().fromJson(request.body(), User.class);
      		String sql = "SELECT email FROM users";
     			ResultSet rs = stmt.executeQuery(sql);		 
     			List<String> usernames = new ArrayList<String>();
     			while (rs.next()) {
     				usernames.add(rs.getString("email"));
     			}
     			if (!usernames.contains(user.getEmail())) {
     				sql = "INSERT INTO users (email, password, firstname, lastname, agegroup, preferencelist) "
  				+ "VALUES ('" + user.getEmail() + "', '" + user.getPassword() + "', '" + user.getFirstName() + "', "
  						+ "'" + user.getLastName() + "', '" + user.getAgeGroup() + "', '" + user.preferenceToString() + "');";
     				stmt.executeUpdate(sql);
     				response.status(200);
     				return true;
     			} else {
     				return false;
     			}   		
      	} catch (Exception e) {
      		response.status(501);
      		return Collections.EMPTY_MAP;
      	} finally {
      		if (connection != null) try{connection.close();} catch(SQLException e){} 
      	}
      }, new JsonTransformer());
      
      
      /**
       * Retrieves a user information that matches the input User object. 
       * Used for setting activity.
       */
      put("/users/setting", (request, response) -> {
      	Connection connection = null;
      	try {
      		connection = DatabaseUrl.extract().getConnection();
      		Statement stmt = connection.createStatement();
      		User user = new Gson().fromJson(request.body(), User.class);
      		String sql = "UPDATE users SET email='"+ user.getEmail() + "', password='" + user.getPassword() + "', "
      				+ "firstname='" + user.getFirstName() + "', lastname='" + user.getLastName() + "', agegroup='" + user.getAgeGroup() + "', "
  					+ "preferencelist='" + user.preferenceToString() + "' WHERE email= '" + user.getEmail() + "';";
      		stmt.executeUpdate(sql);
      		response.status(200);
      		return user;
      	} catch (Exception e) {
      		System.out.println(e);
      		response.status(501);
      		return Collections.EMPTY_MAP;
      	}
      },new JsonTransformer());
      
      
      /**
       * Inputs an email of a user and returns its first name.
       */
      get("/users/name/:email", (request, response) -> {
          Connection connection = null;      
           try {
              connection = DatabaseUrl.extract().getConnection();
              Statement stmt = connection.createStatement();
              String email = request.params(":email");
              String fName = null;
              ResultSet rs = stmt.executeQuery("SELECT firstname FROM users WHERE email = '" + email + "';");
              while(rs.next()) {
                 fName = rs.getString("firstname");
            }
              return fName;
           } catch (Exception e) {
              return Collections.EMPTY_MAP;
           } finally {
              if (connection != null) try{connection.close();} catch(SQLException e){}
           }
       }, new JsonTransformer());
      

      /**
       * Inputs the name of the city and category and
       * returns all the site information of that particular field.
       */
      get("/site/:temp", (request, response) -> {
          Connection connection = null;      
           try {
              connection = DatabaseUrl.extract().getConnection();
              ArrayList<Site> sites = new ArrayList<>();
              Statement stmt = connection.createStatement();
              String temp = request.params(":temp");
              String[] parts = new String[2];
              String delimeter = "-";
              parts = temp.split(delimeter);
              String cityCode = parts[0];
              String category = parts[1];
              Site siteInfo = null;
              ResultSet rs = stmt.executeQuery("SELECT * FROM site WHERE category = '" + category + "' AND cityCode = '" + cityCode + "';");
              while(rs.next()) {
                 String siteid = rs.getString("siteid");
                 String sitename = rs.getString("sitename");
                 double longitude = rs.getDouble("longitude");
                 double latitude = rs.getDouble("latitude");
                 String phonenumber = rs.getString("phonenumber");
                 String cate = rs.getString("category");
                 double duration = rs.getDouble("duration");
                 int monopen = rs.getInt("monopen");
                 int monclose = rs.getInt("monclose");
                 int tueopen = rs.getInt("tueopen");
                 int tueclose = rs.getInt("tueclose");
                 int wedopen = rs.getInt("wedopen");
                 int wedclose = rs.getInt("wedclose");
                 int thuopen = rs.getInt("thuopen");
                 int thuclose = rs.getInt("thuclose");
                 int friopen = rs.getInt("friopen");
                 int friclose = rs.getInt("friclose");
                 int satopen = rs.getInt("satopen");
                 int satclose = rs.getInt("satclose");
                 int sunclose = rs.getInt("sunclose");
                 int sunopen = rs.getInt("sunopen");
                 String siteurl = rs.getString("siteurl");
                 String city = rs.getString("cityCode");
                 siteInfo = new Site(siteid, sitename, latitude, longitude, phonenumber, cate, duration, monopen, monclose, tueopen, tueclose, wedopen, wedclose, thuopen, thuclose, friopen, friclose, satopen, satclose, sunopen, sunclose, siteurl, city);
                 sites.add(siteInfo);
              }
              return sites;
           } catch (Exception e) {
              return Collections.EMPTY_MAP;
           } finally {
              if (connection != null) try{connection.close();} catch(SQLException e){}
           }
      }, new JsonTransformer());
      
      /**
       * Inputs a map of events that has dayIds as a key and ArrayList of events
       * and stores it into the database. If everything goes well, returns true! else returns false.
       */
      post("/event/map/:email", (request, response) -> {
    	  Connection connection = null;
    	  Gson gson = new GsonBuilder().create();
          try {
             connection = DatabaseUrl.extract().getConnection();
             Statement stmt = connection.createStatement();
             String temp = request.body();
                          
             Type type = new TypeToken<Map<String, ArrayList<Event>>>(){}.getType();
             Map<String, ArrayList<Event>> eventMap = gson.fromJson(temp, type);
             ResultSet rs1 = stmt.executeQuery("SELECT COUNT(eventId) AS total FROM event;");
             int count = 0;
             while(rs1.next()) {
            	 count = rs1.getInt("total");
             }
             ArrayList<String> keyList = new ArrayList<>();
             for (String s:eventMap.keySet()) {
            	 keyList.add(s);
             }
             
             
             for (int i = 0; i < eventMap.size(); i++) {
                 ArrayList<Event> eventList = new ArrayList<>();
            	 eventList = eventMap.get(keyList.get(i));
            	 ArrayList<String> eventIds = new ArrayList<>();
            	 for (Event event : eventList) {
            		 String str = "e" + (count + 1);
            		 String sql1 = "INSERT INTO event(eventId, dayId, siteId, duration, startTime, endTime) VALUES ('" + str + "', '" + keyList.get(i) + "', '"
    			  + event.getSiteId() + "', '" + event.getDuration() + "', '" + event.getStartTime() + "', '" + event.getEndTime() + "');";
            		 stmt.executeUpdate(sql1);
            		 eventIds.add(str);
            		 count++;
            	 }
         		 String sql2 = "UPDATE day SET eventid = '" + listToString(eventIds) + "' WHERE dayid = '" + keyList.get(i) + "';";
         		 stmt.executeUpdate(sql2);        		 
             }

             //Update currentlyEditingTripID and currentTripIds
             String email = request.params(":email");

             String pw = null;
             String firstName = null;
           	 String lastName = null;
           	 String ageGroup = null;
           	 List<String> preferences = new ArrayList<>();
           	 String currentTripId = null;
           	 List<String> currentTripIds = new ArrayList<>();
           	 String preference = null;
           	 User user = null;
           	 String currentlyEditingTripId = null;        
             ResultSet rs2 = stmt.executeQuery("SELECT * FROM users WHERE email='"+ email +"';");
             while(rs2.next()) {
            	 pw = rs2.getString("password");
           		 lastName = rs2.getString("lastname");
           		 firstName = rs2.getString("firstname");
           		 ageGroup = rs2.getString("agegroup");
      			 preference= rs2.getString("preferencelist");
      			 preferences = stringToList(preference, preferences);
           		 user = new User(email, firstName, lastName, pw, ageGroup, preferences);
           		 
           		 currentlyEditingTripId = rs2.getString("currentlyeditingtripid");
           		 user.setCurrentlyEditingTripId(currentlyEditingTripId);
           		           		
           		 currentTripId = rs2.getString("currenttripids");
           		 currentTripIds = stringToList(currentTripId, currentTripIds);
           		 currentTripIds.add(currentlyEditingTripId);
             }
             String sql3 = "UPDATE users SET currentlyeditingtripid = " + null + ", currenttripids = '" + listToString(currentTripIds) + "' WHERE email = '" + email + "';";      
             stmt.executeUpdate(sql3);
             return true;
          } catch (Exception e) {
             return Collections.EMPTY_MAP;
          } finally {
             if (connection != null) try{connection.close();} catch(SQLException e){}
          }
      }, new JsonTransformer());
      
      
      /**
       * Inputs a map of updated events that has dayIds as a key and ArrayList of events
       * and stores updated event info into the database. If everything goes well, returns true! else returns false.
       */
      put("/event/map", (request, response) -> {
    	  Connection connection = null;
    	  Gson gson = new GsonBuilder().create();
          try {
             connection = DatabaseUrl.extract().getConnection();
             Statement stmt = connection.createStatement();
             String temp = request.body();
             System.out.println(temp);             
             Type type = new TypeToken<Map<String, ArrayList<Event>>>(){}.getType();
             Map<String, ArrayList<Event>> eventMap = gson.fromJson(temp, type);
            
             ResultSet rs1 = stmt.executeQuery("SELECT COUNT(eventId) AS total FROM event;");
             int count = 0;
             while(rs1.next()) {
            	 count = rs1.getInt("total");
             }

             ArrayList<String> keyList = new ArrayList<>();
             for (String s:eventMap.keySet()) {
            	 keyList.add(s);
             }

             //compare the before and after
             for (int i = 0; i < eventMap.size(); i++) {
                 ArrayList<Event> eventList = new ArrayList<>();
            	 eventList = eventMap.get(keyList.get(i));
            	 ArrayList<String> eventIds = new ArrayList<>();
            	 for (Event event : eventList) {
            		 String sql1;
            		 String eventId;
            		 //check if the event is newly created or not.
            		 if (event.getEventId().equals("new")) {
            			 //create a new event
                		 eventId = "e" + (count + 1);
                		 sql1 = "INSERT INTO event(eventId, dayId, siteId, duration, startTime, endTime) VALUES ('" + eventId + "', '" + keyList.get(i) + "', '"
                				 + event.getSiteId() + "', '" + event.getDuration() + "', '" + event.getStartTime() + "', '" + event.getEndTime() + "');";
                   		 count++;                   		             			 
            		 } else {            			 
                   		//update the old event
	           			eventId = event.getEventId();
	           			sql1 = "UPDATE event SET dayId = '" + event.getDayId() + "', siteId = '" + event.getSiteId() + "', duration = '" + event.getDuration() 
	           				 + "', startTime = '" + event.getStartTime() + "', endTime = '" + event.getEndTime() + "' WHERE eventId = '" + eventId + "';";
            		 }           		 
            		 stmt.executeUpdate(sql1);
            		 eventIds.add(eventId);
            	 }
         		 String sql2 = "UPDATE day SET eventid = '" + listToString(eventIds) + "' WHERE dayid = '" + keyList.get(i) + "';";
         		 stmt.executeUpdate(sql2);        	
             }            
             return true;
          } catch (Exception e) {
             return Collections.EMPTY_MAP;
          } finally {
             if (connection != null) try{connection.close();} catch(SQLException e){}
          }
      }, new JsonTransformer());
      
      /**
       * With selected trip from user, return all corresponding trip information to user as a current trip.
       */
      get("/trip/:tripId/eventList", (request, response) -> {
          Connection connection = null;      
          try {
             connection = DatabaseUrl.extract().getConnection();
             Statement stmt = connection.createStatement();
             String tripId = request.params(":tripId");
             Map<String, Map<String, List<Event>>> totalMap = new HashMap<>();
             
             // Get all information for selected trip
             Map<String, List<Event>> currTripmap = new HashMap<>();
             List<String> dayIdList = new ArrayList<>();
             String sql1 = "SELECT dayid FROM trip WHERE tripid = '" + tripId + "';";
             ResultSet rs1 = stmt.executeQuery(sql1);
             while (rs1.next()) {
            	 String temp = rs1.getString("dayid");
            	 dayIdList = stringToList(temp, dayIdList);
             }
             for (String dayId : dayIdList) {
            	 List<Event> eventList = new ArrayList<>();
            	 List<String> eventIdList = new ArrayList<>();
            	 String sql2 = "SELECT eventid FROM day WHERE dayid = '" + dayId + "';";
            	 ResultSet rs2 = stmt.executeQuery(sql2);
            	 while (rs2.next()) {
            		 String temp2 = rs2.getString("eventid");
            		 eventIdList = stringToList(temp2, eventIdList);
            	 }
            	 for (String eventId : eventIdList) {
            		 String sql3 = "SELECT siteid, starttime, endtime FROM event WHERE eventid = '" + eventId + "';";
            		 ResultSet rs3 = stmt.executeQuery(sql3);
            		 String siteId = null;
//            		 double duration = 0;
            		 int starttime = 0;
            		 int endtime = 0;
            		 while (rs3.next()) {
            			 siteId = rs3.getString("siteid");
//            			 duration = rs3.getDouble("duration");
            			 starttime = rs3.getInt("starttime");
            			 endtime = rs3.getInt("endtime");
            		 }
            		 String sql4 = "SELECT sitename, latitude, longitude FROM site WHERE siteid = '" + siteId + "';";
            		 ResultSet rs4 = stmt.executeQuery(sql4);
            		 String siteName = null;
            		 double latitude = 0;
            		 double longitude = 0;
            		 while (rs4.next()) {
            			 siteName = rs4.getString("sitename");
            			 latitude = rs4.getDouble("latitude");
            			 longitude = rs4.getDouble("longitude");
            		 }
            		 Event event = new Event(eventId, dayId, siteId, siteName, latitude, longitude, starttime, endtime);
            		 eventList.add(event);
            	 }
            	 currTripmap.put(dayId, eventList);
             }
             
             // Get City information for selected trip
             String sql5 = "SELECT citycode FROM trip WHERE tripid = '" + tripId + "';";
             ResultSet rs5 = stmt.executeQuery(sql5);
             String cityCode = null;
             while (rs5.next()) {
            	 cityCode = rs5.getString("citycode");
             }
             
             City selectedCity = null;
             String sql6 = "SELECT * FROM city WHERE citycode = '" + cityCode + "';";
             ResultSet rs6 = stmt.executeQuery(sql6);
             String cityName = null;
             String state = null;
             double latitude = 0;
             double longitude = 0;
             while (rs6.next()) {
            	 cityName = rs6.getString("cityname");
            	 state = rs6.getString("state");
            	 latitude = rs6.getDouble("latitude");
            	 longitude = rs6.getDouble("longitude");
             }

             selectedCity = new City(cityCode, cityName, state, latitude, longitude);
             Gson gson = new Gson();
             String key = gson.toJson(selectedCity);
             
             totalMap.put(key, currTripmap);
             return totalMap;
          } catch (Exception e) {
             return Collections.EMPTY_MAP;
          } finally {
             if (connection != null) try{connection.close();} catch(SQLException e){}
          }
      }, new JsonTransformer());      
      
      /**
       * Inputs an email of a user and returns the list of current trips.
       */
      get("/users/:email/currentTrips", (request, response) -> {
          Connection connection = null;      
           try {
              connection = DatabaseUrl.extract().getConnection();
              Statement stmt = connection.createStatement();
              String email = request.params(":email");
              String temp = null;
              List<String> currtripIds = new ArrayList<>();
              ResultSet rs = stmt.executeQuery("SELECT currenttripids FROM users WHERE email = '" + email + "';");
              while(rs.next()) {
                 temp = rs.getString("currenttripids");
                 currtripIds = stringToList(temp, currtripIds);
              }
              List<Trip> currTripList = new ArrayList<>();
              String dayId = null;
              List<String> dayIds = new ArrayList<>(); 
              String cityCode = null;
              String startDate = null;
              String endDate = null;
              for (String s : currtripIds) {
            	  ResultSet rs1 = stmt.executeQuery("SELECT * FROM trip WHERE tripid = '" + s + "';");
            	  Trip t = null;
            	  while(rs1.next()) {
            		  cityCode = rs1.getString("citycode");
            		  dayId = rs1.getString("dayid");
            		  dayIds = stringToList(dayId, dayIds);
            		  startDate = rs1.getString("startdate");
            		  endDate = rs1.getString("enddate");
            		  t = new Trip(s, cityCode, dayIds, startDate, endDate);
            	  }
            	  currTripList.add(t);
              }
              return currTripList;
           } catch (Exception e) {
              return Collections.EMPTY_MAP;
           } finally {
              if (connection != null) try{connection.close();} catch(SQLException e){}
           }
       }, new JsonTransformer());
      
      
      /**
       * Inputs an email of a user and returns the list of current trips.
       */
      get("/users/:email/pastTrips", (request, response) -> {
          Connection connection = null;      
           try {
              connection = DatabaseUrl.extract().getConnection();
              Statement stmt = connection.createStatement();
              String email = request.params(":email");
              String temp = null;
              List<String> pasttripIds = new ArrayList<>();
              ResultSet rs = stmt.executeQuery("SELECT pasttripids FROM users WHERE email = '" + email + "';");
              while(rs.next()) {
                 temp = rs.getString("pasttripids");
                 pasttripIds = stringToList(temp, pasttripIds);
              }
              List<Trip> pastTripList = new ArrayList<>();
              String dayId = null;
              List<String> dayIds = new ArrayList<>(); 
              String cityCode = null;
              String startDate = null;
              String endDate = null;
              for (String s : pasttripIds) {
            	  ResultSet rs1 = stmt.executeQuery("SELECT * FROM trip WHERE tripid = '" + s + "';");
            	  Trip t = null;
            	  while(rs1.next()) {
            		  cityCode = rs1.getString("citycode");
            		  dayId = rs1.getString("dayid");
            		  dayIds = stringToList(dayId, dayIds);
            		  startDate = rs1.getString("startdate");
            		  endDate = rs1.getString("enddate");
            		  t = new Trip(s, cityCode, dayIds, startDate, endDate);
            	  }
            	  pastTripList.add(t);
              }
              return pastTripList;
           } catch (Exception e) {
              return Collections.EMPTY_MAP;
           } finally {
              if (connection != null) try{connection.close();} catch(SQLException e){}
           }
       }, new JsonTransformer());
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

	/**
	 * Method to connect the database and server.
	 * @return the result of the connection
	 * @throws URISyntaxException 
	 * @throws SQLException
	 */
	private static Connection getConnection() throws URISyntaxException, SQLException {
	    URI dbUri = new URI(System.getenv("DATABASE_URL"));
	    int port = dbUri.getPort();
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + dbUri.getPath();
	    if (dbUri.getUserInfo() != null) {
	    	String username = dbUri.getUserInfo().split(":")[0];
	    	String password = dbUri.getUserInfo().split(":")[1];
	    		return DriverManager.getConnection(dbUrl, username, password);
	    } else {
		    	return DriverManager.getConnection(dbUrl);
	    }
	}
	

	
}
