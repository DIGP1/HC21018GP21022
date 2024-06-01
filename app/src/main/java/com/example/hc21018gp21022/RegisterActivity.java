package com.example.hc21018gp21022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hc21018gp21022.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private EditText txtUser, txtEmail, txtPass,txtPass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reference = FirebaseDatabase.getInstance().getReference();
        txtUser = findViewById(R.id.txtUserRegister);
        txtEmail = findViewById(R.id.txtEmailRegister);
        txtPass = findViewById(R.id.txtPassRegister1);
        txtPass2 = findViewById(R.id.txtPassRegister2);

    }
    public void RegisterUser(){
        if(txtPass.equals(txtPass2)){
            User user = new User();
            user.username = txtUser.toString();
            user.email = txtEmail.toString();
            user.password = txtPass.toString();
            reference.child("Users").push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
    }
}