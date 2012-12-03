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

package ca.cmput301.team13.taskman.model.storage;

import java.io.Serializable;

/**
 * Holds the identity of a user.
 */
public class User implements Serializable {

	private static final long serialVersionUID = 805912187647638458L;
	private String identifier;
    /**
     * Creates a new instance.
     * @param identifier the string to identify the user
     */
    public User(String identifier) {
        this.identifier = identifier;
    }
    /**
     * Returns the identifier string for the user.
     * @return the identifier string for the user
     */
    public String getIdentifier() {
        return identifier;
    }
    /**
     * Compares user identities.
     */
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o instanceof User) {
            return identifier.equals(((User)o).getIdentifier());
        } else if (o instanceof String) {
            return identifier.equals(o);
        }
        return false;
    }
    /**
     * Returns the user identity as a string.
     * @return the string representation of the user identity
     */
    @Override
    public String toString() {
        return identifier;
    }
}
