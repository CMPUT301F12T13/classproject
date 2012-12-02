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
