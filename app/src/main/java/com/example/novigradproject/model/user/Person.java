package com.example.novigradproject.model.user;

public class Person {
    public String id = "";
    public String userName = "";
    public String email = "";
    public String name = "";
    public String firstName = "";
    public String lastName = "";
    public String role = "CUstomer";

    public String extractFirstName() {
        if (firstName != null && !firstName.isEmpty()) {
            return firstName;
        }
        if (name == null || name.trim().isEmpty()) {
            return "Guest";
        }
        name = name.trim();
        int spaceIndex = name.indexOf(' ');
        if (spaceIndex != -1) {
            return name.substring(0, spaceIndex);
        } else {
            return name;
        }
    }
}
