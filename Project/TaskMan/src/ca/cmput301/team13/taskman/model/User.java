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

/**
 * Holds the identity of a user.
 */
public class User {
    private String identifier;
    public User(String identifier) {
        this.identifier = identifier;
    }
    public String getIdentifier() {
        return identifier;
    }
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
    @Override
    public String toString() {
        return identifier;
    }
}
