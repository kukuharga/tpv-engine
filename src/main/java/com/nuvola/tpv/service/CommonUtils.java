package com.nuvola.tpv.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonUtils {
	public static boolean isEmpty(Collection<?> collections) {
		return (collections == null || collections.isEmpty());
	}
	
	public static boolean isEmpty(String text) {
		return (text == null || "".equals(text.trim()));
	}
	
	public static <E> List<E> getList(List<E>list) {
		return(list != null) ? list : new ArrayList<E>();
	}
	
	public static <E> Set<E> getSet(Set<E>set) {
		return(set != null) ? set : new HashSet<E>();
	}
}
