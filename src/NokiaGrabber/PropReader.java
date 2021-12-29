package NokiaGrabber;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.text.SimpleDateFormat;
import Tools.Encriptor;

public class PropReader {
	//Var
	String DB_Host, DB_Username, DB_Password, DB_Port, DB_Name;
	String Ssh_Username, Ssh_Host, Ssh_Password, Protocol;
	String Auto_Login, LogFolder, LogDateFormat,LogFileName, NeList, CommandList, MultiThreading;
	int Login_Attempt, Ssh_Port, MaxThread;
	Encriptor encriptor = new Encriptor();
	

  private void getIt(String config){
    Properties prop = new Properties();
    String fileName = config;
    try { 
	    prop.load(new FileInputStream(fileName)); 
    } catch (IOException e) { System.out.println("Do you have the config file?\n"); System.exit(0);}
                  
    DB_Host 		= 	prop.getProperty("DB_Host");
    DB_Username 	= 	prop.getProperty("DB_Username"); 
    DB_Password 	= 	prop.getProperty("DB_Password");
    DB_Port 		= 	prop.getProperty("DB_Port");
    DB_Name 		= 	prop.getProperty("DB_Name");		
	Protocol 		= 	prop.getProperty("Protocol");	    
    Ssh_Username 	= 	prop.getProperty("Ssh_Username");
    Ssh_Password 	= 	prop.getProperty("Ssh_Password");
    Ssh_Host 		= 	prop.getProperty("Ssh_Host");    
	Auto_Login 		= 	prop.getProperty("Auto_Login");
	LogFolder 		= 	prop.getProperty("LogFolder");
	LogDateFormat 	= 	prop.getProperty("LogDateFormat");
	NeList 			= 	prop.getProperty("NeList");
	CommandList 	= 	prop.getProperty("CommandList");
	LogFileName 	= 	prop.getProperty("LogFileName");
	
    try{
    	MaxThread=Integer.parseInt(prop.getProperty("MaxThread"));
    }catch(NumberFormatException e)
	{MaxThread=1;}
    
	MultiThreading 	= 	prop.getProperty("MultiThreading");
	
	
    try{
        Login_Attempt=Integer.parseInt(prop.getProperty("Login_Attempt"));
    }catch(NumberFormatException e)
	{Login_Attempt=1;}

    try{
    	Ssh_Port=Integer.parseInt(prop.getProperty("Ssh_Port"));
    }catch(NumberFormatException e)
	{Ssh_Port=22;}
    
    try {
		new SimpleDateFormat(LogDateFormat);				
	}
    catch(Exception e)
	{
    	System.out.println("Wrong date Format Configuration!!");
    	LogDateFormat= "yyyyMMdd"; 
    }

  }
  
  public PropReader(String config){
  	  getIt(config);
  }
  
  String getDB_Host(){
	if (DB_Host==null)
	  DB_Host="localhost";
	  return DB_Host;
  }
  
  String getDB_Username(){
	if (DB_Username==null)
	  DB_Username="root";
	  return DB_Username;
  }
  
  String getDB_Password(){
	if (DB_Password==null)
	  DB_Password="root";
	  return DB_Password;
  }
  
  String getDB_Port(){
	if (DB_Port==null)
	  DB_Port="3306";
	  return DB_Port;
  }
  
  String getDB_Name(){
	if (DB_Name==null)
	  DB_Name="traversa2_ph1";	  
	  return DB_Name;
  }
  
  String getSsh_Username(){
  	if (Ssh_Username==null)
	  Ssh_Username="teezdumk";
	  return Ssh_Username;
  }
  
  String getSsh_Password(){ 
	  String pwd=null;
  	if (Ssh_Password!=null)
  		pwd = encriptor.Dekrip(Ssh_Password);
  		if (pwd!=null)
  			{return pwd;}
	return "password";
  }

  String getMultiThreading(){
	  	if (MultiThreading==null)
	  		{
	  		MultiThreading="no";
	  		this.MaxThread=1;
	  		}else
	  	if (!MultiThreading.trim().equalsIgnoreCase("yes"))	
	  		this.MaxThread=1;
	  	
		  return MultiThreading.trim();
	  }  
  
  String getSsh_Host(){
	  	if (Ssh_Host==null)
		  Ssh_Host="localhost";	  
		  return Ssh_Host;
  } 
  
  String getAuto_Login(){
	if (Auto_Login==null)
	  Auto_Login="no";	
	  return Auto_Login;
  }

  String getProtocol(){
	if (Protocol==null)
	  Protocol="SSH";
	  return Protocol;
  }
  
  String getLogFolder(){
		if (LogFolder==null)
			LogFolder="raws";
		  return LogFolder;
  }
  
  String getLogFileName(){
		if (LogFileName==null)
			LogFileName="_";
		  return "_"+LogFileName+"_";
  }
  String getLogDateFormat(){
		if (LogDateFormat==null)
			LogDateFormat="xxx";
		return LogDateFormat;
  }
  
  String getNeList(){
		if (NeList==null)
			NeList="Ne.lst";
		  return NeList;
  }
  
  String getCommandList(){
		if (CommandList==null)
			CommandList="Command.lst";
		  return CommandList;
  }	
  
  int getLogin_Attempt(){
      return Login_Attempt;
  }
  
  int getSsh_Port(){
      return Ssh_Port;
  }

  int getMaxThread(){
	  if (MaxThread>8)
		  MaxThread=8;
      return MaxThread;
  }
}
