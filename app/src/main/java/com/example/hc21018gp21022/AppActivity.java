package com.example.hc21018gp21022;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AppActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app);

        Fragment usuario = new UsuarioFragment();
        Fragment populares = new PopularesFragment();
        Fragment destinos = new DestinosFragment();
        Fragment favoritos = new FavoritosFragment();
    }
}