package com.example.hc21018gp21022.Models;

public class Comment {
    private String idUser;
    private String Comment;

    public Comment(String idUser, String comment) {
        this.idUser = idUser;
        Comment = comment;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
