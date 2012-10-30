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
