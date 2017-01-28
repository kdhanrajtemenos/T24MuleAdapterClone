package com.temenos.adapter.mule.T24outbound.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import jersey.repackaged.com.google.common.net.InetAddresses;

public class AddressChecker {
	
	/**
	 * Check if the given string is a valid URL address
	 * @param urlStr
	 * @return true or false
	 */
	public static boolean isValidURL(String urlStr) {
        URL u = null;
        try {
            u = new URL(urlStr);
        } catch (MalformedURLException e) {
            return false;
        }
        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
	
	
	/**
	 * Check if the given string is a valid IP address
	 * @param ipStr
	 * @return true or false
	 */
	public static boolean checkHostIp(String ipStr){
		if(!ipStr.isEmpty() && ipStr.equals("localhost")){
			return true;
		}
		try {
			InetAddresses.forString(ipStr);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	

	
}
