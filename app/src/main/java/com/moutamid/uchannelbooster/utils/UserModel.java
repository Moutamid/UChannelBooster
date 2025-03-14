package com.moutamid.uchannelbooster.utils;

public class UserModel {
    String key;
    String name;
    String email;
    String phone;
    String image;
    String address;
    String about;
    String lat;
    String lng;
    String deviceID;
    String selectedType;
    boolean subscriptionFromGoogle, isVIP;
    String duration;

    public UserModel() {
    }

    public UserModel(String key, String name, String email, String phone, String image, String address, String about,
                     String lat, String lng, String deviceID, boolean subscriptionFromGoogle, boolean isVIP, String duration) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.address = address;
        this.about = about;
        this.lat = lat;
        this.lng = lng;
        this.deviceID = deviceID;
        this.subscriptionFromGoogle = subscriptionFromGoogle;
        this.isVIP = isVIP;
        this.duration = duration;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }

    public String getLng() { return lng; }
    public void setLng(String lng) { this.lng = lng; }
}
