package com.example.hc21018gp21022.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hc21018gp21022.AppActivity;
import com.example.hc21018gp21022.Models.DestinosModel;
import com.example.hc21018gp21022.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritosAdapter extends BaseAdapter {

    private List<String>dataFavoritos;
    private AppActivity main;
    private Context context;
    private DatabaseReference destinosRef, userRef, userFavRef;
    private String idUser;

    public FavoritosAdapter(List<String> dataFavoritos, AppActivity main, Context context, String idUser) {
        this.dataFavoritos = dataFavoritos;
        this.main = main;
        this.context = context;
        this.idUser = idUser;
    }

    @Override
    public int getCount() {
        return dataFavoritos.size();
    }

    @Override
    public Object getItem(int position) {
        return dataFavoritos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_favoritos, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.lblNombre = convertView.findViewById(R.id.lblNombreDestinoFav);
            viewHolder.lblDescripcion = convertView.findViewById(R.id.lblDescripcionDestinoFav);
            viewHolder.lblUbicacion = convertView.findViewById(R.id.lblUbicacionDestinoFav);
            viewHolder.lblAutor = convertView.findViewById(R.id.lblUsernameDestinoFav);
            viewHolder.btnVerComentarios = convertView.findViewById(R.id.btnComentariosFav);
            viewHolder.btnFav = convertView.findViewById(R.id.btnFav);
            viewHolder.img = convertView.findViewById(R.id.imageView3);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String idDestino = dataFavoritos.get(position);

        userFavRef = FirebaseDatabase.getInstance().getReference("Users").child(idUser);
        destinosRef = FirebaseDatabase.getInstance().getReference("Destinos").child(idDestino);
        destinosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userRef = FirebaseDatabase.getInstance().getReference("Users").child(snapshot.child("idUser").getValue(String.class));
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotUser) {
                            if(snapshotUser.exists()){
                                DestinosModel destino = new DestinosModel(snapshot.getKey(),snapshot.child("nombre").getValue(String.class),
                                        snapshot.child("descripcion").getValue(String.class),snapshot.child("ubicacion").getValue(String.class),
                                        snapshot.child("imgDestino").getValue(String.class),snapshot.child("Rating").getValue(String.class),
                                        snapshotUser.child("username").getValue(String.class));
                                viewHolder.lblNombre.setText(destino.getNombre());
                                viewHolder.lblDescripcion.setText(destino.getDescripcion());
                                viewHolder.lblUbicacion.setText(destino.getUbicacion());
                                viewHolder.lblAutor.setText(destino.getIdUser());
                                // Limpia la imagen anterior antes de cargar la nueva
                                Glide.with(context).clear(viewHolder.img);
                                Glide.with(context)
                                        .load(destino.getUrlImg())
                                        .into(viewHolder.img);

                                verificarFav(viewHolder,destino,position);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Error al cargar el autor", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error al cargar el destino favorito", Toast.LENGTH_SHORT).show();
            }
        });




        return convertView;
    }

    public void verificarFav(FavoritosAdapter.ViewHolder viewHolder, DestinosModel destino, int posicion){
        userFavRef.child("Favoritos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isFavorite = false;
                String favKey = null;

                if(snapshot.exists()){
                    for(DataSnapshot fav : snapshot.getChildren()){
                        if(fav != null && destino.getIdDestino().equals(fav.child("idDestino").getValue(String.class))){
                            isFavorite = true;
                            favKey = fav.getKey();
                            break;  // Break the loop once we find a match
                        }
                    }
                }

                if(isFavorite){
                    viewHolder.btnFav.setText("Eliminar de favoritos");
                    String finalFavKey = favKey;
                    viewHolder.btnFav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userFavRef.child("Favoritos").child(finalFavKey).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                                            dataFavoritos.remove(posicion);
                                            notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error en caso de que la operación de Firebase falle
            }
        });
    }
    static class ViewHolder {
        TextView lblNombre;
        TextView lblDescripcion;
        TextView lblUbicacion;
        TextView lblAutor;
        Button btnVerComentarios;
        Button btnFav;
        ImageView img;
    }
}
