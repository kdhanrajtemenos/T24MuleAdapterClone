package com.temenos.adapter.mule.T24outbound.utils;

import javax.xml.bind.JAXBElement;

/* NOT USED*/
public class JAXBElementToObject {
	
	public static Object convert(JAXBElement<?> _in){
		return _in.getValue();
	}
}
