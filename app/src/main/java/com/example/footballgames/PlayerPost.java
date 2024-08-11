package com.example.footballgames;

public class PlayerPost {
    public String id;
    public String isim;
    public String takim;
    public String reyting;
    public String playerPhoto;


    public PlayerPost(String id,String isim,  String takim, String reyting,String playerPhoto) {
        this.id = id;
        this.isim = isim;
        this.takim = takim;
        this.reyting = reyting;
        this.playerPhoto = playerPhoto;
    }
}
