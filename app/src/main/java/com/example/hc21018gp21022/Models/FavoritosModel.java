package com.example.hc21018gp21022.Models;

public class FavoritosModel {
    private String idFavorito;
    private String idDestino;

    public FavoritosModel(String idFavorito, String idDestino) {
        this.idFavorito = idFavorito;
        this.idDestino = idDestino;
    }

    public String getIdFavorito() {
        return idFavorito;
    }

    public void setIdFavorito(String idFavorito) {
        this.idFavorito = idFavorito;
    }

    public String getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(String idDestino) {
        this.idDestino = idDestino;
    }
}
