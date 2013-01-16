package org.cooltrainer.xval;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The Xbox360 class takes an Xbox 360 serial number and extracts information from it.
 * 
 * <pre>
 * Xbox 360 serial number format:
 * 
 * Twelve digits, LNNNNNNYWWFF
 * 
 * L = production line number
 * NNNNNN = Cumulative number produced on this line this week
 * Y = last digit of production year
 * WW = week of production year
 * FF = factory code
 * 		02 = Mexico
 * 		03 = Hungary
 * 		05 = China
 * 		06 = Taiwan
 * 		07 = USA, Maybe? http://forums.xbox-scene.com/index.php?showtopic=723654
 * </pre>
 * 
 * @author Nicole Reid <root@cooltrainer.org>
 * @since 2012-06-24
 */
public class Xbox360 {
	
	private int line, number, year, week, factory;
	
	/**
	 * Empty strings for unknown factory codes
	 * so Locale.getDisplayCountry will return nothing
	 */
	private final String[] factories = {
		"",
		"",
		"MX",
		"HU",
		"",
		"CN",
		"TW",
		"US",
		"",
		""
	};

	/**
	 * @param serial		An Xbox 360 serial number
	 */
	public Xbox360(String serial) {
		if(Xbox360.validSerial(serial)) {			
			this.line = Integer.parseInt(serial.substring(0, 1));
			this.number = Integer.parseInt(serial.substring(1, 7));
			int year = Integer.parseInt(serial.substring(7, 8));
			this.week = Integer.parseInt(serial.substring(8, 10));
			this.factory = Integer.parseInt(serial.substring(10, 12));
			
			/*
			 * Year is given as a single digit
			 * 5 through 9 == 2005 through 2009
			 * 0 through 4 == 2010 through 2014
			 * Hopefully they're done making new 360s by 2015 :V
			 */
			this.year = year += (year < 5) ? 2010 : 2000;
		}
	}
	
	/**
	 * Validate an Xbox 360 serial number.
	 * 
	 * A valid serial number is twelve numeric digits.
	 * 
	 * @param serial	Serial number to evaluate
	 * @return			Validity
	 */
	public static boolean validSerial(String serial) {
		return Pattern.matches("^[0-9]{12}$", serial);
	}
	
	/**
	 * Validate an encrypted 'X' value string as displayed in the Dashboard.
	 * 
	 * A valid 'X' value string is sixteen hexadecimal digits.
	 * 
	 * @param xval		'X' value to evaluate
	 * @return			Validity
	 */
	public static boolean validXVal(String xval) {
		//Valid encrypted XVal string is 16 hex digits sans dashes
		return Pattern.matches("^[0-9A-F]{16}$", xval.replace("-", "").toUpperCase());
	}
	
	/**
	 * Get the week of manufacture.
	 * @return			YYYY-MM-DD formatted string of manufacture week.
	 */
	public String mfgDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.WEEK_OF_YEAR, this.week);
		calendar.set(Calendar.YEAR, this.year);

		//Return date of the first day of the manufacture week
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(calendar.getTime());
	}
	
	/**
	 * Get translated name for country of manufacture.
	 * @return			Name of country of manufacture in current Locale.
	 */
	public String factoryName() {
		Locale locale = new Locale(Locale.getDefault().getISO3Language(), factories[factory]);
		return locale.getDisplayCountry();
	}
	
	/**
	 * Get factory line number.
	 * 
	 * @return			The factory line on which this console was built.
	 */
	public int getLine() {
		return this.line;
	}
	
	/**
	 * Get console line count.
	 * 
	 * @return			The cumulative weekly number of this console on its factory line.
	 */
	public int getNumber() {
		return this.number;
	}
}
