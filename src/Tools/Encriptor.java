package Tools;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import org.jasypt.encryption.pbe.*;


public class Encriptor {
	StandardPBEStringEncryptor spbe;
	Properties prop = new Properties();
	String fileName = "Grabber.cfg";
	
	public Encriptor(){
		spbe = new StandardPBEStringEncryptor();
		spbe.setPassword("Fauzan%*Suren13#$Aye");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length>0){
			Encriptor en =new Encriptor();
			String encripted = en.Enkrip(args[0]);
			System.out.println("-------------------------------------");
		    System.out.println("Encripted Password Are:");
		    System.out.println(encripted);
		    System.out.println("-------------------------------------");
		    
		    
		    en.EditProperties("Ssh_Password",encripted);
		    
//		    try { 
//		    	Properties prop1 = new Properties();
//			    prop1.load(new FileInputStream(en.fileName)); 
//			    System.out.println("DEKRIP--"+en.Dekrip(prop1.getProperty("Ssh_Password")));
//		    } catch (IOException e) { System.out.println("Do you have the config file?\n"); System.exit(0);}
		    
			
		}else
		{
			System.out.println("You must Specify [passwordToEncript] Argument");
		}
	}

	public String Enkrip(String str){
		try {
			return(spbe.encrypt(str));
			} catch (Exception e){
				return(null);
			}
	}
	
	public String Dekrip(String str){
		try {
			return(spbe.decrypt(str));
			} catch (Exception e){
				return(null);
			}
	}
	
//    private void SetProp(String TheVar, String TheVal){
//		OutputStream os = null;
//		try {
//		os = new FileOutputStream( fileName );                
//		prop.setProperty(TheVar, TheVal);
//		prop.store( os , null);			
//		System.out.println(TheVar+" "+ TheVal +" Have been addded to Grabber.cfg");
//		} catch( IOException e ) {
//			// ...
//			System.out.println("Do you have the "+fileName+" file?\n");
//		} finally {
//			if( null != os ) 
//			try { os.close(); } 
//			catch( IOException e ) 
//			{  }
//		}
//		
//	}
    

private void EditProperties(String var, String val){
	try{
	FileInputStream fstream = new FileInputStream(fileName);
	DataInputStream in = new DataInputStream(fstream);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String strLine;
    String Tmp="";
    boolean isFound=false;
   while ((strLine = br.readLine()) != null)   {
	   if (strLine.startsWith(var))
		   {
		   Tmp+=var+" = "+val+"\n";
		   System.out.println("["+var+" = "+val+"] has been added to "+fileName);
		   isFound=true;
		   }else
	   Tmp+=strLine+"\n";
      }           
   in.close();  
   
   if(!isFound)
	   {
	   Tmp+=var+" = "+val+"\n";
	   System.out.println("["+var+" = "+val+"] has been added to "+fileName);
	   }
   FileWriter aWriter = new FileWriter(fileName, false);
   aWriter.write(Tmp);
   aWriter.flush(); 
   
   aWriter.close();	
	} catch( IOException e ) {
	// ...
	System.out.println("Do you have the "+fileName+" file?\n");
	}
}
	
}
