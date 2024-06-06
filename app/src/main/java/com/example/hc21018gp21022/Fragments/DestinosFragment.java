package com.example.hc21018gp21022.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hc21018gp21022.Adapters.DestinosAdapter;
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
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String idUser;
    private Fragment agregarFragment;
    private AppActivity main;
    private DatabaseReference destinosRef,userFavRef;
    private List<DestinosModel> dataDestinos;
    private DestinosAdapter adapter;
    private ListView ls;

    public DestinosFragment() {
        // Required empty public constructor
    }
    public DestinosFragment(String idUser, AppActivity main){
        this.idUser = idUser;
        this.main = main;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DestinosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DestinosFragment newInstance(String param1, String param2) {
        DestinosFragment fragment = new DestinosFragment();
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
        View root = inflater.inflate(R.layout.fragment_destinos, container, false);
        ls = root.findViewById(R.id.listViewDestinos);
        dataDestinos = new ArrayList<>();
        destinosRef = FirebaseDatabase.getInstance().getReference("Destinos");
        userFavRef = FirebaseDatabase.getInstance().getReference("Users").child(idUser).child("Favoritos");
        ObtenerDestinos();
        ImageButton btnAgregar = root.findViewById(R.id.btnAgregarDestino);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarFragment = new AgregarDestinoFragment(idUser, main);
                main.hideBottomNavigationView();
                main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, agregarFragment)
                        .addToBackStack(null) // Agrega la transacción a la pila de retroceso
                        .commit();
            }
        });
        return root;
    }

    private void ObtenerDestinos() {
        destinosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
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
                        dataDestinos.add(destinoData);
                    }
                    Collections.reverse(dataDestinos);
                    adapter = new DestinosAdapter(dataDestinos,main,getContext(),idUser);
                    ls.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error
                Log.e("Firebase", "Error al obtener datos", error.toException());
            }
        });
    }
}