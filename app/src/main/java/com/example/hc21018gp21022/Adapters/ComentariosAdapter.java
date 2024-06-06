package com.example.hc21018gp21022.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hc21018gp21022.Models.Comment;
import com.example.hc21018gp21022.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ComentariosAdapter extends BaseAdapter {
    private List<Comment> commentsList;
    private Context context;
    private DatabaseReference userFavRef;

    public ComentariosAdapter(List<Comment> commentsList, Context context) {
        this.commentsList = commentsList;
        this.context = context;
    }

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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_comentarios, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.lblUser = convertView.findViewById(R.id.txtNombreComment);
            viewHolder.lblComentario = convertView.findViewById(R.id.txtComentario);
            viewHolder.lblRating = convertView.findViewById(R.id.txtRatingCommenta);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Comment comment = commentsList.get(position);


        userFavRef = FirebaseDatabase.getInstance().getReference("Users").child(comment.getIdUser());

        userFavRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String username = snapshot.child("username").getValue(String.class);
                    viewHolder.lblUser.setText(username);
                    viewHolder.lblComentario.setText(comment.getComment());
                    viewHolder.lblRating.setText(comment.getRating());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return convertView;
    }
    static class ViewHolder {
        TextView lblUser;
        TextView lblComentario;
        TextView lblRating;
    }
}
