package com.temenos.adapter.mule.T24inbound.connector.config;

import java.io.File;
import java.io.IOException;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.Required;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.display.Path;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;

public abstract class AbstractConnectorConfig {

	public static final String DEFAULT_SETTINGS_FILE_NAME = "settings.txt";

	@Configurable
	@Default(value = DEFAULT_SETTINGS_FILE_NAME)
	@Required
	@Path
	@Placement(order = 1, group = "Save Connection settings", tab = "General")
	private String settingsFilePath;

	private String settingsFolder;

	private String settingsFileName;

	private RuntimeConfigSelector runTime;

	public AbstractConnectorConfig(RuntimeConfigSelector runtimeConfigSelector) {
		this.runTime = runtimeConfigSelector;
	}

	/**
	 * Disconnect
	 */
	@Disconnect
	public abstract void disconnect();

	/**
	 * Are we connected
	 */
	@ValidateConnection
	public abstract boolean isConnected();

	/**
	 * Connection id
	 */
	@ConnectionIdentifier
	public String connectionId() {
		return "001";
	}

	/**
	 * @return the runTime
	 */
	public RuntimeConfigSelector getRunTime() {
		return runTime;
	}

	/**
	 * @return the settingsFilePath
	 */
	public String getSettingsFilePath() {
		return settingsFilePath;
	}

	/**
	 * @param settingsFilePath
	 *            the settingsFilePath to set
	 */
	public void setSettingsFilePath(String settingsFilePath) {
		this.settingsFilePath = settingsFilePath;
	}

	/**
	 * @return the settingsFolder
	 */
	public String getSettingsFolder() {
		return settingsFolder;
	}

	/**
	 * @param settingsFolder
	 *            the settingsFolder to set
	 */
	public void setSettingsFolder(String settingsFolder) {
		this.settingsFolder = settingsFolder;
	}

	/**
	 * @return the settingsFileName
	 */
	public String getSettingsFileName() {
		return settingsFileName;
	}

	/**
	 * @param settingsFileName
	 *            the settingsFileName to set
	 */
	public void setSettingsFileName(String settingsFileName) {
		this.settingsFileName = settingsFileName;
	}
	
	public void splitFullFileName() {
        // fill parsed settings file name and directory here
        File file = new File(settingsFilePath);

        // check for existing file and create if missing to fix schema loading
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                //e.printStackTrace();
            }
        }

        if (file.isFile()) {
            setSettingsFolder(file.getParent());
            setSettingsFileName(file.getName());
        }
    }

}
