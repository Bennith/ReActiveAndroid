package com.reactiveandroid.sample.ui.activities;

//pull request - bendothall
//Transactions API Activity

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.internal.serializer.TypeSerializer;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.query.api.Transactions;
import com.reactiveandroid.sample.AppDatabase;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.reactiveandroid.query.api.Transactions.BulkCRUDUpdates;
import static com.reactiveandroid.query.api.Transactions.EndTransactions;

public class TransactionsAPIActivity extends AppCompatActivity {

    CheckBox
            purge_data,
            use_transactions,
            use_bulk_updates;

    EditText
            transactions_entries;

    Button
            process,
            purgeOnlyAction;

    ProgressBar
            overallProgress;

    TextView
            messages;

    public static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public static Date date = new Date();
    Date startTime = new Date();
    Date endTime = new Date();
    String startTimeHumanFriendly = "";
    String endTimeHumanFriendly = "";

    int getPassedDataIndex = 0;
    int rowProcessedCount = 0;
    int actualNotesCount = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.transactions_api_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Transactions API");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        purge_data = (CheckBox)findViewById(R.id.purge_data);
        use_transactions = (CheckBox)findViewById(R.id.use_transactions);
        use_bulk_updates = (CheckBox)findViewById(R.id.use_bulk_updates);

        transactions_entries = (EditText)findViewById(R.id.transactions_entries);

        process = (Button)findViewById(R.id.process);
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UIIsBusy();

                doAsyncTask beginAsyncTask = new doAsyncTask();
                beginAsyncTask.execute();

            }
        });

        purgeOnlyAction = (Button)findViewById(R.id.purgeOnlyAction);
        purgeOnlyAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UIIsBusy();
                Delete.from(Note.class).execute();
                String originalMessageContent = messages.getText().toString();
                messages.setText(
                        "-----------------------------------\n\n" +
                                "Action: Purge Only\n" +
                                "Notes Count: " + Select.from(Note.class).count() + "\n" +
                                originalMessageContent);
                UIIsFinished();

            }
        });


        messages = (TextView)findViewById(R.id.messages);
        messages.setText("");

        overallProgress = (ProgressBar)findViewById(R.id.overallProgress);
        overallProgress.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void UIIsBusy() {

        purge_data.setEnabled(false);
        use_transactions.setEnabled(false);
        use_bulk_updates.setEnabled(false);
        transactions_entries.setEnabled(false);
        process.setEnabled(false);
        overallProgress.setVisibility(View.VISIBLE);

    }

    public void UIIsFinished() {

        purge_data.setEnabled(true);
        use_transactions.setEnabled(true);
        use_bulk_updates.setEnabled(true);
        transactions_entries.setEnabled(true);
        process.setEnabled(true);
        overallProgress.setVisibility(View.GONE);

    }

    public void updateUIActions() {



    }

    public Boolean beginTransactions() {

        boolean isCompleted = true;


        try {

            List<Note> buildPassedData = new ArrayList<Note>();
            int wordLength = 25;
            actualNotesCount = 0;

            //setDefault & convert EditText to int
            int addRowsAmount = 15000;

            try {

                if(transactions_entries.getText().toString() !=null){
                    addRowsAmount = Integer.parseInt(transactions_entries.getText().toString());
                }

            }catch(Exception error) {
                Log.e("Exception", "beginTransactions().Exception().convertEditText(): " + error.toString());
            }

            //region foreach
            for (int rowIndex = 0; rowIndex < addRowsAmount; rowIndex++) {

                //https://www.baeldung.com/java-random-string
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 10;
                Random random = new Random();
                StringBuilder buffer = new StringBuilder(targetStringLength);

                for (int i = 0; i < targetStringLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    buffer.append((char) randomLimitedInt);
                }

                String generatedString = buffer.toString();

                Note note = new Note();
                note.setTitle(generatedString);
                note.setText(generatedString);
                buildPassedData.add(note);

                if(addRowsAmount == rowIndex){
                    break;
                }

            }
            //endregion foreach

            //region SQL processing ...
            startTime = new Date();
            startTimeHumanFriendly = dateFormat.format(startTime);

            if(!use_bulk_updates.isChecked()) {

                if (use_transactions.isChecked()) {
                    Transactions.BeginTransactions(AppDatabase.class);
                }


                if (purge_data.isChecked()) {
                    Delete.from(Note.class).execute();
                }

                getPassedDataIndex = 0;
                rowProcessedCount = 0;

                for(Note note : Select.from(Note.class).fetch()){

                    note.setTitle(note.getTitle() + " updated");
                    note.save(note);
                    rowProcessedCount++;

                }

                for (Note item : buildPassedData) {

                    getPassedDataIndex++;

                    String title = "";
                    title = item.getTitle();

                    if (!title.isEmpty()) {

                        if (Select.from(Note.class).where("title = ?", title).count() == 1) {

                            //update
                            Note updateNote = Select.from(Note.class).where("title = ?", title).fetchSingle();
                            updateNote.save(item);
                            rowProcessedCount++;

                        } else {


                            //create
                            Note createNote = new Note(title, title, ColorGenerator.MATERIAL.getRandomColor());
                            if (createNote.save() > 0) {
                                rowProcessedCount++;
                            }

                        }

                    }

                }


                if (use_transactions.isChecked()) {
                    EndTransactions(AppDatabase.class);
                }

            }else{

                //Use Transactions Bulk Update API
                for(Note note : Select.from(Note.class).fetch()){
                    buildPassedData.add(note);
                }

                BulkCRUDUpdates(AppDatabase.class, Note.class, buildPassedData);

            }

            actualNotesCount = Select.from(Note.class).count();

            endTime = new Date();
            endTimeHumanFriendly = dateFormat.format(endTime);
            isCompleted = true;
            //endregion SQL processing ...

        }catch (Exception error) {
            Log.e("Exception", "beginTransactions().Exception(): " + error.toString());
            isCompleted = false;
        }

        return isCompleted;

    }

    public class doAsyncTask extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {

            Boolean result = false;
            if(beginTransactions()){
                result = true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {


            String originalMessageContent = messages.getText().toString();
            messages.setText(
                    "-----------------------------------\n\n" +
                            "Purge Data: " + purge_data.isChecked() + "\n" +
                            "Using Transaction API: " + use_transactions.isChecked() + "\n\n" +
                            "Started At: " + startTimeHumanFriendly + "\n" +
                            "Ended At: " + endTimeHumanFriendly + "\n" +
                            "Execution Time: " + getExecutionTime(startTime,endTime,TimeUnit.SECONDS) + "\n\n" +
                            "Notes Count: " + actualNotesCount + "\n" +
                            "Rows Validated: " + getPassedDataIndex + "\n" +
                            "Rows Processed: " + rowProcessedCount + "\n\n" +
                            originalMessageContent);

            UIIsFinished();

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Boolean... values) {


        }
    }

    public static long getExecutionTime(Date startTime, Date endTime, TimeUnit timeUnit) {
        long diffInSeconds = endTime.getTime() - startTime.getTime();
        return timeUnit.convert(diffInSeconds, TimeUnit.MILLISECONDS);
    }

}
