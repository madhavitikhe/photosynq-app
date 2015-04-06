package com.photosynq.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.photosynq.app.model.AppSettings;
import com.photosynq.app.model.Data;
import com.photosynq.app.model.Macro;
import com.photosynq.app.model.Option;
import com.photosynq.app.model.ProjectLead;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.RememberAnswers;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 2;
	// Database Name
	private static final String DATABASE_NAME = "PhotoSynqDB";
	// Table Names
	private static final String TABLE_RESEARCH_PROJECT = "research_project";
	private static final String TABLE_QUESTION = "question";
	private static final String TABLE_OPTION = "option";
	private static final String TABLE_PROTOCOL = "protocol";
	private static final String TABLE_MACRO = "macro";
	private static final String TABLE_RESULTS = "results";
	private static final String TABLE_SETTINGS = "settings";
	private static final String TABLE_DATA = "data";
    private static final String TABLE_PROJECT_LEAD = "project_lead";
    private static final String TABLE_REMEMBER_ANSWERS = "remember_answers";

	// Common column names
	public static final String C_RECORD_HASH = "record_hash";
	public static final String C_ROW_ID = "rowid";

	// Research Project columns
    private static final String C_PROJECT_LEAD_ID = "plead_id";
	private static final String C_PROJECT_DIR_TO_COLLAB = "dir_to_collab";
	private static final String C_PROJECT_START_DATE = "start_date";
	private static final String C_PROJECT_END_DATE = "end_date";
	private static final String C_PROJECT_IMAGE_URL = "image_url";
	private static final String C_PROJECT_BETA = "beta";
	public static final String C_PROJECT_ID = "project_id";
	private static final String C_PROJECT_PROTOCOL_IDS = "protocols_ids";
    private static final String C_PROJECT_NAME = "name";
    private static final String C_PROJECT_DESCRIPTION = "description";
    private static final String C_PROJECT_SLUG = "slug";

    // Project lead columns
    private static final String C_LEAD_ID = "plead_id";
    private static final String C_LEAD_NAME = "name";
    private static final String C_LEAD_DATA_COUNT = "data_count";
    private static final String C_LEAD_IMAGE_URL = "image_url";

	// Question and Option Table - column names
    public static final String C_QUESTION_ID = "question_id";// Question
	private static final String C_QUESTION_TEXT = "question_text";// Question
	public static final String C_OPTION_TEXT = "option";// Option
    public static final String C_QUESTION_TYPE = "question_type";

	// Protocol column names
    private static final String C_PROTOCOL_ID = "protocol_id";
    private static final String C_PROTOCOL_NAME = "protocol_name";
    private static final String C_PROTOCOL_DESCRIPTION = "protocol_description";
	public static final String C_PROTOCOL_JSON = "protocol_json";
    private static final String C_PROTOCOL_MACRO_ID = "macro_id";
    public static final String C_PROTOCOL_MACRO_SLUG = "macro_slug";
    private static final String C_PROTOCOL_PRE_SEL = "protocol_pre_sel";

	// Macro column names
    private static final String C_MACRO_ID = "macro_id";
    private static final String C_MACRO_NAME = "macro_name";
    private static final String C_MACRO_DESCRIPTION = "macro_description";
	private static final String C_MACRO_DEFAULT_X_AXIS = "default_x_axis";
	private static final String C_MACRO_DEFAULT_Y_AXIS = "default_y_axis";
	private static final String C_MACRO_JAVASCRIPT_CODE = "javascript_code";
    private static final String C_MACRO_SLUG = "macro_slug";

	// Results column name
	// private static final String C_RECORD_TIME = "record_time";
	public static final String C_READING = "reading";
	private static final String C_UPLOADED = "uploaded";

	// Settings Table Columns names
	private static final String C_USER_ID = "user_id";
	public static final String C_MODE_TYPE = "mode_type";
	public static final String C_CONNECTION_ID = "connection_id";

	// Data table colimn names.
	private static final String C_TYPE = "type";
	private static final String C_VALUES = "value";

    // Remember_Answers table colimn names.
    private static final String C_IS_REMEMBER = "is_remember";
    private static final String C_SELECTED_OPTION_TEXT = "selected_option_text";


    private Context context;

	// Reaserch Project table create statement
	//
	private static final String CREATE_TABLE_RESEARCH_PROJECT = "CREATE TABLE "
			+ TABLE_RESEARCH_PROJECT + "(" + C_RECORD_HASH
			+ " TEXT PRIMARY KEY," + C_PROJECT_ID + " TEXT," + C_PROJECT_NAME + " TEXT,"
			+ C_PROJECT_DESCRIPTION + " TEXT," + C_PROJECT_DIR_TO_COLLAB + " TEXT,"
            + C_PROJECT_LEAD_ID + " TEXT,"
			+ C_PROJECT_START_DATE + " TEXT," + C_PROJECT_END_DATE + " TEXT," + C_PROJECT_BETA
			+ " TEXT," + C_PROJECT_PROTOCOL_IDS + " TEXT," + C_PROJECT_IMAGE_URL + " TEXT"
			+ ")";

    // Project Lead table create statement
    private static final String CREATE_TABLE_PROJECT_LEAD = "CREATE TABLE "
            + TABLE_PROJECT_LEAD + "(" + C_RECORD_HASH
            + " TEXT PRIMARY KEY," + C_LEAD_ID + " TEXT," + C_LEAD_NAME + " TEXT,"
            + C_LEAD_DATA_COUNT + " TEXT," + C_LEAD_IMAGE_URL + " TEXT"
            + ")";

	// Question table create statement
	private static final String CREATE_TABLE_QUESTION = "CREATE TABLE "
			+ TABLE_QUESTION + "(" + C_RECORD_HASH + " TEXT PRIMARY KEY," + C_PROJECT_ID
			+ " TEXT," + C_QUESTION_ID + " TEXT," + C_QUESTION_TEXT + " TEXT, "+C_QUESTION_TYPE+" integer )";

	// Answer table create statement
	private static final String CREATE_TABLE_OPTION = "CREATE TABLE "
			+ TABLE_OPTION + "(" + C_RECORD_HASH + " TEXT PRIMARY KEY," + C_OPTION_TEXT
			+ " TEXT ," + C_PROJECT_ID + " TEXT," + C_QUESTION_ID + " TEXT)";

	// Protocol table create statement
	private static final String CREATE_TABLE_PROTOCOL = "CREATE TABLE "
			+ TABLE_PROTOCOL + "(" + C_RECORD_HASH + " TEXT PRIMARY KEY," + C_PROTOCOL_ID
			+ " TEXT ," + C_PROTOCOL_NAME + " TEXT ," + C_PROTOCOL_JSON + " TEXT ,"
			+ C_PROTOCOL_DESCRIPTION + " TEXT ," + C_PROTOCOL_MACRO_ID + " TEXT ," + C_PROTOCOL_MACRO_SLUG + " TEXT ,"
            + C_PROTOCOL_PRE_SEL +" TEXT)";

	// Macro table create statement
	private static final String CREATE_TABLE_MACRO = "CREATE TABLE "
			+ TABLE_MACRO + "(" + C_RECORD_HASH + " TEXT PRIMARY KEY," + C_MACRO_ID + " TEXT ,"
			+ C_MACRO_NAME + " TEXT ," + C_MACRO_DESCRIPTION + " TEXT ," + C_MACRO_DEFAULT_X_AXIS
			+ " TEXT ," + C_MACRO_DEFAULT_Y_AXIS + " TEXT ," + C_MACRO_JAVASCRIPT_CODE
			+ " TEXT ," + C_MACRO_SLUG + " TEXT)";

	private static final String CREATE_TABLE_RESULTS = "CREATE TABLE "
			+ TABLE_RESULTS + "(" + C_RECORD_HASH + " TEXT PRIMARY KEY," + C_PROJECT_ID
			+ " TEXT ," + C_UPLOADED + " TEXT ," + C_READING + " TEXT)";

	private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE "
			+ TABLE_SETTINGS + "(" + C_USER_ID + " TEXT," + C_MODE_TYPE
			+ " TEXT ," + C_CONNECTION_ID + " TEXT ," + C_PROJECT_ID
			+ " TEXT )";

	private static final String CREATE_TABLE_DATA = "CREATE TABLE "
			+ TABLE_DATA + "(" + C_USER_ID + " TEXT," + C_PROJECT_ID + " TEXT,"
			+ C_QUESTION_ID + " TEXT," + C_TYPE + " TEXT," + C_VALUES + " TEXT )";

    // Remember_Answer table create statement
    private static final String CREATE_TABLE_REMEMBER_ANSWERS = "CREATE TABLE "
            + TABLE_REMEMBER_ANSWERS + "(" + C_USER_ID + " TEXT," + C_PROJECT_ID + " TEXT,"
            + C_QUESTION_ID + " TEXT," + C_SELECTED_OPTION_TEXT + " TEXT," + C_IS_REMEMBER + " TEXT )";

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION); this.context =context;}

	private static DatabaseHelper instance;
    private int mWriteOpenCounter;
    private int mReadOpenCounter;
    private SQLiteDatabase mWriteDatabase;
    private SQLiteDatabase mReadDatabase;

	public static synchronized DatabaseHelper getHelper(Context context) {
		if (instance == null)
			instance = new DatabaseHelper(context);

		return instance;
	}


