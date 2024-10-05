package com.example.disasterrecovery

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Data class untuk menyimpan informasi profil pengguna
data class UserProfile(val id: Int, val name: String, val email: String, val photo: String)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserProfileDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "UserProfile"
        private const val COLUMN_ID = "id" // Tambahkan kolom ID
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHOTO = "photo"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_EMAIL TEXT, $COLUMN_PHOTO TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUserProfile(name: String, email: String, photo: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHOTO, photo)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun getAllUserProfiles(): List<UserProfile> { // Mengembalikan list UserProfile
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val profiles = mutableListOf<UserProfile>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) // Mendapatkan ID
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val photo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
                profiles.add(UserProfile(id, name, email, photo)) // Menggunakan data class UserProfile
            } while (cursor.moveToNext())
        }
        cursor.close()
        return profiles
    }
}
