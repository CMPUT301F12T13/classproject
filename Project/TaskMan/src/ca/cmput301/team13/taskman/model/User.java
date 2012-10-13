package ca.cmput301.team13.taskman.model;

public class User {
	private String identifier;
	public User(String identifier) {
		this.identifier = identifier;
	}
	public String getIdentifier() {
		return identifier;
	}
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof User) {
			return identifier.equals(((User)o).getIdentifier());
		} else if (o instanceof String) {
			return identifier.equals(o);
		}
		return false;
	}
	public String toString() {
		return identifier;
	}
}
