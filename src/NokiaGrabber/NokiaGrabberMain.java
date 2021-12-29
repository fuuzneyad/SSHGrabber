package NokiaGrabber;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;

public class NokiaGrabberMain {

	private static logger log = new logger();
	public final ExecutorService poolRef;
	public static int maxThread = 1;
	private static PropReader conf;
//	private static Session session;
	private final ThreadTracker threadTracker;

	
public NokiaGrabberMain(ThreadTracker threadTracker, ExecutorService poolRef/*, Session Session*/){
	this.poolRef=poolRef;
	this.threadTracker = threadTracker;
//	this.session=session;
}	
 public static void main(String[] arg){
     try{
     String config=null;
     if(arg.length>=1)
    	config = arg[0];else
    	config =  "Grabber.cfg";
     conf = new PropReader(config);  
     
     DateFormat tanggal = new SimpleDateFormat (conf.getLogDateFormat());
     String sekarang = tanggal.format(new Date());
     
     conf.getMultiThreading();
     maxThread=conf.getMaxThread();
   		 
     JSch jsch=new JSch();
	  //jsch.setKnownHosts("/home/traversa/.ssh/known_hosts");	
	 final Session session=jsch.getSession(conf.getSsh_Username(), 
			  conf.getSsh_Host(), conf.getSsh_Port());
	  session.setPassword(conf.getSsh_Password());  
	  session.setConfig("StrictHostKeyChecking", "no");
	  //session.connect();
	  log.write("Connecting to "+conf.getSsh_Username()+"@"
			  +conf.getSsh_Host()+":"+conf.getSsh_Port());
	  session.connect(10000);   // making a connection with timeout.
	  
	  log.write("Connected.");	
	  writeIsFinish("0");
	  
      //NE List File
      FileInputStream Fis = new FileInputStream(conf.getNeList());
      DataInputStream in = new DataInputStream(Fis);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String StrLine;
      ArrayList<String>NE=new ArrayList<String>();
      while ((StrLine = br.readLine()) != null )   {
	      if (!"".equalsIgnoreCase(StrLine.trim()) && !StrLine.startsWith("#")){		 
		      NE.add(StrLine.trim());
	      }
      }

      //Command List File
      Fis = new FileInputStream(conf.getCommandList());
      in = new DataInputStream(Fis);
      br = new BufferedReader(new InputStreamReader(in));
      ArrayList<String>COMMAND=new ArrayList<String>();
      while ((StrLine = br.readLine()) != null)   {
	      if (!"".equalsIgnoreCase(StrLine.trim()) && !StrLine.startsWith("#")){
	    	  COMMAND.add(StrLine.trim());
	      }    	  
      }
	  
	  final ExecutorService pool = Executors.newFixedThreadPool(maxThread);
	  
	  final ThreadTracker threadTracker = new ThreadTracker(){		  	
			public void allThreadsHaveFinished(){
					log.write("Finish...");
					session.disconnect();
					writeIsFinish("1");
					getPool().shutdown();

			}
		};
		
	  threadTracker.setMaxthread(maxThread);
	  threadTracker.setCountNE(NE.size());
	  threadTracker.setPool(pool);
	  NokiaGrabberMain nokiaGrabber = new NokiaGrabberMain(threadTracker, pool/*, session*/);
	  nokiaGrabber.execute(session, sekarang, conf, NE, COMMAND);
	}
	catch(Exception e){
	  log.write(e.getMessage());
//	  e.printStackTrace();	  
	}
}
 
public void execute(Session session, String tanggal, PropReader conf,  ArrayList<String>NE,  ArrayList<String>COMMAND){
	                
	  for (String ne : NE){		  
		  NokiaGrabberThread thread = new NokiaGrabberThread(session, ne, tanggal, threadTracker, conf, COMMAND);
		  poolRef.execute(thread);
	  }
	  	
}

private static void writeIsFinish(String isFinish){
	try {
		FileWriter fstream = new FileWriter("isFinish");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(isFinish+"\n");						  			  
		out.close();
		fstream.close();
	}catch (Exception e){
		log.write("An Exception "+e.getMessage());
	}
}


}