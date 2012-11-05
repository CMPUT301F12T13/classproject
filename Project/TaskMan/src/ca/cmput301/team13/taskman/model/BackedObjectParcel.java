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

import java.io.Serializable;

/**
 * Allows clear expression of the ID and type of a BackedObject Parcel
 */
class BackedObjectParcel implements Serializable {
    private static final long serialVersionUID = -6951704268396268805L;
    public int id;
    public String backedObjectType;
    
    /**
     * Creates a BackedObject Parcel for sending it between Activities
     * @param id					The ID of the BackedObject (for acquisition from the Repository)
     * @param backedObjectType		The qualified class name of the object (usually get this from class.getName())
     */
    public BackedObjectParcel(int id, String backedObjectType) {
        this.id = id;
        this.backedObjectType = backedObjectType;
    }
}
