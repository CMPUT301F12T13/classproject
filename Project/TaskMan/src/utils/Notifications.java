package utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Facilitates the display of quick notifications
 */
public class Notifications {
	
	public static String NOT_IMPLEMENTED = "This feature is not yet implemented.";
	
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context context, String message, int length) {
		Toast.makeText(context, message, length).show();
	}

}
