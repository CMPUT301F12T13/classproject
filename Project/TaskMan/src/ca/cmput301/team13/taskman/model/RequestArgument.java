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

package ca.cmput301.team13.taskman.model;

import android.net.Uri;


public class RequestArgument {
	
	private String name;
	private Object data;
	
	public RequestArgument(String name, Object data) {
		this.name = name;
		this.data = data;
	}
	
	public String getName() {
		return name;
	}

	public Object getData() {
		return data;
	}

	public String toString() {
		return Uri.encode(this.name) + "=" + Uri.encode(this.data.toString());
	}

}
