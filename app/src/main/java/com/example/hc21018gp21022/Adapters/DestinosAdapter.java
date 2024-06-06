package com.example.hc21018gp21022.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hc21018gp21022.AppActivity;
import com.example.hc21018gp21022.Fragments.ComentariosFragment;
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

public class DestinosAdapter extends BaseAdapter {

    private List<DestinosModel> dataDestinos;
    private AppActivity main;
    private Context context;
    private DatabaseReference userRef,userFavRef,commentRef;
    private String idUser;
    private ComentariosFragment comentariosFragment;
    private double mediaRating;

    public DestinosAdapter(List<DestinosModel> dataDestinos, AppActivity main, Context context, String idUser) {
        this.dataDestinos = dataDestinos;
        this.main = main;
        this.context = context;
        this.idUser = idUser;
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
            viewHolder.lblNombre = convertView.findViewById(R.id.lblNombreDestinoPop);
            viewHolder.lblDescripcion = convertView.findViewById(R.id.lblDescripcionDestinoPop);
            viewHolder.lblUbicacion = convertView.findViewById(R.id.lblUbicacionDestinoPop);
            viewHolder.lblAutor = convertView.findViewById(R.id.lblUsernameDestinoPop);
            viewHolder.lblRating = convertView.findViewById(R.id.lblRatingDes);
            viewHolder.btnVerComentarios = convertView.findViewById(R.id.btnComentariosPop);
            viewHolder.btnAgregarFav = convertView.findViewById(R.id.btnFavPop);
            viewHolder.img = convertView.findViewById(R.id.imageView4);
            viewHolder.ratingBar = convertView.findViewById(R.id.ratingBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DestinosModel destino = dataDestinos.get(position);

        commentRef = FirebaseDatabase.getInstance().getReference("Destinos").child(destino.getIdDestino());

        commentRef.child("Comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mediaRating = 0;
                int cantComments = 0;
                if (snapshot.exists()){
                    for (DataSnapshot comment : snapshot.getChildren()){
                        float ratingComment = Float.parseFloat(comment.child("rating").getValue(String.class));
                        mediaRating += ratingComment;
                        cantComments ++;
                    }
                }
                mediaRating += Double.parseDouble(destino.getRating());
                mediaRating = mediaRating/(cantComments+1);
                mediaRating = round(mediaRating, 1);
                viewHolder.lblNombre.setText(destino.getNombre());
                viewHolder.lblDescripcion.setText(destino.getDescripcion());
                viewHolder.lblUbicacion.setText(destino.getUbicacion());
                viewHolder.lblRating.setText(String.valueOf(mediaRating));
                viewHolder.ratingBar.setRating(Float.parseFloat(String.valueOf(mediaRating)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                destino.setIdUser(String.valueOf(viewHolder.lblAutor.getText()));
                comentariosFragment = new ComentariosFragment(destino, idUser);
                main.hideBottomNavigationView();
                main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, comentariosFragment)
                        .addToBackStack(null) // Agrega la transacción a la pila de retroceso
                        .commit();
            }
        });

        // Limpia la imagen anterior antes de cargar la nueva
        Glide.with(context).clear(viewHolder.img);
        Glide.with(context)
                .load(destino.getUrlImg())
                .into(viewHolder.img);

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

    static class ViewHolder {
        TextView lblNombre;
        TextView lblDescripcion;
        TextView lblUbicacion;
        TextView lblAutor;
        TextView lblRating;
        Button btnVerComentarios;
        Button btnAgregarFav;
        ImageView img;
        RatingBar ratingBar;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
