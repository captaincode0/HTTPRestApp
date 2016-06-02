package com.example.captaincode.httprestapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{
    private Button nuevo, consultar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nuevo = (Button)findViewById(R.id.btnnuevo);
        consultar = (Button) findViewById(R.id.btnconsultar);
        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Talleres.class);
                startActivity(intent);
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), viewtallerres.class);
                startActivity(intent);
            }
        });
    }


}
