package com.example.android.heartratemonitor;

public class User {
    private String userName;
    private int userAge;
    private String userWeight;
    private String userHeight;
    private String emergencyContactNumber;


    public User() {
    }

    public User(String userName, int userAge, String userWeight, String userHeight, String emergencyContactNumber) {
        this.userName = userName;
        this.userAge = userAge;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.emergencyContactNumber = emergencyContactNumber;

    }

    public String getUserName() {
        return userName;
    }

    public int getUserAge() {
        return userAge;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public String getUserHeight() {
        return userHeight;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }
}
