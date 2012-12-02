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

package ca.cmput301.team13.taskman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AudioVideoConversion {

    /**
     * Instantiates a new {@link AudioVideoConversion} object.
     */
    public AudioVideoConversion() {
        
    }
    
    /**
     * Resolves a Uri to an absolute file path.
     *      - Paul Burke's getPath method from: http://stackoverflow.com/a/7857102/95764
     * @param context       The Activity's Context
     * @param uri           The Uri to resolve
     * @return              The resolved Uri
     */
    public String resolvePath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = cursor(context, uri);
            try {
                int column_index = cursor
                .getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) { }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Gets a cursor
     * @param context   The Activity's Context
     * @param uri       The Uri to resolve
     * @return          The cursor
     */
    public Cursor cursor(Context context, Uri uri) {
        String[] projection = { "_data" };
        Cursor cursor = null;
        cursor = context.getContentResolver().query(uri, projection, null,
                null, null);
        return cursor;
    }
    
    /**
     * Creates a short array from audio/video data stored at the given file path.
     * @param path      The path to the audio file
     * @return          The short[] representing the audio data
     */
    public short[] getShort(String path) {
        File file;
        FileInputStream stream = null;
        byte[] bytes = null;
        short[] shorts = null;
        file = new File(path);
        //If video of some kind was generated, attempt to convert it and pass it back to the Task Viewer
        if(file != null) {
            try {
                stream = new FileInputStream(file);
                bytes = new byte[(int)file.length()];
                stream.read(bytes);
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Do the conversion
        if(bytes != null) {
            shorts = new short[bytes.length/2];
            // to turn bytes to shorts as either big endian or little endian. 
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        }
        return shorts;
    }
    
}
