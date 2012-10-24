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

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalRepository {
	//Database connection
	private SQLiteDatabase db = null;
	//Database constants and actions
	private RepoHelper helper;
	//Virtual Repository link
	private VirtualRepository vr;
	
	public LocalRepository(Context context, VirtualRepository vr) {
		helper = new RepoHelper(context);
		this.vr = vr;
	}

	void open() throws SQLException {
		db = helper.getWritableDatabase();
	}
	
	public void openTestConnection() {
		db = SQLiteDatabase.create(null);
	}

	void close() {
		helper.close();
	}
	
	void assertOpen() {
		if (db == null) throw new RuntimeException("" +
				"LocalRepository: The repo's DB connection needs to be instantiated " +
				"before creating a task. Call open() first.");
	}
	
	/**
	 * Creates a new Task, with no title, description, or requirements
	 * @param creator The User that has created the Task
	 * @return the Task, with no non-housekeeping values yet set
	 */
	Task createTask(User creator) {
		assertOpen();
		
		Task t = null;
		ContentValues values = new ContentValues();
		values.put(RepoHelper.CREATED_COL, new Date().getTime());
		values.put(RepoHelper.LASTMODIFIED_COL, new Date().getTime());
		values.put(RepoHelper.TITLE_COL, "");
		values.put(RepoHelper.DESC_COL, "");
		values.put(RepoHelper.CREATOR_COL, creator.toString());
		long insertId = db.insert(RepoHelper.TASKS_TBL, null, values);
		
		if(insertId == -1) {
			Log.w("LogStore", "Failed to write LogEntry into database.");
			throw new RuntimeException("Database Write Failure.");
		}
		
		Cursor cursor = db.query(RepoHelper.TASKS_TBL,
				RepoHelper.TASKS_COLS, RepoHelper.ID_COL + " = " + insertId, null,
				null, null, null);
		
		if (cursor.moveToFirst()) {
			t = new Task(
					cursor.getInt(0),//ID
					new Date(cursor.getLong(4)),//Date Created
					new Date(cursor.getLong(5)),//Date Last Modified
					new User(cursor.getString(3)),//Creator
					cursor.getString(1),//Title
					cursor.getString(2),//Description
					new ArrayList<Requirement>(),//Current requirements
					vr
					);
		}
		
		cursor.close();
		return t;
	}
	
	/**
	 * Creates a new Requirement, with no description or fulfillments
	 * @param creator The User that has created the Requirement 
	 * @param task The Task to add the Requirement to
	 * @param contentType The desired content type of the requirement
	 * @return the Requirement, with no non-housekeeping values yet set
	 */
	Requirement createRequirement(User creator, Task task, Requirement.contentType contentType) {
		assertOpen();
		ContentValues values = new ContentValues();
		values.put(RepoHelper.CREATED_COL, new Date().getTime());
		values.put(RepoHelper.LASTMODIFIED_COL, new Date().getTime());
		values.put(RepoHelper.TASK_COL, task.getId());
		values.put(RepoHelper.DESC_COL, "");
		values.put(RepoHelper.CONTENTTYPE_COL, contentType.ordinal());
		values.put(RepoHelper.CREATOR_COL, creator.toString());
		long insertId = db.insert(RepoHelper.REQS_TBL, null, values);
		
		if(insertId == -1) {
			Log.w("LogStore", "Failed to write LogEntry into database.");
			throw new RuntimeException("Database Write Failure.");
		}
		
		Cursor cursor = db.query(RepoHelper.REQS_TBL,
				RepoHelper.REQS_COLS, RepoHelper.ID_COL + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		
		Requirement r = new Requirement(
				cursor.getInt(0),//ID
				new Date(cursor.getLong(5)),//Date Created
				new Date(cursor.getLong(6)),//Date Last Modified
				new User(cursor.getString(4)),//Creator
				cursor.getString(3),//Description
				Requirement.contentType.values()[cursor.getInt(2)],//Content Type
				new ArrayList<Fulfillment>(),//Current requirements
				vr
				);
		
		task.addRequirement(r);
		
		cursor.close();
		return r;
	}
	
	Fulfillment createFulfillment(User creator, Requirement req) {
		assertOpen();
		ContentValues values = new ContentValues();
		values.put(RepoHelper.CREATED_COL, new Date().getTime());
		values.put(RepoHelper.LASTMODIFIED_COL, new Date().getTime());
		values.put(RepoHelper.REQ_COL, req.getId());
		values.put(RepoHelper.CONTENTTYPE_COL, req.getContentType().ordinal());
		values.put(RepoHelper.CREATOR_COL, creator.toString());
		long insertId = db.insert(RepoHelper.REQS_TBL, null, values);
		
		if(insertId == -1) {
			Log.w("LogStore", "Failed to write LogEntry into database.");
			throw new RuntimeException("Database Write Failure.");
		}
		
		Cursor cursor = db.query(RepoHelper.REQS_TBL,
				RepoHelper.REQS_COLS, RepoHelper.ID_COL + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		
		Fulfillment f = new Fulfillment(
				cursor.getInt(0),//ID
				new Date(cursor.getLong(5)),//Date Created
				new Date(cursor.getLong(6)),//Date Last Modified
				Requirement.contentType.values()[cursor.getInt(2)],//Content Type
				new User(cursor.getString(4)),//Creator
				vr
				);
		
		req.addFulfillment(f);
		
		cursor.close();
		return f;
	}
	
	/**
	 * Updates the backing structure to acknowledge any changes that have occurred
	 * @param t the Task to update
	 */
	void updateTask(Task t) {
		assertOpen();
		ContentValues values = new ContentValues();
		values.put(RepoHelper.CREATED_COL, t.getCreatedDate().getTime());
		values.put(RepoHelper.LASTMODIFIED_COL, t.getLastModifiedDate().getTime());
		values.put(RepoHelper.TITLE_COL, t.getTitle());
		values.put(RepoHelper.DESC_COL, t.getDescription());
		values.put(RepoHelper.CREATOR_COL, t.getCreator().toString());
		
		int updateCount = db.update(RepoHelper.TASKS_TBL, values, RepoHelper.ID_COL + "=" + t.getId(), null);
		
		if(updateCount != 1) 
			throw new RuntimeException("Database update failed!");
	}
	
	/**
	 * Update the backing structure to acknowledge any changes that have occurred
	 * @param r the Requirement to update
	 */
	void updateRequirement(Requirement r) {
		assertOpen();
		ContentValues values = new ContentValues();
		values.put(RepoHelper.CREATED_COL, r.getCreatedDate().getTime());
		values.put(RepoHelper.LASTMODIFIED_COL, r.getLastModifiedDate().getTime());
		values.put(RepoHelper.ID_COL, r.getId());
		values.put(RepoHelper.DESC_COL, r.getDescription());
		values.put(RepoHelper.CONTENTTYPE_COL, r.getContentType().ordinal());
		values.put(RepoHelper.CREATOR_COL, r.getCreator().toString());
		
		int updateCount = db.update(RepoHelper.REQS_TBL, values, RepoHelper.ID_COL + "=" + r.getId(), null);
		
		if(updateCount != 1) 
			throw new RuntimeException("Database update failed!");
	}
	
	/**
	 * Update the backing structure to acknowledge any changes that have occurred
	 * @param f the Fulfillment to update
	 */
	void updateFulfillment(Fulfillment f) {
		assertOpen();
		ContentValues values = new ContentValues();
		values.put(RepoHelper.CREATED_COL, f.getCreatedDate().getTime());
		values.put(RepoHelper.LASTMODIFIED_COL, f.getLastModifiedDate().getTime());
		values.put(RepoHelper.ID_COL, f.getId());
		//TODO: Logic to convert typed Fulfillment content into a blob
		
		values.put(RepoHelper.CREATOR_COL, f.getCreator().toString());
		
		int updateCount = db.update(RepoHelper.FULS_TBL, values, RepoHelper.ID_COL + "=" + f.getId(), null);
		
		if(updateCount != 1) 
			throw new RuntimeException("Database update failed!");
	}
	
	/**
	 * Load a list of tasks, restricted by the defined TaskFilter
	 * @param	filter		TaskFilter			The filter by which to restrict results
	 * @return				ArrayList<Task>		The list of tasks received from the DB
	 */
	ArrayList<Task> loadTasks(TaskFilter filter) {
		assertOpen();
		ArrayList<Task> tasks = new ArrayList<Task>();
		
		Cursor cursor = db.query(RepoHelper.TASKS_TBL, RepoHelper.TASKS_COLS, filter.toString(), null, null, null, null);
		
		if (cursor.moveToFirst()) {
			do {
				Task t = new Task(
						cursor.getInt(0),//ID
						new Date(cursor.getLong(4)),//Date Created
						new Date(cursor.getLong(5)),//Date Last Modified
						new User(cursor.getString(3)),//Creator
						cursor.getString(1),//Title
						cursor.getString(2),//Description
						new ArrayList<Requirement>(),//Current requirements
						vr
						);
				tasks.add(t);
			} while (cursor.moveToNext());
		}
		
		return tasks;
	}
	
	/**
	 * Loads a list of Requirements for the specified Task
	 * @param	t 	Task 						The Task to find Requirements for
	 * @return		ArrayList<Requirement>		The list of Requirements for the specified Task
	 */
	ArrayList<Requirement> loadRequirementsForTask(Task t) {
		assertOpen();
		ArrayList<Requirement> reqs = new ArrayList<Requirement>();
		
		Cursor cursor = db.query(RepoHelper.REQS_TBL,
				RepoHelper.REQS_COLS, RepoHelper.TASK_COL + " = " + t.getId(), null,
				null, null, null);
		
		//If we have requirements, load them
		if(cursor.moveToFirst()) {
			do {
				Requirement r = new Requirement(
						cursor.getInt(0),//ID
						new Date(cursor.getLong(5)),//Date Created
						new Date(cursor.getLong(6)),//Date Last Modified
						new User(cursor.getString(4)),//Creator
						cursor.getString(3),//Description
						Requirement.contentType.values()[cursor.getInt(2)],//Content Type
						new ArrayList<Fulfillment>(),//TODO: load current fulfillments
						vr
						);
				reqs.add(r);
			} while (cursor.moveToNext());
		}
		
		return reqs;
	}
	
	/**
	 * Loads a list of fulfillments for the specified requirement
	 * @param	r 	Requirement 				The requirement to find fulfillments for
	 * @return		ArrayList<Fulfillment>		The list of fulfillments for the specified requirement	
	 */
	ArrayList<Fulfillment> loadFulfillments(Requirement r) {
		assertOpen();
		ArrayList<Fulfillment> fulfillments = new ArrayList<Fulfillment>();
		
		Cursor cursor = db.query(RepoHelper.REQS_TBL,
				RepoHelper.REQS_COLS, RepoHelper.REQ_COL + " = " + r.getId(), null,
				null, null, null);
		
		//If we have requirements, load them
		if(cursor.moveToFirst()) {
			do {
				Fulfillment f = new Fulfillment(
						cursor.getInt(0),//ID
						new Date(cursor.getLong(5)),//Date Created
						new Date(cursor.getLong(6)),//Date Last Modified
						r.getContentType(), //Content type
						new User(cursor.getString(4)),//Creator
						vr
						);
				fulfillments.add(f);
			} while (cursor.moveToNext());
		}
		
		return fulfillments;
	}

}

class RepoHelper  extends SQLiteOpenHelper{
	//Tables
	public static final String TASKS_TBL = "tasks";
	public static final String REQS_TBL = "requirements";
	public static final String FULS_TBL = "fulfillments";
	//Columns
	public static final String ID_COL = "id";
	public static final String TITLE_COL = "title";
	public static final String DESC_COL = "description";
	public static final String CREATED_COL = "created";
	public static final String LASTMODIFIED_COL = "lastModified";
	public static final String CREATOR_COL = "creator";
	public static final String TASK_COL = "taskID";
	public static final String REQ_COL = "requirementID";
	public static final String CONTENTTYPE_COL = "contentType";
	public static final String CONTENT_COL = "content";
	//Columns as found in tables
	public static final String[] TASKS_COLS = {ID_COL, TITLE_COL, DESC_COL, CREATOR_COL, CREATED_COL, LASTMODIFIED_COL};
	private static final String[] TASKS_COLTYPES = {"integer primary key autoincrement", "text not null", "integer not null", "integer not null", "integer not null", "integer not null"};
	public static final String[] REQS_COLS = {ID_COL, TASK_COL, CONTENTTYPE_COL, DESC_COL, CREATOR_COL, CREATED_COL, LASTMODIFIED_COL};
	private static final String[] REQS_COLTYPES = {"integer primary key autoincrement", "integer not null", "text not null", "text not null", "text not null", "integer not null", "integer not null"};
	public static final String[] FULS_COLS = {ID_COL, REQ_COL, CONTENT_COL, CREATOR_COL, CREATED_COL, LASTMODIFIED_COL};
	private static final String[] FULS_COLTYPES = {"integer primary key autoincrement", "integer not null", "blob", "text not null", "integer not null", "integer not null"};
    //The name of our database, and SQL Schema version
	private static final String DBNAME = "taskman.db";
	private static final int VERSION = 1;

	public RepoHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		//Construct our create table statements
		String tasksCreate = "create table "+TASKS_TBL+" ( ";
		for(int i=0;i<TASKS_COLS.length;i++) {
			tasksCreate += TASKS_COLS[i] + " " + TASKS_COLTYPES[i];
			if(i<TASKS_COLS.length-1)
				tasksCreate += ", ";
		}
		tasksCreate += ");";
		
		String reqsCreate = "create table "+REQS_TBL+" ( ";
		for(int i=0;i<REQS_COLS.length;i++) {
			reqsCreate += REQS_COLS[i] + " " + REQS_COLTYPES[i];
			if(i<REQS_COLS.length-1)
				reqsCreate += ", ";
		}
		reqsCreate += ");";
		
		String fulsCreate = "create table "+FULS_TBL+" ( ";
		for(int i=0;i<FULS_COLS.length;i++) {
			fulsCreate += FULS_COLS[i] + " " + FULS_COLTYPES[i];
			if(i<FULS_COLS.length-1)
				fulsCreate += ", ";
		}
		fulsCreate += ");";
		
		//Actually run the SQL
		database.execSQL(tasksCreate);
		database.execSQL(reqsCreate);
		database.execSQL(fulsCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("SQLite Helper", "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", wiping Database");
		db.execSQL("DROP TABLE IF EXISTS " + TASKS_TBL);
		db.execSQL("DROP TABLE IF EXISTS " + REQS_TBL);
		db.execSQL("DROP TABLE IF EXISTS " + FULS_TBL);
		onCreate(db);
	}
}