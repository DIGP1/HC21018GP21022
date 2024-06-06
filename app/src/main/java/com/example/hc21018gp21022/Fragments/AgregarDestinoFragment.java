package com.example.hc21018gp21022.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hc21018gp21022.AppActivity;
import com.example.hc21018gp21022.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgregarDestinoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarDestinoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String idUser;
    private AppActivity main;
    private EditText txtNombre, txtUbicacion;
    private String txtRating;
    private MultiAutoCompleteTextView txtDescripcion;
    private ImageView imgVista;
    private Button btnPublicar, btnAgregarImagen;
    private DatabaseReference reference;
    private Fragment fragment;
    private RatingBar ratingBar;
    private boolean imgSubida = false;
    public AgregarDestinoFragment() {
        // Required empty public constructor
    }
    public AgregarDestinoFragment(String idUser, AppActivity main){
        this.idUser = idUser;
        this.main = main;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarDestinoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarDestinoFragment newInstance(String param1, String param2) {
        AgregarDestinoFragment fragment = new AgregarDestinoFragment();
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
        View root = inflater.inflate(R.layout.fragment_agregar_destino, container, false);
        reference = FirebaseDatabase.getInstance().getReference("Destinos");
        txtNombre = root.findViewById(R.id.txtNombreDestino);
        txtDescripcion = root.findViewById(R.id.txtDescripcionDestino);
        txtUbicacion = root.findViewById(R.id.txtUbicacionDestino);
        ratingBar = root.findViewById(R.id.txtRating);

        imgVista = root.findViewById(R.id.imageView);

        btnPublicar = root.findViewById(R.id.btnPublicar);
        btnAgregarImagen = root.findViewById(R.id.btnSelecImg);
        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirGaleria(v);
            }
        });
        ImageButton btnCancelar = root.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volver al fragmento anterior sin guardar
                main.showBottomNavigationView();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return root;
    }
    public void AbrirGaleria(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            imgVista.setImageURI(data.getData());
            btnPublicar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!"".equals(txtNombre.getText().toString()) && !"".equals(txtDescripcion.getText().toString()) &&
                            !"".equals(txtUbicacion.getText().toString()) && ratingBar.getRating()>0){
                        FirebaseStorage storage =  FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference(data.getData().getLastPathSegment());
                        UploadTask uploadTask = storageRef.putFile(data.getData());

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error al subir la imagen desde galeria", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imgSubida = true;
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String, Object> dataDestino = new HashMap<>();
                                        dataDestino.put("nombre",txtNombre.getText().toString());
                                        dataDestino.put("descripcion", txtDescripcion.getText().toString());
                                        dataDestino.put("ubicacion", txtUbicacion.getText().toString());
                                        dataDestino.put("imgDestino", uri.toString());
                                        txtRating = String.valueOf(ratingBar.getRating());
                                        dataDestino.put("Rating", txtRating);
                                        dataDestino.put("idUser", idUser);

                                        reference.push().setValue(dataDestino).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Destino publicado exitosamente!!", Toast.LENGTH_SHORT).show();
                                                main.showBottomNavigationView();
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Error al publicar el destino " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error al obtener el URL de la imagen", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }else {
                        if (ratingBar.getRating()==0){
                            Toast.makeText(getContext(), "Para publicar un destino debes darle un rating!", Toast.LENGTH_SHORT).show();
                        } else if (!imgSubida) {
                            Toast.makeText(getContext(), "Por favor suba una imagen!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }

    public void handleOnBackPressed() {
        // Volver al fragmento anterior sin guardar
        ((AppActivity) getActivity()).showBottomNavigationView();
        getActivity().getSupportFragmentManager().popBackStack();
    }

}