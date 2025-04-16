package com.example.chat.model;

import java.util.UUID;

public class PersonForm {
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String dob;
    private String gender;
    private String imagePath;
    private String timestamp;

    // ✅ Required no-arg constructor for Jackson
    public PersonForm() {
        this.id = UUID.randomUUID().toString(); // Generate ID for Jackson use
    }

    // Optional: Parameterized constructor for manual creation
    public PersonForm(
            String fname,
            String mname,
            String lname,
            String dob,
            String gender,
            String imagePath,
            String timestamp) {
        this.id = UUID.randomUUID().toString();
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.dob = dob;
        this.gender = gender;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    // public LocalDate getDob() {
    // return dob;
    // }

    // public void setDob(LocalDate dob) {
    // System.out.println("settin setDob"+dob);
    // this.dob = dob;
    // }
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
