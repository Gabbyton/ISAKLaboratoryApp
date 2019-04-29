package com.uwcisak.isaklaboratoryapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.uwcisak.isaklaboratoryapp.utilities.GlobalState;
import com.uwcisak.isaklaboratoryapp.utilities.Item;
import com.uwcisak.isaklaboratoryapp.utilities.ItemListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class BorrowActivity extends AppCompatActivity {

    GlobalState state;
    ListAdapter itemListAdapter;
    ListView itemsListView;
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    final static String RAW_INPUT_OPTION = "RAW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        state = (GlobalState) getApplicationContext();
        state.setBorrowingList(new ArrayList<Item>());
        itemListAdapter = new ItemListAdapter( state , state.getInventory() );
        itemsListView = findViewById( R.id.itemsListView );
        itemsListView.setAdapter( itemListAdapter );

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = state.getBorrowingList().get( position );
                state.getBorrowingList().remove(item);
                ItemListAdapter adapter = new ItemListAdapter(getApplicationContext(), state.getBorrowingList());
                itemsListView.setAdapter( adapter );
                Toast.makeText(state, (item.getName() + getText( R.string.item_removed_message_append )), Toast.LENGTH_SHORT).show();
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage( getText( R.string.updating_ledger_message ) );

        mCredential = state.getGoogleAccountCredential();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ItemListAdapter adapter = new ItemListAdapter(getApplicationContext(), state.getBorrowingList());
        itemsListView.setAdapter( adapter );
    }

    public void addButtonClick(View v ) {
        Intent i = new Intent( state , ScanActivity.class );
        startActivity( i );
    }

    public void borrowItems( View view ) {
        if (! isDeviceOnline()) {
            Toast.makeText(this, getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
        } else {
            new BorrowActivity.MakeRequestTask( mCredential ).execute();
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString( R.string.app_name ))
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                writeDataViaApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1cKLXUmRcHRxQPwPXpK9gbQjYnoxScpiaH3n9FKrA0nA/edit?usp=sharing
         * @return List of names and majors
         * @throws IOException
         */
        private Void writeDataViaApi() throws IOException {
            String spreadsheetId = getString( R.string.document_id );
            String range = getText( R.string.view_range_item_ledger ).toString();
            String email = mCredential.getSelectedAccountName();
            String status = getText( R.string.borrow_status_text ).toString();
            String timeStamp = Calendar.getInstance().getTime().toString();
            ArrayList<Item> borrowing = (ArrayList<Item>) state.getBorrowingList().clone();
            List<List<Object>> values = new ArrayList<>();
            for( Item item : borrowing ) {
                List<Object> row = new ArrayList<>();
                row.add(email);
                row.add(item.getCode());
                row.add(status);
                row.add(timeStamp);
                values.add(row);
            }
            ValueRange body = new ValueRange()
                    .setValues(values);
            AppendValuesResponse result =
                    this.mService.spreadsheets().values().append(spreadsheetId, range, body)
                            .setValueInputOption( RAW_INPUT_OPTION )
                            .execute();

            // writing on the student possession sheet
            // remember to update the GlobalState Lists
            ArrayList<String> updateItemCodes = new ArrayList<>();
            int rowNo = state.getReadResult().getPossessionSheetRowNo() + 2;
            String newRange = getText( R.string.view_range_student_possession_ltd ).toString() + rowNo + ":" + rowNo; // Sloppy range definition
            updateItemCodes.addAll( state.getReadResult().getPreviouslyLoanedList() );
            for ( Item item : borrowing ) {
                if ( ! updateItemCodes.contains( item.getCode() ) ) {
                    updateItemCodes.add( item.getCode() );
                }
            }
            StringBuilder newEntry = new StringBuilder();
            for ( String code : updateItemCodes ) {
                newEntry.append(code).append(";");
            }

            List<Object> updateCols = new ArrayList<>();
            updateCols.add( mCredential.getSelectedAccountName() );
            updateCols.add( newEntry.toString() );
            List<List<Object>> updateValues = Collections.singletonList(updateCols);

            ValueRange updateBody = new ValueRange()
                    .setValues(updateValues);
            UpdateValuesResponse updateResult =
                    mService.spreadsheets().values().update(spreadsheetId, newRange, updateBody)
                            .setValueInputOption(RAW_INPUT_OPTION)
                            .execute();
            state.getReadResult().setPreviouslyLoanedList( updateItemCodes );

            return null;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Void voidObj) {
            mProgress.hide();
            finish();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(BorrowActivity.this, "It's in borrowed: " +
                            mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d( "BorrowActivity" , mLastError.getMessage() );
                }
            } else {
                Toast.makeText(BorrowActivity.this, getString( R.string.request_cancelled_message ), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
