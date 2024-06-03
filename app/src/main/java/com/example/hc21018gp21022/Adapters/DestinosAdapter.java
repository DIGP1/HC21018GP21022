package com.example.hc21018gp21022.Adapters;

import android.content.Context;
import android.net.Uri;
import android.text.GetChars;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hc21018gp21022.AppActivity;
import com.example.hc21018gp21022.Models.DestinosModel;
import com.example.hc21018gp21022.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DestinosAdapter extends BaseAdapter {

    private List<DestinosModel> dataDestinos;
    private AppActivity main;
    private Context context;
    private DatabaseReference userRef;

    public DestinosAdapter(List<DestinosModel> dataDestinos, AppActivity main, Context context) {
        this.dataDestinos = dataDestinos;
        this.main = main;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataDestinos.size();
    }

    @Override
    public Object getItem(int position) {
        return dataDestinos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_destinos,null);
        TextView lblNombre,lblDescripcion,lblUbicacion,lblAutor;
        Button btnVerComentarios, btnAgregarFav;
        ImageView img;
        DestinosModel destino = dataDestinos.get(position);


        lblNombre = convertView.findViewById(R.id.lblNombreDestino);
        lblDescripcion = convertView.findViewById(R.id.lblDescripcionDestino);
        lblUbicacion = convertView.findViewById(R.id.lblUbicacionDestino);
        lblAutor = convertView.findViewById(R.id.lblUsernameDestino);

        btnVerComentarios = convertView.findViewById(R.id.btnComentarios);
        btnAgregarFav = convertView.findViewById(R.id.btnAgregarFav);

        img = convertView.findViewById(R.id.imageView2);

        lblNombre.setText(destino.getNombre());
        lblDescripcion.setText(destino.getDescripcion());
        lblUbicacion.setText(destino.getUbicacion());

        Glide.with(context)
                .load(destino.getUrlImg())
                .into(img);

        img.setImageURI(Uri.parse(destino.getUrlImg()));
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(destino.getIdUser());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    lblAutor.setText(snapshot.child("username").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lblAutor.setText("Autor desconocido");
            }
        });



        return convertView;
    }
}
