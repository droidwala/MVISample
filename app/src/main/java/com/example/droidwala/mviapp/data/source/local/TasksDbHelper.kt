package com.example.droidwala.mviapp.data.source.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.droidwala.mviapp.data.source.local.TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
import com.example.droidwala.mviapp.data.source.local.TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION
import com.example.droidwala.mviapp.data.source.local.TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID
import com.example.droidwala.mviapp.data.source.local.TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE
import com.example.droidwala.mviapp.data.source.local.TasksPersistenceContract.TaskEntry.TABLE_NAME
import javax.inject.Inject

/**
 * Created by punitdama on 06/01/18.
 */

class TasksDbHelper @Inject constructor(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Not required as we are not dealing with updating Db
    }


    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "Tasks.db"
        private val TEXT_TYPE = " TEXT"
        private val BOOLEAN_TYPE = " INTEGER"
        private val COMMA_SEP = ","
        private val SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ENTRY_ID + TEXT_TYPE + " PRIMARY KEY," +
                COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_COMPLETED + BOOLEAN_TYPE +
                " )"

    }


}