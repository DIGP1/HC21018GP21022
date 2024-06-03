package com.example.hc21018gp21022.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hc21018gp21022.Adapters.DestinosAdapter;
import com.example.hc21018gp21022.Adapters.FavoritosAdapter;
import com.example.hc21018gp21022.AppActivity;
import com.example.hc21018gp21022.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String idUser;
    private AppActivity main;
    private DatabaseReference favReference;
    private List<String> dataDesFav;
    private FavoritosAdapter adapter;
    private ListView ls;

    public FavoritosFragment() {
        // Required empty public constructor
    }
    public FavoritosFragment(String idUser, AppActivity main){
        this.idUser = idUser;
        this.main = main;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritosFragment newInstance(String param1, String param2) {
        FavoritosFragment fragment = new FavoritosFragment();
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
        View root = inflater.inflate(R.layout.fragment_favoritos, container, false);
        ls = root.findViewById(R.id.listViewFavoritos);
        dataDesFav = new ArrayList<>();
        favReference = FirebaseDatabase.getInstance().getReference("Users").child(idUser).child("Favoritos");
        favReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dataDesFav.clear();
                    for (DataSnapshot dataFav : snapshot.getChildren()) {
                        String idDestino = dataFav.child("idDestino").getValue(String.class);
                        if (idDestino != null) {
                            dataDesFav.add(idDestino);
                        }
                    }
                    adapter = new FavoritosAdapter(dataDesFav, main, getContext(), idUser);
                    ls.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al obtener datos", error.toException());
            }
        });


        return root;
    }

}