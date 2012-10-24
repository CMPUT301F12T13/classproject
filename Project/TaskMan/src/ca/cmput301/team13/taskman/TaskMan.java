package ca.cmput301.team13.taskman;

import ca.cmput301.team13.taskman.model.VirtualRepository;

import android.app.Application;
import android.content.res.Configuration;

public class TaskMan extends Application {
	
	private VirtualRepository repository;
	
	public TaskMan() {
		//instantiate a global context for the repository
		repository = new VirtualRepository(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
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
}
