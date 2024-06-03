package com.example.hc21018gp21022;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hc21018gp21022.Fragments.DestinosFragment;
import com.example.hc21018gp21022.Fragments.FavoritosFragment;
import com.example.hc21018gp21022.Fragments.PopularesFragment;
import com.example.hc21018gp21022.Fragments.UsuarioFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AppActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app);
        String iduser = getIntent().getStringExtra("idUser");
        Fragment usuario = new UsuarioFragment(iduser);
        Fragment populares = new PopularesFragment(iduser);
        Fragment destinos = new DestinosFragment(iduser,this);
        Fragment favoritos = new FavoritosFragment(iduser);


        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, destinos ).commit();

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.favoritosOpcion:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, favoritos ).commit();
                        break;
                    case R.id.destinosOpcion:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, destinos ).commit();
                        break;
                    case R.id.popularesOpcion:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, populares ).commit();
                        break;
                    case R.id.usuarioOpcion:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, usuario ).commit();
                        break;
                }
                return true;
            }
        });
    }
}