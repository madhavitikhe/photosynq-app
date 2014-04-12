package com.photosynq.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.CommonUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "PhotoSynqDB";
	// Table Names
	private static final String TABLE_RESEARCH_PROJECT = "research_project";

	// Reasearch Project Table - column names
	private static final String C_RECORD_HASH = "record_hash";
	private static final String C_ID = "id";
	private static final String C_NAME = "name";
	private static final String C_DESC = "desc";
	private static final String C_DIR_TO_COLLAB = "dir_to_collab";
	private static final String C_START_DATE = "start_date";
	private static final String C_END_DATE = "end_date";
	private static final String C_IMAGE_CONTENT_TYPE = "image_content_type";
	private static final String C_BETA = "beta";

	// Reaserch Project table create statement
	private static final String CREATE_TABLE_RESEARCH_PROJECT = "CREATE TABLE "
			+ TABLE_RESEARCH_PROJECT + "(" + C_RECORD_HASH
			+ " TEXT PRIMARY KEY," + C_ID + " TEXT," + C_NAME + " TEXT,"
			+ C_DESC + " TEXT," + C_DIR_TO_COLLAB + " TEXT," + C_START_DATE
			+ " TEXT," + C_END_DATE + " TEXT," + C_BETA + " TEXT,"
			+ C_IMAGE_CONTENT_TYPE + " TEXT" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_RESEARCH_PROJECT);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESEARCH_PROJECT);

		// create new tables;
		onCreate(db);

	}

	// Insert row in db
	public boolean createResearchProject(ResearchProject rp) {

		SQLiteDatabase db = this.getWritableDatabase();
										
		ContentValues values = new ContentValues();
		values.put(C_ID, null != rp.getId() ? rp.getId() : "");
		values.put(C_NAME, null != rp.getName() ? rp.getName() : "");
		values.put(C_DESC, null != rp.getDesc() ? rp.getDesc() : "");
		values.put(C_DIR_TO_COLLAB,null != rp.getDir_to_collab() ? rp.getDir_to_collab() : "");
		values.put(C_START_DATE,null != rp.getStart_date() ? rp.getStart_date() : "");
		values.put(C_END_DATE, null != rp.getEnd_date() ? rp.getEnd_date() : "");
		values.put(C_BETA, null != rp.getBeta() ? rp.getBeta() : "");
		values.put(C_IMAGE_CONTENT_TYPE,null != rp.getImage_content_type() ? rp.getImage_content_type(): "");
		values.put(C_RECORD_HASH, CommonUtils.getRecordHash(rp));
		// insert row
		long row_id = db.insert(TABLE_RESEARCH_PROJECT, null, values);

		if (row_id >= 0) {
			return true;
		} else {
			return false;
		}

	}

	//Fetch row from db
	public ResearchProject getResearchProject(String recordHash ) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_RESEARCH_PROJECT + " WHERE "
	            + C_RECORD_HASH + " = '" + recordHash + "'";
	 
	    Log.e("DATABASE_HELPER_getResearchProject", selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null)
	        c.moveToFirst();
	 
	    ResearchProject rp = new ResearchProject();
	    rp.setId(c.getString(c.getColumnIndex(C_ID)));
	    rp.setName(c.getString(c.getColumnIndex(C_NAME)));
	    rp.setDesc(c.getString(c.getColumnIndex(C_DESC)));
	    rp.setDir_to_collab(c.getString(c.getColumnIndex(C_DIR_TO_COLLAB)));
	    rp.setStart_date(c.getString(c.getColumnIndex(C_START_DATE)));
	    rp.setEnd_date(c.getString(c.getColumnIndex(C_END_DATE)));
	    rp.setImage_content_type(c.getString(c.getColumnIndex(C_IMAGE_CONTENT_TYPE)));
	    rp.setRecord_hash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
	    rp.setBeta(c.getString(c.getColumnIndex(C_BETA)));

	    return rp;
	}
	
	//Fetch row from db
		public List<ResearchProject> getAllResearchProjects() {
		    SQLiteDatabase db = this.getReadableDatabase();
		    List<ResearchProject> researchProjects = new ArrayList<ResearchProject>();
		    String selectQuery = "SELECT  * FROM " + TABLE_RESEARCH_PROJECT ;
		 
		    Log.e("DATABASE_HELPER_getAllResearchProject", selectQuery);
		 
		    Cursor c = db.rawQuery(selectQuery, null);
		 
		    if (c.moveToFirst()) {
		        do {
		        	ResearchProject rp = new ResearchProject();
				    rp.setId(c.getString(c.getColumnIndex(C_ID)));
				    rp.setName(c.getString(c.getColumnIndex(C_NAME)));
				    rp.setDesc(c.getString(c.getColumnIndex(C_DESC)));
				    rp.setDir_to_collab(c.getString(c.getColumnIndex(C_DIR_TO_COLLAB)));
				    rp.setStart_date(c.getString(c.getColumnIndex(C_START_DATE)));
				    rp.setEnd_date(c.getString(c.getColumnIndex(C_END_DATE)));
				    rp.setImage_content_type(c.getString(c.getColumnIndex(C_IMAGE_CONTENT_TYPE)));
				    rp.setRecord_hash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
				    rp.setBeta(c.getString(c.getColumnIndex(C_BETA)));
		 
		            // adding to todo list
				    researchProjects.add(rp);
		        } while (c.moveToNext());
		    }

		    return researchProjects;
		}
		
	/*
	 * Updating a Research Project
	 */
	public boolean updateResearchProject(ResearchProject rp) {
		
		//recordhash is unique record identifier if its not present then return false 
		
		if(null==rp.getRecord_hash() )
		{
			return false;
		}
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_ID, null != rp.getId() ? rp.getId() : "");
		values.put(C_NAME, null != rp.getName() ? rp.getName() : "");
		values.put(C_DESC, null != rp.getDesc() ? rp.getDesc() : "");
		values.put(C_DIR_TO_COLLAB,null != rp.getDir_to_collab() ? rp.getDir_to_collab() : "");
		values.put(C_START_DATE,null != rp.getStart_date() ? rp.getStart_date() : "");
		values.put(C_END_DATE, null != rp.getEnd_date() ? rp.getEnd_date() : "");
		values.put(C_BETA, null != rp.getBeta() ? rp.getBeta() : "");
		values.put(C_IMAGE_CONTENT_TYPE,null != rp.getImage_content_type() ? rp.getImage_content_type(): "");
		values.put(C_RECORD_HASH, rp.getRecord_hash());

		
		int rowsaffected = db.update(TABLE_RESEARCH_PROJECT, values, C_RECORD_HASH + " = ?",
							new String[] { String.valueOf(rp.getRecord_hash()) });
		// if update fails that indicates there is no then create new row
		if(rowsaffected <= 0)
		{
			return createResearchProject(rp);
		}
		// updating row
		return false;
	}
	
	/*
	 * Deleting a todo
	 */
	public void deleteResearchProject(String recordHash) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_RESEARCH_PROJECT, C_RECORD_HASH + " = ?",
	            new String[] { String.valueOf(recordHash) });
	}
	
	// closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    
	
}
