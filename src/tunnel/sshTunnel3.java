package tunnel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class sshTunnel3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSch jsch = new JSch();
		Session session;
		try {
			
			
			String host,port,username,password,mappingPortFile ;
			host=port=username=password=mappingPortFile = null;
			
			
			 try {
				   InputStream input ;
				   if(args.length>1)
					   input = new FileInputStream(args[0]);
				   else
					   input = new FileInputStream("tunnel.cfg");
				   
		            Properties prop = new Properties();

		            // load a properties file
		            prop.load(input);

		            host = prop.getProperty("host");
		            port = prop.getProperty("port");
		            username = prop.getProperty("username");
		            password = prop.getProperty("password");
		            mappingPortFile = prop.getProperty("mappingPortFile");

		        } catch (IOException ex) {
		            ex.printStackTrace();
		        }
			
			 session = jsch.getSession(username, host, Integer.parseInt(port));
				Properties config = new Properties();
			      config.put("StrictHostKeyChecking", "no");
			      session.setConfig(config);
			      
				session.setPassword(password);
			
			session.connect(4000);
			
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(mappingPortFile));	
				String line = reader.readLine();
				
				while (line != null) {
					
					if(!line.startsWith("#") && line.contains(",") && line.split(",").length>2) {
						Integer lport = Integer.parseInt(line.split(",")[0]);
						String hst = line.split(",")[1];
						Integer rport = Integer.parseInt(line.split(",")[2]);
						System.out.println("set up port forward "+lport+" " +hst+" "+rport);
						
						session.setPortForwardingL(lport, hst, rport);
					}
					line = reader.readLine();
				}
				reader.close();
			}catch (IOException ex) {
				ex.printStackTrace();
			}
			
			System.out.println("Listening..");
			
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

}
