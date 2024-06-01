package com.example.hc21018gp21022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText txtUser, txtPass;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUser = findViewById(R.id.txtUser);
        txtPass = findViewById(R.id.txtPass);
        reference = FirebaseDatabase.getInstance().getReference("Users");

    }
    public void Login(View view){
        if(!txtUser.getText().toString().equals("") && !txtPass.getText().toString().equals("")){
            reference.orderByChild("username").equalTo(txtUser.getText().toString()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot userData : snapshot.getChildren()){
                                    if(userData.child("password").getValue(String.class).equals(txtPass.getText().toString())){
                                        Toast.makeText(MainActivity.this, "Inicio de sesion exitoso. Bienvenido "+userData.child("username").getValue(String.class), Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                       @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else{
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
    public void LoadRegister(View view){
        Intent intent =  new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}