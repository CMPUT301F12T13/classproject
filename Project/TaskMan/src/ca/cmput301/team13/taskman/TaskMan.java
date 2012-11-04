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

import ca.cmput301.team13.taskman.model.User;
import ca.cmput301.team13.taskman.model.VirtualRepository;

import android.app.Application;
import android.content.res.Configuration;
import android.provider.Settings.Secure;
import android.util.Log;

public class TaskMan extends Application {

    private VirtualRepository repository;
    private User user;
    private static TaskMan instance;

    public TaskMan() {
    }

    public static TaskMan getInstance() {
        return instance;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //instantiate a global context for the repository
        repository = new VirtualRepository(this.getApplicationContext());
        user = new User(Secure.getString(this.getContentResolver(), Secure.ANDROID_ID));
        Log.w("TaskMan","User set as: "+user);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public VirtualRepository getRepository() {
        return repository;
    }

    public User getUser() {
        return user;
    }
}
