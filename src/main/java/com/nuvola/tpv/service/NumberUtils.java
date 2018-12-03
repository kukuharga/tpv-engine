package com.nuvola.tpv.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {
	public static String getMoneyFormat(double value) {
		NumberFormat nf = DecimalFormat.getInstance(Locale.US);
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setDecimalSeparator('.');
		customSymbol.setGroupingSeparator(',');
		((DecimalFormat)nf).setDecimalFormatSymbols(customSymbol);
		nf.setGroupingUsed(true);
		return nf.format(value);
	}
}
