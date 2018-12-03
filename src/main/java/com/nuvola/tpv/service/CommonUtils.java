package com.nuvola.tpv.service;

import java.util.Collection;

public class CommonUtils {
	public static boolean isEmpty(Collection<?> collections) {
		return (collections == null || collections.isEmpty());
	}
	
	public static boolean isEmpty(String text) {
		return (text == null || "".equals(text.trim()));
	}
}
