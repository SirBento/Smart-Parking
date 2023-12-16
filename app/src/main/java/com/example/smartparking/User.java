package com.example.smartparking;

public class User {

    public String firstname, surname, Email, phoneNumber, password;
    public User(){}

    public User(String firstname, String surname, String Email, String phoneNumber, String password){

        this.firstname = firstname;
        this.surname = surname;
        this.Email = Email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
