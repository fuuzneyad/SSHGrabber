package tunnel;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class sshTunnel2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSch jsch = new JSch();
		Session session;
		try {
			session = jsch.getSession("root", "150.242.111.242", 2220);
			Properties config = new Properties();
		      config.put("StrictHostKeyChecking", "no");
		      session.setConfig(config);
		      
			session.setPassword("6136teezdumk");
			session.connect(4000);
			session.setPortForwardingL(2005, "172.17.12.77", 3306);
			session.setPortForwardingL(2451, "172.17.12.5", 1521);
			session.setPortForwardingL(2452, "172.17.12.17", 3306);
			session.setPortForwardingL(2480, "172.17.12.70", 443);
			session.setPortForwardingL(2001, "172.16.10.1", 443);
			//session.setPortForwardingL(1033, "103.30.115.1", 80);
			session.setPortForwardingL(2002, "172.16.11.41", 80);
			session.setPortForwardingL(2003, "103.30.113.96", 443);
			
			System.out.println("Listening..");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
