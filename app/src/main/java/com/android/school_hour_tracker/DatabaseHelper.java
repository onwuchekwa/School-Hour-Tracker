package com.android.school_hour_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    // Database name and version
    private static final String DATABASE_NAME = "hour_log.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table Name
    private static final String CLASS_TABLE_NAME = "classes";
    private static final String STUDY_TABLE_NAME = "study_records";
    
    // Classes Table Columns
    private static final String CLASS_ID = "_id";
    private static final String CLASS_CODE = "classCode";
    private static final String CLASS_NAME = "className";

    // Study Records Table Columns
    private static final String STUDY_RECORD_ID = "_id";
    private static final String STUDY_CLASS_ID = "recordClassId";
    private static final String STUDY_RECORD_DATE = "recordDate";
    private static final String STUDY_RECORD_START_TIME = "recordStartTime";
    private static final String STUDY_RECORD_END_TIME = "recordEndTime";
    private static final String STUDY_RECORD_ACTUAL_TIME = "recordActualTime";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table with table name student classes
        String createClassTable = "CREATE TABLE " + CLASS_TABLE_NAME
                + "("
                + CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CLASS_CODE + " TEXT NOT NULL, "
                + CLASS_NAME + " TEXT NOT NULL, "
                + "UNIQUE(" + CLASS_CODE + ", " + CLASS_NAME + ")"
                +")";

        // Create table with table name study hours
        String createStudyRecordTable = "CREATE TABLE " + STUDY_TABLE_NAME
                + "(" + STUDY_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUDY_CLASS_ID + " INTEGER NOT NULL, "
                + STUDY_RECORD_DATE + " TEXT NOT NULL, "
                + STUDY_RECORD_START_TIME + " TEXT NOT NULL, "
                + STUDY_RECORD_END_TIME + " TEXT NOT NULL, "
                + STUDY_RECORD_ACTUAL_TIME + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + STUDY_CLASS_ID + ") REFERENCES " + CLASS_TABLE_NAME + "(" + CLASS_ID + "),"
                + "UNIQUE(" + STUDY_CLASS_ID + ", " + STUDY_RECORD_DATE + ", " + STUDY_RECORD_START_TIME + ", "
                + STUDY_RECORD_END_TIME + ", " + STUDY_RECORD_ACTUAL_TIME + ")"
                + ")";

        db.execSQL(createClassTable);
        db.execSQL(createStudyRecordTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CLASS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STUDY_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert class in to the table
     * @param classId classCode column
     * @param className className Column
     * @return insert state
     */
    public boolean addClasses(String classId, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CLASS_CODE, classId);
        contentValues.put(CLASS_NAME, className);

        Log.d(TAG, "AddClasses " + classId + " and " + className + " to " + CLASS_TABLE_NAME);
        long result = db.insert(CLASS_TABLE_NAME, null, contentValues);

        /* If data is inserted incorrectly, it will return -1 */
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the classes from database
     * @return all classes
     */
    public Cursor getAllClasses() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + CLASS_TABLE_NAME;
        Cursor rawData = db.rawQuery(selectQuery, null);
        return rawData;
    }

    /**
     * Get class Id using the class name
     * @param className name of class
     * @return Id of class
     */
    public Cursor getClassId(String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + CLASS_ID + " FROM " + CLASS_TABLE_NAME +
                " WHERE " + CLASS_NAME + " = '" + className + "'";
        Cursor rawData = db.rawQuery(selectQuery, null);
        return rawData;
    }

    /**
     * Updates class table
     * @param classId classId
     * @param classCode classCode
     * @param className className
     */
    public void updateClassData(int classId, String classCode, String className) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + CLASS_TABLE_NAME + " SET " + CLASS_CODE +
                " = '" + classCode + "', " + CLASS_NAME + " = '" + className +
                "' WHERE " + CLASS_ID + " = '" + classId + "'";
        Log.d(TAG, "Update class query: " + updateQuery);
        Log.d(TAG, "Updating class code to: " + classCode + " class name to: " + className);
        db.execSQL(updateQuery);
    }

    /**
     * deletes class from the database
     * @param classId get class id from intent
     */
    public void deleteClassData(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + CLASS_TABLE_NAME + " WHERE " +
                CLASS_ID + " = '" + classId + "'";
        Log.d(TAG, "Delete class query: " + deleteQuery);
        Log.d(TAG, "Deleting : " + classId + " from database");
        db.execSQL(deleteQuery);
    }

    public boolean addStudyRecord(int recordClassId, String recordDate, String recordStartTime, String recordEndTime,
                                  String recordActualTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STUDY_CLASS_ID, recordClassId);
        contentValues.put(STUDY_RECORD_DATE, recordDate);
        contentValues.put(STUDY_RECORD_START_TIME, recordStartTime);
        contentValues.put(STUDY_RECORD_END_TIME, recordEndTime);
        contentValues.put(STUDY_RECORD_ACTUAL_TIME, recordActualTime);

        Log.d(TAG, "addStudyRecord " + recordClassId + ", " + recordDate + ", " +  recordStartTime + ", " +
                recordEndTime + ", " + recordActualTime + " to " + STUDY_TABLE_NAME);
        long result = db.insert(STUDY_TABLE_NAME, null, contentValues);

        /* If data is inserted incorrectly, it will return -1 */
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllStudyHours(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "
                + STUDY_RECORD_DATE + ", "
                + STUDY_RECORD_START_TIME + ", "
                + STUDY_RECORD_END_TIME + ", "
                + STUDY_RECORD_ACTUAL_TIME + ", sr."
                + STUDY_RECORD_ID
                + " FROM " + STUDY_TABLE_NAME + " sr"
                + " JOIN " + CLASS_TABLE_NAME + " cl"
                + " ON sr." + STUDY_CLASS_ID + " = cl." + CLASS_ID
                + " WHERE sr." + STUDY_CLASS_ID + " = " + classId
                + " ORDER BY " + STUDY_RECORD_DATE + " DESC, " + STUDY_RECORD_START_TIME + " DESC";
        Cursor rawData = db.rawQuery(selectQuery, null);
        return rawData;
    }
}
