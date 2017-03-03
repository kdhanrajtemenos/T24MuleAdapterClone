package com.temenos.adapter.mule.T24outbound.utils;

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

import com.temenos.adapter.mule.T24outbound.config.ConnectorConfig;
import com.temenos.adapter.mule.T24outbound.config.RuntimeConfigSelector;
import com.temenos.adapter.mule.T24outbound.config.ServerType;
import com.temenos.soa.utils.StringUtils;

public class UserCredentialsInterface {

    protected final transient Log log = LogFactory.getLog(getClass());	
	
	public static final String SERVICE_PASS = "SERVICE_PASS";
	public static final String SERVICE_USER = "SERVICE_USER";
	public static final String T24_RUNTIME = "T24_RUNTIME";
	public static final String T24_SERVER_TYPE = "T24_SERVER_TYPE";
	public static final String T24_PORT = "T24_PORT";
	public static final String T24_HOST = "T24_HOST";
	public static final String T24_USER = "T24_USER";
	public static final String T24_PASS = "T24_PASS";
	public static final String T24_NODE_NAMES = "T24_NODE_NAMES";
	public static final String FOLDER = "FOLDER";
	
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
			
		
		/* encrypt user paswords */
		Properties prop = new Properties();
		try {
			
			prop.put(SERVICE_PASS, PasswdUtil.encrypt(servicePassword));
			prop.put(SERVICE_USER, config.getServiceUserName());
			prop.put(T24_RUNTIME, RuntimeConfigSelector.getRunTimeSelector(RuntimeConfigSelector.TAFJ));
			prop.put(T24_SERVER_TYPE, ServerType.getServerTypeSelector(config.getT24ServerType()));
			prop.put(T24_PORT, String.valueOf(config.getT24Port()));
			prop.put(T24_HOST, config.getT24Host());
			prop.put(T24_USER, config.getT24User());
			prop.put(T24_PASS, PasswdUtil.encrypt(t24password));
			prop.put(T24_NODE_NAMES, config.getT24NodeNmes());
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
		
		// will write the properties to the file in the given folder 
		IoResourceUtil io = new IoResourceUtil();
		//if(!io.writePropertiesToFile(prop, config.getSettingsFolder(), config.getSettingsFileName())){
		if(!io.writePropertiesToFile(prop, config.getSettingsFilePath())){
			throw new RuntimeException("Cannot resolve directory for writing " + config.getSettingsFolder()   +  " filename " + config.getSettingsFileName() );
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
		String alternateDir = path.substring(0, path.lastIndexOf(File.separatorChar));
		
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
				/* this should work if storage folder is not part of the flow project */
				is = new FileInputStream( path + "/" +fName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return is;
	}
	
	/**
	 * 
	 * @param connectorConfig - path to credentials file e.g. C:\directory\...\filename.ext
	 * @return the directory path where file exists...this path will be used also for saving the Meta data properties files;
	 * @throws RuntimeException
	 */
	public Properties resolveCredentialFileAndPath(ConnectorConfig config) throws RuntimeException {
		
		String filePath = config.getSettingsFilePath();
		
		/* Check if the input parameter is valid file path */
		if(StringUtils.isNullOrEmpty(filePath)){
			//throw new RuntimeException("invalid settings file path");
			return null;
		}	
		
		/* Split the the directory path and filename*/
		String folder = "";
		String fileName = ConnectorConfig.DEFAULT_SETTINGS_FILE_NAME; //default
		
		boolean isFile = IoResourceUtil.isFile(filePath);
		
		boolean readSettingsMode = false;
		if(!isFile){
			fileName = config.getSettingsFileName();
			folder = config.getSettingsFolder();
			readSettingsMode =  true;
		}else{
			boolean isDirectory  = IoResourceUtil.isDir(filePath);
			if(isDirectory){
				folder = filePath;
				fileName = ConnectorConfig.DEFAULT_SETTINGS_FILE_NAME;
				readSettingsMode =  true;
			}else{
				readSettingsMode = false;
			}
		}
		
		if(!readSettingsMode){
			filePath = filePath.replace("/", File.separator);
			folder = filePath.substring(0,filePath.lastIndexOf(File.separatorChar) );
			fileName  = filePath.substring(filePath.lastIndexOf(File.separatorChar)+1);
		}
		
		/* try to read the resource file. The folder parameter will be used as last option otherwise it will be ignored  */
		InputStream is = tryResolveResourse(fileName, folder);
		if(is==null){
			//throw new RuntimeException("cannot load resourse! " + fileName);
			return null;
		}
		
		/* load file contents in to properties object */
		Properties prop = new Properties();
		try {
			prop.load(is);
		} catch (IOException e) {
			//throw new RuntimeException(e.getMessage());
			return null;
		}
		if(prop == null ||prop.isEmpty()){ 
			//throw new RuntimeException("properties file corrupted! ");
			return null;
		}
		
		if(is != null){
			try {
				is.close();
			} catch (IOException e) {

			}finally{
				
			}
		}
		/* Get encrypted passwords */
		String encT24Pass = prop.getProperty(T24_PASS);		
		String encServicePass  = prop.getProperty(SERVICE_PASS);
		
		if(StringUtils.isNullOrEmpty(encT24Pass)){
			//throw new RuntimeException("setting file is corrupted!");
			return null;
		}

		// no strict check for service password in the configuration file - not mandatory
//		if(StringUtils.isNullOrEmpty(encServicePass)){
//			throw new RuntimeException("setting file is corrupted!");
//		}
		
		// decrypt passwords and store them into this instance fields 
		// for use in connector configuration at run-time and metadata discovery
		this.setT24password(PasswdUtil.decrypt(encT24Pass));
		
		if( ! StringUtils.isNullOrEmpty(encServicePass) ) {
			this.setServicePassword(PasswdUtil.decrypt(encServicePass));
		}
	
		
		prop.put(T24_PASS, this.getT24password() );
		prop.put(SERVICE_PASS, this.getServicePassword() );
		prop.put(FOLDER, folder);
		
		
		config.setSettingsFileName(fileName);
		config.setSettingsFolder(folder);

		return prop;
	}

	
}
