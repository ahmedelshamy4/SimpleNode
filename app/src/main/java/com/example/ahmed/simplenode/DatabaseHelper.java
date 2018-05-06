package com.example.ahmed.simplenode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmed on 4/5/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATA_BASE_VERSION = 1; // Database Version
    public static final String DATA_BASE_NAME = "notes_db";    // Database Name

    public DatabaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);// Creating Tables
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
        onCreate(db); // Create tables again
    }

    public long insertNote(String note) {
        SQLiteDatabase database = this.getWritableDatabase();// get writable database as we want to write data
        ContentValues values = new ContentValues(); // id and timestamp will be inserted automatically.
        values.put(Note.COLUMN_NOTE, note);
        long id = database.insert(Note.TABLE_NAME, null, values);// insert row
        database.close();// close db connection
        return id;
    }

    public int UpdateNote(Note note) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.getNote());

        // updating row
        return database.update(Note.TABLE_NAME, values, Note.COLUMN_ID + "=?", new String[]{String.valueOf(note.getId())});

    }

    public void DeleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public Note getNote(long id) {
        SQLiteDatabase database = this.getReadableDatabase(); // get readable database as we are not inserting anything
        Cursor cursor = database.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
        cursor.close(); // close the db connection
        return note;

    }

    public List<Note> getAllNotes() {
        SQLiteDatabase database = this.getWritableDatabase();
        List<Note> noteList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                noteList.add(note);

            } while (cursor.moveToNext());
        }
        database.close();
        return noteList;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
