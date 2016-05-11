package com.example.vincent.meetyourfriends.entity;

public class Commentaire {

    private int id;
    private String commentaire;
    private int idUser;
    private int idEvent;

    public Commentaire() {

    }
    public Commentaire(int id, String commentaire, int idUser, int idEvent) {
        this.id = id;
        this.commentaire = commentaire;
        this.idUser = idUser;
        this.idEvent = idEvent;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCommentaire() {
        return commentaire;
    }
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdEvent() {
        return idEvent;
    }
    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }
}
