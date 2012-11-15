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
