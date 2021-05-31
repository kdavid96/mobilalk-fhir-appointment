package com.example.fhirappointment;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String email;
    public String accountType;
    public String uid;
    public String name;

    public User() {
        this.email="not initialized";
        this.accountType="not initialized";
        this.uid="not initialized";
        this.name="not initialized";
    }

    public void set(String email, String accountType, String uid, String name){
        this.email = email;
        this.accountType = accountType;
        this.uid = uid;
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String email, String accountType, String uid, String name) {
        this.email = email;
        this.accountType = accountType;
        this.uid = uid;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getUid() {
        return uid;
    }
}