package NokiaGrabber;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlLogging {

    static Connection con = null;
    static logger log = new logger();
    //logger log = new logger();
    static PropReader configuraton = new PropReader("Grabber.cfg");
	
    public  MysqlLogging(){
		}
	
    public static void loadDriver() {
    String DB_Host = configuraton.getDB_Host();
    String DB_Username = configuraton.getDB_Username();
    String DB_Password = configuraton.getDB_Password();
    String DB_Port = configuraton.getDB_Port();
    String DB_Name = configuraton.getDB_Name();
    
    String url="jdbc:mysql://"+DB_Host+":"+DB_Port+"/"+DB_Name;
	
    log.write("loading driver ...");    
    		try {
    		Class.forName("com.mysql.jdbc.Driver");
    		//con=DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root", "root");
    		con=DriverManager.getConnection(url, DB_Username, DB_Password);
    		if (con.isValid(0))
                    log.write("Done.");else
                	log.write("Driver can't be loadded");
			} catch (Exception e){
			log.write("Mysql Error: " + e.getMessage());
			System.exit(1);
			}
	}

 
	public static void CloseConnection() {
         try {
               if(con != null)
               con.close();
             } catch(SQLException e) {
             log.write ("SQL Exception on closing mysql con "+ e.getMessage());
             }
    }

	public static void ExecuteQuery (String Query) {
	try {      
        Statement st = con.createStatement();
		st.execute(Query);
        st.close();		
        } catch(Exception e) {
            log.write("Mysql Error: " + e.getMessage());
        }
    }
	
	public static void MysqlSaveLog (String Time, String Status, String NE, String Message) {
		try {      
	        Statement st = con.createStatement();
	        
			st.execute("INSERT INTO GRAB_LOGGER (`TIME`, `STATUS`, `NE`, `MESSAGE`) VALUES" +
					"('"+Time+"', '"+Status+"', '"+NE+"', '"+Message+"')");
	        st.close();
	        } catch(Exception e) {
	            log.write("Mysql Error: " + e.getMessage());
	        }
	 }
	
}
