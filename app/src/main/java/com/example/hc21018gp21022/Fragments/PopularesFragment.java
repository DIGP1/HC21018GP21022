package com.example.hc21018gp21022.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hc21018gp21022.Adapters.PopularesAdapter;
import com.example.hc21018gp21022.AppActivity;
import com.example.hc21018gp21022.Models.DestinosModel;
import com.example.hc21018gp21022.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopularesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String idUser;
    private AppActivity main;
    private ListView ls;
    private List<DestinosModel> dataPopulares;
    private DatabaseReference popularesRef, commentRef;
    private PopularesAdapter adapter;
    private double mediaRating = 0;


    public PopularesFragment() {
        // Required empty public constructor
    }
    public PopularesFragment(String idUser, AppActivity main){
        this.idUser = idUser;
        this.main = main;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularesFragment newInstance(String param1, String param2) {
        PopularesFragment fragment = new PopularesFragment();
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
        View root = inflater.inflate(R.layout.fragment_populares, container, false);
        popularesRef = FirebaseDatabase.getInstance().getReference("Destinos");
        dataPopulares = new ArrayList<>();
        ls = root.findViewById(R.id.listViewPopulares);
        ObtenerDestinos();



        return root;
    }
    private void ObtenerDestinos() {
        popularesRef.orderByChild("Rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final CountDownLatch latch = new CountDownLatch((int) snapshot.getChildrenCount());

                    for (DataSnapshot destino : snapshot.getChildren()) {
                        Map<String, Object> commentsMap = (Map<String, Object>) destino.child("Comments").getValue();
                        DestinosModel destinoData = new DestinosModel(destino.getKey(),
                                destino.child("nombre").getValue(String.class),
                                destino.child("descripcion").getValue(String.class),
                                destino.child("ubicacion").getValue(String.class),
                                destino.child("imgDestino").getValue(String.class),
                                destino.child("Rating").getValue(String.class),
                                destino.child("idUser").getValue(String.class),
                                commentsMap);

                        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Destinos").child(destinoData.getIdDestino()).child("Comments");
                        commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double mediaRating = 0;
                                int cantComments = 0;
                                if (snapshot.exists()) {
                                    for (DataSnapshot comment : snapshot.getChildren()) {
                                        float ratingComment = Float.parseFloat(comment.child("rating").getValue(String.class));
                                        mediaRating += ratingComment;
                                        cantComments++;
                                    }
                                }
                                mediaRating += Double.parseDouble(destinoData.getRating());
                                mediaRating = mediaRating / (cantComments + 1);
                                mediaRating = round(mediaRating, 1);
                                destinoData.setRating(String.valueOf(mediaRating));

                                dataPopulares.add(destinoData);
                                latch.countDown();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                latch.countDown();
                            }
                        });
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                latch.await();
                                Collections.sort(dataPopulares, new Comparator<DestinosModel>() {
                                    @Override
                                    public int compare(DestinosModel d1, DestinosModel d2) {
                                        double rating1 = Double.parseDouble(d1.getRating());
                                        double rating2 = Double.parseDouble(d2.getRating());
                                        return Double.compare(rating2, rating1);
                                    }
                                });

                                main.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter = new PopularesAdapter(dataPopulares, getContext(), main, idUser);
                                        ls.setAdapter(adapter);
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al obtener datos", error.toException());
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