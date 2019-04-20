package com.reactiveandroid.sample.ui.activities;

//pull request - bendothall
//Pagination API Activity

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.reactiveandroid.query.api.Pagination;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Note;
import com.reactiveandroid.sample.ui.adapters.pagination.PaginationAPIAdapter;
import com.reactiveandroid.sample.ui.adapters.pagination.PaginationAPIAdapterCallback;
import com.reactiveandroid.sample.ui.adapters.pagination.PaginationAPIScrollListener;

import java.util.List;

import static com.reactiveandroid.query.api.Pagination.getTableRowCount;
import static com.reactiveandroid.query.api.Pagination.loadPaginationData;

public class PaginationAPIActivity extends AppCompatActivity implements PaginationAPIAdapterCallback {

    PaginationAPIAdapter
            adapter;

    LinearLayoutManager
            linearLayoutManager;

    RecyclerView
            recycler;

    private boolean
            hasFirstPageLoaded = false;

    private boolean
            isLoading = false;

    private boolean
            isLastPage = false;

    Integer
            totalAdapterItems = 0,
            AdapterItemsReturned = 0,
            limit = 20,
            offset = 0,
            delayLoading = 500;

    String
            orderBy = "ASC",
            column = "id";

    List<Note>
            fetchPaginationResults;

    ActionBar
            actionBar;

    LinearLayout
            notesNotFoundMessage;

    TextView
            currentPaginationType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagination_api_activity);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Pagination API");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        notesNotFoundMessage = (LinearLayout) findViewById(R.id.notes_not_found);
        notesNotFoundMessage.setVisibility(View.GONE);

        currentPaginationType = (TextView) findViewById(R.id.currentPaginationType);

        //region recycler
        recycler = (RecyclerView) findViewById(R.id.recycler);
        adapter = new PaginationAPIAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recycler.addOnScrollListener(new PaginationAPIScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                if(hasFirstPageLoaded) {
                    if(isLastPage){
                        isLoading = false;
                    }else{
                        isLoading = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new loadNextDataAsyncTask().execute();
                            }
                        }, delayLoading);
                    }
                }
            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

        });
        //endregion recycler

        new loadFirstDataAsyncTask().execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pagination_api_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.filterById:
                column = "id";
                currentPaginationType.setText("Filtering By Column: id");
                resetUIView();
                break;

            case R.id.filterByTitle:
                column = "title";
                currentPaginationType.setText("Filtering By Column: title");
                resetUIView();
                break;

            case R.id.filterByText:
                column = "text";
                currentPaginationType.setText("Filtering By Column: text");
                resetUIView();
                break;

            case R.id.filterByColor:
                column = "color";
                currentPaginationType.setText("Filtering By Column: color");
                resetUIView();
                break;

            case R.id.filterByCreatedDate:
                column = "created_at";
                currentPaginationType.setText("Filtering By Column: created_at");
                resetUIView();
                break;

            case R.id.filterByUpdatedDate:
                column = "updated_at";
                currentPaginationType.setText("Filtering By Column: updated_at");
                resetUIView();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void showNotesNotFoundMessage() {
        notesNotFoundMessage.setVisibility(View.VISIBLE);
    }

    public void hideNotesNotFoundMessage() {
        notesNotFoundMessage.setVisibility(View.GONE);
    }

    public void resetUIView() {
            offset = 0;
            isLoading = false;
            hasFirstPageLoaded = false;
            isLastPage = false;
            totalAdapterItems = 0;
            AdapterItemsReturned = 0;
            adapter.clear();
            new loadFirstDataAsyncTask().execute();
    }

    public class loadFirstDataAsyncTask extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean ... params) {

            boolean result = false;

            fetchPaginationResults = loadPaginationData(column, 0, limit, orderBy, Note.class);
            AdapterItemsReturned = fetchPaginationResults.size();
            if(fetchPaginationResults.size() > 0){
                result = true;
                totalAdapterItems = totalAdapterItems + fetchPaginationResults.size();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {

                hasFirstPageLoaded = true;
                adapter.addAll(fetchPaginationResults);
                if (totalAdapterItems == getTableRowCount(Note.class)) {
                    isLastPage = true;
                    adapter.removeLoadingFooter();
                }else{
                    isLoading = false;
                    adapter.addLoadingFooter();
                }

                hideNotesNotFoundMessage();
                actionBar.setSubtitle("Displaying " + totalAdapterItems + " of " + getTableRowCount(Note.class) + " Items");

            }else{

                showNotesNotFoundMessage();
                actionBar.setSubtitle("Displaying " + totalAdapterItems + " of " + getTableRowCount(Note.class) + " Items");
                isLastPage = true;
            }

            logToConsole();

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Boolean... values) {


        }
    }

    public class loadNextDataAsyncTask extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean ... params) {

            boolean result = false;

            offset = offset + limit;
            fetchPaginationResults = loadPaginationData(column, offset, limit, orderBy, Note.class);
            AdapterItemsReturned = fetchPaginationResults.size();
            if(fetchPaginationResults.size() > 0){
                result = true;
                totalAdapterItems = totalAdapterItems + fetchPaginationResults.size();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {

                adapter.removeLoadingFooter();
                isLoading = false;

                adapter.addAll(fetchPaginationResults);
                if (totalAdapterItems == getTableRowCount(Note.class)) {
                    isLastPage = true;
                }else{
                    isLoading = false;
                    adapter.addLoadingFooter();
                }
                actionBar.setSubtitle("Displaying " + totalAdapterItems + " of " + getTableRowCount(Note.class) + " Items");

            }else{

                isLastPage = true;
                adapter.removeLoadingFooter();
                isLoading = false;

            }
            hideNotesNotFoundMessage();
            logToConsole();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Boolean... values) {


        }
    }

    public void retryPageLoad() {
        new loadNextDataAsyncTask().execute();
    }

    public void logToConsole() {

        Log.e("Pagination API", "*******************");
        Log.e("Pagination API", "orderBy: " + orderBy);
        Log.e("Pagination API", "offset: " + offset);
        Log.e("Pagination API", "limit: " + limit);
        Log.e("Pagination API", " ------------------- ");
        Log.e("Pagination API", "AdapterItemsReturned: " + AdapterItemsReturned);
        Log.e("Pagination API", "totalAdapterItems: " + totalAdapterItems);
        Log.e("Pagination API", "getTableRowCount(Note.class): " + getTableRowCount(Note.class));
        Log.e("Pagination API", "adapter.getItemCount(): " + adapter.getItemCount());
        Log.e("Pagination API", " ------------------- ");
        Log.e("Pagination API", "isLastPage: " + isLastPage);
        Log.e("Pagination API", "isLoading: " + isLoading);

    }

}
