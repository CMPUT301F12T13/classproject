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

package ca.cmput301.team13.taskman;

import utils.Identifiers;
import android.app.Application;
import android.content.res.Configuration;
import android.provider.Settings.Secure;
import android.util.Log;
import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;
import ca.cmput301.team13.taskman.model.WebRepository.WebActionCallback;

/**
 * Singleton that provides application-wide access to the
 * {@link VirtualRepository} and {@link User} instances.
 */
public class TaskMan extends Application {

    private VirtualRepository repository;
    private User user;
    private static TaskMan instance;

    public TaskMan() {
    }

    /**
     * Singleton global instance access method.
     * @return Singleton instance
     */
    public static TaskMan getInstance() {
    	if(instance == null) {
    		instance = new TaskMan();
    	}
        return instance;
    }

    /**
     * Handles configuration changed event.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Initializes the app.
     */
    @Override
    public void onCreate() {
    	String androidString = androidString();
        super.onCreate();
        instance = this;
        //instantiate a global context for the repository
        repository = new VirtualRepository(this.getApplicationContext());
        user = new User(androidString);
        Log.w("TaskMan","User set as: "+user);
    }

    /**
     * Gets the Device ID string
     * @return String androidString
     */
    private String androidString() {
        String androidString;
        if (Secure.ANDROID_ID == null) {
            androidString = Identifiers.randomString(15);
        } else {
            androidString = Secure.getString(this.getContentResolver(),
                    Secure.ANDROID_ID);
            if (androidString == null) {
                androidString = Identifiers.randomString(15);
            }
        }
        return androidString;
    }

    /**
     * Handles low memory event.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * Handles termination event.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Returns the application instance of {@link VirtualRepository}.
     * @return the application instance of VirtualRepository
     */
    public VirtualRepository getRepository() {
        return repository;
    }

    /**
     * Returns the {@link User} that is running the app.
     * @return the User that is running the app
     */
    public User getUser() {
    	if(user == null) {
    		user = new User(Identifiers.randomString(15));
    	}
        return user;
    }
}
