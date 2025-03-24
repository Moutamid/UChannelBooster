package com.moutamid.uchannelbooster.activities;


public class PaymentModel {
    String userID, userEmail, price, proofImage;
    boolean isApprove;

    public PaymentModel() {
    }

    public PaymentModel(String userID, String userEmail, String price, String proofImage, boolean isApprove) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.price = price;
        this.proofImage = proofImage;
        this.isApprove = isApprove;
    }
}