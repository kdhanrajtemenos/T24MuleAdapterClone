package com.temenos.adapter.mule.T24inbound.connector.utils;

import java.awt.Frame;
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

import com.temenos.soa.utils.StringUtils;



public class UserCrededntialsInterface {

	
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
			Frame root =JOptionPane.getRootFrame();
			root.setAlwaysOnTop(true);
			JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), userInputs, dialogName, JOptionPane.OK_CANCEL_OPTION);

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

	private void setT24password(String t24password) {
		this.t24password = t24password;
	}

	public String getServicePassword() {
		return servicePassword;
	}

	private void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}
	
	public String saveEncryptedFile(String filePath) throws RuntimeException {
		
		/* Check if the input parameter is valid file path */
		if(StringUtils.isNullOrEmpty(filePath) || !IoResourseUtil.isDirSyntaxCorrect(filePath)){
			throw new RuntimeException("invalid file path");
		}				
		
		/* encrypt user paswords */
		Properties prop = new Properties();
		try {
			prop.put("T24_PASS", PasswdUtil.encrypt(t24password));
			prop.put("SERVICE_PASS", PasswdUtil.encrypt(servicePassword));
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
		io.writePropertiesToFile(prop, folder, fileName);
		
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
		String alternateDir = path.substring(path.lastIndexOf("\\")+1);
		
		/* try several file path locations for resolving at run-time...Note that: "/"+fname - should work at metadata discovery
		 * and fName should work at run-time */
		String possibleLoacations [] = {fName, "/"+fName, "\\"+fName, alternateDir + "/" + fName, alternateDir + "\\" + fName, path + "/" +fName, path + "\\" +fName};
		
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
	public String resolveCredentialFileAndPath(String filePath) throws RuntimeException {
		
		/* Check if the input parameter is valid file path */
		if(StringUtils.isNullOrEmpty(filePath) || !IoResourseUtil.isDirSyntaxCorrect(filePath)){
			throw new RuntimeException("invalid file path");
		}	
		
		/* Split the the directory path and filename*/
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
		
		/* try to read the resource file. The folder parameter will be used as last option otherwise it will be ignored  */
		InputStream is = tryResolveResourse(fileName, folder);
		if(is==null){
			throw new RuntimeException("cannot load resourse! " + fileName);
		}
		
		/* load file contents in to properties object */
		Properties prop = new Properties();
		try {
			prop.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		if(prop == null ||prop.isEmpty()){ 
			throw new RuntimeException("properties file corrupted! ");
		}
		
		/* Get encrypted passwords */
		String encT24Pass = prop.getProperty("T24_PASS");		
		String encServicePass  = prop.getProperty("SERVICE_PASS");
		
		if(StringUtils.isNullOrEmpty(encT24Pass) ||  StringUtils.isNullOrEmpty(encServicePass)){
			throw new RuntimeException("credential file is corrupted!");
		}
		
		/* decrypt passwords and store them into this instance fields for use in connector config at run-time and metadata discovery */
		this.setT24password(PasswdUtil.decrypt(encT24Pass));
		this.setServicePassword(PasswdUtil.decrypt(encServicePass));
		
		/* return only the directory ... it will be used in future for meta data discovery */
		return folder;
	}

	
	public static void main(String [] args) {
		
		UserCrededntialsInterface dial = new UserCrededntialsInterface();
		dial.setInputFields("Fielda", "Fieldb").showUserDialog("test");
	}
	
}
