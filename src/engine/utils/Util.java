package engine.utils;

import java.text.DecimalFormat;

public class Util {

	static public String customFormat(String pattern, double value ) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(value);
		return output;
	}	
}
