package com.example.hc21018gp21022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private EditText txtUser, txtEmail, txtPass,txtPass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        txtUser = findViewById(R.id.txtUserRegister);
        txtEmail = findViewById(R.id.txtEmailRegister);
        txtPass = findViewById(R.id.txtPassRegister1);
        txtPass2 = findViewById(R.id.txtPassRegister2);

    }
    public void RegisterUser(View view){
        if(!txtUser.getText().toString().equals("") && !txtEmail.getText().toString().equals("")
                && !txtPass.getText().toString().equals("") && !txtPass2.getText().toString().equals("")){
            if(txtPass.getText().toString().equals(txtPass2.getText().toString())){

                if(ValidateEmail.isValidEmail(txtEmail.getText().toString())){
                    reference.orderByChild("username").equalTo(txtUser.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(RegisterActivity.this, "El usuario ingresado ya existe", Toast.LENGTH_SHORT).show();
                            }else{
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username",txtUser.getText().toString());
                                userMap.put("email",txtEmail.getText().toString());
                                userMap.put("password",txtPass.getText().toString());
                                userMap.put("idPost", new ArrayList<>());
                                userMap.put("idFavorite", new ArrayList<>());
                                reference.push().setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Error al registrarse " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(this, "Email no valido", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "No dejes campos vacios", Toast.LENGTH_SHORT).show();
        }

    }
}