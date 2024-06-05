package com.example.hc21018gp21022.Adapters;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComentariosAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> commentsList;
    private DestinosModel destino;
    private AppActivity main;
    private Context context;
    private DatabaseReference userRef,userFavRef;
    private String idUser;
    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DestinosAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_comentarios, parent, false);
            viewHolder = new DestinosAdapter.ViewHolder();
            viewHolder.lblNombre = convertView.findViewById(R.id.lblNombreDestinoPop);
            viewHolder.lblDescripcion = convertView.findViewById(R.id.lblDescripcionDestinoPop);
            viewHolder.lblUbicacion = convertView.findViewById(R.id.lblUbicacionDestinoPop);
            viewHolder.lblAutor = convertView.findViewById(R.id.lblUsernameDestinoPop);
            viewHolder.lblRating = convertView.findViewById(R.id.lblRatingDes);
            viewHolder.btnVerComentarios = convertView.findViewById(R.id.btnComentariosPop);
            viewHolder.btnAgregarFav = convertView.findViewById(R.id.btnFavPop);
            viewHolder.img = convertView.findViewById(R.id.imageView4);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DestinosAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.lblNombre.setText(destino.getNombre());
        viewHolder.lblDescripcion.setText(destino.getDescripcion());
        viewHolder.lblUbicacion.setText(destino.getUbicacion());
        viewHolder.lblRating.setText(destino.getRating());

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(destino.getIdUser());
        userFavRef = FirebaseDatabase.getInstance().getReference("Users").child(idUser);
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
        verificarFav(viewHolder, destino);

        viewHolder.btnVerComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Limpia la imagen anterior antes de cargar la nueva
        Glide.with(context).clear(viewHolder.img);
        Glide.with(context)
                .load(destino.getUrlImg())
                .into(viewHolder.img);

        commentsList = new ArrayList<>();

        // Agregar algunos comentarios de ejemplo
        addComment("user1", "This is the first comment.");
        addComment("user2", "This is the second comment.");

        return convertView;
    }
    public void verificarFav(DestinosAdapter.ViewHolder viewHolder, DestinosModel destino){
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
                    viewHolder.btnAgregarFav.setText("Eliminar de favoritos");
                    String finalFavKey = favKey;
                    viewHolder.btnAgregarFav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userFavRef.child("Favoritos").child(finalFavKey).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                                            verificarFav(viewHolder, destino);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                } else {
                    viewHolder.btnAgregarFav.setText("Agregar a favoritos");
                    viewHolder.btnAgregarFav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String,Object> favoritos = new HashMap<>();
                            favoritos.put("idDestino", destino.getIdDestino());
                            userFavRef.child("Favoritos").push().setValue(favoritos).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "El destino se ha agregado a tus favoritos!", Toast.LENGTH_SHORT).show();
                                    verificarFav(viewHolder, destino);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Error al tratar de añadir a favoritos", Toast.LENGTH_SHORT).show();
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
    private void addComment(String idUser, String comment) {
        // Crear un nuevo HashMap para almacenar el comentario
        HashMap<String, String> commentData = new HashMap<>();
        commentData.put("idUser", idUser);
        commentData.put("comment", comment);

        // Agregar el HashMap a la lista
        commentsList.add(commentData);
    }
    static class ViewHolder {
        TextView lblNombre;
        TextView lblDescripcion;
        TextView lblUbicacion;
        TextView lblAutor;
        TextView lblRating;
        Button btnVerComentarios;
        Button btnAgregarFav;
        ImageView img;
    }
}
