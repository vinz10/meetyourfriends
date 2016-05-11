package com.example.vincent.meetyourfriends.entity;

public class UserInEvent {

    private int id;
    private int idUser;
    private int idEvent;

    public UserInEvent(int id, int idUser, int idEvent) {
        this.id = id;
        this.idUser = idUser;
        this.idEvent = idEvent;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return this.idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdEvent() {
        return this.idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }
}