package org.cooltrainer.xval;

import android.util.Log;

/**
 * This class wraps the native Android logging facility and checks
 * log level before logging. This makes it easy to disable verbose
 * logging in production without filling the application with calls
 * to isLoggable(). Just call the CoolLog static methods as you would
 * call Log.
 * 
 * @author 		Nicole Reid <root@cooltrainer.org>
 * @version		2012-07-12
 *
 */
public class CoolLog {

	public static void d(String tag, String msg) {
		if (Log.isLoggable(tag, Log.DEBUG)) {
			Log.d(tag, msg);
		}
	}
	
	public static void d(String tag, String msg, Throwable tr) {
		if (Log.isLoggable(tag, Log.DEBUG)) {
			Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (Log.isLoggable(tag, Log.INFO)) {
			Log.i(tag, msg);
		}
	}
	
	public static void i(String tag, String msg, Throwable tr) {
		if (Log.isLoggable(tag, Log.INFO)) {
			Log.i(tag, msg, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (Log.isLoggable(tag, Log.ERROR)) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (Log.isLoggable(tag, Log.ERROR)) {
			Log.e(tag, msg, tr);
		}
	}

	public static void v(String tag, String msg) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (Log.isLoggable(tag, Log.WARN)) {
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (Log.isLoggable(tag, Log.WARN)) {
			Log.w(tag, msg, tr);
		}
	}
}