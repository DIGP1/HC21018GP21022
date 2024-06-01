package com.example.hc21018gp21022;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void LoadRegister(){
        Intent intent =  new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }
}