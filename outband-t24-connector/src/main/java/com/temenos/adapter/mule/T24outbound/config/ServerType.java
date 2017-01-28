package com.temenos.adapter.mule.T24outbound.config;

public enum ServerType {
	JBOSS_7_2, JBOSS_4_2_3, WEBSPHERE, WEBLOGIC_11G, WEBLOGIC_12C;

	public static String getServerTypeSelector(ServerType type) {
		if (type.equals(JBOSS_7_2)) {
			return "JBoss 7.2";
		} else if (type.equals(JBOSS_4_2_3)) {
			return "JBoss 4.2.3";
		} else if (type.equals(WEBSPHERE)) {
			return "Websphere AS 8.5";
		} else if (type.equals(WEBLOGIC_11G)) {
			return "WebLogic 11g";
		} else if (type.equals(WEBLOGIC_12C)) {
			return "WebLogic 12c";
		}

		return "Undefined";
	}
	
	public static ServerType resolve(String type){
		if (type.equals("JBoss 7.2")) {
			return JBOSS_7_2;
		} else if (type.equals("JBoss 4.2.3")) {
			return JBOSS_4_2_3;
		} else if (type.equals("Websphere AS 8.5")) {
			return WEBSPHERE;
		} else if (type.equals("WebLogic 11g")) {
			return WEBLOGIC_11G;
		} else if (type.equals("WebLogic 12c")) {
			return WEBLOGIC_12C;
		}
		return null;
	}

}