//    public synchronized SQLiteDatabase openWriteDatabase() {
//        mWriteOpenCounter++;
//        if(mWriteOpenCounter == 1) {
//            // Opening new database
//            mWriteDatabase = this.getWritableDatabase();
//        }
//        return mWriteDatabase;
//    }


//    public synchronized void closeWriteDatabase() {
//        mWriteOpenCounter--;
//        if(mWriteOpenCounter == 0) {
//            // Closing database
//            if(mWriteDatabase.isOpen())
//                mWriteDatabase.close();
//        }
//        if(mWriteOpenCounter < 0)
//            mWriteOpenCounter = 0;
//    }
//
//    public synchronized SQLiteDatabase openReadDatabase() {
//        mReadOpenCounter++;
//        if(mReadOpenCounter == 1) {
//            // Opening new database
//            mReadDatabase = this.getReadableDatabase();
//        }
//        return mReadDatabase;
//    }

//    public synchronized void closeReadDatabase() {
//        mReadOpenCounter--;
//        if(mReadOpenCounter == 0) {
//            // Closing database
//            if(mReadDatabase.isOpen())
//                mReadDatabase.close();
//        }
//        if(mReadOpenCounter < 0)
//            mReadOpenCounter = 0;
//    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_RESEARCH_PROJECT);
		db.execSQL(CREATE_TABLE_QUESTION);
		db.execSQL(CREATE_TABLE_OPTION);
		db.execSQL(CREATE_TABLE_PROTOCOL);
		db.execSQL(CREATE_TABLE_MACRO);
		db.execSQL(CREATE_TABLE_RESULTS);
		db.execSQL(CREATE_TABLE_SETTINGS);
		db.execSQL(CREATE_TABLE_DATA);
        db.execSQL(CREATE_TABLE_PROJECT_LEAD);
        db.execSQL(CREATE_TABLE_REMEMBER_ANSWERS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESEARCH_PROJECT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROTOCOL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MACRO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECT_LEAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMEMBER_ANSWERS);
		// create new tables;
		onCreate(db);

	}

	public long createResult(ProjectResult result) {
        long retVal = -1;
		try {
			SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_PROJECT_ID,
					null != result.getProjectId() ? result.getProjectId() : "");
			values.put(C_READING,
					null != result.getReading() ? result.getReading() : "");
			values.put(C_UPLOADED,
					null != result.getUploaded() ? result.getUploaded() : "");
			// insert row
			long row_id = db.insert(TABLE_RESULTS, null, values);

			if (row_id >= 0) {
				retVal = row_id;
			}
		} catch (SQLiteConstraintException contraintException) {

		} catch (SQLException sqliteException) {

		}
        //closeWriteDatabase();
        return retVal;
	}

