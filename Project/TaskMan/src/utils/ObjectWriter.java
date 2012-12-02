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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.util.Base64;

public class ObjectWriter {
	
	/**
	 * Writes a Serializable object to a String
	 * @param object			The object to serialize
	 * @return					String the Base64 String representation of the object
	 * @throws IOException
	 */
	public static String objectToString(Serializable object) throws IOException {
		ByteArrayOutputStream objectBytes = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput = new ObjectOutputStream(objectBytes);
		objectOutput.writeObject(object);
		objectOutput.close();
		return new String(Base64.encode(objectBytes.toByteArray(), 0));
	}
	
	/**
	 * Converts a Base64 String into an Object
	 * @param string		The string to un-serialize
	 * @return				The created object
	 * @throws StreamCorruptedException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object stringToObject(String string) throws StreamCorruptedException, IOException, ClassNotFoundException {
		Object object;
		byte[] objectData = Base64.decode(string.getBytes(), 0);
		ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(objectData));
		object = objectInput.readObject();
		objectInput.close();
		return object;
	}

}
