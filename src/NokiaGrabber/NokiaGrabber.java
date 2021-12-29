package NokiaGrabber;

import com.jcraft.jsch.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
//import java.io.StringWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NokiaGrabber {

	private static ArrayList<String> SpecParam;
	private static int Counter=0;
	private static String buffer="";
	private static logger log = new logger();
	
 public static void main(String[] arg){
                
	 try{
		 String config=null;
	     if(arg.length>=1)
	    	config = arg[0];else
	    	config =  "Grabber.cfg";
	     PropReader   conf = new PropReader(config);
	     
	  DateFormat tanggal = new SimpleDateFormat (conf.getLogDateFormat());
      String sekarang = tanggal.format(new Date());		 
      
      //NE List File
      FileInputStream Fis = new FileInputStream(conf.getNeList());
      DataInputStream in = new DataInputStream(Fis);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String StrLine;
      ArrayList<String>NE=new ArrayList<String>();
      while ((StrLine = br.readLine()) != null )   {
	      if (!"".equalsIgnoreCase(StrLine.trim())){		 
		      NE.add(StrLine.trim());
	      }    	  
      }      

      //Command List File
      Fis = new FileInputStream(conf.getCommandList());
      in = new DataInputStream(Fis);
      br = new BufferedReader(new InputStreamReader(in));
      ArrayList<String>COMMAND=new ArrayList<String>();
      while ((StrLine = br.readLine()) != null)   {
	      if (!"".equalsIgnoreCase(StrLine.trim())){
	    	  COMMAND.add(StrLine.trim());
	      }    	  
      }
      
     
	  JSch jsch=new JSch();
	  Session session=jsch.getSession(conf.getSsh_Username(), 
			  conf.getSsh_Host(), conf.getSsh_Port());
	  session.setPassword(conf.getSsh_Password());  
	  session.setConfig("StrictHostKeyChecking", "no");
	  //session.connect();
	  log.write("Connecting to "+conf.getSsh_Username()+"@"
			  +conf.getSsh_Host()+":"+conf.getSsh_Port());
	  session.connect(10000);   // making a connection with timeout.

	  log.write("Connected.");
	  
	  for (String ne : NE) {
	      for (String command : COMMAND){
	    	  		String FN=conf.getLogFolder()+"/"+ne+conf.getLogFileName()+sekarang+".txt";
//	    	  		grab(session, command, FN, command);
	    	  		log.write("exemmlmx -s -n "+ne+" -c \""+command+"\"");
	    	  		grab(session, "exemmlmx -s -n "+ne+" -c \""+command+"\"",
	    	  				FN, command);
	    	  		
//	    	  		sleepTime = generator.nextInt( 5000 );
////		    	  	Thread.sleep( sleepTime );
//		    	  	session.wait(sleepTime);		    	  	
		    	  	
//		    	 	if (command.equalsIgnoreCase("./jfl.sh") && SpecParam!=null){
	    	  		if (command.equalsIgnoreCase("JFL;") && SpecParam!=null){
		    	  		for (String Params : SpecParam){
		    	  			  log.write(">exemmlmx -s -n "+ne+" -c \"JFI:UPD="+Params+";\"");
		  		  	    	  grab(session, "exemmlmx -s -n "+ne+" -c \"JFI:UPD="+Params+";\"",
		  		  	    			FN, "XXX");
		    	  		}
	    	  		}else
//		    	 	if (command.equalsIgnoreCase("./juq.sh") && SpecParam!=null){
		   	  		if (command.equalsIgnoreCase("JUQ;") && SpecParam!=null){
		    	  		for (String Params : SpecParam){
		    	  				log.write(">exemmlmx -s -n "+ne+" -c \"JUQ:NAME="+Params+";\"");
		    	  				grab(session, "exemmlmx -s -n "+ne+" -c \"JUQ:NAME="+Params+";\"",
		    	  						FN, "XXX");
		    	  		}
		    	  	}else    	  
//		    	 	if (command.equalsIgnoreCase("cat jui.txt") && SpecParam!=null){
	    	  		if (command.equalsIgnoreCase("JUI:::;") && SpecParam!=null){
		    	  		for (String Params : SpecParam){
		    	  				log.write(">exemmlmx -s -n "+ne+" -c \"JUI:NAME="+Params+";\"");
		    	  				grab(session, "exemmlmx -s -n "+ne+" -c \"JUI:NAME="+Params+";\"",
		    	  						FN, "XXX");
		    	  		}
		    	  	}else
//		    	 	if (command.equalsIgnoreCase("cat rci.txt") && SpecParam!=null){
	    	  		if (command.equalsIgnoreCase("RCI;") && SpecParam!=null){
	    	  			if (buffer!=null && !buffer.equals(""))
	    	  				SpecParam.add(buffer.charAt(buffer.length()-1)=='&' ? buffer.substring(0,buffer.lastIndexOf("&")) : buffer);
		    	  		for (String Params : SpecParam){
		    	  				log.write(">exemmlmx -s -n "+ne+" -c \"CEL:CGR="+Params+";\"");
		    	  				grab(session, "exemmlmx -s -n "+ne+" -c \"CEL:CGR="+Params+";\"",
		    	  						FN, "XXX");
		    	  		}
		    	  		 Counter=0;
		    	  		 buffer="";
		    	  	}else
//			    	 	if (command.equalsIgnoreCase("cat e3s.txt") && SpecParam!=null){
		    	  		if (command.equalsIgnoreCase("E3S;") && SpecParam!=null){
			    	  		for (String Params : SpecParam){			    	  				
			    	  			log.write(">exemmlmx -s -n "+ne+" -c \"E3J:POOLNAME="+Params+";\"");
			    	  				grab(session, "exemmlmx -s -n "+ne+" -c \"E3J:POOLNAME="+Params+";\"",
			    	  					FN, "XXX");
			    	  		}
			    	  	}		    	  		
	      }
	  }
//	try{Thread.sleep(5000);}catch(Exception ee){}
	  
//  grab(session, "exemmlmx -s -n XBDG1 -c \"RCI;\"");
	  
	  log.write("Finish...");
	  
	  session.disconnect();
	}
	catch(Exception e){
//	  System.out.println(e.getMessage());
	  log.write(e.getMessage());
//	  System.out.println(e);
//	  e.printStackTrace();	  
	}
}

