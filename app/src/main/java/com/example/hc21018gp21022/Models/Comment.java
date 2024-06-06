package com.example.hc21018gp21022.Models;

public class Comment {
    private String idUser;
    private String Comment;
    private String Rating;
    public Comment(String idUser, String comment,String Rating) {
        this.idUser = idUser;
        this.Comment = comment;
        this.Rating = Rating;
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

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
