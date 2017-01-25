package com.temenos.adapter.mule.T24outbound.utils;



import javax.swing.*;

public class DirectoryPickUp {


	
	public String openDlg(){
		String result="";
		JFileChooser chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choose directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);
		//    
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
			String workDirectory = chooser.getCurrentDirectory().getPath();
			String pickedUpDirectory = chooser.getSelectedFile().getPath();
			result = pickedUpDirectory;
			System.out.println("getCurrentDirectory: "  +  workDirectory);
			System.out.println("getSelectedFile: "  +  pickedUpDirectory);
		}
		else {
			System.out.println("No Selection ");
		}
		
		return result; 
	}
	
	public static String getDir(){
		DirectoryPickUp instance = new DirectoryPickUp();
		return instance.openDlg();
	}
}
