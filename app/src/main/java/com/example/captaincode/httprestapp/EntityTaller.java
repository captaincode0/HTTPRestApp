package com.example.captaincode.httprestapp;

/**
 * Created by captaincode on 19/05/16.
 */
public class EntityTaller {
    private int id;
    private String description;
    private int hours;
    private String place;
    private String datei;
    private String datef;

    public EntityTaller(){

    }

    public EntityTaller(int id, String description, int hours, String place, String datei, String datef){
        this.id = id;
        this.description = description;
        this.hours = hours;
        this.place = place;
        this.datei = datei;
        this.datef = datef;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getDatei() {
        return datei;
    }

    public void setDatei(String datei) {
        this.datei = datei;
    }

    public String getDatef() {
        return datef;
    }

    public void setDatef(String datef) {
        this.datef = datef;
    }

    public void print(){
        System.out.println("id: "+this.id+", descripcion: "+this.description+", horas, "+this.hours+", fechai: "+this.datei+", fechaf: "+this.datef);
    }
}
