package com.example.ahmed.simplenode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    List<Note> noteList=new ArrayList<>();
    NoteAdapter adapter;
    RecyclerView recyclerView;
    TextView textViewEmptyText;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        textViewEmptyText = findViewById(R.id.empty_notes_view);

        databaseHelper = new DatabaseHelper(this);
        noteList.addAll(databaseHelper.getAllNotes());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });


        adapter = new NoteAdapter(noteList, this);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapter);

        toggleEmptyNotes();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                ShowActionDialog(position);
            }
        }));
    }

    private void ShowActionDialog(final int position) {
        CharSequence sequence[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(sequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, noteList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    private void toggleEmptyNotes() {
        if (databaseHelper.getNotesCount() > 0) {
            textViewEmptyText.setVisibility(View.GONE);
        } else {
            textViewEmptyText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean b, final Note note, final int position) {
        inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.node_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        final EditText editTextInput = view.findViewById(R.id.note);
        TextView textViewInput = view.findViewById(R.id.dialog_title);

        textViewInput.setText(!b ? getString(R.string.new_note_title) : getString(R.string.edit_note_title));

        if (b && note != null) {
            editTextInput.setText(note.getNote());
        }
        builder.setCancelable(false)
                .setPositiveButton(b ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                String input = editTextInput.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(MainActivity.this, "Enter node!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    dialog.dismiss();
                }
                // check if user updating note
                if (b && note != null) {
                    // update note by it's id
                    updateNote(input, position);
                } else {
                    // create new note
                    createNote(input);
                }
            }
        });

    }

    private void createNote(String input) {
        long id = databaseHelper.insertNote(input);//inserted note id
        Note note = databaseHelper.getNote(id);
        if (note != null) {
            noteList.add(0, note);// adding new note to array list at 0 position
            adapter.notifyDataSetChanged();    // refreshing the list
            toggleEmptyNotes();
        }
    }

    private void updateNote(String input, int position) {

        Note note = noteList.get(position);
        note.setNote(input);//updating note text
        databaseHelper.UpdateNote(note);  // updating note in db
        // refreshing the list
        noteList.set(position, note);
        adapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    private void deleteNote(int i) {
        databaseHelper.DeleteNote(noteList.get(i));// deleting the note from db
        // removing the note from the list
        noteList.remove(i);
        adapter.notifyItemRemoved(i);
        toggleEmptyNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
