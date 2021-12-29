package tunnel;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class sshTunnel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSch jsch = new JSch();
		Session session;
		try {
			session = jsch.getSession("fauzan", "103.30.115.1");
			Properties config = new Properties();
		      config.put("StrictHostKeyChecking", "no");
		      session.setConfig(config);
			session.setPassword("teezdumk6136");
			session.connect(4000);
			session.setPortForwardingL(1032, "172.20.25.22", 80);
			session.setPortForwardingL(1033, "172.20.25.22", 1521);
			session.setPortForwardingL(2452, "172.17.12.17", 3306);
			session.setPortForwardingL(1034, "172.20.25.26", 1521);
			session.setPortForwardingL(1035, "172.20.25.21", 1521);
			session.setPortForwardingL(1036, "103.30.113.96", 443);
			//session.setPortForwardingL(1033, "103.30.115.1", 80);
			session.setPortForwardingL(2005, "172.17.12.77", 3306);
			System.out.println("Listening..");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
