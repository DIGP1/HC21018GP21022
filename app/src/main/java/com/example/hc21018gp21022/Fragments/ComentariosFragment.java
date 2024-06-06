package com.example.hc21018gp21022.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hc21018gp21022.Adapters.ComentariosAdapter;
import com.example.hc21018gp21022.Adapters.DestinosAdapter;
import com.example.hc21018gp21022.AppActivity;
import com.example.hc21018gp21022.Models.Comment;
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
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComentariosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComentariosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public DestinosModel destino;
    private String idUser;
    private DatabaseReference reference,RatingOriginalRef;
    private List<Comment> dataComments;
    private ComentariosAdapter adapter;
    private ListView ls;
    private double mediaRating = 0.00f;
    private TextView lblRating;


    private DatabaseReference userRef,userFavRef;

    public ComentariosFragment(DestinosModel destino, String idUser) {
        this.destino = destino;
        this.idUser = idUser;
    }

    public ComentariosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComentariosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComentariosFragment newInstance(String param1, String param2) {
        ComentariosFragment fragment = new ComentariosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_comentarios, container, false);

        TextView lblNombre = v.findViewById(R.id.lblNombreDestinoPop);
        TextView lblDescripcion = v.findViewById(R.id.lblDescripcionDestinoPop);
        TextView lblUbicacion = v.findViewById(R.id.lblUbicacionDestinoPop);
        TextView lblAutor = v.findViewById(R.id.lblUsernameDestinoPop);
        lblRating = v.findViewById(R.id.lblRatingDes);
        EditText txtRating = v.findViewById(R.id.txtRatingComment);
        ImageButton btnComentario = v.findViewById(R.id.btnComentarioPop);
        Button btnAgregarFav = v.findViewById(R.id.btnFavPop);
        ImageView img = v.findViewById(R.id.imageView4);
        EditText comentario = v.findViewById(R.id.edtComentario);
        ls = v.findViewById(R.id.listviewComentarios);

        lblNombre.setText(destino.getNombre());
        lblDescripcion.setText(destino.getDescripcion());
        lblUbicacion.setText(destino.getUbicacion());
        lblAutor.setText(destino.getIdUser());

        dataComments = new ArrayList<>();

        // Limpia la imagen anterior antes de cargar la nueva
        Glide.with(this).clear(img);
        Glide.with(this)
                .load(destino.getUrlImg())
                .into(img);
        reference =  FirebaseDatabase.getInstance().getReference("Destinos").child(destino.getIdDestino());
        CargarComentarios(destino);


        btnComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(comentario.getText().toString()) && !"".equals(txtRating.getText().toString())){
                    String commentId = reference.push().getKey();
                    Comment comment = new Comment(idUser,comentario.getText().toString(),txtRating.getText().toString());

                    reference.child("Comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Comentario publicado exitosamente!!", Toast.LENGTH_SHORT).show();
                            comentario.setText("");
                            txtRating.setText("");
                            CargarComentarios(destino);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Hubo un error al publicar el comentario", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "No puedes enviar un comentario vacio!", Toast.LENGTH_SHORT).show();
                }
            }

        });
        return v;
    }

    public void handleOnBackPressed() {
        ((AppActivity) getActivity()).showBottomNavigationView();
        getActivity().getSupportFragmentManager().popBackStack();
    }
    private void CargarComentarios(DestinosModel destino){
        reference.child("Comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    dataComments.clear();
                    for (DataSnapshot comentario : snapshot.getChildren()){
                        Comment comment = new Comment(comentario.child("idUser").getValue(String.class),
                                comentario.child("comment").getValue(String.class),
                                comentario.child("rating").getValue(String.class));
                        float ratingComment = Float.parseFloat(comentario.child("rating").getValue(String.class));
                        mediaRating += ratingComment;
                        dataComments.add(comment);
                    }
                }
                Collections.reverse(dataComments);
                adapter = new ComentariosAdapter(dataComments, getContext());
                ls.setAdapter(adapter);
                RatingOriginalRef = FirebaseDatabase.getInstance().getReference("Destinos").child(destino.getIdDestino());
                RatingOriginalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String rating = snapshot.child("Rating").getValue(String.class);
                            Log.d("Rating Original->", rating);
                            mediaRating += Double.parseDouble(rating);
                            mediaRating = mediaRating/(dataComments.size()+1);
                            mediaRating = round(mediaRating, 1);
                            lblRating.setText(String.valueOf(mediaRating));
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //mediaRating += Double.parseDouble(destino.getRating());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar la publicacion!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}