private static void grab(Session session, String Runcommand, String FN, String command){
  try {
	  boolean IsSpecialCommand = false;
      Channel channel=session.openChannel("exec");
      
      channel.setInputStream(null);
      ((ChannelExec)channel).setErrStream(System.err);
   	  ((ChannelExec)channel).setCommand(Runcommand); 
      channel.connect(3000);

      InputStream is = channel.getInputStream();
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
//      The Output File
      FileWriter out; 
      out= new FileWriter(FN, true);
      
     
//      if (command.equalsIgnoreCase("./jfl.sh") || command.equalsIgnoreCase("./juq.sh") || command.equalsIgnoreCase("cat jui.txt") || command.equalsIgnoreCase("cat rci.txt") || command.equalsIgnoreCase("cat e3s.txt"))
      if (command.equals("JFL;") || command.equals("JUQ;") || command.equals("JUI:::;") || command.equals("RCI;") || command.equals("E3S;"))
    	  IsSpecialCommand=true;else
    		  IsSpecialCommand=false;
      
      if (IsSpecialCommand){
//1st	      	  
    	  SpecParam = new ArrayList<String>();
    	  String line;    	      	  
	       while ((line = in.readLine()) != null  || !channel.isClosed()) {
	         if (line != null) {
	        	 out.write(line +'\r'+'\n');
	        	 out.flush();
	        	 ProcessSpecialCommand(line, command);
	         }
	       }
      }else{
//2nd     
      byte[] tmp=new byte[1024];
      while(true){
    	  while(is.available()>0){
    		  int i=is.read(tmp, 0, 1024);
    		  if(i<0)break;
    		  String tempStr = new String(tmp, 0, i);
//		      System.out.println(tempStr);
    		  out.write(tempStr);
    		  out.flush();
    		  
    	  }
    	  if(channel.isClosed()){
    		  break;
    	  }
    	  try//yyn
          {
              Thread.sleep(1000);
          }
          catch (Exception ee)
          {
        	  System.err.println("sleep exp "+ee);
          }
      }
    }
      
    out.close();
    in.close();
    is.close();
     
    channel.disconnect();
  	} catch (Exception e){
  		log.write(e.getMessage());
    }
}

private static void ProcessSpecialCommand(String Str, String command){
	try {
	if (command.equalsIgnoreCase("JFL;")){//"JFL;""./jfl.sh"
		if (Str.indexOf("AAL2")>-1 || Str.indexOf("IPV4")> -1)
		{
//			System.out.println(Str);
			String[] Splitted;
			Splitted= Str.split("\\s+");
			for(int i=0; i<Splitted.length; i++){
				if (i==1 && Splitted[i]!=null)
				{
					SpecParam.add(Splitted[i]);
					break;
				}
			}
		}
	}
	else 
		if (command.equalsIgnoreCase("JUQ;")){//"JUQ;""./juq.sh"
			if (Str.indexOf("RESULT NAME")>-1)
			{
//				System.out.println(Str);
				String[] Splitted;
				Splitted= Str.split("\\s+");
				for(int i=0; i<Splitted.length; i++){
					if (i==3 && Splitted[i]!=null)
					{
						SpecParam.add(Splitted[i]);
						break;
					}
				}
			}
		}	
	else 
	if (command.equalsIgnoreCase("JUI:::;")){//"JUI:::;""cat jui.txt"
		if (Str.indexOf("DETERMINATION")>-1)
		{
//			System.out.println(Str);
			String[] Splitted;
			Splitted= Str.split("\\s+");
			for(int i=0; i<Splitted.length; i++){
				if (i==0 && Splitted[i]!=null)
				{
					SpecParam.add(Splitted[i]);
					break;
				}
			}
		}
	}
	else 
	if (command.equalsIgnoreCase("RCI;")){//"RCI;""cat rci.txt"
		if ((Str.indexOf("BI")>-1 || Str.indexOf("OUT")>-1) && (Str.indexOf("WO-EX")>-1 || Str.indexOf("BA-US")>-1))
		{
//			System.out.println(Str);
			String[] Splitted;
			Splitted= Str.split("\\s+");
			for(int i=0; i<Splitted.length; i++){
				if (i==0 && Splitted[i]!=null){
//					System.out.println(Splitted[i]);
					Counter++;
					buffer+=Splitted[i];
					if (Counter % 10 ==0){
//						System.out.println(buffer);
						SpecParam.add(buffer);
						buffer="";
					}else
						buffer+="&";
					break;
				}
	
			}
		}
	}
	else 
		if (command.equalsIgnoreCase("E3S;")){//"E3S;""cat e3s.txt"
			if (Str.indexOf("POOLNAME . :")>-1)
			{
				String[] Splitted;
				Splitted= Str.split("\\s+");
				for(int i=0; i<Splitted.length; i++){
					if (i==6 && Splitted[i]!=null)
					{						
						SpecParam.add(Splitted[i].trim());
						break;
					}
				}
			}
		}
	}catch (Exception e){
//		System.out.println("EXCEPTION may be "+e);
//		System.out.println(e.getMessage());
		log.write(e.getMessage());
//		e.printStackTrace();
	}
	
}

}