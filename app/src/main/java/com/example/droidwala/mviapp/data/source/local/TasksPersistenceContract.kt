package com.example.droidwala.mviapp.data.source.local

import android.provider.BaseColumns

/**
 * Created by punitdama on 06/01/18.
 */
object TasksPersistenceContract{

    object TaskEntry : BaseColumns{
        const val TABLE_NAME = "tasks"
        const val COLUMN_NAME_ENTRY_ID = "entryid"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_COMPLETED = "completed"
    }
}