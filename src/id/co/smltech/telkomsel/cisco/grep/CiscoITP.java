package id.co.smltech.telkomsel.cisco.grep;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.naming.AuthenticationException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class CiscoITP {
	private static Logger logger = Logger.getLogger(CiscoITP.class.getName());
	private static long startTime = System.currentTimeMillis();
	private static AtomicBoolean isLoggedIn = new AtomicBoolean(false);
	private static int responseTimeout = -1;
	private static InputStream reader;
	private static OutputStream writer;
	private static String encoding = "UTF-8";
	private static FileWriter fileWriter;
	private static int maxLoginAttempt = 5;
	private static int currentLoginAttempt = 0;
	private static String currNE;
	private static String returnOK = "ok";
	
	public static void main(String[] args) throws JSchException, IOException, TimeoutException, AuthenticationException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		FileHandler saveLogToFile = new FileHandler("logs/cisco_log_"+sdf.format(new Date())+".log");
		
		saveLogToFile.setFormatter(new SimpleFormatter());
		logger.addHandler(saveLogToFile);
		logger.setLevel(Level.ALL);
		
		/*
		 * read configuration file
		 */
		File propFile = new File("resources/cisco_cli_conf.cfg");
		Properties prop = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(propFile);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			logger.logp(Level.SEVERE, CiscoITP.class.getName(), "File config initiation", "Exception {0} ", new Object []{e.getMessage()});
			e.printStackTrace();
		} catch (IOException e) {
			logger.logp(Level.SEVERE, CiscoITP.class.getName(), "IO Exception", "Exception {0} ", new Object []{e.getMessage()});
			e.printStackTrace();
		}
		
		String oss_host = prop.getProperty("oss_host");
		int oss_port = Integer.parseInt(prop.getProperty("oss_port"));
		String oss_username = prop.getProperty("oss_username");
		String oss_password = prop.getProperty("oss_password");
		
		String ne_list_file = prop.getProperty("ne_list");
		String ne_username = prop.getProperty("ne_username");
		String ne_password = prop.getProperty("ne_password");
		
		String app_output_path = prop.getProperty("app_output_path");
		String command_list_file = prop.getProperty("command_list_file_path");
		
		int response_timeout = Integer.parseInt(prop.getProperty("response_timeout"));
		responseTimeout = response_timeout;
		
		maxLoginAttempt = Integer.parseInt(prop.getProperty("max_login_attempt"));
		
		/*
		 * get ne list
		 */
		File neListFile = new File(ne_list_file);
		
		/*
		 * get command list
		 */
		ArrayList<String> commandList = generateCommandList(command_list_file);
		
		FileReader fr = new FileReader(neListFile);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		
		while((line = br.readLine()) != null){
			
			String [] arrData = line.split(",") ;
			
			/*
			 * init output file
			 */
			String outFilePath = app_output_path+"/"+"cisco-"+arrData[0]+"-"+sdf.format(new Date())+".txt";
			logger.info(currNE + " : Writing file to "+outFilePath);
			currNE = arrData[1];
			
			/*
			 * grep data
			 */
			logger.log(Level.INFO, "Start getting data from "+arrData[0]);
			grepData(
				oss_host, oss_username, oss_port, oss_password,
				ne_username, ne_password, commandList,
				outFilePath
			);
			
			logger.log(Level.INFO, "Getting data from "+arrData[0]+" is done.");
		}
		writer.flush();
		writer.close();
		writer = null;
		
		reader.close();
		reader = null;
		br.close();
		fr.close();
		
		try {
			/*
			 * waiting 3 minutes to shut down the apps
			 */
			Thread.sleep(3*60000);
			logger.info("Application Terminated.");
			System.exit(1);
			// (:D)>
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * <b>This is the method to convert list command that saved from file format into String ArrayList format</b>
	 * @param command_list_file is the file path which contain list of commands.
	 * @return list of command that reformed into ArrayList.
	 */
	private static ArrayList<String> generateCommandList(String command_list_file) {
		File commandFile = new File(command_list_file);
		ArrayList<String> commandList = new ArrayList<String>();
		
		try {
			FileReader fr1 = new FileReader(commandFile);
			BufferedReader br1 = new BufferedReader(fr1);
			String contentLine = "";
			
			while((contentLine = br1.readLine()) != null){
				commandList.add(contentLine);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return commandList;
	}

	/**
	 * 
	 * @param sshHost is a string which is the IP or host name of SSH server 
	 * @param sshUsername
	 * @param sshPort
	 * @param sshPassword
	 * @param telnetUsername
	 * @param telnetPassword
	 * @param commands
	 * @param filePath
	 */
	private static void grepData(String sshHost, String sshUsername, int sshPort, String sshPassword, 
			String telnetUsername, String telnetPassword,
			ArrayList<String> commands,
			String filePath) {
		
		JSch jsch = new JSch();
		try {
			
			Session session = jsch.getSession(sshUsername, sshHost, sshPort);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(sshPassword);
			session.connect();
			
			Channel channel = session.openChannel("shell");
			channel.connect();
			
			
			if(writer != null)
				writer.close();
			if(reader != null)
				reader.close();
			
			reader = channel.getInputStream();
			writer = channel.getOutputStream();
			
			if(fileWriter != null)
				fileWriter.close();
			fileWriter = new FileWriter(filePath);
			
			loginAndGetData(currNE, telnetUsername, telnetPassword, commands);
			
			fileWriter.close();
			channel.disconnect();
			session.disconnect();
		} catch (JSchException e) {
			logger.logp(Level.SEVERE, CiscoITP.class.getName(), "Grep data "+currNE, "SSH Exception {0} ", new Object []{e.getMessage()});
		} catch (IOException e) {
			logger.logp(Level.SEVERE, CiscoITP.class.getName(), "Grep data "+currNE, "IO Exception {0} ", new Object []{e.getMessage()});
		} catch (TimeoutException e) {
			logger.logp(Level.SEVERE, CiscoITP.class.getName(), "Grep data "+currNE, "Time out Exception {0} ", new Object []{e.getMessage()});
		} catch (AuthenticationException e) {
			logger.logp(Level.SEVERE, CiscoITP.class.getName(), "Grep data "+currNE, "Authentication Exception {0} ", new Object []{e.getMessage()});
		} 
		
	}

	
	/**
	 * @since April, 19 2014
	 * @author Irnadi Zen
	 * @param telnetHost is a string refer IP of telnet host 
	 * @param telnetUsername is a string refer of telnet username
	 * @param telnetPassword is a string refer of telnet password
	 * @param commands is a ArrayList<String> refer of telnet command list
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ResponseTimeoutException
	 * @throws TimeoutException
	 * @throws AuthenticationException
	 */
	@SuppressWarnings("serial")
	private static void loginAndGetData(String telnetHost,
			String telnetUsername, String telnetPassword,
			ArrayList<String> commands) throws UnsupportedEncodingException,
			IOException, TimeoutException,
			AuthenticationException {
		
		telnetLogin(telnetHost, telnetUsername, telnetPassword);
		
		if(isLoggedIn.get()){
			/*
			 * command running 
			 */
			for(String command : commands){
				
				sendCommand(command,false);
				readUntil(">");
				
			}
			
			/*
			 * exit from telnet
			 */
			sendCommand("exit",false);
			
		} else {
			if(currentLoginAttempt <= maxLoginAttempt ){
				loginAndGetData(telnetHost, telnetUsername, telnetPassword, commands);
			} else {
				currentLoginAttempt = 0;
			}
			
			
			throw new AuthenticationException(){
				@Override
				public Throwable getCause() {
					logger.logp(Level.WARNING, CiscoITP.class.getName(), "grepData "+currNE, "Login failed");
					return super.getCause();
				}
			};
		}
	}
	
	/**
	 * 
	 * @param host
	 * @param username
	 * @param password
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ResponseTimeoutException
	 * @throws TimeoutException
	 */
	private static void telnetLogin(String host, String username, String password) throws UnsupportedEncodingException,
			IOException, TimeoutException {
		
		isLoggedIn.set(false);
		
		currentLoginAttempt ++;
		
		sendCommand("telnet "+host+" -override",false);
		String telnetReturnMsg = readUntil("username:");
		if(telnetReturnMsg.indexOf("This device is not in CiscoWorks Network Compliance Manager")>-1){
			logger.severe(currNE + " : This device is not in CiscoWorks Network Compliance Manager");
			return;
		}else{
			sendCommand(username,false);
			
			readUntil("password:");
			sendCommand(password,true);
			
			if(readUntil(">").equals(returnOK)){
				isLoggedIn.set(true);
			}
		}
	}
	
	/**
	 * 
	 * @param command is a string command which is will be send to SSH server
	 * @param isPassword is a boolean parameter as a flag for defining that password value would not recording into the log file 
	 * @throws Exception 
	 */
	protected static void sendCommand(String command, boolean isPassword) throws UnsupportedEncodingException, IOException
	{
		
		writer.write((command+"\n").getBytes(encoding));
		writer.flush();
		
		if(!isPassword){
			logger.info("Sending command "+command+" into network "+currNE+".");
		}
				
	}
	
	/**
	 * 
	 * @param stringPattern
	 * @throws IOException
	 * @throws TimeoutException 
	 * @throws Exception 
	 */
	protected static String readUntil(String stringPattern) throws IOException, TimeoutException
	{
		logger.log(Level.INFO, currNE+" : reading stream until matched regex ["  + stringPattern + "]");
		int byteSize = 2048;
		ByteArrayOutputStream bytebuf = new ByteArrayOutputStream(byteSize);
		
		try
		{
			Thread.sleep(1000); // wait for running command
		}
		catch (InterruptedException e)
		{
		}
		
		startTime = System.currentTimeMillis();
		
		try
		{
			try
			{
				//TODO remove if LINEMODE is enabled.
				// wait for running command
				Thread.sleep(1000); 
			}
			catch (InterruptedException e)
			{
			}
			while(System.currentTimeMillis() - startTime < responseTimeout * 1000)
			{
				
				while (reader.available() > 0)
				{
	
					byte[] tmp = new byte[byteSize];
					int i = reader.read(tmp, 0, 1024);
					if (i < 0)
					{
						break;
					}
					bytebuf.write(tmp, 0, i);

				}
				
				logger.info("Getting data from the steam was done.");
				
				bytebuf.flush();
				
				if (bytebuf.toString(encoding).indexOf(stringPattern)>-1)
					break;
				
				// timeout processing is disabled if seting responseTimeout to 0. 
				if(responseTimeout > 0 && (System.currentTimeMillis()- startTime  ) > responseTimeout * 1000 )
				{
					logger.info(currNE + " : Timeout : " + responseTimeout + " seconds");
					throw new TimeoutException();
				}
			}
			
			String dataStream = bytebuf.toString(encoding);
			fileWriter.append(dataStream);
			fileWriter.flush();
			
			logger.info("Writing data to the file was done.");
			
			if(dataStream.indexOf(stringPattern)>-1){
				return returnOK;
			}else{
				return dataStream;
			}
			
			/*
			if(matcher == null)
			{
				logger.log(Level.INFO, "no response");
				matcher = pattern.matcher(dataStream);
			}
			if (!matcher.find())
			{
				matcher = pattern.matcher(dataStream);
			}
			if (matcher != null && matcher.find(0) && matcher.groupCount() >= 1)
			{
				return (matcher.group(1));
			}
			return null;
			*/
		}
		catch(TimeoutException e) 
		{
			
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
	}

	@Override
	public String toString() {
		return "CiscoITP [getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

}
