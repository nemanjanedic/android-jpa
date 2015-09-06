package com.nemanjanedic.androidjpa.demoapp;

import com.nemanjanedic.androidjpa.demoapp.dao.Notepad;
import com.nemanjanedic.androidjpa.demoapp.dao.NotepadDao;
import com.nemanjanedic.androidjpa.demoapp.db.DatabaseOpenHelper;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "androidjpa-testapp";

    private Button insertButton;

    private Button updateButton;

    private Button deleteButton;

    private TextView textArea;

    private DatabaseOpenHelper dbOpenHelper;

    private NotepadDao notepadDao;

    private final ArrayList<Notepad> notepadCache = new ArrayList<Notepad>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        insertButton = (Button) findViewById(R.id.insertButton);
        updateButton = (Button) findViewById(R.id.updateButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        textArea = (TextView) findViewById(R.id.textArea);

        insertButton.setOnClickListener(insertButtonOnClickListener);
        updateButton.setOnClickListener(updateButtonOnClickListener);
        deleteButton.setOnClickListener(deleteButtonOnClickListener);

        dbOpenHelper = new DatabaseOpenHelper(this);
        notepadDao = new NotepadDao();
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

    @Override
    protected void onResume() {
        super.onResume();

        LoadNotepadsTask loadNotepadsTask = new LoadNotepadsTask();
        loadNotepadsTask.execute();
    }

    private final View.OnClickListener insertButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            Notepad newNotepad = new Notepad();
            Date now = new Date();
            newNotepad.setName("Notepad [" + now.toGMTString() + "]");
            newNotepad.setDateCreated(now.getTime());
            newNotepad.setDateModified(now.getTime());

            SaveNotepadTask saveNotepadTask = new SaveNotepadTask();
            saveNotepadTask.execute(newNotepad);
        }
    };

    private final View.OnClickListener updateButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            if (!notepadCache.isEmpty()) {
                Notepad notepad = notepadCache.get(notepadCache.size() - 1);
                Date now = new Date();
                notepad.setName("Notepad [" + now.toGMTString() + "]");
                notepad.setDateModified(now.getTime());

                SaveNotepadTask saveNotepadTask = new SaveNotepadTask();
                saveNotepadTask.execute(notepad);
            }
        }
    };

    private final View.OnClickListener deleteButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            if (!notepadCache.isEmpty()) {
                Notepad notepad = notepadCache.get(notepadCache.size() - 1);

                DeleteNotepadTask deleteNotepadTask = new DeleteNotepadTask();
                deleteNotepadTask.execute(notepad);
            }
        }
    };

    private class LoadNotepadsTask extends AsyncTask<Void, Void, List<Notepad>> {

        @Override
        protected List<Notepad> doInBackground(final Void... params) {
            SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
            return notepadDao.findAll(db);
        }

        @Override
        protected void onPostExecute(final List<Notepad> result) {
            notepadCache.clear();
            notepadCache.addAll(result);
            for (Notepad notepad : notepadCache) {
                String logLine = "\nLoaded: " + notepad.getName() + " with id: " + notepad.getId();
                textArea.append(logLine);
            }
        }

    }

    private class SaveNotepadTask extends AsyncTask<Notepad, Void, Notepad> {

        @Override
        protected Notepad doInBackground(final Notepad... params) {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            Notepad savedNotepad = notepadDao.save(params[0], db);
            return savedNotepad;
        }

        @Override
        protected void onPostExecute(final Notepad result) {
            String logLine = "\nSaved: " + result.getName() + " with id: " + result.getId();
            textArea.append(logLine);
            if (!notepadCache.contains(result)) {
                notepadCache.add(result);
            }
        }

    }

    private class DeleteNotepadTask extends AsyncTask<Notepad, Void, Notepad> {

        @Override
        protected Notepad doInBackground(final Notepad... params) {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            notepadDao.delete(params[0], db);
            return params[0];
        }

        @Override
        protected void onPostExecute(final Notepad result) {
            String logLine = "\nDeleted Notepad with id: " + result.getId();
            textArea.append(logLine);
            if (notepadCache.contains(result)) {
                notepadCache.remove(result);
            }
        }

    }

}
