package com.uwcisak.isaklaboratoryapp;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.uwcisak.isaklaboratoryapp.utilities.GlobalState;
import com.uwcisak.isaklaboratoryapp.utilities.Item;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(!checkPermission()) {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), getText( R.string.permission_granted_message ), Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), getText( R.string.permission_denied_message ), Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel(getText( R.string.request_permission__message ).toString() ,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(com.uwcisak.isaklaboratoryapp.ScanActivity.this)
                .setMessage(message)
                .setPositiveButton(getText( R.string.okay ), okListener)
                .setNegativeButton(getText( R.string.cancel ), null)
                .create()
                .show();
    }

    @Override
    public void handleResult(final Result result) {
        final String myResult = result.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( getText( R.string.camera_handler_header ) );
        builder.setPositiveButton(getText( R.string.okay ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( ! successfullyAddingBorrowingItem( result.getText() ) )
                    finish();
                else
                    scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });
        builder.setNeutralButton(getText( R.string.try_again ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });
        builder.setMessage(result.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    private boolean successfullyAddingBorrowingItem(String result ) {
        GlobalState state = (GlobalState) getApplicationContext();
        Item item = null;
        boolean noItem = true;
        for ( Item ledgerItem : state.getInventory() ) {
            if ( ledgerItem.getCode().equals( result ) ) {
                item = ledgerItem;
                noItem = false;
                break;
            }
        }
        if ( noItem ) {
            Toast.makeText(state, getText( R.string.missing_from_index_message ), Toast.LENGTH_SHORT).show();
            return true;
        }
        else if ( state.getBorrowingList().contains( item ) ) {
            Toast.makeText(state, getText( R.string.already_exists_message ), Toast.LENGTH_SHORT).show();
            return true;
        }

        state.getBorrowingList().add( item );
        return false;
    }
}