package com.example.hassaanproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // <-- Import EditText
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList; // <-- Import ArrayList
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout notesContainer;
    private Button btnNewNote;
    private EditText etSearch; // <-- The search bar
    private List<String[]> allNotes = new ArrayList<>(); // <-- To hold the master list of notes

    private final ActivityResultLauncher<Intent> noteEditorLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadNotes(); // Reload notes from DB after an edit/create/delete
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);

        dbHelper = new DatabaseHelper(this);
        notesContainer = findViewById(R.id.notes_list_container);
        btnNewNote = findViewById(R.id.btnNewNote);
        etSearch = findViewById(R.id.etSearch); // <-- Initialize the search bar

        loadNotes(); // Initial load of all notes

        btnNewNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
            noteEditorLauncher.launch(intent);
        });

        // --- ADD SEARCH FUNCTIONALITY ---
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When user types, filter the notes
                filterAndDisplayNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void loadNotes() {
        // Fetch all notes from the database and store them
        allNotes = dbHelper.getAllNotes();
        // Display the notes (initially unfiltered)
        filterAndDisplayNotes(etSearch.getText().toString());
    }

    private void filterAndDisplayNotes(String query) {
        notesContainer.removeAllViews();
        List<String[]> filteredNotes = new ArrayList<>();

        // If query is empty, show all notes. Otherwise, filter them.
        if (query.isEmpty()) {
            filteredNotes.addAll(allNotes);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (String[] note : allNotes) {
                String title = note[1].toLowerCase();
                String content = note[2].toLowerCase();
                if (title.contains(lowerCaseQuery) || content.contains(lowerCaseQuery)) {
                    filteredNotes.add(note);
                }
            }
        }

        // --- Display logic (moved from loadNotes) ---
        if (filteredNotes.isEmpty()) {
            TextView emptyView = new TextView(this);
            // Show different message depending on if user is searching or has no notes
            if (allNotes.isEmpty()) {
                emptyView.setText("No notes yet. Tap 'New Note' to add one!");
            } else {
                emptyView.setText("No notes found for your search.");
            }
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextSize(16);
            emptyView.setPadding(0, 48, 0, 48);
            notesContainer.addView(emptyView);
        } else {
            // Iterate in reverse to show newest notes first
            for (int i = filteredNotes.size() - 1; i >= 0; i--) {
                String[] note = filteredNotes.get(i);
                View noteCard = createNoteCard(note);
                notesContainer.addView(noteCard);
            }
        }
    }


    private View createNoteCard(String[] noteData) {
        long noteId = Long.parseLong(noteData[0]);
        String noteTitle = noteData[1];
        String noteContent = noteData[2];

        // Use a CardView for a nicer look
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 24); // Margin between cards
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(16f);
        cardView.setCardElevation(8f);
        cardView.setContentPadding(24, 24, 24, 24);
        cardView.setCardBackgroundColor(0xFFFFFFFF); // White background

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);

        TextView titleView = new TextView(this);
        titleView.setText(noteTitle);
        titleView.setTextSize(18);
        titleView.setTextColor(0xFF000000); // Black text
        titleView.setMaxLines(1);
        titleView.setEllipsize(TextUtils.TruncateAt.END);

        TextView contentView = new TextView(this);
        contentView.setText(noteContent);
        contentView.setTextSize(14);
        contentView.setTextColor(0xFF555555); // Gray text
        contentView.setMaxLines(3);
        contentView.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.setMargins(0, 8, 0, 0);
        contentView.setLayoutParams(contentParams);

        innerLayout.addView(titleView);
        innerLayout.addView(contentView);
        cardView.addView(innerLayout);

        // Set click listener to open the editor for THIS note
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
            intent.putExtra("NOTE_ID", noteId);
            intent.putExtra("NOTE_TITLE", noteTitle);
            intent.putExtra("NOTE_CONTENT", noteContent);
            noteEditorLauncher.launch(intent);
        });

        return cardView;
    }
}
