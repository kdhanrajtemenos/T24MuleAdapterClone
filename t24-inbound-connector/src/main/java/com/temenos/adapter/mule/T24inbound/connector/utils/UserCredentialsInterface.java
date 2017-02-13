package com.temenos.adapter.mule.T24inbound.connector.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigServerSelector;
import com.temenos.soa.utils.StringUtils;

public class UserCredentialsInterface {

    protected final transient Log log = LogFactory.getLog(getClass());
	
	public static final String T24_PASS = "T24_PASS";
	public static final String SERVICE_PASS = "SERVICE_PASS";
	public static final String T24_RUNTIME = "T24_RUNTIME";
	public static final String T24_SERVER_TYPE = "T24_SERVER_TYPE";
	public static final String T24_PORT = "T24_PORT";
	public static final String T24_HOST = "T24_HOST";
	public static final String T24_USER = "T24_USER";
	public static final String T24_NODE_NAMES = "T24_NODE_NAMES";
	public static final String FOLDER = "FOLDER";
	public static final String T24_EJB_STATEFUL = "T24_EJB_STATEFUL";
	public static final String T24_EJB_NAME = "T24_EJB_NAME";
	
	
	private String t24password;
	
	private String servicePassword;
	
	private JComponent[] userInputs;

	private JPasswordField t24passwordField = new JPasswordField();
	private JPasswordField servicePasswordField = new JPasswordField();
	
	public UserCredentialsInterface setInputFields(String labelT24Pass, String labelServicePass ){
		
		t24passwordField = new JPasswordField();
		servicePasswordField = new JPasswordField();
	
		userInputs = new JComponent[] { new JLabel(labelT24Pass), t24passwordField, new JLabel(labelServicePass), servicePasswordField };
		
		return this;
	}
	
	public UserCredentialsInterface showUserDialog(String dialogName){
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JOptionPane.showConfirmDialog(null, userInputs, dialogName, JOptionPane.OK_CANCEL_OPTION);

			this.setT24password(new String(t24passwordField.getPassword()));
			this.setServicePassword(new String(servicePasswordField.getPassword()));
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		return this;
	}
	


	public String getT24password() {
		return t24password;
	}

	public void setT24password(String t24password) {
		this.t24password = t24password;
	}

