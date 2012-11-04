package ca.cmput301.team13.taskman.model;

import java.io.Serializable;

/**
 * Allows clear expression of the ID and type of a BackedObject Parcel
 */
class BackedObjectParcel implements Serializable {
	private static final long serialVersionUID = -6951704268396268805L;
	public int id;
	public String backedObjectType;
	
	public BackedObjectParcel(int id, String backedObjectType) {
		this.id = id;
		this.backedObjectType = backedObjectType;
	}
}
