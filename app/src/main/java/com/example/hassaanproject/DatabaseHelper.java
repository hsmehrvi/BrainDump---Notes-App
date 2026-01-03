package com.example.hassaanproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KeepNotes.db";
    private static final int DATABASE_VERSION = 1;

    // --- User Table ---
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
            + COLUMN_USER_PASSWORD + " TEXT"
            + ")";

    // --- Notes Table ---
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_NOTE_ID = "id";
    private static final String COLUMN_NOTE_TITLE = "title";
    private static final String COLUMN_NOTE_CONTENT = "content";
    // We can add a user ID to link notes to users later if needed

    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTES + "("
            + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOTE_TITLE + " TEXT,"
            + COLUMN_NOTE_CONTENT + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create both tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // --- User Methods ---

    public boolean addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // --- Notes Methods ---

    /**
     * Adds a new note to the database.
     * @return the ID of the new note, or -1 if it failed.
     */
    public long addNote(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, title);
        values.put(COLUMN_NOTE_CONTENT, content);

        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    /**
     * Retrieves all notes from the database.
     * For simplicity, this returns a list of simple string arrays.
     * In a more complex app, you would use a Note object.
     * @return a List where each item is a String[] {id, title, content}
     */
    public List<String[]> getAllNotes() {
        List<String[]> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        if (cursor.moveToFirst()) {
            do {
                String[] note = {
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))
                };
                notesList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notesList;
    }

    // ... (keep all existing methods like addUser, checkUser, addNote, getAllNotes)

    /**
     * Updates an existing note in the database.
     * @return the number of rows affected, should be 1 if successful.
     */
    public int updateNote(long id, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, title);
        values.put(COLUMN_NOTE_CONTENT, content);

        // Update the row where the note ID matches
        return db.update(TABLE_NOTES, values, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Deletes a note from the database.
     * @param id The ID of the note to delete.
     */
    public void deleteNote(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete the row where the note ID matches
        db.delete(TABLE_NOTES, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

}
