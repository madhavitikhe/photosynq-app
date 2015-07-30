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
import com.photosynq.app.model.ProjectCreator;
import com.photosynq.app.model.ProjectResult;
import com.photosynq.app.model.Protocol;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.RememberAnswers;
import com.photosynq.app.model.ResearchProject;
import com.photosynq.app.model.UserAnswer;
import com.photosynq.app.utils.PrefUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
	// Database Version
	private static final int DATABASE_VERSION = 3;
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
	private static final String TABLE_USER_ANSWERS = "user_answers";

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
    //private static final String C_PROJECT_SLUG = "slug";
	private static final String C_IS_CONTRIBUTED = "is_contributed";

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

	// User Entered Answers.
	private static final String C_USER_ENTERED_ANSWERS = "user_entered_answers";
    private static final String TEXT = " TEXT ";
    private static final String COMMA = ", ";
    private static final String PRIMARY_KEY = " PRIMARY KEY ";
    private static final String CREATE_TABLE = "CREATE TABLE ";

    private Context context;
	// Reaserch Project table create statement
	//
	private static final StringBuilder CREATE_TABLE_RESEARCH_PROJECT =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_RESEARCH_PROJECT ).append("(")
                    .append(C_RECORD_HASH).append(TEXT).append(PRIMARY_KEY).append(COMMA)
                    .append(C_PROJECT_ID).append( TEXT).append(COMMA)
                    .append(C_PROJECT_NAME).append(TEXT).append(COMMA)
                    .append(C_PROJECT_DESCRIPTION).append(TEXT).append(COMMA)
                    .append(C_PROJECT_DIR_TO_COLLAB).append(TEXT).append(COMMA)
                    .append(C_PROJECT_LEAD_ID).append(TEXT).append(COMMA)
                    .append(C_PROJECT_START_DATE).append(TEXT).append(COMMA)
                    .append(C_PROJECT_END_DATE).append(TEXT).append(COMMA)
                    .append(C_PROJECT_BETA).append(TEXT).append(COMMA)
                    .append(C_IS_CONTRIBUTED).append(TEXT).append(COMMA)
                    .append(C_PROJECT_PROTOCOL_IDS).append(TEXT).append(COMMA)
                    .append(C_PROJECT_IMAGE_URL).append(TEXT).append(")");

    // Project Lead table create statement
    private static final StringBuilder CREATE_TABLE_PROJECT_LEAD =
                new StringBuilder( CREATE_TABLE)
                    .append(TABLE_PROJECT_LEAD).append("(")
                    .append(C_LEAD_ID).append(TEXT).append(PRIMARY_KEY).append(COMMA)
                    .append(C_LEAD_NAME).append(TEXT).append(COMMA)
                    .append(C_LEAD_DATA_COUNT).append(TEXT).append(COMMA)
                    .append(C_LEAD_IMAGE_URL).append(TEXT).append(")");


	// Question table create statement

	private static final StringBuilder CREATE_TABLE_QUESTION =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_QUESTION).append("(")
                    .append(C_RECORD_HASH).append(TEXT).append(PRIMARY_KEY).append(COMMA)
                    .append(C_PROJECT_ID).append(TEXT).append(COMMA)
                    .append(C_QUESTION_ID).append(TEXT).append(COMMA)
                    .append(C_QUESTION_TEXT).append(TEXT).append(COMMA)
                    .append(C_QUESTION_TYPE).append(" integer ").append(")");

	// Answer table create statement

	private static final StringBuilder CREATE_TABLE_OPTION =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_OPTION).append("(")
                    .append(C_RECORD_HASH).append(TEXT).append(PRIMARY_KEY).append(COMMA)
                    .append(C_OPTION_TEXT).append(TEXT).append(COMMA)
                    .append(C_PROJECT_ID).append(TEXT).append(COMMA)
                    .append(C_QUESTION_ID).append(TEXT).append(")");

	// Protocol table create statement
	private static final StringBuilder CREATE_TABLE_PROTOCOL =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_PROTOCOL).append("(")
                    .append(C_RECORD_HASH).append(TEXT).append(PRIMARY_KEY).append(COMMA)
                    .append(C_PROTOCOL_ID).append(TEXT).append(COMMA)
                    .append(C_PROTOCOL_NAME).append(TEXT).append(COMMA)
                    .append(C_PROTOCOL_JSON).append(TEXT).append(COMMA)
                    .append(C_PROTOCOL_DESCRIPTION).append(TEXT).append(COMMA)
                    .append(C_PROTOCOL_MACRO_ID).append(TEXT).append(COMMA)
                    .append(C_PROTOCOL_MACRO_SLUG).append(TEXT).append(COMMA)
                    .append(C_PROTOCOL_PRE_SEL).append(TEXT).append(")");
    // Macro table create statement
    private static final StringBuilder CREATE_TABLE_MACRO =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_MACRO).append("(")
                    .append(C_RECORD_HASH).append(TEXT).append(PRIMARY_KEY).append(COMMA)
                    .append(C_MACRO_ID).append(TEXT).append(COMMA)
                    .append(C_MACRO_NAME).append(TEXT).append(COMMA)
                    .append(C_MACRO_DESCRIPTION).append(TEXT).append(COMMA)
                    .append(C_MACRO_DEFAULT_X_AXIS).append(TEXT).append(COMMA)
                    .append(C_MACRO_DEFAULT_Y_AXIS).append(TEXT).append(COMMA)
                    .append(C_MACRO_JAVASCRIPT_CODE).append(TEXT).append(COMMA)
                    .append(C_MACRO_SLUG).append(TEXT).append(")");

    private static final StringBuilder CREATE_TABLE_RESULTS =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_RESULTS).append("(")
                    .append(C_RECORD_HASH).append(TEXT).append(PRIMARY_KEY).append(COMMA)
                    .append(C_PROJECT_ID).append(TEXT).append(COMMA)
                    .append(C_UPLOADED).append(TEXT).append(COMMA)
                    .append(C_READING).append(TEXT).append(")");

    private static final StringBuilder CREATE_TABLE_SETTINGS =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_SETTINGS).append("(")
                    .append(C_USER_ID).append(TEXT).append(COMMA)
                    .append(C_MODE_TYPE).append(TEXT).append(COMMA)
                    .append(C_CONNECTION_ID).append(TEXT).append(COMMA)
                    .append(C_PROJECT_ID).append(TEXT).append(")");

    private static final StringBuilder CREATE_TABLE_DATA =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_DATA).append("(")
                    .append(C_USER_ID).append(TEXT).append(COMMA)
                    .append(C_PROJECT_ID).append(TEXT).append(COMMA)
                    .append(C_QUESTION_ID).append(TEXT).append(COMMA)
                    .append(C_TYPE).append(TEXT).append(COMMA)
                    .append(C_VALUES).append(TEXT).append(")");

    private static final StringBuilder CREATE_TABLE_REMEMBER_ANSWERS =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_REMEMBER_ANSWERS).append("(")
                    .append(C_USER_ID).append(TEXT).append(COMMA)
                    .append(C_PROJECT_ID).append(TEXT).append(COMMA)
                    .append(C_QUESTION_ID).append(TEXT).append(COMMA)
                    .append(C_SELECTED_OPTION_TEXT).append(TEXT).append(COMMA)
                    .append(C_IS_REMEMBER).append(TEXT).append(")");

    private static final StringBuilder CREATE_TABLE_USER_ANSWERS =
            new StringBuilder(CREATE_TABLE)
                    .append(TABLE_USER_ANSWERS).append("(")
                    .append(C_USER_ENTERED_ANSWERS).append(TEXT).append(PRIMARY_KEY)
                    .append(")");

    private static final String FROM = " FROM ";
    private static final String SELECT = "SELECT ";
    private static final String WHERE = " WHERE ";
    private static final String AND =  " AND ";
    private static final String DROP_TABLE_IF_EXIST = "DROP TABLE IF EXISTS ";

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION); this.context =context;}

	private static DatabaseHelper instance;

	public static synchronized DatabaseHelper getHelper(Context context) {
		if (instance == null)
			instance = new DatabaseHelper(context);

		return instance;
	}
    public static synchronized SQLiteDatabase getWDatabase(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);

        return instance.getWritableDatabase();
    }


	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_RESEARCH_PROJECT.toString());
		db.execSQL(CREATE_TABLE_QUESTION.toString());
		db.execSQL(CREATE_TABLE_OPTION.toString());
		db.execSQL(CREATE_TABLE_PROTOCOL.toString());
		db.execSQL(CREATE_TABLE_MACRO.toString());
		db.execSQL(CREATE_TABLE_RESULTS.toString());
		db.execSQL(CREATE_TABLE_SETTINGS.toString());
		db.execSQL(CREATE_TABLE_DATA.toString());
        db.execSQL(CREATE_TABLE_PROJECT_LEAD.toString());
        db.execSQL(CREATE_TABLE_REMEMBER_ANSWERS.toString());
		db.execSQL(CREATE_TABLE_USER_ANSWERS.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// on upgrade drop older tables
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_RESEARCH_PROJECT);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_QUESTION);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_OPTION);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_PROTOCOL);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_MACRO);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_RESULTS);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_SETTINGS);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_DATA);
        db.execSQL(DROP_TABLE_IF_EXIST + TABLE_PROJECT_LEAD);
        db.execSQL(DROP_TABLE_IF_EXIST + TABLE_REMEMBER_ANSWERS);
		db.execSQL(DROP_TABLE_IF_EXIST + TABLE_USER_ANSWERS);
		// create new tables;
		onCreate(db);

	}

	public long createResult(ProjectResult result) {
		if (result.getReading().length() <= 0){
			return -1;
		}
		try {
			SQLiteDatabase db = getWDatabase(context);
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
				return row_id;
			}
		} catch (Exception e) {
            Log.e("DBHGAURC", e.getMessage());
        }
        return -1;
	}

	public int getAllUnuploadedResultsCount(String projectId){
		StringBuilder selectQuery = new StringBuilder();

        if (null != projectId) {

            selectQuery.append(SELECT).append(" count(*) ").append(FROM)
                     .append(TABLE_RESULTS).append(WHERE)
                     .append(C_UPLOADED).append(" = 'N' ").append(AND)
                     .append(C_PROJECT_ID).append(" = '")
                     .append(projectId).append("'");

			Log.e("DBHGAURC", selectQuery.toString());
		} else {

            selectQuery.append(SELECT).append(" count(*) ").append(FROM)
                    .append(TABLE_RESULTS).append(WHERE)
                    .append(C_UPLOADED).append(" = 'N'");

			Log.e("DBHGAURC", selectQuery.toString());
		}

		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);
        int cnt = c.getCount();
        c.close();
        return cnt;
	}

	public List<ProjectResult> getAllUnUploadedResults() {
        StringBuilder selectQuery = new StringBuilder();
        List<ProjectResult> projectsResults = new ArrayList<>();
        selectQuery.append(SELECT).append(" rowid,* ").append(FROM)
                .append(TABLE_RESULTS).append(WHERE)
                .append(C_UPLOADED).append(" = 'N'");

		Log.e("DBHGAUR", selectQuery.toString());

		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

		if (c.moveToFirst()) {
			do {
				ProjectResult rp = new ProjectResult();
				rp.setId(c.getString(c.getColumnIndex(C_ROW_ID)));
				rp.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				rp.setReading(c.getString(c.getColumnIndex(C_READING)));
				rp.setUploaded(c.getString(c.getColumnIndex(C_UPLOADED)));

				projectsResults.add(rp);
			} while (c.moveToNext());
		}

		c.close();
		return projectsResults;
	}


	public void deleteResult(String rowid) {

        getWDatabase(context).delete(TABLE_RESULTS, C_ROW_ID + " = ?",
                new String[]{rowid});
	}

	// Insert research project information in database
	public boolean createResearchProject(ResearchProject rp) {

		try {

			ContentValues values = new ContentValues();
			values.put(C_PROJECT_ID, null != rp.getId() ? rp.getId() : "");
			values.put(C_PROJECT_NAME, null != rp.getName() ? rp.getName() : "");
			values.put(C_PROJECT_DESCRIPTION,
					null != rp.getDescription() ? rp.getDescription() : "");
			values.put(C_PROJECT_DIR_TO_COLLAB,
					null != rp.getDirToCollab() ? rp.getDirToCollab() : "");
			values.put(C_PROJECT_LEAD_ID,
					null != rp.getCreatorId() ? rp.getCreatorId() : "");
			values.put(C_PROJECT_START_DATE,
					null != rp.getStartDate() ? rp.getStartDate() : "");
			values.put(C_PROJECT_END_DATE, null != rp.getEndDate() ? rp.getEndDate()
					: "");
			values.put(C_PROJECT_BETA, null != rp.getBeta() ? rp.getBeta() : "");
			values.put(C_PROJECT_PROTOCOL_IDS,
					null != rp.getProtocols_ids() ? rp.getProtocols_ids() : "");
			values.put(C_PROJECT_IMAGE_URL, null != rp.getImageUrl() ? rp.getImageUrl()
					: "");
			values.put(C_IS_CONTRIBUTED, null != rp.getIs_contributed() ? rp.getIs_contributed()
					: "");
			values.put(C_RECORD_HASH, rp.getRecordHash());
			// insert row
			long row_id = getWDatabase(context).insert(TABLE_RESEARCH_PROJECT, null, values);

			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
			Log.d("DBHRP",
					"Record already present in database for record hash ="
							+ rp.getRecordHash());

		} catch (SQLException ignored) {}
        return false;
	}

	// Get research project information from database
	public ResearchProject getResearchProject(String id) {
        ResearchProject rp = null;
		StringBuilder selectQuery = new StringBuilder(SELECT).append(" * ")
                .append(FROM)
                .append(TABLE_RESEARCH_PROJECT)
				.append(WHERE).append(C_PROJECT_ID)
                .append(" = '").append(id).append("'");

		Log.e("DBHGRP", selectQuery.toString());

		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

        if (c != null) {
            c.moveToFirst();

            if (c.getCount() > 0) {
                rp = new ResearchProject();
                rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
                rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
                rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
                rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
				rp.setCreatorId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
				rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
                rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
                rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
                rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
                rp.setProtocols_ids(c.getString(c.getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
                rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));
            }
            c.close();
        }
        return rp;
	}

	public List<ResearchProject> getUserCreatedContributedProjects(String userId) {

		List<ResearchProject> researchProjects = new ArrayList<>();
		StringBuilder selectQuery = new StringBuilder(SELECT).append(" * ")
                .append(FROM).append( TABLE_RESEARCH_PROJECT)
				.append(WHERE).append( C_PROJECT_LEAD_ID)
                .append(" = '").append(userId)
                .append("' or ").append(C_IS_CONTRIBUTED).append(" = 'true'");

		Log.e("DBHGUCCP", selectQuery.toString());
		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

		if (c.moveToFirst()) {
			do {
				ResearchProject rp = new ResearchProject();
				rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
				rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
				rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
				rp.setCreatorId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
				rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
				rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
				rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
				rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
				rp.setProtocols_ids(c.getString(c
						.getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
				rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));
				rp.setIs_contributed(c.getString(c.getColumnIndex(C_IS_CONTRIBUTED)));

				researchProjects.add(rp);
			} while (c.moveToNext());
		}

		c.close();
		return researchProjects;
	}

	// Get all research project information from database where name contains search string.
	public List<ResearchProject> getUserCreatedContributedProjects(String searchString, String userId) {
		List<ResearchProject> researchProjects = new ArrayList<>();

		StringBuilder selectQuery = new StringBuilder(SELECT).append(" * ")
                .append(FROM).append( TABLE_RESEARCH_PROJECT)
				.append(WHERE).append(C_PROJECT_LEAD_ID)
                .append(" = '").append(userId)
				.append("' or ").append(C_IS_CONTRIBUTED).append(" = 'true')")
                .append(AND).append(C_PROJECT_NAME ).append(" LIKE '%").append(searchString).append("%'");

		Log.e("DBHGUCCP", selectQuery.toString());

		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

		if (c.moveToFirst()) {
			do {
				ResearchProject rp = new ResearchProject();
				rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
				rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
				rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
				rp.setCreatorId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
				rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
				rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
				rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
				rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
				rp.setProtocols_ids(c.getString(c
                        .getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
				rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));
				rp.setIs_contributed(c.getString(c.getColumnIndex(C_IS_CONTRIBUTED)));

				researchProjects.add(rp);
			} while (c.moveToNext());
		}

		c.close();
		return researchProjects;
	}


	// Get all research project information from database.
	public List<ResearchProject> getAllResearchProjects() {
		List<ResearchProject> researchProjects = new ArrayList<>();
		StringBuilder selectQuery = new StringBuilder(SELECT).append(" * ").append(FROM).append(TABLE_RESEARCH_PROJECT);

        Log.e("DBHGARP", selectQuery.toString());

		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

		if (c.moveToFirst()) {
			do {
				ResearchProject rp = new ResearchProject();
				rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
				rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
				rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
				rp.setCreatorId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
				rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
				rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
				rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
				rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
				rp.setProtocols_ids(c.getString(c
						.getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
				rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));

				researchProjects.add(rp);
			} while (c.moveToNext());
		}

		c.close();
		return researchProjects;
	}

    // Get all research project information from database where name contains search string.
    public List<ResearchProject> getAllResearchProjects(String searchString) {
        List<ResearchProject> researchProjects = new ArrayList<>();
        StringBuilder selectQuery = new StringBuilder(SELECT).append(" * ")
                .append(FROM).append( TABLE_RESEARCH_PROJECT)
                .append(WHERE).append( C_PROJECT_NAME)
                .append(" LIKE '%").append(searchString).append("%'");

        Log.e("DBHGARP", selectQuery.toString());

        Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

        if (c.moveToFirst()) {
            do {
                ResearchProject rp = new ResearchProject();
                rp.setId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
                rp.setName(c.getString(c.getColumnIndex(C_PROJECT_NAME)));
                rp.setDescription(c.getString(c.getColumnIndex(C_PROJECT_DESCRIPTION)));
                rp.setDirToCollab(c.getString(c.getColumnIndex(C_PROJECT_DIR_TO_COLLAB)));
				rp.setCreatorId(c.getString(c.getColumnIndex(C_PROJECT_LEAD_ID)));
                rp.setStartDate(c.getString(c.getColumnIndex(C_PROJECT_START_DATE)));
                rp.setEndDate(c.getString(c.getColumnIndex(C_PROJECT_END_DATE)));
                rp.setImageUrl(c.getString(c.getColumnIndex(C_PROJECT_IMAGE_URL)));
                rp.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));
                rp.setProtocols_ids(c.getString(c
						.getColumnIndex(C_PROJECT_PROTOCOL_IDS)));
                rp.setBeta(c.getString(c.getColumnIndex(C_PROJECT_BETA)));

				researchProjects.add(rp);
            } while (c.moveToNext());
        }

        c.close();
            return researchProjects;
    }

	/*
	 * Updating a Research Project
	 */
	public boolean updateResearchProject(ResearchProject rp) {

        ContentValues values = new ContentValues();
        values.put(C_PROJECT_ID, null != rp.getId() ? rp.getId() : "");
        values.put(C_PROJECT_NAME, null != rp.getName() ? rp.getName() : "");
        values.put(C_PROJECT_DESCRIPTION,
                null != rp.getDescription() ? rp.getDescription() : "");
        values.put(C_PROJECT_DIR_TO_COLLAB,
                null != rp.getDirToCollab() ? rp.getDirToCollab() : "");
        values.put(C_PROJECT_LEAD_ID,
                null != rp.getCreatorId() ? rp.getCreatorId() : "");
        values.put(C_PROJECT_START_DATE, null != rp.getStartDate() ? rp.getStartDate()
                : "");
        values.put(C_PROJECT_END_DATE, null != rp.getEndDate() ? rp.getEndDate() : "");
        values.put(C_PROJECT_BETA, null != rp.getBeta() ? rp.getBeta() : "");
        values.put(C_PROJECT_IMAGE_URL, null != rp.getImageUrl() ? rp.getImageUrl()
                : "");
        values.put(C_IS_CONTRIBUTED, null != rp.getIs_contributed() ? rp.getIs_contributed()
                : "");
        values.put(C_PROJECT_PROTOCOL_IDS,
                null != rp.getProtocols_ids() ? rp.getProtocols_ids() : "");
        values.put(C_RECORD_HASH, rp.getRecordHash());

        int rowsaffected = getWDatabase(context).update(TABLE_RESEARCH_PROJECT, values, C_PROJECT_ID
                + " = ?", new String[]{String.valueOf(rp.getId())});
        // if update fails that indicates there is no then create new row
        return rowsaffected > 0 || createResearchProject(rp);
    }


    // Insert project lead information in database
    public boolean createProjectLead(ProjectCreator projectCreator) {
        try {
            ContentValues values = new ContentValues();
            values.put(C_LEAD_ID, null != projectCreator.getId() ? projectCreator.getId() : "");
            values.put(C_LEAD_NAME, null != projectCreator.getName() ? projectCreator.getName() : "");
			values.put(C_LEAD_IMAGE_URL, null != projectCreator.getImageUrl() ? projectCreator.getImageUrl() : "");

            long row_id = getWDatabase(context).insert(TABLE_PROJECT_LEAD, null, values);

            if (row_id >= 0) {
                return true;
            }
        } catch (SQLiteConstraintException contraintException) {
            // If data already present then handle the case here.
            Log.d("DHPL", "Record already present in database for record hash =" + projectCreator.getRecordHash());

        } catch (SQLException sqliteException) {
            Log.d("DHPL", sqliteException.getMessage());
        }
        return false;
    }

    // Get project lead information from database
    public ProjectCreator getProjectLead(String id) {ProjectCreator projectCreator = null;

        StringBuilder selectQuery = new StringBuilder(SELECT).append(" * ")
                .append(FROM).append(TABLE_PROJECT_LEAD)
                .append(WHERE).append(C_LEAD_ID).append(" = '").append(id).append("'");

        Log.e("DHPL", selectQuery.toString());

        Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

        if (c != null) {
            c.moveToFirst();

            if (c.getCount() > 0) {
                projectCreator = new ProjectCreator();
                projectCreator.setId(c.getString(c.getColumnIndex(C_LEAD_ID)));
                projectCreator.setName(c.getString(c.getColumnIndex(C_LEAD_NAME)));
				projectCreator.setImageUrl(c.getString(c.getColumnIndex(C_LEAD_IMAGE_URL)));
            }
            c.close();
        }
        return projectCreator;
    }

    /*
     * Updating a Project Lead
     */
    public boolean updateProjectLead(ProjectCreator projectCreator) {

        ContentValues values = new ContentValues();
        values.put(C_LEAD_ID, null != projectCreator.getId() ? projectCreator.getId() : "");
        values.put(C_LEAD_NAME, null != projectCreator.getName() ? projectCreator.getName() : "");
        values.put(C_LEAD_IMAGE_URL, null != projectCreator.getImageUrl() ? projectCreator.getImageUrl() : "");

        int rowsaffected = getWDatabase(context).update(TABLE_PROJECT_LEAD, values, C_LEAD_ID
                + " = ?", new String[]{String.valueOf(projectCreator.getId())});
        // if update fails that indicates there is no then create new row
        return rowsaffected > 0 || createProjectLead(projectCreator);
    }

    // Insert a question in database
	public boolean createQuestion(Question que) {
		try {
			ContentValues values = new ContentValues();
			values.put(C_RECORD_HASH,
					null != que.getRecordHash() ? que.getRecordHash() : "");
			values.put(C_QUESTION_TEXT,
					null != que.getQuestionText() ? que.getQuestionText() : "");
			values.put(C_QUESTION_ID, que.getQuestionId());
			values.put(C_PROJECT_ID, que.getProjectId());
            values.put(C_QUESTION_TYPE, que.getQuestionType());
			// insert row
			long row_id = getWDatabase(context).insert(TABLE_QUESTION, null, values);
			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
			// If data already present then handle the case here.
            Log.d("DBHCQ", "Record already present in database for record hash =" + que.getRecordHash());
		} catch (SQLException sqliteException) {
            Log.d("DBHCQ", sqliteException.getMessage());
		}
        return false;
	}

	public boolean updateQuestion(Question question) {

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

        int rowsaffected = getWDatabase(context).update(
                TABLE_QUESTION,
                values,
                C_QUESTION_ID + " = ? and " + C_PROJECT_ID + " =?",
                new String[]{question.getQuestionId(), question.getProjectId()});

        String user_id = PrefUtils.getFromPrefs(context, PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

        if (question.getQuestionType() == Question.PROJECT_DEFINED || question.getQuestionType() == Question.PHOTO_TYPE_DEFINED) {
            Data data = new Data(user_id, question.getProjectId(), question.getQuestionId(), "", "");
            updateData(data);
        }
        // if update fails that indicates there is no then create new row
        return rowsaffected > 0 || createQuestion(question);

    }

	// Insert Option in database
	public boolean createOption(Option op) {
		try {
			ContentValues values = new ContentValues();
			values.put(C_RECORD_HASH, op.getRecordHash());
			values.put(C_OPTION_TEXT,
					null != op.getOptionText() ? op.getOptionText() : "");
			values.put(C_QUESTION_ID,
					null != op.getQuestionId() ? op.getQuestionId() : "");
			values.put(C_PROJECT_ID,
					null != op.getProjectId() ? op.getProjectId() : "");
			// insert row
			long row_id = getWDatabase(context).insert(TABLE_OPTION, null, values);
			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
            Log.d("DBHCO", "Record already present in database for record hash =" + op.getRecordHash());
		} catch (SQLException sqliteException) {
            Log.d("DBHCO", sqliteException.getMessage());
		}
        return false;
	}

	public boolean updateOption(Option option) {

        ContentValues values = new ContentValues();
        values.put(C_RECORD_HASH, option.getRecordHash());
        values.put(C_OPTION_TEXT,
                null != option.getOptionText() ? option.getOptionText() : "");
        values.put(C_QUESTION_ID,
                null != option.getQuestionId() ? option.getQuestionId() : "");
        values.put(C_PROJECT_ID,
                null != option.getProjectId() ? option.getProjectId() : "");

        int rowsaffected = getWDatabase(context).update(TABLE_OPTION, values, C_RECORD_HASH
                        + " = ?",
                new String[]{String.valueOf(option.getRecordHash())});
        // if update fails that indicates there is no then create new row
        return rowsaffected > 0 || createOption(option);
    }

	public Question getQuestionForProject(String project_id,String question_id) {
		Question que= new Question();
		StringBuilder selectQuery =new StringBuilder(SELECT).append(" * ")
                .append(FROM).append(TABLE_QUESTION )
                .append(WHERE).append(C_PROJECT_ID).append(" = ").append(project_id)
                .append(AND).append(C_QUESTION_ID).append(" = ").append(question_id);

		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

		if (c.moveToFirst()) {

				String questionId = c
						.getString(c.getColumnIndex(C_QUESTION_ID));
				que.setQuestionText(c.getString(c
						.getColumnIndex(C_QUESTION_TEXT)));
				que.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
				que.setQuestionId(questionId);
                que.setQuestionType(c.getInt(c.getColumnIndex(C_QUESTION_TYPE)));
				que.setRecordHash(c.getString(c.getColumnIndex(C_RECORD_HASH)));

				if (!que.getQuestionText().equals("")) {
					StringBuilder selectOptionsQuery = new StringBuilder(SELECT).append(" * ")
                    .append(FROM).append(TABLE_OPTION).append(WHERE).append( C_QUESTION_ID).append( " = ")
                            .append(questionId).append(AND).append(C_PROJECT_ID).append( " = ").append(project_id);

					Cursor optionCursor = getWDatabase(context).rawQuery(selectOptionsQuery.toString(), null);
					if (optionCursor.moveToFirst()) {
						List<String> optionlist = new ArrayList<>();
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
		return que;
	}

	// Get list of all questions for given project.
	public List<Question> getAllQuestionForProject(String project_id) {
		List<Question> questions = new ArrayList<>();
		StringBuilder selectQuery = new StringBuilder(SELECT).append(" * ")
                .append(FROM).append(TABLE_QUESTION)
                .append(WHERE).append(C_PROJECT_ID).append(" = ").append( project_id);

		Cursor c = getWDatabase(context).rawQuery(selectQuery.toString(), null);

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
					StringBuilder selectOptionsQuery = new StringBuilder(SELECT).append(" * ")
                            .append(FROM).append(TABLE_OPTION)
                            .append(WHERE).append( C_QUESTION_ID).append(" = ")
                            .append(questionId).append(AND).append(C_PROJECT_ID ).append( " = ")
							.append( project_id);

					Cursor optionCursor = getWDatabase(context).rawQuery(selectOptionsQuery.toString(), null);
					if (optionCursor.moveToFirst()) {
						List<String> optionlist = new ArrayList<>();
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
		return questions;
	}

	// Insert Option in database
	public boolean createProtocol(Protocol protocol) {
		try {
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
			long row_id = getWDatabase(context).insert(TABLE_PROTOCOL, null, values);
			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
            Log.d("DBHCP", "Record already present in database for record hash =" + protocol.getRecordHash());
		} catch (SQLException sqliteException) {
            Log.d("DBHCP", sqliteException.getMessage());
		}

        return false;
	}

	public boolean updateProtocol(Protocol protocol) {

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

        int rowsaffected = getWDatabase(context).update(TABLE_PROTOCOL, values, C_PROTOCOL_ID + " = ?",
                new String[]{String.valueOf(protocol.getId())});
        // if update fails that indicates there is no then create new row
        return rowsaffected > 0 || createProtocol(protocol);
    }

	// Get all protocols
	public List<Protocol> getAllProtocolsList() {
		List<Protocol> protocols = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + TABLE_PROTOCOL;

		Cursor c = getWDatabase(context).rawQuery(selectQuery, null);

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

				protocols.add(protocol);
			} while (c.moveToNext());
		}
		c.close();
		return protocols;
	}

    // Get pre-selected protocols
    public List<Protocol> getFewProtocolList() {
        List<Protocol> protocols = new ArrayList<>();
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
		Protocol protocol = new Protocol();
		String selectQuery = "SELECT  * FROM " + TABLE_PROTOCOL + " WHERE "
				+ C_PROTOCOL_ID + " = " + protocolId;

        Cursor c = getWDatabase(context).rawQuery(selectQuery, null);

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
		return protocol;
	}

	// Insert Macro in database
	public boolean createMacro(Macro macro) {

		try {

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
			long row_id = getWDatabase(context).insert(TABLE_MACRO, null, values);
			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
            Log.d("DBHCM", "Record already present in database for record hash =" + macro.getRecordHash());
		} catch (SQLException sqliteException) {
            Log.d("DBHCM", sqliteException.getMessage());
		}

        return false;
	}

	public boolean updateMacro(Macro macro) {

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

        int rowsaffected = getWDatabase(context).update(TABLE_MACRO, values, C_MACRO_ID + " = ?",
                new String[]{String.valueOf(macro.getId())});
        // if update fails that indicates there is no then create new row
        return rowsaffected > 0 || createMacro(macro);
    }

	public List<Macro> getAllMacros() {
		List<Macro> macros = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + TABLE_MACRO;

		Cursor c = getWDatabase(context).rawQuery(selectQuery, null);

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

				macros.add(macro);
			} while (c.moveToNext());
		}
		c.close();
		return macros;
	}

	public boolean createSettings(AppSettings setting) {
		try {

			ContentValues values = new ContentValues();
			values.put(C_MODE_TYPE, setting.getModeType());
			values.put(C_USER_ID, setting.getUserId());
			values.put(C_CONNECTION_ID, setting.getConnectionId());
			values.put(C_PROJECT_ID, setting.getProjectId());

			// Inserting Row
			long row_id = getWDatabase(context).insert(TABLE_SETTINGS, null, values);
			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
            Log.d("DBHCS", "Record already present in database setting");
		} catch (SQLException sqliteException) {
            Log.d("DBHCS", sqliteException.getMessage());
		}
        return false;
	}

	// Getting single parameters of settings
	public AppSettings getSettings(String userID) {

		String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS + " WHERE "
				+ C_USER_ID + " = '" + userID + "'";

		AppSettings setting = new AppSettings();
		setting.setUserId(userID);
		Cursor c = getWDatabase(context).rawQuery(selectQuery, null);
		if (c.moveToFirst()) {

			setting.setUserId(c.getString(c.getColumnIndex(C_USER_ID)));
			setting.setModeType(c.getString(c.getColumnIndex(C_MODE_TYPE)));
			setting.setConnectionId(c.getString(c
					.getColumnIndex(C_CONNECTION_ID)));
			setting.setProjectId(c.getString(c.getColumnIndex(C_PROJECT_ID)));
		}
		c.close();
		return setting;
	}

	// Updating single setting
	public boolean updateSettings(AppSettings setting) {

        ContentValues values = new ContentValues();
        values.put(C_USER_ID, setting.getUserId());
        values.put(C_MODE_TYPE, setting.getModeType());
        values.put(C_CONNECTION_ID, setting.getConnectionId());
        values.put(C_PROJECT_ID, setting.getProjectId());

        // updating row
        int rowUpdated = getWDatabase(context).update(TABLE_SETTINGS, values, C_USER_ID + " = ?",
                new String[]{String.valueOf(setting.getUserId())});

        return rowUpdated > 0 || createSettings(setting);

    }

	public boolean createData(Data data) {
		try {
			ContentValues values = new ContentValues();
			values.put(C_USER_ID, data.getUser_id());
			values.put(C_PROJECT_ID, data.getProject_id());
			values.put(C_QUESTION_ID, data.getQuestion_id());
			values.put(C_TYPE, data.getType());
			values.put(C_VALUES, data.getValue());

			// Inserting Row
			long row_id = getWDatabase(context).insert(TABLE_DATA, null, values);
			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
            Log.d("DBHCD", "Record already present in database for data");
		} catch (SQLException sqliteException) {
            Log.d("DHPL", sqliteException.getMessage());
		}
        return false;
	}

	// Getting single parameters of settings
	public Data getData(String userID, String projectID, String questionID) {

		String selectQuery = "SELECT  * FROM " + TABLE_DATA + " WHERE "
				+ C_USER_ID + " = '" + userID + "' and " + C_PROJECT_ID + " = '" + projectID + "' and " + C_QUESTION_ID + " = '" + questionID + "'";

		Data data = new Data();
		data.setUser_id(userID);
		Cursor c = getWDatabase(context).rawQuery(selectQuery, null);
		if (c.moveToFirst()) {

			data.setUser_id(c.getString(c.getColumnIndex(C_USER_ID)));
			data.setProject_id(c.getString(c.getColumnIndex(C_PROJECT_ID)));
			data.setQuestion_id(c.getString(c.getColumnIndex(C_QUESTION_ID)));
			data.setType(c.getString(c.getColumnIndex(C_TYPE)));
			data.setValue(c.getString(c.getColumnIndex(C_VALUES)));
		}
		c.close();
		return data;
	}

	// Updating single setting
	public boolean updateData(Data data) {

        ContentValues values = new ContentValues();
        values.put(C_USER_ID, data.getUser_id());
        values.put(C_PROJECT_ID, data.getProject_id());
        values.put(C_QUESTION_ID, data.getQuestion_id());
        values.put(C_TYPE, data.getType());
        values.put(C_VALUES, data.getValue());

        // updating row
        int rowUpdated = getWDatabase(context).update(
                TABLE_DATA, values, C_USER_ID + " = ? and " + C_QUESTION_ID + " = ? and " + C_PROJECT_ID + " = ?",
                new String[]{data.getUser_id(), data.getQuestion_id(), data.getProject_id()});

        return rowUpdated > 0 || createData(data);
    }

    public boolean createRememberAnswers(RememberAnswers rememberAnswers) {
        try {

            ContentValues values = new ContentValues();
            values.put(C_USER_ID, rememberAnswers.getUser_id());
            values.put(C_PROJECT_ID, rememberAnswers.getProject_id());
            values.put(C_QUESTION_ID, rememberAnswers.getQuestion_id());
            values.put(C_SELECTED_OPTION_TEXT, rememberAnswers.getSelected_option_text());
            values.put(C_IS_REMEMBER, rememberAnswers.getIs_remember());

            // Inserting Row
            long row_id = getWDatabase(context).insert(TABLE_REMEMBER_ANSWERS, null, values);
            if (row_id >= 0) {
                return true;
            }
        } catch (SQLiteConstraintException contraintException) {
            Log.d("DBHCRA", "Record already present in database for remembred answers");
        } catch (SQLException sqliteException) {
            Log.d("DBHCRA", sqliteException.getMessage());
        }
        return false;
    }

    // Getting single parameters of remember answer
    public RememberAnswers getRememberAnswers(String userID, String projectID, String questionID ) {

        String selectQuery = "SELECT  * FROM " + TABLE_REMEMBER_ANSWERS + " WHERE "
                + C_USER_ID + " = '" + userID + "' and " + C_PROJECT_ID + " = '" + projectID + "' and " + C_QUESTION_ID + " = '" + questionID + "'";

        RememberAnswers rememberAnswers = new RememberAnswers();
        rememberAnswers.setUser_id(userID);
        Cursor c = getWDatabase(context).rawQuery(selectQuery, null);
        if (c.moveToFirst()) {

            rememberAnswers.setUser_id(c.getString(c.getColumnIndex(C_USER_ID)));
            rememberAnswers.setProject_id(c.getString(c.getColumnIndex(C_PROJECT_ID)));
            rememberAnswers.setQuestion_id(c.getString(c.getColumnIndex(C_QUESTION_ID)));
            rememberAnswers.setSelected_option_text(c.getString(c.getColumnIndex(C_SELECTED_OPTION_TEXT)));
            rememberAnswers.setIs_remember(c.getString(c.getColumnIndex(C_IS_REMEMBER)));
        }
        c.close();
        return rememberAnswers;
    }


    // Updating single remember answer
    public boolean updateRememberAnswers(RememberAnswers rememberAnswers) {

        ContentValues values = new ContentValues();
        values.put(C_USER_ID, rememberAnswers.getUser_id());
        values.put(C_PROJECT_ID, rememberAnswers.getProject_id());
        values.put(C_QUESTION_ID, rememberAnswers.getQuestion_id());
        values.put(C_SELECTED_OPTION_TEXT, rememberAnswers.getSelected_option_text());
        values.put(C_IS_REMEMBER, rememberAnswers.getIs_remember());

        // updating row
        int rowUpdated = getWDatabase(context).update(
                TABLE_REMEMBER_ANSWERS, values, C_USER_ID + " = ? and " + C_PROJECT_ID + " = ? and " + C_QUESTION_ID + " = ?",
                new String[]{rememberAnswers.getUser_id(), rememberAnswers.getProject_id(), rememberAnswers.getQuestion_id()});

        return rowUpdated > 0 || createRememberAnswers(rememberAnswers);
    }

	public boolean createUserAnswers(UserAnswer userAnswer) {
		try {

			ContentValues values = new ContentValues();
			values.put(C_USER_ENTERED_ANSWERS, userAnswer.getOptionText());

			// Inserting Row
			long row_id = getWDatabase(context).insert(TABLE_USER_ANSWERS, null, values);
			if (row_id >= 0) {
				return true;
			}
		} catch (SQLiteConstraintException contraintException) {
            Log.d("DBHCUA", "Record already present in database for User answers");
		} catch (SQLException sqliteException) {
            Log.d("DBHCRA", sqliteException.getMessage());
		}
		return false;
	}

	public List<String> getAllUserAnswers() {
		List<String> userAnswers = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_USER_ANSWERS ;

		Log.e("DHGAUA", selectQuery);

		Cursor c = getWDatabase(context).rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				UserAnswer us = new UserAnswer();
				us.setOptionText(c.getString(c.getColumnIndex(C_USER_ENTERED_ANSWERS)));

				userAnswers.add(us.getOptionText());
			} while (c.moveToNext());
		}

		c.close();
		return userAnswers;
	}

    public void deleteAllData(){
        SQLiteDatabase db = getWDatabase(context);
        db.delete(TABLE_OPTION,null,null);
        db.delete(TABLE_DATA,null,null);
        db.delete(TABLE_MACRO,null,null);
        db.delete(TABLE_PROTOCOL,null,null);
        db.delete(TABLE_QUESTION,null,null);
        db.delete(TABLE_RESEARCH_PROJECT,null,null);
        db.delete(TABLE_PROJECT_LEAD,null,null);
        db.delete(TABLE_REMEMBER_ANSWERS,null,null);
		db.delete(TABLE_RESULTS,null,null);
		db.delete(TABLE_USER_ANSWERS,null,null);
		//closeWriteDatabase();
    }

	public void deleteOptions(String project_id) {

		getWDatabase(context).delete(TABLE_OPTION, C_PROJECT_ID + " = ?",
				new String[]{null != project_id ? project_id : ""});

	}

	public void deleteQuestions(String project_id) {

		getWDatabase(context).delete(TABLE_QUESTION, C_PROJECT_ID + " = ?",
				new String[] { project_id});

	}
}
