package com.example.hc21018gp21022.Adapters;

import android.content.Context;
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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_destinos, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.lblNombre = convertView.findViewById(R.id.lblNombreDestino);
            viewHolder.lblDescripcion = convertView.findViewById(R.id.lblDescripcionDestino);
            viewHolder.lblUbicacion = convertView.findViewById(R.id.lblUbicacionDestino);
            viewHolder.lblAutor = convertView.findViewById(R.id.lblUsernameDestino);
            viewHolder.btnVerComentarios = convertView.findViewById(R.id.btnComentarios);
            viewHolder.btnAgregarFav = convertView.findViewById(R.id.btnAgregarFav);
            viewHolder.img = convertView.findViewById(R.id.imageView2);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DestinosModel destino = dataDestinos.get(position);

        viewHolder.lblNombre.setText(destino.getNombre());
        viewHolder.lblDescripcion.setText(destino.getDescripcion());
        viewHolder.lblUbicacion.setText(destino.getUbicacion());

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(destino.getIdUser());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    viewHolder.lblAutor.setText(snapshot.child("username").getValue(String.class));
                } else {
                    viewHolder.lblAutor.setText("Autor desconocido");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                viewHolder.lblAutor.setText("Autor desconocido");
            }
        });

        // Limpia la imagen anterior antes de cargar la nueva
        Glide.with(context).clear(viewHolder.img);
        Glide.with(context)
                .load(destino.getUrlImg())
                .into(viewHolder.img);

        return convertView;
    }

    static class ViewHolder {
        TextView lblNombre;
        TextView lblDescripcion;
        TextView lblUbicacion;
        TextView lblAutor;
        Button btnVerComentarios;
        Button btnAgregarFav;
        ImageView img;
    }

}
