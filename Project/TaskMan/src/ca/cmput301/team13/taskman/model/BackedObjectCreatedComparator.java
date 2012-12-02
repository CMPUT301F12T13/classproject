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

import java.util.Comparator;

/**
 * Compares the creation dates of {@link BackedObject} objects.
 */
public class BackedObjectCreatedComparator implements Comparator<BackedObject> {

	/**
	 * Compares the creation dates of two {@link BackedObject} instances.
	 */
    public int compare(BackedObject bo1, BackedObject bo2) {
        return bo2.getCreatedDate().compareTo(bo1.getCreatedDate());
    }

}
