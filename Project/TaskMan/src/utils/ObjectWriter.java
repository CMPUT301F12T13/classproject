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
