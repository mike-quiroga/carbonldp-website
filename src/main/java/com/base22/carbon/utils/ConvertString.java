package com.base22.carbon.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ConvertString {
	
	/**
	 * Converts the given String to an InputStream.
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream toInputStream(String str) {
		
		return new ByteArrayInputStream(str.getBytes());
		
	}

}