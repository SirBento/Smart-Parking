package com.example.smartparking;

public class Ticket {
    //For payment ticket
    String ticketname, ticketregNo, ticketDate, ticketDuration , Uid;

    //For booking ticket
    String bookTicketname, bookTicketDate, bookTicketDuration, bookSlot, bookUid;

    public String getBookUid() {
        return bookUid;
    }

    public void setBookUid(String bookUid) {
        this.bookUid = bookUid;
    }


    public String getBookTicketname() {
        return bookTicketname;
    }

    public void setBookTicketname(String bookTicketname) {
        this.bookTicketname = bookTicketname;
    }

    public String getBookTicketDate() {
        return bookTicketDate;
    }

    public void setBookTicketDate(String bookTicketDate) {
        this.bookTicketDate = bookTicketDate;
    }

    public String getBookTicketDuration() {
        return bookTicketDuration;
    }

    public void setBookTicketDuration(String bookTicketDuration) {
        this.bookTicketDuration = bookTicketDuration;
    }

    public String getBookSlot() {
        return bookSlot;
    }

    public void setBookSlot(String bookSlot) {
        this.bookSlot = bookSlot;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getTicketname() {
        return ticketname;
    }

    public void setTicketname(String ticketname) {
        this.ticketname = ticketname;
    }

    public String getTicketregNo() {
        return ticketregNo;
    }

    public void setTicketregNo(String ticketregNo) {
        this.ticketregNo = ticketregNo;
    }

    public String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getTicketDuration() {
        return ticketDuration;
    }

    public void setTicketDuration(String ticketDuration) {
        this.ticketDuration = ticketDuration;
    }
}
