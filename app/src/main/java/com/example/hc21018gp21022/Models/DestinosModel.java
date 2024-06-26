package com.example.hc21018gp21022.Models;

import java.util.Map;

public class DestinosModel {
    private String idDestino;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private String urlImg;
    private String Rating;
    private String idUser;
    private Map<String, Object> comments;

    public DestinosModel(String idDestino, String nombre, String descripcion, String ubicacion, String urlImg, String rating, String idUser,Map<String, Object> comments) {
        this.idDestino = idDestino;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.urlImg = urlImg;
        this.Rating = rating;
        this.idUser = idUser;
        this.comments = comments;
    }

    public Map<String, Object> getComments() {
        return comments;
    }

    public void setComments(Map<String, Object> comments) {
        this.comments = comments;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(String idDestino) {
        this.idDestino = idDestino;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
