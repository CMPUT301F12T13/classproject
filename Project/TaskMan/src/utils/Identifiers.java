package utils;

import java.util.Date;
import java.util.Random;

public class Identifiers {
	
	private static String characterBase = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	/**
	 * Generates a random string of characters (using characterBase as the pool of available characters)
	 * @param length			The length of the output String
	 * @return
	 */
	public static String randomString(int length) {
		return randomString(characterBase, length);
	}

	/**
	 * Generates a random string of characters
	 * @param characterBase		The set of characters available for use in the output String
	 * @param length			The length of the output String
	 * @return
	 */
	public static String randomString(String characterBase, int length) {
		Random random = new Random(new Date().getTime());
		String output = "";
		for(int i=0; i<length; i++) {
			output += characterBase.charAt(random.nextInt(characterBase.length()));
		}
		return output;
	}
}
