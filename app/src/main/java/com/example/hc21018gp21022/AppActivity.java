package com.example.hc21018gp21022;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hc21018gp21022.Fragments.AgregarDestinoFragment;
import com.example.hc21018gp21022.Fragments.ComentariosFragment;
import com.example.hc21018gp21022.Fragments.DestinosFragment;
import com.example.hc21018gp21022.Fragments.FavoritosFragment;
import com.example.hc21018gp21022.Fragments.PopularesFragment;
import com.example.hc21018gp21022.Fragments.UsuarioFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AppActivity extends AppCompatActivity {
    public BottomNavigationView navigationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app);
        String iduser = getIntent().getStringExtra("idUser");
        Fragment usuario = new UsuarioFragment(iduser);
        Fragment populares = new PopularesFragment(iduser,this);
        Fragment destinos = new DestinosFragment(iduser,this);
        Fragment favoritos = new FavoritosFragment(iduser, this);


        navigationView = findViewById(R.id.bottomNavigationView);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

            if (currentFragment instanceof AgregarDestinoFragment) {
                ((AgregarDestinoFragment) currentFragment).handleOnBackPressed();
            }else if(currentFragment instanceof ComentariosFragment) {
                ((ComentariosFragment) currentFragment).handleOnBackPressed();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("¿Desea salir de la aplicacion?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }

            return true; // Devuelve true para indicar que has manejado el evento
        }
        return super.onKeyDown(keyCode, event);
    }
    public void showBottomNavigationView() {
        navigationView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigationView() {
        navigationView.setVisibility(View.GONE);
    }
}