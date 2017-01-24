package com.temenos.adapter.mule.T24inbound.connector.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Properties;

import org.mule.util.FileUtils;

import com.temenos.adapter.mule.T24inbound.connector.metadata.extract.T24BaseInboundMetadataExctractor;
import com.temenos.adapter.mule.T24inbound.connector.metadata.extract.T24BaseInboundMetadataExctractor;
import com.temenos.adapter.mule.T24inbound.connector.utils.IoResourseUtil;
import com.temenos.adapter.mule.T24inbound.connector.utils.PasswdUtil;

public class IoResourseUtil {

	
	public static final String LOAD_ANY  = "LOAD_ANY";
	
	public static final String LOAD_SCHEMA  = "LOAD_SCHEMA";
	public static final String LOAD_CREDENTIALS  = "LOAD_CREDENTIALS";
	public static final String WS_CO_CODE = "WS_CO_CODE";
	public static final String WS_USR_NAME = "WS_USR_NAME";
	public static final String WS_ENC_PASS = "WS_ENC_PASS";
	
	public static final String AGENT_USR_NAME = "AGENT_USR_NAME";
	public static final String AGENT_ENC_PASS = "AGENT_ENC_PASS";
	
	public static final String BASE_DIR ="/";
	public static final String SHEMA_DIR = "D:/Schemas";
	public static final String SHEMA_DIR_KEY = "SHEMA_DIR_KEY";
	
	
	private InputStream tryInStreams(String fName){
		InputStream is = null;
		is =  this.getClass().getResourceAsStream(fName);
		if(is != null) {
			//System.out.println("Get metadata resourse (id=1) " + fName);
			return is;
		}
		is =  Thread.currentThread().getContextClassLoader().getResourceAsStream(fName);
		if(is != null) {
			//System.out.println("Get metadata resourse (id=2) " + fName);
			return is;
		}
		is =  IoResourseUtil.class.getResourceAsStream(fName);
		if(is != null) {
			//System.out.println("Get metadata resourse (id=3) " + fName);
			return is;
		}
		is =  IoResourseUtil.class.getClassLoader().getResourceAsStream(fName);
		if(is != null) {
			//System.out.println("Get metadata resourse (id=4) " + fName);
			return is;
		}
		is =  this.getClass().getClassLoader().getResourceAsStream(fName);
		if(is != null) {
			//System.out.println("Get metadata resourse (id=5) " + fName);
			return is;
		}
		//System.out.println("NONE OF THEM WORKED FOR: " + fName);
		return null;
	}
	
	
	private InputStream readConfigarableResourse(String path, String resourseFile) throws RuntimeException { //D:\TestOut\Credentials.txt
		InputStream is = null;
		try{
			if(path.equals(SHEMA_DIR)){
				is =   new FileInputStream(path+File.separatorChar+resourseFile); 
			}else{
				
				String alterbateDir = path.substring(path.lastIndexOf(File.separatorChar)+1);
				String possibleFilePaths [] = {resourseFile, File.separatorChar + resourseFile , File.separatorChar  + T24BaseInboundMetadataExctractor.INPUT_SCHEMA_FOLDER_NAME + "/" + resourseFile, File.separatorChar +  T24BaseInboundMetadataExctractor.OUTPUT_SCHEMA_FOLDER_NAME  + "/" + resourseFile, File.separatorChar +  T24BaseInboundMetadataExctractor.METADATA_FOLDER_NAME +"/"+resourseFile ,"/"+resourseFile, path+resourseFile, alterbateDir+resourseFile};
				for(String fpath : possibleFilePaths){
					
					is = tryInStreams(fpath);
					if(is!=null) {
						System.out.println("Get resource: " + fpath);
						break;
					}
				}
				String fullPath = path+resourseFile;
				if(is==null){
					 is = new FileInputStream(fullPath); 
					 System.out.println("Get resourse (absolute path) " + fullPath);
					 
				}
			}
 		
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		return is;
	}
	
	public static boolean isDirPrepeared(String directory) {
		  if(directory == null || directory.isEmpty()) return false;
		  File dir = new File(directory);
		  if (! dir.exists()){
			  dir.mkdir();
		  }
		  Path file = dir.toPath();
		  if (file == null || !Files.exists(file)) return false;
		  else return Files.isDirectory(file);
	}
	
	public static boolean isFile(String filePath){
		if(filePath == null || filePath.isEmpty()) return false;
		File dir = new File(filePath);
		Path file = dir.toPath();
		if (file == null || !Files.exists(file)) return false;
		return true;
	}
	
	public static boolean isDirSyntaxCorrect(String path) {
        try {

        	Paths.get(path);
        }catch (InvalidPathException e) { 
            return false;
        }catch ( NullPointerException e){
        	return false;
        }

        return true;
    }

	/**
	 * Reads a properties file
	 * @param filename
	 * @param resourseType
	 * @return Properties
	 * @throws RuntimeException
	 */
	public  Properties readResourseFile(String filename, String location, String resourseType)  throws RuntimeException {

		
		if(resourseType==null || filename == null){
			throw new RuntimeException("Incorrect recourse type");
		}
		if(resourseType.isEmpty() || filename.isEmpty()){
			throw new RuntimeException("Incorrect recourse type");
		}
		if(location==null || location.isEmpty()) {
			location = File.separator;
		}
		Properties prop = new Properties();
		InputStream fis = readConfigarableResourse(location, filename);

		if(fis != null){
			try {
				prop.load(fis);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}finally{
				try {
					fis.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
			}		
			if(resourseType.equals(LOAD_CREDENTIALS) ){				
				String encrypted_ws_pass = prop.getProperty(WS_ENC_PASS);
				String decrypted_ws_pass = PasswdUtil.decrypt(encrypted_ws_pass);
				prop.setProperty(WS_ENC_PASS, decrypted_ws_pass);
				
				String encrypted_agent_pass = prop.getProperty(AGENT_ENC_PASS);
				String decrypted_agent_pass = PasswdUtil.decrypt(encrypted_agent_pass);
				prop.setProperty(AGENT_ENC_PASS, decrypted_agent_pass);
			}
		}else{
			throw new RuntimeException("Cannot find resourse");
		}
		return prop;
	}
	
	/**
	 * Reads a file content as String
	 * @param filename
	 * @param resourseType
	 * @return Properties
	 * @throws RuntimeException
	 */
	public String readSchemaFile(String filename, String location)  throws RuntimeException {


		if(filename.isEmpty()){
			throw new RuntimeException("Incorrect schema file");
		}
		if(location==null || location.isEmpty()) {
			location = File.separator;
		}

		InputStream fis = readConfigarableResourse(location, filename);
		if(fis != null){
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
	
			String line;
			try {
	
				br = new BufferedReader(new InputStreamReader(fis));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
	
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				fis.close();
			} catch (IOException e) {
				throw new RuntimeException("schema resourse load failed!");
			}
			return sb.toString();
		}else{
			throw new RuntimeException("Cannot load schema resourse!");
		}
		
	}
	

	/**
	 * 
	 * @param fName
	 * @param uname
	 * @param pass
	 * @param coCode
	 * @param agentUser
	 * @param agentPass
	 * @param directory 
	 * @return boolean
	 */
	public boolean writeCredentialsFile(String fName, String uname, String pass, String coCode, String agentUser, String agentPass, String directory){		
		OutputStream  fos= null;
		try {

			File f = new File(directory, fName);
			fos = new FileOutputStream(f);			

			Properties prop = new Properties();
			String ws_enc_pass = PasswdUtil.encrypt(pass);
			String agent_enc_pass = PasswdUtil.encrypt(agentPass);
			prop.put(WS_CO_CODE, coCode);
			prop.put(WS_USR_NAME, uname);
			prop.put(WS_ENC_PASS, ws_enc_pass);
			prop.put(AGENT_USR_NAME, agentUser);
			prop.put(AGENT_ENC_PASS, agent_enc_pass);
			prop.put(SHEMA_DIR_KEY, directory);	
			
			prop.store(fos, null);
		} catch (GeneralSecurityException e) {

			return false;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} 
		finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					return false;
				}
			}
		}
		return true;		
	}
	
	/**
	 * Writes Properties object to file
	 * @param prop
	 * @param filename
	 * @return true or false
	 */
	public boolean writePropertiesToFile(Properties prop, String directory ,String filename){
		OutputStream  fos= null;
		try{

			File f = null; 
			f= new File(directory+File.separatorChar+ filename); ///src/main/api/ , paswor
			fos = new FileOutputStream(f);
			prop.store(fos, null);
		}catch (FileNotFoundException e) {
			return false;
		}catch (IOException e) {
			return false;
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					return false;
				}
			}else{
				System.out.println("Can't resolve schema directory for writing");
				return false;
			}
		}
		return true;
	}
	
	public void writeSchemas(String filePath, String content) throws IOException {
		FileUtils.writeStringToFile(new File(filePath), content);
	}
	
	public String createDirectory(String directory, String folder){
		if(directory == null || directory.isEmpty()) {
			return null;
		}
		
		String fullFolder = directory;
		if(!folder.isEmpty()){
			fullFolder +=  File.separatorChar + folder;
		}
		
		File dir = new File(fullFolder);
		if (! dir.exists()){
			dir.mkdir();
		}
		return fullFolder;
	}
}
