package com.example.user.app;

/**
 * Created by User on 6/22/2016.
 */
public class Phone {
    private String phoneName, isLost, ringRequest;

    public Phone(String phoneName, String isLost, String ringRequest) {
        this.setIsLost(isLost);
        this.setPhoneName(phoneName);
        this.setRingRequest(ringRequest);
    }

    public String getIsLost() {
        return isLost;
    }

    public void setIsLost(String isLost) {
        this.isLost = isLost;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getRingRequest() {
        return ringRequest;
    }

    public void setRingRequest(String ringRequest) {
        this.ringRequest = ringRequest;
    }
}
