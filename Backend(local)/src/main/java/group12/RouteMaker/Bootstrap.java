package group12.RouteMaker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

import static spark.Spark.*;
import static spark.SparkBase.staticFileLocation;

import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.jdbc2.optional.SimpleDataSource;
import org.sqlite.SQLiteDataSource;


/**
 * Hello world!
 *
 */
public class Bootstrap {
	public static final String IP_ADDRESS = "localhost";
	public static final int PORT = 4321;
	
	public static void main( String[] args ) throws Exception {
		
		//Properties properties = configureDataSource();
		String dbUrl = configureDataSource();
		if (dbUrl == null) {
			System.out.printf("Could not find RM.db in the current directory (%s). Terminating\n",
                    Paths.get(".").toAbsolutePath().normalize());
            System.exit(1);
		}
		
		//Specify the IP address and Port at which the server should be run
		ipAddress(IP_ADDRESS);
		port(PORT);
		
		//Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");
		
        //Create the model instance and then configure and start the web service
        try {
            RMService model = new RMService(dbUrl);
            new RMController(model);
        } catch (RMService.RMServiceException ex) {
            //logger.error("Failed to create a TodoService instance. Aborting");
        }
    }
		

	/**
	 * Check if the database file exists in the current directory. It it does exists create a 
	 * DataSource instance for the file and return it.
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	private static String configureDataSource() throws ClassNotFoundException, SQLException {
		Path rmPath = Paths.get(".", "RM.db");
		if (!(Files.exists(rmPath))) {
			try { Files.createFile(rmPath); }
			catch (java.io.IOException ex) {
				System.err.println("Failed to create RM.db file in current directory. Aborting");
			}			
		}
		String dbUrl = "jdbc:postgresql:rm";  
		return dbUrl;
		
//		SQLiteDataSource dataSource = new SQLiteDataSource();
//        dataSource.setUrl("jdbc:postgres://localhost:5432/rm");
//        return dataSource;

//		dataSource.setUrl("jdbc:postgres://localhost:5432/RM");
		
		
//		Properties props = new Properties();
//		props.setProperty("user", "postgres");
//		props.setProperty("password", "sing0627");

		
		
//		Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/rm", props);
		//Statement stmt = c.createStatement();     
//		stmt.executeUpdate("DROP DATABASE RM");
		//stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS testdb WITH ENCODING='UTF8' CONNECTION LIMIT=-1;");
		//stmt.close();

		
	}
}
