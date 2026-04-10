package com.example.mobileticketapp.models;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String title;
    private String description;
    private String posterUrl;
    private String trailerUrl;
    private int duration; // in minutes
    private String releaseDate;
    private String genre;
    private boolean isShowing;

    public Movie() {}

    public Movie(String id, String title, String description, String posterUrl, String trailerUrl, int duration, String releaseDate, String genre, boolean isShowing) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.isShowing = isShowing;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public boolean isShowing() { return isShowing; }
    public void setShowing(boolean showing) { isShowing = showing; }
}
