package ca.cmput301.team13.taskman.test;

import android.util.Log;

public class Logging {
	
	private static String ERROR_TAG = "TestCaseError";
	
	public static void logError(String message) {
		Log.i(Logging.ERROR_TAG, message);
	}

}
