package group12.RouteMaker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class TestRmServer {
	private String dbUrl = "jdbc:postgresql:rm";
	private String userName = "postgres";
	private String password = "sing0627";
	

	//**************************//
	//			Set Up			//
	//**************************//

	@Before
	public void before() throws Exception {
		//Clear the database and then start the server
		clearDB();
		
		//Start the main server
		Bootstrap.main(null);
		System.out.println("helloworld");
		Spark.awaitInitialization();
	}

	@After
	public void tearDown() throws SQLException {
		//Strop the server
		clearDB();
		Spark.stop();
	}

	//**************************//
	//			Tests			//
	//**************************//
	@Test
	public void testCreateUser() throws Exception {
		System.out.println("================= TEST CREAT USER =================");
		List<String> pref_1 = new ArrayList<>();
		pref_1.add("Museums");
		pref_1.add("Art");
		pref_1.add("NightLife");
		
		List<String> pref_2 = new ArrayList<>();
		pref_2.add("Entertainment");
		pref_2.add("Landmarks");
		
		List<String> pref_3 = new ArrayList<>();
		pref_3.add("Food");
		
		
		User[] entries = new User[] {
			new User("jason.mraz@gmail.com", "jason", "mraz", "123", "30s", pref_1),
			new User("tom.cruse@gmail.com", "tom", "cruse", "456", "40s", pref_2)
		};

		//Try to enter valid user input
		for (User u : entries) {
			Response radd = request("POST", "/api/v1/users/sign_up", u);
			System.out.println(radd.httpStatus);
			assertEquals("Failed to create user", 200, radd.httpStatus);
			assertEquals("Failed to return true", "true", radd.content);
		}
		
		//Try to enter invalid(already registered email address)
		User re_user = new User("tom.cruse@gmail.com", "tommy", "cruise", "789", "40s", pref_3);
		Response radd = request("POST", "/api/v1/users/sign_up", re_user);
		assertEquals("Failed to catch hanle", 200, radd.httpStatus);
		assertEquals("Failed to return false", "false", radd.content);

		//Get Users back
		List<User> results = new ArrayList<>();
		Response r = request("GET", "/api/v1/users/jason.mraz@gmail.com", null);
		assertEquals("Failed to get user", 200, r.httpStatus);
		results.add(r.getContentAsObject(User.class));
		
		r = request("GET", "/api/v1/users/tom.cruse@gmail.com", null);
		assertEquals("Failed to get user", 200, r.httpStatus);
		results.add(r.getContentAsObject(User.class));
		
		for (int i = 0; i < results.size(); i++) {
			User actual = results.get(i);
			assertEquals(String.format("Index %d: Missmatch in email", i), entries[i].getEmail(), actual.getEmail());
			assertEquals(String.format("Index %d: Missmatch in firstName", i), entries[i].getFirstName(), actual.getFirstName());
			assertEquals(String.format("Index %d: Missmatch in lastName", i), entries[i].getLastName(), actual.getLastName());
			assertEquals(String.format("Index %d: Missmatch in password", i), entries[i].getPassword(), actual.getPassword());
			assertEquals(String.format("Index %d: Missmatch in ageGroup", i), entries[i].getAgeGroup(), actual.getAgeGroup());
			assertEquals(String.format("Index %d: Missmatch in preferenceList", i), entries[i].getPreferenceList(), actual.getPreferenceList());
		}
	}
	
	@Test
	public void testUpdateUser() throws Exception {
		System.out.println("================= TEST UPDATE USER =================");
		List<String> pref_1 = new ArrayList<>();
		pref_1.add("Museums");
		pref_1.add("Art");
		pref_1.add("NightLife");
		
		List<String> pref_2 = new ArrayList<>();
		pref_2.add("Entertainment");
		pref_2.add("Landmarks");
		
		List<String> pref_3 = new ArrayList<>();
		pref_3.add("Food");
		
		
		User[] entries = new User[] {
			new User("jason.mraz@gmail.com", "jason", "mraz", "123", "30s", pref_1),
			new User("tom.cruse@gmail.com", "tom", "cruse", "456", "40s", pref_2)
		};

		//Try to enter valid user input
		for (User u : entries) {
			Response radd = request("POST", "/api/v1/users/sign_up", u);
			assertEquals("Failed to create user", 200, radd.httpStatus);
			assertEquals("Failed to return true", "true", radd.content);
		}
		
		//Update userInformation
		entries[0].setPassword("135");
		entries[0].setAgeGroup("20s or below");
		List<String> pref_4 = new ArrayList<>();
		pref_4.add("Museums");
		pref_4.add("Food");
		entries[0].setPreferenceList(pref_4);
		
		entries[1].setPassword("dne");
		entries[1].setAgeGroup("50s");
		List<String> pref_5 = new ArrayList<>();
		pref_5.add("Art");
		pref_5.add("Landmarks");
		entries[1].setPreferenceList(pref_5);
		
		for (User u : entries) {
			Response radd = request("PUT", "/api/v1/users/setting", u);
			assertEquals("Failed to create user", 200, radd.httpStatus);
		}
		
		//Get users back and check if the information is updated
		List<User> results = new ArrayList<>();
		Response r = request("GET", "/api/v1/users/jason.mraz@gmail.com", null);
		assertEquals("Failed to get user", 200, r.httpStatus);
		results.add(r.getContentAsObject(User.class));
		
		r = request("GET", "/api/v1/users/tom.cruse@gmail.com", null);
		assertEquals("Failed to get user", 200, r.httpStatus);
		results.add(r.getContentAsObject(User.class));
		
		for (int i = 0; i < results.size(); i++) {
			User actual = results.get(i);
			assertEquals(String.format("Index %d: Missmatch in email", i), entries[i].getEmail(), actual.getEmail());
			assertEquals(String.format("Index %d: Missmatch in firstName", i), entries[i].getFirstName(), actual.getFirstName());
			assertEquals(String.format("Index %d: Missmatch in lastName", i), entries[i].getLastName(), actual.getLastName());
			assertEquals(String.format("Index %d: Missmatch in password", i), entries[i].getPassword(), actual.getPassword());
			assertEquals(String.format("Index %d: Missmatch in ageGroup", i), entries[i].getAgeGroup(), actual.getAgeGroup());
			assertEquals(String.format("Index %d: Missmatch in preferenceList", i), entries[i].getPreferenceList(), actual.getPreferenceList());
		}		
	}
	
	@Test
	public void createNewTrip() throws Exception {
		System.out.println("================= TEST CREAT NEW TRIP ID =================");
		List<String> pref_1 = new ArrayList<>();
		pref_1.add("Museums");
		pref_1.add("Art");
		pref_1.add("NightLife");
		
		List<String> pref_2 = new ArrayList<>();
		pref_2.add("Entertainment");
		pref_2.add("Landmarks");
		
		List<String> pref_3 = new ArrayList<>();
		pref_3.add("Food");
		
		
		User[] entries = new User[] {
			new User("jason.mraz@gmail.com", "jason", "mraz", "123", "30s", pref_1),
			new User("tom.cruse@gmail.com", "tom", "cruse", "456", "40s", pref_2)
		};

		//Try to enter valid user input
		for (User u : entries) {
			Response radd = request("POST", "/api/v1/users/sign_up", u);
			assertEquals("Failed to create user", 200, radd.httpStatus);
			assertEquals("Failed to return true", "true", radd.content);
		}

		//Create a new user ID and changes user's currentlyEditingId
		for (int i = 0; i < entries.length; i++) {
			Response res = request("POST", "/api/v1/trip/current/"+entries[i].getEmail(), null);
			assertEquals("Failed to create a new TripId", 200, res.httpStatus);
			assertEquals("Failed to generate a currTripId", "\"t" + (i+1) + "\"", res.content);
		}			
		
		//Check if user's currentlyEditingId is changed
		//Get users back and check if the information is updated
		List<User> results = new ArrayList<>();
		Response r = request("GET", "/api/v1/users/jason.mraz@gmail.com", null);
		assertEquals("Failed to get user", 200, r.httpStatus);
		User a = r.getContentAsObject(User.class);
		assertEquals("Failed to update user's currentlyEditingTripId", "t1", a.getCurrentlyEditingTripId()); 
		
		r = request("GET", "/api/v1/users/tom.cruse@gmail.com", null);
		assertEquals("Failed to get user", 200, r.httpStatus);
		results.add(r.getContentAsObject(User.class));
		User b = r.getContentAsObject(User.class);
		assertEquals("Failed to update user's currentlyEditingTripId", "t2", b.getCurrentlyEditingTripId());		
	}
	
	@Test
	public void createDay() throws Exception {
		
	}
	
	
	//************************************//
	// Generic Helper Methods and classes //
	//************************************//
	
    private Response request(String method, String path, Object content) {
        try {
			URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, path);
            System.out.println(url);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoInput(true);
            if (content != null) {
                String contentAsJson = new Gson().toJson(content);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter output = new OutputStreamWriter(http.getOutputStream());
                
                output.write(contentAsJson);
                output.flush();
                output.close();
            }

            String responseBody = IOUtils.toString(http.getInputStream());
			return new Response(http.getResponseCode(), responseBody);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}
    }

        
    private static class Response {

		public String content;
        
		public int httpStatus;

		public Response(int httpStatus, String content) {
			this.content = content;
            this.httpStatus = httpStatus;
		}

        public <T> T getContentAsObject(Type type) {
            return new Gson().fromJson(content, type);
        }
	}
	
    
    //****************************************************//
  	// RouteMaker app specific Helper Methods and classes //
  	//****************************************************//
    private void clearDB() {
        try (Connection conn = DriverManager.getConnection(dbUrl, userName, password)){
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS users";
            stmt.executeUpdate(sql);
            String sql1 = "DROP TABLE IF EXISTS trip";
            stmt.executeUpdate(sql1);
            String sql2 = "DROP TABLE IF EXISTS day";
            stmt.executeUpdate(sql2);
            String sql3 = "DROP TABLE IF EXISTS event";
            stmt.executeUpdate(sql3);            
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
    }        
}
