package com.example.hc21018gp21022;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

    }
    public void LoadRegister(){
        Intent intent =  new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }
}