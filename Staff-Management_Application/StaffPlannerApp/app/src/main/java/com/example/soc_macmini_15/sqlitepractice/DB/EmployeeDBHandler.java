package com.example.soc_macmini_15.sqlitepractice.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EmployeeDBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "staffs.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_EMPLOYEES = "staffs";
    public static final String COLUMN_ID = "staffId";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_HIRE_DATE = "taskScheduleDate";
    public static final String COLUMN_DEPT = "task";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TTA= "timeToDestination";
    public static final String COLUMN_DESTINATION = "destination";

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_EMPLOYEES + " (" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FIRST_NAME + " TEXT, " + COLUMN_LAST_NAME
            + " TEXT, " + COLUMN_GENDER + " TEXT, " + COLUMN_HIRE_DATE + " NUMERIC, " + COLUMN_DEPT + " TEXT " + ", " + COLUMN_STATUS
            + " TEXT," +  COLUMN_TTA + " TEXT, " + COLUMN_DESTINATION + " TEXT)";

    public EmployeeDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }
}
