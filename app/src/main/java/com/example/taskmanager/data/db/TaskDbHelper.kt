package com.example.taskmanager.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.taskmanager.data.model.TaskModel

class TaskDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "taskmanager.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_TASKS = "tasks"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_DESC = "description"
        const val COL_SUBJECT = "subject"
        const val COL_DEADLINE = "deadline"
        const val COL_PRIORITY = "priority"
        const val COL_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val create = """
            CREATE TABLE $TABLE_TASKS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_DESC TEXT,
                $COL_SUBJECT TEXT,
                $COL_DEADLINE INTEGER,
                $COL_PRIORITY INTEGER DEFAULT 0,
                $COL_STATUS INTEGER DEFAULT 0
            );
        """.trimIndent()
        db.execSQL(create)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun insertTask(task: TaskModel): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_TITLE, task.title)
            put(COL_DESC, task.description)
            put(COL_SUBJECT, task.subject)
            put(COL_DEADLINE, task.deadlineMillis)
            put(COL_PRIORITY, task.priority)
            put(COL_STATUS, task.status)
        }
        val id = db.insert(TABLE_TASKS, null, cv)
        db.close()
        return id
    }

    fun updateTask(task: TaskModel): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_TITLE, task.title)
            put(COL_DESC, task.description)
            put(COL_SUBJECT, task.subject)
            put(COL_DEADLINE, task.deadlineMillis)
            put(COL_PRIORITY, task.priority)
            put(COL_STATUS, task.status)
        }
        val rows = db.update(TABLE_TASKS, cv, "$COL_ID = ?", arrayOf(task.id.toString()))
        db.close()
        return rows
    }

    fun deleteTask(id: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_TASKS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun getAllTasks(): List<TaskModel> {
        val list = mutableListOf<TaskModel>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_TASKS, null, null, null, null, null, "$COL_DEADLINE ASC")
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val t = TaskModel(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_DESC)),
                        subject = it.getString(it.getColumnIndexOrThrow(COL_SUBJECT)),
                        deadlineMillis = it.getLong(it.getColumnIndexOrThrow(COL_DEADLINE)),
                        priority = it.getInt(it.getColumnIndexOrThrow(COL_PRIORITY)),
                        status = it.getInt(it.getColumnIndexOrThrow(COL_STATUS))
                    )
                    list.add(t)
                } while (it.moveToNext())
            }
        }
        db.close()
        return list
    }
}