//	public List<ProjectResult> getAllResultsForProject(String projectId) {
//		SQLiteDatabase db = openReadDatabase();
//		List<ProjectResult> projectsResults = new ArrayList<ProjectResult>();
//		String selectQuery = "SELECT  rowid,* FROM " + TABLE_RESULTS
//				+ " WHERE " + C_PROJECT_ID + " = " + projectId;
//
//		Log.e("DATABASE_HELPER_getAllResearchProject", selectQuery);
//
//		Cursor c = db.rawQuery(selectQuery, null);
//
//		if (c.moveToFirst()) {
//			do {
//				ProjectResult rp = new ProjectResult();
//				rp.setId(c.getString(c.getColumnIndex(C_ROW_ID)));
//				rp.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
//				rp.setReading(c.getString(c.getColumnIndex(C_READING)));
//				rp.setUploaded(c.getString(c.getColumnIndex(C_UPLOADED)));
//
//				// adding to todo list
//				projectsResults.add(rp);
//			} while (c.moveToNext());
//		}
//
//		c.close();
//        closeReadDatabase();
//		return projectsResults;
//	}
//
	public List<ProjectResult> getAllUnUploadedResults() {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
        List<ProjectResult> projectsResults = new ArrayList<ProjectResult>();
		String selectQuery = "SELECT rowid,* FROM " + TABLE_RESULTS + " WHERE "
				+ C_UPLOADED + " = 'N'";

		Log.e("DATABASE_HELPER_getAllResearchProject", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				ProjectResult rp = new ProjectResult();
				rp.setId(c.getString(c.getColumnIndex(C_ROW_ID)));
				rp.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				rp.setReading(c.getString(c.getColumnIndex(C_READING)));
				rp.setUploaded(c.getString(c.getColumnIndex(C_UPLOADED)));

				// adding to todo list
				projectsResults.add(rp);
			} while (c.moveToNext());
		}

		c.close();
        //closeReadDatabase();
		return projectsResults;
	}
//
//	public boolean updateResults(ProjectResult result) {
//
//        boolean retVal = false;
//		SQLiteDatabase db = openWriteDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(C_PROJECT_ID,
//				null != result.getProjectId() ? result.getProjectId() : "");
//		values.put(C_READING, null != result.getReading() ? result.getReading()
//				: "");
//		values.put(C_UPLOADED,
//				null != result.getUploaded() ? result.getUploaded() : "");
//
//		int rowsaffected = db.update(
//				TABLE_RESULTS,
//				values,
//				C_PROJECT_ID + " = ? and " + C_ROW_ID + " =?",
//				new String[] { String.valueOf(result.getProjectId()),
//						String.valueOf(result.getId()) });
//		// updating row
//		if (rowsaffected > 0) {
//			retVal = true;
//		}
//        closeWriteDatabase();
//		return retVal;
//	}

	public void deleteResult(String rowid) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
		db.delete(TABLE_RESULTS, C_ROW_ID + " = ?",
				new String[] { String.valueOf(rowid) });
        //closeWriteDatabase();
	}

	// Insert research project information in database
	public boolean createResearchProject(ResearchProject rp) {
        boolean retVal = false;
		try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_PROJECT_ID, null != rp.getId() ? rp.getId() : "");
			values.put(C_PROJECT_NAME, null != rp.getName() ? rp.getName() : "");
			values.put(C_PROJECT_DESCRIPTION,
					null != rp.getDescription() ? rp.getDescription() : "");
			values.put(C_PROJECT_DIR_TO_COLLAB,
					null != rp.getDirToCollab() ? rp.getDirToCollab() : "");
            values.put(C_PROJECT_LEAD_ID,
                    null != rp.getpLeadId() ? rp.getpLeadId() : "");
			values.put(C_PROJECT_START_DATE,
					null != rp.getStartDate() ? rp.getStartDate() : "");
			values.put(C_PROJECT_END_DATE, null != rp.getEndDate() ? rp.getEndDate()
					: "");
			values.put(C_PROJECT_BETA, null != rp.getBeta() ? rp.getBeta() : "");
			values.put(C_PROJECT_PROTOCOL_IDS,
					null != rp.getProtocols_ids() ? rp.getProtocols_ids() : "");
			values.put(C_PROJECT_IMAGE_URL, null != rp.getImageUrl() ? rp.getImageUrl()
					: "");
			values.put(C_RECORD_HASH, rp.getRecordHash());
			// insert row
			long row_id = db.insert(TABLE_RESEARCH_PROJECT, null, values);

			if (row_id >= 0) {
				retVal = true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
			Log.d("DATABASE_HELPER_RESEARCH_PROJECTS",
					"Record already present in database for record hash ="
							+ rp.getRecordHash());

		} catch (SQLException sqliteException) {}
        //closeWriteDatabase();
        return retVal;
	}

	// Get research project information from database
	public ResearchProject getResearchProject(String id) {
        ResearchProject rp = null;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_RESEARCH_PROJECT
				+ " WHERE " + C_PROJECT_ID + " = '" + id + "'";

		Log.e("DATABASE_HELPER_getResearchProject", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();

            if (c.getCount() > 0) {
                rp = new ResearchProject();
                rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
                rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
                rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
                rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
                rp.setpLeadId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
                rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
                rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
                rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
                rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
                rp.setProtocols_ids(c.getString(c.getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
                rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));
            }
            c.close();
        }
        //closeReadDatabase();
        return rp;
	}

	// Get all research project information from database.
	public List<ResearchProject> getAllResearchProjects() {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
		List<ResearchProject> researchProjects = new ArrayList<ResearchProject>();
		String selectQuery = "SELECT  * FROM " + TABLE_RESEARCH_PROJECT;

		Log.e("DATABASE_HELPER_getAllResearchProject", selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				ResearchProject rp = new ResearchProject();
				rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
				rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
				rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
                rp.setpLeadId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
				rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
				rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
				rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
				rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
				rp.setProtocols_ids(c.getString(c
						.getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
				rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));

				// adding to todo list
				researchProjects.add(rp);
			} while (c.moveToNext());
		}

		c.close();
        //closeReadDatabase();
		return researchProjects;
	}

    // Get all research project information from database where name contains search string.
    public List<ResearchProject> getAllResearchProjects(String searchString) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
        List<ResearchProject> researchProjects = new ArrayList<ResearchProject>();
        String selectQuery = "SELECT  * FROM " + TABLE_RESEARCH_PROJECT
                + " WHERE " + C_PROJECT_NAME + " LIKE '%" + searchString + "%'";

        Log.e("DATABASE_HELPER_getAllResearchProject", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                ResearchProject rp = new ResearchProject();
                rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
                rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
                rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
                rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
                rp.setpLeadId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
                rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
                rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
                rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
                rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
                rp.setProtocols_ids(c.getString(c
                        .getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
                rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));

                // adding to todo list
                researchProjects.add(rp);
            } while (c.moveToNext());
        }

        c.close();
        //closeReadDatabase();
        return researchProjects;
    }

	/*
	 * Updating a Research Project
	 */
	public boolean updateResearchProject(ResearchProject rp) {

        boolean retVal = false;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_PROJECT_ID, null != rp.getId() ? rp.getId() : "");
		values.put(C_PROJECT_NAME, null != rp.getName() ? rp.getName() : "");
		values.put(C_PROJECT_DESCRIPTION,
				null != rp.getDescription() ? rp.getDescription() : "");
		values.put(C_PROJECT_DIR_TO_COLLAB,
				null != rp.getDirToCollab() ? rp.getDirToCollab() : "");
        values.put(C_PROJECT_LEAD_ID,
                null != rp.getpLeadId() ? rp.getpLeadId() : "");
		values.put(C_PROJECT_START_DATE, null != rp.getStartDate() ? rp.getStartDate()
				: "");
		values.put(C_PROJECT_END_DATE, null != rp.getEndDate() ? rp.getEndDate() : "");
		values.put(C_PROJECT_BETA, null != rp.getBeta() ? rp.getBeta() : "");
		values.put(C_PROJECT_IMAGE_URL, null != rp.getImageUrl() ? rp.getImageUrl()
				: "");
		values.put(C_PROJECT_PROTOCOL_IDS,
				null != rp.getProtocols_ids() ? rp.getProtocols_ids() : "");
		values.put(C_RECORD_HASH, rp.getRecordHash());

		int rowsaffected = db.update(TABLE_RESEARCH_PROJECT, values, C_PROJECT_ID
				+ " = ?", new String[] { String.valueOf(rp.getId()) });
		// if update fails that indicates there is no then create new row
		if (rowsaffected <= 0) {
			retVal = createResearchProject(rp);
		}else{ // updating row
            retVal = true;
        }
		//closeWriteDatabase();
		return retVal;
	}

