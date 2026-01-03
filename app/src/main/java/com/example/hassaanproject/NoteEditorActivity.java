package com.example.hassaanproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NoteEditorActivity extends AppCompatActivity {

    private EditText etNoteTitle, etNoteContent;
    private DatabaseHelper dbHelper;
    private long existingNoteId = -1; // -1 indicates a new note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar_note_editor);
        setSupportActionBar(toolbar);

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);

        // Check if an existing note is being edited
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("NOTE_ID")) {
            existingNoteId = intent.getLongExtra("NOTE_ID", -1);
            String title = intent.getStringExtra("NOTE_TITLE");
            String content = intent.getStringExtra("NOTE_CONTENT");

            etNoteTitle.setText(title);
            etNoteContent.setText(content);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Note");
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New Note");
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_editor, menu);
        // Hide delete button for new notes
        if (existingNoteId == -1) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete_note);
            if (deleteItem != null) {
                deleteItem.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_note) {
            saveOrUpdateNote();
            return true;
        } else if (id == R.id.action_delete_note) {
            confirmDelete();
            return true;
        } else if (id == android.R.id.home) {
            finish(); // Just go back, don't save automatically
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveOrUpdateNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            title = "Untitled Note";
        }

        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Cannot save an empty note", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existingNoteId == -1) { // This is a new note
            long result = dbHelper.addNote(title, content);
            if (result != -1) {
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
            }
        } else { // This is an existing note
            int rowsAffected = dbHelper.updateNote(existingNoteId, title, content);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error updating note", Toast.LENGTH_SHORT).show();
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteNote(existingNoteId);
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // To refresh the list
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
