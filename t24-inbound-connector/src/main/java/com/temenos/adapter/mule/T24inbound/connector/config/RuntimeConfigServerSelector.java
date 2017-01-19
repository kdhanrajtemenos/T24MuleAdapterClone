package com.temenos.adapter.mule.T24inbound.connector.config;


/**
 * Enumerator for the design-time runtime selector drop-down list 
 * 
 *   JBoss 7.2/JBoss 4.2.3/Websphere AS 8.5/WebLogic 11g/WebLogic 12c
 */
public enum RuntimeConfigServerSelector {
	JBOSS72, JBOSS423, WEBSPHERE85, WEBLOGIC11G, WEBLOGIC12C;
	
	public static String getRuntimeConfigServerSelector(RuntimeConfigServerSelector type){

		if(type.equals(JBOSS72)){
			return "JBoss 7.2";
		}else if(type.equals(JBOSS423)){
			return "JBoss 4.2.3";
		}else if(type.equals(WEBSPHERE85)){
			return "Websphere AS 8.5";
		}else if(type.equals(WEBLOGIC11G)){
			return "WebLogic 11g";
		}else if(type.equals(WEBLOGIC12C)){
			return "WebLogic 12c";
		}
		return "Undefined";
		
	}
	
	public String toString() {
		return getRuntimeConfigServerSelector(this);
	}
}