	public String getServicePassword() {
		return servicePassword;
	}

	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}
	
	public void saveEncryptedFile(ConnectorConfig config) throws RuntimeException {
		
		Properties prop = new Properties();
		try {
			//prop.put(SERVICE_PASS, PasswdUtil.encrypt(servicePassword));
			prop.put(T24_SERVER_TYPE, RuntimeConfigServerSelector.getRuntimeConfigServerSelector(config.getT24RunTime()));
			prop.put(T24_RUNTIME, RuntimeConfigSelector.getRunTimeSelector(RuntimeConfigSelector.TAFJ));
			prop.put(T24_HOST, config.getT24Host());
			prop.put(T24_PORT, String.valueOf(config.getT24Port()));
			prop.put(T24_NODE_NAMES, config.getNodeName());
			prop.put(T24_USER, config.getT24User());

			// encrypt user password 
			prop.put(T24_PASS, PasswdUtil.encrypt(t24password));

			prop.put(T24_EJB_STATEFUL, config.getEjbStateful().toString());
			
			if(null != config.getEjbName() && 0 != config.getEjbName().length()) {
				prop.put(T24_EJB_NAME, config.getEjbName());
			}
			
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("GeneralSecurityException in property creation "+e.getMessage());
		}
		
		/* will write the properties to the file in the given folder */
		IoResourseUtil io = new IoResourseUtil();
		if(!io.writePropertiesToFile(prop, config.getSettingsFilePath())){
			throw new RuntimeException("Cannot resolve directory for writing");
		}
	}
	
	private InputStream tryInStreams(String fName){
		InputStream is = null;
		is =  this.getClass().getResourceAsStream(fName);
		if(is != null) {
			/* This should work if this class is executed as standard java application */
			return is;
		}
		is =  Thread.currentThread().getContextClassLoader().getResourceAsStream(fName);
		if(is != null) {
			/* This should work if this class is executed at mule run-time */
			return is;
		}
		is =  UserCredentialsInterface.class.getResourceAsStream(fName);
		if(is != null) {
			/* This should work if this class is executed at mule meta data discovery */
			return is;
		}
		is =  UserCredentialsInterface.class.getClassLoader().getResourceAsStream(fName);
		if(is != null) {
			/* Try alternate methods */
			return is;
		}
		is =  this.getClass().getClassLoader().getResourceAsStream(fName);
		if(is != null) {
			return is;
		}
		/* Should reach hear only in test connectivity...when file is not a yet an internal resource */
		return null;
	}
	
	private InputStream tryResolveResourse(String fName, String path){
		
		/* get the last sub-folder */
		String alternateDir = path.substring(0,path.lastIndexOf(File.separator));
		
		/* try several file path locations for resolving at run-time...Note that: "/"+fname - should work at metadata discovery
		 * and fName should work at run-time */
		String possibleLoacations [] = {path, path + File.separatorChar +fName, fName, "/"+fName, File.separatorChar+fName, alternateDir + "/" + fName, alternateDir + File.separatorChar + fName, path + "/" +fName};
		
		InputStream is = null;
		for(String location : possibleLoacations){
			is = tryInStreams(location);
			if(is != null) { 
				log.debug("Load runtime configuration from location: "+location);
				break;
			}
		}
		if(is==null){
			try {
				/* this should work if storrage folder is not part of flow project */
				is = new FileInputStream( path + "/" +fName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return is;
	}
	
	/**
	 * Extract parameters from file
	 * 
	 * @param filePath - path to credentials file e.g. C:\directory\...\filename.ext
	 * @return property object from file or null 
	 */
	public Properties resolveCredentialFileAndPath(String filePath)  {
		
		/* Check if the input parameter is valid file path */
		if(StringUtils.isNullOrEmpty(filePath) ){
			return null;
		}
		
		boolean readMode;
		
		if(!IoResourseUtil.isDir(filePath)){
			readMode = false;
		}else{
			readMode = true;
		}
		
		/* Split the the directory path and filename*/
		String folder = null;
		String fileName = null;
		filePath = filePath.replace("/", File.separator);
		int filePathSeparratorIdx = filePath.lastIndexOf(File.separator);
		if(filePathSeparratorIdx != -1 && filePathSeparratorIdx<filePath.length()){
			folder = filePath.substring(0,filePathSeparratorIdx );
			fileName = filePath.substring(filePathSeparratorIdx + 1);
		}else{
			return null;
			//throw new RuntimeException("the given file is not valid");
		}
		
		/* try to read the resource file. The folder parameter will be used as last option otherwise it will be ignored  */
		InputStream is = null;
		Properties p = null;
		if(!readMode){
			 is = tryResolveResourse(fileName, folder);
			 if(is==null){
				return null;
				//throw new RuntimeException("cannot load resourse from design time! " + fileName);
			 }
		}else{
			IoResourseUtil io =  new IoResourseUtil();
			try{
				//System.out.println("Loading..." + fileName);
				p = io.readResourseFile(fileName, folder + File.separatorChar, IoResourseUtil.LOAD_SCHEMA);
				
			}catch(RuntimeException e){
				return null;
				//throw new RuntimeException(e);
			}
		}
		

		
		/* load file contents in to properties object */
		Properties prop = new Properties();
		try {
			if(!readMode){
				prop.load(is);
			}else{
				if(p.isEmpty()){
					return null;
					//throw new RuntimeException("cannot load resource for run-time! " + fileName);
				}
				prop = p;
			}
		} catch (IOException e) {
			return null;
			//throw new RuntimeException(e.getMessage());
		}
		if(prop == null ||prop.isEmpty()){
			return null;
			//throw new RuntimeException("properties file corrupted! ");
		}
		
		/* Get encrypted passwords */
		String encT24Pass = prop.getProperty(T24_PASS);		
		//String encServicePass  = prop.getProperty(SERVICE_PASS);
		
		if(StringUtils.isNullOrEmpty(encT24Pass)){
			return null;
			//throw new RuntimeException("credential file is corrupted!");
		}
		
		/* decrypt passwords and store them into this instance fields for use in the connector configuration at run-time and metadata discovery */
		String decryptedPass = PasswdUtil.decrypt(encT24Pass);
		//this.setServicePassword(PasswdUtil.decrypt(encServicePass));
		
		prop.put(T24_PASS, decryptedPass );
		
		//prop.put(SERVICE_PASS, this.getServicePassword() );
		prop.put(FOLDER, folder);
		
		return prop;
	}
	
	public static void main(String [] args) {
		if(args.length > 0) {
			try {
				System.out.print("Encrypted value of parameter ["+args[0]+"] = " + PasswdUtil.encrypt(args[0]));
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
}
