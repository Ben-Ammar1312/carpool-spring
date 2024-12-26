package gle.carpoolspring.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnnonceForm {
    private String lieuDepart;
    private String lieuArrivee;
    private Double departLat;
    private Double departLng;
    private Double arriveLat;
    private Double arriveLng;
    private LocalDate dateDepart;
    private String heureDepart;
    private int nbrPlaces;
    private Float prix;

    // We store "waypoints" as a List so index-based binding is easy in Thymeleaf:
    private List<WaypointForm> waypoints = new ArrayList<>();

    // Getters and setters for everything
    public String getLieuDepart() {
        return lieuDepart;
    }
    public void setLieuDepart(String lieuDepart) {
        this.lieuDepart = lieuDepart;
    }

    public String getLieuArrivee() {
        return lieuArrivee;
    }
    public void setLieuArrivee(String lieuArrivee) {
        this.lieuArrivee = lieuArrivee;
    }

    public Double getDepartLat() {
        return departLat;
    }
    public void setDepartLat(Double departLat) {
        this.departLat = departLat;
    }

    public Double getDepartLng() {
        return departLng;
    }
    public void setDepartLng(Double departLng) {
        this.departLng = departLng;
    }

    public Double getArriveLat() {
        return arriveLat;
    }
    public void setArriveLat(Double arriveLat) {
        this.arriveLat = arriveLat;
    }

    public Double getArriveLng() {
        return arriveLng;
    }
    public void setArriveLng(Double arriveLng) {
        this.arriveLng = arriveLng;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }
    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getHeureDepart() {
        return heureDepart;
    }
    public void setHeureDepart(String heureDepart) {
        this.heureDepart = heureDepart;
    }

    public int getNbrPlaces() {
        return nbrPlaces;
    }
    public void setNbrPlaces(int nbrPlaces) {
        this.nbrPlaces = nbrPlaces;
    }

    public Float getPrix() {
        return prix;
    }
    public void setPrix(Float prix) {
        this.prix = prix;
    }

    public List<WaypointForm> getWaypoints() {
        return waypoints;
    }
    public void setWaypoints(List<WaypointForm> waypoints) {
        this.waypoints = waypoints;
    }
}