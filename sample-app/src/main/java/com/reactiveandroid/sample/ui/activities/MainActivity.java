package com.reactiveandroid.sample.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.mvp.presenters.NotesListPresenter;
import com.reactiveandroid.sample.mvp.views.NotesListView;
import com.reactiveandroid.sample.ui.adapters.NotesAdapter;

import java.util.List;

public class MainActivity extends MvpAppCompatActivity implements NotesListView {

    private View notesNotFoundMessage;
    private FloatingActionButton newNoteButton;
    private RecyclerView notesList;
    private NotesAdapter notesAdapter;

    @InjectPresenter
    NotesListPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesNotFoundMessage = findViewById(R.id.notes_not_found);

        newNoteButton = (FloatingActionButton) findViewById(R.id.fab_new_note);
        newNoteButton.setOnClickListener(view -> presenter.onNewNoteButtonClicked());

        notesList = (RecyclerView) findViewById(R.id.notes_list);
        notesAdapter = new NotesAdapter(this);
        notesAdapter.setOnItemClickListener(position -> presenter.onNoteSelected(position));
        notesList.setAdapter(notesAdapter);
    }

    @Override
    public void updateNotesList(List<Note> notes) {
        notesAdapter.setNotes(notes);
    }

    @Override
    public void showNotesList() {
        notesList.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNotesList() {
        notesList.setVisibility(View.GONE);
    }

    @Override
    public void showNotesNotFoundMessage() {
        notesNotFoundMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNotesNotFoundMessage() {
        notesNotFoundMessage.setVisibility(View.GONE);
    }

    @Override
    public void openNoteDetailsScreen(long noteId) {
        startActivity(NoteDetailsActivity.buildIntent(this, noteId));
    }

    //pull request - bendothall
    //Transactions API
    public void openTransactionsAPIActivityScreen() {
        startActivity(new Intent(MainActivity.this, TransactionsAPIActivity.class));
    }

    //pull request - bendothall
    //Pagination API
    public void openPaginationAPIActivityScreen() {
        startActivity(new Intent(MainActivity.this, PaginationAPIActivity.class));
    }

    @Override
    public void openFoldersEditScreen() {
        startActivity(FoldersEditActivity.buildIntent(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        initSearchItem(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_folders_edit_screen:
                presenter.onOpenFoldersEditScreenClicked();
                break;
            case R.id.delete_all_notes:
                presenter.onDeleteAllNotesClicked();
                break;

            //pull request - bendothall
            //Transactions API menu item action
            case R.id.transaction_api:
                openTransactionsAPIActivityScreen();
                break;

            //pull request - bendothall
            //Transactions API menu item action
            case R.id.pagination_api:
                openPaginationAPIActivityScreen();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSearchItem(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearchQuery(newText);
                return true;
            }
        });
    }

}
