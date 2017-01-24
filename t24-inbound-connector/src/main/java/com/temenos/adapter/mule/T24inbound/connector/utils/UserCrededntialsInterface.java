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

import com.temenos.adapter.mule.T24inbound.connector.config.ConnectorConfig;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24inbound.connector.config.RuntimeConfigServerSelector;
import com.temenos.soa.utils.StringUtils;

public class UserCrededntialsInterface {

	public static final String T24_PASS = "T24_PASS";
	public static final String SERVICE_PASS = "SERVICE_PASS";
	public static final String T24_RUNTIME = "T24_RUNTIME";
	public static final String T24_SERVER_TYPE = "T24_SERVER_TYPE";
	public static final String T24_PORT = "T24_PORT";
	public static final String T24_HOST = "T24_HOST";
	public static final String T24_USER = "T24_USER";
	public static final String T24_NODE_NAMES = "T24_NODE_NAMES";
	public static final String FOLDER = "FOLDER";
	
	
	private String t24password;
	
	private String servicePassword;
	
	private JComponent[] userInputs;

	private JPasswordField t24passwordField = new JPasswordField();
	private JPasswordField servicePasswordField = new JPasswordField();
	
	public UserCrededntialsInterface setInputFields(String labelT24Pass, String labelServicePass ){
		
		t24passwordField = new JPasswordField();
		servicePasswordField = new JPasswordField();
	
		userInputs = new JComponent[] { new JLabel(labelT24Pass), t24passwordField, new JLabel(labelServicePass), servicePasswordField };
		
		return this;
	}
	
	public UserCrededntialsInterface showUserDialog(String dialogName){
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
	
	public String saveEncryptedFile(String filePath, ConnectorConfig config) throws RuntimeException {
		
		/* Check if the input parameter is valid file path */
		if(StringUtils.isNullOrEmpty(filePath) || !IoResourseUtil.isDirSyntaxCorrect(filePath)){
			throw new RuntimeException("invalid file path");
		}				
		
		/* encrypt user paswords */
		Properties prop = new Properties();
		try {
			prop.put(T24_PASS, PasswdUtil.encrypt(t24password));
			prop.put(SERVICE_PASS, PasswdUtil.encrypt(servicePassword));
			prop.put(T24_RUNTIME, RuntimeConfigSelector.getRunTimeSelector(RuntimeConfigSelector.TAFJ));
			prop.put(T24_SERVER_TYPE, RuntimeConfigServerSelector.getRuntimeConfigServerSelector(config.getT24RunTime()));
			prop.put(T24_PORT, String.valueOf(config.getPort()));
			prop.put(T24_HOST, config.getAgentHost());
			prop.put(T24_USER, config.getAgentUser());
			prop.put(T24_NODE_NAMES, config.getNodeName());
		
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
		
		/* split directory from file name */
		String folder = null;
		String fileName = null;
		filePath = filePath.replace("/", "\\");
		int filePathSeparratorIdx = filePath.lastIndexOf("\\");
		if(filePathSeparratorIdx != -1 && filePathSeparratorIdx<filePath.length()){
			folder = filePath.substring(0,filePathSeparratorIdx );
			fileName = filePath.substring(filePathSeparratorIdx + 1);
		}else{
			throw new RuntimeException("the given file is not valid");
		}	
		
		/* will write the properties to the file in the given folder */
		IoResourseUtil io = new IoResourseUtil();
		if(!io.writePropertiesToFile(prop, folder, fileName)){
			throw new RuntimeException("Cannot resolve directory for writing");
		}
		
		/* return only the directory ... it will be used in future */
		return folder;
	}
	
	private InputStream tryInStreams(String fName){
		InputStream is = null;
		is =  this.getClass().getResourceAsStream(fName);
		if(is != null) {
			/* This should work if this class is executed as standart java application */
			return is;
		}
		is =  Thread.currentThread().getContextClassLoader().getResourceAsStream(fName);
		if(is != null) {
			/* This should work if this class is executed at mule run-time */
			return is;
		}
		is =  UserCrededntialsInterface.class.getResourceAsStream(fName);
		if(is != null) {
			/* This should work if this class is executed at mule meta data discovery */
			return is;
		}
		is =  UserCrededntialsInterface.class.getClassLoader().getResourceAsStream(fName);
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
		String alternateDir = path.substring(path.lastIndexOf(File.separator)+1);
		
		/* try several file path locations for resolving at run-time...Note that: "/"+fname - should work at metadata discovery
		 * and fName should work at run-time */
		String possibleLoacations [] = {fName, "/"+fName, File.separatorChar+fName, alternateDir + "/" + fName, alternateDir + File.separatorChar + fName, path + "/" +fName, path + File.separatorChar +fName};
		
		InputStream is = null;
		for(String location : possibleLoacations){
			is = tryInStreams(location);
			if(is != null) break;
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
	 * 
	 * @param filePath - path to credentials file e.g. C:\directory\...\filename.ext
	 * @return the directory path where file exists...this path will be used also for saving the Meta data properties files;
	 * @throws RuntimeException
	 */
	public Properties resolveCredentialFileAndPath(String filePath)  {
		
		/* Check if the input parameter is valid file path */
		if(StringUtils.isNullOrEmpty(filePath) ){
			return null;
		}
		
		boolean readMode;
		
		if(!IoResourseUtil.isDirSyntaxCorrect(filePath)){
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
					//throw new RuntimeException("cannot load resourse for run-time! " + fileName);
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
		String encServicePass  = prop.getProperty(SERVICE_PASS);
		
		if(StringUtils.isNullOrEmpty(encT24Pass) ||  StringUtils.isNullOrEmpty(encServicePass)){
			return null;
			//throw new RuntimeException("credential file is corrupted!");
		}
		
		/* decrypt passwords and store them into this instance fields for use in connector config at run-time and metadata discovery */
		this.setT24password(PasswdUtil.decrypt(encT24Pass));
		this.setServicePassword(PasswdUtil.decrypt(encServicePass));
		
		
		prop.put(T24_PASS, this.getT24password() );
		prop.put(SERVICE_PASS, this.getServicePassword() );
		prop.put(FOLDER, folder);
		
		/* return only the directory ... it will be used in future for meta data discovery */
		return prop;
	}

	
}