//	/*
//	 * Deleting a research project
//	 */
//	public void deleteResearchProject(String id) {
//		SQLiteDatabase db = openWriteDatabase();
//		db.delete(TABLE_RESEARCH_PROJECT, C_ID + " = ?",
//				new String[] { String.valueOf(id) });
//        closeWriteDatabase();
//	}
//

    // Insert project lead information in database
    public boolean createProjectLead(ProjectLead projectLead) {
        boolean retVal = false;
        try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(C_LEAD_ID, null != projectLead.getId() ? projectLead.getId() : "");
            values.put(C_LEAD_NAME, null != projectLead.getName() ? projectLead.getName() : "");
            values.put(C_LEAD_DATA_COUNT,
                    null != projectLead.getDataCount() ? projectLead.getDataCount() : "");
            values.put(C_LEAD_IMAGE_URL,
                    null != projectLead.getImageUrl() ? projectLead.getImageUrl() : "");

            long row_id = db.insert(TABLE_PROJECT_LEAD, null, values);

            if (row_id >= 0) {
                retVal = true;
            }
        } catch (SQLiteConstraintException contraintException) {
            // If data already present then handle the case here.
            Log.d("DATABASE_HELPER_PROJECT_LEAD",
                    "Record already present in database for record hash ="
                            + projectLead.getRecordHash());

        } catch (SQLException sqliteException) {}
        //closeWriteDatabase();
        return retVal;
    }

    // Get project lead information from database
    public ProjectLead getProjectLead(String id) {
        ProjectLead projectLead = null;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PROJECT_LEAD
                + " WHERE " + C_LEAD_ID + " = '" + id + "'";

        Log.e("DATABASE_HELPER_getProjectLead", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();

            if (c.getCount() > 0) {
                projectLead = new ProjectLead();
                projectLead.setId(c.getString(c.getColumnIndex(C_LEAD_ID)));
                projectLead.setName(c.getString(c.getColumnIndex(C_LEAD_NAME)));
                projectLead.setDataCount(c.getString(c.getColumnIndex(C_LEAD_DATA_COUNT)));
                projectLead.setImageUrl(c.getString(c.getColumnIndex(C_LEAD_IMAGE_URL)));
            }
            c.close();
        }
        //closeReadDatabase();
        return projectLead;
    }

    /*
     * Updating a Project Lead
     */
    public boolean updateProjectLead(ProjectLead projectLead) {

        boolean retVal = false;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_LEAD_ID, null != projectLead.getId() ? projectLead.getId() : "");
        values.put(C_LEAD_NAME, null != projectLead.getName() ? projectLead.getName() : "");
        values.put(C_LEAD_DATA_COUNT,
                null != projectLead.getDataCount() ? projectLead.getDataCount() : "");
        values.put(C_LEAD_IMAGE_URL,
                null != projectLead.getImageUrl() ? projectLead.getImageUrl() : "");

        int rowsaffected = db.update(TABLE_PROJECT_LEAD, values, C_LEAD_ID
                + " = ?", new String[] { String.valueOf(projectLead.getId()) });
        // if update fails that indicates there is no then create new row
        if (rowsaffected <= 0) {
            retVal = createProjectLead(projectLead);
        }else{ // updating row
            retVal = true;
        }
        //closeWriteDatabase();
        return retVal;
    }

    // Insert a question in database
	public boolean createQuestion(Question que) {
        boolean retVal = false;
		try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_RECORD_HASH,
					null != que.getRecordHash() ? que.getRecordHash() : "");
			values.put(C_QUESTION_TEXT,
					null != que.getQuestionText() ? que.getQuestionText() : "");
			values.put(C_QUESTION_ID, que.getQuestionId());
			values.put(C_PROJECT_ID, que.getProjectId());
            values.put(C_QUESTION_TYPE, que.getQuestionType());
			// insert row
			long row_id = db.insert(TABLE_QUESTION, null, values);
			if (row_id >= 0) {
				retVal = true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
		} catch (SQLException sqliteException) {
		}
        //closeWriteDatabase();
        return retVal;
	}

	public boolean updateQuestion(Question question) {
        boolean retVal = false;

        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_RECORD_HASH,
				null != question.getRecordHash() ? question.getRecordHash()
						: "");
		values.put(C_QUESTION_ID,
				null != question.getQuestionId() ? question.getQuestionId()
						: "");
		values.put(C_QUESTION_TEXT,
				null != question.getQuestionText() ? question.getQuestionText()
						: "");
		values.put(C_PROJECT_ID, question.getProjectId());
        values.put(C_QUESTION_TYPE, question.getQuestionType());

		int rowsaffected = db.update(
				TABLE_QUESTION,
				values,
				C_QUESTION_ID + " = ? and " + C_PROJECT_ID + " =?",
				new String[] { String.valueOf(question.getQuestionId()),
						String.valueOf(question.getProjectId()) });
        String user_id = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);
        if(question.getQuestionType() == Question.PROJECT_DEFINED) {
            Data data = new Data(user_id, question.getProjectId(), question.getQuestionId(), "", "");
            updateData(data);
        }
		// if update fails that indicates there is no then create new row
		if (rowsaffected <= 0) {
			retVal = createQuestion(question);
		}else {
            // updating row
            retVal = true;
        }
        //closeWriteDatabase();
        return retVal;
	}

	// Insert Option in database
	public boolean createOption(Option op) {
        boolean retVal = false;
		try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_RECORD_HASH, op.getRecordHash());
			values.put(C_OPTION_TEXT,
					null != op.getOptionText() ? op.getOptionText() : "");
			values.put(C_QUESTION_ID,
					null != op.getQuestionId() ? op.getQuestionId() : "");
			values.put(C_PROJECT_ID,
					null != op.getProjectId() ? op.getProjectId() : "");
			// insert row
			long row_id = db.insert(TABLE_OPTION, null, values);
			if (row_id >= 0) {
				retVal = true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
		} catch (SQLException sqliteException) {
		}
        //closeWriteDatabase();
        return retVal;
	}

	public boolean updateOption(Option option) {
        boolean retVal = false;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_RECORD_HASH, option.getRecordHash());
		values.put(C_OPTION_TEXT,
				null != option.getOptionText() ? option.getOptionText() : "");
		values.put(C_QUESTION_ID,
				null != option.getQuestionId() ? option.getQuestionId() : "");
		values.put(C_PROJECT_ID,
				null != option.getProjectId() ? option.getProjectId() : "");

		int rowsaffected = db.update(TABLE_OPTION, values, C_RECORD_HASH
				+ " = ?",
				new String[] { String.valueOf(option.getRecordHash()) });
		// if update fails that indicates there is no then create new row
		if (rowsaffected <= 0) {
			retVal = createOption(option);
		}else {
            retVal = true;
        }
        //closeWriteDatabase();
        return retVal;
	}

	public Question getQuestionForProject(String project_id,String question_id) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
		Question que= new Question();
		String selectQuery = "SELECT  * FROM " + TABLE_QUESTION + " WHERE "
				+ C_PROJECT_ID + " = " + project_id
				+ " AND " + C_QUESTION_ID + " = " + question_id;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {

				String questionId = c
						.getString(c.getColumnIndex(C_QUESTION_ID));
				que.setQuestionText(c.getString(c
						.getColumnIndex(C_QUESTION_TEXT)));
				que.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				que.setQuestionId(questionId);
                que.setQuestionType(c.getInt(c.getColumnIndex(C_QUESTION_TYPE)));
				que.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));

				// adding to todo list
				if (!que.getQuestionText().equals("")) {
					String selectOptionsQuery = "SELECT  * FROM "
							+ TABLE_OPTION + " WHERE " + C_QUESTION_ID + " = "
							+ questionId + " and " + C_PROJECT_ID + " = "
							+ project_id;

					Cursor optionCursor = db.rawQuery(selectOptionsQuery, null);
					if (optionCursor.moveToFirst()) {
						List<String> optionlist = new ArrayList<String>();
						do {
							String option = optionCursor.getString(optionCursor
									.getColumnIndex(C_OPTION_TEXT));
							optionlist.add(option);

						} while (optionCursor.moveToNext());

						que.setOptions(optionlist);
						optionCursor.close();

					}
			}
		}
		c.close();
        //closeReadDatabase();
		return que;
	}

	// Get list of all questions for given project.
	public List<Question> getAllQuestionForProject(String project_id) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
		List<Question> questions = new ArrayList<Question>();
		String selectQuery = "SELECT  * FROM " + TABLE_QUESTION + " WHERE "
				+ C_PROJECT_ID + " = " + project_id;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Question que = new Question();
				String questionId = c
						.getString(c.getColumnIndex(C_QUESTION_ID));
				que.setQuestionText(c.getString(c
						.getColumnIndex(C_QUESTION_TEXT)));
				que.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				que.setQuestionId(questionId);
                que.setQuestionType(c.getInt(c.getColumnIndex(C_QUESTION_TYPE)));
				que.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));

				if (!que.getQuestionText().equals("")) {
					String selectOptionsQuery = "SELECT  * FROM "
							+ TABLE_OPTION + " WHERE " + C_QUESTION_ID + " = "
							+ questionId + " and " + C_PROJECT_ID + " = "
							+ project_id;

					Cursor optionCursor = db.rawQuery(selectOptionsQuery, null);
					if (optionCursor.moveToFirst()) {
						List<String> optionlist = new ArrayList<String>();
						do {
							String option = optionCursor.getString(optionCursor
									.getColumnIndex(C_OPTION_TEXT));
							optionlist.add(option);

						} while (optionCursor.moveToNext());

						que.setOptions(optionlist);
						optionCursor.close();

					}
					questions.add(que);
				}
			} while (c.moveToNext());

		}
		c.close();
        //closeReadDatabase();
		return questions;
	}

	// Insert Option in database
	public boolean createProtocol(Protocol protocol) {
        boolean retVal = false;
		try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_RECORD_HASH, protocol.getRecordHash());
			values.put(C_PROTOCOL_ID, null != protocol.getId() ? protocol.getId() : "");
			values.put(C_PROTOCOL_NAME, null != protocol.getName() ? protocol.getName()
					: "");
			values.put(
					C_PROTOCOL_JSON,
					null != protocol.getProtocol_json() ? protocol
							.getProtocol_json() : "");
			values.put(
					C_PROTOCOL_DESCRIPTION,
					null != protocol.getDescription() ? protocol
							.getDescription() : "");
			values.put(C_PROTOCOL_MACRO_ID,
					null != protocol.getMacroId() ? protocol.getMacroId() : "");
			values.put(C_PROTOCOL_MACRO_SLUG, null != protocol.getSlug() ? protocol.getSlug()
					: "");
            values.put(C_PROTOCOL_PRE_SEL,
                    null != protocol.getPreSelected() ? protocol.getPreSelected() : "");
			// insert row
			long row_id = db.insert(TABLE_PROTOCOL, null, values);
			if (row_id >= 0) {
				retVal = true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
		} catch (SQLException sqliteException) {
		}
