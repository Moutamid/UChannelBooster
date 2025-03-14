package com.moutamid.uchannelbooster.models;

public class BankModel {
    private String id, bankName, accountHolder, accountNumber, extraInfo, logoUrl, image;

    public BankModel() {}

    public BankModel(String id, String bankName, String accountHolder, String accountNumber, String extraInfo, String logoUrl) {
        this.id = id;
        this.bankName = bankName;
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.extraInfo = extraInfo;
        this.logoUrl = logoUrl;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getExtraInfo() { return extraInfo; }
    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}