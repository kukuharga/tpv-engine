package com.nuvola.tpv.service;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CsvUtils {

	public static String getCsvContent(List<?> dataList, String[] header, String delimiter) {
		StringBuffer sb = new StringBuffer();
		if (dataList == null || dataList.isEmpty())
			return "";

		dataList.stream().forEach(n -> {
			try {
				Object f;

				for (int i = 0; i < header.length; i++) {
					// Invoke getter method
					f = new PropertyDescriptor(header[i], n.getClass()).getReadMethod().invoke(n);

					// Format and append the result
					sb.append(formatObject(f));

					// Append delimiter for next column or new line after last column
					sb.append((i < header.length - 1) ? delimiter : "\n");
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| IntrospectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		return sb.toString();
	}

	public static boolean isGetter(Method method) {
		if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0) {
			if (method.getName().matches("^get[A-Z].*") && !method.getReturnType().equals(void.class))
				return true;
			if (method.getName().matches("^is[A-Z].*") && method.getReturnType().equals(boolean.class))
				return true;
		}
		return false;
	}

	public static String getHeader(String[] header, String delimiter) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < header.length; i++) {
			sb.append(header[i]);
			sb.append((i < header.length - 1) ? delimiter : "\n");
		}

		return sb.toString();
	}

	private static String formatDate(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		return sdf.format(dt);
	}

	protected static Object formatObject(Object f) {

		f = (f instanceof Boolean) ? ((Boolean) f) ? "Y" : "N" : f;
		f = (f instanceof Date) ? formatDate((Date) f) : f;
		return f != null ? f : "";
	}

}
