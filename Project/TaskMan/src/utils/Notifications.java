/*
 * This file is part of TaskMan
 *
 * Copyright (C) 2012 Jed Barlow, Mark Galloway, Taylor Lloyd, Braeden Petruk
 *
 * TaskMan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskMan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TaskMan.  If not, see <http://www.gnu.org/licenses/>.
 */

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
