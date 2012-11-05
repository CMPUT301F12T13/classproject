package utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Facilitates the display of quick notifications
 */
public class Notifications {

    public static String NOT_IMPLEMENTED = "This feature is not yet implemented.";

    /**
     * Shows a Toast with the specified message for a LENGTH_SHORT period of time
     * @param context		The Context to show it in
     * @param message		The message to display
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a Toast with the specified message for the specified length of time
     * @param context		The Context to show it in
     * @param message		The message to display
     * @param length		The length of time to display the message
     * 							Toast.LENGTH_SHORT  or  Toast.LENGTH_LONG
     */
    public static void showToast(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }

}
