package com.hbernabe.loots.Model;

public class Users
{
    private String email, phone, password, image, address, username;

    public Users()
    {

    }

    public Users(String email,String phone, String password, String image, String address, String username) {
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

