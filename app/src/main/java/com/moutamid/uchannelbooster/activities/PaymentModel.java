package com.moutamid.uchannelbooster.activities;


public class PaymentModel {
    String ID, userID, username, userEmail, price, duration, proofImage, selectedType;
    long timestamp;
    boolean isApprove;
    private String bankName, accountHolder, accountNumber;

    // Default constructor
    public PaymentModel() {
    }


    public PaymentModel(String ID, String userID, String username, String userEmail, String price, String duration, String proofImage, String selectedType, long timestamp, boolean isApprove, String bankName, String accountHolder, String accountNumber) {
        this.ID = ID;
        this.userID = userID;
        this.username = username;
        this.userEmail = userEmail;
        this.price = price;
        this.duration = duration;
        this.proofImage = proofImage;
        this.selectedType = selectedType;
        this.timestamp = timestamp;
        this.isApprove = isApprove;
        this.bankName = bankName;
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
    }

    public PaymentModel(String ID, String userID, String username, String userEmail, String price,
                        String duration, String proofImage, String selectedType, long timestamp, boolean isApprove
    ) {
        this.ID = ID;
        this.userID = userID;
        this.username = username;
        this.userEmail = userEmail;
        this.price = price;
        this.duration = duration;
        this.proofImage = proofImage;
        this.timestamp = timestamp;
        this.isApprove = isApprove;
        this.selectedType = selectedType;
    }

    // Getters and setters for selectedType
    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }


    public boolean isApprove() {
        return isApprove;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getProofImage() {
        return proofImage;
    }

    public void setApprove(boolean approve) {
        isApprove = approve;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setProofImage(String proofImage) {
        this.proofImage = proofImage;
    }
}
