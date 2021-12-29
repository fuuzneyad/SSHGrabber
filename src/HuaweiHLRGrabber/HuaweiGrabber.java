package HuaweiHLRGrabber;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.*;

public class HuaweiGrabber {
	private String argument;
	private String localpath;
	private static final Logger Log = Logger.getLogger("GLOBAL");
	
	public HuaweiGrabber(String argument){
		this.argument=argument;
	}

	
	private void startit(){
		try {
			String configFile="properties/HuaweiHLRConfig.cfg";
			Properties prop = new Properties();
		    try {
		    	prop.load(new FileInputStream(configFile)); 	
		    }catch (IOException e) { System.out.println("Ga ada "+configFile+" file?\n"); System.exit(0);}
		    
		   
		    String nodelist=prop.getProperty("NODELIST_FILE");
		    String nodepath=prop.getProperty("NODE_FILELOC_PARRENT");
		    String nodefilename=prop.getProperty("NODE_FILENAME");
		    String username=prop.getProperty("NODE_USERNAME");
		    String password=prop.getProperty("NODE_PASSWORD");
		    localpath=prop.getProperty("LOCAL_FILELOC");
		    
		      FileInputStream Fis = new FileInputStream(nodelist);
		      DataInputStream in = new DataInputStream(Fis);
		      BufferedReader br = new BufferedReader(new InputStreamReader(in));
		      String StrLine;
		      while ((StrLine = br.readLine()) != null )   {
			      if (!"".equalsIgnoreCase(StrLine.trim()) && StrLine.contains("|")){		 
			    	  String[] Splitted=StrLine.split("\\|");
		    		  if(Splitted.length==4){
		    			  String ne_name=Splitted[0];
		    			  String location_name=Splitted[1];
		    			  String node_ip=Splitted[2];
		    			  String node_task=Splitted[3];
		    			  
		    			  Log.debug("trying getting data for :"+ne_name+"-"+location_name+"("+node_ip+")");
		    			  getData(username, node_ip, password, nodepath+"/"+node_task, nodefilename, ne_name);
		    		  }else
		    			  Log.error("Uncomplete nodelist format: "+StrLine +" Must be "+Splitted.length+" delimeted \"|\"");
			      }
		      }
		      
		      br.close();
		      in.close();
		      
		    }catch (Exception e){
		    	
		    }
	}
	private void getData(String username, String host, String pass, String path, String filename, String ne_name){
		   JSch jsch = null;
	       Session session = null;
	       Channel channel = null;
	       ChannelSftp c = null;
	       try {
	           jsch = new JSch();
	           session = jsch.getSession(username, host, 22);
	           session.setPassword(pass);
	           session.setConfig("StrictHostKeyChecking", "no");
	           session.connect();
	           
	           channel = session.openChannel("sftp");
	           Log.debug("Connecting to :"+ne_name+"("+host+")");
	           channel.connect();
	           Log.debug("Connected.");
	           c = (ChannelSftp) channel;

	       } catch (Exception e) { 	Log.error(e.getMessage());	}

	       try {
	    	   c.cd(path);
	    	   @SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list= c.ls("*"+filename+"*");
	    	   for (ChannelSftp.LsEntry theList: list){
	    		   if (theList.getFilename().contains(argument) || argument.equalsIgnoreCase("all")){
	    			   Log.debug("Getting File(s)=>"+theList.getFilename());	  
	    			   c.get(theList.getFilename(),localpath+"/"+ne_name+"_"+theList.getFilename());
	    		   }
	    	   }
	       } catch (Exception e) {	Log.error(e.getMessage());	}
	       
	       c.disconnect();
	       session.disconnect();
	}
	
	
	public static void main(String[] args) {
		String argument=null;
		if(args.length>0)
			argument=args[0];else
			{
				System.out.println("You must specify the pattern argument [ALL | [filepattern]]..");
				System.exit(1);
			}
    
	    HuaweiGrabber gbr= new HuaweiGrabber(argument);
	    gbr.startit();
	    
	    
	}

}
