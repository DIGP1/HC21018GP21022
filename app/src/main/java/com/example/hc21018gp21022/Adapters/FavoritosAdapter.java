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

import java.util.List;
import java.util.Map;

public class FavoritosAdapter extends BaseAdapter {

    private List<String>dataFavoritos;
    private AppActivity main;
    private Context context;
    private DatabaseReference destinosRef, userRef, userFavRef,commentRef;
    private String idUser;
    private double mediaRating;
    private ComentariosFragment comentariosFragment;
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
            viewHolder.lblNombre = convertView.findViewById(R.id.lblNombreDestinoPop);
            viewHolder.lblDescripcion = convertView.findViewById(R.id.lblDescripcionDestinoPop);
            viewHolder.lblUbicacion = convertView.findViewById(R.id.lblUbicacionDestinoPop);
            viewHolder.lblAutor = convertView.findViewById(R.id.lblUsernameDestinoPop);
            viewHolder.lblRating = convertView.findViewById(R.id.lblRatingDes);
            viewHolder.btnVerComentarios = convertView.findViewById(R.id.btnComentariosPop);
            viewHolder.btnFav = convertView.findViewById(R.id.btnFavPop);
            viewHolder.img = convertView.findViewById(R.id.imageView4);
            viewHolder.ratingBar = convertView.findViewById(R.id.ratingBar);
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
                                Map<String, Object> commentsMap = (Map<String, Object>) snapshot.child("Comments").getValue();
                                DestinosModel destino = new DestinosModel(snapshot.getKey(),
                                        snapshot.child("nombre").getValue(String.class),
                                        snapshot.child("descripcion").getValue(String.class),
                                        snapshot.child("ubicacion").getValue(String.class),
                                        snapshot.child("imgDestino").getValue(String.class),
                                        snapshot.child("Rating").getValue(String.class),
                                        snapshotUser.child("username").getValue(String.class),
                                        commentsMap);
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
                                        viewHolder.lblRating.setText(String.valueOf(mediaRating));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                viewHolder.lblNombre.setText(destino.getNombre());
                                viewHolder.lblDescripcion.setText(destino.getDescripcion());
                                viewHolder.lblUbicacion.setText(destino.getUbicacion());
                                viewHolder.lblAutor.setText(destino.getIdUser());
                                viewHolder.ratingBar.setRating(Float.parseFloat(String.valueOf(mediaRating)));

                                // Limpia la imagen anterior antes de cargar la nueva
                                Glide.with(context).clear(viewHolder.img);
                                Glide.with(context)
                                        .load(destino.getUrlImg())
                                        .into(viewHolder.img);

                                verificarFav(viewHolder,destino,position);
                                viewHolder.btnVerComentarios.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        destino.setIdUser(String.valueOf(viewHolder.lblAutor.getText()));
                                        comentariosFragment = new ComentariosFragment(destino, idUser,main);
                                        main.hideBottomNavigationView();
                                        main.getSupportFragmentManager().beginTransaction()
                                                .add(android.R.id.content, comentariosFragment)  // usa android.R.id.content para añadir el fragmento sobre todo el contenido
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });
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
        TextView lblRating;
        Button btnVerComentarios;
        Button btnFav;
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
