package com.example.mobileticketapp.models;

import java.util.List;

public class Ticket {
    private String id;
    private String uid;
    private String showtimeId;
    private List<String> seatNumbers;
    private int totalPrice;
    private long bookingTime;
    private String movieTitle;
    private String startTime;
    private String theaterName;

    public Ticket() {
    }

    public Ticket(String id, String uid, String showtimeId, List<String> seatNumbers, int totalPrice, long bookingTime, String movieTitle, String startTime, String theaterName) {
        this.id = id;
        this.uid = uid;
        this.showtimeId = showtimeId;
        this.seatNumbers = seatNumbers;
        this.totalPrice = totalPrice;
        this.bookingTime = bookingTime;
        this.movieTitle = movieTitle;
        this.startTime = startTime;
        this.theaterName = theaterName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public long getBookingTime() { return bookingTime; }
    public void setBookingTime(long bookingTime) { this.bookingTime = bookingTime; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
}