//        closeWriteDatabase();
        return retVal;
	}

	public boolean updateProtocol(Protocol protocol) {
        boolean retVal = false;

        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_RECORD_HASH, protocol.getRecordHash());
		values.put(C_PROTOCOL_ID, null != protocol.getId() ? protocol.getId() : "");
		values.put(C_PROTOCOL_NAME, null != protocol.getName() ? protocol.getName() : "");
		values.put(
				C_PROTOCOL_JSON,
				null != protocol.getProtocol_json() ? protocol
						.getProtocol_json() : "");
		values.put(C_PROTOCOL_DESCRIPTION,
				null != protocol.getDescription() ? protocol.getDescription()
						: "");
		values.put(C_PROTOCOL_MACRO_ID,
				null != protocol.getMacroId() ? protocol.getMacroId() : "");
		values.put(C_PROTOCOL_MACRO_SLUG, null != protocol.getSlug() ? protocol.getSlug() : "");
        values.put(C_PROTOCOL_PRE_SEL, null != protocol.getPreSelected() ? protocol.getPreSelected() : "");

		int rowsaffected = db.update(TABLE_PROTOCOL, values, C_PROTOCOL_ID + " = ?",
				new String[] { String.valueOf(protocol.getId()) });
		// if update fails that indicates there is no then create new row
		if (rowsaffected <= 0) {
			retVal = createProtocol(protocol);
		}else {
            retVal = true;
        }
        //closeWriteDatabase();
        return retVal;
	}

	// Get all protocols
	public List<Protocol> getAllProtocolsList() {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
		List<Protocol> protocols = new ArrayList<Protocol>();
		String selectQuery = "SELECT  * FROM " + TABLE_PROTOCOL;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Protocol protocol = new Protocol();
                protocol.setRecordHash(c.getString(c
						.getColumnIndex(C_RECORD_HASH)));
				protocol.setId(c.getString(c.getColumnIndex(C_PROTOCOL_ID)));
				protocol.setDescription(c.getString(c
						.getColumnIndex(C_PROTOCOL_DESCRIPTION)));
				protocol.setName(c.getString(c.getColumnIndex(C_PROTOCOL_NAME)));
				protocol.setProtocol_json(c.getString(c
						.getColumnIndex(C_PROTOCOL_JSON)));
				protocol.setSlug(c.getString(c.getColumnIndex(C_PROTOCOL_MACRO_SLUG)));
				protocol.setMacroId(c.getString(c.getColumnIndex(C_MACRO_ID)));
                protocol.setPreSelected(c.getString(c.getColumnIndex(C_PROTOCOL_PRE_SEL)));

				// adding to todo list
				protocols.add(protocol);
			} while (c.moveToNext());
		}
		c.close();
        //closeReadDatabase();
		return protocols;
	}

    // Get pre-selected protocols
    public List<Protocol> getFewProtocolList() {
        List<Protocol> protocols = new ArrayList<Protocol>();
        List<Protocol> allProtocols = getAllProtocolsList();

        for(int proIdx = 0 ; proIdx < allProtocols.size(); proIdx++){
            Protocol protocol = allProtocols.get(proIdx);
            if(protocol.isPreSelected()){
                protocols.add(protocol);
            }

        }

        return protocols;
    }


	public Protocol getProtocol(String protocolId) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
		Protocol protocol = new Protocol();
		String selectQuery = "SELECT  * FROM " + TABLE_PROTOCOL + " WHERE "
				+ C_PROTOCOL_ID + " = " + protocolId;
		;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {

			protocol.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
			protocol.setId(c.getString(c.getColumnIndex(C_PROTOCOL_ID)));
			protocol.setDescription(c.getString(c.getColumnIndex(C_PROTOCOL_DESCRIPTION)));
			protocol.setName(c.getString(c.getColumnIndex(C_PROTOCOL_NAME)));
			protocol.setProtocol_json(c.getString(c
					.getColumnIndex(C_PROTOCOL_JSON)));
			protocol.setSlug(c.getString(c.getColumnIndex(C_PROTOCOL_MACRO_SLUG)));
			protocol.setMacroId(c.getString(c.getColumnIndex(C_PROTOCOL_MACRO_ID)));
            protocol.setPreSelected(c.getString(c.getColumnIndex(C_PROTOCOL_PRE_SEL)));
		}
		c.close();
        //closeReadDatabase();
		return protocol;
	}

	// Insert Macro in database
	public boolean createMacro(Macro macro) {
        boolean retVal = false;
		try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_RECORD_HASH, macro.getRecordHash());
			values.put(C_MACRO_ID, null != macro.getId() ? macro.getId() : "");
			values.put(C_MACRO_NAME, null != macro.getName() ? macro.getName() : "");
			values.put(C_MACRO_DESCRIPTION,
					null != macro.getDescription() ? macro.getDescription()
							: "");
			values.put(C_MACRO_SLUG, null != macro.getSlug() ? macro.getSlug() : "");
			values.put(C_MACRO_DEFAULT_X_AXIS,
					null != macro.getDefaultXAxis() ? macro.getDefaultXAxis()
							: "");
			values.put(C_MACRO_DEFAULT_Y_AXIS,
					null != macro.getDefaultYAxis() ? macro.getDefaultYAxis()
							: "");
			values.put(
					C_MACRO_JAVASCRIPT_CODE,
					null != macro.getJavascriptCode() ? macro
							.getJavascriptCode() : "");
			// insert row
			long row_id = db.insert(TABLE_MACRO, null, values);
			if (row_id >= 0) {
				retVal = true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
		} catch (SQLException sqliteException) {
		}

        //closeWriteDatabase();
        return retVal;
	}

	public boolean updateMacro(Macro macro) {
        boolean retVal = false;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_RECORD_HASH, macro.getRecordHash());
		values.put(C_MACRO_ID, null != macro.getId() ? macro.getId() : "");
		values.put(C_MACRO_NAME, null != macro.getName() ? macro.getName() : "");
		values.put(C_MACRO_DESCRIPTION,
				null != macro.getDescription() ? macro.getDescription() : "");
		values.put(C_MACRO_SLUG, null != macro.getSlug() ? macro.getSlug() : "");
		values.put(C_MACRO_DEFAULT_X_AXIS,
				null != macro.getDefaultXAxis() ? macro.getDefaultXAxis() : "");
		values.put(C_MACRO_DEFAULT_Y_AXIS,
				null != macro.getDefaultYAxis() ? macro.getDefaultYAxis() : "");
		values.put(C_MACRO_JAVASCRIPT_CODE,
				null != macro.getJavascriptCode() ? macro.getJavascriptCode()
						: "");

		int rowsaffected = db.update(TABLE_MACRO, values, C_MACRO_ID + " = ?",
				new String[] { String.valueOf(macro.getId()) });
		// if update fails that indicates there is no then create new row
		if (rowsaffected <= 0) {
			retVal = createMacro(macro);
		}else{
            retVal = true;
        }
		//closeWriteDatabase();
        return retVal;
	}

