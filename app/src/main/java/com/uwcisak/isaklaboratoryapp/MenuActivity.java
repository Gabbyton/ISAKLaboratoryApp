package com.uwcisak.isaklaboratoryapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    Button borrowButton;
    Button returnButton;
    Button instructionsButton;
    Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        borrowButton = findViewById(R.id.borrowButton);
        returnButton = findViewById( R.id.returningButton);
        instructionsButton = findViewById(R.id.instructionsButton);
        closeButton = findViewById(R.id.closeButton);

        borrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent borrowIntent = new Intent( getApplicationContext() , BorrowActivity.class);
                startActivity( borrowIntent );
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext() , ReturnActivity.class);
                startActivity( intent );
            }
        });

        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext() , InstructionsActivity.class);
                startActivity( intent );
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