//	// get macro from database
//	public Macro getMacro(String id) {
//		SQLiteDatabase db = openReadDatabase();
//
//		String selectQuery = "SELECT  * FROM " + TABLE_MACRO + " WHERE " + C_ID
//				+ " = '" + id + "'";
//
//		Log.e("DATABASE_HELPER_getMacro", selectQuery);
//
//		Cursor c = db.rawQuery(selectQuery, null);
//
//		if (c != null)
//			c.moveToFirst();
//
//		Macro macro = new Macro();
//		macro.setId(c.getString(c.getColumnIndex(C_ID)));
//		macro.setName(c.getString(c.getColumnIndex(C_NAME)));
//		macro.setDescription(c.getString(c.getColumnIndex(C_DESCRIPTION)));
//		macro.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
//		macro.setSlug(c.getString(c.getColumnIndex(C_SLUG)));
//		macro.setDefaultXAxis(c.getString(c.getColumnIndex(C_DEFAULT_X_AXIS)));
//		macro.setDefaultYAxis(c.getString(c.getColumnIndex(C_DEFAULT_Y_AXIS)));
//		macro.setJavascriptCode(c.getString(c.getColumnIndex(C_JAVASCRIPT_CODE)));
//
//        closeReadDatabase();
//		return macro;
//	}

	public List<Macro> getAllMacros() {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
		List<Macro> macros = new ArrayList<Macro>();
		String selectQuery = "SELECT  * FROM " + TABLE_MACRO;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				Macro macro = new Macro();
				macro.setId(c.getString(c.getColumnIndex(C_MACRO_ID)));
				macro.setName(c.getString(c.getColumnIndex(C_MACRO_NAME)));
				macro.setDescription(c.getString(c
						.getColumnIndex(C_MACRO_DESCRIPTION)));
				macro.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
				macro.setSlug(c.getString(c.getColumnIndex(C_MACRO_SLUG)));
				macro.setDefaultXAxis(c.getString(c
						.getColumnIndex(C_MACRO_DEFAULT_X_AXIS)));
				macro.setDefaultYAxis(c.getString(c
						.getColumnIndex(C_MACRO_DEFAULT_Y_AXIS)));
				macro.setJavascriptCode(c.getString(c
						.getColumnIndex(C_MACRO_JAVASCRIPT_CODE)));

				// adding to todo list
				macros.add(macro);
			} while (c.moveToNext());
		}
		c.close();
        //closeReadDatabase();
		return macros;
	}

	public boolean createSettings(AppSettings setting) {
        boolean retVal = false;
		try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_MODE_TYPE, setting.getModeType());
			values.put(C_USER_ID, setting.getUserId());
			values.put(C_CONNECTION_ID, setting.getConnectionId());
			values.put(C_PROJECT_ID, setting.getProjectId());

			// Inserting Row
			long row_id = db.insert(TABLE_SETTINGS, null, values);
			if (row_id >= 0) {
				retVal = true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
		} catch (SQLException sqliteException) {
		}
        //closeWriteDatabase();
        return retVal;
	}

	// Getting single parameters of settings
	public AppSettings getSettings(String userID) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS + " WHERE "
				+ C_USER_ID + " = '" + userID + "'";

		AppSettings setting = new AppSettings();
		setting.setUserId(userID);
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {

			setting.setUserId(c.getString(c.getColumnIndex(C_USER_ID)));
			setting.setModeType(c.getString(c.getColumnIndex(C_MODE_TYPE)));
			setting.setConnectionId(c.getString(c
					.getColumnIndex(C_CONNECTION_ID)));
			setting.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
		}
		c.close();
        //closeReadDatabase();
		return setting;
	}

	// Updating single setting
	public boolean updateSettings(AppSettings setting) {
        boolean retVal = false;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_USER_ID, setting.getUserId());
		values.put(C_MODE_TYPE, setting.getModeType());
		values.put(C_CONNECTION_ID, setting.getConnectionId());
		values.put(C_PROJECT_ID, setting.getProjectId());

		// updating row
		int rowUpdated = db.update(TABLE_SETTINGS, values, C_USER_ID + " = ?",
				new String[] { String.valueOf(setting.getUserId()) });

		if (rowUpdated <= 0) {
			retVal = createSettings(setting);
		}else{
            retVal = true;
        }
		//closeWriteDatabase();
        return retVal;
	}

	public boolean createData(Data data) {
        boolean retVal = false;
		try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(C_USER_ID, data.getUser_id());
			values.put(C_PROJECT_ID, data.getProject_id());
			values.put(C_QUESTION_ID, data.getQuestion_id());
			values.put(C_TYPE, data.getType());
			values.put(C_VALUES, data.getValue());

			// Inserting Row
			long row_id = db.insert(TABLE_DATA, null, values);
			if (row_id >= 0) {
				retVal = true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
		} catch (SQLException sqliteException) {
		}
        //closeWriteDatabase();
        return retVal;
	}

	// Getting single parameters of settings
	public Data getData(String userID, String projectID, String questionID) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_DATA + " WHERE "
				+ C_USER_ID + " = '" + userID + "' and " + C_PROJECT_ID + " = '" + projectID + "' and " + C_QUESTION_ID + " = '" + questionID + "'";

		Data data = new Data();
		data.setUser_id(userID);
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {

			data.setUser_id(c.getString(c.getColumnIndex(C_USER_ID)));
			data.setProject_id(c.getString(c.getColumnIndex(C_PROJECT_ID)));
			data.setQuestion_id(c.getString(c.getColumnIndex(C_QUESTION_ID)));
			data.setType(c.getString(c.getColumnIndex(C_TYPE)));
			data.setValue(c.getString(c.getColumnIndex(C_VALUES)));
		}
		c.close();
        //closeReadDatabase();
		return data;
	}

	// Updating single setting
	public boolean updateData(Data data) {
        boolean retVal = false;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(C_USER_ID, data.getUser_id());
		values.put(C_PROJECT_ID, data.getProject_id());
		values.put(C_QUESTION_ID, data.getQuestion_id());
		values.put(C_TYPE, data.getType());
		values.put(C_VALUES, data.getValue());

		// updating row
		int rowUpdated = db.update(TABLE_DATA, values, C_USER_ID + " = ? and " + C_QUESTION_ID + " = ? and " + C_PROJECT_ID + " = ?" ,
				new String[] { String.valueOf(data.getUser_id()),String.valueOf(data.getQuestion_id()),String.valueOf(data.getProject_id()) });

		if (rowUpdated <= 0) {
			retVal = createData(data);
		}else{
            retVal = true;
        }
        //closeWriteDatabase();
		return retVal;
	}

    public boolean createRememberAnswers(RememberAnswers rememberAnswers) {
        boolean retVal = false;
        try {
            SQLiteDatabase db = getHelper(context).getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(C_USER_ID, rememberAnswers.getUser_id());
            values.put(C_PROJECT_ID, rememberAnswers.getProject_id());
            values.put(C_QUESTION_ID, rememberAnswers.getQuestion_id());
            values.put(C_SELECTED_OPTION_TEXT, rememberAnswers.getSelected_option_text());
            values.put(C_IS_REMEMBER, rememberAnswers.getIs_remember());

            // Inserting Row
            long row_id = db.insert(TABLE_REMEMBER_ANSWERS, null, values);
            if (row_id >= 0) {
                retVal = true;
            }
        } catch (SQLiteConstraintException contraintException) {
            // If data already present then handle the case here.
        } catch (SQLException sqliteException) {
        }
        //closeWriteDatabase();
        return retVal;
    }

    // Getting single parameters of remember answer
    public RememberAnswers getRememberAnswers(String userID, String projectID, String questionID ) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_REMEMBER_ANSWERS + " WHERE "
                + C_USER_ID + " = '" + userID + "' and " + C_PROJECT_ID + " = '" + projectID + "' and " + C_QUESTION_ID + " = '" + questionID + "'";

        RememberAnswers rememberAnswers = new RememberAnswers();
        rememberAnswers.setUser_id(userID);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {

            rememberAnswers.setUser_id(c.getString(c.getColumnIndex(C_USER_ID)));
            rememberAnswers.setProject_id(c.getString(c.getColumnIndex(C_PROJECT_ID)));
            rememberAnswers.setQuestion_id(c.getString(c.getColumnIndex(C_QUESTION_ID)));
            rememberAnswers.setSelected_option_text(c.getString(c.getColumnIndex(C_SELECTED_OPTION_TEXT)));
            rememberAnswers.setIs_remember(c.getString(c.getColumnIndex(C_IS_REMEMBER)));
        }
        c.close();
        //closeReadDatabase();
        return rememberAnswers;
    }

    public int getRememberAnswersCount(String userID, String projectID) {
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_REMEMBER_ANSWERS + " WHERE "
                + C_USER_ID + " = '" + userID + "' and " + C_PROJECT_ID + " = '" + projectID + "' and " + C_IS_REMEMBER + " = '1'";

        Cursor c = db.rawQuery(selectQuery, null);
        int count = c.getCount();
        c.close();
        //closeReadDatabase();
        return count;
    }


    // Updating single remember answer
    public boolean updateRememberAnswers(RememberAnswers rememberAnswers) {
        boolean retVal = false;
        SQLiteDatabase db = getHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_USER_ID, rememberAnswers.getUser_id());
        values.put(C_PROJECT_ID, rememberAnswers.getProject_id());
        values.put(C_QUESTION_ID, rememberAnswers.getQuestion_id());
        values.put(C_SELECTED_OPTION_TEXT, rememberAnswers.getSelected_option_text());
        values.put(C_IS_REMEMBER, rememberAnswers.getIs_remember());

        // updating row
        int rowUpdated = db.update(TABLE_REMEMBER_ANSWERS, values, C_USER_ID + " = ? and " + C_PROJECT_ID + " = ? and " + C_QUESTION_ID + " = ?",
                new String[] { String.valueOf(rememberAnswers.getUser_id()),String.valueOf(rememberAnswers.getProject_id()),String.valueOf(rememberAnswers.getQuestion_id()) });

        if (rowUpdated <= 0) {
            retVal = createRememberAnswers(rememberAnswers);
        }else{
            retVal = true;
        }
        //closeWriteDatabase();
        return retVal;
    }

    public void deleteAllData(){
        SQLiteDatabase db = getHelper(context).getWritableDatabase();
        db.delete(TABLE_OPTION,null,null);
        db.delete(TABLE_DATA,null,null);
        db.delete(TABLE_MACRO,null,null);
        db.delete(TABLE_PROTOCOL,null,null);
        db.delete(TABLE_QUESTION,null,null);
        db.delete(TABLE_RESEARCH_PROJECT,null,null);
        db.delete(TABLE_SETTINGS,null,null);
        db.delete(TABLE_PROJECT_LEAD,null,null);
        db.delete(TABLE_REMEMBER_ANSWERS,null,null);
        //closeWriteDatabase();
    }

}
